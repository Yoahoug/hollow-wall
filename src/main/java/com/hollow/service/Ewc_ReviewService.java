package com.hollow.service;

import com.hollow.bean.Xhs_Post;
import com.hollow.dao.Ewc_ReviewDao;

import java.util.List;

/**
 * 审核业务逻辑（ewclpy）。
 *
 * 规则：
 *   1. 只有 PENDING 的帖子可以审核，重复审核会被条件更新挡住
 *   2. 驳回必须填理由，理由会展示给作者
 */
public class Ewc_ReviewService {

    /** 与 post.reject_reason 的字段长度一致 */
    private static final int MAX_REASON_LENGTH = 200;

    private final Ewc_ReviewDao reviewDao = new Ewc_ReviewDao();

    public List<Xhs_Post> listPending() {
        return reviewDao.findPending();
    }

    /**
     * 已发布的帖子，供管理员查看。
     */
    public List<Xhs_Post> listPublished() {
        return reviewDao.findPublished();
    }

    public int countPending() {
        return reviewDao.countPending();
    }

    /**
     * 通过审核。返回 null 表示成功，否则是可展示给管理员的提示。
     */
    public String approve(long postId, long reviewerId) {
        boolean changed = reviewDao.approve(postId, reviewerId);
        return changed ? null : "这条帖子已被处理过，请刷新后再看";
    }

    /**
     * 驳回。理由为空或过长都不进入数据库。
     */
    public String reject(long postId, long reviewerId, String rejectReason) {
        if (rejectReason == null || rejectReason.isBlank()) {
            return "驳回时请填写理由";
        }
        String reason = rejectReason.trim();
        if (reason.length() > MAX_REASON_LENGTH) {
            return "驳回理由不能超过 " + MAX_REASON_LENGTH + " 字";
        }

        boolean changed = reviewDao.reject(postId, reviewerId, reason);
        return changed ? null : "这条帖子已被处理过，请刷新后再看";
    }
}
