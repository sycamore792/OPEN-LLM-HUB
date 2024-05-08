package org.sycamore.llm.hub.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.sycamore.llm.hub.service.dao.entity.ModelDO;
import org.sycamore.llm.hub.service.dto.resp.SelectModelServerInfoByKeyRespDTO;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/5/8 15:23
 * @version: 1.0
 * @description: TODO
 */
public interface ModelMapper extends BaseMapper<ModelDO> {


    List<SelectModelServerInfoByKeyRespDTO> selectModelServerInfoByKey(@Param("key") String key,@Param("modelName") String modelName);
}
