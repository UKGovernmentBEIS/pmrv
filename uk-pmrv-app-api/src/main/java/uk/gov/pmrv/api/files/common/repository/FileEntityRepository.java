package uk.gov.pmrv.api.files.common.repository;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.pmrv.api.files.common.domain.FileEntity;

@NoRepositoryBean
public interface FileEntityRepository<T extends FileEntity, ID extends Serializable> extends JpaRepository<T, ID> {

    Optional<T> findByUuid(String uuid);
}