package de.muenchen.eh.kvue;

import de.muenchen.eh.XJustizContentProvider;
import de.muenchen.xjustiz.xjustiz0500straf.content.ContentContainer;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Log4j2
@Data
@CsvRecord(separator = ";", skipFirstLine = true)
public class Beteiligter implements XJustizContentProvider {

    @DataField(pos = 1, trim = true)
    private String satzl;
    @DataField(pos = 2, trim = true)
    private String ehowinr;
    @DataField(pos = 3, trim = true)
    private String ehkennz;
    @DataField(pos = 4, trim = true)
    private String ehfzart;
    @DataField(pos = 5, trim = true)
    private String ehfzmarke;
    @DataField(pos = 6, trim = true)
    private String ehfzfarbe;
    @DataField(pos = 7, trim = true)
    private String ehnation;
    @DataField(pos = 8, trim = true)
    private String ehnatmverf;
    @DataField(pos = 9, trim = true)
    private String ehtatort;
    @DataField(pos = 10, trim = true)
    private String ehtatstr1;
    @DataField(pos = 11, trim = true)
    private String ehtathnr1;
    @DataField(pos = 12, trim = true)
    private String ehtatstr2;
    @DataField(pos = 13, trim = true)
    private String ehtathnr2;
    @DataField(pos = 14, trim = true)
    private String ehtatkzgeg;
    @DataField(pos = 15, trim = true)
    private String ehtatpuhr;
    @DataField(pos = 16, trim = true)
    private String ehtatlima;
    @DataField(pos = 17, trim = true)
    private String ehtatlimanr;
    @DataField(pos = 18, trim = true)
    private String ehtatdatv;
    @DataField(pos = 19, trim = true)
    private String ehtatdatb;
    @DataField(pos = 20, trim = true)
    private String ehtatstdv;
    @DataField(pos = 21, trim = true)
    private String ehtatminv;
    @DataField(pos = 22, trim = true, required = true)
    private String ehtatstdb;
    @DataField(pos = 23, trim = true, required = true)
    private String ehtatminb;
    @DataField(pos = 24, trim = true)
    private String ehbusskz1;
    @DataField(pos = 25, trim = true)
    private String ehbusskz1txtt1;
    @DataField(pos = 26, trim = true)
    private String ehbusskz1txtt2;
    @DataField(pos = 27, trim = true)
    private String ehbusskz1txtt3;
    @DataField(pos = 28, trim = true)
    private String ehbusskz2;
    @DataField(pos = 29, trim = true)
    private String ehbusskz2txtt1;
    @DataField(pos = 30, trim = true)
    private String ehbusskz2txtt2;
    @DataField(pos = 31, trim = true)
    private String ehbusskz2txtt3;
    @DataField(pos = 32, trim = true)
    private String ehtbsl;
    @DataField(pos = 33, trim = true)
    private String ehtbsltxtt1;
    @DataField(pos = 34, trim = true)
    private String ehtbsltxtt2;
    @DataField(pos = 35, trim = true)
    private String ehtbsltxtt3;
    @DataField(pos = 36, trim = true)
    private String ehtatbtxtt1;
    @DataField(pos = 37, trim = true)
    private String ehtatbtxtt2;
    @DataField(pos = 38, trim = true)
    private String ehtatbtxtt3;
    @DataField(pos = 39, trim = true)
    private String ehzeuge;
    @DataField(pos = 40, trim = true)
    private String ehverwbetrag;
    @DataField(pos = 41, trim = true)
    private String ehkassz;
    @DataField(pos = 42, trim = true)
    private String ehsollstbetrag;
    @DataField(pos = 43, trim = true)
    private String ehsollausstxt1;
    @DataField(pos = 44, trim = true)
    private String ehsollstgeb;
    @DataField(pos = 45, trim = true)
    private String ehsollausstxt2;
    @DataField(pos = 46, trim = true)
    private String ehsollstausl;
    @DataField(pos = 47, trim = true)
    private String ehsollstgesamt;
    @DataField(pos = 48, trim = true)
    private String ehauslkasta;
    @DataField(pos = 49, trim = true)
    private String ehauslag;
    @DataField(pos = 50, trim = true)
    private String ehgesbetr1;
    @DataField(pos = 51, trim = true)
    private String ehgesbetr2;
    @DataField(pos = 52, trim = true)
    private String ehazpol;
    @DataField(pos = 53, trim = true)
    private String ehbeschart;
    @DataField(pos = 54, trim = true)
    private String ehbeschanord;
    @DataField(pos = 55, trim = true)
    private String ehanhdat;
    @DataField(pos = 56, trim = true)
    private String ehanzdat;
    @DataField(pos = 57, trim = true)
    private String ehmassn;
    @DataField(pos = 58, trim = true)
    private String ehstatustxt;
    @DataField(pos = 59, trim = true)
    private String ehgrund;
    @DataField(pos = 60, trim = true)
    private String ehgrundtxt;
    @DataField(pos = 61, trim = true)
    private String ehpzudat;
    @DataField(pos = 62, trim = true)
    private String ehpzuart;
    @DataField(pos = 63, trim = true)
    private String ehpzupagnr;
    @DataField(pos = 64, trim = true)
    private String ehp1beschuldart;
    @DataField(pos = 65, trim = true)
    private String ehp1anrede;
    @DataField(pos = 66, trim = true)
    private String ehp1schluessel;
    @DataField(pos = 67, trim = true)
    private String ehp1name;
    @DataField(pos = 68, trim = true)
    private String ehp1vorname;
    @DataField(pos = 69, trim = true)
    private String ehp1nambest;
    @DataField(pos = 70, trim = true)
    private String ehp1gebname;
    @DataField(pos = 71, trim = true)
    private String ehp1gebnambest;
    @DataField(pos = 72, trim = true)
    private String ehp1gebort;
    @DataField(pos = 73, trim = true)
    private String ehp1gebdat;
    @DataField(pos = 74, trim = true)
    private String ehp1geschl;
    @DataField(pos = 75, trim = true)
    private String ehp1akad;
    @DataField(pos = 76, trim = true)
    private String ehp1land;
    @DataField(pos = 77, trim = true)
    private String ehp1plz;
    @DataField(pos = 78, trim = true)
    private String ehp1ort;
    @DataField(pos = 79, trim = true)
    private String ehp1strasse;
    @DataField(pos = 80, trim = true)
    private String ehp1hausnr;
    @DataField(pos = 81, trim = true)
    private String ehp1buchst;
    @DataField(pos = 82, trim = true)
    private String ehp1zusatz;
    @DataField(pos = 83, trim = true)
    private String ehp1teilnr;
    @DataField(pos = 84, trim = true)
    private String ehp1postf;
    @DataField(pos = 85, trim = true)
    private String ehp1whgeber;
    @DataField(pos = 86, trim = true)
    private String ehp2beschuldart;
    @DataField(pos = 87, trim = true)
    private String ehp2anrede;
    @DataField(pos = 88, trim = true)
    private String ehp2schluessel;
    @DataField(pos = 89, trim = true)
    private String ehp2name;
    @DataField(pos = 90, trim = true)
    private String ehp2vorname;
    @DataField(pos = 91, trim = true)
    private String ehp2nambest;
    @DataField(pos = 92, trim = true)
    private String ehp2gebname;
    @DataField(pos = 93, trim = true)
    private String ehp2gebnambest;
    @DataField(pos = 94, trim = true)
    private String ehp2gebort;
    @DataField(pos = 95, trim = true)
    private String ehp2gebdat;
    @DataField(pos = 96, trim = true)
    private String ehp2geschl;
    @DataField(pos = 97, trim = true)
    private String ehp2akad;
    @DataField(pos = 98, trim = true)
    private String ehp2land;
    @DataField(pos = 99, trim = true)
    private String ehp2plz;
    @DataField(pos = 100, trim = true)
    private String ehp2ort;
    @DataField(pos = 101, trim = true)
    private String ehp2strasse;
    @DataField(pos = 102, trim = true)
    private String ehp2hausnr;
    @DataField(pos = 103, trim = true)
    private String ehp2buchst;
    @DataField(pos = 104, trim = true)
    private String ehp2zusatz;
    @DataField(pos = 105, trim = true)
    private String ehp2teilnr;
    @DataField(pos = 106, trim = true)
    private String ehp2postf;
    @DataField(pos = 107, trim = true)
    private String ehp2whgeber;
    @DataField(pos = 108, trim = true)
    private String ehp3beschuldart;
    @DataField(pos = 109, trim = true)
    private String ehp3anrede;
    @DataField(pos = 110, trim = true)
    private String ehp3schluessel;
    @DataField(pos = 111, trim = true)
    private String ehp3name;
    @DataField(pos = 112, trim = true)
    private String ehp3vorname;
    @DataField(pos = 113, trim = true)
    private String ehp3nambest;
    @DataField(pos = 114, trim = true)
    private String ehp3gebname;
    @DataField(pos = 115, trim = true)
    private String ehp3gebnambest;
    @DataField(pos = 116, trim = true)
    private String ehp3gebort;
    @DataField(pos = 117, trim = true)
    private String ehp3gebdat;
    @DataField(pos = 118, trim = true)
    private String ehp3geschl;
    @DataField(pos = 119, trim = true)
    private String ehp3akad;
    @DataField(pos = 120, trim = true)
    private String ehp3land;
    @DataField(pos = 121, trim = true)
    private String ehp3plz;
    @DataField(pos = 122, trim = true)
    private String ehp3ort;
    @DataField(pos = 123, trim = true)
    private String ehp3strasse;
    @DataField(pos = 124, trim = true)
    private String ehp3hausnr;
    @DataField(pos = 125, trim = true)
    private String ehp3buchst;
    @DataField(pos = 126, trim = true)
    private String ehp3zusatz;
    @DataField(pos = 127, trim = true)
    private String ehp3teilnr;
    @DataField(pos = 128, trim = true)
    private String ehp3postf;
    @DataField(pos = 129, trim = true)
    private String ehp3whgeber;
    @DataField(pos = 130, trim = true)
    private String ehbdat;
    @DataField(pos = 131, trim = true)
    private String ehbzustdat;
    @DataField(pos = 132, trim = true)
    private String ehbzustst;
    @DataField(pos = 133, trim = true)
    private String ehbrkdat;
    @DataField(pos = 134, trim = true)
    private String ehbrbehdat;
    @DataField(pos = 135, trim = true)
    private String ehbwean;
    @DataField(pos = 136, trim = true)
    private String ehbwegew;
    @DataField(pos = 137, trim = true)
    private String ehbabju;
    @DataField(pos = 138, trim = true)
    private String ehbruju;
    @DataField(pos = 139, trim = true)
    private String ehbabslju;
    @DataField(pos = 140, trim = true)
    private String ehb2dat;
    @DataField(pos = 141, trim = true)
    private String ehb2zustdat;
    @DataField(pos = 142, trim = true)
    private String ehb2zustst;
    @DataField(pos = 143, trim = true)
    private String ehb2rkdat;
    @DataField(pos = 144, trim = true)
    private String ehb2einspr;
    @DataField(pos = 145, trim = true)
    private String ehb2wean;
    @DataField(pos = 146, trim = true)
    private String ehb2wegew;
    @DataField(pos = 147, trim = true)
    private String ehb2abju;
    @DataField(pos = 148, trim = true)
    private String ehb2ruju;
    @DataField(pos = 149, trim = true)
    private String ehb2abslju;
    @DataField(pos = 150, trim = true)
    private String ehb3dat;
    @DataField(pos = 151, trim = true)
    private String ehb3zusatdat;
    @DataField(pos = 152, trim = true)
    private String ehb3zustst;
    @DataField(pos = 153, trim = true)
    private String ehb3rkdat;
    @DataField(pos = 154, trim = true)
    private String ehb3einspr;
    @DataField(pos = 155, trim = true)
    private String ehb3wean;
    @DataField(pos = 156, trim = true)
    private String ehb3wegew;
    @DataField(pos = 157, trim = true)
    private String ehb3abju;
    @DataField(pos = 158, trim = true)
    private String ehb3ruju;
    @DataField(pos = 159, trim = true)
    private String ehb3abslju;
    @DataField(pos = 160, trim = true)
    private String ehgeschwmess;
    @DataField(pos = 161, trim = true)
    private String ehgeschwzul;
    @DataField(pos = 162, trim = true)
    private String ehmesstol;
    @DataField(pos = 163, trim = true)
    private String ehgeschwmin;
    @DataField(pos = 164, trim = true)
    private String ehgeschwueb;
    @DataField(pos = 165, trim = true)
    private String ehfilmnr;
    @DataField(pos = 166, trim = true)
    private String ehfilmdat;
    @DataField(pos = 167, trim = true)
    private String ehfilmbildpos;
    @DataField(pos = 168, trim = true)
    private String ehbewfoto;
    @DataField(pos = 169, trim = true)
    private String ehbewfaschr;
    @DataField(pos = 170, trim = true)
    private String ehbewlischr;
    @DataField(pos = 171, trim = true)
    private String ehbewfrontf;
    @DataField(pos = 172, trim = true)
    private String ehbewradar;
    @DataField(pos = 173, trim = true)
    private String ehbewgutacht;
    @DataField(pos = 174, trim = true)
    private String ehbewangbetr;
    @DataField(pos = 175, trim = true)
    private String ehpunkte;
    @DataField(pos = 176, trim = true)
    private String ehfahrverbot;
    @DataField(pos = 177, trim = true)
    private String ehsbname;
    @DataField(pos = 178, trim = true)
    private String ehsbzimmer;
    @DataField(pos = 179, trim = true)
    private String ehsbtel;
    @DataField(pos = 180, trim = true)
    private String ehlinefeed;

    @Override
    public ContentContainer supplyXJustizRequestContent() {

        BeteiligterContentContainerFactory contentContainerFactory = new BeteiligterContentContainerFactory(this);
        return contentContainerFactory.supplyContentContainer();
    }

}
