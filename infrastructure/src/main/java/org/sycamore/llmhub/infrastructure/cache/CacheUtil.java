
package org.sycamore.llmhub.infrastructure.cache;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.assertj.core.util.Strings;
//import com.google.common.base.Joiner;
//import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * 缓存工具类
 *
 *
 */
public final class CacheUtil {

    private static final String SPLICING_OPERATOR = ":";

    /**
     * 构建缓存标识
     *
     * @param keys
     * @return
     */
    public static String buildKey(String... keys) {
        Arrays.stream(keys).forEach(each -> Optional.ofNullable(empty2Null(each)).orElseThrow(() -> new RuntimeException("构建缓存 key 不允许为空")));
//        Stream.of(keys).forEach(each -> Optional.ofNullable(Strings.emptyToNull(each)).orElseThrow(() -> new RuntimeException("构建缓存 key 不允许为空")));
        StringJoiner keyJoiner = new StringJoiner(SPLICING_OPERATOR);
        Arrays.stream(keys).forEach(keyJoiner::add);
        return keyJoiner.toString();
    }
    private static Object empty2Null(String obj) {
       if (StringUtils.isEmpty(obj)){
           return null;
       }
       return obj;
    }
    /**
     * 判断结果是否为空或空的字符串
     *
     * @param cacheVal
     * @return
     */
    public static boolean isNullOrBlank(Object cacheVal) {
        return cacheVal == null || (cacheVal instanceof String && Strings.isNullOrEmpty((String) cacheVal));
    }
}
