package com.hollow.service;

import com.hollow.dao.LikeDao;
import com.hollow.dao.PostWriteDao;
import com.hollow.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;

/**
 * 点赞相关的业务逻辑（xhaosen1）。
 *
 * 规则（见分工文档第 7 节）：
 *   1. 防重复点赞靠 post_like 的 uk_post_user 唯一约束，不先查后插，
 *      重复插入时数据库抛唯一约束冲突，这里捕获后提示已经赞过
 *   2. 点赞记录的插入/删除和 post.like_count 的更新是两步，
 *      必须放在同一事务内，要么都成功要么都不动
 */
public class LikeService {

    private LikeDao likeDao = new LikeDao();
    private PostWriteDao postWriteDao = new PostWriteDao();

    /**
     * 点赞。
     * 成功返回 null，失败返回一句提示文字：
     *   已经赞过 / 帖子不存在或已删除。
     */
    public String like(long userId, long postId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 第一步：插入点赞记录。重复插入会被唯一约束拦下来
                likeDao.insert(conn, postId, userId);

                // 第二步：帖子点赞数加一。影响行数为 0 说明帖子不存在
                int rows = postWriteDao.changeLikeCount(conn, postId, 1);
                if (rows == 0) {
                    conn.rollback();
                    return "帖子不存在或已删除";
                }

                conn.commit();
                return null;

            } catch (SQLIntegrityConstraintViolationException e) {
                // uk_post_user 唯一约束冲突：这个人已经赞过这条帖子了
                conn.rollback();
                return "已经赞过这条帖子了";
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("点赞失败", e);
        }
    }

    /**
     * 取消点赞。
     * 成功返回 null，失败返回一句提示文字：还没有赞过 / 帖子不存在。
     */
    public String cancel(long userId, long postId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 第一步：删点赞记录。影响行数为 0 说明本来就没赞过
                int rows = likeDao.delete(conn, postId, userId);
                if (rows == 0) {
                    conn.rollback();
                    return "还没有赞过这条帖子";
                }

                // 第二步：帖子点赞数减一（GREATEST 保证不减成负数）
                postWriteDao.changeLikeCount(conn, postId, -1);

                conn.commit();
                return null;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("取消点赞失败", e);
        }
    }

    /**
     * 查某个用户点赞过的帖子 id 集合。"我的"页面标出已赞状态用。
     */
    public Set<Long> findLikedPostIds(long userId) {
        return likeDao.findLikedPostIds(userId);
    }
}
