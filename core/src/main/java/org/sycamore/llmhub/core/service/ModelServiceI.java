package org.sycamore.llmhub.core.service;

import org.sycamore.llmhub.core.ModelRequestCommand;
import org.sycamore.llmhub.infrastructure.common.SseEmitter;

/**
 * @author: Sycamore
 * @date: 2024/7/19 15:36
 * @version: 1.0
 * @description: TODO
 */
public interface ModelServiceI {
    void chatCompletions(ModelRequestCommand command, SseEmitter sseEmitter);
}
