package org.sycamore.llmhub.infrastructure.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class SseEmitter extends ResponseBodyEmitter {
    private static final MediaType TEXT_PLAIN;
    private final Lock writeLock = new ReentrantLock();

    public SseEmitter() {
    }

    public SseEmitter(Long timeout) {
        super(timeout);
    }

    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);
        HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        }

    }


    public void send(Object object) throws IOException {
        this.send(object, (MediaType)null);
    }

    public void send(Object object, @Nullable MediaType mediaType) throws IOException {
        this.send(event().data(object, mediaType));
    }
    public void sendBody(Object object,MediaType mediaType) throws IOException {
        this.writeLock.lock();

        try {
            super.send(object,mediaType);
        } finally {
            this.writeLock.unlock();
        }

    }
    public void send(SseEventBuilder builder) throws IOException {
        Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = builder.build();
        this.writeLock.lock();

        try {
            super.send(dataToSend);
        } finally {
            this.writeLock.unlock();
        }

    }

    public String toString() {
        return "SseEmitter@" + ObjectUtils.getIdentityHexString(this);
    }

    public static SseEventBuilder event() {
        return new SseEventBuilderImpl();
    }

    static {
        TEXT_PLAIN = new MediaType("text", "plain", StandardCharsets.UTF_8);
    }

    public interface SseEventBuilder {
        SseEventBuilder id(String id);

        SseEventBuilder name(String eventName);

        SseEventBuilder reconnectTime(long reconnectTimeMillis);

        SseEventBuilder comment(String comment);

        SseEventBuilder data(Object object);

        SseEventBuilder data(Object object, @Nullable MediaType mediaType);

        Set<ResponseBodyEmitter.DataWithMediaType> build();
    }

    private static class SseEventBuilderImpl implements SseEventBuilder {
        private final Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = new LinkedHashSet(4);
        @Nullable
        private StringBuilder sb;

        private SseEventBuilderImpl() {
        }

        public SseEventBuilder id(String id) {
            this.append("id:").append(id).append('\n');
            return this;
        }

        public SseEventBuilder name(String name) {
            this.append("event:").append(name).append('\n');
            return this;
        }

        public SseEventBuilder reconnectTime(long reconnectTimeMillis) {
            this.append("retry:").append(String.valueOf(reconnectTimeMillis)).append('\n');
            return this;
        }

        public SseEventBuilder comment(String comment) {
            this.append(':').append(comment).append('\n');
            return this;
        }

        public SseEventBuilder data(Object object) {
            return this.data(object, (MediaType)null);
        }

        public SseEventBuilder data(Object object, @Nullable MediaType mediaType) {
            this.append("data:");
            this.saveAppendedText();
            if (object instanceof String text) {
                object = StringUtils.replace(text, "\n", "\ndata:");
            }

            this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(object, mediaType));
            this.append('\n');
            return this;
        }

        SseEventBuilderImpl append(String text) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }

            this.sb.append(text);
            return this;
        }

        SseEventBuilderImpl append(char ch) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(ch);
            return this;
        }

        public Set<ResponseBodyEmitter.DataWithMediaType> build() {
            if (!StringUtils.hasLength(this.sb) && this.dataToSend.isEmpty()) {
                return Collections.emptySet();
            } else {
                this.append('\n');
                this.saveAppendedText();
                return this.dataToSend;
            }
        }

        private void saveAppendedText() {
            if (this.sb != null) {
                this.dataToSend.add(new ResponseBodyEmitter.DataWithMediaType(this.sb.toString(), SseEmitter.TEXT_PLAIN));
                this.sb = null;
            }

        }
    }
}
