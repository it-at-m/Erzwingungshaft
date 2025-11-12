package de.muenchen.erzwingungshaft.xta;

import genv3.de.xoev.transport.xta.x211.ContentType;
import jakarta.activation.DataHandler;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.attachment.ByteDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

/**
 * Command Line Runner to Test Connection and sending of first packages.
 */
@Slf4j
@Profile("manual")
@SpringBootApplication(scanBasePackages = { "de.muenchen.erzwingungshaft" })
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class XtaCommandLineRunner implements CommandLineRunner {

    //@Autowired
    //private XtaAdapterImpl adapter;

    public static void main(String[] args) {
        SpringApplication.run(XtaCommandLineRunner.class, args);
    }

    @Override
    public void run(String... args) {
        log.debug("Run mit args: {}", Arrays.toString(args));
        try (Scanner scanner = new Scanner(System.in)) {
            String[] currentArgs = args.length > 1 ? args : null;
            System.out.println("Geben Sie Befehle ein (oder 'exit' zum Beenden):");

            while (true) {
                String[] inputArgs = currentArgs != null ? currentArgs : readInput(scanner);
                if (isExit(inputArgs)) break;
                normalizeEmptyParams(inputArgs);
                try {
                    executeCommand(inputArgs);
                } catch (Exception e) {
                    log.debug("Fehler: ", e);
                }
                currentArgs = null; // Nur beim ersten Mal die args nutzen
            }
        }
        System.out.println("Programm wird beendet.");
        System.exit(0);
    }

    private String[] readInput(Scanner scanner) {
        System.out.println("\nnächsten Befehl eingeben (isAccountActive, createMessageId, sendMessage, getTransportReport):");
        System.out.println("beenden mit 'exit'");
        System.out.print("> ");
        return scanner.nextLine().trim().split("\\s+");
    }

    private boolean isExit(String[] args) {
        return args.length > 0 && "exit".equalsIgnoreCase(args[0]);
    }

    private void normalizeEmptyParams(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("\"\"".equals(args[i])) args[i] = "";
        }
    }

    private void executeCommand(String[] args) {
        String result = "Ergebnis";
        if (args.length == 0) return;
        switch (args[0]) {
        case "isAccountActive" -> {
            //var result = adapter.isAccountActive();
            System.out.printf("isAccountActive(): %s%n", result);
        }
        case "createMessageId" -> {
            //var result = adapter.createMessageId();
            System.out.printf("createMessageId(): %s%n", result);
        }
        case "sendMessage" -> {
            if (args.length >= 6) {
                //var dto = new XtaAddressDto(args[1], args[2], args[3], args[4]);
                //adapter.sendMessage(dto, args[5]);
            } else if (args.length >= 5) {
                //var dto = new XtaAddressDto(args[1], args[2], args[3], null);
                //adapter.sendMessage(dto, args[4]);
            } else {
                System.out.println("Fehlende Parameter für sendMessage");
                return;
            }
            System.out.println("sendMessage() erfolgreich ausgeführt.");
        }
        case "getTransportReport" -> {
            if (args.length < 2) {
                System.out.println("Parameter für getTransportReport fehlt.");
                return;
            }
            //var result = adapter.getTransportReport(args[1]);
            System.out.printf("getTransportReport(%s): %s%n", args[1], result);
        }
        default -> {
            System.out.printf("Unbekannter Befehl: %s%n", args[0]);
        }
        }
    }

    public static class FileService {

        /**
         * Lädt den Inhalt einer Datei aus dem resources/tmp Ordner als String.
         *
         * @param filename Dateiname (z. B. "myfile.txt")
         * @return Dateiinhalt als String
         * @throws IOException falls Datei nicht gefunden oder nicht lesbar ist
         */
        public static String loadFileContent(String filename) throws IOException {
            ClassPathResource resource = new ClassPathResource("tmp/" + filename);
            try (var inputStream = resource.getInputStream()) {
                return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            }
        }
    }

    public static ContentType createDummyMessage() throws IOException {
        String fileName = "example_externAnJustiz.xml";
        byte[] fileContent = FileService.loadFileContent(fileName).getBytes(StandardCharsets.UTF_8);
        DataHandler dataHandler = new DataHandler(new ByteDataSource(fileContent));

        ContentType toReturn = new ContentType();
        toReturn.setContentType("text/xml");
        toReturn.setEncoding(StandardCharsets.UTF_8.name());
        toReturn.setFilename(fileName);
        toReturn.setLang("de");
        toReturn.setId("id-" + UUID.randomUUID());
        toReturn.setSize(BigInteger.valueOf(fileContent.length));
        toReturn.setValue(dataHandler);

        return toReturn;
    }

    public static ContentType createDummyAttachment() {
        ContentType messageContent = new ContentType();
        var base64message = """
                JVBERi0xLgoxIDAgb2JqPDwvS2lkc1s8PC9QYXJlbnQgMSAwIFIvUmVzb3VyY2Vz
                PDw+Pi9Db250ZW50cyAyIDAgUj4+XT4+ZW5kb2JqIDIgMCBvYmo8PD4+c3RyZWFt
                CkJULyA5IFRmKEhlbGxvIFdvcmxkKScgRVQKZW5kc3RyZWFtCmVuZG9iaiB0cmFp
                bGVyPDwvUm9vdDw8L1BhZ2VzIDEgMCBSPj4+Pg==
                """.getBytes(StandardCharsets.UTF_8);
        var message = Base64.getMimeDecoder().decode(base64message);
        messageContent.setContentType("application/pdf");
        messageContent.setFilename("min.pdf");

        messageContent.setContentDescription("minimales PDF");
        messageContent.setEncoding(StandardCharsets.UTF_8.name());
        messageContent.setId("id-" + UUID.randomUUID());
        messageContent.setLang("de");
        messageContent.setSize(BigInteger.valueOf(message.length));
        DataHandler dh = new DataHandler(new ByteDataSource(message));
        messageContent.setValue(dh);
        return messageContent;
    }
}
