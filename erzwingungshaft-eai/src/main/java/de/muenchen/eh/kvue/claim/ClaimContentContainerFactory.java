package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.common.ExtractEhIdentifier;
import de.muenchen.eh.log.DocumentType;
import de.muenchen.eh.log.db.entity.ClaimDocument;
import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.eh.log.db.repository.ClaimDocumentRepository;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import de.muenchen.xjustiz.xjustiz0500straf.content.FachdatenContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.GrunddatenContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.SchriftgutContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.fachdaten.StrasseHausnummer;
import de.muenchen.xjustiz.xjustiz0500straf.content.fachdaten.Tatort;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Anschrift;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Beteiligung;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Rolle;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.*;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSDokumentklasse;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSRollenbezeichnungTyp3;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSStaatenTyp3;
import de.muenchen.xjustiz.xoev.codelisten.XoevGeschlecht;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@RequiredArgsConstructor
@Slf4j
public class ClaimContentContainerFactory {

    private final ImportClaimData importClaimData;
    private final ClaimImport claimImport;
    private final ClaimDocumentRepository claimDocumentRepository;

    public ContentContainer supplyContentContainer() throws DatatypeConfigurationException {

        return new ContentContainer(supplyFachdatenContent(), supplyGrunddatenContent(), supplySchriftgutContent());
    }

    private FachdatenContent supplyFachdatenContent() {

        FachdatenContent fachdatenContent = new FachdatenContent();

        var startDate = getLocalDate(getImportClaimData().getEhtatdatv());
        fachdatenContent.setAnfangsDatumUhrzeit(LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), Integer.parseInt(getImportClaimData().getEhtatstdv()), Integer.parseInt(getImportClaimData().getEhtatminv())));
        LocalDate endDate = getImportClaimData().getEhtatdatb().isBlank() ? startDate : getLocalDate(getImportClaimData().getEhtatdatb());
        fachdatenContent.setEndeDatumUhrzeit(LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), Integer.parseInt(getImportClaimData().getEhtatstdb()), Integer.parseInt(getImportClaimData().getEhtatminb())));

        Tatort tatortContent = new Tatort();
        tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getImportClaimData().getEhtatstr1(), getImportClaimData().getEhtathnr1()));
        if (!getImportClaimData().getEhtatstr2().isBlank())
            tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getImportClaimData().getEhtatstr2(), getImportClaimData().getEhtathnr2()));
        tatortContent.setOrt(getImportClaimData().getEhtatort());

        //      tatortContent.setOrtsbeschreibung(getEhtat..);

        fachdatenContent.getTatorte().add(tatortContent);

        return fachdatenContent;
    }

    private GrunddatenContent supplyGrunddatenContent() {

        Beteiligung ehBetroffener = new Beteiligung();
        var rolle = new Rolle();

        rolle.setRollenbezeichnung(XoevCodeGDSRollenbezeichnungTyp3.BETROFFENER.getDescriptor());
        ehBetroffener.addRolle(rolle);

        setPersonalData(ehBetroffener);

        return new GrunddatenContent(new ArrayList<>(List.of(ehBetroffener)));
    }

    private SchriftgutContent supplySchriftgutContent() throws DatatypeConfigurationException {
        SchriftgutContent schriftgutContent = new SchriftgutContent();

        schriftgutContent.setDokumente(Optional.of(createDocuments()));
        schriftgutContent.setAkten(Optional.of(createAkte(BigInteger.valueOf(1))));

        return schriftgutContent;
    }

    private List<Dokument> createDocuments() {

        List<Dokument> documents = new ArrayList<>();
        List<ClaimDocument> claimDocuments = claimDocumentRepository.findByClaimImportId(claimImport.getId());

        AtomicLong count = new AtomicLong(1);
        claimDocuments.forEach(document -> {

            var uuidIdentAntrag = UUID.randomUUID().toString();
            List<Datei> antraege = new ArrayList<>();

            var fileName = ExtractEhIdentifier.getFileName(document.getFileName());
            long index = count.getAndIncrement();
            Identifikation identifikationAntrag = new Identifikation(uuidIdentAntrag, BigInteger.valueOf(index));
            Datei file = new Datei(fileName, BigInteger.valueOf(1));
            antraege.add(file);
            FachspezifischeDatenDokument fachspezifischeDatenDokumentAntrag = new FachspezifischeDatenDokument(document.getDocumentType().equals(DocumentType.ANTRAG.getDescriptor()) ? XoevCodeGDSDokumentklasse.ANTRAG : XoevCodeGDSDokumentklasse.BESCHEID, uuidIdentAntrag.concat("_").concat(fileName), antraege);
            documents.add(new Dokument(identifikationAntrag, fachspezifischeDatenDokumentAntrag));

        });

        return documents;
    }

    private List<Akte> createAkte(BigInteger nummer) throws DatatypeConfigurationException {

        var uuidIdentAkte = UUID.randomUUID().toString();

        List<Akte> akten = new ArrayList<>();

        Identifikation identifikationAkte = new Identifikation(uuidIdentAkte, nummer);
        FachspezifischeDatenAkte fachspezifischeDatenAkte = FachspezifischeDatenAkte.builder().choiceFreitext( "TODO : Freitext", false).build();

        Akte akte = new Akte(identifikationAkte, null, null, fachspezifischeDatenAkte);

        akten.add(akte);

        return akten;
    }

    private void setPersonalData(Beteiligung ehBetroffener) {

        var person = ehBetroffener.generateBeteiligter().generateNatuerlichePerson();
        person.setGeschlecht(supplyGeschlecht(getImportClaimData().getEhp1geschl()));

        var name = person.generateVollerName();
        name.setVorname(getImportClaimData().getEhp1vorname());
        name.setNachname(getImportClaimData().getEhp1name());
        name.setTitel(getImportClaimData().getEhp1akad());
        name.setNamensvorsatz(getImportClaimData().getEhp1nambest());
        name.setGeburtsname(getImportClaimData().getEhp1gebname());

        var geburt = person.generateGeburt();
        geburt.setGeburtsdatum(dateFormatConverter(getImportClaimData().getEhp1gebdat(), "yyyy-MM-dd"));
        geburt.setGeburtsort(getImportClaimData().getEhp1gebort());

        person.addAnschrift(setAddress());
    }

    private Anschrift setAddress() {

        var anschrift = new Anschrift();

        anschrift.setAnschriftenzusatz(getImportClaimData().getEhp1zusatz());
        anschrift.setStrasse(getImportClaimData().getEhp1strasse());
        anschrift.setHausnummer(getImportClaimData().getEhp1hausnr());
        anschrift.setPostfachnummer(getImportClaimData().getEhp1postf());
        anschrift.setPlz(getImportClaimData().getEhp1plz());
        anschrift.setOrt(getImportClaimData().getEhp1ort());
        anschrift.setWohnungsgeber(getImportClaimData().getEhp1whgeber());
        anschrift.setStaat(XoevCodeGDSStaatenTyp3.DEUTSCHLAND.getDescriptor());

        return anschrift;
    }

    private XoevGeschlecht supplyGeschlecht(String ehp1geschl) {

        switch (ehp1geschl.trim().toUpperCase()) {

            case "M":
                return XoevGeschlecht.MAENNLICH;
            case "W":
                return XoevGeschlecht.WEIBLICH;
            default:
                return XoevGeschlecht.UNBEKANNT;

        }
    }

    public static String dateFormatConverter(String dateToConvert, String outputFormat) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);

        LocalDate date = LocalDate.parse(dateToConvert, inputFormatter);
        return date.format(outputFormatter);

    }

    private LocalDate getLocalDate(String dateToConvert) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateToConvert, inputFormatter);

    }

    /**
     * @param formatedDate "yyyy-MM-dd"
     * @return XMLGregorianCalendar
     * @throws DatatypeConfigurationException
     */
    private XMLGregorianCalendar getXMLGregorianCalendar(String formatedDate) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(formatedDate);
    }

}
