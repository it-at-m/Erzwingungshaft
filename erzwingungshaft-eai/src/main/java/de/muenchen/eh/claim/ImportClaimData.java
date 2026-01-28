package de.muenchen.eh.claim;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

@Data
@FixedLengthRecord(length = 3542, paddingChar = ' ')
public class ImportClaimData {

    @DataField(pos = 1, length = 4, trim = true, align = "B")
    private String ehsatzlaenge;

    //     Tat-Daten
    @DataField(pos = 5, length = 1, trim = true, align = "B")
    private String ehowigb;
    @DataField(pos = 6, length = 3, trim = true, align = "B")
    private String ehowirga;
    @DataField(pos = 9, length = 1, trim = true, align = "B")
    private String ehowiblank1;
    @DataField(pos = 10, length = 3, trim = true, align = "B")
    private String ehowiowi;
    @DataField(pos = 13, length = 1, trim = true, align = "B")
    private String ehowiblank2;
    @DataField(pos = 14, length = 5, trim = true, align = "B")
    private String ehowilfnr;

    @DataField(pos = 19, length = 1, trim = true, align = "B")
    private String ehowischr;
    @DataField(pos = 20, length = 2, trim = true, align = "B")
    private String ehowijahr;
    @DataField(pos = 22, length = 30, trim = true, align = "B")
    private String ehowirichter;
    @DataField(pos = 52, length = 24, trim = true, align = "B")
    private String ehowititel;

    @DataField(pos = 76, length = 10, trim = true, align = "B")
    private String ehkennz;
    @DataField(pos = 86, length = 70, trim = true, align = "B")
    private String ehfzart;
    @DataField(pos = 156, length = 20, trim = true, align = "B")
    private String ehfzmarke;
    @DataField(pos = 176, length = 30, trim = true, align = "B")
    private String ehfzfarbe;
    @DataField(pos = 206, length = 47, trim = true, align = "B")
    private String ehnation;
    @DataField(pos = 253, length = 1, trim = true, align = "B")
    private String ehnatmverf;
    @DataField(pos = 254, length = 26, trim = true, align = "B")
    private String ehtatort;
    @DataField(pos = 280, length = 25, trim = true, align = "B")
    private String ehtatstr1;
    @DataField(pos = 305, length = 4, trim = true, align = "B")
    private String ehtathnr1;
    @DataField(pos = 309, length = 25, trim = true, align = "B")
    private String ehtatstr2;
    @DataField(pos = 334, length = 4, trim = true, align = "B")
    private String ehtathnr2;
    @DataField(pos = 338, length = 1, trim = true, align = "B")
    private String ehtatkzgeg;
    @DataField(pos = 339, length = 1, trim = true, align = "B")
    private String ehtatpuhr;
    @DataField(pos = 340, length = 1, trim = true, align = "B")
    private String ehtatlima;
    @DataField(pos = 341, length = 5, trim = true, align = "B")
    private String ehtatlimanr;
    @DataField(pos = 346, length = 10, trim = true, align = "B")
    private String ehtatdatv;
    @DataField(pos = 356, length = 10, trim = true, align = "B")
    private String ehtatdatb;
    @DataField(pos = 366, length = 2, trim = true, align = "B")
    private String ehtatstdv;
    @DataField(pos = 368, length = 2, trim = true, align = "B")
    private String ehtatminv;
    @DataField(pos = 370, length = 2, trim = true, align = "B")
    private String ehtatstdb;
    @DataField(pos = 372, length = 2, trim = true, align = "B")
    private String ehtatminb;
    @DataField(pos = 374, length = 5, trim = true, align = "B")
    private String ehbusskz1;

    @DataField(pos = 379, length = 54, trim = true, align = "B")
    private String ehbusskz1txtt1;
    @DataField(pos = 433, length = 54, trim = true, align = "B")
    private String ehbusskz1txtt2;
    @DataField(pos = 487, length = 54, trim = true, align = "B")
    private String ehbusskz1txtt3;
    @DataField(pos = 541, length = 5, trim = true, align = "B")
    private String ehbusskz2;

    @DataField(pos = 546, length = 54, trim = true, align = "B")
    private String ehbusskz2txtt1;
    @DataField(pos = 600, length = 54, trim = true, align = "B")
    private String ehbusskz2txtt2;
    @DataField(pos = 654, length = 54, trim = true, align = "B")
    private String ehbusskz2txtt3;
    @DataField(pos = 708, length = 3, trim = true, align = "B")
    private String ehtbsl;

    @DataField(pos = 711, length = 61, trim = true, align = "B")
    private String ehtbsltxtt1;
    @DataField(pos = 772, length = 61, trim = true, align = "B")
    private String ehtbsltxtt2;
    @DataField(pos = 833, length = 61, trim = true, align = "B")
    private String ehtbsltxtt3;

    @DataField(pos = 894, length = 70, trim = true, align = "B")
    private String ehtatbtxtt1;
    @DataField(pos = 964, length = 70, trim = true, align = "B")
    private String ehtatbtxtt2;
    @DataField(pos = 1034, length = 70, trim = true, align = "B")
    private String ehtatbtxtt3;
    @DataField(pos = 1104, length = 59, trim = true, align = "B")
    private String ehzeuge;

    //     Zahlungs-Daten
    @DataField(pos = 1163, length = 6, trim = true, align = "B")
    private String ehverwbetrag;
    @DataField(pos = 1169, length = 20, trim = true, align = "B")
    private String ehkassz;
    @DataField(pos = 1189, length = 6, trim = true, align = "B")
    private String ehsollstbetrag;
    @DataField(pos = 1195, length = 30, trim = true, align = "B")
    private String ehsollausstxt1;
    @DataField(pos = 1225, length = 6, trim = true, align = "B")
    private String ehsollstgeb;
    @DataField(pos = 1231, length = 30, trim = true, align = "B")
    private String ehsollausstxt2;
    @DataField(pos = 1261, length = 6, trim = true, align = "B")
    private String ehsollstausl;
    @DataField(pos = 1267, length = 6, trim = true, align = "B")
    private String ehsollstgesamt;
    @DataField(pos = 1273, length = 6, trim = true, align = "B")
    private String ehauslkasta;
    @DataField(pos = 1279, length = 6, trim = true, align = "B")
    private String ehauslag;
    @DataField(pos = 1285, length = 6, trim = true, align = "B")
    private String ehgesbetr1;
    @DataField(pos = 1291, length = 6, trim = true, align = "B")
    private String ehgesbetr2;

    //     Verfahrens-Daten
    @DataField(pos = 1297, length = 20, trim = true, align = "B")
    private String ehazpol;
    @DataField(pos = 1317, length = 34, trim = true, align = "B")
    private String ehbeschart;
    @DataField(pos = 1351, length = 65, trim = true, align = "B")
    private String ehbeschanord;
    @DataField(pos = 1416, length = 20, trim = true, align = "B")
    private String ehanhdat;
    @DataField(pos = 1436, length = 20, trim = true, align = "B")
    private String ehanzdat;
    @DataField(pos = 1456, length = 13, trim = true, align = "B")
    private String ehmassn;
    @DataField(pos = 1469, length = 65, trim = true, align = "B")
    private String ehstatustxt;
    @DataField(pos = 1534, length = 2, trim = true, align = "B")
    private String ehgrund;

    @DataField(pos = 1536, length = 55, trim = true, align = "B")
    private String ehgrundtxtt1;
    @DataField(pos = 1591, length = 55, trim = true, align = "B")
    private String ehgrundtxtt2;
    @DataField(pos = 1646, length = 55, trim = true, align = "B")
    private String ehgrundtxtt3;
    @DataField(pos = 1701, length = 55, trim = true, align = "B")
    private String ehgrundtxtt4;
    @DataField(pos = 1756, length = 10, trim = true, align = "B")
    private String ehpzudat;
    @DataField(pos = 1766, length = 1, trim = true, align = "B")
    private String ehpzuart;
    @DataField(pos = 1767, length = 11, trim = true, align = "B")
    private String ehpzupagnr;

    //     Personen-Daten
    //      Beschuldigter
    @DataField(pos = 1778, length = 35, trim = true, align = "B")
    private String ehp1beschuldart;
    @DataField(pos = 1813, length = 15, trim = true, align = "B")
    private String ehp1anrede;
    @DataField(pos = 1828, length = 1, trim = true, align = "B")
    private String ehp1schluessel;
    @DataField(pos = 1829, length = 45, trim = true, align = "B")
    private String ehp1name;
    @DataField(pos = 1874, length = 40, trim = true, align = "B")
    private String ehp1vorname;
    @DataField(pos = 1914, length = 45, trim = true, align = "B")
    private String ehp1nambest;
    @DataField(pos = 1959, length = 45, trim = true, align = "B")
    private String ehp1gebname;
    @DataField(pos = 2004, length = 45, trim = true, align = "B")
    private String ehp1gebnambest;
    @DataField(pos = 2049, length = 40, trim = true, align = "B")
    private String ehp1gebort;
    @DataField(pos = 2089, length = 10, trim = true, align = "B")
    private String ehp1gebdat;
    @DataField(pos = 2099, length = 1, trim = true, align = "B")
    private String ehp1geschl;
    @DataField(pos = 2100, length = 25, trim = true, align = "B")
    private String ehp1akad;
    @DataField(pos = 2125, length = 3, trim = true, align = "B")
    private String ehp1land;
    @DataField(pos = 2128, length = 7, trim = true, align = "B")
    private String ehp1plz;
    @DataField(pos = 2135, length = 26, trim = true, align = "B")
    private String ehp1ort;
    @DataField(pos = 2161, length = 25, trim = true, align = "B")
    private String ehp1strasse;
    @DataField(pos = 2186, length = 4, trim = true, align = "B")
    private String ehp1hausnr;
    @DataField(pos = 2190, length = 2, trim = true, align = "B")
    private String ehp1buchst;
    @DataField(pos = 2192, length = 7, trim = true, align = "B")
    private String ehp1zusatz;
    @DataField(pos = 2199, length = 5, trim = true, align = "B")
    private String ehp1teilnr;
    @DataField(pos = 2204, length = 10, trim = true, align = "B")
    private String ehp1postf;
    @DataField(pos = 2214, length = 26, trim = true, align = "B")
    private String ehp1whgeber;
    //      Jur. Vertreter
    @DataField(pos = 2240, length = 35, trim = true, align = "B")
    private String ehp2art;
    @DataField(pos = 2275, length = 15, trim = true, align = "B")
    private String ehp2anrede;
    @DataField(pos = 2290, length = 1, trim = true, align = "B")
    private String ehp2perssl;
    @DataField(pos = 2291, length = 45, trim = true, align = "B")
    private String ehp2name;
    @DataField(pos = 2336, length = 40, trim = true, align = "B")
    private String ehp2vorname;
    @DataField(pos = 2376, length = 45, trim = true, align = "B")
    private String ehp2nambest;
    @DataField(pos = 2421, length = 45, trim = true, align = "B")
    private String ehp2gebname;
    @DataField(pos = 2466, length = 45, trim = true, align = "B")
    private String ehp2gebnambest;
    @DataField(pos = 2511, length = 40, trim = true, align = "B")
    private String ehp2gebort;
    @DataField(pos = 2551, length = 10, trim = true, align = "B")
    private String ehp2gebdat;
    @DataField(pos = 2561, length = 1, trim = true, align = "B")
    private String ehp2geschl;
    @DataField(pos = 2562, length = 25, trim = true, align = "B")
    private String ehp2akad;
    @DataField(pos = 2587, length = 3, trim = true, align = "B")
    private String ehp2land;
    @DataField(pos = 2590, length = 7, trim = true, align = "B")
    private String ehp2plz;
    @DataField(pos = 2597, length = 26, trim = true, align = "B")
    private String ehp2ort;
    @DataField(pos = 2623, length = 25, trim = true, align = "B")
    private String ehp2strasse;
    @DataField(pos = 2648, length = 4, trim = true, align = "B")
    private String ehp2hausnr;
    @DataField(pos = 2652, length = 2, trim = true, align = "B")
    private String ehp2buchst;
    @DataField(pos = 2654, length = 7, trim = true, align = "B")
    private String ehp2zusatz;
    @DataField(pos = 2661, length = 5, trim = true, align = "B")
    private String ehp2teilnr;
    @DataField(pos = 2666, length = 10, trim = true, align = "B")
    private String ehp2postf;
    @DataField(pos = 2676, length = 26, trim = true, align = "B")
    private String ehp2whgeber;
    //      Gesetzl. Vertreter
    @DataField(pos = 2702, length = 35, trim = true, align = "B")
    private String ehp3art;
    @DataField(pos = 2737, length = 15, trim = true, align = "B")
    private String ehp3anrede;
    @DataField(pos = 2752, length = 1, trim = true, align = "B")
    private String ehp3perssl;
    @DataField(pos = 2753, length = 45, trim = true, align = "B")
    private String ehp3name;
    @DataField(pos = 2798, length = 40, trim = true, align = "B")
    private String ehp3vorname;
    @DataField(pos = 2838, length = 45, trim = true, align = "B")
    private String ehp3nambest;
    @DataField(pos = 2883, length = 45, trim = true, align = "B")
    private String ehp3gebname;
    @DataField(pos = 2928, length = 45, trim = true, align = "B")
    private String ehp3gebnambest;
    @DataField(pos = 2973, length = 40, trim = true, align = "B")
    private String ehp3gebort;
    @DataField(pos = 3013, length = 10, trim = true, align = "B")
    private String ehp3gebdat;
    @DataField(pos = 3023, length = 1, trim = true, align = "B")
    private String ehp3geschl;
    @DataField(pos = 3024, length = 25, trim = true, align = "B")
    private String ehp3akad;
    @DataField(pos = 3049, length = 3, trim = true, align = "B")
    private String ehp3land;
    @DataField(pos = 3052, length = 7, trim = true, align = "B")
    private String ehp3plz;
    @DataField(pos = 3059, length = 26, trim = true, align = "B")
    private String ehp3ort;
    @DataField(pos = 3085, length = 25, trim = true, align = "B")
    private String ehp3strasse;
    @DataField(pos = 3110, length = 4, trim = true, align = "B")
    private String ehp3hausnr;
    @DataField(pos = 3114, length = 2, trim = true, align = "B")
    private String ehp3buchst;
    @DataField(pos = 3116, length = 7, trim = true, align = "B")
    private String ehp3zusatz;
    @DataField(pos = 3123, length = 5, trim = true, align = "B")
    private String ehp3teilnr;
    @DataField(pos = 3128, length = 10, trim = true, align = "B")
    private String ehp3postf;
    @DataField(pos = 3138, length = 26, trim = true, align = "B")
    private String ehp3whgeber;

    //     Rechtsbehelfs-Daten
    @DataField(pos = 3164, length = 10, trim = true, align = "B")
    private String ehbdat;
    @DataField(pos = 3174, length = 10, trim = true, align = "B")
    private String ehbzustdat;
    @DataField(pos = 3184, length = 1, trim = true, align = "B")
    private String ehbzustst;
    @DataField(pos = 3185, length = 10, trim = true, align = "B")
    private String ehbrkdat;
    @DataField(pos = 3195, length = 10, trim = true, align = "B")
    private String ehbrbehdat;
    @DataField(pos = 3205, length = 10, trim = true, align = "B")
    private String ehbwean;
    @DataField(pos = 3215, length = 10, trim = true, align = "B")
    private String ehbwegew;
    @DataField(pos = 3225, length = 10, trim = true, align = "B")
    private String ehbabju;
    @DataField(pos = 3235, length = 10, trim = true, align = "B")
    private String ehbruju;
    @DataField(pos = 3245, length = 10, trim = true, align = "B")
    private String ehbabslju;

    //      2. Bescheid
    @DataField(pos = 3255, length = 10, trim = true, align = "B")
    private String ehb2dat;
    @DataField(pos = 3265, length = 10, trim = true, align = "B")
    private String ehb2zustdat;
    @DataField(pos = 3275, length = 1, trim = true, align = "B")
    private String ehb2zustst;
    @DataField(pos = 3276, length = 10, trim = true, align = "B")
    private String ehb2rkdat;
    @DataField(pos = 3286, length = 10, trim = true, align = "B")
    private String ehb2einspr;
    @DataField(pos = 3296, length = 10, trim = true, align = "B")
    private String ehb2wean;
    @DataField(pos = 3306, length = 10, trim = true, align = "B")
    private String ehb2wegew;
    @DataField(pos = 3316, length = 10, trim = true, align = "B")
    private String ehb2abju;
    @DataField(pos = 3326, length = 10, trim = true, align = "B")
    private String ehb2ruju;
    @DataField(pos = 3336, length = 10, trim = true, align = "B")
    private String ehb2abslju;

    //      3. Bescheid
    @DataField(pos = 3346, length = 10, trim = true, align = "B")
    private String ehb3dat;
    @DataField(pos = 3356, length = 10, trim = true, align = "B")
    private String ehb3zustdat;
    @DataField(pos = 3366, length = 1, trim = true, align = "B")
    private String ehb3zustst;
    @DataField(pos = 3367, length = 10, trim = true, align = "B")
    private String ehb3rkdat;
    @DataField(pos = 3377, length = 10, trim = true, align = "B")
    private String ehb3einspr;
    @DataField(pos = 3387, length = 10, trim = true, align = "B")
    private String ehb3wean;
    @DataField(pos = 3397, length = 10, trim = true, align = "B")
    private String ehb3wegew;
    @DataField(pos = 3407, length = 10, trim = true, align = "B")
    private String ehb3abju;
    @DataField(pos = 3417, length = 10, trim = true, align = "B")
    private String ehb3ruju;
    @DataField(pos = 3427, length = 10, trim = true, align = "B")
    private String ehb3abslju;

    //     Radar-Daten
    @DataField(pos = 3437, length = 3, trim = true, align = "B")
    private String ehgeschwmess;
    @DataField(pos = 3440, length = 3, trim = true, align = "B")
    private String ehgeschwzul;
    @DataField(pos = 3443, length = 3, trim = true, align = "B")
    private String ehmesstol;
    @DataField(pos = 3446, length = 3, trim = true, align = "B")
    private String ehgeschwmin;
    @DataField(pos = 3449, length = 3, trim = true, align = "B")
    private String ehgeschwueb;
    @DataField(pos = 3452, length = 4, trim = true, align = "B")
    private String ehfilmnr;
    @DataField(pos = 3456, length = 10, trim = true, align = "B")
    private String ehfilmdat;
    @DataField(pos = 3466, length = 10, trim = true, align = "B")
    private String ehfilmbildpos;
    @DataField(pos = 3476, length = 1, trim = true, align = "B")
    private String ehbewfoto;
    @DataField(pos = 3477, length = 1, trim = true, align = "B")
    private String ehbewfaschr;
    @DataField(pos = 3478, length = 1, trim = true, align = "B")
    private String ehbewlischr;
    @DataField(pos = 3479, length = 1, trim = true, align = "B")
    private String ehbewfrontf;
    @DataField(pos = 3480, length = 1, trim = true, align = "B")
    private String ehbewradar;
    @DataField(pos = 3481, length = 1, trim = true, align = "B")
    private String ehbewgutacht;
    @DataField(pos = 3482, length = 1, trim = true, align = "B")
    private String ehbewangbetr;
    @DataField(pos = 3483, length = 1, trim = true, align = "B")
    private String ehpunkte;
    @DataField(pos = 3484, length = 1, trim = true, align = "B")
    private String ehfahrverbot;

    //  Sachb.Daten
    @DataField(pos = 3485, length = 25, trim = true, align = "B")
    private String ehsbname;
    @DataField(pos = 3510, length = 10, trim = true, align = "B")
    private String ehsbzimmer;
    @DataField(pos = 3520, length = 10, trim = true, align = "B")
    private String ehsbtel;

    @DataField(pos = 3530, length = 3, trim = true, align = "B")
    private String ehlinefeed;

    // Geschaeftspartner
    @DataField(pos = 3533, length = 10, trim = true, align = "B")
    private String ehgpid;

}
