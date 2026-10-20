# 分工与边界

**Hollow** —— 校园匿名留言墙。
JSP + Servlet + Service + DAO + JavaBean，MVC 分层，Tomcat 9 + MySQL 8。

成员：Yoahoug、yujie8791-ai、xhaosen1、ewclpy

---

## 1. 切分方式

按垂直切片划分：每个人负责一组页面，以及这些页面背后的 Servlet 和 SQL。

不按"一人写全部 DAO、一人写全部 JSP"的方式划分。按层切会让四个人同时改
同一批文件，冲突无法避免，也无法独立验证自己要实现的业务。

按垂直切片划分后，四个人不共用任何 Java 源文件。

各模块内部怎么拆分、类名叫什么、建几个文件，由各自在自己分支内决定，
落在下表的职责范围内即可。

---

## 2. 分工

三份业务模块工作量相当，负责人承担骨架与公共层，并负责最终联调。

| 成员 | 模块 | 页面 | 负责的数据 | 工作量占比 |
| --- | --- | --- | --- | --- |
| Yoahoug | 骨架、认证、权限、数据库初始化、联调 | 登录注册页、公共片段与样式 | `sys_user` | 约 30% |
| yujie8791-ai | 浏览：列表、详情、评论 | 首页、详情页、分享页 | `post_comment` | 约 23% |
| xhaosen1 | 创作：发帖、我的、点赞 | 发帖页、我的页 | `post_like` | 约 23% |
| ewclpy | 治理：审核、用户管理、统计 | 三个后台页面 | `post`（状态列） | 约 24% |

负责人的工作量略多，主要来自骨架和联调两处，业务功能量与其他三人持平。

---

## 3. 模块职责

只规定每个模块承担的业务范围，不规定类名、文件数量与拆分方式。
具体怎么组织代码，由各自在自己分支内决定，落在下表范围内即可。

### Yoahoug —— 骨架与公共层

| 范围 | 内容 |
| --- | --- |
| 工程与配置 | 构建配置、部署描述符、数据库连接配置 |
| 数据库 | 建表与初始化数据，由负责人在共享库执行 |
| 认证 | 注册、登录、退出、登录态维持 |
| 权限 | 游客与登录用户的区分、普通用户与管理员的区分 |
| 公共能力 | 数据库连接、密码加密、HTML 转义、取当前登录用户 |
| 公共界面 | 登录注册页、全站页头与页脚、公共样式 |
| 后台支撑 | 用户列表、停用、删除 |
| 收尾 | 合并三个分支，负责最终联调 |

### yujie8791-ai —— 浏览侧

| 范围 | 内容 |
| --- | --- |
| 列表 | 首页帖子列表、分页、关键词搜索 |
| 详情 | 单条帖子展示、分享页 |
| 评论 | 评论展示与提交 |
| 数据 | 帖子只读查询、评论读写 |
| 要点 | 分页与总数分两条查询；用户内容输出前转义 |

### xhaosen1 —— 创作侧

| 范围 | 内容 |
| --- | --- |
| 发布 | 发帖、删除自己的帖子 |
| 我的 | 个人信息与我的帖子，含各自审核状态 |
| 互动 | 点赞与取消点赞 |
| 数据 | 帖子写入、点赞关系读写 |
| 要点 | 新帖进入待审核；点赞靠唯一约束防重复，不先查后插 |

### ewclpy —— 治理侧

| 范围 | 内容 |
| --- | --- |
| 审核 | 待审队列、通过、驳回、隐藏 |
| 用户管理 | 用户列表、停用、删除 |
| 统计 | 图表数据查询与展示 |
| 数据 | 帖子审核状态更新、统计查询 |
| 要点 | 条件更新防止重复审核；用户管理调用负责人的能力，不直接写用户表 |

---

## 4. post 表的分工

`post` 被三个人使用，因此按写入职责分开，不共用同一份数据访问代码：

| 职责 | 归属 | 允许的语句 |
| --- | --- | --- |
| 查询 | yujie8791-ai | 只允许 `SELECT` |
| 写入 | xhaosen1 | `INSERT`，以及作者删除自己的帖子 |
| 审核 | ewclpy | 只允许 `UPDATE` 审核相关列 |

各自的数据访问代码放在自己负责的文件里，避免两人同时修改同一份代码。

`sys_user` 不做拆分，写操作只由负责人负责。后台的用户管理调用负责人的能力，
不直接写用户表，这样删除用户的多步事务只存在于一处。

---

## 5. 跨模块调用

不在下表内的跨模块调用视为越界。

| 调用方 | 被调用方 | 能力 | 用途 |
| --- | --- | --- | --- |
| 全部 | Yoahoug | 从请求中取当前登录用户 | 权限判断、显示昵称 |
| 全部 | Yoahoug | HTML 转义 | 输出用户内容 |
| ewclpy | xhaosen1 | 按 id 读取帖子 | 审核时展示内容 |
| ewclpy | Yoahoug | 分页获取用户列表 | 后台用户管理 |
| ewclpy | Yoahoug | 更新用户状态 | 停用 / 启用 |
| ewclpy | Yoahoug | 删除用户 | 内部为单个事务 |

方法签名写入代码后不再单方面修改。需要调整时先改本表，再通知其他人拉取。

### 依赖

仅以下三个，新增依赖会改变所有人的构建结果，需要先确认：

| 依赖 | 用途 |
| --- | --- |
| JSTL | 页面标签 |
| MySQL 驱动 | 数据库连接 |
| BCrypt | 密码哈希 |

另外 `javax.servlet-api` 是 `provided` 作用域，由 Tomcat 自带，不算新增依赖。

### 包名与目录

包名统一用 **`com.hollow`**，四个模块都在这个包下：

```
com.hollow.bean      实体类
com.hollow.dao       数据访问
com.hollow.service   业务逻辑
com.hollow.servlet   控制器
com.hollow.filter    过滤器
com.hollow.util      工具类
```

各自在自己模块内新增包或类都可以，不必严格照这个分层。
JSP 放在 `src/main/webapp/jsp/` 下，静态资源放 `src/main/webapp/` 下。

---

## 6. 路由

| 方法 | URL | 归属 | 阶段一状态 |
| --- | --- | --- | --- |
| GET | `/` | yujie | 已接（`Yoa_Index.jsp` 转发到 `/home`） |
| GET | `/post` | yujie | 待实现 |
| GET | `/share` | yujie | 待实现 |
| POST | `/app/comment` | yujie | 待实现 |
| GET/POST | `/login` | Yoahoug | 已实现 |
| GET/POST | `/register` | Yoahoug | 已实现 |
| POST | `/logout` | Yoahoug | 已实现 |
| GET | `/app/post/new` | xhaosen | 已接（占位页） |
| POST | `/app/post/save` | xhaosen | 待实现 |
| POST | `/app/post/delete` | xhaosen | 待实现 |
| GET | `/app/my` | xhaosen | 已接（占位页） |
| POST | `/app/like` | xhaosen | 待实现 |
| GET | `/admin/review` | ewclpy | 已接（占位页） |
| POST | `/admin/review` | ewclpy | 待实现 |
| GET | `/admin/user` | ewclpy | 待实现 |
| POST | `/admin/user/status` | ewclpy | 待实现 |
| POST | `/admin/user/delete` | ewclpy | 待实现 |
| GET | `/admin/stats` | ewclpy | 待实现 |
| GET | `/admin/stats/data` | ewclpy | 待实现 |

路由名不修改。新增路由需要先更新本表。

补充一条：`GET /` 本身不能直接映射给 Servlet（`<url-pattern>/</url-pattern>` 会
接管所有静态资源），所以 `/` 用 `Yoa_Index.jsp` 作欢迎页转发到 `/home`，
`Yuj_HomeServlet` 挂在 `/home` 上。对外行为不变，访问 `/` 就是首页。

### 路由怎么声明

Servlet 和过滤器统一用注解声明，`web.xml` 里不再登记：

```java
@WebServlet("/app/post/new")     // 加在 Servlet 类上
@WebFilter("/admin/*")           // 加在 Filter 类上
```

新增路由时，加一个带 `@WebServlet` 的类即可，**不需要改 `web.xml`**，
也就不会和其他人改到同一个文件。

数据库初始化脚本同时提供测试账号（普通用户与管理员）和若干示例帖子，
供其他人拉取后直接登录与验证列表分页。

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| `admin@hollow.com` | `123456` | 管理员 |
| `user@hollow.com` | `123456` | 普通用户 |

### 拦截

| 前缀 | 过滤器 |
| --- | --- |
| `/app/*` | 登录校验 |
| `/admin/*` | 登录校验 + 管理员校验 |
| 其它 | 公开，游客可访问 |

未登录访问 `/app/*` 时记录原始 URL，跳转登录页，登录成功后回到原页面。

### 公共代码在哪

阶段一已经写好下列公共类，其他人可以直接调用，**不要另写一份**：

| 类 | 方法 | 用途 |
| --- | --- | --- |
| `com.hollow.util.Yoa_AuthUtil` | `currentUser(request)` | 取当前登录用户，返回 `Yoa_User`，没登录返回 `null` |
| | `isLogin(request)` | 是否已登录 |
| | `isAdmin(request)` | 是否管理员 |
| | `SESSION_USER` | Session 里的键名，页面用 `${sessionScope.loginUser}` |
| `com.hollow.util.Yoa_HtmlUtil` | `escape(text)` | HTML 转义，**自己拼 HTML 字符串时必须调用** |
| `com.hollow.util.Yoa_DBUtil` | `getConnection()` | 获取数据库连接，配合 try-with-resources 用 |
| `com.hollow.util.Yoa_PasswordUtil` | `hash(raw)` / `check(raw, hash)` | 密码哈希与校验 |
| `com.hollow.bean.Yoa_User` | `getId()` `getNickname()` `isAdmin()` 等 | 用户对象 |

页面里输出用户内容，用 JSTL 的 `<c:out>` 已经自动转义，不用再调 `Yoa_HtmlUtil`：

```jsp
<c:out value="${post.content}"/>
```

只有自己 `out.print("..." + 变量)` 拼接时才需要 `Yoa_HtmlUtil.escape()`。

`sys_user` 的读写只由负责人负责，其他人**不要写这张表的 SQL**，
需要用户数据时调用负责人的能力（见第 5 节）。

---

## 7. 数据约定

### 帖子状态

```
PENDING   待审核，仅作者本人与管理员可见
PUBLISHED 已通过，公开可见
REJECTED  已驳回，仅作者本人可见，附带驳回理由
HIDDEN    已隐藏，曾公开后被下架
```

规则：

1. 新帖一律待审核。首页只展示已通过的帖子。
2. 审核使用带条件的更新语句：

   ```sql
   UPDATE post SET status = 'PUBLISHED', reviewer_id = ?, reviewed_at = NOW()
   WHERE id = ? AND status = 'PENDING';
   ```

   影响行数为 0 表示该帖已被处理，不做重复审核。

3. 仅待审核状态允许作者编辑自己的帖子。通过后不能修改内容，只能删除。

### 输出转义

帖子正文、评论、昵称等用户输入内容，输出到页面前必须转义。
使用 JSP 标签库输出时已自动转义，其余拼接方式需手动处理。

### 删除用户

四步在同一事务内，顺序不可调整：

```sql
DELETE FROM post_like    WHERE user_id = ?;
DELETE FROM post_comment WHERE user_id = ?;
DELETE FROM post         WHERE user_id = ?;
DELETE FROM sys_user     WHERE id = ?;
```

### 点赞

由唯一约束防止重复，不采用先查询再插入的方式：

```sql
INSERT INTO post_like (post_id, user_id, created_at) VALUES (?, ?, NOW());
UPDATE post SET like_count = like_count + 1 WHERE id = ?;
```

两步在同一事务内。插入触发唯一约束冲突时，提示已经赞过。

---

## 8. 禁止事项

- 修改不属于自己模块的文件
- 对他人负责的数据表执行 `INSERT` / `UPDATE` / `DELETE`
- 修改构建配置、部署描述符、建表脚本、路由名
- 新增 Maven 依赖
- 在数据库中直接修改表结构
- 提交包含连接地址、账号、密码的文件
- 未经说明修改第 5 节列出的能力

---

## 9. 阶段

**阶段一：负责人交付骨架**

交付内容：

| 范围 | 内容 |
| --- | --- |
| 工程 | 构建配置、部署描述符、数据库连接配置模板 |
| 数据 | 建表与初始化数据，由负责人在共享库执行 |
| 认证 | 注册、登录、退出、登录态维持 |
| 权限 | 登录校验与管理员校验 |
| 公共能力 | 数据库连接、密码加密、HTML 转义、取当前登录用户 |
| 公共界面 | 登录注册页、页头页脚、公共样式 |
| 占位 | 三个业务模块的路由入口与占位页面，保证可访问 |

建表与初始化由负责人完成，其他人不需要执行任何建表语句。

验收：其他人拉取代码后，无需额外沟通即可完成

- 打包成功
- 打开首页不是 404 / 500
- 用提供的测试账号登录成功
- 打开自己负责的占位页面，内容正常渲染

四项全部通过再开始写业务代码。

### 阶段一交付的占位清单

负责人已经建好下列占位入口。路由和 Servlet 都已接好，**只差页面内容**。
认领方式：改自己名下那个 JSP 即可，不用动 Servlet、不用动 `web.xml`。

| 成员 | 路由 | Servlet（已建好） | 要改的页面文件 | 现在的状态 |
| --- | --- | --- | --- | --- |
| yujie8791-ai | `GET /home` | `Yuj_HomeServlet` | `jsp/Yuj_Home.jsp` | 占位文字 |
| xhaosen1 | `GET /app/post/new` | `Xhs_PostNewServlet` | `jsp/post/Xhs_PostNew.jsp` | 占位文字 |
| xhaosen1 | `GET /app/my` | `Xhs_MyServlet` | `jsp/post/Xhs_My.jsp` | 占位文字 |
| ewclpy | `GET /admin/review` | `Ewc_ReviewServlet` | `jsp/Ewc_Review.jsp` | 占位文字 |

几点说明：

1. `GET /` 由 `Yoa_Index.jsp` 转发到 `/home`，所以首页实际改的是 `home.jsp`。
   `GET /` 本身无法直接映射给 Servlet，那样会抢走 css 等静态资源。
2. `/app/*` 已经过登录过滤器，`/admin/*` 已经过管理员过滤器，
   页面里不用再自己判断登录，但**取昵称等展示仍需自己处理**。
3. 路由名不要改。需要新增路由，先更新本文档第 6 节再改代码。
4. 占位页目前用的是 `jsp/placeholder/` 目录，正式页面建议挪到自己模块的目录，
   例如 `jsp/post/`、`jsp/admin/`，随各自习惯，不强制。

尚未实现、需要各自补路由的部分（本文档第 6 节已登记，但阶段一只做了 GET 入口）：

| 成员 | 还缺的路由 |
| --- | --- |
| yujie8791-ai | `GET /post`、`GET /share`、`POST /app/comment` |
| xhaosen1 | `POST /app/post/save`、`POST /app/post/delete`、`POST /app/like` |
| ewclpy | `POST /admin/review`、`GET /admin/user`、`POST /admin/user/status`、`POST /admin/user/delete`、`GET /admin/stats`、`GET /admin/stats/data` |

这些由各自在自己分支补，`web.xml` 不用改——Servlet 统一用
`@WebServlet` 注解声明路由，加一个新的 Servlet 类就行。

**阶段二：三人并行**

各自在独立分支上实现自己的模块。负责人同期完成后台需要的用户列表、
停用、删除三块能力。

**阶段三：联调**

由负责人合并三个分支到主分支，走一遍完整流程：
游客浏览 → 注册 → 发帖 → 审核 → 首页展示 → 点赞 → 评论。
发现的问题按模块退回给对应负责人处理，负责人不代为修改业务代码。

---

## 10. 模块验收

每个模块需要能独立验证。

| 成员 | 验收内容 |
| --- | --- |
| Yoahoug | 注册、重复邮箱被拒、登录、普通用户访问后台被拒、未登录访问需登录页面时跳转并在登录后回跳、退出 |
| yujie8791-ai | 首页分页、翻页、关键词搜索、进入详情、提交评论、提交含脚本标签的内容时页面原样显示 |
| xhaosen1 | 未登录点发帖跳转登录并在登录后回到编辑页、发布后我的页面显示审核中、首页不显示、点赞成功且重复点赞被拦截 |
| ewclpy | 审核队列、通过后首页出现、驳回不填理由被拒、停用用户后该用户无法登录、统计页图表有数据 |

## 11. 分支

```
feat/yoahoug-auth
feat/yujie-wall
feat/xhaosen-post
feat/ewclpy-admin
```

- 提交信息格式：`feat(module): 做了什么` / `fix(module): 修了什么`
- 每次提交只做一件事
- 每天开工先拉取主分支变更
- 合并冲突由对应模块负责人处理
