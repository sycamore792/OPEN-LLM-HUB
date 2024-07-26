package org.sycamore.llmhub.infrastructure.dataobject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sycamore.llmhub.infrastructure.dataobject.ModelDO;
import org.sycamore.llmhub.infrastructure.dto.req.ModelPageQueryReqDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelWithCompanyInfoRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelWithTypeInfoRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.SelectModelServerInfoByKeyRespDTO;

import java.util.List;
import java.util.Objects;

/**
 * @author: Sycamore
 * @date: 2024/5/8 15:23
 * @version: 1.0
 * @description: TODO
 */
public interface ModelMapper extends BaseMapper<ModelDO> {
    @Select("""
             select llm_model_id , api_key ,model_server_name,model_server_base_url,server_params,protocol_code
                    from (select *
                          from api_key_model
                          where api_key = #{key}
                            and model_name = #{modelName}
                            and del_flag != 1 
                            
              ) a
                             left join model_llm b
                 
                              on a.llm_model_id = b.id
                             AND b.model_type = 1
                             AND b.del_flag != 1
            """)
    List<SelectModelServerInfoByKeyRespDTO> selectModelServerInfoByKey(@Param("key") String key, @Param("modelName") String modelName);


    ModelWithCompanyInfoRespDTO selectModelBaseInfoById(@Param("id") Long id);


    Page<ModelWithCompanyInfoRespDTO> listModelWithCompanyInfoByApiKey(Page<ModelDO> page, @Param("apiKey") String apiKey);




    @Select("""
select l.id, t.type_name as model_type, t.remark as model_type_desc, l.protocol_code, l.remark as remark
from (select * from model_llm where id = #{id}) l
         left join model_type t on l.model_type = t.id and l.del_flag = 0 and t.del_flag = 0
            """)
    ModelWithTypeInfoRespDTO selectModelTypeInfoById(@Param("id") Long id);


    Page<ModelWithCompanyInfoRespDTO> listModelWithCompanyInfo(Page<ModelDO> page, @Param("reqDTO") ModelPageQueryReqDTO reqDTO);
}
