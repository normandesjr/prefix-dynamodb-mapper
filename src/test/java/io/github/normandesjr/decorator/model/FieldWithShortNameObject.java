package io.github.normandesjr.decorator.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import io.github.normandesjr.annotation.DynamoDBPrefix;

public class FieldWithShortNameObject {

    public static final String A_PREFIX = "OBJ_";

    private String a;

    public FieldWithShortNameObject(String a) {
        this.a = a;
    }

    @DynamoDBPrefix(A_PREFIX)
    @DynamoDBHashKey
    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
}
