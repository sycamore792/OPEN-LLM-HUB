package org.sycamore.llm.hub.service.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.sycamore.llm.hub.frameworks.common.toolkits.BeanUtil;
import org.sycamore.llm.hub.frameworks.common.wrappers.PageResponse;
import org.sycamore.llm.hub.frameworks.common.wrappers.Result;
import org.sycamore.llm.hub.service.dao.entity.ModelDO;
import org.sycamore.llm.hub.service.dto.req.QueryModelPageReqDTO;
import org.sycamore.llm.hub.service.service.IModelService;
import org.sycamore.llm.hub.service.vo.req.QueryModelPageReqVO;
import org.sycamore.llm.hub.service.vo.resp.QueryModelPageRespVO;

/**
 * @author: Sycamore
 * @date: 2024/4/15 18:01
 * @version: 1.0
 * @description: TODO
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "LLM模型-控制器")
public class ModelController {
    private final IModelService modelService;

    @PostMapping("llm-model/llm-model-page/{pageNum}/{pageSize}")
    public Result<PageResponse<QueryModelPageRespVO>> modelPage(
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,
            @RequestBody(required = false) QueryModelPageReqVO queryModelPageReqVO) {
        Page<ModelDO> modelPage = modelService.queryModelPage(pageNum, pageSize, BeanUtil.convert(queryModelPageReqVO, QueryModelPageReqDTO.class));
        return Result
                .<PageResponse<QueryModelPageRespVO>>builder()
                .data(PageResponse.<QueryModelPageRespVO>builder()
                        .records(BeanUtil.convert(modelPage.getRecords(), QueryModelPageRespVO.class))
                        .total(modelPage.getTotal())
                        .current(Long.valueOf(pageNum))
                        .size(Long.valueOf(pageSize))
                        .build())
                .message("获取成功")
                .code("200")
                .success(true)
                .build();
    }

}
