package org.sycamore.llmhub.infrastructure.cache;

/**
 * 缓存过滤
 *
 *
 */
@FunctionalInterface
public interface CacheGetFilter<T> {

    /**
     * 缓存过滤
     *
     * @param param 输出参数
     * @return {@code true} 如果输入参数匹配，否则 {@link Boolean#TRUE}
     */
    boolean filter(T param);
}