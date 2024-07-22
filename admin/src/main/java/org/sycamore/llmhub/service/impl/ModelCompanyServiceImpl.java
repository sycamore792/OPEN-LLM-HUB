package org.sycamore.llmhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sycamore.llmhub.infrastructure.cache.CacheUtil;
import org.sycamore.llmhub.infrastructure.cache.DistributedCache;
import org.sycamore.llmhub.infrastructure.dataobject.ModelCompany;
import org.sycamore.llmhub.infrastructure.dataobject.mapper.ModelCompanyMapper;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;
import org.sycamore.llmhub.service.ModelCompanyServiceI;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: Sycamore
 * @date: 2024/7/21 15:26
 * @version: 1.0
 * @description: TODO
 */
@Service
@RequiredArgsConstructor
public class ModelCompanyServiceImpl extends ServiceImpl<ModelCompanyMapper, ModelCompany> implements ModelCompanyServiceI {
    private final DistributedCache distributedCache;
    @Override
    public List<ModelCompany> listWithCache() {
        return distributedCache.safeGet(CacheUtil.buildKey("model_company_list"), List.class, this::list, 3, TimeUnit.DAYS);
    }
}
