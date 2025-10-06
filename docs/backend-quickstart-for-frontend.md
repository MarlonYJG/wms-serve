## 前端开发者后端快速上手（基于本项目）

### 核心认知
- **分层模型**：Controller(接收请求) → Service(业务逻辑) → Repository(数据访问) → Entity(数据结构)
- **请求-响应**：JSON 请求/响应，校验失败与业务异常由全局异常处理统一返回
- **环境区分**：`application.yml` / `application-dev.yml` / `application-prod.yml` 控制端口、数据库、日志等

### 运行与目录导航
- **运行（开发环境）**：
  - 开发：`mvn spring-boot:run -Dspring-boot.run.profiles=dev`
  - 健康检查：`http://localhost:8080/api/health`
- **代码入口与分层位置**：
  - 启动类：`com.bj.wms.WmsApplication`
  - 控制器：`com.bj.wms.controller.*`
  - 业务层：`com.bj.wms.service.*`
  - 数据访问：`com.bj.wms.repository.*`
  - 实体/校验：`com.bj.wms.entity.*`
  - 全局异常：`com.bj.wms.exception.GlobalExceptionHandler`
  - 配置：`com.bj.wms.config.*`

### 常用开发闭环（新增一个业务为例）
1. 设计接口
   - 路径与方法：如 `POST /api/products`
   - 请求/响应 JSON 模型
2. 定义实体/DTO 与校验
   - 在 `entity` 定义字段（如 `Product`）
   - 使用 `jakarta.validation` 注解（`@NotBlank`、`@Size` 等）
3. 数据访问
   - 在 `repository` 新增查询方法（Spring Data JPA 根据方法名自动实现）
4. 业务逻辑
   - 在 `service` 编写增删改查/校验/事务（`@Transactional`）
5. 暴露接口
   - 在 `controller` 编写 `@GetMapping/@PostMapping` 等方法
   - 使用 `@Valid` 触发参数校验
6. 异常与返回
   - 业务异常 `throw new RuntimeException("...")`
   - 由 `GlobalExceptionHandler` 统一返回标准错误响应
7. 本地验证
   - 用 Postman/Apifox 调 API，核对分页、筛选、错误码
8. 日志与调试
   - 在 `application-*.yml` 调整 `logging.level.com.bj.wms`
   - 控制台日志或断点调试

### 常见能力清单
- **分页/排序**：`Pageable`、`Page<T>`（控制器演示见现有代码）
- **校验**：`@Valid` + 注解（`@NotBlank`、`@Size`…）
- **事务**：`@Transactional`（写操作建议加在 Service 方法）
- **CORS**：`WebConfig` 已放开，前端可直接跨域访问
- **安全**：`SecurityConfig` 目前放开所有请求，后续可加鉴权
- **环境**：dev 用 H2（内存库），prod 用 MySQL；通过 `-Dspring-boot.run.profiles=xxx` 切换

### 从现有代码“查与改”的方法
- 参考已实现模块（User/Product/Warehouse）
  - 复制四件套：Entity → Repository → Service → Controller
  - 改包名与类名，按需求改字段与校验
- 统一返回
  - 可直接返回实体/分页对象，由全局异常统一兜底错误结构
  - 也可使用 `util/ResponseUtil` 生成统一响应
- 统一错误
  - 在业务中抛异常，由 `GlobalExceptionHandler` 统一处理

### 建议的第一步练习
1. 用 Postman 调通现有接口（读写全链路）
   - GET `/api/users`、POST `/api/users`
   - GET `/api/products`、POST `/api/products`
   - GET `/api/warehouses`、POST `/api/warehouses`
2. 新增一个简单实体（如 `Category`）
   - 按四层加文件 → 跑通 CRUD → 补一个搜索接口

### 参考端点（示例）
- 应用地址：`http://localhost:8080/api`
- 健康检查：`http://localhost:8080/api/health`
- H2 控制台（dev）：`http://localhost:8080/h2-console`

### 常见问题
- 启动报错：先看控制台日志，其次检查 `application-*.yml` 配置与数据库连接
- 403/401：当前安全配置放开所有接口；若你开启了鉴权，要同步提供 Token 或调整放行策略
- 懒加载异常：本项目已配置 `spring.jpa.open-in-view=false`，建议在 Service 层完成数据装配

---
如需我按你的需求直接生成“新实体/接口”的代码与 Postman 集合，请列出字段/接口清单即可。


