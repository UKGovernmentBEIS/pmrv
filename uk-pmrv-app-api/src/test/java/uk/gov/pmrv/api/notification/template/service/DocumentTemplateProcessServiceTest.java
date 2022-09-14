package uk.gov.pmrv.api.notification.template.service;

import com.j256.simplemagic.ContentInfoUtil;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.utils.ResourceFileUtil;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.TemplatesConfiguration;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.EmailTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

class DocumentTemplateProcessServiceTest {
    private static FreemarkerTemplateEngine freemarkerTemplateEngine;

    @BeforeAll
    public static void init() {
        TemplatesConfiguration templatesConfiguration = new TemplatesConfiguration();
        freemarker.template.Configuration freemarkerConfig = templatesConfiguration.freemarkerConfig();
        freemarkerTemplateEngine = templatesConfiguration.freemarkerTemplateEngine(freemarkerConfig);
    }

    @Test
    void generateFileDocumentFromTemplate_rfi_template() throws IOException, DocumentTemplateProcessException {
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        String signatoryUser = "Signatory user full name";
        Path rfiTemplateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "L025_P3_Request_for_further_information_notice_20130402.docx");
        FileDTO rfiTemplateEnglandFile = createFile(rfiTemplateFilePath);
        
        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> rfiWorkflowData = new HashMap<>();
        Date deadlineDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        rfiWorkflowData.put("deadline", deadlineDate);
        rfiWorkflowData.put("questions", List.of("question1", "question2"));
        
        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, rfiWorkflowData);

        byte[] generatedPdfFile = new DocumentTemplateProcessService(freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(rfiTemplateEnglandFile, templateParams, "fileNameToGenerate");
        
        //assertions
        try(PDDocument pdfDoc = PDDocument.load(generatedPdfFile);){
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            pdfStripper.setStartPage(0);
            pdfStripper.setLineSeparator(" ");
            pdfStripper.setEndPage(pdfDoc.getNumberOfPages());
            String pdfText = pdfStripper.getText(pdfDoc);
            
            assertThat(pdfText).contains(templateParams.getPermitId());
            assertThat(pdfText).contains(templateParams.getCompetentAuthorityParams().getName());
            assertThat(pdfText).contains(templateParams.getSignatoryParams().getFullName());
            assertThat(pdfText).contains(templateParams.getAccountParams().getLegalEntityName());
            assertThat(pdfText).contains(templateParams.getAccountParams().getLegalEntityLocation().split("\\n")[0]);
            assertThat(pdfText).contains("question1");
            assertThat(pdfText).contains("question2");
            assertThat(pdfText).contains(new SimpleDateFormat("dd MMMM yyyy").format(deadlineDate));
        }
    }
    
    @Test
    void generateFileDocumentFromTemplate_rfi_template_when_processing_fails_should_throw_DocumentTemplateProcessException() throws IOException {
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        String signatoryUser = "Signatory user full name";
        Path rfiTemplateForCAEnglandFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "L025_P3_Request_for_further_information_notice_20130402.docx");
        FileDTO rfiTemplateForCAEnglandFile = createFile(rfiTemplateForCAEnglandFilePath);
        
        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> rfiWorkflowData = new HashMap<>();
        
        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, rfiWorkflowData);
        
        try{
            new DocumentTemplateProcessService(freemarkerTemplateEngine)
                    .generateFileDocumentFromTemplate(rfiTemplateForCAEnglandFile, templateParams, "fileNameToGenerate");
        } catch (DocumentTemplateProcessException e) {
            return;
        }
        
        fail("Should not reach here");
    }

    @Test
    void generateFileDocumentFromTemplate_rde_opred_template() throws IOException, DocumentTemplateProcessException {
        CompetentAuthority ca = CompetentAuthority.OPRED;
        String signatoryUser = "Signatory user full name";
        Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "opred", "L026 P3 Request for time extension notice.docx");
        FileDTO templateFile = createFile(templateFilePath);

        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> workflowData = new HashMap<>();
        Date extensionDate = Date.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date deadlineDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        workflowData.put("extensionDate", extensionDate);
        workflowData.put("deadline", deadlineDate);

        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, workflowData);

        byte[] generatedPdfFile = new DocumentTemplateProcessService(freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, "fileNameToGenerate");

        //assertions
        try(PDDocument pdfDoc = PDDocument.load(generatedPdfFile);){
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            pdfStripper.setStartPage(0);
            pdfStripper.setLineSeparator(" ");
            pdfStripper.setEndPage(pdfDoc.getNumberOfPages());
            String pdfText = pdfStripper.getText(pdfDoc);

            assertThat(pdfText).contains(templateParams.getPermitId());
            assertThat(pdfText).contains("ca central info");
            assertThat(pdfText).contains(templateParams.getCompetentAuthorityParams().getName());
            assertThat(pdfText).contains(templateParams.getSignatoryParams().getFullName());
            assertThat(pdfText).contains(templateParams.getAccountParams().getLegalEntityName());
            assertThat(pdfText).contains(templateParams.getAccountParams().getLegalEntityLocation().split("\\n")[0]);
            assertThat(pdfText).contains(new SimpleDateFormat("dd MMMM yyyy").format(extensionDate));
        }
    }
    
    private TemplateParams buildTemplateParams(CompetentAuthority ca, String signatoryUser, FileDTO signatureFile,
            Map<String, Object> workflowData) {
        return TemplateParams.builder()
                .emailParams(EmailTemplateParams.builder()
                        .toRecipient("email@email")
                        .ccRecipients(List.of("cc1@email", "cc2@email"))
                        .build())
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(ca)
                        .logo(ResourceFileUtil.getCompetentAuthorityLogo(ca))
                        .build())
                .competentAuthorityCentralInfo("ca central info")
                .signatoryParams(SignatoryTemplateParams.builder()
                        .fullName(signatoryUser)
                        .signature(signatureFile.getFileContent())
                        .jobTitle("Project Manager")
                        .build())
                .accountParams(AccountTemplateParams.builder()
                        .emitterType(EmitterType.HSE.name())
                        .legalEntityName("LE name")
                        .legalEntityLocation("LE ethnikis antistaseos\nLE street number 124\nLE postal code 15125")
                        .name("account name")
                        .siteName("Account site name")
                        .location("Account ethnikis  \nAccount street number 125 \nAccount postal code 15126")
                        .primaryContact("primary contact")
                        .serviceContact("service contact")
                        .build())
                .permitId("12345")
                .workflowParams(WorkflowTemplateParams.builder()
                        .requestId("123")
                        .requestTypeInfo("your AER permit notification")
                        .requestSubmissionDate(new Date())
                        .params(workflowData)
                        .build())
                .build();
    }
    
    private FileDTO createFile(Path sampleFilePath) throws IOException {
        byte[] bytes = Files.readAllBytes(sampleFilePath);
        return FileDTO.builder()
                .fileContent(bytes)
                .fileName(sampleFilePath.getFileName().toString())
                .fileSize(sampleFilePath.toFile().length())
                .fileType(new ContentInfoUtil().findMatch(bytes).getContentType().getMimeType())
                .build();
    }
}
