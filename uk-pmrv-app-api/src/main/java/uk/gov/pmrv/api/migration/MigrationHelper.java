package uk.gov.pmrv.api.migration;

import java.util.HashMap;
import java.util.Map;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

public final class MigrationHelper {
    
    private static final Map<String, CompetentAuthority> compAuthMapper;
    private static final Map<String, String> roleMapper;
    private static final Map<String, LocationType> locationTypeMapper;
    private static final Map<String, AccountStatus> accountStatusMapper;
    
    static {
        compAuthMapper = new HashMap<>();
        compAuthMapper.put("SEPA", CompetentAuthority.SCOTLAND);
        compAuthMapper.put("NIEA", CompetentAuthority.NORTHERN_IRELAND);
        compAuthMapper.put("EA", CompetentAuthority.ENGLAND);
        compAuthMapper.put("NRW", CompetentAuthority.WALES);
        compAuthMapper.put("DECC", CompetentAuthority.OPRED);
        
        roleMapper = new HashMap<>();
        roleMapper.put("Competent Authority", AuthorityConstants.REGULATOR_ADMIN_TEAM_ROLE_CODE);
        roleMapper.put("Competent Authority Assistant", AuthorityConstants.REGULATOR_TECHNICAL_OFFICER_ROLE_CODE);
        roleMapper.put("Competent Authority Admin", AuthorityConstants.CA_SUPER_USER_ROLE_CODE); //exclude VB manage?
        roleMapper.put("Member State Admin", AuthorityConstants.PMRV_SUPER_USER_ROLE_CODE);
        roleMapper.put("Operator Admin", AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE);
        roleMapper.put("Operator", AuthorityConstants.OPERATOR_ROLE_CODE);
        roleMapper.put("Operator Editor", "operator_editor");

        locationTypeMapper = new HashMap<>();
        locationTypeMapper.put("POSTAL", LocationType.ONSHORE);
        locationTypeMapper.put("COORDINATE", LocationType.OFFSHORE);

        accountStatusMapper = new HashMap<>();
        accountStatusMapper.put("Live", AccountStatus.LIVE);
    }
    
    public static CompetentAuthority resolveCompAuth(String ets) {
        return compAuthMapper.get(ets);
    }
    
    public static String getEtsCaByPmrvCa(CompetentAuthority ca) {
        return compAuthMapper.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() == ca)
                  .findFirst()
                  .get().getKey();
    }
    
    public static String getEtsRoleNameByPmrvRole(String pmrvRoleCode) {
        return roleMapper.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue().equals(pmrvRoleCode))
                  .findFirst()
                  .get().getKey();
    }
    
    public static String resolveRoleCode(String etsRole) {
        return roleMapper.get(etsRole);
    }

    public static LocationType resolveLocationType(String ets) {
        return locationTypeMapper.get(ets);
    }

    public static AccountStatus resolveAccountStatus(String ets) {
        return accountStatusMapper.get(ets);
    }
    
}
