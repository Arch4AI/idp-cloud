# idp-cloud

## 前言

一个统一的CLOUD前后端建设模版。

+ 支持多种分布式Session会话管理。
+ 支持单一用户会话配置。
+ 集成了JustAuth提供多社交类型的OAuth2.0协议登录.
+ 支持自定义OAuth2.0服务端配置。
+ 支持配置记住我(采用MySQL数据库存储).
+ 支持扩展多数据源.
+ 支持Quartz集群式定时任务配置。
+ 支持配置接口限流
+ 支持Post Raw请求加密
+ 支持短信选择器(Local、阿里云、腾讯云) 默认开启本地(Local)日志打印
+ 支持移动支付(阿里云网页端支付、腾讯云Native)
+ 支持对象存储文件(OSS、COS)
+ 支持国际化(简体中文、繁体中文、英文)
+ 支持WebSocket

## 技术框架

| 技术                                          | 版本            | 说明                    |
|---------------------------------------------|---------------|-----------------------|
| Spring Boot                                 | 3.0.5         | MVC核心框架               |
| Spring Security                             | 3.0.5         | Web应用安全防护             |
| MyBatis                                     | 3.5.11        | ORM框架                 |
| Spring data redis                           | 3.0.4         | redis连接库              |
| spring-security-oauth2-authorization-server | 1.0.1         | OAuth2.0/OIDC v1服务端   |
| lettuce                                     | 6.2.3.RELEASE | redis连接池              |
| jackson                                     | 2.14.2        | JSON序列化               |
| quartz                                      | 2.3.2         | 定时任务管理                |
| flyway                                      | 9.5.1         | 数据库版本管理               |
| hikari                                      | 5.0.1         | 数据库连接池                |
| logback                                     | 1.4.6         | log日志工具               |
| reflections                                 | 0.10.2        | 反射工具                  |
| lombok                                      | 1.18.26       | 简化对象封装工具              |
| hutool                                      | 5.8.18        | 更适合国人的java工具集         |
| JustAuth                                    | 1.16.5        | 小而全而美的第三方登录开源组件(源码改造) |
| vue                                         | 3.*           | 前端框架                  |
| vite                                        | 4.*           | 前端构建工具                |
| axios                                       | ^1.3.4        | 前后端数据交互               |
| pinia                                       | ^2.0.33       | 前端状态数据存储              |
| tdesign-vue-next                            | ^1.2.2        | 组件库                   |
| vue-router                                  | ^4.1.6        | 路由                    |


### 中间件
| 名称       | 版本    | 说明    |
|----------|-------|-------|
| MySQL    | 8.0^  | 数据库   |
| Redis    | 6.^   | 缓存数据库 |
| Undertow | 2.3.4 | 应用服务器 |
