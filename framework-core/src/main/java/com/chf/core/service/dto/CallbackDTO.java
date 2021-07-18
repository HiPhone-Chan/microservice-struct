package com.chf.core.service.dto;

public class CallbackDTO {

    private String type;

    private Object data;

    private String sign;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CallbackDTO [type=" + type + ", data=" + data + ", sign=" + sign + "]";
    }
}
