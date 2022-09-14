package uk.gov.pmrv.api.common.utils;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

import com.j256.simplemagic.ContentInfoUtil;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.common.domain.dto.ResourceFile;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Log4j2
@UtilityClass
public class ResourceFileUtil {

    public ResourceFile getResourceFile(String filepath) throws IOException {
        byte[] content = new ClassPathResource(filepath).getInputStream().readAllBytes();

        return ResourceFile.builder()
            .fileContent(content)
            .fileSize(content.length)
            .fileType(new ContentInfoUtil().findMatch(content).getContentType().getMimeType())
            .build();
    }
    
    public byte[] getCompetentAuthorityLogo(CompetentAuthority competentAuthority) {
        try {
            return new ClassPathResource("images/ca/" + competentAuthority.getLogoPath()).getInputStream().readAllBytes();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER);
        }
    }
}
