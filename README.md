# Prefix DynamoDB Mapper

## Motivation

It is a best practise to add some prefix string to data save at DynamoDB.

So, the goal of this project is keep this task as simple as possible, using annotation.

## How to use it

On a get method add the @DynamoDBPrefix with the prefix, as the following code:

````java
@DynamoDBPrefix("PRODUCT_")
@DynamoDBHashKey(attributeName = "pk")
public String getSku() {
    return sku;
}
````

It only works on getters methods, otherwise it will be ignored.

## Developers

### Release

````
./mvnw -Drevision=0.0.1.RELEASE deploy scm:tag
````
