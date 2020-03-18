package io.github.normandesjr;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public abstract class DynamoDBMapperDecorator extends DynamoDBMapper {

    protected DynamoDBMapper decoratedDynamoDBMapper;

    public DynamoDBMapperDecorator(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        super(amazonDynamoDB);
        this.decoratedDynamoDBMapper = dynamoDBMapper;
    }

}