package com.example.bean;

public class FindInfo {
    Integer infoId;    //内容ID
    String infoTitle;   //内容标题
    String infoDescribe;   //内容简述
    String infoDetail;   //内容详情
    Integer type;    //类型：0表示日记，1表示趣事
    Integer support;   //点赞数
    String infoAuthor;  //作者

    public Integer getInfoId() {
        return infoId;
    }
    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }


    public String getInfoTitle() {
        return infoTitle;
    }
    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoDescribe() {
        return infoDescribe;
    }
    public void setInfoDescribe(String infoDescribe) {
        this.infoDescribe = infoDescribe;
    }

    public String getInfoDetail() { return infoDetail; }
    public void setInfoDetail(String infoDetail) {
        this.infoDetail = infoDetail;
    }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getSupport() { return support; }
    public void setSupport(Integer support) { this.support = support; }

    public String getInfoAuthor() {
        return infoAuthor;
    }
    public void setInfoAuthor(String infoAuthor) { this.infoAuthor = infoAuthor; }
}
