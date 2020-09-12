package com.example.gotsaintwho.pojo;

import com.example.gotsaintwho.callbackListener.ViewOnClickListener;

/**
 * 例如朋友圈选项，GPS周边定位选项等
 */
public class Multifunction{

    private String name;
    private int icon;
    private ViewOnClickListener viewOnClickListener;

    public Multifunction(String name, int icon, ViewOnClickListener viewOnClickListener) {
        this.name = name;
        this.icon = icon;
        this.viewOnClickListener = viewOnClickListener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public ViewOnClickListener getViewOnClickListener() {
        return viewOnClickListener;
    }

    public void setViewOnClickListener(ViewOnClickListener viewOnClickListener) {
        this.viewOnClickListener = viewOnClickListener;
    }
}
