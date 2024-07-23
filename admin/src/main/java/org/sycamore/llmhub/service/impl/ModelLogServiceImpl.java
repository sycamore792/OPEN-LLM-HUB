package org.sycamore.llmhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelLogMapper;
import org.sycamore.llmhub.infrastructure.dto.resp.ApiRequestCountRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelDataRespDTO;
import org.sycamore.llmhub.service.ModelLogServiceI;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/7/23 16:41
 * @version: 1.0
 * @description: TODO
 */
@Service
@RequiredArgsConstructor
public class ModelLogServiceImpl extends ServiceImpl<ModelLogMapper, ModelLog> implements ModelLogServiceI {


    @Override
    public ModelDataRespDTO apiRequestCount() {

        ModelDataRespDTO modelDataRespDTO = new ModelDataRespDTO();
        modelDataRespDTO.setApiRequestCount(this.baseMapper.apiRequestCount());
        modelDataRespDTO.setApiTokensCost(this.baseMapper.apiTokensCost());
        modelDataRespDTO.setApiModelUsedInfo(this.baseMapper.apiModelUsedInfo());
        return modelDataRespDTO;
    }
}
