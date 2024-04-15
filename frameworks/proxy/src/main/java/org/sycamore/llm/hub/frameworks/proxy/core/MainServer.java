package org.sycamore.llm.hub.frameworks.proxy.core;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
import io.netty.channel.epoll.Epoll;
import lombok.extern.slf4j.Slf4j;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.sycamore.llm.hub.frameworks.proxy.config.NettyServerProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Sycamore
 * @date: 2024/4/15 15:54
 * @version: 1.0
 * @description: 主启动类
 */
@Slf4j
@RequiredArgsConstructor
public class MainServer implements ApplicationContextAware {
    private final NettyServerProperties nettyServerProperties;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private List<ChannelHandler> handlers;
    public void start() throws Exception {
        //初始化工作组
        this.init();

        try {
            serverBootstrap.group(bossGroup, workerGroup) // 设置主和工作事件循环组
                    .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class) // 设置服务器通道类型
                    .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                    .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                    .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                    .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                    .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小
                    .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 设置连接的处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(),                                       // HTTP服务器编解码器
                                    new HttpObjectAggregator(65536)             // 添加HTTP对象聚合器，用于将多个消息转换为单一的FullHttpRequest或FullHttpResponse

                            );
                            //加载用户自定义处理器
                            if (Objects.nonNull(handlers)&&!handlers.isEmpty()){
                                handlers.forEach(handler ->{
                                    ch.pipeline().addLast(handler);
                                    log.info("loaded user handler：{}",handler.getClass().getName());
                                });
                            }
                        }
                    });
            Channel ch = serverBootstrap.bind(nettyServerProperties.getPort()).sync().channel(); // 绑定端口并同步等待成功
            log.info("proxy server app started success, server properties info --> {}", nettyServerProperties.toString());
            ch.closeFuture().sync(); // 等待服务端链路关闭
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully(); // 优雅关闭boss线程组
                log.info("boos group has bean shutdown");
            }

            if (workerGroup != null) {
                workerGroup.shutdownGracefully(); // 优雅关闭worker线程组
                log.info("worker group has bean shutdown");
            }
            log.info("server has bean shutdown, bye~");
        }
    }
    private void init() {
        // 创建服务端启动引导
        this.serverBootstrap  = new ServerBootstrap();
        if (useEpoll()) {
            // 判断是否使用Epoll模型，这是Linux系统下的高性能网络通信模型
            log.info("os support epoll, selected EpollEventLoop model");
            this.bossGroup = new EpollEventLoopGroup(nettyServerProperties.getBossGroupCapacity(),
                    new DefaultThreadFactory("epoll-netty-boss-nio"));
            this.workerGroup = new EpollEventLoopGroup(nettyServerProperties.getWorkerGroupCapacity(),
                    new DefaultThreadFactory("epoll-netty-worker-nio"));
        } else {
            log.info("os not support epoll, selected NioEventLoop model");
            // 否则使用默认的NIO模型
            this.bossGroup = new NioEventLoopGroup(nettyServerProperties.getBossGroupCapacity(),
                    new DefaultThreadFactory("default-netty-boss-nio"));
            this.workerGroup = new NioEventLoopGroup(nettyServerProperties.getWorkerGroupCapacity(),
                    new DefaultThreadFactory("default-netty-worker-nio"));
        }
    }

    // 检测是否使用Epoll优化性能
    private boolean useEpoll() {
        return SystemUtils.IS_OS_LINUX && Epoll.isAvailable();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ServerHandler.class);
        // 创建一个列表来保存排序后的beans
        List<Map.Entry<String, Object>> sortedBeans = new ArrayList<>(beansWithAnnotation.entrySet());
        // 按照注解上的order属性值对beans进行排序
        sortedBeans.sort((entry1, entry2) -> {
            ServerHandler annotation1 = entry1.getValue().getClass().getAnnotation(ServerHandler.class);
            ServerHandler annotation2 = entry2.getValue().getClass().getAnnotation(ServerHandler.class);
            return Integer.compare(annotation1.order(), annotation2.order());
        });
        // 排序后的beans执行操作
        sortedBeans.forEach(entry -> {
            if (entry.getValue() instanceof ChannelHandler) {
                if (Objects.isNull(handlers)) {
                    handlers = new ArrayList<>();
                }
                handlers.add((ChannelHandler) entry.getValue());
                log.info("find server handler: {}", entry.getKey());
            }
        });
    }
}
