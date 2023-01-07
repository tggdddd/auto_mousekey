# 自动鼠标键盘

#### 介绍
减少重复机械的鼠标键盘操作，释放双手

#### 软件架构
软件架构说明
利用jnativehook完成键盘鼠标的记录，与回放。

awt包下类的可视化界面

#### 使用说明

1.  直接使用
2.  热键关闭也会被记录到，回放也会结束重复

可替代方案：
    - 这里是列表文本这里是列表文本用无关热键结束回放，然后更改结束热键
    - 这里是列表文本打开记录文件，删除最后几行（可能中间还有鼠标移动记录）
3.  热键修改在重新启动应用后会恢复默认

可替代方案：
    - 这里是列表文本修改源码的默认键
    - 这里是列表文本增加配置读取类
4.  使用卡顿
    乱糟糟的代码
5.  容易出現的bug： 在结束录制后没有将按键松开的操作记录下来导致使用异常

可替代方案：
    - 按一下那个按键将press释放（最常见 ctrl键），播放没有运行的话，第二次播放就行
6.  重复回放需要按回车才确定，0为无限。   输入不了参考5（按下Ctrl）