package com.chf.core.web.vm;

public class SingleValueVM {

    private Object value;

    public SingleValueVM() {
    }

    public SingleValueVM(Object value) {
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
