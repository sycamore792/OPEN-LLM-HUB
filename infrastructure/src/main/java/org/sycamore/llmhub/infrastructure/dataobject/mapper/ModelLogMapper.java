package org.sycamore.llmhub.infrastructure.dataobject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelStatisticRespDTO;

/**
 * @author 桑运昌
 */
public interface ModelLogMapper extends BaseMapper<ModelLog> {

    @Select("""
    SELECT
    AVG(tps) AS avg_tps,
    AVG(first_chunk_delay) AS avg_first_chunk_delay,
    SUM(prompt_tokens) AS total_prompt_tokens,
    SUM(completion_tokens) AS total_completion_tokens,
    SUM(total_tokens) AS total_tokens
FROM
    model_log
WHERE
    model_id = #{id} and
    error_json IS NULL
""")
    ModelStatisticRespDTO getModelStatistic  (@Param("id") Long id);
}
