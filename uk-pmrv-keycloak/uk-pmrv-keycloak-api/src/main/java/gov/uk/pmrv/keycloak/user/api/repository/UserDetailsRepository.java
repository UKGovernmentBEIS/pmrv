package gov.uk.pmrv.keycloak.user.api.repository;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.keycloak.models.jpa.entities.UserEntity;

import gov.uk.pmrv.keycloak.user.api.model.SignatureDTO;
import gov.uk.pmrv.keycloak.user.api.model.UserDetailsRequestDTO;
import gov.uk.pmrv.keycloak.user.api.model.jpa.Signature;
import gov.uk.pmrv.keycloak.user.api.model.jpa.UserDetails;

public class UserDetailsRepository {
    private final EntityManager entityManager;

    public UserDetailsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<UserDetails> findUserDetails(String userId) {
        return Optional.ofNullable(entityManager.find(UserDetails.class, userId));
    }
    
    public void saveUserDetails(UserDetailsRequestDTO updatedUserDetailsRequestDTO) {
        UserEntity userEntity = entityManager.find(UserEntity.class, updatedUserDetailsRequestDTO.getId());
        UserDetails userDetails = findUserDetails(updatedUserDetailsRequestDTO.getId()).orElse(null);
        if(userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setUser(userEntity);
        }
        
        // handle signature
        SignatureDTO newSignature = updatedUserDetailsRequestDTO.getSignature();
        if(newSignature != null) {
            userDetails.setSignature(
                new Signature(UUID.randomUUID().toString(),
                        newSignature.getContent(),
                        newSignature.getName(),
                        newSignature.getSize(),
                        newSignature.getType()));
        } 
        
        if(userDetails.getId() == null) {
            entityManager.persist(userDetails);
        } else {
            entityManager.merge(userDetails);
        }
    }
    
    public Optional<Signature> findUserSignatureBySignatureUuid(String signatureUuid) {
        return entityManager.createQuery(
                "select u.signature from UserDetails u where u.signature.signatureUuid = :signatureUuid", Signature.class)
                    .setParameter("signatureUuid", signatureUuid)
                    .getResultList().stream().findFirst();
    }
}
