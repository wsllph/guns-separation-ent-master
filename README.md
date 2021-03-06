<p align="center">
    <img src="https://images.gitee.com/uploads/images/2020/1009/170306_5a189fff_1564001.jpeg" width="300">
    <br>      
    <br>      
    <p align="center">
        Guns-Separation |sepəˈreɪʃn| 蕴意为分离版本。<br/>
        项目采用前后端分离架构，代码简洁，功能丰富，开箱即用，开创快速开发平台新趋势<br/>
        <br>
        <a href="https://www.antdv.com/docs/vue/introduce-cn/">
            <img src="https://img.shields.io/badge/vue--ant--design-2.1.0-blue.svg" alt="bootstrap">
        </a> 
        +
        <a href="http://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/spring--boot-2.3.1-green.svg" alt="spring-boot">
        </a>
        <a href="http://mp.baomidou.com">
            <img src="https://img.shields.io/badge/mybatis--plus-3.3.2-blue.svg" alt="mybatis-plus">
        </a>  
    </p>
</p>

-----------------------------------------------------------------------------------------------

### 部署注意事项

上线时候，前端工作流设计器的配置，注意设置这里为/api开头
![](.README_images/cf511bf8.png)

并且nginx的配置如下：
![](.README_images/25ec22b5.png)


### 在线演示
* 账号密码：superAdmin/123456，地址：https://sep.stylefeng.cn

### v1.1更新内容

1. 增加上传图片的预览功能
2. 完善数据范围分配时候的判断逻辑
3. 授权数据取消父级子级关联
4. 【前端】工作台界面使用静态数据、环境显示抽屉默认设置为全显示
5. 统一日志打印格式
6. 修复邮件发送异常的问题
7. 修复菜单遍历没有修改子应用的问题
8. 默认去掉oss，cos，短信的依赖包，减少了默认打包体积
9. 【pr合并】修改密码加密方式为bcrypt
10. 修复定位bug

### 官方公众号和微信群
<table>
    <tr>
        <td>官方公众号</td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0415/104911_9bc924a5_551203.png" width="120"/></td>
        <td>扫码邀请入群</td>
        <td><img src="https://images.gitee.com/uploads/images/2019/0419/103622_d6e9fa5d_551203.png" width="120"/></td>
    </tr>
</table>

### 快速开始

准备以下环境：
1. npm，jdk1.8，maven 3.6或以上版本。
2. 需要准备一个mysql 5.7数据库。
3. 您的IDE需要安装lombok插件。

前端运行：
1. cd _web/
2. npm install
3. npm run serve

后端运行：
1. 将数据库文件_sql/guns-separation.sql导入到数据库
2. 修改guns-main/src/main/resources/application-local.yml文件，修改为您的数据库ip，账号和密码
3. 运行guns-main/src/main/java/cn/stylefeng/guns/GunsApplication类，即可启动后端程序

### 框架优势

1. 模块化架构设计，层次清晰，业务层推荐写到单独模块，方便升级。
2. 前后端分离架构，分离开发，分离部署，前后端互不影响。
3. 前端技术采用vue + antdv + axios。
3. 后端采用spring boot + mybatis-plus + hutool等，开源可靠。
4. 基于spring security(jwt) + 用户UUID双重认证。
5. 基于AOP实现的接口粒度的鉴权，最细粒度过滤权限资源。
6. 基于hibernate validator实现的校验框架，支持自定义校验注解。
7. 提供Request-No的响应header快速定位线上异常问题。
8. 在线用户可查，可在线踢人，同账号登录可同时在线，可单独在线（通过系统参数配置）。
9. 支持前端 + 后端在线代码生成（后续开放）。
10. 支持jenkins一键部署，另自带docker maven插件，支持docker部署。
11. 文件，短信，缓存，邮件等，利用接口封装，方便拓展。
12. 文件默认使用本地文件，短信默认使用阿里云sms，缓存默认使用内存缓存。
13. 文档齐全，持续更新，视频教程将发布到Bilibili（后续开放）。

### 功能介绍

1. 主控面板。控制台页面，可进行工作台，分析页，统计等功能的展示。
2. 用户管理。对企业用户和系统管理员用户的维护，可绑定用户职务，机构，角色，数据权限等。
3. 应用管理。通过应用来控制不同维度的菜单展示。
4. 机构管理。公司组织架构维护，支持多层级结构的树形结构。
5. 职位管理。用户职务管理，职务可作为用户的一个标签，职务目前没有和权限等其他功能挂钩。
6. 菜单管理。菜单目录，菜单，和按钮的维护是权限控制的基本单位。
7. 角色管理。角色绑定菜单后，可限制相关角色的人员登录系统的功能范围。角色也可以绑定数据授权范围。
8. 字典管理。系统内各种枚举类型的维护。
9. 访问日志。用户的登录和退出日志的查看和管理。
10. 操作日志。用户的操作业务的日志的查看和管理。
11. 服务监控。服务器的运行状态，Java虚拟机信息，jvm等数据的查看。
12. 在线用户。当前系统在线用户的查看。
13. 数据监控。druid控制台功能，可查看sql的运行信息。
14. 公告管理。系统的公告的管理。
15. 文件管理。文件的上传下载查看等操作，文件可使用本地存储，阿里云oss，腾讯cos接入，支持拓展。
16. 定时任务。定时任务的维护，通过cron表达式控制任务的执行频率。
17. 系统配置。系统运行的参数的维护，参数的配置与系统运行机制息息相关。
18. 邮件发送。发送邮件功能。
19. 短信发送。短信发送功能，可使用阿里云sms，腾讯云sms，支持拓展。

### 版权声明

本项目完全开源，无任何私有封装的jar，但请您务必遵守开源协议，若有违规者将追究法律责任。

Guns采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1. 请不要删除和修改根目录下的LICENSE文件。
2. 请不要删除和修改Guns源码头部的版权声明。
3. 请保留源码和相关描述文件的项目出处，作者声明等。
4. 分发源码时候，请注明软件出处https://gitee.com/stylefeng/guns-separation
5. 在修改包名，模块名称，项目代码等时，请注明软件出处https://gitee.com/stylefeng/guns-separation

若您的项目无法满足以上几点，可申请商业授权，获取Guns商业授权许可，联系qq：`332464581`，备注购买授权。

### stylefeng技术团队荣誉作品

| 成员组成 | 负责内容 |
| :---: | :---: |
| 冯硕楠 | 后端 |
| 徐玉祥 | 后端 | 
| 俞宝山 | 前端 |

官网：https://www.stylefeng.cn/