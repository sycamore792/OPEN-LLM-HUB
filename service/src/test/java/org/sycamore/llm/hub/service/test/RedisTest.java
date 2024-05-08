package org.sycamore.llm.hub.service.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: Sycamore
 * @date: 2024/4/29 23:21
 * @version: 1.0
 * @description: TODO
 */
@SpringBootTest
@RequiredArgsConstructor
public class RedisTest {
    private final RedissonClient redissonClient;

    @Test
    void test() {
        redissonClient.getLock("test").tryLock()
    }
}
