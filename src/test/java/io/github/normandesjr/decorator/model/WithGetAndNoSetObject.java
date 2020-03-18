package io.github.normandesjr.decorator.model;

import io.github.normandesjr.annotation.DynamoDBPrefix;

public class WithGetAndNoSetObject {

    private String id;
    private String name;

    public WithGetAndNoSetObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @DynamoDBPrefix("OBJ_")
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
