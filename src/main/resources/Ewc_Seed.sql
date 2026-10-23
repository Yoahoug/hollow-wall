-- seed.sql：补充测试数据（可以重复执行，不会报错）
-- 往数据库里多塞几个用户和帖子，方便测试用户管理功能

INSERT IGNORE INTO sys_user (email, password_hash, nickname, role, status) VALUES
('test1@hollow.com', '$2a$10$cur4r/Dw/m4Tb/vKrp1pvOhSoxPn1kKM2Zg/c7vIBS6RODg7hOGbO', '测试用户甲', 'USER', 'ACTIVE'),
('test2@hollow.com', '$2a$10$cur4r/Dw/m4Tb/vKrp1pvOhSoxPn1kKM2Zg/c7vIBS6RODg7hOGbO', '测试用户乙', 'USER', 'DISABLED'),
('test3@hollow.com', '$2a$10$cur4r/Dw/m4Tb/vKrp1pvOhSoxPn1kKM2Zg/c7vIBS6RODg7hOGbO', '测试用户丙', 'USER', 'ACTIVE');

INSERT IGNORE INTO post (user_id, title, content, status, like_count, comment_count) VALUES
(2, '测试帖1：食堂推荐', '二食堂的麻辣香锅真的绝了', 'PUBLISHED', 5, 2),
(2, '测试帖2：求带飞', '有没有人周末一起去爬翠屏山', 'PENDING', 0, 0),
(2, '测试帖3：图书馆占座', '早上八点去图书馆，三楼已经全满了', 'PUBLISHED', 2, 1);
