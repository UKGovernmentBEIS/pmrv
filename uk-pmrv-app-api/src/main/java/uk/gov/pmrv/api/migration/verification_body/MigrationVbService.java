package uk.gov.pmrv.api.migration.verification_body;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.repository.CountryRepository;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Log4j2
@RequiredArgsConstructor
public class MigrationVbService extends MigrationBaseService {
    private static final String QUERY_BASE =
        "select v.fldVerifierID id, \r\n" +
            "v.fldName name, \r\n" +
            "v.fldIsDisabled isDisabled, \r\n" +
            "addr.fldAddressLine1 addr_line1, \r\n" +
            "addr.fldAddressLine2 addr_line2, \r\n" +
            "addr.fldCity addr_city, \r\n" +
            "addr.fldCountry addr_country, \r\n" +
            "addr.fldPostcodeZIP addr_postcode, \r\n" +
            "v.fldDateCreated created_date, \r\n" +
            "case \r\n" +
            " when v.fldVerifierID in ('BD17ED21-8955-4922-B2D0-AE3100F4F217', 'C56FBF3B-D1E4-4958-AACF-AE3100F66709', '2DF6D4DE-64F7-4D6E-A609-AE3100F5F1EB') \r\n" +
            "  then 'EU_ETS_INSTALLATIONS' \r\n" +
            " when em.fldName = 'Installation' \r\n" +
            "  then 'UK_ETS_INSTALLATIONS' \r\n" +
            " else \r\n" +
            "  'UK_ETS_AVIATION' \r\n" +
            "end accreditation_type, \r\n" +
            "case \r\n" +
            " when v.fldVerifierID = 'BD17ED21-8955-4922-B2D0-AE3100F4F217' \r\n" +
            "  then 'V 109' \r\n" +
            " when v.fldVerifierID = 'C56FBF3B-D1E4-4958-AACF-AE3100F66709' \r\n" +
            "  then '005-GHG' \r\n" +
            " when v.fldVerifierID = '2DF6D4DE-64F7-4D6E-A609-AE3100F5F1EB' \r\n" +
            "  then '000' \r\n" +
            " else \r\n" +
            "  accr.fldRegistrationNumber \r\n" +
            "end reference_number \r\n" +
            "from tblVerifier v \r\n" +
            "join tblAddress  addr on addr.fldAddressID = v.fldAddressID \r\n" +
            "left join tblVerifierAccreditation accr on v.fldVerifierID = accr.fldVerifierID and (nullif(trim(accr.fldRegistrationNumber), '') is not null or accr.fldIsAccredited = 1) \r\n" +
            "left join tlkpEmitterType em on em.fldEmitterTypeID = accr.fldEmitterTypeID ";

    private final CountryRepository countryRepository;
    private final MigrationVbCreationService migrationVbCreationService;
    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;

    @Override
    public String getResource() {
        return "verification-bodies";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = constructQuery(ids);
        List<String> results = new ArrayList<>();
        List<VerificationBodyVO> vbs = migrationJdbcTemplate.query(query, new VerificationBodyMapper());
        Map<String, List<VerificationBodyVO>> vbIdsMap = vbs.stream().collect(Collectors.groupingBy(VerificationBodyVO::getId));

        Map<VerificationBodyVO, Set<EmissionTradingScheme>> vbEmissionTradingSchemesMap = vbIdsMap.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getValue().get(0),
                entry -> entry.getValue().stream()
                    .map(VerificationBodyVO::getAccreditationType)
                    .collect(Collectors.toSet())
                ));

        AtomicInteger failedCounter = new AtomicInteger(0);
        vbEmissionTradingSchemesMap.forEach((vb, emissionTradingSchemes) -> {
            List<String> migrationResults = migrateVb(vb, emissionTradingSchemes, failedCounter);
            results.addAll(migrationResults);
        });
        results.add("migration of vb results: " + failedCounter.get() + "/" + vbIdsMap.size() + " failed");
        return results;
    }

    private List<String> migrateVb(VerificationBodyVO legacyVb, Set<EmissionTradingScheme> emissionTradingSchemes, AtomicInteger failedCounter) {
        List<String> results = new ArrayList<>();
        String country = countryRepository.findByName(legacyVb.getCountry()).map(Country::getCode).orElse(null);
        VerificationBodyEditDTO verificationBodyEditDTO = VerificationBodyEditDTO.builder()
            .name(legacyVb.getName())
            .accreditationReferenceNumber(legacyVb.getReferenceNumber())
            .address(AddressDTO.builder()
                    .line1(legacyVb.getLine1())
                    .line2(legacyVb.getLine2())
                    .city(legacyVb.getCity())
                    .postcode(legacyVb.getPostcode())
                    .country(country)
                    .build())
            .emissionTradingSchemes(emissionTradingSchemes)
            .build();

        Set<ConstraintViolation<VerificationBodyEditDTO>> constraintViolations =
                validator.validate(verificationBodyEditDTO);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(
                    v -> results.add(constructErrorMessage(legacyVb.getId(), legacyVb.getName(), v.getMessage(),
                            v.getPropertyPath().iterator().next().getName())));
            failedCounter.incrementAndGet();
        } else {
            try {
                migrationVbCreationService.createVb(verificationBodyEditDTO, legacyVb.isDisabled());
                results.add(constructSuccessMessage(legacyVb.getId(), legacyVb.getName()));
            } catch (Exception ex) {
                failedCounter.incrementAndGet();
                log.error("migration of vb: {} failed with {}", legacyVb.getId(),
                        ExceptionUtils.getRootCause(ex).getMessage());
                results.add(constructErrorMessage(legacyVb.getId(), legacyVb.getName(),
                        ExceptionUtils.getRootCause(ex).getMessage(), null));
            }
        }
        return results;
    }

    private String constructQuery(String ids) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);

        List<String> idList = !StringUtils.isBlank(ids) ? new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();
        if (!idList.isEmpty()) {
            queryBuilder.append(" where v.fldVerifierID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        queryBuilder.append(" order by v.fldName, em.fldName;");
        return queryBuilder.toString();
    }

    private static String constructErrorMessage(String id, String name, String errorMessage, String data) {
        return "id: " + id +
                " | name: " + name +
                " | Error: " + errorMessage +
                " | data: " + data;
    }

    private static String constructSuccessMessage(String id, String name) {
        return "id: " + id +
                " | name: " + name;
    }
}
