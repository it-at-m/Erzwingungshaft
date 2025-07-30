package de.muenchen.eh.kvue;

import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import de.muenchen.xjustiz.xjustiz0500straf.content.FachdatenContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.GrunddatenContent;
import de.muenchen.xjustiz.xjustiz0500straf.content.fachdaten.StrasseHausnummer;
import de.muenchen.xjustiz.xjustiz0500straf.content.fachdaten.Tatort;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Anschrift;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Beteiligung;
import de.muenchen.xjustiz.xjustiz0500straf.content.grunddaten.verfahrensdaten.beteiligung.Rolle;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSRollenbezeichnungTyp3;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSStaatenTyp3;
import de.muenchen.xjustiz.xoev.codelisten.XoevGeschlecht;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class EhCaseContentContainerFactory {

    private final EhCase ehCase;

    public ContentContainer supplyContentContainer() {

        return new ContentContainer(supplyFachdatenContent(), supplyGrunddatenContent());
    }

    private FachdatenContent supplyFachdatenContent()  {

        FachdatenContent fachdatenContent = new FachdatenContent();

        var startDate = getLocalDate(getEhCase().getEhtatdatv());
        fachdatenContent.setAnfangsDatumUhrzeit(LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), Integer.parseInt(getEhCase().getEhtatstdv()), Integer.parseInt(getEhCase().getEhtatminv())));
        LocalDate endDate = getEhCase().getEhtatdatb().isBlank() ? startDate : getLocalDate(getEhCase().getEhtatdatb());
        fachdatenContent.setEndeDatumUhrzeit(LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), Integer.parseInt(getEhCase().getEhtatstdb()), Integer.parseInt(getEhCase().getEhtatminb())));

        Tatort tatortContent = new Tatort();
        tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getEhCase().getEhtatstr1(), getEhCase().getEhtathnr1()));
        if (!getEhCase().getEhtatstr2().isBlank())
            tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getEhCase().getEhtatstr2(), getEhCase().getEhtathnr2()));
        tatortContent.setOrt(getEhCase().getEhtatort());

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


    private void setPersonalData(Beteiligung ehBetroffener) {

        var person = ehBetroffener.generateBeteiligter().generateNatuerlichePerson();
        person.setGeschlecht(supplyGeschlecht(getEhCase().getEhp1geschl()));

        var name = person.generateVollerName();
        name.setVorname(getEhCase().getEhp1vorname());
        name.setNachname(getEhCase().getEhp1name());
        name.setTitel(getEhCase().getEhp1akad());
        name.setNamensvorsatz(getEhCase().getEhp1nambest());
        name.setGeburtsname(getEhCase().getEhp1gebname());

        var geburt = person.generateGeburt();
        geburt.setGeburtsdatum(dateFormatConverter(getEhCase().getEhp1gebdat()));
        geburt.setGeburtsort(getEhCase().getEhp1gebort());

        person.addAnschrift(setAddress());
    }

    private Anschrift setAddress() {

        var anschrift = new Anschrift();

        anschrift.setAnschriftenzusatz(getEhCase().getEhp1zusatz());
        anschrift.setStrasse(getEhCase().getEhp1strasse());
        anschrift.setHausnummer(getEhCase().getEhp1hausnr());
        anschrift.setPostfachnummer(getEhCase().getEhp1postf());
        anschrift.setPlz(getEhCase().getEhp1plz());
        anschrift.setOrt(getEhCase().getEhp1ort());
        anschrift.setWohnungsgeber(getEhCase().getEhp1whgeber());
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

    private String dateFormatConverter(String dateToConvert) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = LocalDate.parse(dateToConvert, inputFormatter);
        return date.format(outputFormatter);

    }
    private LocalDate getLocalDate(String dateToConvert) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateToConvert, inputFormatter);

    }


}
