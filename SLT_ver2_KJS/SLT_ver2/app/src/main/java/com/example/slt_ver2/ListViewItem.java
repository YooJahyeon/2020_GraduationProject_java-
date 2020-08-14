package com.example.slt_ver2;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable iconDrawable ;
    private String descStr ;
    private int position;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setPos(int pos){
        position = pos;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public int getPosition() {
        return this.position;
    }
}