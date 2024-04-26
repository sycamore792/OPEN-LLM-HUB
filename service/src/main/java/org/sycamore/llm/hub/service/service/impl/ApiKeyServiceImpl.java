package org.sycamore.llm.hub.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sycamore.llm.hub.service.dao.entity.ApiKeyDO;
import org.sycamore.llm.hub.service.dao.mapper.ApiKeyMapper;
import org.sycamore.llm.hub.service.service.IApiKeyService;

/**
 * @author: Sycamore
 * @date: 2024/4/26 13:52
 * @version: 1.0
 * @description: TODO
 */
@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKeyDO> implements IApiKeyService {

}
