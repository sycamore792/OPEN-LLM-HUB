package org.sycamore.llmhub.core.handler.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.infrastructure.exception.ClientException;

import static org.sycamore.llmhub.infrastructure.exception.BaseErrorCode.APIKEY_PARAMS_BLANK_ERROR;
import static org.sycamore.llmhub.infrastructure.exception.BaseErrorCode.REQUEST_PARAMS_EMPTY_ERROR;

/**
 * @author: Sycamore
 * @date: 2024/7/19 14:40
 * @version: 1.0
 * @description: 外部api请求参数非空校验
 *
 */
@Component
public class OuterApiRequestParamNotNullChainHandler implements OuterApiRequestChainFilter<ModelRequestCommand> {
    @Override
    public void handler(ModelRequestCommand requestParam) {
        if (requestParam.getRequestModel() == null) {
            throw new ClientException(REQUEST_PARAMS_EMPTY_ERROR);
        }
        if (StringUtils.isBlank(requestParam.getApiKey())){
            throw new ClientException(APIKEY_PARAMS_BLANK_ERROR);
        }
        if (!requestParam.getApiKey().startsWith("Bearer ")){
            throw new ClientException(APIKEY_PARAMS_BLANK_ERROR);
        }
        requestParam.setApiKey(requestParam.getApiKey().substring(7));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
