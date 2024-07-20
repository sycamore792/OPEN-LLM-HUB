# 🤖OPEN-LLM-HUB

<span style="font-size: 30px;"> <strong>Description：</strong></span>
<p style="color: #adaba2;font-size: 20px;font-family: 'DejaVu Sans Mono'">
<strong>LLM管理平台，屏蔽多LLM厂商的API差异化，提供高性能的统一服务接口（OpenAI API）并定制接口业务逻辑
</strong>
</p> 
项目关键词：
<li>高性能服务器</li>
<li>业务场景广覆盖</li>
<li>开箱即用</li>


<br>


<span style="font-size: 30px;"> <strong>项目版本</strong></span>
<li>v1.0：<strong>基本功能实现 -> 模型服务管理、服务调用、监控等</strong></li>
<li>v2.0：<strong>接口保障性功能实现 -> 熔断降级、限流、负载均衡等</strong></li>
<li>v3.0：<strong>服务高可用（模型调用接口）</strong></li>
<br>

<span style="font-size: 30px;"> <strong>🚀更新</strong></span>


<li>24.4.16：模型服务统一网关：路由验证、token鉴权</li>




<br>
<span style="font-size: 30px;"> <strong style="color: blanchedalmond">TODO</strong></span>

- [x] 流式数据转发
- [x] 请求适配器
- [x] 响应适配器
- [x] sse协议
- [x] 非流式协议
- [x] 提供默认的模型API协议支持
- [ ] 限流、频控
- [ ] 调度策略(分布式优先级队列)
- [ ] websocket协议支持
- [x] 流式接口与非流式接口（能否整合？）
- [ ] 数据埋点收集
- [ ] ....

<br>
<span style="font-size: 30px;"> <strong>Note 📓</strong></span>

### 一些思考：

    1.  对于一个模型服务平台来讲，流量集中于模型调用，因此，需要结合考虑模型调用的并发能力，
    可拓展性，需要提供一个高性能的外部接口。这里我们考虑使用 netty 作为外部接口的实现框架，有如下几点考虑：
        a. netty 是一个高性能的异步网络框架，可以支持高并发的网络请求，考虑到模型调用场景以长连接为主，常规http框架网路模型弊端？
        b. netty 的网络协议高度可定制，符合我们的业务需求。
        c. 。。。。
    基于以上几点的综合考虑，确定使用 netty 来实现该项目的核心组件：高性能的请求转发服务器。对于该组件，我们可以想象到的几点是：
        a. 该组件应设置多处 SPI 扩展点，如：
            - 请求解析处理器（熔断？限流？降级？）
            - 请求路由处理器（根据业务标识加载请求配置 OpenApi）
            - 客户端侧根据OpenApi发起请求
        b. 细颗粒度配置支持，将所有配置开放给用户（在默认配置模板的支持下）
        c. 路由表路径匹配：DFA？Hash？ 
            哈希解决不了路径参数问题
            DFA : 将 URI 按分割符进行分割构建 DFA 树，以此进行快速路由判断（ps：这里可能涉及路径参数，需要对传统dfa做一些改动; 路由更新涉及DFA树的reload）
                
    2. 整合多模型厂商通信协议：
        a. 理想情况：无代码侵入，通过配置实现协议适配
        b. 类似场景大家的做法都是使用适配器，通过硬编码集成到项目中
        c. ''适配器模式''

    3. 核心还是服务端与客户端的桥接，通过共用一个事件循环