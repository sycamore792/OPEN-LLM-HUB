package org.sycamore.llmhub.core.handler.api;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.infrastructure.cache.CacheUtil;
import org.sycamore.llmhub.infrastructure.cache.DistributedCache;
import org.sycamore.llmhub.infrastructure.cache.StringRedisTemplateProxy;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelMapper;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;
import org.sycamore.llmhub.infrastructure.exception.ClientException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.sycamore.llmhub.infrastructure.exception.BaseErrorCode.*;

/**
 * @author: Sycamore
 * @date: 2024/7/19 14:40
 * @version: 1.0
 * @description: 外部api参数合法性校验
 */
@Component
@RequiredArgsConstructor
public class OuterApiRequestParamVerifyChainHandler implements OuterApiRequestChainFilter<ModelRequestCommand> {

    private final DistributedCache distributedCache;
    private final ModelMapper modelMapper;
    @Override
    public void handler(ModelRequestCommand requestParam) {
        if (!requestParam.getRequestModel().isValid()) {
            throw new ClientException(REQUEST_BODY_VERIFY_ERROR);
        }
        String apiKey = requestParam.getApiKey();
        String model = requestParam.getRequestModel().getModel();

        SelectModelServerInfoByKeyRespDTO modelServerInfo = distributedCache.safeGet(
                CacheUtil.buildKey("modelServerInfo", apiKey, model),
                SelectModelServerInfoByKeyRespDTO.class,
                () -> {
                    return modelMapper.selectModelServerInfoByKey(apiKey, model).get(0);
                },
                7,
                TimeUnit.DAYS
        );
        if (Objects.isNull(modelServerInfo)){
            throw new ClientException(MODEL_INFO_VERIFY_ERROR);
        }
        requestParam.setModelServerInfo(modelServerInfo);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
