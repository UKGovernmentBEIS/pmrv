package gov.uk.pmrv.keycloak.user.api.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;

import gov.uk.pmrv.keycloak.user.api.model.UserInfo;

class UserMapperTest {

    @Test
    void mapToUsersInfo() {
        UserEntity user1 = new UserEntity();
        user1.setId("1");
        user1.setFirstName("fn1");
        user1.setLastName("ln1");
        user1.setEmail("email1@email", false);
        user1.setEnabled(true);
        UserAttributeEntity user1Attr1 = new UserAttributeEntity();
        user1Attr1.setId("1");
        user1Attr1.setName("attr1");
        user1Attr1.setValue("attr1val");
        user1Attr1.setUser(user1);
        UserAttributeEntity user1Attr2 = new UserAttributeEntity();
        user1Attr2.setId("2");
        user1Attr2.setName("attr2");
        user1Attr2.setValue("attr2val");
        user1Attr2.setUser(user1);
        user1.setAttributes(List.of(user1Attr1, user1Attr2));
        
        UserEntity user2 = new UserEntity();
        user2.setId("2");
        user2.setFirstName("fn2");
        user2.setLastName("ln2");
        user2.setEmail("email2@email", false);
        user2.setEnabled(false);
        UserAttributeEntity user2Attr1 = new UserAttributeEntity();
        user2Attr1.setId("3");
        user2Attr1.setName("attr3");
        user2Attr1.setValue("attr3val");
        user2Attr1.setUser(user2);
        UserAttributeEntity user2Attr2 = new UserAttributeEntity();
        user2Attr2.setId("4");
        user2Attr2.setName("attr4");
        user2Attr2.setValue("attr4val");
        user2Attr2.setUser(user2);
        user2.setAttributes(List.of(user2Attr1, user2Attr2));
        
        
        List<UserEntity> userEntities = List.of(
                user1, user2
                );
        
        List<UserInfo> result = new UserMapper().mapToUsersInfo(userEntities);
        
        assertThat(result).containsExactly(
                UserInfo.builder().id("1").firstName("fn1").lastName("ln1").email("email1@email").enabled(true)
                .build(),
                UserInfo.builder().id("2").firstName("fn2").lastName("ln2").email("email2@email").enabled(false)
                .build()
                );
    }
    
    @Test
    void mapToUsersInfoWithAttributes() {
        UserEntity user1 = new UserEntity();
        user1.setId("1");
        user1.setFirstName("fn1");
        user1.setLastName("ln1");
        user1.setEmail("email1@email", false);
        user1.setEnabled(true);
        UserAttributeEntity user1Attr1 = new UserAttributeEntity();
        user1Attr1.setId("1");
        user1Attr1.setName("attr1");
        user1Attr1.setValue("attr1val");
        user1Attr1.setUser(user1);
        UserAttributeEntity user1Attr2 = new UserAttributeEntity();
        user1Attr2.setId("2");
        user1Attr2.setName("attr2");
        user1Attr2.setValue("attr2val");
        user1Attr2.setUser(user1);
        user1.setAttributes(List.of(user1Attr1, user1Attr2));
        
        UserEntity user2 = new UserEntity();
        user2.setId("2");
        user2.setFirstName("fn2");
        user2.setLastName("ln2");
        user2.setEmail("email2@email", false);
        user2.setEnabled(false);
        UserAttributeEntity user2Attr1 = new UserAttributeEntity();
        user2Attr1.setId("3");
        user2Attr1.setName("attr3");
        user2Attr1.setValue("attr3val");
        user2Attr1.setUser(user2);
        UserAttributeEntity user2Attr2 = new UserAttributeEntity();
        user2Attr2.setId("4");
        user2Attr2.setName("attr4");
        user2Attr2.setValue("attr4val");
        user2Attr2.setUser(user2);
        user2.setAttributes(List.of(user2Attr1, user2Attr2));
        
        
        List<UserEntity> userEntities = List.of(
                user1, user2
                );
        
        List<UserInfo> result = new UserMapper().mapToUsersInfoWithAttributes(userEntities);
        
        assertThat(result).containsExactly(
                UserInfo.builder().id("1").firstName("fn1").lastName("ln1").email("email1@email").enabled(true)
                .attributes(Map.of("attr1", "attr1val", "attr2", "attr2val"))
                .build(),
                UserInfo.builder().id("2").firstName("fn2").lastName("ln2").email("email2@email").enabled(false)
                .attributes(Map.of("attr3", "attr3val", "attr4", "attr4val"))
                .build()
                );
    }
}
