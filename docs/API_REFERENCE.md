# API 参考文档

本文档记录了高德地图中已知的 MTOP API 及其用途。

## 已知的推广相关 API

### 1. 推荐流 API

**API 名称：** `mtop.relationrecommend.mtoprecommend.recommendstream/1.0`

**用途：** 获取推荐内容流

**请求参数：**
```json
{
  "scene": "首页",
  "pageSize": 20,
  "page": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "type": "recommend",
        "title": "推荐内容",
        "url": "..."
      }
    ]
  }
}
```

**拦截建议：** 可以拦截，返回空列表

---

### 2. 小程序推荐 API

**API 名称：** `mtop.relationrecommend.mtoprecommend.listRecommendMiniApps`

**用途：** 获取推荐的小程序列表

**请求参数：**
```json
{
  "scene": "首页",
  "pageSize": 10
}
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "miniApps": [
      {
        "id": "xxx",
        "name": "小程序名称",
        "icon": "..."
      }
    ]
  }
}
```

**拦截建议：** 可以拦截，返回空列表

---

### 3. 金融服务 API

**API 名称：** `mtop.autonavi.mp.finance.*`

**用途：** 金融服务相关功能

**拦截建议：** 可以拦截，返回空响应

---

### 4. 钱包服务 API

**API 名称：** `mtop.autonavi.mp.wallet.*`

**用途：** 钱包服务相关功能

**拦截建议：** 可以拦截，返回空响应

---

### 5. 车主服务 API

**API 名称：** `mtop.autonavi.mp.carservice.*`

**用途：** 车主服务相关功能

**拦截建议：** 可以拦截，返回空响应

---

## 已知的正常 API

### 1. 地图数据 API

**API 名称：** `mtop.autonavi.map.*`

**用途：** 地图瓦片、路线规划等核心功能

**拦截建议：** 不要拦截

---

### 2. 搜索 API

**API 名称：** `mtop.autonavi.search.*`

**用途：** 地点搜索、路线搜索等

**拦截建议：** 不要拦截

---

### 3. 定位 API

**API 名称：** `mtop.autonavi.location.*`

**用途：** 定位服务

**拦截建议：** 不要拦截

---

### 4. 实时路况 API

**API 名称：** `mtop.autonavi.traffic.*`

**用途：** 实时路况信息

**拦截建议：** 不要拦截

---

## API 拦截策略

### 策略一：精确匹配

对于已知的推广 API，使用精确匹配：

```java
private static final Set<String> BLOCKED_APIS = new HashSet<>(Arrays.asList(
    "mtop.relationrecommend.mtoprecommend.listRecommendMiniApps",
    "mtop.autonavi.mp.finance.*",
    "mtop.autonavi.mp.wallet.*",
    "mtop.autonavi.mp.carservice.*"
));
```

### 策略二：关键词匹配

对于包含特定关键词的 API，使用模糊匹配：

```java
private static final String[] BLOCK_KEYWORDS = {
    "recommend",
    "promotion",
    "advert",
    "finance",
    "wallet",
    "carservice"
};
```

### 策略三：白名单模式

只允许特定的 API 通过，其他全部拦截：

```java
private static final Set<String> ALLOWED_APIS = new HashSet<>(Arrays.asList(
    "mtop.autonavi.map.*",
    "mtop.autonavi.search.*",
    "mtop.autonavi.location.*",
    "mtop.autonavi.traffic.*"
));
```

## 日志分析

### 日志格式

```
[HH:mm:ss.SSS] MTOP API: api_name
  Version: version
  Request: request_data
  Response: response_data
---
```

### 分析步骤

1. **收集日志**
   ```bash
   adb shell "cat /data/data/com.autonavi.minimap/amap_blocker_logs/mtop_log_$(date +%Y-%m-%d).txt"
   ```

2. **筛选推广 API**
   ```bash
   grep -i "recommend\|promotion\|advert" mtop_log.txt
   ```

3. **统计 API 调用**
   ```bash
   grep "MTOP API:" mtop_log.txt | awk '{print $3}' | sort | uniq -c | sort -rn
   ```

4. **查看拦截记录**
   ```bash
   grep "BLOCKED" mtop_log.txt
   ```

## 添加新的拦截 API

### 步骤

1. **运行模块，收集日志**
   - 打开高德地图
   - 执行各种操作
   - 拉取日志文件

2. **分析日志，找到推广 API**
   - 查找包含 `recommend`、`promotion`、`advert` 等关键词的 API
   - 确认这些 API 的用途

3. **添加到拦截列表**
   - 精确匹配：添加到 `BLOCKED_APIS`
   - 模糊匹配：添加到 `BLOCK_KEYWORDS`

4. **重新编译并测试**
   - 编译项目
   - 安装到设备
   - 测试拦截效果

### 示例

假设发现新的推广 API：`mtop.autonavi.mp.ad.getRecommend`

1. 添加到精确匹配列表：
   ```java
   private static final Set<String> BLOCKED_APIS = new HashSet<>(Arrays.asList(
       "mtop.relationrecommend.mtoprecommend.listRecommendMiniApps",
       "mtop.autonavi.mp.ad.getRecommend"
   ));
   ```

2. 或者添加到关键词匹配列表：
   ```java
   private static final String[] BLOCK_KEYWORDS = {
       "recommend",
       "promotion",
       "advert",
       "ad.get"
   };
   ```

## 注意事项

1. **不要拦截核心功能 API**
   - 地图数据、搜索、定位、路况等 API 不要拦截
   - 否则会影响高德地图的正常使用

2. **谨慎使用关键词匹配**
   - 关键词匹配可能会误伤正常 API
   - 建议先用精确匹配，确认后再用关键词匹配

3. **定期更新拦截列表**
   - 高德地图会更新 API
   - 定期检查日志，更新拦截列表

4. **测试拦截效果**
   - 添加新的拦截 API 后，要测试是否影响正常功能
   - 如果影响正常功能，及时调整

## 参考资源

- [MTOP SDK 文档](https://github.com/nicklockwood/MTOP)
- [高德地图开放平台](https://lbs.amap.com/)
- [LSPosed 官方文档](https://lsposed.org/)
