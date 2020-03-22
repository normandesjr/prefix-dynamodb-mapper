package io.github.normandesjr.decorator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.github.normandesjr.decorator.model.WithHashAndRangeWithoutPrefix;
import io.github.normandesjr.decorator.model.WithManyPrefixObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrefixKeyDynamoDBMapperLoaderTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    @InjectMocks
    private PrefixKeyDynamoDBMapper prefixKeyDynamoDBMapper;

    @Test
    public void should_load_without_any_changes() {
        when(dynamoDBMapper.load(WithHashAndRangeWithoutPrefix.class, "id", "name"))
                .thenReturn(new WithHashAndRangeWithoutPrefix("id", "name"));

        WithHashAndRangeWithoutPrefix withHashAndRangeWithoutPrefix = prefixKeyDynamoDBMapper.load(WithHashAndRangeWithoutPrefix.class, "id", "name");

        assertEquals("id", withHashAndRangeWithoutPrefix.getId());
        assertEquals("name", withHashAndRangeWithoutPrefix.getName());
    }

    @Test
    public void should_load_object_with_many_prefix() {
        when(dynamoDBMapper.load(
                WithManyPrefixObject.class, WithManyPrefixObject.ID_PREFIX.concat("id"), WithManyPrefixObject.NAME_PREFIX.concat("name")))
                .thenReturn(new WithManyPrefixObject(
                        WithManyPrefixObject.ID_PREFIX.concat("id"), WithManyPrefixObject.NAME_PREFIX.concat("name")));

        WithManyPrefixObject loadedWithManyPrefixObject = prefixKeyDynamoDBMapper
                .load(WithManyPrefixObject.class, "id", "name");

        assertEquals("id", loadedWithManyPrefixObject.getId());
        assertEquals("name", loadedWithManyPrefixObject.getName());
    }

    @Test
    public void should_return_null_if_not_found() {
        when(dynamoDBMapper.load(
                WithManyPrefixObject.class, WithManyPrefixObject.ID_PREFIX.concat("id"), WithManyPrefixObject.NAME_PREFIX.concat("name")))
                .thenReturn(null);

        WithManyPrefixObject loadedWithManyPrefixObject = prefixKeyDynamoDBMapper
                .load(WithManyPrefixObject.class, "id", "name");

        assertEquals(null, loadedWithManyPrefixObject);
    }

}
