package gov.uk.pmrv.keycloak.user.api.provider;

import java.util.Collections;
import java.util.List;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import gov.uk.pmrv.keycloak.user.api.model.jpa.UserDetails;

public class UserDetailsJpaEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(UserDetails.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/liquibase/userdetails-changelog_v_0_1_0.xml";
    }

    @Override
    public String getFactoryId() {
        return UserDetailsJpaEntityProviderFactory.ID;
    }
    
    @Override
    public void close() {
    }

}
