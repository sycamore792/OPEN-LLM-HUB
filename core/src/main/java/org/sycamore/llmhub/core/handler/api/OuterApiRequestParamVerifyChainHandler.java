package org.sycamore.llmhub.core.handler.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.infrastructure.exception.ClientException;

import static org.sycamore.llmhub.infrastructure.exception.BaseErrorCode.*;

/**
 * @author: Sycamore
 * @date: 2024/7/19 14:40
 * @version: 1.0
 * @description: 外部api参数合法性校验
 */
@Component
public class OuterApiRequestParamVerifyChainHandler implements OuterApiRequestChainFilter<ModelRequestCommand> {
    @Override
    public void handler(ModelRequestCommand requestParam) {
        if (!requestParam.getRequestModel().isValid()) {
            throw new ClientException(REQUEST_BODY_VERIFY_ERROR);
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
