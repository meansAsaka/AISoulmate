# AISoulmate
AI角色扮演聊天平台



## 环境搭建：

前端：Vue3 + Vite

服务端：SpringMVC + SpringBoot + SpringAI + MySQL + Redis （jdk17）



## 环境运行：（由于时间原因没有进行一个项目部署）

前端：进入AISoulmate-Front-end文件夹下，首先安装对应包npm install （如果第一次），然后直接运行npm run dev即可运行前端环境

服务端：用IDEA打开修改对应的MySQL用户名和password，Redis修改端口号以及password，然后直接运行即可



## 分工：

肖洋（产品）：产品稿设计，文档撰写

胡绍星（前端）：前端UI界面设计开发，路由逻辑实现

王喆（服务端）：服务端实现，OSS云图片存储，AI大模型对话功能，WebSocket语音功能等等



## 整体流程展示：

#### 登录、注册页面（注册页面的 "+" 部分是上传头像图片）

![login](E:\AISoulmate\AISoulmate\images\login.png)

![Register](E:\AISoulmate\AISoulmate\images\Register.png)

#### 主页面

1. 包括主题切换（亮色、暗色切换）

2. 头像展示（点击头像可以退出登录）

3. 最近AI聊天对话

4. 热门聊天角色展示，点击对应头像即可与对应AI进行对话

![HomePage](E:\AISoulmate\AISoulmate\images\HomePage.png)

#### 角色对话界面

1. 可以选择模型（tongyi、deepseek）
2. 可以正常发送聊天对话
3. 也可进行语音通话
4. 点击上方logo或者右上角关闭即可返回主界面

![ChatPage](E:\AISoulmate\AISoulmate\images\ChatPage.png)

![Chat](E:\AISoulmate\AISoulmate\images\Chat.png)

相关文档：

[‬‌‬‌﻿﻿﻿⁠﻿‌‬‬﻿‍﻿‬﻿‌‌‍⁠七牛云 - 飞书云文档](https://dcnzz7zjgwsh.feishu.cn/wiki/PPgxwHIEfix36ok1j0ycnmTanKg)

[‌‬‍⁠⁠⁠‬⁠‌‬‬‬‌‌⁠‍﻿‬‌‌‌‬‬﻿﻿Ai 角色扮演网站 · 技术交付包（swagger 草案 + Persona/ssml 模板 + 低保真原型 + Db 结构）（可参考） - 飞书云文档](https://dcnzz7zjgwsh.feishu.cn/wiki/KVuHwxQCvim6R8k41KpcDcj5nGg)
