package uk.gov.pmrv.api.notification.template.service;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.Lvl;
import org.docx4j.wml.Numbering;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.docx4j.Docx4J.FLAG_NONE;

@Log4j2
@Validated
@Service
@RequiredArgsConstructor
public class DocumentTemplateProcessService {

    private static final String SYMBOL_FONT = "Symbol";

    private final FreemarkerTemplateEngine freemarkerTemplateEngine;

    public byte[] generateFileDocumentFromTemplate(@Valid FileDTO fileDocumentTemplate, @Valid TemplateParams templateParams, String fileNameToGenerate)
          throws DocumentTemplateProcessException {
        try (final InputStream inputStream = new ByteArrayInputStream(fileDocumentTemplate.getFileContent());
              final BufferedInputStream inputBufferedStream = new BufferedInputStream(inputStream);
              final ByteArrayOutputStream processedOutputStream = new ByteArrayOutputStream();
              final BufferedOutputStream processedBufferedOutputStream = new BufferedOutputStream(processedOutputStream);
        ) {
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(inputBufferedStream, freemarkerTemplateEngine);
            IContext context = report.createContext();
            FieldsMetadata metadata = report.createFieldsMetadata();

            metadata.addFieldAsImage("competentAuthorityLogo");
            context.put("competentAuthorityLogo",
                  new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getCompetentAuthorityParams().getLogo()), false));
            metadata.addFieldAsImage("competentAuthorityLogo2");//TODO: find a way to reference the image inside the template mutliple times
            context.put("competentAuthorityLogo2",
                  new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getCompetentAuthorityParams().getLogo()), false));

            //add signatory signature
            metadata.addFieldAsImage("signature");
            context.put("signature", new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getSignatoryParams().getSignature()), false));
            metadata.addFieldAsImage("signature2");//TODO:
            context.put("signature2", new ByteArrayImageProvider(new ByteArrayInputStream(templateParams.getSignatoryParams().getSignature()), false));

            //add params
            context.put("email", templateParams.getEmailParams());
            context.put("competentAuthority", templateParams.getCompetentAuthorityParams());
            context.put("competentAuthorityCentralInfo", templateParams.getCompetentAuthorityCentralInfo());
            context.put("signatory", templateParams.getSignatoryParams());
            context.put("account", templateParams.getAccountParams());
            context.put("workflow", templateParams.getWorkflowParams());
            context.put("permitId", templateParams.getPermitId());
            context.put("currentDate", new Date());

            report.process(context, processedBufferedOutputStream);
            return convertToPdf(processedOutputStream, fileNameToGenerate);

        } catch (Exception e) {
            log.error("Error when generation file from template", e);
            throw new DocumentTemplateProcessException(e.getMessage());
        }
    }

    private byte[] convertToPdf(ByteArrayOutputStream processedOutputStream, String fileNameToGenerate) throws Exception {
        try (final ByteArrayOutputStream convertedOutputStream = new ByteArrayOutputStream();
              final BufferedInputStream processedBufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(processedOutputStream.toByteArray()));
        ) {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(processedBufferedInputStream);
            Mapper fontMapper = new IdentityPlusMapper();

            replaceNumberingsWithSymbolFontIfAny(wordMLPackage);

            wordMLPackage.setFontMapper(fontMapper);

            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setOpcPackage(wordMLPackage);
            foSettings.setApacheFopMime(MIME_PDF);

            FopFactory fopFactory = FORendererApacheFOP.getFopFactoryBuilder(foSettings).build();

            FOUserAgent foUserAgent = FORendererApacheFOP.getFOUserAgent(foSettings, fopFactory);
            foUserAgent.setAccessibility(true);
            foUserAgent.setPdfUAEnabled(true);
            foUserAgent.setProducer("PMRV");
            foUserAgent.setCreator("PMRV");
            foUserAgent.setAuthor("PMRV");
            foUserAgent.setTitle(fileNameToGenerate);
            foUserAgent.setCreationDate(new Date());

            Docx4J.toFO(foSettings, convertedOutputStream, FLAG_NONE);
            
            return convertedOutputStream.toByteArray();
        }
    }

    private void replaceNumberingsWithSymbolFontIfAny(WordprocessingMLPackage wordMLPackage) {
        NumberingDefinitionsPart numberingDefinitionsPart = wordMLPackage.getMainDocumentPart().getNumberingDefinitionsPart();

        if (Objects.nonNull(numberingDefinitionsPart)) {
            Numbering numbering = numberingDefinitionsPart.getJaxbElement();
            for (Numbering.AbstractNum abstractNumNode : numbering.getAbstractNum()) {
                for (Lvl lvl : abstractNumNode.getLvl()) {
                    if (lvl.getRPr() != null && lvl.getRPr().getRFonts() != null) {
                        lvl.getRPr().getRFonts().setAscii(SYMBOL_FONT);
                        lvl.getRPr().getRFonts().setHAnsi(SYMBOL_FONT);
                    }
                }
            }
        }
    }
}
