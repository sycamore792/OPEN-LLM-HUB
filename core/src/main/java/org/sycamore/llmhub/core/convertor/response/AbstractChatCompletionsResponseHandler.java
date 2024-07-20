package org.sycamore.llmhub.core.convertor.response;

/**
 * @author: Sycamore
 * @date: 2024/7/20 18:06
 * @version: 1.0
 * @description: TODO
 */
public abstract class AbstractChatCompletionsResponseHandler {
    public abstract String doneHandler(String event);
}
