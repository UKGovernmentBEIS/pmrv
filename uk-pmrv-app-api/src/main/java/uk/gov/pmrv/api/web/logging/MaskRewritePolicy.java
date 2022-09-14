package uk.gov.pmrv.api.web.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(name = "MaskRewritePolicy", category = Core.CATEGORY_NAME, elementType = "rewritePolicy", printObject = true)
public class MaskRewritePolicy implements RewritePolicy {

    private final List<String> regularExpressions;

    private static final String MASKING_CHARACTER = "*";
    private static final String PAYLOAD_PROPERTY_PATTERN_PREFIX = "\"";
    private static final String PAYLOAD_PROPERTY_PATTERN_SUFFIX = "\"\\s*:\\s*\"([^\"]*)\"";


    private MaskRewritePolicy(final List<Property> properties) {
        this.regularExpressions = new ArrayList<>();
        properties.forEach(property -> {
            String propertyValue = property.getValue();
            //Form the same regex pattern for all payload properties, with only one capturing group
            if ("payloadProperty".equals(property.getName()) && !propertyValue.isBlank()) {
                regularExpressions.add(PAYLOAD_PROPERTY_PATTERN_PREFIX + propertyValue.trim() + PAYLOAD_PROPERTY_PATTERN_SUFFIX);
            }
        });
    }

    @PluginFactory
    public static MaskRewritePolicy create(@PluginElement("Properties") final Property[] properties) {
        if (properties == null || properties.length == 0) {
            return null;
        }
        return new MaskRewritePolicy(Arrays.asList(properties));
    }

    @Override
    public LogEvent rewrite(LogEvent source) {
        final Message msg = source.getMessage();

        final SimpleMessage maskedMsg = new SimpleMessage(maskLogMessage(msg.getFormattedMessage()));

        return new Log4jLogEvent.Builder(source).setMessage(maskedMsg).build();
    }

    private String maskLogMessage(String message) {
        StringBuilder sb = new StringBuilder(message);

        this.regularExpressions.forEach(regex -> {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sb);

            while (matcher.find()) {
                sb.replace(matcher.start(1), matcher.end(1), maskString(matcher.group(1)));
            }
        });

        return sb.toString();
    }

    private String maskString(String value) {
        StringBuilder maskedValue = new StringBuilder(value);
        int maskedValueLength = maskedValue.length();
        return maskedValue.replace(0, maskedValueLength, StringUtils.repeat(MASKING_CHARACTER, maskedValueLength)).toString();
    }
}
