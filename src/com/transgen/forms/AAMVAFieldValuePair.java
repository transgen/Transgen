package com.transgen.forms;

public class AAMVAFieldValuePair {
    private String field;
    private String value;
    private String desc;

    public AAMVAFieldValuePair(String field, String desc, String value) {
        this.field = field;
        this.desc = desc;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
