package com.yanleiz.waitbus.beans;

public class Directs {
    private String dir;
    private String dir_code;

    public Directs(String dir, String dir_code) {
        this.dir = dir;
        this.dir_code = dir_code;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir_code() {
        return dir_code;
    }

    public void setDir_code(String dir_code) {
        this.dir_code = dir_code;
    }

    @Override
    public String toString() {
        return dir;
    }
}
