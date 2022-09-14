package uk.gov.pmrv.api.migration.installationaccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOffShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.repository.CountryRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
class AccountDTOBuilder {

    private final CountryRepository countryRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityMapper legalEntityMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public AccountDTO constructAccountDTO(Emitter emitter) {
        String accountId = emitter.getId();
        AccountDTO account = new AccountDTO();

        account.setId(Long.parseLong(emitter.getEmitterDisplayId()));
        account.setName(emitter.getName());
        account.setSiteName(emitter.getSiteName());
        account.setEmissionTradingScheme(EmissionTradingScheme.valueOf(emitter.getScheme()));
        account.setAccountType(AccountType.INSTALLATION);
        account.setAcceptedDate(emitter.getAcceptedDate());

        //resolve CA
        final String etswapCompAuth = emitter.getCompetentAuthority();
        Optional.ofNullable(MigrationHelper.resolveCompAuth(etswapCompAuth))
            .ifPresentOrElse(
                account::setCompetentAuthority,
                () -> log.error("'{}' - PMRV CA cannot be resolved for ETSWAP value '{}'", accountId, etswapCompAuth));

        //resolve legal entity
        final String etswapLegalEntityName = emitter.getLegalEntityName();
        if(!ObjectUtils.isEmpty(etswapLegalEntityName)) {
            legalEntityRepository.findByNameAndStatus(etswapLegalEntityName, LegalEntityStatus.ACTIVE)
                .map(legalEntityMapper::toLegalEntityDTO)
                .ifPresentOrElse(
                    account::setLegalEntity,
                    () -> log.error("'{}' - Legal Entity cannot be resolved for ETSWAP value '{}'", accountId, etswapLegalEntityName)
            );
        } else  {
            log.error("'{}' - Legal Entity not found in ETSWAP", accountId);
        }

        //resolve location
        final String etswapLocationType = emitter.getLocationType();
        Optional.ofNullable(MigrationHelper.resolveLocationType(etswapLocationType)).ifPresentOrElse(
            locationType -> account.setLocation(constructLocationDTO(locationType, emitter)),
            () -> log.error("'{}' - Location Type cannot be resolved for ETSWAP value '{}'", accountId, etswapLocationType)
        );
        
        // resolve commence date
        final String commencementDate = emitter.getCommencementDate();
        Optional.ofNullable(commencementDate)
            .ifPresentOrElse(
                    date -> account.setCommencementDate(LocalDate.parse(date, formatter)),
                    () -> log.error("ETSWAP commencementDate not found '{}'", accountId));
        
        return account;
    }

    private LocationDTO constructLocationDTO(LocationType locationType, Emitter emitter) {
        LocationDTO locationDTO = null;

        if (locationType == LocationType.ONSHORE) {
            locationDTO = constructLocationOnShore(emitter);
        } else if (locationType == LocationType.OFFSHORE) {
            locationDTO = constructLocationOffShore(emitter.getLongitude(), emitter.getLatitude());
        }

        return locationDTO;
    }

    private LocationOnShoreDTO constructLocationOnShore(Emitter emitter) {
        List<String> ukCountries = List.of("Great Britain", "UK", "uk");
        if (ukCountries.contains(emitter.getCountry())) {
            emitter.setCountry("United Kingdom");
        }

        List<String> englandCountries = List.of("England, UK", "Oxfordshire");
        if (englandCountries.contains(emitter.getCountry())) {
            emitter.setCountry("England");
        }

        if (emitter.getCountry().contains("Scotland")) {
            emitter.setCountry("Scotland");
        }

        if ("N. Ireland".equals(emitter.getCountry())) {
            emitter.setCountry("Northern Ireland");
        }

        String country = countryRepository.findByName(emitter.getCountry()).map(Country::getCode).orElse(emitter.getCountry());

        LocationOnShoreDTO location = LocationOnShoreDTO.builder()
            .gridReference(emitter.getGridReference().replace(" ",""))
            .address(AddressDTO.builder()
                .line1(emitter.getLocationLine1())
                .line2(emitter.getLocationLine2())
                .city(emitter.getCity())
                .country(country)
                .postcode(emitter.getPostCode())
                .build())
            .build();

        location.setType(LocationType.ONSHORE);
        return location;
    }

    private LocationOffShoreDTO constructLocationOffShore(String longitude, String latitude) {
        LocationOffShoreDTO location = new LocationOffShoreDTO();

        //longitude
        if(!ObjectUtils.isEmpty(longitude)) {
            String[] longitudeValues = longitude.trim().split("\\s+");
            if(longitudeValues.length == 4) {
                location.setLongitude(constructCoordinatesDTO(longitudeValues));
            }
        }

        //latitude
        if(!ObjectUtils.isEmpty(latitude)) {
            String[] latitudeValues = latitude.trim().split("\\s+");
            if(latitudeValues.length == 4) {
                location.setLatitude(constructCoordinatesDTO(latitudeValues));
            }
        }

        location.setType(LocationType.OFFSHORE);
        return location;
    }

    private CoordinatesDTO constructCoordinatesDTO(String[] coordinates) {
        return CoordinatesDTO.builder()
            .degree(NumberUtils.isParsable(coordinates[0]) ? Integer.parseInt(coordinates[0]) : null)
            .minute(NumberUtils.isParsable(coordinates[1]) ? Integer.parseInt(coordinates[1]) : null)
            .second(NumberUtils.isParsable(coordinates[2]) ? Double.parseDouble(coordinates[2]) : null)
            .cardinalDirection(CardinalDirection.findCardinalDirection(coordinates[3].charAt(0)))
            .build();
    }
}
