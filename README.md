# ZhuaTech SearchAI · 知华企业智能搜索平台

## 2026-10-23 功能更新：候选片段BM25可解释排序

对调用者已授权候选的分词结果计算BM25分数，保留命中词、稳定排序与零命中空结果，不扩张检索权限。

登录真实后端后，点击右下角“业务计算工具”，可输入结构化数据、执行计算和导出完整结果。该功能已实现后端算法，并非静态示例响应。

接口：`POST /api/workbench/candidate-bm25-ranking`（沿用当前登录 JWT）。计算仅处理本次输入，不读取其他用户记录，不写入业务单据、不自动审批、不调用外部模型。算法口径、输入示例、边界和测试方法见[本次更新说明](docs/AI_WORKFLOW_UPGRADE_20261023.md)。

[简体中文](README.md) | [English](README.en.md)

为企业内部知识、文档、业务系统和数据目录提供权限感知的语义搜索、问答与引用溯源。

由 **上海如静知华信息科技有限公司（知华科技）** 发布维护。官网：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)。

![Java 21](https://img.shields.io/badge/Java-21-365f83) ![Vue 3](https://img.shields.io/badge/Vue-3-42b883) ![MySQL 8](https://img.shields.io/badge/MySQL-8-4479A1) ![License](https://img.shields.io/badge/license-Non--Commercial-b88745)

## 搜索答案之前，先确认谁能看什么

为企业内部知识、文档、业务系统和数据目录提供权限感知的语义搜索、问答与引用溯源。

采用混合召回、权限过滤、语义重排和段落级引用的参考架构，避免“能检索但不可审计”的黑盒问答。

## 功能全景

- 管理端：企业搜索运营控制台、任务台账、计划排期、规则模板、审核决策、资源监控和运营分析。
- H5 工作台：我的任务、资料查询、智能处理、人工反馈、证据查看和问题升级。
- AI 参考能力：`EnterpriseSearchService` 提供“权限感知搜索计划”的确定性实现，可替换为企业自有模型。
- 工程能力：JWT 权限、JPA、Flyway、MySQL、演示数据、Docker Compose、响应式 Vue 3 前端。

## 页面预览

### 企业搜索运营台

![企业搜索运营台](docs/images/searchai-admin.png)

### 知识运营 H5

![知识运营 H5](docs/images/searchai-h5.png)

演示账号：管理端 `planner / Demo@2026`，H5 端 `operator / Demo@2026`。截图和演示数据均为虚构内容。

## 本地运行

```bash
cd frontend
npm install
npm run dev:demo
```

浏览器访问 `http://localhost:5173`。后端使用 Java 21、Spring Boot 与 MySQL 8，完整容器方式：

```bash
cp .env.example .env
docker compose up --build
```

Java 包名为 `cn.zhuatech.searchai`，数据库名为 `zhuatech_searchai`。API 摘要见 [docs/api.md](docs/api.md)。

## 使用许可与商业授权

本工程仅限个人学习、研究和非商业技术交流，**不得商用**。企业内部生产使用、SaaS、私有化部署、客户交付、收费培训、品牌替换或商业分发，须事先取得上海如静知华信息科技有限公司书面授权。详细条款见 [LICENSE](LICENSE)。

需要 企业智能搜索平台 私有化部署、模型接入、系统集成或深度定制，请访问[知华科技官网](https://www.zhuatech.cn/)，也可扫码咨询：

| 产品与方案咨询 | 深度开发定制 |
| --- | --- |
| ![微信咨询二维码一](docs/images/zhuatech-wechat-consulting.png) | ![微信咨询二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

SEO：企业智能搜索、AI搜索、语义检索、RAG搜索、Java企业搜索源码、知华科技、上海如静知华信息科技有限公司。

## 企业搜索质量

新增 `POST /api/searchai/insights/search-quality`，结合零结果、点击、前三点击、查询改写、陈旧结果和权限泄露计算质量分，输出 `HEALTHY`、`IMPROVE` 或 `BLOCK`。

## 企业级搜索索引发布

新增 `POST /api/enterprise/searchai/search-index-publication`，覆盖授权、ACL、隐私、新鲜度、相关性、零结果率、监控和回滚，返回 `PUBLISH / CANARY / BLOCKED`。详见 [索引发布说明](docs/ENTERPRISE_INDEX_PUBLICATION.md)。

## 搜索结果安全与引用治理

`POST /api/enterprise/searchai/result-governance` 在候选结果返回前执行租户隔离、ACL、分类审批、个人信息脱敏和相关度过滤，并用引用覆盖率与来源多样性决定 `RELEASE / REVIEW / BLOCKED`。详见[结果治理说明](docs/ENTERPRISE_RESULT_GOVERNANCE.md)。
