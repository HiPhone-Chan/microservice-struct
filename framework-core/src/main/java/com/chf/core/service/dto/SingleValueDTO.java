package com.chf.core.service.dto;

import java.io.Serializable;

public class SingleValueDTO implements Serializable {

    private static final long serialVersionUID = -4745314296783687153L;
    private Object value;

    public SingleValueDTO() {
    }

    public SingleValueDTO(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SingleValueVM [value=" + value + "]";
    }

}
