# 具体问题


## 整体框架逻辑方面

* 具体模块分工, 参考别的方案
* 任务的派发, push模式 还是 pull模式? master往worker分发, 还是worker从某个地方主动认领?

* 权限管理? 怎么在调度系统里面支持或配合支持权限管理? 因为是代理执行, 权限怎么传递到后续系统里面去? 比如Hdfs,MR,Hbase,Hive,Spark之类的系统里去?
  * 变通的方法, Hive/Sql之类的, 在前端脚本里面进行进行解析判断, 如果没有权限的表就不让操作. Hive server能配置gateway机器?


## 与客户端交互逻辑相关

* 如何提供Job/Task的状态通知.
  * Pull的方式, Rest的接口, 最简单的, 系统提供接口查询状态?
  * 或者走ZK等形式通知?
  * 或者单独提供Server, 提供回调形式的接口? (这个可以在上面的形式中包装起来?) 有没有可能做到性能和响应速度兼顾?
  
* 如和获取当前计划Plan的拓扑逻辑.

## 调度逻辑相关

* 任务触发机制

考虑支持触发方式, 同样的job, 需要可以支持不同的前后续处理方式, 比如前续: 立即触发不管前提条件. 后续: 触发后续依赖任务, 和不触发后续依赖任务.

* 定时任务的多次触发逻辑

  * 定时任务, 重复执行的, 如果前一次触发没运行完,后一次触发怎么办? 
    * 是否要按类型? 选择忽略? 允许多次重叠触发? 跑后面一个停掉前一个? -> 策略是否可以做出来让用户可以选择. 这些策略能合并成一个简单的字段来描述? 用bit位来表示? 用json串来表示?
  * 由于运行失败,或者Master重置, 导致多次分配任务给Worker的这种
    * master端应该防止这种情况? 否则,发到多个worker上, worker之间也没法沟通判断. master怎么防止这种情况?
    * 如果分到一个worker, worker自己能判断去重? 做为辅助防错手段. 至少能留下error或warning信息

* 同时有任务依赖和时间触发两种依赖关系的作业的处理逻辑.
  * 如何处理复杂的任务依赖的调度逻辑? 比如前续任务的调度周期和依赖任务的调度周期不一致? 依赖A/B, A一个小时执行一次, B两天执行一次. C一天执行一次, C如何判断什么时候该执行? 以哪一次A或B的执行成功情况为准?

* 如何支持暂停调度系统自动调度的功能.

暂停,重启等逻辑如何支持?  什么场景下需要? 讨论一下case.

* 修改任务状态

比如希望重新运行一些的任务, 需要重置任务状态? 提供一个手动run任务的可选执行方式机制

* 如何override自动调度策略

比如需要跳过任务, 需要强制执行, 需要假装成功, 需要放弃任务等等和自动调度策略和运行逻辑可能冲突的. 这些问题如何解决? 这个大概是静态plan的好处之一, 可以手工修改plan和task状态, 动态计算的plan, plan在内存中, 怎么做到这一点. 有什么办法.

* 一条具体路径的重置

比如要完成一条关键路径的触发工作, 从后往前找依赖关系, 重新执行部分重要任务 -> 这个应该通过一些外部逻辑来实现, 另外我们是否要做到这么复杂? 还是简单的提供: 单个重run, 单个以及后续重run? 只是效率的区别, 我们可以考虑后续再加强这一块, 不过底层调度系统需要提供一些基本原子操作来支持. 比如任务重置, 手工触发之类. 这个要考虑上.

* 如何标识作业,任务, 某次运行等?

我们希望达到的状态是: 每个作业配置有唯一标识, 每次计划运行有唯一标识, 每次重试也有唯一标识. 然后这几个标识要能很容易的关联起来.  
目前我们的系统里有Jobid/taskid/planid 这些的逻辑是? 

* 作业优先级的处理逻辑?
  * 分队列? 还是同一个Ready队列,执行的时候优先执行高优先级任务?
  * 是否需要自动的优先级调整的策略? 比如失败重试的, 调高优先级, 但是又不能调到最高....
  * 优先级怎么控制住? 用户可以随意设置优先级的话, 就要靠自觉了, 用户加系统控制怎么结合? 优先级体系怎么设置合理, 需要考虑.
  
* 如何检测循环依赖关系? 即确保调度依赖是个DAG图?


## 资源控制/负载相关

* Worker机器的资源控制?

是否通过配置内存,CPU等资源配额来管理任务并发? Worker自身要控制自己的负载情况决定是否执行任务.怎么提供反馈机制? 比如拒绝执行, 当前等待队列等.  
是否应该反过来? worker主动pull任务?

* 任务往Worker的分发的逻辑?

部分基于响应的原则? Worker注册上来以后, 向master发送心跳信息, 心跳信息包含当前worker可用资源情况? Master根据worker负载情况分配任务, 如果worker一段时间没有心跳响应则不往该Worker发送后续任务. 这个的好处是worker的资源,负载情况可以动态检测,后续链路的负载情况也可以通过这种方式上报, 坏处是Master自己势必也要维护部分这些信息便于做任务派发, 要和心跳信息同步这些逻辑. 这里面的逻辑要想清楚.

* master分发逻辑的插件化?

需要提供哪些信息给分发逻辑的接口? 比如要做负载均衡等等.

* 用户的维度, 怎么控制用户的资源使用情况等(比如一个用户的负载导致其它任务没有资源执行)? 目前是否需要控制额度? 如果目前不控制, 要预留什么样的接口支持以后能控制?

* 定向指派任务的分发?

比如worker提供分组或标签或id信息, master根据作业的配置信息, 匹配worker来执行?

* Worker心跳丢失以后, 相关任务的运行逻辑?  kill掉相关任务? 怎么kill? 如果worker没有真正挂掉, 相关任务还没有处理, 没法kill? 后面又再起任务? 这些任务什么时候再跑? -> 可以设置当前任务Owner? 任务状态版本号? 这样worker可以更新任务状态, 检查任务Owner等, 比如5秒检查一次. Master可以修改当前任务Owner等? 这个可以考虑在DB里做还是在ZK里做. (这个抽象成模块, 便于更换逻辑.)

* Yarn集群的负载情况谁来监控? 如何控制? Master节点来收集? 这样感觉模块隔离做得不太好. 考虑做一个constraint模块? 这样可以向Master节点注册和更新constraint? 这样, Yarn集群的负载流控只是作为一个具体的constraint来实现? constraint模块可以是任务进去, 然后返回机器? 或者干脆不返回机器, 表示暂不执行? 这就要求constraint模块可以获取到当前的任务和集群信息. 这块怎么能做到通用?

## 任务执行流程相关

* 大批量数据的传输逻辑? 

比如hive的output结果这种? 走通用的任务结果输出接口? 还是任务结果输出只是一个message, message的内容具体再描述如何获得结果? 这样做的话,容易适配不同的应用场景, 但是就是不通用化处理了, 每个任务类型的输出, 客户端得自己实现解析.


## 监控相关

* 失败日志, 成功日志?

成功日志可以用来和失败日志比对, 但是尺寸大小这个怎么解决, 都放DB尺寸是个问题. 需要考虑日志的存放方式. 比如定期备份到HDFS上? 或者放到HBase里?

* 状态的监控的实现方式? 要深度built-in进去, 不是简单的log, 可插入的metrics方案要考虑一下. -> 需要去调研一下合适的技术手段. Jmx?  开源的框架? Spark/hadoop的实现?


## Executor执行容器相关

### 通用

* Job的任务参数,运行环境参数的传递

* 如何动态控制作业行为?  比如需要暂停? 重启? 关闭? 更新? 等等. 是否定义通用接口, 由Executor具体根据作业类型实现?

* jar包依赖关系的解决.

如果走MR方式来做的话, 考虑使用distributed cache来做, 对于MR任务, 直接作为hadoop job的参数使用. 对于Java任务, 在MR封装以后的Map任务中拿到路径, 再设为环境变量然后另起进程调用java程序. 减少依赖冲突的可能性?

### Hive

* Hive UDF怎么处理?
  * Cli模式下, 应该容易处理, 解决依赖关系, 用init脚本初始化就好, 可以动态部署, 是不是这样?
  * Hive Server模式下怎么处理, 如何做到动态部署?

* hive cli的模式, 对集群的配置, 在hiveql里面可以set. hive Server模式呢?

* Hive server2 和 hive cli / beeline的方式使用hive的比较?
  * 权限控制方面都是怎么处理的. 例如 hive server2 : https://cwiki.apache.org/confluence/display/Hive/Setting+Up+HiveServer2#SettingUpHiveServer2-Authentication/SecurityConfiguration
  * 执行效率的区别?
  * 负载区别?  (hive server2 会不会负载很重?)
  * udf之类的支持方式(hive server是不是要支持UDF就要更新hive server?)
  * oozie的hive action怎么实现的? 有不同类型?

### MR

### JAVA

### Shell

* 复杂的输出结果怎么反馈回来? 如果不是通过stdout的话, oozie的实现方式似乎是通过设置环境变量?


## 周边系统,外围工具应用逻辑

* Xray里面Job依赖逻辑的计算, Xray里面自动计算脚本对表的依赖, 但是实际上没有时间上的依赖关系逻辑判断, 如果我们的要求是只要表在,后续脚本就可以运行.即不管表内容的更新的时间. 也就是说其实脚本对生成这个表的脚本是没有依赖的, 这个逻辑怎么处理?

* 任务的批处理操作? 比如批量重置,暂停,重跑等等. 考虑在系统之外由周边工具实现, 要做哪些, 怎么做, 需要系统提供什么接口来支持?

* 还有哪些操作,是日常需要的, 可以提供, 不适合放在核心调度系统里面, 可以通过外围工具来支持的?

* 任务脚本的历史变更信息的维护, XRAY需要提供接口可以提交Hive任务脚本. xray可以自己维护历史版本,这个目前是怎么维护的? 但是于此同时, 关键是要暴露接口,允许外部使用方自己更容易做代码的集中管理.


## 需要描述的业务逻辑流程

* 作业注册,修改,删除,对应的如何调整作业依赖关系
* 作业的一次实例的状态变更通知流程
* 作业的一次实例的结果处理流程
* 作业的执行分发流程?

## 其它待细化的大方向的问题

* 权限,多用户管理相关.





