package org.sycamore.llm.hub.service.toolkits;

/**
 * @author: Sycamore
 * @date: 2024/4/19 23:57
 * @version: 1.0
 * @description: TODO
 */
public class ResponseConvertUtil {
    private static final String EVENT_SOURCE_PREFIX = "data: ";
    public static String convertEventSource2String(String eventSourceChunk){
        if (eventSourceChunk.startsWith(EVENT_SOURCE_PREFIX)){
            return eventSourceChunk.substring(EVENT_SOURCE_PREFIX.length(), eventSourceChunk.length() - 1);
        }
        return eventSourceChunk;
    }
}
