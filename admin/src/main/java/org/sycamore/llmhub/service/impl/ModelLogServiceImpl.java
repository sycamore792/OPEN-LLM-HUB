package org.sycamore.llmhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sycamore.llmhub.infrastructure.cache.CacheUtil;
import org.sycamore.llmhub.infrastructure.cache.DistributedCache;
import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelLogMapper;
import org.sycamore.llmhub.infrastructure.dto.resp.ApiRequestCountRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelDataRespDTO;
import org.sycamore.llmhub.service.ModelLogServiceI;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: Sycamore
 * @date: 2024/7/23 16:41
 * @version: 1.0
 * @description: TODO
 */
@Service
@RequiredArgsConstructor
public class ModelLogServiceImpl extends ServiceImpl<ModelLogMapper, ModelLog> implements ModelLogServiceI {

    private final DistributedCache distributedCache;

    @Override
    public ModelDataRespDTO apiRequestCount() {
        return distributedCache.safeGet(CacheUtil.buildKey("api_data"), ModelDataRespDTO.class, () -> {
                    ModelDataRespDTO modelDataRespDTO = new ModelDataRespDTO();
                    modelDataRespDTO.setApiRequestCount(this.baseMapper.apiRequestCount());
                    modelDataRespDTO.setApiTokensCost(this.baseMapper.apiTokensCost());
                    modelDataRespDTO.setApiModelUsedInfo(this.baseMapper.apiModelUsedInfo());
                    return modelDataRespDTO;
                },
                1,
                TimeUnit.HOURS
        );
    }
}
