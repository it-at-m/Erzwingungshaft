package de.muenchen.eh.infrastructure.integration.xta.transport.container;

import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.common.FileNameUtils;
import de.muenchen.eh.infrastructure.db.entity.ClaimDocument;
import de.muenchen.eh.infrastructure.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.infrastructure.integration.efile.DocumentName;
import de.muenchen.eh.infrastructure.integration.xta.transport.ByteArrayDataSource;
import de.muenchen.eh.infrastructure.integration.xta.transport.StringDataSource;
import de.xoev.transport.xta._211.ContentType;
import de.xoev.transport.xta._211.GenericContentContainer;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component()
@RequiredArgsConstructor
public class XtaMessageContainer {

    private ClaimContentWrapper claimContentWrapper;
    private final ClaimDocumentRepository claimDocumentRepository;
    private List<ClaimDocument> documents;

    public GenericContentContainer build(ClaimContentWrapper claimContentWrapper) {

        this.claimContentWrapper = claimContentWrapper;
        this.documents = this.claimDocumentRepository.findByClaimImportIdOrderByDocumentType(this.claimContentWrapper.getClaimImport().getId());

        ContentContainerBuilder contentContainerBuilder = ContentContainerBuilder.builder()
                .message(build())
                .attachments(createMessageAttachments())
                .build();

        return GenericContentContainerBuilder.builder().contentContainer(contentContainerBuilder).build().buildContainer();
    }

    private ContentType build() {

        DataSource textMessage = new StringDataSource(
                new String(claimContentWrapper.getClaimImport().getOutputDirectory().getBytes()),
                "text/plain",
                "message");

        DataHandler textDataHandler = new DataHandler(textMessage);

        return ContentTypeBuilder.builder()
                .contentType("text/plain")
                .encoding("UTF-8")
                .contentDescription("Message text")
                .value(textDataHandler)
                .build().build();
    }

    private List<ContentType> createMessageAttachments() {
        List<ContentType> messageAttachments = new ArrayList<>(List.of(buildxJustizXml()));
        messageAttachments.addAll(buildPdfAttachments());
        return messageAttachments;
    }

    private ContentType buildxJustizXml() {
        String xmlFileName = DocumentName.VERFAHRENSMITTEILUNG.getFullName();
        DataSource justizMessage = new StringDataSource(
                new String(this.claimContentWrapper.getXjustizXml().getBytes(StandardCharsets.UTF_8)),
                "application/xml",
                xmlFileName);
        DataHandler justizDataHandler = new DataHandler(justizMessage);

        return ContentTypeBuilder.builder()
                .contentType("application/xml")
                .encoding("UTF-8")
                .filename(xmlFileName)
                .contentDescription("Generated xjustiz xml message.")
                .value(justizDataHandler)
                .build().build();
    }

    private List<ContentType> buildPdfAttachments() {

        List<ContentType> documentBuilders = new ArrayList<>();
        this.documents.forEach(content -> {

            String fileName = FileNameUtils.toHumanReadableFileName(content.getFileName());

            DataSource message = new ByteArrayDataSource(
                    content.getDocument(),
                    "application/pdf",
                    fileName);

            DataHandler handler = new DataHandler(message);

            documentBuilders.add(ContentTypeBuilder.builder()
                    .contentType("application/pdf")
                    .filename(handler.getName())
                    .contentDescription(fileName.concat(" for the submitted claim"))
                    .value(handler)
                    .build().build());
        });

        return documentBuilders;
    }
}
