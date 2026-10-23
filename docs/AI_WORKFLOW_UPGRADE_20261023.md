# 候选片段BM25可解释排序

> 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
> 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。

## 实现与使用

对调用者已授权候选的分词结果计算BM25分数，保留命中词、稳定排序与零命中空结果，不扩张检索权限。

最多100个候选、每个200词，查询最多50词；调用方自行完成分词及授权过滤。匹配大小写敏感、查询重复词去重。使用正值IDF：ln(1+(N−df+0.5)/(df+0.5))，空文档不命中，零命中返回空表。按6位分数后编号稳定排序，不能将本接口当真实向量检索或ACL。

- 入口：真实账号登录后，右下角“业务计算工具”。默认沿用当前登录JWT；演示模式的demo-token不可执行。
- 接口：`POST /api/workbench/candidate-bm25-ranking`，`Content-Type: application/json`，`Authorization: Bearer <登录令牌>`。
- 返回：200含`success:true,data`；身份缺失/失效401；输入错误400含可读message。独立计算只允许调用者提交数据，不读取数据库、其他用户数据、文件或URL。
- 结果在内存中保留，刷新或退出丢失；可导出完整JSON。列表仅预览100行，导出不会截断。
- 请求限制：数组最多500项（各业务数组有更小上限），嵌套12层，10000节点，文本总量128000字符，单文本64000字符。通用数值最多30位有效数字、尺度绝对值≤12；金额最多2位小数。日期1900–2200年；本地时间精确到整分钟。
- 页面限制请求128 KiB、超时15秒。服务端字段限制在JSON解码后执行；生产反向代理仍须配置请求体上限、速率限制和部署级访问控制，不将这些校验当作网络层防护。

## 请求示例（合成数据）

```json
{
  "query": [
    "srm"
  ],
  "k1": 1.2,
  "b": 0.75,
  "topK": 5,
  "documents": [
    {
      "id": "A",
      "tokens": [
        "srm",
        "srm"
      ]
    },
    {
      "id": "B",
      "tokens": [
        "crm"
      ]
    }
  ]
}
```

## 验证

后端Java21：`cd backend && mvn test`；前端：`cd frontend && npm ci && npm run build`。

新增测试覆盖算法样例、特定边界、异常输入、零分母或缺失证据、重复执行不修改输入、过深/过长请求、过高数字精度；MockMvc覆盖匿名401、无效JWT401、有效JWT200和参数错误400。用例位于`backend/src/test/java/cn/zhuatech/searchai/workbench/`。实际结果以本批交付测试报告为准，不以文档替代测试执行。

## 能力边界 / Scope

This is an authenticated, deterministic calculation over caller-supplied data. Ranks caller-authorized tokenized candidate documents using BM25 with explicit length normalization, matched terms and deterministic ties; it is not a permission filter or tokenizer. It does not run external inference, retrieve protected records, persist workflows or prove production readiness of the whole application. Results require business review; do not use them as automatic approval, regulatory certification or payment authorization.

原有业务模块、数据模式与许可不变；没有引入数据库迁移。该功能是可使用的辅助工具，不代表整个项目所有历史演示页面已改造成生产业务系统。

## 原有业务修复

修复原有搜索计划：部门仅匹配数据源冒号前的完整命名空间，不能用fin命中finance或任意位置子串；大小写处理固定Locale.ROOT，空部门/空来源HTTP参数拒绝，规范空白查询。userDepartments仍由请求提供，因此计划绝不是服务端权限凭证；真实检索必须绑定服务端账号、部门和文档ACL，不能直接使用本规划接口的permittedSources作为授权结果。新增4个单元回归和空部门HTTP回归。
