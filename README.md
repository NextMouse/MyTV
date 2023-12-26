# MyTV

## Media Play Manager For m3u

- 一个快速制作m3u播放列表的工具
- 支持VLC播放器 (https://www.videolan.org/vlc/)
- 未安装VLC，则采用JavaFx MediaPlayer 播放器

![image](https://github.com/NextMouse/MyTV/assets/11313291/f13226a1-8243-4e6e-b38f-07a933280537)

### 使用方法
1. 本地支持Java 17
2. 下载Release版本
3. 运行run.bat
- 若想使用Java 8版本，请自行编译

## 功能点
### 1. 打开本地m3u文件

### 2. 导出制作的m3u播放列表
- 导出m3u格式的列表

### 3. 清空列表
- 清空当前的播放列表

### 4. 对播放列表进行去重
- 去重逻辑：组名、标题、播放地址都相同的情况下认为是同一个节目

### 5. 搜索
- 支持搜索 IPTV Link Search http://tonkiang.us/ 中的节目

- 支持网络m3u文件、本地m3u文件节目标题匹配搜索

### 6. 复制、粘贴

### 7. 修改节目信息

### 8. 配置代理，0.1.1版本以下不支持视频代理
- TV logo 会从代理下载
- 网络m3u文件会从代理下载
