package org.sycamore.llmhub.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sycamore.llmhub.infrastructure.common.PageResponse;
import org.sycamore.llmhub.infrastructure.common.PageUtil;
import org.sycamore.llmhub.infrastructure.common.Result;
import org.sycamore.llmhub.infrastructure.common.Results;
import org.sycamore.llmhub.infrastructure.dataobject.ApiKeyDO;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ApiKeyMapper;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelMapper;
import org.sycamore.llmhub.infrastructure.dto.req.ModelPageQueryReqDTO;
import org.sycamore.llmhub.service.ModelBaseServiceI;
import org.sycamore.llmhub.service.ModelCompanyServiceI;

/**
 * @author: Sycamore
 * @date: 2024/7/19 10:55
 * @version: 1.0
 * @description: TODO
 */

@RestController
@RequiredArgsConstructor
@Tag(name = "api key控制器")
public class ApiKeyController {

    private final ApiKeyMapper apiKeyMapper;
    private final ModelBaseServiceI modelBaseService;

    @GetMapping("/apiKey/list/{pageNum}/{pageSize}")
    @Operation(summary = "分页获取ApiKey列表")
    public PageResponse listKey(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return PageUtil.convert(apiKeyMapper.selectPage(new Page<>(pageNum, pageSize), null));
    }

    @GetMapping("/apiKey/model/list/{pageNum}/{pageSize}")
    @Operation(summary = "分页获取apikey关联的模型列表")
    public PageResponse listModelPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @RequestParam("apiKey") String apiKey) {
        return modelBaseService.modelListPageQueryByApiKey(pageNum, pageSize,apiKey);
    }

}
