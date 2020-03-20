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

### Loading

There are a big difference when use _load_, because we need the hash and optionally range key.

It is mandatory to use @DynamoDBHashKey and @DynamoDBRangeKey to use DynamoDBMapper.load(Class<T> clazz, Object hashKey, Object rangeKey).

````java
Product product = dynamoDBMapper.load(Product.class, "AAA1111", "MacBook Pro");
````

Supposing you have hashKey with PROD_ prefix and rangeKey with "PROD_NAME_ prefix with @DynamoDBPrefix annotation
You will get the returned product without these prefixes.

## Developers

### Check quality

There is a docker-compose.yml to start a sonarqube.

````
./mvnw clean install sonar:sonar
````

Check at localhost:9000 the report.

### Release

If your version is a release version (does not end in -SNAPSHOT):

````
./mvnw clean deploy
````

