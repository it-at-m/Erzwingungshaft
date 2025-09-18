package de.muenchen.eh.kvue.claim;

import de.muenchen.eh.log.db.entity.ImportEntity;
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

    private final ClaimData ehClaimData;
    private final ImportEntity metadata;

    public ContentContainer supplyContentContainer() throws DatatypeConfigurationException {

        return new ContentContainer(supplyFachdatenContent(), supplyGrunddatenContent(), supplySchriftgutContent());
    }

    private FachdatenContent supplyFachdatenContent()  {

        FachdatenContent fachdatenContent = new FachdatenContent();

        var startDate = getLocalDate(getEhClaimData().getEhtatdatv());
        fachdatenContent.setAnfangsDatumUhrzeit(LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), Integer.parseInt(getEhClaimData().getEhtatstdv()), Integer.parseInt(getEhClaimData().getEhtatminv())));
        LocalDate endDate = getEhClaimData().getEhtatdatb().isBlank() ? startDate : getLocalDate(getEhClaimData().getEhtatdatb());
        fachdatenContent.setEndeDatumUhrzeit(LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), Integer.parseInt(getEhClaimData().getEhtatstdb()), Integer.parseInt(getEhClaimData().getEhtatminb())));

        Tatort tatortContent = new Tatort();
        tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getEhClaimData().getEhtatstr1(), getEhClaimData().getEhtathnr1()));
        if (!getEhClaimData().getEhtatstr2().isBlank())
            tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getEhClaimData().getEhtatstr2(), getEhClaimData().getEhtathnr2()));
        tatortContent.setOrt(getEhClaimData().getEhtatort());

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

        Laufzeit laufzeit = new Laufzeit(getXMLGregorianCalendar("2099-01-01"), getXMLGregorianCalendar("2099-12-31"));
        AnwendungspezifischeErweiterung anwendungspezifischeErweiterung = new AnwendungspezifischeErweiterung("TODO", "TODO");
        AktenzeichenStrukuriert aktenzeichenStrukuriert = new AktenzeichenStrukuriert("TODO", "TODO","TODO","1", "TODO");
        FachspezifischeDatenAkte fachspezifischeDatenAkte = new FachspezifischeDatenAkte(aktenzeichenStrukuriert);

        Akte akte = new Akte(identifikationAkte, laufzeit, anwendungspezifischeErweiterung, fachspezifischeDatenAkte);
        akten.add(akte);

        return akten;
    }

    private void setPersonalData(Beteiligung ehBetroffener) {

        var person = ehBetroffener.generateBeteiligter().generateNatuerlichePerson();
        person.setGeschlecht(supplyGeschlecht(getEhClaimData().getEhp1geschl()));

        var name = person.generateVollerName();
        name.setVorname(getEhClaimData().getEhp1vorname());
        name.setNachname(getEhClaimData().getEhp1name());
        name.setTitel(getEhClaimData().getEhp1akad());
        name.setNamensvorsatz(getEhClaimData().getEhp1nambest());
        name.setGeburtsname(getEhClaimData().getEhp1gebname());

        var geburt = person.generateGeburt();
        geburt.setGeburtsdatum(dateFormatConverter(getEhClaimData().getEhp1gebdat(), "yyyy-MM-dd"));
        geburt.setGeburtsort(getEhClaimData().getEhp1gebort());

        person.addAnschrift(setAddress());
    }

    private Anschrift setAddress() {

        var anschrift = new Anschrift();

        anschrift.setAnschriftenzusatz(getEhClaimData().getEhp1zusatz());
        anschrift.setStrasse(getEhClaimData().getEhp1strasse());
        anschrift.setHausnummer(getEhClaimData().getEhp1hausnr());
        anschrift.setPostfachnummer(getEhClaimData().getEhp1postf());
        anschrift.setPlz(getEhClaimData().getEhp1plz());
        anschrift.setOrt(getEhClaimData().getEhp1ort());
        anschrift.setWohnungsgeber(getEhClaimData().getEhp1whgeber());
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
