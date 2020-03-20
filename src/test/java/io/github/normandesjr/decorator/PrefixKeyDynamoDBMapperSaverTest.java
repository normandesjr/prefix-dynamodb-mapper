package io.github.normandesjr.decorator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.github.normandesjr.decorator.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PrefixKeyDynamoDBMapperSaverTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    @InjectMocks
    private PrefixKeyDynamoDBMapper prefixKeyDynamoDBMapper;

    @Captor
    private ArgumentCaptor<NoPrefixObject> noPrefixObjectCatptor;

    @Captor
    private ArgumentCaptor<WithOnePrefixObject> withOnePrefixObjectCaptor;

    @Captor
    private ArgumentCaptor<WithManyPrefixObject> withManyPrefixObjectCaptor;

    @Captor
    private ArgumentCaptor<FieldWithShortNameObject> fieldWithShortNameObjectCaptor;

    @Test
    public void should_only_save_without_change_object() {
        final String id = "id";
        final String name = "name";
        NoPrefixObject noPrefixObject = new NoPrefixObject(id, name);

        prefixKeyDynamoDBMapper.save(noPrefixObject);

        verify(dynamoDBMapper).save(noPrefixObjectCatptor.capture());
        assertEquals(id, noPrefixObjectCatptor.getValue().getId());
        assertEquals(name, noPrefixObjectCatptor.getValue().getName());
    }

    @Test
    public void should_change_object_with_prefix() {
        final String id = "id";
        final String name = "name";
        WithOnePrefixObject withOnePrefixObject = new WithOnePrefixObject(id, name);

        prefixKeyDynamoDBMapper.save(withOnePrefixObject);

        verify(dynamoDBMapper).save(withOnePrefixObjectCaptor.capture());
        assertEquals(WithOnePrefixObject.ID_PREFIX + id, withOnePrefixObjectCaptor.getValue().getId());
        assertEquals(name, withOnePrefixObjectCaptor.getValue().getName());
    }

    @Test
    public void should_change_all_attributes_with_prefix() {
        final String id = "id";
        final String name = "name";
        WithManyPrefixObject withManyPrefixObject = new WithManyPrefixObject(id, name);

        prefixKeyDynamoDBMapper.save(withManyPrefixObject);

        verify(dynamoDBMapper).save(withManyPrefixObjectCaptor.capture());
        assertEquals(WithManyPrefixObject.ID_PREFIX + id, withManyPrefixObjectCaptor.getValue().getId());
        assertEquals(WithManyPrefixObject.NAME_PREFIX + name, withManyPrefixObjectCaptor.getValue().getName());
    }

    @Test
    public void should_throw_exception_because_no_set_method() {
        final String id = "id";
        final String name = "name";
        WithGetAndNoSetObject withGetAndNoSetObject = new WithGetAndNoSetObject(id, name);

        assertThrows(NoSuchMethodException.class, () -> prefixKeyDynamoDBMapper.save(withGetAndNoSetObject));
    }

    @Test
    public void should_change_short_attributes_with_prefix() {
        final String a = "a";
        FieldWithShortNameObject fieldWithShortNameObject = new FieldWithShortNameObject(a);

        prefixKeyDynamoDBMapper.save(fieldWithShortNameObject);

        verify(dynamoDBMapper).save(fieldWithShortNameObjectCaptor.capture());
        assertEquals(FieldWithShortNameObject.A_PREFIX + a, fieldWithShortNameObjectCaptor.getValue().getA());
    }

    @Test
    public void should_throw_exception_because_private_set_method() {
        final String id = "id";
        PrivateSetObject privateSetObject = new PrivateSetObject(id);

        assertThrows(IllegalAccessException.class, () -> prefixKeyDynamoDBMapper.save(privateSetObject));
    }


}
