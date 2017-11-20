# 微信公众号-电小科Bot(微信服务器)

![](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)![](../resources/logo.png)



## 功能

1. 作为微信公众号的后台服务器，对用户数据进行处理。
2. 与[LUIS](https://www.luis.ai/home)进行交互，将用户数据提交给LUIS进行语义识别，并对识别结果进行处理。
3. 根据LUIS语义识别的结果，在数据库查询，并将结果返回给用户。



## 运行方式

1. 使用maven运行demo程序: `mvn jetty:run`
2. 打war包发布到tomcat运行

## 技术分析

工程整体结构图如下所示：

![](../resources/wechatserver.png)



- LUIS:自然语言处理模型
  + ​

## 目前进展

## 当前不足

- 当前只支持微信文字查询，不支持图片请求以及

## 下一步方向

- 完善LUIS，增加更多的语义识别，实现更多查询功能
- 优化对数据库的访问，减少访问时间，加快相应速度。