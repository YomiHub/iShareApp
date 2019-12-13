package com.example.bean;

public class CommentInfo {
    Integer commentId; //评论Id
    Integer infoId;    //文章ID
    String commentUser;   //评论者
    String commentDetail;   //评论详情

    public Integer getCommentId() {
        return commentId;
    }
    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getInfoId() {
        return infoId;
    }
    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public String getCommentUser() {
        return commentUser;
    }
    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentDetail() {
        return commentDetail;
    }
    public void setCommentDetail(String commentDetail) {
        this.commentDetail = commentDetail;
    }
}
