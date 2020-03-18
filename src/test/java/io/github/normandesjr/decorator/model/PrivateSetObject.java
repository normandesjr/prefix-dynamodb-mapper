package io.github.normandesjr.decorator.model;

import io.github.normandesjr.annotation.DynamoDBPrefix;

public class PrivateSetObject {

    public static final String ID_PREFIX = "OBJ_";

    private String id;

    public PrivateSetObject(String id) {
        this.id = id;
    }

    @DynamoDBPrefix(ID_PREFIX)
    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }
}
