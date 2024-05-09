package org.sycamore.llm.hub.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Sycamore
 * @date: 2024/4/15 18:01
 * @version: 1.0
 * @description: TODO
 */
@RestController
public class ModelController {


    @PostMapping("model/model-page")
    public String modelPage() {
        return "model-page";
    }

}
