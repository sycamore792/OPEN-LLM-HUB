package org.sycamore.llm.hub.service.enums;

/**
 * @author sycamore
 */

public enum DelEnum {

    /**
     * 正常状态
     */
    NORMAL(0),

    /**
     * 删除状态
     */
    DELETE(1);

    private final Integer statusCode;

    DelEnum(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer code() {
        return this.statusCode;
    }

    public String strCode() {
        return String.valueOf(this.statusCode);
    }

    @Override
    public String toString() {
        return strCode();
    }
}