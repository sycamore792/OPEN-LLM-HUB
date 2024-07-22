package org.sycamore.llmhub.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.sycamore.llmhub.infrastructure.dataobject.ModelCompany;

import java.util.List;

/**
 * @author 桑运昌
 */
public interface ModelCompanyServiceI extends IService<ModelCompany> {
    List<ModelCompany> listWithCache();
}
