# 🚀 API-HUB

> **一站式热门数据接口集合**  
> 🌐 Unified API Hub for Trending Data Across Platforms

---

## 📖 项目简介 | Project Introduction

**API-HUB** 是一个聚合热门榜单与资讯的 API 服务平台，目标是让开发者用最简单的方式获取互联网上的热门内容，无需爬虫，无需授权，开箱即用。

**API-HUB** is an aggregated API service for trending content across various platforms. Designed for developers who want quick and reliable access to hot data — no scraping, no tokens, just clean and ready-to-use APIs.

---

## 📚 接口速览 | API Overview

| 📌 数据源 | 🧩 接口路径                  | 💬 描述                                                |
|---------|--------------------------|------------------------------------------------------|
| 🎮 职业哥天梯 | `/getmatch/{playerName}` | 查询指定玩家的天梯数据（CS2）<br>Get CS2 ladder info by player name |
| �️ 投票 | `/vote` (POST)<br>`/vote/stats` (GET) | 为想新增的职业哥投票<br>按票数降序返回全局投票统计 |
| �📰 澎湃新闻 | `/hotlist/paper`         | 最新要闻热点<br>Latest news highlights from The Paper      |
| 💡 掘金技术热榜 | `/hotlist/juejin`        | 掘金社区热门文章<br>Hot tech posts from Juejin               |
| 🎬 豆瓣欧美剧榜 | `/hotlist/doubanea`      | 豆瓣热门欧美剧<br>Trending Western shows on Douban          |
| 🧠 36氪热门 | `/hotlist/36kr`          | 36氪商业/科技热文<br>Hot business/tech articles from 36Kr   |

### playName | pro player Name
目前支持以下职业哥的天梯/比赛数据（持续更新中）：
- `donk`
- `twistzz`
- `niko`
- `kyousuke`
- `elige`
- `ZywOo`
- `ropz`
- `m0nesy`
- `w0nderful`
- `jl`
- `im`
- `s1mple`
---

## 🛠️ 如何使用 | How to Use

1. 直接访问任意接口，例如：

```bash
GET samrol-express.shop/getmatch/donk
GET samrol-express.shop/hotlist/juejin
```
- 所有接口默认返回标准JSON | All interfaces return standard JSON by default.
- 支持跨域调用（CORS Enabled）
---

## 📝 更新日志 | Changelog

### 2026-11-11
- 敏感词过滤改为接入 sensitive-word 库：DFA 匹配（大小写/全半角/繁简体/变形字符自动归一），内置 6 万余条系统词库与外部自定义词库链式合并，词库变更经重建初始化实现无缝热切换；自定义词库固定读取外部文件，不再随包内置。
- 评论支持一级回复：留言可指定父评论，列表返回嵌套结构，回复同样受敏感词与限流约束；发表评论改为需登录（X-Auth-Token），作者身份关联账号。
- 封面代理泛化为通用缩略图代理：按 URL 透传任意 extractor 封面，域名白名单 + HTTPS 强制 + 图片类型/大小校验，按 URL 摘要磁盘缓存；缩略图接口豁免 API Key（小程序 image 标签无法携带请求头）。解析结果新增平台标识（extractor_key）。
- 新增 X/Twitter 视频 GIF 转换：下载请求可携带 gif 标记，任务完成后经 FFmpeg 转码为循环 GIF（失败降级保留原视频）；文件接口按扩展名返回 image/gif。
- 下载提交时校验当日剩余额度，并以动态 --max-filesize 约束 yt-dlp。完成后按实际文件字节计费，失败与取消不计费。游客默认每天 100 MB，普通账号默认每天 3 GB，可单设个人额度。管理员每天固定 30 GB，优先于个人额度。/api/usage 返回当日用量。
- 管理员字段由 [users 表升级脚本](doc/sql/20261111-users-is-admin.sql) 添加。新环境或升级环境先对尚无该列的用户表执行一次脚本。将指定账号的 `is_admin` 设为 1 后，下一次用量查询及下载校验即使用管理员额度。默认值为 0。
- 新增账号体系：邮箱验证码注册/登录/忘记密码（163 SMTP 发信、Redis 存验证码与限频、BCrypt 密码散列、UUID 令牌 7 天有效可注销）；注册昵称走敏感词校验；用户表预留套餐档位与个人配额覆盖字段，为后续付费扩容留扩展位；发件人显示名"管油阿特"。
- 新增服务总开关 service.closed：开启后解析与下载通道关闭（接口提示"服务器流量已用完"），用量接口返回 closed 标志供前端展示，其余接口不受影响。
- 新增 youtube.proxy 配置：HTTP 代理同时作用于封面拉取（HttpClient）与 yt-dlp 解析/下载命令，供受限网络环境使用。

### 2026-11-10
- 新增 YouTube 视频解析下载服务，基于 yt-dlp 子进程实现：视频信息解析、封面代理、下载任务管理（进度轮询/取消/文件取回）、定时清理，面向微信小程序提供后端接口，接口需 API Key 鉴权，支持 cookies 应对风控。
- 新增下载服务相关配置组，外部工具路径、cookies、并发数、文件保留时长等支持环境变量覆盖。
- 新增访客标识追踪：客户端请求携带 X-Visitor-Id 头，后端异步记录访客（唯一标识、首末次访问时间、最近 IP、访问次数）入库，为评论等用户功能做铺垫。
- 新增评论留言接口：留言发表与最新列表查询，作者身份关联访客标识，含昵称兜底、头像白名单、内容长度上限与同用户发言限流；API Key 鉴权范围扩展至全部 /api/** 接口。
- 新增评论敏感词过滤：内置约 1.3 万条词库（含简繁体变体），命中返回"评论内容不宜发布"；支持通过环境变量指定外部词库文件，文件变更后每分钟自动热加载，词库缺失时放行不阻断。

### 2026-10-19
- 移除选手 BOROS（`PlayerEnum`、前端导航与对应数据表同步删除）。
- 新增对局职业哥识别：新增 `pro-players.json` 职业选手注册表（67 人，含被追踪的 13 人）与 `ProPlayerRegistry` 组件；解析 FACEIT history 阵容时按 `player_id` 识别同阵营/对方阵营的职业哥，比赛记录新增 `teammates`、`opponents` 字段，各选手表同步加列（仅对新增数据生效）。
- 新增投票接口 `POST /vote` 与 `GET /vote/stats`，配套 `pro_votes` 表按规范化名字（小写）聚合票数，供前端统计展示。
- 数据库全部表字符集/排序规则统一为 `utf8mb4` / `utf8mb4_general_ci`。

### 2026-04-07

- 将 FACEIT 职业哥比赛数据源切换为官方 `open.faceit.com/data/v4` API。
- 新增 `faceit.api.token` / `FACEIT_API_TOKEN` 配置支持，便于本地与服务器环境管理鉴权信息。
- 重写 FACEIT 比赛历史、比赛详情、队伍均分的解析逻辑，提升线上可用性与数据准确性。
- 优化定时任务的超时、重试、错误日志与失败降级处理，方便排查服务器部署问题。
- 补充 FACEIT 解析相关测试，降低后续接口变更带来的维护成本。

---

## 📢 免责声明 | Disclaimer

- 本项目仅供学习与研究使用，所有数据来源于公开网页。
- 本项目不存储、不篡改任何原始数据。
- 若数据源变动导致接口失效，请自行适配或提交 issue。
- 若涉及版权或合法性问题，请及时联系我们处理。

> This project is for **educational and research purposes only**.  
> All data is collected from publicly accessible sources.  
> This project does **not** store, alter, or tamper with any original content.  
> If there is any copyright or legal concern, please contact us for removal.
