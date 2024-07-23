package org.sycamore.llmhub.infrastructure.dataobject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sycamore.llmhub.infrastructure.dataobject.ModelLog;
import org.sycamore.llmhub.infrastructure.dto.resp.ApiModelUsedInfoRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ApiRequestCountRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ApiTokensCostRespDTO;
import org.sycamore.llmhub.infrastructure.dto.resp.ModelStatisticRespDTO;

import java.util.List;

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
    ModelStatisticRespDTO getModelStatistic(@Param("id") Long id);


    @Select("""
                SELECT
                    DATE_FORMAT(create_time, '%m-%d') AS date,
                    COUNT(*) AS request_count
                FROM
                    model_log
                WHERE
                    create_time >= DATE_SUB(CURDATE(), INTERVAL 9 DAY)
                GROUP BY
                    DATE_FORMAT(create_time, '%m-%d')
                ORDER BY
                    MIN(create_time) ASC
                LIMIT 10;
            """)
    List<ApiRequestCountRespDTO> apiRequestCount();


    @Select("""
                SELECT
                    DATE_FORMAT(create_time, '%m-%d') AS date,
                    SUM(total_tokens) AS tokens_cost
                FROM
                    model_log
                WHERE
                    create_time >= DATE_SUB(CURDATE(), INTERVAL 9 DAY)
                GROUP BY
                    DATE_FORMAT(create_time, '%m-%d')
                ORDER BY
                    MIN(create_time) ASC
                LIMIT 10;
            """)
    List<ApiTokensCostRespDTO> apiTokensCost();


    @Select("""
            SELECT
                model_llm.id,
                model_name,
                count,
                ROUND(percentage, 2) AS percentage
            FROM
                (
                SELECT
                    model_id,
                    COUNT(*) AS count,
                    COUNT(*) * 100.0 / (SELECT COUNT(*) FROM model_log) AS percentage
                FROM
                    model_log
                WHERE
                    model_id IS NOT NULL
                GROUP BY
                    model_id
            ) a

            left join model_llm ON model_llm.id = a.model_id
            ORDER BY
                count DESC;
            """)
    List<ApiModelUsedInfoRespDTO> apiModelUsedInfo();
}
