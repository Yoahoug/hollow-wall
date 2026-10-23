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
| 审核 | 待审队列、通过、驳回 |
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
| GET | `/post` | yujie | 已实现 |
| GET | `/share` | yujie | 已实现 |
| POST | `/app/comment` | yujie | 已实现 |
| GET/POST | `/login` | Yoahoug | 已实现 |
| GET/POST | `/register` | Yoahoug | 已实现 |
| POST | `/logout` | Yoahoug | 已实现 |
| GET | `/app/post/new` | xhaosen | 已实现 |
| POST | `/app/post/save` | xhaosen | 已实现 |
| POST | `/app/post/delete` | xhaosen | 已实现 |
| GET | `/app/my` | xhaosen | 已实现 |
| POST | `/app/like` | xhaosen | 已实现 |
| GET | `/admin/review` | ewclpy | 已实现（待审队列 + 已发布列表） |
| POST | `/admin/review` | ewclpy | 已实现（`action` 取 `approve` / `reject`） |
| GET | `/admin/user` | ewclpy | 已实现 |
| POST | `/admin/user` | ewclpy | 已实现（`action` 取 `disable` / `enable` / `delete`） |
| GET | `/admin/stats` | ewclpy | 已实现 |
| GET | `/admin/stats/data` | ewclpy | 待实现 |

路由名不修改。新增路由需要先更新本表。

`POST /admin/user` 与下面三条此前同时保留过两种写法，现统一为一条：
`action=disable`、`action=enable`、`action=delete` 由同一个 Servlet 按参数分派。
原表中的 `POST /admin/user/status`、`POST /admin/user/delete` 不再单独实现。

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
| `com.hollow.util.Yoa_RedirectUtil` | `safeBackPath(request, fallback)` | 从 Referer 中只取本站路径，避免开放重定向 |
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
HIDDEN    历史数据，后台已取消隐藏操作，仅用于兼容已存在的帖子
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

### 评论数

`post.comment_count` 列不作为展示依据：写入评论的一方不更新该列，
浏览侧在查询时用 `SELECT COUNT(*) FROM post_comment WHERE post_id = ?`
实时统计，列表与详情都以这个结果为准。

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

四个模块都完成后，占位页已被正式页面取代，现状如下：

| 成员 | 路由 | Servlet | 页面文件 | 现在的状态 |
| --- | --- | --- | --- | --- |
| yujie8791-ai | `GET /home` | `Yuj_HomeServlet` | `jsp/Yuj_Home.jsp` | 已实现（列表、分页、搜索） |
| xhaosen1 | `GET /app/post/new` | `Xhs_PostNewServlet` | `jsp/post/Xhs_PostNew.jsp` | 已实现 |
| xhaosen1 | `GET /app/my` | `Xhs_MyServlet` | `jsp/post/Xhs_My.jsp` | 已实现 |
| ewclpy | `GET /admin/review` | `Ewc_ReviewServlet` | `jsp/Ewc_Review.jsp` | 已实现（待审队列、通过、驳回） |

几点说明：

1. `GET /` 由 `Yoa_Index.jsp` 转发到 `/home`。
   `GET /` 本身无法直接映射给 Servlet，那样会抢走 css 等静态资源。
2. `/app/*` 已经过登录过滤器，`/admin/*` 已经过管理员过滤器，
   页面里不用再自己判断登录，但**取昵称等展示仍需自己处理**。
3. 路由名不要改。需要新增路由，先更新本文档第 6 节再改代码。
4. 页面按归属加文件名前缀，见第 12 节。

尚未实现、需要各自补路由的部分（本文档第 6 节已登记，但阶段一只做了 GET 入口）：

| 成员 | 还缺的路由 |
| --- | --- |
| ewclpy | `GET /admin/stats/data` |

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

---

## 12. 文件命名约定

文件名带归属前缀，一眼能看出这个文件是谁的。前缀统一下划线后接模块名。

| 前缀 | 成员 | 模块 |
| --- | --- | --- |
| `Yoa_` | Yoahoug | 骨架、认证、权限、公共能力与界面 |
| `Yuj_` | yujie8791-ai | 浏览：列表、详情、评论 |
| `Xhs_` | xhaosen1 | 创作：发帖、我的、点赞 |
| `Ewc_` | ewclpy | 治理：审核、用户管理、统计 |

### Java 类

Java 要求 public 类名与文件名一致，所以类名同样带前缀：

```
com.hollow.bean.Yoa_User
com.hollow.dao.Yuj_PostQueryDao
com.hollow.service.Xhs_LikeService
com.hollow.servlet.Ewc_UserServlet
```

### JSP 与静态资源

```
webapp/Yoa_Index.jsp            入口页，转发到 /home
webapp/jsp/Yoa_Header.jsp       全站页头
webapp/jsp/Yoa_Footer.jsp       全站页脚
webapp/jsp/Yoa_Login.jsp        登录页
webapp/jsp/Yoa_Register.jsp     注册页
webapp/jsp/Yuj_Home.jsp         首页
webapp/jsp/post/Yuj_Detail.jsp  详情页
webapp/jsp/post/Yuj_Share.jsp   分享页
webapp/jsp/post/Xhs_My.jsp      我的页面
webapp/jsp/post/Xhs_PostNew.jsp 发帖页
webapp/jsp/Ewc_Review.jsp       审核页
webapp/jsp/admin/Ewc_User.jsp   用户管理页
webapp/jsp/admin/Ewc_Home.jsp   后台统计页
webapp/css/Yoa_Style.css        公共样式
webapp/css/Yuj_Browse.css       浏览侧样式
```

### SQL 与配置

```
resources/Yoa_Schema.sql            建表与初始化数据
resources/Yoa_db.properties.example 连接配置模板
resources/Ewc_Seed.sql              供测试用的补充数据
```

### 不参与改名

以下文件名由框架或构建约定固定，改名会导致运行失败：

| 文件 | 原因 |
| --- | --- |
| `pom.xml` | Maven 约定 |
| `src/main/webapp/WEB-INF/web.xml` | Servlet 规范约定 |
| `src/main/resources/db.properties` | `Yoa_DBUtil` 按此名从 classpath 读取 |
| `.gitignore` | Git 约定 |

新增文件时按上表加前缀。URL 路径、Session 键名、数据库表名不加前缀。

---

## 13. 文件归属核对（2026-10-23）

本次按 `main` 的提交历史逐文件核对「文件名前缀」与「首次新增该文件的提交作者」，
结论是四组前缀与模块负责人一致，没有发现前端文件错挂。

### 前端文件归谁

| 文件 | 归属 | 说明 |
| --- | --- | --- |
| `Yoa_Index.jsp` | Yoahoug | 站点入口，只转发到 `/home` |
| `Yoa_Header.jsp`、`Yoa_Footer.jsp` | Yoahoug | 全站公共片段，不是业务页面 |
| `Yoa_Login.jsp`、`Yoa_Register.jsp` | Yoahoug | 认证页面 |
| `Yoa_Style.css` | Yoahoug | 公共样式 |
| `Yuj_Home.jsp`、`post/Yuj_Detail.jsp`、`post/Yuj_Share.jsp`、`Yuj_Browse.css` | yujie8791-ai | 浏览侧页面与样式 |
| `post/Xhs_My.jsp`、`post/Xhs_PostNew.jsp` | xhaosen1 | 创作侧页面 |
| `Ewc_Review.jsp`、`admin/Ewc_User.jsp`、`admin/Ewc_Home.jsp` | ewclpy | 治理侧页面 |

`Yoa_` 下的前端文件只覆盖入口、页头页脚、登录注册和公共样式，对应第 3 节
「Yoahoug —— 骨架与公共层」里的「公共界面」，不计入任何一个业务模块的页面。
业务页面分别由浏览、创作、治理三侧负责，四组都同时包含 Java 与 JSP，不存在
「某个人只写后端、另一个人只写前端」的切法。

表中的归属按最终维护人计算。三个业务模块的占位页最初由负责人在第一天搭建，
正式页面与后端逻辑由各自负责人在 2026-10-23 完成，因此占位页的首次提交人是
负责人，不表示业务模块归属发生变化。

### 批量改名不改变归属

2026-10-23 曾有两个由负责人执行的批量改名提交（`b7ef997`、`4dbcb47`），把当时
所有源文件统一改成带前缀的名字。这两个提交已被后续的主分支历史重写替换。
现在 `main` 上，第一天的骨架、公共界面与三个模块占位页由负责人在 `04b1f28`
一次提交；各业务模块的正式功能提交从 2026-10-23 起分别由对应负责人提交。
判断文件属于谁，以第 2 节分工表、第 12 节前缀表和当前 `main` 的提交人为准，
不要以某个批量改名提交的作者为准。远端仍保留的 `feat/xhaosen-post`、
`feat/yujie-wall` 是改名前的分支，文件名是旧名，不要用它们做归属判断。

### 已知的边界例外

下面两个文件的前缀写的是实际作者，但触碰了第 4 节的数据边界。需要调整的是
代码职责，不是文件名，不要为了「看起来归负责人」而改前缀：

| 文件 | 现状 | 处理方向 |
| --- | --- | --- |
| `Ewc_AdminUserDao.java` | ewclpy 编写，直接读写 `sys_user` | 对应联调清单 P1-5；改为调用 `Yoa_UserDao`，不要只改前缀 |
| `Ewc_Seed.sql` | ewclpy 编写的补充测试数据 | 对应联调清单 P1-6；由负责人决定收编进 `Yoa_Schema.sql` 或保留为测试专用 |

除这两处外，没有发现前缀与作者不一致的源文件。
