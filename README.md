# FloatingRun
### 悬浮窗执行 shell 指令

**一个悬浮窗执行 shell 指令的小程序**

这是把原项目从 eclipse 迁移到 Android Studio 的新分支。

支持 SDK 19（Android 4.4） - SDK 27 （Android 8.1）


### 使用场景:
玩某游戏的时候,经常需要重启游戏,使用 am 指令快速进行重启应用...

命令：
`am start -S com.huanmeng.zhanjian2/org.cocos2dx.cpp.AppActivity`

需要 root

### 一些问题:
1. 开启移动模式后,在真机低分辨率/小屏幕设备有时候会把按下操作判定为移动；
	解决方法：
		移动到固定位置后，关闭移动模式；
		将移动模式改为长按触发（待完成）。

2. 执行命令的返回结果没有换行分割；
	解决方法：
		没想到。

3. 只能执行立即返回结果的指令，否则会卡死；
	解决方法：
		换用其它指令，如ping -c 1 github.com
		这是特性！

### 参考项目:

悬浮窗：

[android 悬浮窗口的实现](http://blog.csdn.net/stevenhu_223/article/details/8504058)

[Android 8.0 悬浮窗变动与用法](https://blog.csdn.net/mai763727999/article/details/78983375)

shell 命令执行：[android-common](https://github.com/Trinea/android-common)

图标：[Bison 仓鼠](https://weibo.com/bisonbison) 的表情包
