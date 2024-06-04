package org.sycamore.llm.hub.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sycamore.llm.hub.frameworks.common.wrappers.PageResponse;
import org.sycamore.llm.hub.service.dao.entity.ModelDO;
import org.sycamore.llm.hub.service.dao.mapper.ModelMapper;
import org.sycamore.llm.hub.service.dto.req.QueryModelPageReqDTO;
import org.sycamore.llm.hub.service.dto.resp.QueryModelPageRespDTO;
import org.sycamore.llm.hub.service.service.IModelService;

import java.util.Objects;

/**
 * @author: Sycamore
 * @date: 2024/5/10 17:36
 * @version: 1.0
 * @description: TODO
 */
@Service
@RequiredArgsConstructor
public class ModelServiceImpl extends ServiceImpl<ModelMapper, ModelDO> implements IModelService {


    @Override
    public Page<ModelDO> queryModelPage(Integer pageNum,Integer pageSize,QueryModelPageReqDTO reqDTO) {
        Page<ModelDO> page = Page.of(pageNum, pageSize);
        LambdaQueryWrapper<ModelDO> queryWrapper = Wrappers.lambdaQuery(ModelDO.class);
        queryWrapper.orderByDesc(ModelDO::getCreateTime);
        if (reqDTO != null) {
            queryWrapper
                    .eq(Objects.nonNull(reqDTO.getAuthorCompanyId()),ModelDO::getAuthorCompanyId,reqDTO.getAuthorCompanyId())
                    .eq(Objects.nonNull(reqDTO.getDeployCompanyId()),ModelDO::getDeployCompanyId,reqDTO.getDeployCompanyId())
                    .like(StrUtil.isNotBlank(reqDTO.getModelName()), ModelDO::getModelName ,reqDTO.getModelName());
        }
        return this.baseMapper.selectPage(page, queryWrapper);
    }
}
