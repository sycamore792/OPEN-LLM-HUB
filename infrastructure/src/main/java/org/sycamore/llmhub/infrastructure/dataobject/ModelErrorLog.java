package org.sycamore.llmhub.infrastructure.dataobject;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 桑运昌
 */
@Data
@AllArgsConstructor
public class ModelErrorLog {
    private String errorMsg;
    private String errorStack;
}
