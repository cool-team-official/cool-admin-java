
<p align="center">
  <a href="https://midwayjs.org/" target="blank"><img src="https://cool-show.oss-cn-shanghai.aliyuncs.com/admin/logo.png" width="200" alt="Midway Logo" /></a>
</p>
<p align="center">cool-admin(java版)后台权限管理系统，开源免费，Ai编码、流程编排、模块化、插件化，用于快速构建后台应用程序，详情可到<a href="https://cool-admin.com" target="_blank">官网</a> 进一步了解。
<p align="center">
    <a href="https://github.com/cool-team-official/cool-admin-midway/blob/master/LICENSE" target="_blank"><img src="https://img.shields.io/badge/license-MIT-green?style=flat-square" alt="GitHub license" />
    <a href=""><img src="https://img.shields.io/github/package-json/v/cool-team-official/cool-admin-midway?style=flat-square" alt="GitHub tag"></a>
    <img src="https://img.shields.io/github/last-commit/cool-team-official/cool-admin-midway?style=flat-square" alt="GitHub tag"></a>
</p>

## 技术栈

- 后端：**`Springboot3` `Mybatis-Flex`**
- 前端：**`Vue3` `Vite` `Element-Ui` `Typescript`**
- 数据库：**`Mysql` `Postgresql` `Sqlite(适配中)` `...`**

## 特性

Ai时代，很多老旧的框架已经无法满足现代化的开发需求，Cool-Admin开发了一系列的功能，让开发变得更简单、更快速、更高效。

- **Ai编码**：通过微调大模型学习框架特有写法，实现简单功能从Api接口到前端页面的一键生成[详情](https://java.cool-admin.com/src/guide/ai.html)
- **流程编排**：通过拖拽编排方式，即可实现类似像智能客服这样的功能[详情](https://cool-js.com/plugin/118)
- **多租户**：支持多租户，采用全局动态注入查询条件[详情](https://java.cool-admin.com/src/guide/tenant.html)
- **多语言**：基于大模型自动翻译，无需更改原有代码[详情](https://java.cool-admin.com/src/guide/i18n.html)
- **模块化**：代码是模块化的，清晰明了，方便维护
- **插件化**：插件化的设计，可以通过安装插件的方式扩展如：支付、短信、邮件等功能
- **自动初始化**：数据自动化，无需再手动维护，启动时自动生成数据库表和表结构数据
- **cool-admin-java-plus**：  [详情](https://gitee.com/hlc4417/cool-admin-java-plus)
- ......
![](https://cool-show.oss-cn-shanghai.aliyuncs.com/admin/flow.png)

## 地址

- 官网：[https://cool-admin.com](https://cool-admin.com)
- 文档：[https://java.cool-admin.com](https://java.cool-admin.com)
- 商城项目：[https://cool-js.com/plugin/140](https://cool-js.com/plugin/140)
- Ai流程编排+知识库项目：[https://cool-js.com/plugin/118](https://cool-js.com/plugin/118)
- cool-admin-java-plus：https://gitee.com/hlc4417/cool-admin-java-plus

## 演示

[https://show.cool-admin.com](https://show.cool-admin.com)

- 账户：admin
- 密码：123456

![](https://cool-show.oss-cn-shanghai.aliyuncs.com/admin/home-mini.png)

#### 项目前端

系统是前后端分离的，启动完成后，还需要启动前端项目，前端项目地址：

[https://github.com/cool-team-official/cool-admin-vue](https://github.com/cool-team-official/cool-admin-vue)

或

[https://gitee.com/cool-team-official/cool-admin-vue](https://gitee.com/cool-team-official/cool-admin-vue)

或

[https://gitcode.com/cool_team/cool-admin-vue](https://gitcode.com/cool_team/cool-admin-vue)

## 微信群

<img width="260" src="https://cool-show.oss-cn-shanghai.aliyuncs.com/admin/wechat.jpeg?v=1" alt="Admin Wechat"></a>

## 运行

### 环境要求

- Java Graalvm 17+
- Maven 3.6+

### 配置

修改数据库配置，配置文件位于`src/resources/application-local.yml`

以 Mysql 为例，其他数据库适配中...

Mysql(`>=5.7版本`)，建议 8.0，首次启动会自动初始化并导入数据

```yaml
# mysql，驱动已经内置，无需安装
spring:
    datasource:
        url: jdbc:mysql://127.0.0.1:3306/cool?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2b8
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
```

### 启动

注：项目使用到了[Mybatis-Flex 的Apt功能](https://mybatis-flex.com/zh/others/apt.html)，如果启动报错，请先执行`mvn compile`编译

1、启动文件：`src/main/java/com/cool/CoolApplication.java`

2、启动完成后，访问：[http://localhost:8001](http://localhost:8001)

3、如果看到以下界面，说明启动成功。这时候再启动前端项目即可，数据库会自动初始化，默认账号：admin，密码：123456

![](https://cool-show.oss-cn-shanghai.aliyuncs.com/admin/run.png)
