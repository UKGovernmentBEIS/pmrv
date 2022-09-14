package uk.gov.pmrv.api.permit.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "permit")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@NamedQuery(
    name = PermitEntity.NAMED_QUERY_FIND_PERMIT_ACCOUNT_BY_ID,
    query = "select p.accountId as accountId "
        + "from PermitEntity p "
        + "where p.id = :id")
@NamedNativeQuery(
    name = PermitEntity.NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID,
    query = "select p.id as permitEntityId, p.account_id as accountId "
        + "from permit p "
        + "where p.data->'permitAttachments'->>:attachmentUuid is not null")
@NamedNativeQuery(
    name = PermitEntity.NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_IDS,
    query = "select p.id as permitEntityId, p.account_id as accountId, CAST(p.data->>'permitType' as varchar) as permitType "
        + "from permit p "
        + "where account_id in(:accountIds)")
@NamedQuery(
    name = PermitEntity.NAMED_QUERY_FIND_PERMIT_ID_BY_ACCOUNT_ID,
    query = "select p.id "
        + "from PermitEntity p "
        + "where p.accountId = :accountId")
public class PermitEntity {

    public static final String NAMED_QUERY_FIND_PERMIT_ACCOUNT_BY_ID = "PermitEntity.findPermitAccountById";
    public static final String NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID = "PermitEntity.findByAttachmentUuid";
    public static final String NAMED_QUERY_FIND_PERMIT_ID_BY_ACCOUNT_ID = "PermitEntity.findPermitIdByAccountId";
    public static final String NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_IDS = "PermitEntity.findPermitsByAccountIds";

    @Id
    private String id;

    @Type(type = "jsonb")
    @Column(name = "data", columnDefinition = "jsonb")
    @Valid
    private PermitContainer permitContainer;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;
}
