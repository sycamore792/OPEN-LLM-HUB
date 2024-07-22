package org.sycamore.llmhub.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.sycamore.llmhub.infrastructure.common.PageResponse;
import org.sycamore.llmhub.infrastructure.dataobject.ModelDO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelInfoRespDTO;
import org.sycamore.llmhub.infrastructure.dto.req.ModelPageQueryReqDTO;

/**
 * @author 桑运昌
 */
public interface ModelBaseServiceI extends IService<ModelDO> {

    ModelInfoRespDTO getModelDetailById(Long id);

    PageResponse modelListPageQuery(Integer pageNum, Integer pageSize, ModelPageQueryReqDTO reqDTO);
}
