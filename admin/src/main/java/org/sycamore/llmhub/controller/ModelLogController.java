package org.sycamore.llmhub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sycamore.llmhub.infrastructure.common.Result;
import org.sycamore.llmhub.infrastructure.common.Results;
import org.sycamore.llmhub.service.ModelLogServiceI;

/**
 * @author: Sycamore
 * @date: 2024/7/23 16:38
 * @version: 1.0
 * @description: TODO
 */


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "模型日志控制器")
public class ModelLogController {
    private final ModelLogServiceI modelLogService;


    @GetMapping("model_log/api_request_count")
    @Operation(summary = "api请求次数")
    public Result apiRequestCount() {
        return Results.success(modelLogService.apiRequestCount());
    }

}
