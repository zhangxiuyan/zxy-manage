# SysUserController 接口文档

## 基础信息

| 属性 | 值 |
|------|-----|
| 基础路径 | `/manage/sys/user` |
| 描述 | 系统模块-用户管理 |
| 作者 | zxy |

---

## 接口列表

### 1. 用户注册

**路径:** `POST /manage/sys/user/register`

**请求头:**
```
Content-Type: application/json
```

**请求参数 (RegisterRequestDTO):**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| accountId | string | 是 | 账号ID |
| password | string | 是 | 密码 |
| nickname | string | 是 | 昵称 |
| email | string | 否 | 邮箱 |
| mobile | string | 否 | 手机号 |

**请求示例:**
```json
{
  "accountId": "zhangsan",
  "password": "EncryptedPassword123!",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "mobile": "13800138000"
}
```

**响应:** `HttpResult<Map<String, Long>>`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "userId": 1709368800000
  }
}
```

---

### 2. 新增用户

**路径:** `POST /manage/sys/user/create`

**请求头:**
```
Content-Type: application/json
```

**请求参数 (SysUser):**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 否 | 用户ID (自增) |
| account | string | 是 | 账号 |
| password | string | 是 | 密码 |
| nickname | string | 是 | 昵称 |
| avatar | string | 否 | 头像URL |
| email | string | 否 | 邮箱 |
| mobile | string | 否 | 手机号 |
| status | integer | 否 | 状态: 0-正常, 1-禁用 |
| deleteFlag | integer | 否 | 删除标志: 0-未删, 1-已删 |

**请求示例:**
```json
{
  "account": "lisi",
  "password": "$2a$10$xJx...",
  "nickname": "李四",
  "avatar": "https://example.com/avatar/lisi.png",
  "email": "lisi@example.com",
  "mobile": "13900139000",
  "status": 0,
  "deleteFlag": 0
}
```

**响应:** `HttpResult<String>`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": "success"
}
```

---

### 3. 获取当前用户信息

**路径:** `GET /manage/sys/user/info`

**请求头:**
```
Authorization: Bearer <token>
```

**响应:** `HttpResult<UserResponseVO>`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | long | 用户ID |
| accountId | string | 账号ID |
| nickname | string | 昵称 |
| avatar | string | 头像URL |
| email | string | 邮箱 |
| mobile | string | 手机号 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1709368800000,
    "accountId": "zhangsan",
    "nickname": "张三",
    "avatar": "https://example.com/avatar/zhangsan.png",
    "email": null,
    "mobile": null
  }
}
```

---

### 4. 更新用户信息

**路径:** `PUT /manage/sys/user/update`

**请求头:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**请求参数 (UserUpdateRequestDTO):**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | string | 否 | 昵称 |
| avatar | string | 否 | 头像URL |
| email | string | 否 | 邮箱 |
| mobile | string | 否 | 手机号 |

**请求示例:**
```json
{
  "nickname": "张三改名",
  "avatar": "https://example.com/avatar/zhangsan_new.png",
  "email": "zhangsan_new@example.com"
}
```

**响应:** `HttpResult<Void>`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

## 通用响应码

| 响应码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 禁止访问 |
| 500 | 服务器内部错误 |