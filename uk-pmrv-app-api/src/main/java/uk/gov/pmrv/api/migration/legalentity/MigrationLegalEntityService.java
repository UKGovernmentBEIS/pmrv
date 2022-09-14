package uk.gov.pmrv.api.migration.legalentity;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationLegalEntityService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final LegalEntityService legalEntityService;
    private final Validator validator;
    private final MigrationLegalEntityMapper migrationLegalEntityMapper = Mappers.getMapper(MigrationLegalEntityMapper.class);

    /**
     * tblOperator has one-to-many relationship with tblEmitter
     * and tblEmitter one-to-one with tblInstallationEmitter where
     * the reference_number resides
     **/
    private static final String QUERY_BASE =
            "with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' as fd), " + 
            "allPermits as ( " + 
            "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID " + 
            "      from tblForm F " + 
            "      join tblFormData FD ON FD.fldFormID = F.fldFormID " + 
            "      join tlnkFormTypePhase FTP ON F.fldFormTypePhaseID = FTP.fldFormTypePhaseID " + 
            "      join tlkpFormType FT ON FTP.fldFormTypeID = FT.fldFormTypeID " + 
            "      join tlkpPhase P ON FTP.fldPhaseID = P.fldPhaseID " + 
            "     where FD.fldMinorVersion = 0 AND P.fldDisplayName = 'Phase 3' AND FT.fldName = 'IN_PermitApplication' " + 
            "), " + 
            "mxPVer as ( " + 
            "    select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' " + 
            "      from allPermits " + 
            "     group by fldFormID " + 
            "), latestPermit as ( " + 
            "    select p.fldEmitterID, FD.* " + 
            "      from allPermits p " + 
            "      join mxPVer ON p.fldFormID = mxPVer.FormID AND p.fldMajorVersion = mxPVer.MaxMajorVersion " + 
            "      join tblFormData FD ON FD.fldFormDataID = p.fldFormDataID " + 
            "), le_addr_corr as ( " + 
            "    select * from (values " + 
            "        ('AD5E0A65-B610-415F-BC17-9FA600CE5D76','137 High Street',null,'Burton-On-Trent','DE14 1JZ','England'), " + 
            "        ('3B0CBCF0-0365-413D-9EA3-9FA600CE5D76','1 City Place','Beehive Ring Road','Gatwick','RH6 0PA','England'), " + 
            "        ('7769844C-AA12-46B2-8D1A-9FA600CE5D76','350 Euston Road',null,'London','NW1 3AX','England'), " + 
            "        ('10EC10AC-3F55-4501-BA3E-A8B000F3DC9D','Leicester Road',null,'Ibstock','LE67 6HS','England'), " + 
            "        ('5BD1796C-D162-4A4A-99F8-9FA600CE5D76','Leicester Road',null,'Ibstock','LE67 6HS','England'), " + 
            "        ('6C530397-4480-4EC2-ADE7-A234009E062E','Portland House','Bickenhill Lane','Birmingham','B37 7BQ','England'), " + 
            "        ('14D75330-B2EA-4EC0-9342-9FA600CE5D76','Shell Centre',null,'London','SE1 7NA','England'), " + 
            "        ('CE065D19-72CF-4A89-9749-A0A900E4954C','Breedon-On-The-Hill',null,'Derby','DE73 8AP','England'), " + 
            "        ('8B241454-5682-445E-878F-9FA600CE5D76','Serco House','16 Bartley Wood Business Park','Hook','RG27 9UY','England'), " + 
            "        ('61D89E4D-F22E-43A1-A8A8-A0B300E236AE','Bardon Hall','Copt Oak Road','Markfield','LE67 9PJ','England'), " + 
            "        ('8F6CB494-B147-466D-ADE6-9FA600CE5D76','Building CT3','Centrium','St Albans','AL1 2RE','England'), " + 
            "        ('477EC4FA-4127-4006-A9E0-9FA600CE5D76','Chertsey Road',null,'Sunbury On Thames','TW16 7BP',null), " + 
            "        ('7FC7A810-CEEF-4865-9559-9FA600CE5D76','Walton Oaks Dorking Road',null,'Tadworth','KT20 7NS','England'), " + 
            "        ('5315644F-4524-4A40-B0A7-A21C009116F0','Portland House','Bickenhill Lane','Birmingham','B37 7BQ','England'), " + 
            "        ('DFFE58F8-407D-4EDA-AB7B-A1C100E863C8','PO Box 12','Bournville Lane','Birmingham','B30 2LU','England'), " + 
            "        ('05E21A4C-0166-49D2-9BC3-9FA600CE5D76','Headlands Lane',null,'Knottingley','WF11 0HP','England'), " + 
            "        ('0A527DDD-E5DA-4802-8E72-A2B401043F70','Q3 Office','Shared Services Centre','Newcastle Upon Tyne','NE12 8EX','England'), " + 
            "        ('0330A89E-B617-4209-9158-9FA600CE5D76','Q3 Office','Shared Services Centre','Newcastle Upon Tyne','NE12 8EX','England'), " + 
            "        ('7046E67D-BDC0-4142-8720-9FA600CE5D76','980 Great West Road',null,'Brentford','TW8 9GS','England'), " + 
            "        ('9EBEED04-230E-444F-B701-9FA600CE5D76','Central Accounts Department','East Leake','Loughborough','LE12 6JU','England'), " + 
            "        ('926E593A-21B8-4C4F-B397-9FA600CE5D76','Maple Cross House','Denham Way Maple Cross','Rickmansworth','WD3 9SW','England'), " + 
            "        ('08BB1CD4-40EF-48CA-8CD1-AD4E008A4670','18th Floor, 10 Upper Bank Street','Canary Wharf','London','E14 5BF',null), " + 
            "        ('BC0E7DB6-4590-4F37-B046-A3970092E827','C/O Pinsent Masons Llp','1 Park Row','Leeds','LS1 5AB','United Kingdom'), " + 
            "        ('4CCE2287-6235-4318-B28D-A3970094267C','Suite 1, 3rd Floor','11-12 St. James''s Square','London','SW1Y 4LB','England'), " + 
            "        ('BCAA49EA-E6ED-4783-8471-9FA600CE5D76','Ermyn Way',null,'Leatherhead','KT22 8UX','England'), " + 
            "        ('3D9DF1D4-9F75-4C1C-B3BE-9FA600CE5D76','Hinton House','Birchwood Park Avenue','Warrington','WA3 6GR','England'), " + 
            "        ('024ABBEE-3498-4284-9FDC-9FA600CE5D76','Kings Place 90 York Way',null,'London','N1 9FX','England'), " + 
            "        ('124E17A9-F656-423C-BF01-9FA600CE5D76','Cunard Building','Water Street','Liverpool','L3 1SF','England'), " + 
            "        ('665A483E-3917-4F0F-B5DE-9FA600CE5D76','European Technical Centre','Pilkington Technology Centre','Ormskirk','L40 5UF','England'), " + 
            "        ('826D9148-5524-44D2-9EA3-9FA600CE5D76','Q3 Office','Shared Services Centre','Newcastle Upon Tyne','NE12 8EX','England'), " + 
            "        ('916E451F-953C-45BD-8006-9FA600CE5D76','PO Box 10','Stafford Road','St Helens','WA10 3NS','England'), " + 
            "        ('C7F4C246-5FE7-4D19-AF97-9FA600CE5D76','Grand Buildings','1-3 Strand','London','WC2N 5EH','England'), " + 
            "        ('0655E6D6-9A6E-4733-B112-9FA600CE5D76','Gordano House','Marsh Lane','Bristol','BS20 0NE','England'), " + 
            "        ('8A03DA3C-9046-4780-9FC6-9FA600CE5D76','Hanson House','14 Castle Hill','Maidenhead','SL6 4JJ','England'), " + 
            "        ('6A246790-1F5C-442E-A159-9FA600CE5D76','20 Gresham Street','4th Floor','London','EC2V 7JE','United Kingdom'), " + 
            "        ('7D18052B-02D1-41E0-93C4-9FA600CE5D76','Brunel Way Baglan Energy Park','Briton Ferry','Neath','SA11 2FP','Wales'), " + 
            "        ('793292FD-518F-461B-8A4D-9FA600CE5D76','18 Grosvenor Place',null,'London','SW1X 7HS','England'), " + 
            "        ('FC88F8F7-3B0E-4B63-9AFB-A39700932D3D','78 Cannon Street',null,'London','EC4N 6AF','England'), " + 
            "        ('BC320703-FEDF-4F73-8F64-9FA600CE5D76','No.1 Forbury Place','43 Forbury Road','Reading','RG1 3JH','England'), " + 
            "        ('20235DD0-8D5B-4B41-80EB-A11F00BB089A','G S K House','980 Great West Road','Brentford','TW8 9GS','England'), " + 
            "        ('67580D94-B6E4-4293-8CE4-9FA600CE5D76','Clearwater Court','Vastern Road','Reading','RG1 8DB','England'), " + 
            "        ('D421E5C3-94C7-47A4-B813-A397009343B0','Cunard House 5th Floor','15 Regent Street','London','SW1Y 4LR',null), " + 
            "        ('21013902-CE9D-4413-B036-A4DB00A971AD','Unit 2300, Compton House','The Crescent','Birmingham','B37 7YE','England'), " + 
            "        ('2468E094-1BD9-4DB3-87CB-9FA600CE5D76','Barnett Way','Barnwood','Gloucester','GL4 3RS','England'), " + 
            "        ('3BF5E520-44E6-450F-9F98-A84A008341A3','1st Floor 20 Kingston Road','Staines-Upon-Thames','Staines','TW18 4LG','England'), " + 
            "        ('0B3364D0-9720-438C-BAE8-9FA600CE5D76','Southfields Road',null,'Dunstable','LU6 3EJ','England'), " + 
            "        ('836004A0-530C-4111-941D-9FA600CE5D76','Woodland House','Woodland Park','Hessle','HU13 0FA','United Kingdom'), " + 
            "        ('B043F160-1A77-41ED-B903-9FA600CE5D76','First Floor Templeback','10 Temple Back','Bristol','BS1 6FL','England'), " + 
            "        ('9349B03D-D34E-49A6-BE61-A20600BED0A0','Masters House','107 Hammersmith Road','London','W14 0QH','United Kingdom'), " + 
            "        ('23DEECB6-E79C-4519-B88F-AD4E008CBCBF','18th Floor 10 Upper Bank Street','Canary Wharf','London','E14 5BF','England'), " + 
            "        ('ABC4E068-5E3B-45B5-A3A0-A0B600BD5D27','Swalesmoor Farm','Swales Moor Road','Halifax','HX3 6UF',null), " + 
            "        ('DC40D776-5CBE-4494-A5A0-9FA600CE5D76','G S K House','980 Great West Road','Brentford','TW8 9GS','England'), " + 
            "        ('24339EE0-F1F2-4F78-8686-9FA600CE5D76','PO Box 9',null,'Runcorn','WA7 4JE','England'), " + 
            "        ('AA1196C0-16BB-47C3-98FF-9FA600CE5D76','Building 58','East Moors Road','Cardiff','CF24 5NN',null), " + 
            "        ('FFBC5FD2-C2B5-49AE-A79C-9FA600CE5D76','8 Hanover Square',null,'London','W1S 1HQ','England'), " + 
            "        ('E8C08055-3506-416C-871B-9FA600CE5D76','Wienerberger House','Brooks Drive','Cheadle','SK8 3SA','England'), " + 
            "        ('9C422ED4-6C2A-4AF9-83D7-9FA600CE5D76','Lake District Creamery','Station Road','Wigton','CA7 2AR','England'), " + 
            "        ('93DFE39B-EDF6-4EBF-B4F5-A39700944FA3','Cannon Place','78 Cannon Street','London','EC4N 6AF',null), " + 
            "        ('9928BE80-253E-4035-9335-9FA600CE5D76','Q3 Office','Shared Services Centre','Newcastle Upon Tyne','NE12 8EX','England'), " + 
            "        ('A2A5B853-384A-4CFC-AA52-9FA600CE5D76','Q3 Office','Shared Services Centre','Newcastle Upon Tyne','NE12 8EX','England'), " + 
            "        ('C38C56A0-55DD-426B-A52C-9FA600CE5D76','Liberty Steel Newport Limited','Corporation Road','Newport','NP19 4XE',null), " + 
            "        ('C4D9C240-71F8-466C-8DCD-9FF1011005C3','6th Floor, Radcliffe House','Blenheim Court','Solihull','B91 2AA','England'), " + 
            "        ('04ED9687-B34D-4304-92CE-9FBB01007665','Hill Village','Nadder Lane','South Molton','EX36 4HP','England'), " + 
            "        ('C9A0B6EB-2D7B-4137-8E3B-9FA600CE5D76','5 Grange Park Court','Roman Way','Northampton','NN4 5EA','England'), " + 
            "        ('8C80B442-A2B4-4CCB-92CB-AC1700A5982C','1030 Centre Park','Slutchers Lane','Warrington','WA1 1QL','England'), " + 
            "        ('1AF40009-8ADC-4EB4-9D25-AB6500827282','Administration Building','Brigg Road','Scunthorpe','DN16 1XA','England'), " + 
            "        ('0FB6F06F-3446-4FAC-ACA8-9FBB01007665','Caledonia House','Prime Four Business Park, Kingswells Causeway','Aberdeen','AB15 8PU',null), " + 
            "        ('339C6393-0AB2-4FEC-9825-9FA600CE5D76','7 Am Bahnhof',null,'Iphofen','97346','Germany'), " + 
            "        ('FC2D8E9F-40DB-43E8-B2C0-9FA600CE5D76','11 Gortahurk Road','Tonymore Derrylin','Enniskillen','BT92 9DD','Northern Ireland'), " + 
            "        ('EC519C66-164B-4E0A-985E-A11D00F244BA','11 Lochside Place',null,'Edinburgh','EH12 9HA','Scotland'), " + 
            "        ('323A8B5E-BB6E-4448-A2A2-9FA600CE5D76','3-4 Broadway Park','South Gyle Broadway','Edinburgh','EH12 9JZ','Scotland'), " + 
            "        ('0983977C-E67B-4270-9665-ACF000ADA313','20 Castle Terrace','2nd Floor North','Edinburgh','EH1 2EN','Scotland'), " + 
            "        ('1E959CE8-92F2-4EFE-BD22-9FBB01007665','Inveralmond House','200 Dunkeld Road','Perth','PH1 3AQ',null), " + 
            "        ('787A72A0-E951-442D-8F7D-A5A800B31549','13 Queen''s Road',null,'Aberdeen','AB15 4YL',null), " + 
            "        ('A4625F0D-C5FE-4234-AA84-9FA600CE5D76','Cowick Hall','Snaith','Goole','DN14 9AA',null), " + 
            "        ('5C619140-FD8B-4E2B-8762-9FA600CE5D76','Trust Headquarters','8 Beech Hill Road','Sheffield','S10 2SB',null), " + 
            "        ('235DAF0C-79FC-43D0-9F32-9FBB01007665','J B Russell House, Gartnavel Royal Hospital Campus','1055 Great Western Road','Glasgow','G12 0XH',null), " + 
            "        ('A031B57E-D616-4FAD-B41D-AEA200E488EC','Shared Services Centre Q3 Office, Quorum Business Park','Benton Lane','Newcastle Upon Tyne','NE12 8EX','GB') " + 
            "    ) as t (operatorId, line1, line2, city, postcode, country) " + 
            "), countries as ( " + 
            "    select * from (values " + 
            "        ('Germany','DE'), " + 
            "        ('United Kingdom','GB'), " + 
            "        ('England','GB-ENG'), " + 
            "        ('Northern Ireland','GB-NIR'), " + 
            "        ('Scotland','GB-SCT'), " + 
            "        ('Wales','GB-WLS') " + 
            "    ) as t (country, code) " + 
            "), a as ( " + 
            "    select o.fldOperatorID as operatorId, " + 
            "           em.fldEmitterDisplayId as emitterDisplayId, " + 
            "           em.fldName as emitterName, " + 
            "           ie.fldEmitterID as emitterId, " + 
            "           ca.fldName as competent_authority, " + 
            "           isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_operator_legal_status)[1]', 'NVARCHAR(max)'), 'NULL') as 'type', " + 
            "           o.fldName as name, " + 
            "           nullif(trim(upper(ie.fldCompanyRegistrationNumber)), '') fldCompanyRegistrationNumber, " + 
            "           substring(nullif(trim(upper(ie.fldCompanyRegistrationNumber)), ''), 1, 2) prefix, " + 
            "           nullif(trim(upper(substring(fldCompanyRegistrationNumber, 3, 100))), '') number, " + 
            "           case when c.operatorId is not null then c.line1 else " + 
            "             case F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_operator_legal_status)[1]', 'NVARCHAR(max)') " + 
            "             when 'Company / Corporate Body' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_company_registered_office_address-Address_line_1)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             when 'Partnership' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_partnership_address-Address_line_1)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             else NULL end " + 
            "           end as line1, " + 
            "           case when c.operatorId is not null then c.line2 else " + 
            "             case F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_operator_legal_status)[1]', 'NVARCHAR(max)') " + 
            "             when 'Company / Corporate Body' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_company_registered_office_address-Address_line_2)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             when 'Partnership' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_partnership_address-Address_line_2)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             else NULL end " + 
            "           end as line2, " + 
            "           case when c.operatorId is not null then c.city else " + 
            "             case F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_operator_legal_status)[1]', 'NVARCHAR(max)') " + 
            "             when 'Company / Corporate Body' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_company_registered_office_address-City)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             when 'Partnership' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_partnership_address-City)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             else NULL end " + 
            "           end as city, " + 
            "           case when c.operatorId is not null then c.postcode else " + 
            "             case F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_operator_legal_status)[1]', 'NVARCHAR(max)') " + 
            "             when 'Company / Corporate Body' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_company_registered_office_address-Postcode)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             when 'Partnership' THEN isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Od_partnership_address-Postcode)[1]', 'NVARCHAR(max)'), 'NULL') " + 
            "             else NULL end " + 
            "           end as postcode, " + 
            "           case when c.operatorId is not null then c.country else null end as country " + 
            "      from tblEmitter em " + 
            "      join tlkpEmitterStatus es ON em.fldEmitterStatusID = es.fldEmitterStatusID " + 
            "      join tblOperator o ON em.fldOperatorID = o.fldOperatorID " + 
            "      join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = em.fldCompetentAuthorityID " + 
            "      join latestPermit f ON em.fldEmitterID = f.fldEmitterID " + 
            "      join tblInstallationEmitter ie ON ie.fldEmitterID = em.fldEmitterID " + 
            "      left join le_addr_corr c on c.operatorId = o.fldOperatorID " + 
            "     where es.fldDisplayName = 'Live' " + 
            ") " + 
            "select operatorId, " + 
            "       emitterDisplayId, " + 
            "       emitterName, " + 
            "       emitterId, " + 
            "       competent_authority, " + 
            "       type, " + 
            "       name, " + 
            "       case when len(a.fldCompanyRegistrationNumber) <= 8 and isnumeric(a.fldCompanyRegistrationNumber) = 1 " + 
            "            then replicate('0', 8-len(a.fldCompanyRegistrationNumber)) + a.fldCompanyRegistrationNumber " + 
            "            when a.prefix like '[A-Z][A-Z]' " + 
            "            then case when len(a.number) <= 6 and isnumeric(a.number) = 1 " + 
            "                      then a.prefix + replicate('0', 6-len(a.number)) + a.number " + 
            "                      end " + 
            "            else isnull(a.fldCompanyRegistrationNumber, 'N/A') " + 
            "       end reference_number, " + 
            "       line1, " + 
            "       line2, " + 
            "       city, " + 
            "       postcode, " + 
            "       case when c.country is not null then c.code else 'GB' end country " + 
            "  from a left join countries c on a.country = c.country " + 
            " where operatorId not in ('FD750CD1-7C51-4CA1-B17E-9FBB00FFB979', 'A82C2C76-6E1A-4DB9-A3C9-AE1400C360FA') ";

    @Override
    public String getResource() {
        return "legal-entities";
    }

    @Override
    public List<String> migrate(String ids) {
        List<String> results = new ArrayList<>();
        AtomicInteger failedCounter = new AtomicInteger(0);

        Map<String, List<LegalEntityVO>> les = getLegalEntities(ids);
        for (Map.Entry<String, List<LegalEntityVO>> entry : les.entrySet()) {
            List<LegalEntityVO> legalEntityVOs = entry.getValue();
            //if all legalEntityVOs have empty referenceNumber set N/A to all, otherwise remove legalEntityVOs which have empty legalEntityVOs
            if (legalEntityVOs.stream()
                    .filter(legalEntityVO -> ObjectUtils.isEmpty(legalEntityVO.getReferenceNumber()))
                    .count() == legalEntityVOs.size()) {
                for (LegalEntityVO le: legalEntityVOs) {
                    le.setReferenceNumber("N/A");
                }
            }
            legalEntityVOs.removeIf(legalEntityVO -> ObjectUtils.isEmpty(legalEntityVO.getReferenceNumber()));
            List<String> migrationResults = migrateLegalEntity(legalEntityVOs, failedCounter);
            results.addAll(migrationResults);
        }
        results.add("migration of legal entities results: " + failedCounter.get() + "/" + les.size() + " failed");
        return results;
    }

    @NotNull
    private List<String> migrateLegalEntity(List<LegalEntityVO> legalEntityVOs, AtomicInteger failedCounter) {
        List<String> results = new ArrayList<>();
        boolean hasMultipleReferenceNumbers = false;
        boolean hasMultipleAddresses = false;

        if (legalEntityVOs.size() > 1) {
            hasMultipleReferenceNumbers = hasMultipleReferenceNumbers(results, legalEntityVOs);
            hasMultipleAddresses = hasMultipleAddresses(results, legalEntityVOs);
        }

        LegalEntityDTO legalEntityDTO = migrationLegalEntityMapper.toLegalEntityDTO(legalEntityVOs.get(0));

        // Validate
        List<String> constraintViolations = validator.validate(legalEntityDTO).stream()
                .map(constraint -> constructErrorMessage(legalEntityVOs.get(0),
                        constraint.getMessage(), constraint.getPropertyPath().iterator().next().getName() + ":" + constraint.getInvalidValue()))
                .collect(Collectors.toList());
        results.addAll(constraintViolations);

        if (constraintViolations.isEmpty()) {
            //Create
            legalEntityService.createLegalEntity(legalEntityDTO, LegalEntityStatus.ACTIVE);
            results.add(constructSuccessMessage(legalEntityVOs.get(0)));
        }

        //failed counter also counts for legal_entities with MultipleAddresses or MultipleReferenceNumbers although those are being migrated successfully by choosing the first one
        if (!constraintViolations.isEmpty() || hasMultipleAddresses || hasMultipleReferenceNumbers) {
            failedCounter.incrementAndGet();
        }

        return results;
    }

    private Map<String, List<LegalEntityVO>> getLegalEntities(String ids) {
        String sql = ObjectUtils.isEmpty(ids) ? QUERY_BASE :
                String.format(QUERY_BASE + " and operatorId IN (%s)", Arrays.stream(ids.split(","))
                        .filter(Objects::nonNull)
                        .map(id -> "'" + id.trim() + "'").collect(Collectors.joining(",")));

        // Get operator legal entities from ETSWAP
        return migrationJdbcTemplate.query(sql, new LegalEntityVOMapper())
                .stream().collect(Collectors.groupingBy(LegalEntityVO::getOperatorId));
    }

    private boolean hasMultipleAddresses(List<String> results, List<LegalEntityVO> legalEntityVOs) {
        List<AddressDTO> addresses = legalEntityVOs.stream()
                .map(legalEntityVO -> AddressDTO.builder()
                        .city(legalEntityVO.getCity())
                        .line1(legalEntityVO.getLine1())
                        .line2(legalEntityVO.getLine2())
                        .postcode(legalEntityVO.getPostcode())
                        .country(legalEntityVO.getCountry())
                        .build())
                .distinct()
                .collect(Collectors.toList());
        if (addresses.size() > 1) {
            legalEntityVOs.forEach(legalEntityVO -> results.add(constructErrorMessage(legalEntityVO, "Multiple addresses", addresses.size() + " addresses, " + addresses.toString())));
            return true;
        }
        return false;
    }

    private boolean hasMultipleReferenceNumbers(List<String> results, List<LegalEntityVO> legalEntityVOs) {
        List<String> refNums = legalEntityVOs.stream()
                .map(LegalEntityVO::getReferenceNumber)
                .distinct()
                .collect(Collectors.toList());
        if (refNums.size() > 1) {
            legalEntityVOs.forEach(legalEntityVO -> results.add(constructErrorMessage(legalEntityVO, "Multiple reference numbers", refNums.size() + " refNums, " + refNums.toString())));
            return true;
        }
        return false;
    }

    private String constructErrorMessage(LegalEntityVO le, String errorMessage, String data) {
        return "operatorId: " + le.getOperatorId() +
                " | operatorName: " + le.getName() +
                " | emitterId: " + le.getEmitterId() +
                " | emitterDisplayId: " + le.getEmitterDisplayId() +
                " | CA: " + le.getCompetentAuthority() +
                " | referenceNumber: " + le.getReferenceNumber() +
                " | address: " + le.getLine1() + "@" + le.getLine2() + "@" + le.getCity() + "@" + le.getPostcode() + "@" + le.getCountry() +
                " | Error: " + errorMessage +
                " | data: " + data;
    }

    private String constructSuccessMessage(LegalEntityVO le) {
        return "operatorId: " + le.getOperatorId() +
                " | operatorName: " + le.getName() +
                " | emitterId: " + le.getEmitterId() +
                " | emitterDisplayId: " + le.getEmitterDisplayId() +
                " | CA: " + le.getCompetentAuthority() +
                " | referenceNumber: " + le.getReferenceNumber();
    }
}
