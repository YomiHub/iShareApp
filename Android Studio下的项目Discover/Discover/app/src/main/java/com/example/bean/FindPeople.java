package com.example.bean;

public class FindPeople {
    String userName;    //用户昵称
    String signature;   //用户签名
    String userLogImage;   //用户头像
   // String passWord;   //用户密码

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUserLogImage() {
        return userLogImage;
    }
    public void setUserLogImage(String userLogImage) {
        this.userLogImage = userLogImage;
    }

    /*public String getPassWord() { return passWord; }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }*/
}
