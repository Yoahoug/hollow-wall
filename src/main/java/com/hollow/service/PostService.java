package com.hollow.service;

import com.hollow.bean.Post;
import com.hollow.dao.LikeDao;
import com.hollow.dao.PostWriteDao;
import com.hollow.util.DBUtil;

import java.sql.Connection;
import java.util.List;

/**
 * 帖子创作相关的业务逻辑：发帖、删帖、查自己的帖子、按 id 查帖子（xhaosen1）。
 *
 * Servlet 只负责取参数和跳页面，规则写在这里：
 *   1. 新帖一律进入待审核（PENDING），首页只展示已通过的帖子
 *   2. 通过审核后帖子不能改内容，作者只能删除
 *   3. 作者删帖时连带清理该帖的点赞记录，两步在同一事务内
 */
public class PostService {

    private PostWriteDao postWriteDao = new PostWriteDao();
    private LikeDao likeDao = new LikeDao();

    /**
     * 发布新帖。
     * 成功返回 null，失败返回一句提示文字。
     * 状态固定 PENDING，等管理员审核。
     */
    public String publish(long userId, String title, String content) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);

        boolean success = postWriteDao.insert(post);
        if (!success) {
            return "发布失败，请稍后再试";
        }
        return null;
    }

    /**
     * 作者删除自己的帖子。
     * 删帖和清理该帖的点赞记录在同一事务内：帖子删掉了，点赞记录留着也没有意义。
     * 返回是否删除成功，传别人的帖子 id 或不存在的 id 都返回 false。
     */
    public boolean deleteOwn(long userId, long postId) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // WHERE 里带 user_id，不是自己的帖子删不掉
                int rows = postWriteDao.deleteOwn(conn, postId, userId);

                // 只有真的删掉了帖子才清理点赞记录
                if (rows > 0) {
                    likeDao.deleteByPost(conn, postId);
                }

                conn.commit();
                return rows > 0;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("删除帖子失败", e);
        }
    }

    /**
     * 按 id 查一条帖子，什么状态都查得出来。
     * 审核模块（ewclpy）展示内容时调用，签名不再单方面修改。
     * 查不到返回 null。
     */
    public Post findById(long postId) {
        return postWriteDao.findById(postId);
    }

    /**
     * 查某个用户的全部帖子，按时间倒序。"我的"页面用。
     */
    public List<Post> findByUser(long userId) {
        return postWriteDao.findByUser(userId);
    }
}
