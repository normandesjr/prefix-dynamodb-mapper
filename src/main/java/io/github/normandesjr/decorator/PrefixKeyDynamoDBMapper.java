package io.github.normandesjr.decorator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.pivovarit.function.ThrowingConsumer;
import io.github.normandesjr.DynamoDBMapperDecorator;
import io.github.normandesjr.annotation.DynamoDBPrefix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;

/**
 * <p>
 * Decorator for DynamoDBMapper.
 * </p>
 *
 * Create a @Bean like this:
 *
 * <pre class="brush: java">
 * &#064;Bean
 * public PrefixKeyDynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
 *     return new PrefixKeyDynamoDBMapper(amazonDynamoDB, new DynamoDBMapper(amazonDynamoDB));
 * }
 * </pre>
 *
 * @author normandesjr
 */
public class PrefixKeyDynamoDBMapper extends DynamoDBMapperDecorator {

    public PrefixKeyDynamoDBMapper(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        super(amazonDynamoDB, dynamoDBMapper);
    }

    /**
     * Add prefix value of @DynamoDBPrefix annotation before save to DynamoDB
     */
    @Override
    public <T> void save(T object) {
        stream(object.getClass().getDeclaredMethods())
                .filter(filterDynamoDBPrefixAnnotation())
                .filter(filterOnlyGetMethods())
                .forEach(prefixObject(object));

        decoratedDynamoDBMapper.save(object);
    }

    /**
     * Add prefix value of @DynamoDBPrefix annotation before perform load on DynamoDB and remove it after it
     */
    @Override
    public <T> T load(Class<T> clazz, Object hashKey, Object rangeKey) {
        final Method hashKeyMethod = getHashKeyMethod(clazz);
        final Method rangeKeyMethod = getRangeKeyMethod(clazz);

        final String hashKeyPrefix = getHashKeyPrefix(hashKeyMethod);
        final String rangeKeyPrefix = getHashKeyPrefix(rangeKeyMethod);

        final String hashKeyWithPrefix = hashKeyPrefix.concat((String) hashKey);
        final String rangeKeyWithPrefix = rangeKeyPrefix.concat((String) rangeKey);

        T loadedObject = decoratedDynamoDBMapper.load(clazz, hashKeyWithPrefix, rangeKeyWithPrefix);

        if (loadedObject != null) {
            Optional.of(hashKeyMethod).ifPresent(setOriginalValue(loadedObject, hashKey, hashKeyMethod));
            Optional.of(rangeKeyMethod).ifPresent(setOriginalValue(loadedObject, rangeKey, rangeKeyMethod));
        }

        return loadedObject;
    }

    private String getHashKeyPrefix(Method hashKeyMethod) {
        return Optional.of(hashKeyMethod)
                .filter(filterDynamoDBPrefixAnnotation())
                .map(getDynamoDBPrefixValue())
                .orElseGet(() -> "");
    }

    private <T> Method getRangeKeyMethod(Class<T> clazz) {
        return stream(clazz.getDeclaredMethods())
                .filter(filterDynamoDBRangeKeyAnnotation())
                .filter(filterOnlyGetMethods())
                .findAny().orElseThrow(IllegalArgumentException::new);
    }

    private <T> Method getHashKeyMethod(Class<T> clazz) {
        return stream(clazz.getDeclaredMethods())
                .filter(filterDynamoDBHashKeyAnnotation())
                .filter(filterOnlyGetMethods())
                .findAny().orElseThrow(IllegalArgumentException::new);
    }

    private Consumer<Method> setOriginalValue(Object object, Object hashKey, Method hashKeyMethod) {
        return ThrowingConsumer.sneaky(method -> getSetMethod(object, hashKeyMethod).invoke(object, hashKey));
    }

    private Function<Method, String> getDynamoDBPrefixValue() {
        return method -> method.getDeclaredAnnotation(DynamoDBPrefix.class).value();
    }

    private <T> Consumer<Method> prefixObject(T object) {
        return ThrowingConsumer.sneaky(method -> invokeSetMethod(object, method));
    }

    private Predicate<Method> filterOnlyGetMethods() {
        return method -> method.getName().startsWith("get");
    }

    private Predicate<Method> filterDynamoDBPrefixAnnotation() {
        return method -> nonNull(method.getDeclaredAnnotation(DynamoDBPrefix.class));
    }

    private <T> Object invokeSetMethod(T object, Method method) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return getSetMethod(object, method).invoke(object, getValuePrefixed(object, method));
    }

    private <T> String getValuePrefixed(T object, Method method) throws IllegalAccessException, InvocationTargetException {
        return method.getDeclaredAnnotation(DynamoDBPrefix.class).value() + method.invoke(object);
    }

    private <T> Method getSetMethod(T object, Method method) throws NoSuchMethodException {
        return object.getClass().getDeclaredMethod(
                "set" + method.getName().replaceFirst("get", ""), method.getReturnType());
    }

    private Predicate<Method> filterDynamoDBHashKeyAnnotation() {
        return method -> nonNull(method.getDeclaredAnnotation(DynamoDBHashKey.class));
    }

    private Predicate<Method> filterDynamoDBRangeKeyAnnotation() {
        return method -> nonNull(method.getDeclaredAnnotation(DynamoDBRangeKey.class));
    }

}
