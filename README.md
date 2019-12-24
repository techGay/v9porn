
9*Porn Android 客户端，突破游客每天观看10次视频的限制，还可以下载视频

### 目前具备功能
1. 无限制观看（绝大部分用户）
2. 登录收藏功能
3. 下载视频
4. 额外的图片板块以及其他视频板块
5. 浏览论坛帖子功能

### 技术概览
rxjava + retrofit + rxcache + mvp + jsoup + dagger2

时间能力有限，欢迎各位提交PR

### 更新日志：

-----2019年12月24日 v1.1.4

最近很忙，403问题早就解决了，就是没时间完善代码，抽空写写算是完成了；如果你没有使用官方的用久地址，那么理论上不会（使用代理地址）触发google机器人验证，
当前程序会自动检查403后跳转验证界面，请根据提示完成验证操作即可（可能需要多试几次才能成功），如果不能自动跳转，你也可用进入更多设置手动打开验证界面进行验证

1. 修复了使用官方用久地址403问题。
2. 最低版本要求5.0以上，不在支持5.0以下系统（有需要可自行下载源码调整编译）。


-----2019年12月01日 v1.1.3

最近心情比较不甚很好，也比较忙，抽空只修复了两个问题（当前403问题没有好办法，大家等等几天看看情况吧）

1. 修复无法解析视频地址问题。
2. 修复登录问题。

-----2019年04月26日 v1.1.2

1. 修复因升级targetSdkVersion28导致的下载以及浏览论坛崩溃问题
2. 现在不登录也能无限制观看作者其他的视频
3. 其他改动调整及bug修复


**注意：无法自动更新或者9.0以上系统需要手动下载apk升级**

-----2019年04月25日 v1.1.1

1. 修复p*gav无法解析以及视频无法播放问题
2. 优化论坛图片显示问题
3. 其他改动调整及bug修复


-----2019年03月22日 v1.1.0

感谢[Cabchinoe](https://github.com/Cabchinoe)提供的最新9*Porn视频地址解析方法，我个人尝试了另一种方式，即使用一个不可见的WebView加载md5.js，然后调用js解密参数也能实现地址解析
但是效率相对没有直接代码解析快，可作为备用的方式。

1. 修复9*porn视频无法播放问题 [Cabchinoe](https://github.com/Cabchinoe)
2. 修复妹子图列表无法加载问题
3. 优化论坛帖子内容图片显示问题
4. 去掉一些依赖，升级了部分库版本，最低api支持为19，即4.4以上
6. 其他改动调整及bug修复


**注意：v1.0.1以下版本需要手动下载apk并卸载重新安装，否则程序崩溃且下版本无法自动更新（之前项目老版本需卸载重装）**
[更多更新日志...](https://github.com/techGay/v9porn/blob/master/UPGRADE_LOG.md)



### 安装包APK下载

程序带自动检查更新升级功能，如若不能自动更新，在[APK](https://github.com/techGay/v9porn/tree/master/apk)目录或者[released](https://github.com/techGay/v9porn/releases)界面手动下载升级即可

### 自行编译--编译环境

请用最新版AndroidStudio（至少3.0以上），个人本地基本都是有更新就更新

当前版本：

Android Studio 3.3.2

Build #AI-182.5107.16.33.5314842, built on February 16, 2019

JRE: 1.8.0_152-release-1248-b01 amd64

JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o

Windows 10 10.0

### 其他
觉得项目不错，给个start或赞赏，请我喝杯咖啡：

 微信 | 支付宝 
 ------------- | -------------
 ![赞赏](https://github.com/techGay/v9porn/blob/master/img/mm_reward_qrcode_1547141812376.png) | ![赞赏](https://github.com/techGay/v9porn/blob/master/img/alipay1547141972480.jpg) 

### 赞赏&打赏列表

[....列表](https://github.com/techGay/v9porn/blob/master/REWARD.md)

### 声明
本项目仅做技术交流使用，任何人或组织无论以何种形式将其用在其他任何地方由此引发的各种问题均与本人无关