package org.sycamore.llmhub.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Sycamore
 * @date: 2024/7/19 10:55
 * @version: 1.0
 * @description: TODO
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "模型控制器")
public class ModelController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
