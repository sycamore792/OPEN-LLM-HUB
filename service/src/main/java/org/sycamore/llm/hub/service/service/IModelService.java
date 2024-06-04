package org.sycamore.llm.hub.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sycamore.llm.hub.frameworks.common.wrappers.PageResponse;
import org.sycamore.llm.hub.service.dao.entity.ModelDO;
import org.sycamore.llm.hub.service.dto.req.QueryModelPageReqDTO;
import org.sycamore.llm.hub.service.dto.resp.QueryModelPageRespDTO;
import org.sycamore.llm.hub.service.vo.req.QueryModelPageReqVO;

/**
 * @author sycamore
 */
public interface IModelService extends IService<ModelDO> {

    Page<ModelDO> queryModelPage(Integer pageNum, Integer pageSize, QueryModelPageReqDTO reqDTO);

}
