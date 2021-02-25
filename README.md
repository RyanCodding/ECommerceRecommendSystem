#  大数据电商推荐系统
## 第1章 项目体系架构设计
### 1.1 项目系统架构
项目以推荐系统建设领域知名的经过修改过的中文亚马逊电商数据集作为依托，以某电商网站真实业务数据架构为基础，构建了符合教学体系的一体化的电商推荐系统，包含了离线推荐与实时推荐体系，综合利用了协同过滤算法以及基于内容的推荐方法来提供混合推荐。提供了从前端应用、后台服务、算法设计实现、平台部署等多方位的闭环的业务实现。

![flow.png](http://chuantu.xyz/t6/741/1614246430x1033348286.png)

**用户可视化**：主要负责实现和用户的交互以及业务数据的展示，主体采用AngularJS2进行实现，部署在Apache服务上(后准备改为html+ngix)。

**综合业务服务**：主要实现JavaEE层面整体的业务逻辑，通过SpringBoot进行构建，对接业务需求。

【**数据存储部分**】

**业务数据库**：项目采用广泛应用的文档数据库MongoDB作为主数据库，主要负责平台业务逻辑数据的存储。

**缓存数据库**：项目采用Redis作为缓存数据库，主要用来支撑实时推荐系统部分对于数据的高速获取需求。

【**离线推荐部分**】

**离线统计服务**：批处理统计性业务采用Spark Core + Spark SQL进行实现，实现对指标类数据的统计任务。

**离线推荐服务**：离线推荐业务采用Spark Core + Spark MLLib进行实现，采用ALS算法进行实现。

【**实时推荐部分**】

**日志采集服务**：通过利用Flume-ng对业务平台中用户对于商品的一次评分行为进行采集，实时发送到Kafka集群。

**消息缓冲服务**：项目采用Kafka作为流式数据的缓存组件，接受来自Flume的数据采集请求。并将数据推送到项目的实时推荐系统部分。

**实时推荐服务**：项目采用Spark Streaming作为实时推荐系统，通过接收Kafka中缓存的数据，通过设计的推荐算法实现对实时推荐的数据处理，并将结构合并更新到MongoDB数据库。
## 1.2 项目数据流程
![data.png](http://chuantu.xyz/t6/741/1614246497x1700340443.png)

【**系统初始化部分**】
1. 通过Spark SQL将系统初始化数据加载到MongoDB中。

【**离线推荐部分**】
1. 可以通过Azkaban实现对于离线统计服务以离线推荐服务的调度，通过设定的运行时间完成对任务的触发执行。
2. 离线统计服务从MongoDB中加载数据，将【商品平均评分统计】、【商品评分个数统计】、【最近商品评分个数统计】三个统计算法进行运行实现，并将计算结果回写到MongoDB中；离线推荐服务从MongoDB中加载数据，通过ALS算法分别将【用户推荐结果矩阵】、【影片相似度矩阵】回写到MongoDB中。

【**实时推荐部分**】
1.	Flume从综合业务服务的运行日志中读取日志更新，并将更新的日志实时推送到Kafka中；Kafka在收到这些日志之后，通过kafkaStream程序对获取的日志信息进行过滤处理，获取用户评分数据流【UID|MID|SCORE|TIMESTAMP】，并发送到另外一个Kafka队列；Spark Streaming监听Kafka队列，实时获取Kafka过滤出来的用户评分数据流，融合存储在Redis中的用户最近评分队列数据，提交给实时推荐算法，完成对用户新的推荐结果计算；计算完成之后，将新的推荐结构和MongDB数据库中的推荐结果进行合并。

【**业务系统部分**】
1.	推荐结果展示部分，从MongoDB中将离线推荐结果、实时推荐结果、内容推荐结果进行混合，综合给出相对应的数据。
2.	商品信息查询服务通过对接MongoDB实现对商品信息的查询操作。
3.	商品评分部分，获取用户通过UI给出的评分动作，后台服务进行数据库记录后，一方面将数据推动到Redis群中，另一方面，通过预设的日志框架输出到Tomcat中的日志中。
4.	商品标签部分，项目提供用户对商品打标签服务。
## 1.3 数据模型
1.	Product【商品数据表】

      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  productId | Int  |  商品的ID |   |
      |name   | String  | 商品的名称  |   |
      |  categories | String  | 商品所属类别  | 每一项用&#124;分割  |
      |  imageUrl | String  | 商品图片的URL  |   |
      | tags  | String  | 商品的UGC标签  | 每一项用&#124;分割  |
2.	Rating【用户评分表】

      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  userId | Int  |  用户的ID |   |
      |productId   | Int  | 商品的ID  |   |
      |  score | Double  | 商品的分值  | |
      |  timestamp | Long  | 评分的时间  |   |

3.	Tag【商品标签表】
      
      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  userId | Int  |  用户的ID |   |
      |productId   | Int  | 商品的ID  |   |
      |  tag | String  | 商品的标签  | |
      |  timestamp | Long  | 评分的时间  |   |

4.	User【用户表】
      
      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  userId | Int  |  用户的ID |   |
      |username   |String  | 用户名  |   |
      |  password | String  | 用户密码  | |
      |  timestamp | Lon0067  | 用户创建的时间  |   |
5.	RateMoreProductsRecently【最近商品评分个数统计表】
      
      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  productId | Int  |  商品的ID |   |
      |count   |Int  | 商品的评分数  |   |
      |  yearmonth | String  | 评分的时段  |yyyymm |

6.	RateMoreProducts【商品评分个数统计表】

      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  productId | Int  |  商品的ID |   |
      |count   |Int  | 商品的评分数  |   |
      
7.	AverageProductsScore【商品平均评分表】
      
      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  productId | Int  |  商品的ID |   |
      |avg   |Double  | 商品的平均评分  |   |
8.	ProductRecs【商品相似性矩阵】
      
      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  productId | Int  |  商品的ID |   |
      |recs   |Array[(productId:Int,score:Double)]  | 该商品最相似的商品集合  |   |
9.	UserRecs【用户商品推荐矩阵】
      
      |  字段名 | 字段类型  | 字段描述  |字段备注   |
      | ------------ | ------------ | ------------ | ------------ |
      |  userId | Int  |  用户的ID |   |
      |recs   |Array[(productId:Int,score:Double)]  | 	推荐给该用户的商品集合	  |   |
10.	StreamRecs【用户实时商品推荐矩阵】
       
       |  字段名 | 字段类型  | 字段描述  |字段备注   |
       | ------------ | ------------ | ------------ | ------------ |
       |  userId | Int  |  用户的ID |   |
       |recs   |Array[(productId:Int,score:Double)]  | 	实时推荐给该用户的商品集合		  |   |
## 第2章 工具环境搭建   
我们的项目中用到了多种工具进行数据的存储、计算、采集和传输，本章主要简单介绍设计的工具环境搭建。
如果机器的配置不足，推荐只采用一台虚拟机进行配置，而非完全分布式，将该虚拟机CPU的内存设置的尽可能大，推荐为CPU > 4、MEM > 4GB。
### 2.1 MongoDB、Redis、Spark、Zookeeper、Flume-ng、Kafka 暂时配置单节点，后期改高可用
## 第3章 离线推荐服务建设
### 3.1 离线推荐服务
离线推荐服务是综合用户所有的历史数据，利用设定的离线统计算法和离线推荐算法周期性的进行结果统计与保存，计算的结果在一定时间周期内是固定不变的，变更的频率取决于算法调度的频率。
离线推荐服务主要计算一些可以预先进行统计和计算的指标，为实时计算和前端业务相应提供数据支撑。
离线推荐服务主要分为统计推荐、基于隐语义模型的协同过滤推荐以及基于内容和基于Item-CF的相似推荐。我们这一章主要介绍前两部分，基于内容和Item-CF的推荐在整体结构和实现上是类似的。
### 3.2 基于隐语义模型的协同过滤推荐
项目采用 ALS 作为协同过滤算法，根据 MongoDB 中的用户评分表计算离线的用户商品推荐列表以及商品相似度矩阵。
#### 3.2.1 用户商品推荐列表
通过 ALS 训练出来的 Model 来计算所有当前用户商品的推荐列表，主要思路如下：

1. userId 和 productId 做笛卡尔积，产生（userId，productId）的元组
2. 通过模型预测（userId，productId）对应的评分。
3. 将预测结果通过预测分值进行排序。
4. 返回分值最大的 K 个商品，作为当前用户的推荐列表。
   最后生成的数据结构如下：将数据保存到 MongoDB 的 UserRecs 表中
   
   ![sim.png](http://chuantu.xyz/t6/741/1614246850x1033348286.png)

### 3.2.2 商品相似度矩阵
通过ALS计算商品相似度矩阵，该矩阵用于查询当前商品的相似商品并为实时推荐系统服务。
离线计算的ALS 算法，算法最终会为用户、商品分别生成最终的特征矩阵，分别是表示用户特征矩阵的U(m x k)矩阵，每个用户由 k个特征描述；表示物品特征矩阵的V(n x k)矩阵，每个物品也由 k 个特征描述。
V(n x k)表示物品特征矩阵，每一行是一个 k 维向量，虽然我们并不知道每一个维度的特征意义是什么，但是k 个维度的数学向量表示了该行对应商品的特征。
所以，每个商品用V(n x k)每一行的 向量表示其特征，于是任意两个商品 p：特征向量为 ，商品q：特征向量为 之间的相似度sim(p,q)可以使用 和 的余弦值来表示：

<img src="https://latex.codecogs.com/gif.latex?Sim(p,q)&space;=&space;\frac{\sum\limits^{k}_{i=0}(t_{pi}\times&space;t_{qi})}{\sqrt{\sum\limits^{k}_{i=0}k^2_{pi&space;}\times\sum\limits^{k}_{i=0}k^2_{qi&space;}&space;}}" title="Sim(p,q) = \frac{\sum\limits^{k}_{i=0}(t_{pi}\times t_{qi})}{\sqrt{\sum\limits^{k}_{i=0}k^2_{pi }\times\sum\limits^{k}_{i=0}k^2_{qi } }}" />

数据集中任意两个商品间相似度都可以由公式计算得到，商品与商品之间的相似度在一段时间内基本是固定值。最后生成的数据保存到MongoDB的ProductRecs表中。

![sim.png](http://chuantu.xyz/t6/741/1614246601x1700340443.png)

### 3.2.3 模型评估和参数选取
在上述模型训练的过程中，我们直接给定了隐语义模型的rank,iterations,lambda三个参数。对于我们的模型，这并不一定是最优的参数选取，所以我们需要对模型进行评估。通常的做法是计算均方根误差（RMSE），考察预测评分与实际评分之间的误差。

<img src="https://latex.codecogs.com/gif.latex?RMSE&space;=&space;\sqrt{\frac{1}{n}\sum_{n}^{t=1}(observed_{t}-predicted_{t})^2}" title="RMSE = \sqrt{\frac{1}{n}\sum_{n}^{t=1}(observed_{t}-predicted_{t})^2}" />

有了RMSE，我们可以就可以通过多次调整参数值，来选取RMSE最小的一组作为我们模型的优化选择。
## 第4章 实时推荐服务建设
### 4.1 实时推荐服务
实时计算与离线计算应用于推荐系统上最大的不同在于实时计算推荐结果应该反映最近一段时间用户近期的偏好，而离线计算推荐结果则是根据用户从第一次评分起的所有评分记录来计算用户总体的偏好。
用户对物品的偏好随着时间的推移总是会改变的。比如一个用户u 在某时刻对商品p 给予了极高的评分，那么在近期一段时候，u 极有可能很喜欢与商品p 类似的其他商品；而如果用户u 在某时刻对商品q 给予了极低的评分，那么在近期一段时候，u 极有可能不喜欢与商品q 类似的其他商品。所以对于实时推荐，当用户对一个商品进行了评价后，用户会希望推荐结果基于最近这几次评分进行一定的更新，使得推荐结果匹配用户近期的偏好，满足用户近期的口味。
如果实时推荐继续采用离线推荐中的ALS 算法，由于算法运行时间巨大，不具有实时得到新的推荐结果的能力；并且由于算法本身的使用的是评分表，用户本次评分后只更新了总评分表中的一项，使得算法运行后的推荐结果与用户本次评分之前的推荐结果基本没有多少差别，从而给用户一种推荐结果一直没变化的感觉，很影响用户体验。
另外，在实时推荐中由于时间性能上要满足实时或者准实时的要求，所以算法的计算量不能太大，避免复杂、过多的计算造成用户体验的下降。鉴于此，推荐精度往往不会很高。实时推荐系统更关心推荐结果的动态变化能力，只要更新推荐结果的理由合理即可，至于推荐的精度要求则可以适当放宽。
所以对于实时推荐算法，主要有两点需求：

（1）用户本次评分后、或最近几个评分后系统可以明显的更新推荐结果；

（2）计算量不大，满足响应时间上的实时或者准实时要求；
### 4.2 实时推荐模型
#### 4.2.1 实时推荐模型算法设计
当用户u 对商品p 进行了评分，将触发一次对u 的推荐结果的更新。由于用户u 对商品p 评分，对于用户u 来说，他与p 最相似的商品们之间的推荐强度将发生变化，所以选取与商品p 最相似的K 个商品作为候选商品。
每个候选商品按照“推荐优先级”这一权重作为衡量这个商品被推荐给用户u 的优先级。
这些商品将根据用户u 最近的若干评分计算出各自对用户u 的推荐优先级，然后与上次对用户u 的实时推荐结果的进行基于推荐优先级的合并、替换得到更新后的推荐结果。
具体来说：
首先，获取用户u 按时间顺序最近的K 个评分，记为RK；获取商品p 的最相似的K 个商品集合，记为S；
然后，对于每个商品<img src="https://latex.codecogs.com/gif.latex?q\in&space;S" title="q\in S" /> ，计算其推荐优先级<img src="https://latex.codecogs.com/gif.latex?E_{qu}" title="E_{qu}" />，计算公式如下：

<img src="https://latex.codecogs.com/gif.latex?E_{uq}&space;=&space;\frac{\sum\limits_{r\in&space;RK}sim(q,r)\times&space;R_{r}&space;}{sim\_sum}&plus;lgmax\left\{incount,1&space;\right\}-lgmax\left\{recount,1&space;\right\}" title="E_{uq} = \frac{\sum\limits_{r\in RK}sim(q,r)\times R_{r} }{sim\_sum}+lgmax\left\{incount,1 \right\}-lgmax\left\{recount,1 \right\}" />

其中：
表示用户u 对商品r 的评分；
sim(q,r)表示商品q 与商品r 的相似度，设定最小相似度为0.6，当商品q和商品r 相似度低于0.6 的阈值，则视为两者不相关并忽略；
sim_sum 表示q 与RK 中商品相似度大于最小阈值的个数；
incount 表示RK 中与商品q 相似的、且本身评分较高（>=3）的商品个数；
recount 表示RK 中与商品q 相似的、且本身评分较低（<3）的商品个数；

**公式的意义如下**：

首先对于每个候选商品q，从u 最近的K 个评分中，找出与q 相似度较高（>=0.6）的u 已评分商品们，对于这些商品们中的每个商品r，将r 与q 的相似度乘以用户u 对r 的评分，将这些乘积计算平均数，作为用户u 对商品q 的评分预测即

<img src="https://latex.codecogs.com/gif.latex?\frac{\sum&space;\limits_{r\in&space;RK&space;}sim(q,r)\times&space;R_{r}}{sim\_sum}" title="\frac{\sum \limits_{r\in RK }sim(q,r)\times R_{r}}{sim\_sum}" />

然后，将u 最近的K 个评分中与商品q 相似的、且本身评分较高（>=3）的商品个数记为 incount，计算lgmax{incount,1}作为商品 q 的“增强因子”，意义在于商品q 与u 的最近K 个评分中的n 个高评分(>=3)商品相似，则商品q 的优先级被增加lgmax{incount,1}。如果商品 q 与 u 的最近 K 个评分中相似的高评分商品越多，也就是说n 越大，则商品q 更应该被推荐，所以推荐优先级被增强的幅度较大；如果商品q 与u 的最近K 个评分中相似的高评分商品越少，也就是n 越小，则推荐优先级被增强的幅度较小；

而后，将u 最近的K 个评分中与商品q 相似的、且本身评分较低（<3）的商品个数记为 recount，计算lgmax{recount,1}作为商品 q 的“削弱因子”，意义在于商品q 与u 的最近K 个评分中的n 个低评分(<3)商品相似，则商品q 的优先级被削减lgmax{incount,1}。如果商品 q 与 u 的最近 K 个评分中相似的低评分商品越多，也就是说n 越大，则商品q 更不应该被推荐，所以推荐优先级被减弱的幅度较大；如果商品q 与u 的最近K 个评分中相似的低评分商品越少，也就是n 越小，则推荐优先级被减弱的幅度较小；

最后，将增强因子增加到上述的预测评分中，并减去削弱因子，得到最终的q 商品对于u 的推荐优先级。在计算完每个候选商品q 的 后，将生成一组<商品q 的ID, q 的推荐优先级>的列表updatedList：

<img src="https://latex.codecogs.com/gif.latex?updataList&space;=&space;\bigcup&space;\limits_{q\in&space;S}\left\{qID,E_{uq}&space;\right\}" title="updataList = \bigcup \limits_{q\in S}\left\{qID,E_{uq} \right\}" />

而在本次为用户u 实时推荐之前的上一次实时推荐结果Rec 也是一组<商品m,m 的推荐优先级>的列表，其大小也为K：

<img src="https://latex.codecogs.com/gif.latex?Rec=&space;\bigcup&space;\limits_{m\in&space;Rec}\left\{mID,E_{um}&space;\right\}&space;,len(Rec)=K" title="Rec= \bigcup \limits_{m\in Rec}\left\{mID,E_{um} \right\} ,len(Rec)=K" />

接下来，将updated_S 与本次为u 实时推荐之前的上一次实时推荐结果Rec进行基于合并、替换形成新的推荐结果NewRec：

<img src="https://latex.codecogs.com/gif.latex?New&space;Rec&space;=&space;topK(i\in&space;Rec\cup&space;updateList,cmp=E_{ui})" title="New Rec = topK(i\in Rec\cup updateList,cmp=E_{ui})" />

其中，i表示updated_S 与Rec 的商品集合中的每个商品，topK 是一个函数，表示从 Rec updated _ S中选择出最大的 K 个商品，cmp =   表示topK 函数将推荐优先级 值最大的K 个商品选出来。最终，NewRec 即为经过用户u 对商品p 评分后触发的实时推荐得到的最新推荐结果。

总之，实时推荐算法流程流程基本如下：

（1）用户u 对商品p 进行了评分，触发了实时推荐的一次计算；

（2）选出商品p 最相似的K 个商品作为集合S；

（3）获取用户u 最近时间内的K 条评分，包含本次评分，作为集合RK；

（4）计算商品的推荐优先级，产生<qID,>集合updated_S；

将updated_S 与上次对用户u 的推荐结果Rec 利用公式(4-4)进行合并，产生新的推荐结果NewRec；作为最终输出。
### 4.3 实时推荐算法的实现
实时推荐算法的前提：
1.	在Redis集群中存储了每一个用户最近对商品的K次评分。实时算法可以快速获取。
2.	离线推荐算法已经将商品相似度矩阵提前计算到了MongoDB中。
3.	Kafka已经获取到了用户实时的评分数据。
      算法过程如下：
      实时推荐算法输入为一个评分<userId, productId, rate, timestamp>，而执行的核心内容包括：获取userId 最近K 次评分、获取productId 最相似K 个商品、计算候选商品的推荐优先级、更新对userId 的实时推荐结果。

### 4.4 实时系统联调
系统实时推荐的数据流向是：业务系统 -> 日志 -> flume 日志采集 -> kafka streaming数据清洗和预处理 -> spark streaming 流式计算。
## 第5章 冷启动问题处理
整个推荐系统更多的是依赖于用于的偏好信息进行商品的推荐，那么就会存在一个问题，对于新注册的用户是没有任何偏好信息记录的，那这个时候推荐就会出现问题，导致没有任何推荐的项目出现。
处理这个问题一般是通过当用户首次登陆时，为用户提供交互式的窗口来获取用户对于物品的偏好，让用户勾选预设的兴趣标签。
当获取用户的偏好之后，就可以直接给出相应类型商品的推荐。

## 第6章 其它形式的离线相似推荐服务
### 6.1 基于内容的相似推荐
原始数据中的tag文件，是用户给商品打上的标签，这部分内容想要直接转成评分并不容易，不过我们可以将标签内容进行提取，得到商品的内容特征向量，进而可以通过求取相似度矩阵。这部分可以与实时推荐系统直接对接，计算出与用户当前评分商品的相似商品，实现基于内容的实时推荐。
为了避免热门标签对特征提取的影响，我们还可以通过TF-IDF算法对标签的权重进行调整，从而尽可能地接近用户偏好。
### 6.2 基于物品的协同过滤相似推荐
基于物品的协同过滤（Item-CF），只需收集用户的常规行为数据（比如点击、收藏、购买）就可以得到商品间的相似度，在实际项目中应用很广。
我们的整体思想是，如果两个商品有同样的受众（感兴趣的人群），那么它们就是有内在相关性的。所以可以利用已有的行为数据，分析商品受众的相似程度，进而得出商品间的相似度。我们把这种方法定义为物品的“同现相似度”，公式如下：

<img src="https://latex.codecogs.com/gif.latex?w_{ij}=\frac{|N_{i}\cap&space;N_{j}|}{\sqrt{|N_{i}||N_{j}|}}" title="w_{ij}=\frac{|N_{i}\cap N_{j}|}{\sqrt{|N_{i}||N_{j}|}}" />

