package uk.gov.pmrv.api.files.attachments.domain;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.FileEntity;

@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@SequenceGenerator(name = "default_file_id_generator", sequenceName = "file_attachment_seq", allocationSize = 1)
@Table(name = "file_attachment")
public class FileAttachment extends FileEntity {

}
