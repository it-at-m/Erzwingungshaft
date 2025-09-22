package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.entity.ClaimImport;
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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class ClaimContentContainerFactory {

    private final ImportClaimData ehImportClaimData;
    private final ClaimImport metadata;

    public ContentContainer supplyContentContainer() throws DatatypeConfigurationException {

        return new ContentContainer(supplyFachdatenContent(), supplyGrunddatenContent(), supplySchriftgutContent());
    }

    private FachdatenContent supplyFachdatenContent()  {

        FachdatenContent fachdatenContent = new FachdatenContent();

        var startDate = getLocalDate(getEhImportClaimData().getEhtatdatv());
        fachdatenContent.setAnfangsDatumUhrzeit(LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), Integer.parseInt(getEhImportClaimData().getEhtatstdv()), Integer.parseInt(getEhImportClaimData().getEhtatminv())));
        LocalDate endDate = getEhImportClaimData().getEhtatdatb().isBlank() ? startDate : getLocalDate(getEhImportClaimData().getEhtatdatb());
        fachdatenContent.setEndeDatumUhrzeit(LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), Integer.parseInt(getEhImportClaimData().getEhtatstdb()), Integer.parseInt(getEhImportClaimData().getEhtatminb())));

        Tatort tatortContent = new Tatort();
        tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getEhImportClaimData().getEhtatstr1(), getEhImportClaimData().getEhtathnr1()));
        if (!getEhImportClaimData().getEhtatstr2().isBlank())
            tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getEhImportClaimData().getEhtatstr2(), getEhImportClaimData().getEhtathnr2()));
        tatortContent.setOrt(getEhImportClaimData().getEhtatort());

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

 //       schriftgutContent.setAnschreiben(Optional.of(UUID.randomUUID().toString()));
        schriftgutContent.setDokumente(Optional.of(createDocuments()));
        schriftgutContent.setAkten(Optional.of(createAkte()));

        return schriftgutContent;
    }

    private List<Dokument> createDocuments() {

        List<Dokument> documents = new ArrayList<>();

//        @TODO
//        - Hier createDocuments ... müssen jetzt die Daten aus dem importierten Fall aus den KVUE Fällen rein. Die Daten müsssen aus der Route noch übergeben werden.
//        - Klären ob die FachspezifischenDaten aus der EAkte gebraucht werden damit keine xsd Parser Fehler auftritt (https://git.muenchen.de/km33/erzwingungshaft/datenmapping/-/blob/main/Mapping_Files/0500010_externAnJustiz_BBJ4.xml?ref_type=heads#L232)
//        - Prozesschritte 'Import Daten -> PDFs -> Fallbearbeitung' werden erst gestartet, wenn die vorhergehnde Route fertig ist ?
//        -  KVUE-Content-Daten in 'application-eh-process' aus Datei oder einfacher aus Datenbank auslesen und File nur zur Dokumentation belassen ?

        // Antrag
        var uuidIdentAntrag = UUID.randomUUID().toString();
        List<Datei> antraege = new ArrayList<>();
        var antragDateiName = "1000809085_5793341761427_20240807_EH.pdf";

        Identifikation identifikationAntrag = new Identifikation(uuidIdentAntrag, BigInteger.valueOf(1));
        Datei antrag = new Datei(antragDateiName, BigInteger.valueOf(1));
        antraege.add(antrag);
        FachspezifischeDatenDokument fachspezifischeDatenDokumentAntrag = new FachspezifischeDatenDokument(XoevCodeGDSDokumentklasse.ANTRAG, uuidIdentAntrag.concat("_").concat(antragDateiName), antraege);
        documents.add(new Dokument(identifikationAntrag, fachspezifischeDatenDokumentAntrag));

        // Bescheid
        var uuidIdentBescheid =  UUID.randomUUID().toString();
        List<Datei> bescheide = new ArrayList<>();
        var bescheidDateiName = "1000809085_5793341761427_20240807_URB.pdf";

        Identifikation identifikationBescheid = new Identifikation(uuidIdentBescheid, BigInteger.valueOf(2));
        Datei Bescheid = new Datei(bescheidDateiName, BigInteger.valueOf(1));
        bescheide.add(Bescheid);
        FachspezifischeDatenDokument fachspezifischeDatenDokumentBescheid = new FachspezifischeDatenDokument(XoevCodeGDSDokumentklasse.BESCHEID, uuidIdentBescheid.concat("_").concat(bescheidDateiName), bescheide);
        documents.add(new Dokument(identifikationBescheid, fachspezifischeDatenDokumentBescheid));

        return documents;
    }

    private List<Akte> createAkte() throws DatatypeConfigurationException {

        var uuidIdentAkte =  UUID.randomUUID().toString();

        List<Akte> akten = new ArrayList<>();


        Identifikation identifikationAkte = new Identifikation(uuidIdentAkte, BigInteger.valueOf(1));

        // Xaver kann das Optional auch weggelassen werden ????
        Laufzeit laufzeit = new Laufzeit(getXMLGregorianCalendar("2099-01-01"), getXMLGregorianCalendar("2099-12-31"));

        // Xaver Optional kann weg ?
        AnwendungspezifischeErweiterung anwendungspezifischeErweiterung = new AnwendungspezifischeErweiterung("TODO", "TODO");

    //    Xaver    AktenzeichenFreitext   statt AktenzeichenStrukuriert ??
        AktenzeichenStrukuriert aktenzeichenStrukuriert = new AktenzeichenStrukuriert("TODO", "TODO","TODO","1", "TODO");

        FachspezifischeDatenAkte fachspezifischeDatenAkte = new FachspezifischeDatenAkte(aktenzeichenStrukuriert);


 //       Akte akte = new Akte(identifikationAkte, null, anwendungspezifischeErweiterung, fachspezifischeDatenAkte);
        Akte akte = new Akte(identifikationAkte, laufzeit, anwendungspezifischeErweiterung, fachspezifischeDatenAkte);
        akten.add(akte);

        return akten;
    }

    private void setPersonalData(Beteiligung ehBetroffener) {

        var person = ehBetroffener.generateBeteiligter().generateNatuerlichePerson();
        person.setGeschlecht(supplyGeschlecht(getEhImportClaimData().getEhp1geschl()));

        var name = person.generateVollerName();
        name.setVorname(getEhImportClaimData().getEhp1vorname());
        name.setNachname(getEhImportClaimData().getEhp1name());
        name.setTitel(getEhImportClaimData().getEhp1akad());
        name.setNamensvorsatz(getEhImportClaimData().getEhp1nambest());
        name.setGeburtsname(getEhImportClaimData().getEhp1gebname());

        var geburt = person.generateGeburt();
        geburt.setGeburtsdatum(dateFormatConverter(getEhImportClaimData().getEhp1gebdat(), "yyyy-MM-dd"));
        geburt.setGeburtsort(getEhImportClaimData().getEhp1gebort());

        person.addAnschrift(setAddress());
    }

    private Anschrift setAddress() {

        var anschrift = new Anschrift();

        anschrift.setAnschriftenzusatz(getEhImportClaimData().getEhp1zusatz());
        anschrift.setStrasse(getEhImportClaimData().getEhp1strasse());
        anschrift.setHausnummer(getEhImportClaimData().getEhp1hausnr());
        anschrift.setPostfachnummer(getEhImportClaimData().getEhp1postf());
        anschrift.setPlz(getEhImportClaimData().getEhp1plz());
        anschrift.setOrt(getEhImportClaimData().getEhp1ort());
        anschrift.setWohnungsgeber(getEhImportClaimData().getEhp1whgeber());
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
     *
     * @param formatedDate  "yyyy-MM-dd"
     * @return XMLGregorianCalendar
     * @throws DatatypeConfigurationException
     */
    private XMLGregorianCalendar getXMLGregorianCalendar(String formatedDate) throws DatatypeConfigurationException {
        return  DatatypeFactory.newInstance().newXMLGregorianCalendar(formatedDate);
    }


}
