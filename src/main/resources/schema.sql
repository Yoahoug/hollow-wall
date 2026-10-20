-- ============================================================
-- Hollow 校园匿名留言墙 —— 建表与初始化数据
--
-- 执行人：Yoahoug（只有负责人可以执行建表脚本）
-- 数据库：hollow_wall（utf8mb4）
--
-- 本脚本可以重复执行：
--   建表用 CREATE TABLE IF NOT EXISTS
--   初始化数据用 INSERT IGNORE，靠唯一键跳过已存在的数据
-- 所以第一次执行和第二次执行都不会报错。
-- ============================================================

-- ------------------------------------------------------------
-- 1. sys_user 用户表（只有负责人读写）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email`         VARCHAR(100) NOT NULL                COMMENT '登录邮箱，全站唯一',
  `password_hash` VARCHAR(100) NOT NULL                COMMENT 'BCrypt 哈希，不存明文',
  `nickname`      VARCHAR(30)  NOT NULL                COMMENT '昵称，匿名展示用',
  `role`          VARCHAR(10)  NOT NULL DEFAULT 'USER' COMMENT 'USER 普通用户 / ADMIN 管理员',
  `status`        VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE 正常 / DISABLED 已停用',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- ------------------------------------------------------------
-- 2. post 帖子表
--    查询归 yujie8791-ai，写入归 xhaosen1，审核状态归 ewclpy
--    建表只由负责人执行一次
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `post` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL                COMMENT '作者，sys_user.id',
  `title`         VARCHAR(100) NOT NULL                COMMENT '标题',
  `content`       TEXT         NOT NULL                COMMENT '正文',
  `status`        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PUBLISHED/REJECTED/HIDDEN',
  `reject_reason` VARCHAR(200) DEFAULT NULL            COMMENT '驳回理由，仅 REJECTED 时有值',
  `reviewer_id`   BIGINT       DEFAULT NULL            COMMENT '审核人，sys_user.id',
  `reviewed_at`   DATETIME     DEFAULT NULL            COMMENT '审核时间',
  `like_count`    INT          NOT NULL DEFAULT 0      COMMENT '点赞数',
  `comment_count` INT          NOT NULL DEFAULT 0      COMMENT '评论数',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_user` (`user_id`),
  KEY `idx_post_status_created` (`status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子';

-- ------------------------------------------------------------
-- 3. post_comment 评论表（yujie8791-ai 读写）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `post_comment` (
  `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id`    BIGINT   NOT NULL                COMMENT '所属帖子',
  `user_id`    BIGINT   NOT NULL                COMMENT '评论人',
  `content`    VARCHAR(500) NOT NULL            COMMENT '评论内容',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_post` (`post_id`),
  KEY `idx_comment_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论';

-- ------------------------------------------------------------
-- 4. post_like 点赞表（xhaosen1 读写）
--    uk_post_user 唯一约束：靠它防止重复点赞，代码里不先查后插
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `post_like` (
  `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id`    BIGINT   NOT NULL                COMMENT '被点赞的帖子',
  `user_id`    BIGINT   NOT NULL                COMMENT '点赞人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_like_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞';

-- ============================================================
-- 初始化数据
-- 明文密码都是 123456，表里存的是 BCrypt 哈希
--   admin@hollow.com / 123456  管理员
--   user@hollow.com  / 123456  普通用户
-- 用星号开头的示例帖子用来验证首页列表和分页，内容是中文，用于检查乱码
-- ============================================================

INSERT IGNORE INTO `sys_user` (`id`, `email`, `password_hash`, `nickname`, `role`, `status`) VALUES
(1, 'admin@hollow.com', '$2a$10$t5/pCEfijjVVacVmjulJPueyyqyXUQSWJa3rMpRbE9NfOVhch1YDm', '管理员', 'ADMIN', 'ACTIVE'),
(2, 'user@hollow.com',  '$2a$10$cur4r/Dw/m4Tb/vKrp1pvOhSoxPn1kKM2Zg/c7vIBS6RODg7hOGbO', '小明',   'USER',  'ACTIVE');

INSERT IGNORE INTO `post` (`id`, `user_id`, `title`, `content`, `status`, `like_count`, `comment_count`) VALUES
(1, 2, '图书馆四楼的自习室什么时候最空？', '想找个安静的地方复习，每次去都满座。有没有人有经验，说说什么时间段人最少。', 'PUBLISHED', 3, 0),
(2, 2, '二号食堂的糖醋里脊是不是换厨师了', '味道和上学期完全不一样了，酸得有点过头。有人有同感吗。', 'PUBLISHED', 1, 0),
(3, 2, '求推荐一门好过的选修课', '下学期想凑个学分，最好是那种不用点名、期末交报告的。', 'PUBLISHED', 0, 0),
(4, 2, '晚上操场的灯能不能晚点关', '十点就关灯了，跑步的只能摸黑跑。希望学校能延到十一点。', 'PENDING', 0, 0);
