package io.github.normandesjr.decorator.model;

import io.github.normandesjr.annotation.DynamoDBPrefix;

public class FieldWithShortNameObject {

    public static final String A_PREFIX = "OBJ_";

    private String a;

    public FieldWithShortNameObject(String a) {
        this.a = a;
    }

    @DynamoDBPrefix(A_PREFIX)
    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
