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
            return eventSourceChunk.substring(EVENT_SOURCE_PREFIX.length()).trim();
        }
        return eventSourceChunk;
    }

    public static void main(String[] args) {
        String s = "data: {\"id\": \"chatcmpl-Gbd5Uqoo87Sj7XkSTZca5T\", \"model\": \"Qwen1.5-14B-Chat\", \"choices\": [{\"index\": 0, \"delta\": {\"role\": \"assistant\"}, \"finish_reason\": null}]}";
        String trim = s.substring(EVENT_SOURCE_PREFIX.length()).trim();

    }

    public static String convertString2EventSourceChunk(String eventSourceChunk){
        return EVENT_SOURCE_PREFIX + eventSourceChunk + "\n\n";
    }
}
