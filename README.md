# 🚀 API-HUB

> **一站式热门数据接口集合**  
> 🌐 Unified API Hub for Trending Data Across Platforms

---

## 📖 项目简介 | Project Introduction

**API-HUB** 是一个聚合热门榜单与资讯的 API 服务平台，目标是让开发者用最简单的方式获取互联网上的热门内容，无需爬虫，无需授权，开箱即用。

**API-HUB** is an aggregated API service for trending content across various platforms. Designed for developers who want quick and reliable access to hot data — no scraping, no tokens, just clean and ready-to-use APIs.

---
## 🧰 技术栈 | Tech Stack

### 🚀 后端框架 | Backend Framework
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Servlet](https://img.shields.io/badge/Servlet-Java%20EE-4479A1?style=flat&logo=java&logoColor=white)

### 💾 数据持久层 | Data Access
![MyBatis](https://img.shields.io/badge/MyBatis-0C6BA0?style=flat&logo=data&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

### 🌐 通用特性 | General Features
![RESTful API](https://img.shields.io/badge/REST%20API-Implemented-005571?style=flat&logo=api&logoColor=white)
![JSON](https://img.shields.io/badge/JSON-Data-FFCA28?style=flat&logo=json&logoColor=black)
![CORS Enabled](https://img.shields.io/badge/CORS-enabled-brightgreen)

---

## 📚 接口速览 | API Overview

| 📌 数据源 | 🧩 接口路径 | 💬 描述                                                  |
|---------|-------------|--------------------------------------------------------|
| 🎮 职业哥天梯 | `/getMatch/{playerName}` | 查询指定玩家的天梯数据（CS2）<br>Get CS2 ladder info by player name |
| 📰 澎湃新闻 | `/hotList/paper` | 最新要闻热点<br>Latest news highlights from The Paper        |
| 💡 掘金技术热榜 | `/hotList/juejin` | 掘金技术社区热门文章<br>Hot tech posts from Juejin               |
| 🎬 豆瓣欧美剧榜 | `/hotList/doubanEA` | 豆瓣热门欧美剧<br>Trending Western shows on Douban            |
| 🧠 36氪热门 | `/hotList/36kr` | 36氪商业科技热文<br>Hot business/tech articles from 36Kr      |

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
GET samrol-express.shop/getMatch/donk
GET samrol-express.shop/hotList/juejin
```
- 所有接口默认返回标准JSON | All interfaces return standard JSON by default.
- 支持跨域调用（CORS Enabled）
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
