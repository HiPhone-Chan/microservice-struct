package com.chf.core.web.vm;

public class PathVM {

    private String type;

    private String path;

    public PathVM() {
    }

    public PathVM(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
