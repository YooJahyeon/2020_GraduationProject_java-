package com.example.slt_ver2;

public class SignLanguageList {

    String signLanguageName;
    Integer imageUrl;

    public SignLanguageList(String signLanguageName, Integer imageUrl) {
        this.signLanguageName = signLanguageName;
        this.imageUrl = imageUrl;
    }
    public String getSignLanguageName() {
        return signLanguageName;
    }
    public void setSignLanguageName(String signLanguageName) { this.signLanguageName = signLanguageName;}
    public Integer getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(Integer imageUrl) {
        this.imageUrl = imageUrl;
    }
}
