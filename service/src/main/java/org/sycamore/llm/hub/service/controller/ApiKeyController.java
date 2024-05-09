package org.sycamore.llm.hub.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Sycamore
 * @date: 2024/5/9 18:46
 * @version: 1.0
 * @description: TODO
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiKeyController {

    @PostMapping("key/page/{pageNum}/{pageSize}")
    public String keyPage(@PathVariable("pageNum") Integer pageNum,
                          @PathVariable("pageSize") Integer pageSize) {
        return "key/page";
    }
}
