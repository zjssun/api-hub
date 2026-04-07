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
| 📰 澎湃新闻 | `/hotlist/paper`         | 最新要闻热点<br>Latest news highlights from The Paper      |
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
