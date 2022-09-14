package uk.gov.pmrv.api.user.core.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.FileType;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;
import uk.gov.pmrv.api.user.core.domain.model.core.SignatureConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserSignatureValidatorService {

    private final List<FileValidatorService> fileValidators;
    
    public void validateSignature(FileDTO signature) {
        if(signature == null) {
            return;
        }
        
        //special validators for signature file
        
        // type
        if(!FileType.BMP.getMimeTypes().contains(signature.getFileType())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, FileType.BMP.getSimpleType());
        }
        
        // size
        if (signature.getFileSize() >= SignatureConstants.MAX_ALLOWED_SIZE_BYTES) {
            throw new BusinessException(ErrorCode.MAX_FILE_SIZE_ERROR);
        }
        
        // image dimensions
        try(ByteArrayInputStream imageStream = new ByteArrayInputStream(signature.getFileContent())){
            BufferedImage image = ImageIO.read(imageStream);
            if(image.getWidth() > SignatureConstants.MAX_ALLOWED_WIDTH_PIXELS || 
                    image.getHeight() > SignatureConstants.MAX_ALLOWED_HEIGHT_PIXELS) {
                throw new BusinessException(ErrorCode.INVALID_IMAGE_DIMENSIONS);
            }
        } catch (IOException e) {
            log.error(e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER);
        }
        
        //common validators
        fileValidators.forEach(validator -> validator.validate(signature));
    }
}
