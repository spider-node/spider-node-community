## spider-node是什么
- spider可以将原本存在于代码中错综复杂的方法调用关系以可视化流程图的形式更直观的展示出来，并提供了将所见的方法节点加以控制的配置手段。spider-node可以管系统中的所有业务功能。业务都需要通过spider进行输出，从而spider管控了业务功能的生命周期。
  
- spider-node是一个研发标准定义者。
  
- spider-node是一个帮助研发团队快速构建业务中台，领域模型，实现系统之间最大程度解耦，从提供提升研发效率的中间件。

## spider-node想要解决的问题

- 【业务模糊】代码复杂、模型文档更新不及时，致使新同学和非技术同学不能短时间内了解业务现状。技术和非技术间对同一业务理解存在分歧而不自知。甚至业务Owner也不能很流畅的描述出自己所负责的业务，产品对研发实现的流程一无所知，再进行产品设计的时候，无法考虑到研发的代码流程，
  
- 【代码杂乱】项目中涉及到许多领域对象，对象间不仅存在复杂的前后依赖关系还相互掺杂没有明显边界，代码多次迭代后更是混乱不堪难以维护，迭代风险高，很难兼容全部的业务场景，当业务流程繁杂，存在需要延迟，轮询，异步等组合使用的情况下。该功能的全景，几乎很难被后者挖掘，以及迭代。
  存在于线上环境的偶现问题更是难以排查。需要一种可以通过简单操作就能将重要节点数据都保存下来的能力，此能力堪比对链路精细化梳理后的系统性日志打印
  
- 【事件的解耦】事件层面，并不解耦，只是把压力给到了订阅者，上游是自做完自身的事情，这样就导致，下游要各种关系上游的业务场景，以及业务字段，以及字段的取值逻辑等等，以及下游保证消费的成功与补偿。把下游做复杂了。
  
- 【测试复杂】业务场景多样，不乏一
  
- 【性能低下】某业务链路由一系列子任务组成，其中需要并行处理一些耗时长且数据间没有依赖的子任务，但苦于没有精简且无代码侵入的并发框架，
  
- 【平台之殇】维护平台型产品，为众多上游业务线提供着基础服务，但在短时间内应对各个业务方的定制化需求捉襟见肘，更不知如何做好平台与业务、业务与业务之间的隔离，更不知道系统之间交互，如何做到解耦。
  
- 【回溯困难】业务流转数据状态追踪困难，只些复杂的链路难以被测试覆盖。或者三方数据Mock困难，测试成本居高不下


## spider-node 有那些特点

- 【业务可视】编排好的图示模型即为代码真实的执行链路，通过所见（ 图示模型 ）即所得（ 代码执行 ）的方式在技术和业务之间架起一道通用语言的桥梁，使彼此之间沟通更加顺畅，在spider-ui中可快速修改模型，不需要发版进行部署。
  
- 【配置灵活】提供开始事件、结束事件、服务节点、排他网关、包含网关、并行网关、条件表达式，等配置组件，可以支持变态复杂的业务流程
  
- 【性能优异】最底层采用vertx + grpc + rocksdb 对单个域功能的损耗在1-2毫秒之间
  
- 【集群模式】支持使用zookeeper,hazelcast，作为集群底座，平行的架构模式，支持水平扩展。
  
- 【分布式事务】分布式基于seata-at改造而来。
  
- 【定义上线标准】通过配置参数，指定流程需要走到的域功能，来判断是否满足场景。
  
- 【领域模型】定于域，每个域只关注，自身的域功能/域对象（需要通过看spider-提供的研发方式来理解）
  
- 【中台】利用领域的属性，来搭建中台能力，（需要根据spider-提供的研发方式来理解）
  
- 【功能灰度】目前实现灰度都是从服务的部署版本来实现的灰度（一定层度上让硬件成本变高），spider提供功能版本在一个服务实例中进行灰度。

## spider-node 架构图
![在这里插入图片描述](/spider-framework.png)

## spider-node 官网
[http://www.spider-node.cn](http://www.spider-node.cn/)