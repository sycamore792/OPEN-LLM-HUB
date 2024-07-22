package org.sycamore.llmhub.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sycamore.llmhub.infrastructure.common.PageResponse;
import org.sycamore.llmhub.infrastructure.common.Result;
import org.sycamore.llmhub.infrastructure.common.Results;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelMapper;
import org.sycamore.llmhub.service.ModelBaseServiceI;
import org.sycamore.llmhub.service.ModelCompanyServiceI;
import org.sycamore.llmhub.infrastructure.dto.req.ModelPageQueryReqDTO;

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
    private final ModelCompanyServiceI modelCompanyService;
    private final ModelBaseServiceI modelBaseService;

    @GetMapping("/model/company/list")
    @Operation(summary = "获取模型厂商列表")
    public Result listCompany() {
        return Results.success(modelCompanyService.listWithCache());
    }


    @GetMapping("/model/list/{pageNum}/{pageSize}")
    @Operation(summary = "分页获取模型列表")
    public PageResponse listModelPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @ParameterObject ModelPageQueryReqDTO reqDTO) {
        return modelBaseService.modelListPageQuery(pageNum, pageSize,reqDTO);
    }

    @GetMapping("/model/{id}")
    @Operation(summary = "获取模型详情")
    public Result getModelDetailById(@PathVariable("id") Long id) {
        return Results.success(modelBaseService.getModelDetailById(id));
    }

}
