package org.sycamore.llmhub.infrastructure.dataobject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sycamore.llmhub.infrastructure.dataobject.ModelDO;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;

import java.util.List;

/**
 * @author: Sycamore
 * @date: 2024/5/8 15:23
 * @version: 1.0
 * @description: TODO
 */
public interface ModelMapper extends BaseMapper<ModelDO> {
    @Select("""
             select llm_model_id , api_key ,model_server_name,model_server_base_url,server_params
                    from (select *
                          from api_key_model
                          where api_key = #{key}
                            and model_name = #{modelName}
                            and del_flag != 1) a
                             left join model_llm b
                                       on a.llm_model_id = b.id
            """)
    List<SelectModelServerInfoByKeyRespDTO> selectModelServerInfoByKey(@Param("key") String key, @Param("modelName") String modelName);
}
