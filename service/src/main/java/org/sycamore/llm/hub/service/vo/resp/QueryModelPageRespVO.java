package org.sycamore.llm.hub.service.vo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * @author: Sycamore
 * @date: 2024/5/10 17:47
 * @version: 1.0
 * @description: TODO
 */
@Data
public class QueryModelPageRespVO {

    private String id;
    private String modelName;
    private Date createTime;
    private Date updateTime;
}
