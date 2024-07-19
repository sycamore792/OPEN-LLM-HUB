package org.sycamore.llmhub.core.handler.api;


import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.infrastructure.chain.AbstractChainHandler;

/**
 * @author 桑运昌
 */
public interface OuterApiRequestChainFilter<T extends ModelRequestCommand> extends AbstractChainHandler<ModelRequestCommand> {
    
    @Override
    default String mark() {
        return OuterApiChainMarkEnum.OUTER_API_REQUEST_FILTER.name();
    }
}