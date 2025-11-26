package de.muenchen.eh.xta.transport.container;

import de.muenchen.eh.kvue.claim.ClaimProcessingContentWrapper;
import de.muenchen.eh.log.DocumentType;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.xta.transport.ByteArrayDataSource;
import de.muenchen.eh.xta.transport.StringDataSource;
import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component()
@RequiredArgsConstructor
public class RequestGenericContentContainerBuilder {

    private ClaimProcessingContentWrapper claimContentWrapper;
    private final ClaimDocumentRepository claimDocumentRepository;
    private List<ClaimDocument>documents;

    public GenericContentContainer build(ClaimProcessingContentWrapper claimContentWrapper) {

        this.claimContentWrapper = claimContentWrapper;
        this.documents = this.claimDocumentRepository.findByClaimImportId(this.claimContentWrapper.getClaimImport().getId());

        return GenericContentContainerBuilder.builder()
                .contentContainer(
                        ContentContainerBuilder.builder()
                                .message(buildMessage())
                                .attachment(buildxJustizXml())
                                .attachment(buildAntragDocument())
                                .attachment(buildBescheidDocument())
                                .build()
                )
                .build()
                .buildContainer();

    }

    private ContentTypeBuilder buildMessage() {

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
                .build();
    }

    private ContentTypeBuilder buildxJustizXml() {
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
                .build();
    }

    private ContentTypeBuilder buildAntragDocument() {
        return buildDocument(DocumentType.ANTRAG);
    }

    private ContentTypeBuilder buildBescheidDocument() {
        return buildDocument(DocumentType.BESCHEID);
    }

    private ContentTypeBuilder buildDocument(DocumentType type) {

        ClaimDocument document = this.documents.stream()
                .filter(doc -> doc.getDocumentType().equals(type.getDescriptor()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Document missing!"));

        DataSource message = new ByteArrayDataSource(
                document.getDocument(),
                "application/pdf",
                Paths.get(document.getFileName()).getFileName().toString()
        );

        DataHandler handler = new DataHandler(message);

        return ContentTypeBuilder.builder()
                .contentType("application/pdf")
                .filename(handler.getName())
                .contentDescription(type.getDescriptor().concat(".pdf for the submitted claim"))
                .value(handler)
                .build();
    }

}
