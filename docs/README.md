<h1 style="display: flex; align-items: center;">
 <img style="margin-right: 8px;" src="./assert/logo.svg" width="32px" height="32px"/> orion-ops-pro 是什么
</h1>

`orion-ops-pro` 一款开箱即用的运维平台, 提供了资产管理、资产授权、Web终端、WebSftp、角色管理、系统管理等功能。为运维团队提供轻量化的运维治理平台。它是根据 `orion-ops`
的产品思路完全重构的一套系统, 重新设计了架构并优化交互逻辑, 操作更快捷友好。

<p style="text-align: left">
    <a target="_blank" style="text-decoration: none" href="https://app.codacy.com/gh/lijiahangmax/orion-ops-pro/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade">
        <img src="https://app.codacy.com/project/badge/Grade/49eaab3a9a474af3b87e1d21ffec71c4" alt="quality"/>
    </a>
	<a target="_blank" style="text-decoration: none" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" alt="jdk8"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://www.apache.org/licenses/LICENSE-2.0">
		<img src="https://img.shields.io/github/license/lijiahangmax/orion-ops-pro" alt="License"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://gitee.com/lijiahangmax/orion-ops-pro/stargazers">
		<img src="https://gitee.com/lijiahangmax/orion-ops-pro/badge/star.svg?theme=dark" alt="star"/>
	</a>
	<a target="_blank" style="text-decoration: none" href="https://gitee.com/lijiahangmax/orion-ops-pro/members">
		<img src="https://gitee.com/lijiahangmax/orion-ops-pro/badge/fork.svg?theme=dark" alt="fork"/>
	</a>		
	<!-- <a target="_blank" style="text-decoration: none" href="https://github.com/lijiahangmax/orion-ops-pro">
		<img src="https://img.shields.io/github/stars/lijiahangmax/orion-ops-pro.svg?style=social" alt="star"/>
	</a> -->	
</p>

<br/>  

当前版本: **1.0.0-beta.1**  
github: https://github.com/lijiahangmax/orion-ops-pro  
gitee: https://gitee.com/lijiahangmax/orion-ops-pro  
文档: https://lijiahangmax.gitee.io/orion-ops-pro/#/    
demo: http://101.43.254.243:1081/#/

演示账号: `admin`    
演示密码: `admin`  
留个小星星再走吧⭐

## 特性

* 易用便捷: 极简配置, 开箱即用, 并兼容 Docker 部署方式。
* 资产管理: 支持灵活配置主机分组, 统一管理主机、秘钥和身份。
* 资产授权: 可将资产数据授权给指定角色和用户。
* 权限控制: 全面管理用户角色, 支持动态菜单配置和强制下线等功能。
* 在线终端: 提供便捷的在线 Web 终端服务, 支持自定义快捷键和主题风格。
* 文件管理: 实现远程主机文件的批量上传、下载和在线编辑等操作。
* 可扩展性: 前后端代码规范统一, 代码质量高、健壮且易于阅读和扩展。

[comment]: <> (  FIXME * 批量操作: 支持远程主机批量执行命令 以及 批量执行上传文件)

[comment]: <> (  FIXME * 调度任务: 维护 cron 表达式, 定时执行主机命令)

[comment]: <> (  FIXME * 功能强大: 命令批量执行, 任务定时调度, 远程日志查看, 操作日志全记录等)

## 快速开始

docker安装: https://lijiahangmax.gitee.io/orion-ops-pro/#/quickstart/docker-install   
安装文档: https://lijiahangmax.gitee.io/orion-ops-pro/#/quickstart/install   
开发文档: https://lijiahangmax.gitee.io/orion-ops-pro/#/advance/dev   
操作手册: https://lijiahangmax.gitee.io/orion-ops-pro/#/operator/asset  
常见问题: https://lijiahangmax.gitee.io/orion-ops-pro/#/quickstart/faq  
roadmap: https://lijiahangmax.gitee.io/orion-ops-pro/#/about/roadmap

## 技术栈

* Docker
* SpringBoot
* Mysql
* Redis
* Vue3
* Arco Design

[//]: # ( fixme 免责声明 &#40;注意事项&#41; &#40;在使用平台之前, 请确保您已经了解并同意相关的使用协议和隐私政策。&#41; &#40;为了数据安全, 请定期备份数据和配置, 以防意外丢失或损坏。&#41;)

## 功能预览

> 工作台

![工作台](./assert/img/workplace.png "工作台")

> 资产管理

![主机列表](./assert/img/asset_host_list.png "主机列表")
![资产授权](./assert/img/asset_grant.png "资产授权")

> 主机终端

![主机终端](./assert/img/terminal_ssh.png "主机终端")
![命令片段](./assert/img/terminal_snippet.png "命令片段")
![主题设置](./assert/img/terminal_theme.png "主题设置")
![终端设置](./assert/img/terminal_setting.png "终端设置")
![sftp](./assert/img/terminal_sftp.png "sftp")
![传输列表](./assert/img/terminal_transfer.png "传输列表")

> 用户管理

![用户列表](./assert/img/user_list.png "用户列表")  
![个人中心](./assert/img/user_info.png "个人中心")
![操作日志](./assert/img/user_operator_log.png "操作日志")

> 系统管理

![系统菜单](./assert/img/system_menu.png "系统菜单")  
![分配菜单](./assert/img/user_grant_menu.png "分配菜单")

## 联系我

<div style="display: flex;">
  <img src="./assert/img/qq_group1.jpg" alt="qq" width="268px" height="398px"/>  
  <img src="./assert/img/wx.jpg" alt="wx" width="298px" height="398px"/>  
</div>

📧 微信添加备注: ops

## 支持一下

<img src="./assert/img/support_pay.jpg" alt="收款码" width="540px"/>  

🎁 为了项目能健康持续的发展, 我期望获得相应的资金支持, 你们的支持是我不断更新前进的动力!

## License

本项目遵循 [Apache-2.0](https://github.com/lijiahangmax/orion-ops-pro/blob/main/LICENSE) 开源许可证。  