package io.github.normandesjr.decorator.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import io.github.normandesjr.annotation.DynamoDBPrefix;

public class WithManyPrefixObject {

    public static final String ID_PREFIX = "OBJ_";
    public static final String NAME_PREFIX = "OBJ_2_";

    private String id;
    private String name;

    public WithManyPrefixObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @DynamoDBPrefix(ID_PREFIX)
    @DynamoDBHashKey(attributeName = "pk")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBPrefix(NAME_PREFIX)
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WithManyPrefixObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
