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
public class BeteiligterContentContainerFactory {

    private final Beteiligter beteiligter;

    public ContentContainer supplyContentContainer() {

        return new ContentContainer(supplyFachdatenContent(), supplyGrunddatenContent());
    }

    private FachdatenContent supplyFachdatenContent() {

        FachdatenContent fachdatenContent = new FachdatenContent();

        var startDate = getLocalDate(getBeteiligter().getEhtatdatv());
        fachdatenContent.setAnfangsDatumUhrzeit(LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), Integer.valueOf(getBeteiligter().getEhtatstdv()), Integer.valueOf(getBeteiligter().getEhtatminv())));
        LocalDate endDate = getBeteiligter().getEhtatdatb().isBlank() ? startDate : getLocalDate(getBeteiligter().getEhtatdatb());
        fachdatenContent.setEndeDatumUhrzeit(LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), Integer.valueOf(getBeteiligter().getEhtatstdb()), Integer.valueOf(getBeteiligter().getEhtatminb())));

        Tatort tatortContent = new Tatort();
        tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getBeteiligter().getEhtatstr1(), getBeteiligter().getEhtathnr1()));
        if (!getBeteiligter().getEhtatstr2().isBlank())
            tatortContent.getStrasseHausnummer().add(new StrasseHausnummer(getBeteiligter().getEhtatstr2(), getBeteiligter().getEhtathnr2()));
        tatortContent.setOrt(getBeteiligter().getEhtatort());

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
        person.setGeschlecht(supplyGeschlecht(getBeteiligter().getEhp1geschl()));

        var name = person.generateVollerName();
        name.setVorname(getBeteiligter().getEhp1vorname());
        name.setNachname(getBeteiligter().getEhp1name());
        name.setTitel(getBeteiligter().getEhp1akad());
        name.setNamensvorsatz(getBeteiligter().getEhp1nambest());
        name.setGeburtsname(getBeteiligter().getEhp1gebname());

        var geburt = person.generateGeburt();
        geburt.setGeburtsdatum(dateFormatConverter(getBeteiligter().getEhp1gebdat()));
        geburt.setGeburtsort(getBeteiligter().getEhp1gebort());

        person.addAnschrift(setAddress());
    }

    private Anschrift setAddress() {

        var anschrift = new Anschrift();

        anschrift.setAnschriftenzusatz(getBeteiligter().getEhp1zusatz());
        anschrift.setStrasse(getBeteiligter().getEhp1strasse());
        anschrift.setHausnummer(getBeteiligter().getEhp1hausnr());
        anschrift.setPostfachnummer(getBeteiligter().getEhp1postf());
        anschrift.setPlz(getBeteiligter().getEhp1plz());
        anschrift.setOrt(getBeteiligter().getEhp1ort());
        anschrift.setWohnungsgeber(getBeteiligter().getEhp1whgeber());
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
