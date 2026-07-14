# 后端开发规范

本文档约定 `backend/` 的公共契约。业务模块应复用现有公共类，不要自行定义另一套响应体、JWT 解析器或文件存储逻辑。

## 1. 本地启动

环境要求：JDK 17+、Maven 3.9+、PostgreSQL 14+。

```powershell
createdb -U postgres campus_trade
psql -U postgres -d campus_trade -f backend/src/main/resources/db/schema.sql
psql -U postgres -d campus_trade -f backend/src/main/resources/db/data.sql
cd backend
.\mvnw.cmd spring-boot:run
```

配置优先从环境变量读取：

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/campus_trade` | 数据库连接 |
| `DB_USERNAME` | `postgres` | 数据库用户 |
| `DB_PASSWORD` | `postgres` | 数据库密码 |
| `JWT_SECRET` | 仅供本地开发的固定值 | 生产环境必须替换，至少 32 字节 |
| `JWT_EXPIRATION` | `86400000` | Token 有效期，毫秒 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | 前端来源，多个值以逗号分隔 |
| `UPLOAD_DIR` | `./uploads` | 图片存储根目录 |

## 2. 分层与命名

- Controller 只做参数接收、当前用户提取和响应封装，不写 SQL。
- Service 承担业务校验与事务边界；实现类命名为 `XxxServiceImpl`。
- Mapper 继承 MyBatis-Plus `BaseMapper<Entity>`，复杂 SQL 放 XML。
- DTO 用于请求参数，VO 用于响应；不要直接把密码等敏感实体字段返回前端。
- 数据库字段使用 `snake_case`，Java 字段使用 `camelCase`。

PostgreSQL 中 `user` 和 `order` 是关键字。任务书要求保留这两个表名，实体必须显式映射：

```java
@TableName("\"user\"")
public class User { }

@TableName("\"order\"")
public class Order { }
```

需要自动填充或逻辑删除的实体字段按以下方式声明：

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;

@TableLogic
private Integer deleted;
```

分页查询的 `pageSize` 不得超过 100，超出时由业务层直接拒绝或截断。

## 3. 统一响应与异常

所有 JSON API 返回 `Result<T>`：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {}
}
```

Controller 正常返回：

```java
return Result.success(productService.getDetail(id));
```

业务校验失败抛 `BusinessException`，不要在 Controller 中 `try/catch`：

```java
throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
```

`GlobalExceptionHandler` 会让 HTTP 状态与错误语义保持一致：未登录 401、无权限 403、不存在 404、参数错误 400、未知异常 500。未知异常只向客户端返回通用提示，详细堆栈写入服务端日志。

## 4. JWT 与权限

登录成功后由用户模块调用：

```java
String token = jwtUtils.generateToken(user.getId(), user.getRole());
```

前端在后续请求中携带：

```text
Authorization: Bearer <token>
```

Token 载荷契约：

```json
{
  "sub": "1",
  "userId": 1,
  "role": "USER"
}
```

`JwtAuthenticationFilter` 会把用户 ID 写入 `Authentication.getName()`，Controller 可通过 `Authentication` 获取当前用户：

```java
Long userId = Long.valueOf(authentication.getName());
```

`/api/auth/login`、`/api/auth/register`、API 文档和图片 GET 请求匿名可访问，其余接口默认需要登录。管理员接口同时添加 `@AdminOnly`：

```java
@AdminOnly
@PostMapping("/api/admin/products/{id}/approve")
public Result<Void> approve(@PathVariable Long id) { ... }
```

## 5. 图片上传

请求：`POST /api/file/upload`，表单字段名为 `file`，需要 JWT。

响应示例：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "url": "/uploads/202607/1/550e8400-e29b-41d4-a716-446655440000.png"
  }
}
```

支持 jpg/jpeg/png/webp，单张不超过 5MB。服务端同时检查扩展名和 magic bytes，最终文件名由 UUID 生成。返回的 `/uploads/**` 地址可直接访问；兼容访问接口为 `/api/file/{yyyyMM}/{userId}/{filename}`。

## 6. 提交前检查

```powershell
cd backend
.\mvnw.cmd clean test
```

至少确认：编译通过、上下文可启动、未登录请求返回 401、携带有效 JWT 可访问、`http://localhost:5173` 的预检请求通过、伪造图片被拒绝。
