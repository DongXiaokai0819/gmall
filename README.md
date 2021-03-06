# 分布式简易商城实战项目
----
## 项目介绍
> 功能点：
    分布式商城，简易的购物流程、简易后端运营平台。
技术点：
       核心技术为Spring Boot + Dubbo实现。

## 平台目录结构说明

├─gmall-parent----------------------------父项目，公共依赖<br/>
│  │<br/>
│  ├─服务提供者<br/>
│  │  │<br/>
│  │  ├─gmall-user-service----------------------------用户管理service服务<br/>
│  │  │<br/>
│  │  ├─gmall-manage-service----------------------------后台管理service服务<br/>
│  │  │<br/>
│  │  ├─gmall-search-service----------------------------搜索service服务<br/>
│  │  │<br/>
│  │  ├─gmall-cart-service----------------------------购物车service服务<br/>
│  │  │<br/>
│  │  ├─gmall-order-service----------------------------订单管理service服务<br/>
│  │  │<br/>
│  │  ├─gmall-cart-service----------------------------购物车service服务<br/>
│  ├─服务消费方<br/>
│  │  │<br/>
│  │  ├─gmall-user-web----------------------------用户管理web服务<br/>
│  │  │<br/>
│  │  ├─gmall-manage-web----------------------------后台管理web服务<br/>
│  │  │<br/>
│  │  ├─gmall-search-web----------------------------搜索web服务<br/>
│  │  │<br/>
│  │  ├─gmall-order-web----------------------------订单管理web服务<br/>
│  │  │<br/>
│  │  ├─gmall-cart-web----------------------------购物车web服务<br/>
│  │  │<br/>
│  │  ├─gmall-item-web----------------------------商品服务<br/>
│  │  │<br/>
│  │  ├─gmall-payment ----------------------------支付服务<br/>
│  │  │<br/>
│  │  ├─gmall-passport-web----------------------------权限管理服务<br/>
│  │  │<br/>
│  │  ├─gmall-seckill----------------------------秒杀服务<br/>
│  │  │<br/>
│  │  ├─gware-manage----------------------------库存服务<br/>
│  │<br/>
│  ├─gmall-api--------------------------公共核心依赖包<br/>
│  │<br/>
│  ├─gmall-common-util--------------------------公共工具包<br/>
│  │<br/>
│  ├─gmall-service-util--------------------------服务提供者工具包<br/>
│  │<br/>
│  ├─gmall-web-util--------------------------服务消费者依赖包<br/>




