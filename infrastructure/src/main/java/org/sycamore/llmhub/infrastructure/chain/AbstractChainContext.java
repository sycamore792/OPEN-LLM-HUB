package org.sycamore.llmhub.infrastructure.chain;

import org.assertj.core.util.Maps;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.sycamore.llmhub.infrastructure.common.ApplicationContextHolder;

import java.util.*;

/**
 * @author 桑运昌
 */

public final class AbstractChainContext<T> implements CommandLineRunner {
    
    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();
    
    /**
     * 责任链组件执行
     *
     * @param requestParam 请求参数
     */
    public void handler(String mark, T requestParam) {
        abstractChainHandlerContainer.get(mark).stream()
                .sorted(Comparator.comparing(Ordered::getOrder)).forEach(each -> each.handler(requestParam));
    }
    
    @Override
    public void run(String... args) throws Exception {
        // 获取 Spring IOC 容器中所有 AbstractChainHandler 接口实现
        Map<String, AbstractChainHandler> chainFilterMap = ApplicationContextHolder.getBeansOfType(AbstractChainHandler.class);
        chainFilterMap.forEach((beanName, bean) -> {
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(bean.mark());
            if (abstractChainHandlers == null) {
                abstractChainHandlers = new ArrayList();
            }
            abstractChainHandlers.add(bean);
            // 根据 mark 标识将责任链模式分类，放入责任链容器上下文中
            abstractChainHandlerContainer.put(bean.mark(), abstractChainHandlers);
        });
    }
}