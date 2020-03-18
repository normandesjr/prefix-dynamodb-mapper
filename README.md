# Prefix DynamoDB Mapper

## Motivation

It is a best practise to add some prefix string to data save in DynamoDB.

So, the goal of this project is to keep this task as simple as possible, using annotation.

## How to use it

### Configuration

````java
@Bean
public PrefixKeyDynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
    return new PrefixKeyDynamoDBMapper(amazonDynamoDB, new DynamoDBMapper(amazonDynamoDB));
}
````

### Annotation

On a get method add the @DynamoDBPrefix with the prefix, as the following code:

````java
@DynamoDBPrefix("PRODUCT_")
@DynamoDBHashKey(attributeName = "pk")
public String getSku() {
    return sku;
}
````

It only works on getters methods, otherwise it will be ignored.

### Saving

Create the object that you would like to save and using the Decorator PrefixKeyDynamoDBMapper the prefix will be added for you.

````java
Product product = new Product("AAA111");
dynamoDBMapper.save(product)
````

If you check at database you'll see the value "PRODUCT_AAA111" as the value of "pk" data.

## Developers

### Release

If your version is a release version (does not end in -SNAPSHOT):

````
./mvnw clean deploy
````

Set the property autoReleaseAfterClose to false to be able manually inspect the staging repository and trigger a release of the staging repository later with:

````
./mvnw nexus-staging:release
````

If you find something went wrong you can drop the staging repository with:

````
./mvnw nexus-staging:drop
````
