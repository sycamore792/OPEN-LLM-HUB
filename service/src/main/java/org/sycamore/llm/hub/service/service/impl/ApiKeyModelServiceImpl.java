package org.sycamore.llm.hub.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sycamore.llm.hub.service.dao.entity.ApiKeyModelDO;
import org.sycamore.llm.hub.service.dao.mapper.ApiKeyModelMapper;
import org.sycamore.llm.hub.service.service.IApiKeyModelService;

/**
 * @author: Sycamore
 * @date: 2024/4/26 14:39
 * @version: 1.0
 * @description: TODO
 */
@Service
public class ApiKeyModelServiceImpl extends ServiceImpl<ApiKeyModelMapper, ApiKeyModelDO> implements IApiKeyModelService {
}
