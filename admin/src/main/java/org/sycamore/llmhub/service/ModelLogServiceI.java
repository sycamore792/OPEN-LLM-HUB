package org.sycamore.llmhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;
import org.sycamore.llmhub.infrastructure.dto.resp.ApiRequestCountRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelDataRespDTO;

/**
 * @author 桑运昌
 */
public interface ModelLogServiceI extends IService<ModelLog> {
    ModelDataRespDTO apiRequestCount();
}
