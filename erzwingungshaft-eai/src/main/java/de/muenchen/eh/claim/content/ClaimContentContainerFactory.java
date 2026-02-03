package de.muenchen.eh.claim.content;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.ImportClaimData;
import de.muenchen.eh.common.FileNameUtils;
import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.db.entity.ClaimImport;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.log.DocumentType;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import de.muenchen.xjustiz.xjustiz0500straf.content.FachdatenContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.GrunddatenContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.NachrichtenkopfContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.SchriftgutContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.fachdaten.StrasseHausnummer;
import de.muenchen.xjustiz.xjustiz0500straf.content.fachdaten.Tatort;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Anschrift;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Beteiligung;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Rolle;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.instanzdaten.Aktenzeichen;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.instanzdaten.Instanztype;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.Akte;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.Datei;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.Dokument;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.FachspezifischeDatenAkte;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.FachspezifischeDatenDokument;
import de.muenchen.xjustiz.xjustiz0500straf.content.schriftgutobjekte.Identifikation;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSRollenbezeichnungTyp3;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSStaatenTyp3;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ClaimContentContainerFactory {

    public static final String DD_MM_YYYY_DOT = "dd.MM.yyyy";
    public static final String YYYY_MM_DD_HYPHEN = "yyyy-MM-dd";
    public static final String EUROPE_BERLIN = "Europe/Berlin";

    private final ClaimContentWrapper claimContentWrapper;
    private final ClaimDocumentRepository claimDocumentRepository;

    private final ImportClaimData importClaimData;
    private final ClaimImport claimImport;

    public ClaimContentContainerFactory(ClaimContentWrapper claimContentWrapper, ClaimDocumentRepository claimDocumentRepository) {

        this.claimContentWrapper = claimContentWrapper;
        this.claimDocumentRepository = claimDocumentRepository;

        this.claimImport = claimContentWrapper.getClaimImport();
        this.importClaimData = claimContentWrapper.getEhImportClaimData();
    }

    public ContentContainer supplyContentContainer() throws DatatypeConfigurationException, ParseException {
        log.debug("Data for xml generation: {}", claimContentWrapper.getEhImportClaimData().toString());
        ContentContainer content = new ContentContainer(supplyNachrichtenKopfContent(), supplyFachdatenContent(), supplyGrunddatenContent(),
                supplySchriftgutContent());
        log.debug("Content for xml generation: {}", content);
        return content;
    }

    private NachrichtenkopfContent supplyNachrichtenKopfContent() {
        NachrichtenkopfContent nachrichtenkopfContent = new NachrichtenkopfContent();
        nachrichtenkopfContent.setAktenzeichen(getClaimImport().getKassenzeichen());
        return nachrichtenkopfContent;
    }


    private FachdatenContent supplyFachdatenContent() throws DatatypeConfigurationException, ParseException {

        FachdatenContent fachdatenContent = new FachdatenContent();

        fachdatenContent.setErlassdatum(ContentContainerFactoryHelper.convertLocalDate(claimContentWrapper.getEhImportClaimData().getEhbdat(), DD_MM_YYYY_DOT));
        fachdatenContent.setRechtskraftdatum(
                ContentContainerFactoryHelper.convertXMLGregorianCalendar(claimContentWrapper.getEhImportClaimData().getEhbrkdat(), DD_MM_YYYY_DOT, EUROPE_BERLIN));

        fachdatenContent.setAnfangDatum(ContentContainerFactoryHelper.convertTargetStringFormat(getImportClaimData().getEhtatdatv(), DD_MM_YYYY_DOT, YYYY_MM_DD_HYPHEN));
        fachdatenContent.setAnfangUhrzeit(ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat(getImportClaimData().getEhtatstdv(), getImportClaimData().getEhtatminv(), null, Locale.GERMANY));
        fachdatenContent.setEndeDatum(ContentContainerFactoryHelper.convertTargetStringFormat(getImportClaimData().getEhtatdatb(), DD_MM_YYYY_DOT, YYYY_MM_DD_HYPHEN));
        fachdatenContent.setEndeUhrzeit(ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat(getImportClaimData().getEhtatstdb(), getImportClaimData().getEhtatminb(), null, Locale.GERMANY));

        Tatort tatortContent = new Tatort();
        tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getImportClaimData().getEhtatstr1(), getImportClaimData().getEhtathnr1()));
        if (!getImportClaimData().getEhtatstr2().isBlank())
            tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getImportClaimData().getEhtatstr2(), getImportClaimData().getEhtathnr2()));
        tatortContent.setOrt(getImportClaimData().getEhtatort());

        //      tatortContent.setOrtsbeschreibung(getEhtat..);

        fachdatenContent.getTatorte().add(tatortContent);

        Optional<Double> fine = Optional.empty();
        if (getClaimContentWrapper().getEhImportClaimData().getEhverwbetrag() != null
                && !getClaimContentWrapper().getEhImportClaimData().getEhverwbetrag().isBlank())
            fine = Optional
                    .of(new BigDecimal(getClaimContentWrapper().getEhImportClaimData().getEhverwbetrag()).divide(new BigDecimal(100)).doubleValue());

        Optional<Double> totalFine = Optional.empty();
        if (getClaimContentWrapper().getEhImportClaimData().getEhgesbetr1() != null
                && !getClaimContentWrapper().getEhImportClaimData().getEhgesbetr1().isBlank())
            totalFine = Optional
                    .of(new BigDecimal(getClaimContentWrapper().getEhImportClaimData().getEhgesbetr1()).divide(new BigDecimal(100)).doubleValue());

        fine.ifPresent(fachdatenContent::setGeldbusse);

        if (fine.isPresent()) {
            final Double f = fine.get();
            totalFine.ifPresent(t -> fachdatenContent.setAuslagen(t - f));
        }

        return fachdatenContent;
    }

    private GrunddatenContent supplyGrunddatenContent() {

        Beteiligung ehBetroffener = new Beteiligung();
        var rolle = new Rolle();

        rolle.setRollenbezeichnung(XoevCodeGDSRollenbezeichnungTyp3.BETROFFENER.getDescriptor());
        ehBetroffener.addRolle(rolle);

        setPersonalData(ehBetroffener);

        Map<Instanztype, Aktenzeichen> auswahlInstanzbehoerden = new TreeMap<>();
        auswahlInstanzbehoerden.put(Instanztype.GERICHT, new Aktenzeichen("neu"));
        auswahlInstanzbehoerden.put(Instanztype.BETEILIGTER, new Aktenzeichen(claimContentWrapper.getClaimImport().getOutputDirectory()));

        return new GrunddatenContent(new ArrayList<>(List.of(ehBetroffener)), auswahlInstanzbehoerden);
    }

    private SchriftgutContent supplySchriftgutContent() throws DatatypeConfigurationException {
        SchriftgutContent schriftgutContent = new SchriftgutContent();

        schriftgutContent.setDokumente(Optional.of(createDocuments()));
        schriftgutContent.setAkten(Optional.of(createAkte(BigInteger.valueOf(1))));

        return schriftgutContent;
    }

    private List<Dokument> createDocuments() {

        List<Dokument> documents = new ArrayList<>();
        List<ClaimDocument> claimDocuments = claimDocumentRepository.findByClaimImportIdOrderByDocumentType(claimImport.getId());

        AtomicLong count = new AtomicLong(1);
        claimDocuments.forEach(document -> {

            var uuidIdentAntrag = UUID.randomUUID().toString();
            List<Datei> antraege = new ArrayList<>();

            var fileName = FileNameUtils.getFileName(document.getFileName());
            long index = count.getAndIncrement();
            Identifikation identifikationAntrag = new Identifikation(uuidIdentAntrag, BigInteger.valueOf(index));
            Datei file = new Datei(fileName, BigInteger.valueOf(1));
            antraege.add(file);
            FachspezifischeDatenDokument fachspezifischeDatenDokumentAntrag = new FachspezifischeDatenDokument(
                    FileNameUtils.getGdsDokumentenklasse(DocumentType.fromDescriptor(document.getDocumentType())),
                    uuidIdentAntrag.concat("_").concat(fileName), antraege);
            documents.add(new Dokument(identifikationAntrag, fachspezifischeDatenDokumentAntrag));

        });

        return documents;
    }

    private List<Akte> createAkte(BigInteger nummer) throws DatatypeConfigurationException {

        var uuidIdentAkte = UUID.randomUUID().toString();

        List<Akte> akten = new ArrayList<>();

        Identifikation identifikationAkte = new Identifikation(uuidIdentAkte, nummer);
        FachspezifischeDatenAkte fachspezifischeDatenAkte = FachspezifischeDatenAkte.builder().choiceFreitext("-", false).build();
        Akte akte = new Akte(identifikationAkte, null, null, fachspezifischeDatenAkte);

        akten.add(akte);

        return akten;
    }

    private void setPersonalData(Beteiligung ehBetroffener) {

        var person = ehBetroffener.generateBeteiligter().generateNatuerlichePerson();
        person.setGeschlecht(ContentContainerFactoryHelper.supplyXoevGeschlecht(getImportClaimData().getEhp1geschl()));

        var name = person.generateVollerName();
        name.setVorname(getImportClaimData().getEhp1vorname());
        name.setNachname(getImportClaimData().getEhp1name());
        name.setTitel(getImportClaimData().getEhp1akad());
        name.setNamensvorsatz(getImportClaimData().getEhp1nambest());
        name.setGeburtsname(getImportClaimData().getEhp1gebname());

        var geburt = person.generateGeburt();
        geburt.setGeburtsdatum(ContentContainerFactoryHelper.convertTargetStringFormat(getImportClaimData().getEhp1gebdat(), DD_MM_YYYY_DOT, YYYY_MM_DD_HYPHEN));
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


}
