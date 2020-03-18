package io.github.normandesjr.decorator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.github.normandesjr.DynamoDBMapperDecorator;
import io.github.normandesjr.annotation.DynamoDBPrefix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.pivovarit.function.ThrowingConsumer.sneaky;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;

/**
 * <p>
 * Decorator for DynamoDBMapper.
 * </p>
 * <p>
 * Create a @Bean like this:
 * <pre class="brush: java">
 * &#064;Bean
 * public PrefixKeyDynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
 *     return new PrefixKeyDynamoDBMapper(amazonDynamoDB, new DynamoDBMapper(amazonDynamoDB));
 * }
 * </pre>
 * </p>
 * @author normandesjr
 */
public class PrefixKeyDynamoDBMapper extends DynamoDBMapperDecorator {

    public PrefixKeyDynamoDBMapper(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        super(amazonDynamoDB, dynamoDBMapper);
    }

    @Override
    public <T> void save(T object) {
        stream(object.getClass().getDeclaredMethods())
                .filter(filterDynamoDBPrefixAnnotation())
                .filter(filterOnlyGetMethods())
                .forEach(prefixObject(object));

        decoratedDynamoDBMapper.save(object);
    }

    private <T> Consumer<Method> prefixObject(T object) {
        return sneaky(method -> invokeSetMethod(object, method));
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

}
