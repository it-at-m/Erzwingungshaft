package de.muenchen.eh.claim.xta.transport.container;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.claim.xta.transport.ByteArrayDataSource;
import de.muenchen.eh.claim.xta.transport.StringDataSource;
import genv3.de.xoev.transport.xta.x211.ContentType;
import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
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
        this.documents = this.claimDocumentRepository.findByClaimImportId(this.claimContentWrapper.getClaimImport().getId());

        ContentContainerBuilder contentContainerBuilder = ContentContainerBuilder.builder()
                .message(build())
                .attachments(createMessageAttachments())
                .build();

        return GenericContentContainerBuilder.builder().contentContainer(contentContainerBuilder).build().buildContainer();
    }

    private ContentType build() {

        DataSource textMessage = new StringDataSource(
                Base64.getEncoder().encodeToString(claimContentWrapper.getClaimImport().getOutputDirectory().getBytes()),
                "text/plain",
                "message"
        );

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
        String xmlFileName = this.claimContentWrapper.getClaimImport().getOutputDirectory().concat(".xml");
        DataSource justizMessage = new StringDataSource(
                Base64.getEncoder().encodeToString(this.claimContentWrapper.getXjustizXml().getBytes(StandardCharsets.UTF_8)),
                "application/xml",
                xmlFileName
        );
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

            DataSource message = new ByteArrayDataSource(
                    content.getDocument(),
                    "application/pdf",
                    Paths.get(content.getFileName()).getFileName().toString()
            );

            DataHandler handler = new DataHandler(message);

            documentBuilders.add(ContentTypeBuilder.builder()
                    .contentType("application/pdf")
                    .filename(handler.getName())
                    .contentDescription(content.getDocumentType().concat(".pdf for the submitted claim"))
                    .value(handler)
                    .build().build()
            );
        });

        return documentBuilders;
    }
}