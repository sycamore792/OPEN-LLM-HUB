package org.sycamore.llmhub.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sycamore.llmhub.infrastructure.cache.CacheUtil;
import org.sycamore.llmhub.infrastructure.cache.DistributedCache;
import org.sycamore.llmhub.infrastructure.common.OutApiResponseStrategyMarkEnum;
import org.sycamore.llmhub.infrastructure.common.PageResponse;
import org.sycamore.llmhub.infrastructure.common.PageUtil;
import org.sycamore.llmhub.infrastructure.dataobject.ModelDO;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelLogMapper;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelMapper;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelHealthyRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelInfoRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelWithCompanyInfoRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelWithTypeInfoRespDTO;
import org.sycamore.llmhub.service.ModelBaseServiceI;
import org.sycamore.llmhub.infrastructure.dto.req.ModelPageQueryReqDTO;

/**
 * @author: Sycamore
 * @date: 2024/7/21 19:07
 * @version: 1.0
 * @description: TODO
 */
@Service
@RequiredArgsConstructor
public class ModelBaseServiceImpl extends ServiceImpl<ModelMapper, ModelDO> implements ModelBaseServiceI {
    private final DistributedCache distributedCache;
    private final ModelLogMapper modelLogMapper;

    @Override
    public ModelInfoRespDTO getModelDetailById(Long id) {
        ModelInfoRespDTO modelInfoRespDTO = new ModelInfoRespDTO();
        // 查询模型统计信息
        modelInfoRespDTO.setModelStatistic(modelLogMapper.getModelStatistic (id));

        // 查询模型基础信息
        ModelWithCompanyInfoRespDTO modelWithCompanyInfoRespDTO = this.baseMapper.selectModelBaseInfoById(id);
        modelInfoRespDTO.setAuthorCompanyNameZh(modelWithCompanyInfoRespDTO.getAuthorCompanyNameZh());
        modelInfoRespDTO.setCreateTime(modelWithCompanyInfoRespDTO.getCreateTime());
        modelInfoRespDTO.setDeployCompanyNameZh(modelWithCompanyInfoRespDTO.getDeployCompanyNameZh());
        modelInfoRespDTO.setId(String.valueOf(modelWithCompanyInfoRespDTO.getId()));
        modelInfoRespDTO.setModelName(modelWithCompanyInfoRespDTO.getModelName());
        modelInfoRespDTO.setModelServerBaseUrl(modelWithCompanyInfoRespDTO.getModelServerBaseUrl());
        modelInfoRespDTO.setModelServerName(modelWithCompanyInfoRespDTO.getModelServerName());


        // 查询模型类型信息
        ModelWithTypeInfoRespDTO modelWithTypeInfoRespDTO = this.baseMapper.selectModelTypeInfoById(id);
        modelInfoRespDTO.setModelType(modelWithTypeInfoRespDTO.getModelTypeDesc());
        modelInfoRespDTO.setModelTypeCode(modelWithTypeInfoRespDTO.getModelType());
        modelInfoRespDTO.setProtocolCode(modelWithTypeInfoRespDTO.getProtocolCode());
        modelInfoRespDTO.setRemark(modelWithTypeInfoRespDTO.getRemark());
        modelInfoRespDTO.setProtocolDesc(OutApiResponseStrategyMarkEnum.getByCode(modelWithTypeInfoRespDTO.getProtocolCode()).getMark());
        return modelInfoRespDTO;
    }

    @Override
    public PageResponse modelListPageQuery(Integer pageNum, Integer pageSize, ModelPageQueryReqDTO reqDTO) {
        Page<ModelDO> page = new Page<>(pageNum, pageSize);

        Page<ModelWithCompanyInfoRespDTO> pageResult = this.baseMapper.listModelWithCompanyInfo(page, reqDTO);
        for (ModelWithCompanyInfoRespDTO record : pageResult.getRecords()) {
            ModelHealthyRespDTO modelHealthy = distributedCache.get(CacheUtil.buildKey("model_healthy", record.getId().toString()), ModelHealthyRespDTO.class);
            if (modelHealthy != null) {
                record.setModelHealthyInfo(modelHealthy);
            }
        }
        return PageUtil.convert(pageResult);
    }

    @Override
    public PageResponse modelListPageQueryByApiKey(Integer pageNum, Integer pageSize,String apiKey) {
        Page<ModelDO> page = new Page<>(pageNum, pageSize);

        Page<ModelWithCompanyInfoRespDTO> pageResult = this.baseMapper.listModelWithCompanyInfoByApiKey(page, apiKey);
//        for (ModelWithCompanyInfoRespDTO record : pageResult.getRecords()) {
//            ModelHealthyRespDTO modelHealthy = distributedCache.get(CacheUtil.buildKey("model_healthy", record.getId().toString()), ModelHealthyRespDTO.class);
//            if (modelHealthy != null) {
//                record.setModelHealthyInfo(modelHealthy);
//            }
//        }
        return PageUtil.convert(pageResult);
    }
}
