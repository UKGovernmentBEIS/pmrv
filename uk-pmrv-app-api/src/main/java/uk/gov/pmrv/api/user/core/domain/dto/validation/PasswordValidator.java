package uk.gov.pmrv.api.user.core.domain.dto.validation;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.codec.Hex;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * The password validator.
 */
@Log4j2
public class PasswordValidator implements ConstraintValidator<Password, String> {

    /** The {@link AppProperties} */
    private final PasswordClientService passwordClientService;

    /** The {@link Zxcvbn} */
    private final Zxcvbn zxcvbn;

    /**
     * The {@link PasswordValidator} constructor.
     *
     * @param passwordClientService {@link PasswordClientService}
     */
    public PasswordValidator(PasswordClientService passwordClientService) {
        this.passwordClientService = passwordClientService;
        this.zxcvbn = new Zxcvbn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return true;
        }
        return isStrongPassword(password) && !isPwnedPassword(password);
    }

    /**
     * Checks if the password has been hacked
     *
     * @param password the clean-text password
     * @return true if the password is not pwned
     */
    private boolean isPwnedPassword(String password) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            log.error("Exception during message digest algorithm:", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER);
        }

        byte[] sha1Digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

        String passwordHash = new String(Hex.encode(sha1Digest));

        String res = passwordClientService.searchPassword(passwordHash.substring(0, 5));

        List<String> matchedHashes = Arrays.asList(res.split("\r\n"));

        String passwordHashSuffix = passwordHash.substring(5, 40);

        return matchedHashes.stream()
                .map(h -> h.substring(0, 35))
                .anyMatch(h -> h.equalsIgnoreCase(passwordHashSuffix));
    }

    /**
     * Checks if the password is strong according to https://github.com/dropbox/zxcvbn
     *
     * @param password the clean-text password
     * @return true if the password is not weak
     */
    private boolean isStrongPassword(String password) {
        Strength strength = zxcvbn.measure(password);
        return strength.getScore() != 0;

    }
}
