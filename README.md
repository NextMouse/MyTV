# MyTV

## Media Play Manager For m3u

- 一个快速制作m3u播放列表的工具
- 支持VLC播放器 (https://www.videolan.org/vlc/)
- 未安装VLC，则采用JavaFx MediaPlayer 播放器

![image](https://github.com/NextMouse/MyTV/assets/11313291/f13226a1-8243-4e6e-b38f-07a933280537)

### 使用方法
- 若想使用Java 8版本，请自行编译
1. 本地支持Java 17
2. 下载Release版本
3. 运行run.bat
```bat
REM 这是一个bat运行jar模板，将X.X.X替换为自己需要运行的版本号
@echo off
chcp 65001
start javaw -noverify -jar -Dfile.encoding=utf8 ./MyTV-X.X.X-jar-with-dependencies.jar
exit
```

## 功能点
### 1. 打开本地m3u文件
![image](https://github.com/NextMouse/MyTV/assets/11313291/5028da50-69d0-4014-8bbb-289cd1dcaa8e)


### 2. 导出制作的m3u播放列表
- 导出m3u格式的列表
![image](https://github.com/NextMouse/MyTV/assets/11313291/d850356b-bae4-496b-9685-511e846a16fa)

### 3. 清空列表
- 清空当前的播放列表

### 4. 对播放列表进行去重
- 去重逻辑：组名、标题、播放地址都相同的情况下认为是同一个节目

### 5. 搜索
- 支持搜索 IPTV Link Search http://tonkiang.us/ 中的节目
![image](https://github.com/NextMouse/MyTV/assets/11313291/0d0648db-912d-4ed4-8cf7-aaf7ed480602)

- 支持网络m3u文件、本地m3u文件节目标题匹配搜索
![image](https://github.com/NextMouse/MyTV/assets/11313291/68cad367-e58d-4119-bbc1-32802d192a13)
![image](https://github.com/NextMouse/MyTV/assets/11313291/1e058117-41ae-4488-b980-73678275050a)

### 6. 复制、粘贴
- 多开运行后，两个进程间可以通过复制粘贴互相传递节目

### 7. 修改节目信息
![image](https://github.com/NextMouse/MyTV/assets/11313291/a51deb55-7f4d-4bff-bf98-90c482a0324e)

### 8. 标记资源可用
- 标记后会在当前运行目录下生成my-tv.db文件，存储当前节目地址信息
- 加载视频列表后自动匹配my-tv.db数据库中的节目，匹配成功标记为“红心”，表示可用

### 9. 配置代理，0.1.1版本以下不支持视频代理
- TV logo 会从代理下载
- 网络m3u文件会从代理下载
