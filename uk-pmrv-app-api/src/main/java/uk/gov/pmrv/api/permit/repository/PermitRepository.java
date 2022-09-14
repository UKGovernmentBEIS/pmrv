package uk.gov.pmrv.api.permit.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;

@Repository
public interface PermitRepository extends JpaRepository<PermitEntity, String> {

    @Transactional(readOnly = true)
    @Query(name = PermitEntity.NAMED_QUERY_FIND_PERMIT_ACCOUNT_BY_ID)
    Optional<Long> findPermitAccountById(String id);

    @Transactional(readOnly = true)
    @Query(name = PermitEntity.NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID, nativeQuery = true)
    Optional<PermitEntityAccountDTO> findPermitEntityAccountByAttachmentUuid(
        @Param("attachmentUuid") String attachmentUuid);

    @Transactional(readOnly = true)
    @Query(name = PermitEntity.NAMED_QUERY_FIND_PERMIT_ID_BY_ACCOUNT_ID)
    Optional<String> findPermitIdByAccountId(Long accountId);

    @Transactional(readOnly = true)
    Optional<PermitEntity> findByAccountId(Long accountId);

    @Transactional(readOnly = true)
    @Query(name = PermitEntity.NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_IDS, nativeQuery = true)
    List<PermitEntityAccountDTO> findByAccountIdIn(List<Long> accountIds);
}
