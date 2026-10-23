package com.hollow.bean;

import java.util.Date;

/**
 * 帖子实体类，对应 post 表的一行。
 *
 * 帖子状态：
 *   PENDING   待审核，仅作者本人与管理员可见
 *   PUBLISHED 已通过，公开可见
 *   REJECTED  已驳回，仅作者本人可见，附带驳回理由
 *   HIDDEN    已隐藏，曾公开后被下架
 */
public class Post {

    private long id;
    private long userId;
    private String title;
    private String content;
    private String status;        // PENDING / PUBLISHED / REJECTED / HIDDEN
    private String rejectReason;  // 仅 REJECTED 时有值
    private long reviewerId;
    private Date reviewedAt;
    private int likeCount;
    private int commentCount;
    private Date createdAt;

    /** 是否待审核 */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /** 是否已通过 */
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /** 是否已驳回 */
    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    /** 是否已隐藏 */
    public boolean isHidden() {
        return "HIDDEN".equals(status);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Date getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Date reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
