# Prefix DynamoDB Mapper

## Motivation

Sometimes you need to add some prefixes to attributes when using DynamoDB.

The goal of this project is to keep this task as simple as possible, using annotations.

## How to use it

### Configuration

The lib is released at Maven Central, so you can just add the dependency at your pom.xml

````xml
<dependency>
	<groupId>io.github.normandesjr</groupId>
	<artifactId>prefix-dynamodb-mapper</artifactId>
	<version>0.1.0</version>
</dependency>
````

We will decorate the original _DynamoDBMapper_ with the _PrefixKeyDynamoDBMapper_ as follow:

````java
@Bean
public PrefixKeyDynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
    return new PrefixKeyDynamoDBMapper(amazonDynamoDB, new DynamoDBMapper(amazonDynamoDB));
}
````

### Annotation

On a get method add the _@DynamoDBPrefix_ with the prefix, as the following code:

````java
@DynamoDBPrefix("PRODUCT_")
@DynamoDBHashKey(attributeName = "pk")
public String getSku() {
    return sku;
}
````

Attention, it only works on getters methods and it's mandatory to have the setter method too.

### Saving

Create the object that you would like to save and using the decorator _PrefixKeyDynamoDBMapper_ the prefix will be added for you.

````java
Product product = new Product("AAA111");
dynamoDBMapper.save(product)
````

If you check at database you'll see the value "PRODUCT_AAA111" as the value of "pk" data.

### Loading

It's mandatory to use @DynamoDBHashKey and @DynamoDBRangeKey with DynamoDBMapper.load(Class<T> clazz, Object hashKey, Object rangeKey).

````java
Product product = dynamoDBMapper.load(Product.class, "AAA1111", "MacBook Pro");
````

Supposing you have hashKey with PROD_ prefix and rangeKey with "PROD_NAME_ prefix the returned product will not have these prefixes.

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
