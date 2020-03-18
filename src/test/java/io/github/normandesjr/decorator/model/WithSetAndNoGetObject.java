package io.github.normandesjr.decorator.model;

import io.github.normandesjr.annotation.DynamoDBPrefix;

public class WithSetAndNoGetObject {

    private String id;
    private String name;

    public WithSetAndNoGetObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @DynamoDBPrefix("OBJ_")
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
