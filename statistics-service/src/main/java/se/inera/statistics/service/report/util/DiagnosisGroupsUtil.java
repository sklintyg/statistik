package se.inera.statistics.service.report.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.inera.statistics.service.report.model.DiagnosisGroup;

public final class DiagnosisGroupsUtil {

    private static final Map<String, List<DiagnosisGroup>> SUB_GROUPS = initSubGroups();

    private DiagnosisGroupsUtil() {
    }

    public static String getGroupIdForCode(String icd10Code) {
        for (Entry<String, List<DiagnosisGroup>> entry : SUB_GROUPS.entrySet()) {
            for (DiagnosisGroup diagnosisGroup : entry.getValue()) {
                if (diagnosisGroup.isCodeInGroup(icd10Code)) {
                    return entry.getKey();
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }

    public static DiagnosisGroup getSubGroupForCode(String icd10Code) {
        for (Entry<String, List<DiagnosisGroup>> entry : SUB_GROUPS.entrySet()) {
            for (DiagnosisGroup diagnosisGroup : entry.getValue()) {
                if (diagnosisGroup.isCodeInGroup(icd10Code)) {
                    return diagnosisGroup;
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }

    public static List<DiagnosisGroup> getAllDiagnosisGroups() {
        ArrayList<DiagnosisGroup> groups = new ArrayList<>();
        groups.add(group("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"));
        groups.add(group("C00-D48", "Tumörer"));
        groups.add(group("D50-D89", "Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet"));
        groups.add(group("E00-E90", "Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar"));
        groups.add(group("F00-F99", "Psykiska sjukdomar och syndrom samt beteendestörningar"));
        groups.add(group("G00-G99", "Sjukdomar i nervsystemet"));
        groups.add(group("H00-H59", "Sjukdomar i ögat och närliggande organ"));
        groups.add(group("H60-H95", "Sjukdomar i örat och mastoidutskottet"));
        groups.add(group("I00-I99", "Cirkulationsorganens sjukdomar"));
        groups.add(group("J00-J99", "Andningsorganens sjukdomar"));
        groups.add(group("K00-K93", "Matsmältningsorganens sjukdomar"));
        groups.add(group("L00-L99", "Hudens och underhudens sjukdomar"));
        groups.add(group("M00-M99", "Sjukdomar i muskuloskeletala systemet och bindväven"));
        groups.add(group("N00-N99", "Sjukdomar i urin- och könsorganen"));
        groups.add(group("O00-O99", "Graviditet, förlossning och barnsängstid"));
        groups.add(group("P00-P96", "Vissa perinatala tillstånd"));
        groups.add(group("Q00-Q99", "Medfödda missbildningar, deformiteter och kromosomavvikelser"));
        groups.add(group("R00-R99", "Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes"));
        groups.add(group("S00-T98", "Skador, förgiftningar och vissa andra följder av yttre orsaker"));
        groups.add(group("V01-Y98", "Yttre orsaker till sjukdom och död"));
        groups.add(group("Z00-Z99", "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården"));
        groups.add(group("U00-U99", "Koder för särskilda ändamål"));
        return groups;
    }

    /**
     * Only used for creating mock data. TODO Remove method when mock data is no
     * longer needed.
     */
    public static List<DiagnosisGroup> getSubGroups(String groupId) {
        return SUB_GROUPS.get(groupId);
    }

    private static DiagnosisGroup group(String code, String description) {
        return new DiagnosisGroup(code, description);
    }

    private static Map<String, List<DiagnosisGroup>> initSubGroups() {
        List<DiagnosisGroup> aGroups = new ArrayList<>();
        aGroups.add(group("A00-A09", "Infektionssjukdomar utgående från mag-tarmkanalen"));
        aGroups.add(group("A15-A19", "Tuberkulos"));
        aGroups.add(group("A20-A28", "Vissa djurburna bakteriesjukdomar"));
        aGroups.add(group("A30-A49", "Andra bakteriesjukdomar"));
        aGroups.add(group("A50-A64", "Huvudsakligen sexuellt överförda infektioner"));
        aGroups.add(group("A65-A69", "Andra spiroketsjukdomar"));
        aGroups.add(group("A70-A74", "Andra sjukdomar orsakade av klamydia"));
        aGroups.add(group("A75-A79", "Sjukdomar orsakade av rickettsiaarter"));
        aGroups.add(group("A80-A89", "Virussjukdomar i centrala nervsystemet"));
        aGroups.add(group("A90-A99", "Febersjukdomar orsakade av virus överförda av leddjur och virusorsakade hemorragiska febrar"));
        aGroups.add(group("B00-B09", "Virussjukdomar med hudutslag och slemhinneutslag"));
        aGroups.add(group("B15-B19", "Virushepatit"));
        aGroups.add(group("B20-B24", "Sjukdom orsakad av humant immunbristvirus [HIV]"));
        aGroups.add(group("B25-B34", "Andra virussjukdomar"));
        aGroups.add(group("B35-B49", "Svampsjukdomar"));
        aGroups.add(group("B50-B64", "Protozosjukdomar"));
        aGroups.add(group("B65-B83", "Masksjukdomar"));
        aGroups.add(group("B85-B89", "Lusangrepp, acarinos (angrepp av kvalster) och andra infestationer"));
        aGroups.add(group("B90-B94", "Sena effekter av infektionssjukdomar och parasitsjukdomar"));
        aGroups.add(group("B95-B98", "Bakterier, virus och andra infektiösa organismer"));
        aGroups.add(group("B99-B99", "Andra infektionssjukdomar"));

        List<DiagnosisGroup> cGroups = new ArrayList<>();
        cGroups.add(group("C00-C14", "Maligna tumörer i läpp, munhåla och svalg"));
        cGroups.add(group("C15-C26", "Maligna tumörer i matsmältningsorganen"));
        cGroups.add(group("C30-C39", "Maligna tumörer i andningsorganen och brösthålans organ"));
        cGroups.add(group("C40-C41", "Maligna tumörer i ben och ledbrosk"));
        cGroups.add(group("C43-C44", "Melanom och andra maligna tumörer i huden"));
        cGroups.add(group("C45-C49", "Maligna tumörer i mesotelial (kroppshåletäckande) vävnad och mjukvävnad"));
        cGroups.add(group("C50-C50", "Malign tumör i bröstkörtel"));
        cGroups.add(group("C51-C58", "Maligna tumörer i de kvinnliga könsorganen"));
        cGroups.add(group("C60-C63", "Maligna tumörer i de manliga könsorganen"));
        cGroups.add(group("C64-C68", "Maligna tumörer i urinorganen"));
        cGroups.add(group("C69-C72", "Maligna tumörer i öga, hjärnan och andra delar av centrala nervsystemet"));
        cGroups.add(group("C73-C75", "Maligna tumörer i tyreoidea och andra endokrina körtlar"));
        cGroups.add(group("C76-C80", "Maligna tumörer med ofullständigt angivna, sekundära och icke specificerade lokalisationer"));
        cGroups.add(group("C81-C96", "Maligna tumörer i lymfatisk, blodbildande och besläktad vävnad"));
        cGroups.add(group("C97-C97", "Flera (primära) maligna tumörer med olika utgångspunkter"));
        cGroups.add(group("D00-D09", "Cancer in situ (lokalt begränsad cancer utgången från epitel)"));
        cGroups.add(group("D10-D36", "Benigna tumörer"));
        cGroups.add(group("D37-D48", "Tumörer av osäker eller okänd natur"));

        List<DiagnosisGroup> dGroups = new ArrayList<>();
        dGroups.add(group("D50-D53", "Nutritionsanemier"));
        dGroups.add(group("D55-D59", "Hemolytiska anemier (blodbrist på grund av ökad nedbrytning av röda blodkroppar)"));
        dGroups.add(group("D60-D64", "Aplastisk anemi (blodbrist på grund av upphörd eller minskad blodbildning i benmärgen) och andra anemier"));
        dGroups.add(group("D65-D69", "Koagulationsrubbningar, purpura (punktformiga blödningar i huden mm) och andra blödningstillstånd"));
        dGroups.add(group("D70-D77", "Andra sjukdomar i blod och blodbildande organ"));
        dGroups.add(group("D80-D89", "Vissa rubbningar i immunsystemet"));

        List<DiagnosisGroup> eGroups = new ArrayList<>();
        eGroups.add(group("E00-E07", "Sjukdomar i sköldkörteln"));
        eGroups.add(group("E10-E14", "Diabetes (sockersjuka)"));
        eGroups.add(group("E15-E16", "Andra rubbningar i glukosreglering och bukspottkörtelns inre sekretion"));
        eGroups.add(group("E20-E35", "Sjukdomar i andra endokrina körtlar"));
        eGroups.add(group("E40-E46", "Näringsbrist"));
        eGroups.add(group("E50-E64", "Andra näringsbristtillstånd"));
        eGroups.add(group("E65-E68", "Fetma och andra övernäringstillstånd"));
        eGroups.add(group("E70-E90", "Ämnesomsättningssjukdomar"));

        List<DiagnosisGroup> fGroups = new ArrayList<>();
        fGroups.add(group("F00-F09", "Organiska, inklusive symtomatiska, psykiska störningar"));
        fGroups.add(group("F10-F19", "Psykiska störningar och beteendestörningar orsakade av psykoaktiva substanser"));
        fGroups.add(group("F20-F29", "Schizofreni, schizotypa störningar och vanföreställningssyndrom"));
        fGroups.add(group("F30-F39", "Förstämningssyndrom"));
        fGroups.add(group("F40-F48", "Neurotiska, stressrelaterade och somatoforma syndrom"));
        fGroups.add(group("F50-F59", "Beteendestörningar förenade med fysiologiska rubbningar och fysiska faktorer"));
        fGroups.add(group("F60-F69", "Personlighetsstörningar och beteendestörningar hos vuxna"));
        fGroups.add(group("F70-F79", "Psykisk utvecklingsstörning"));
        fGroups.add(group("F80-F89", "Störningar av psykisk utveckling"));
        fGroups.add(group("F90-F98", "Beteendestörningar och emotionella störningar med debut vanligen under barndom och ungdomstid"));
        fGroups.add(group("F99-F99", "Ospecificerad psykisk störning"));

        List<DiagnosisGroup> gGroups = new ArrayList<>();
        gGroups.add(group("G00-G09", "Inflammatoriska sjukdomar i centrala nervsystemet"));
        gGroups.add(group("G10-G14", "Systemiska atrofier som primärt engagerar centrala nervsystemet"));
        gGroups.add(group("G20-G26", "Basalgangliesjukdomar och rörelserubbningar"));
        gGroups.add(group("G30-G32", "Andra degenerativa sjukdomar i nervsystemet"));
        gGroups.add(group("G35-G37", "Myelinförstörande sjukdomar i centrala nervsystemet"));
        gGroups.add(group("G40-G47", "Episodiska och paroxysmala sjukdomar"));
        gGroups.add(group("G50-G59", "Sjukdomar i nerver, nervrötter och nervplexus"));
        gGroups.add(group("G60-G64", "Polyneuropatier (samtidig sjukdom i flera perifera nerver) och andra sjukdomar i perifera nervsystemet"));
        gGroups.add(group("G70-G73",
                "Neuromuskulära transmissionsrubbningar (rubbningar i överföring av impulser mellan nerver och muskler) och sjukdomar i muskler"));
        gGroups.add(group("G80-G83", "Cerebral pares och andra förlamningssyndrom"));
        gGroups.add(group("G90-G99", "Andra sjukdomar i nervsystemet"));

        List<DiagnosisGroup> hLowGroups = new ArrayList<>();
        hLowGroups.add(group("H00-H06", "Sjukdomar i ögonlock, tårapparat och ögonhåla"));
        hLowGroups.add(group("H10-H13", "Sjukdomar i bindehinnan"));
        hLowGroups.add(group("H15-H22", "Sjukdomar i senhinnan, hornhinnan, regnbågshinnan och ciliarkroppen"));
        hLowGroups.add(group("H25-H28", "Sjukdomar i linsen"));
        hLowGroups.add(group("H30-H36", "Sjukdomar i åderhinnan och näthinnan"));
        hLowGroups.add(group("H40-H42", "Glaukom (grön starr)"));
        hLowGroups.add(group("H43-H45", "Sjukdomar i glaskroppen och ögongloben"));
        hLowGroups.add(group("H46-H48", "Sjukdomar i synnerven och synbanorna"));
        hLowGroups.add(group("H49-H52", "Sjukdomar i ögonmusklerna, förändringar i de binokulära rörelserna samt ögats ackommodation och refraktion"));
        hLowGroups.add(group("H53-H54", "Synstörningar och blindhet"));
        hLowGroups.add(group("H55-H59", "Andra sjukdomar i ögat och närliggande organ"));

        List<DiagnosisGroup> hHighGroups = new ArrayList<>();
        hHighGroups.add(group("H60-H62", "Sjukdomar i ytterörat och hörselgången"));
        hHighGroups.add(group("H65-H75", "Sjukdomar i mellanörat och mastoidutskottet"));
        hHighGroups.add(group("H80-H83", "Sjukdomar i innerörat"));
        hHighGroups.add(group("H90-H95", "Andra öronsjukdomar"));

        List<DiagnosisGroup> iGroups = new ArrayList<>();
        iGroups.add(group("I00-I02", "Akut reumatisk feber"));
        iGroups.add(group("I05-I09", "Kroniska reumatiska hjärtsjukdomar"));
        iGroups.add(group("I10-I15", "Hypertonisjukdomar (högt blodtryck och därmed sammanhängande sjukdomar)"));
        iGroups.add(group("I20-I25", "Ischemiska hjärtsjukdomar (sjukdomar orsakade av otillräcklig blodtillförsel till hjärtmuskeln)"));
        iGroups.add(group("I26-I28", "Sjukdomstillstånd inom lungcirkulationen"));
        iGroups.add(group("I30-I52", "Andra former av hjärtsjukdom"));
        iGroups.add(group("I60-I69", "Sjukdomar i hjärnans kärl"));
        iGroups.add(group("I70-I79", "Sjukdomar i artärer, arterioler (småartärer) och kapillärer"));
        iGroups.add(group("I80-I89", "Sjukdomar i vener, lymfkärl och lymfkörtlar som ej klassificeras annorstädes"));
        iGroups.add(group("I95-I99", "Andra och icke specificerade sjukdomar i cirkulationsorganen"));

        List<DiagnosisGroup> jGroups = new ArrayList<>();
        jGroups.add(group("J00-J06", "Akuta infektioner i övre luftvägarna"));
        jGroups.add(group("J09-J18", "Influensa och lunginflammation"));
        jGroups.add(group("J20-J22", "Andra akuta infektioner i nedre luftvägarna"));
        jGroups.add(group("J30-J39", "Andra sjukdomar i övre luftvägarna"));
        jGroups.add(group("J40-J47", "Kroniska sjukdomar i nedre luftvägarna"));
        jGroups.add(group("J60-J70", "Lungsjukdomar av yttre orsaker"));
        jGroups.add(group("J80-J84", "Andra lungsjukdomar som huvudsakligen engagerar interstitiet (lungornas stödjevävnad)"));
        jGroups.add(group("J85-J86", "Variga och nekrotiska tillstånd i nedre luftvägarna"));
        jGroups.add(group("J90-J94", "Andra sjukdomar i lungsäcken"));
        jGroups.add(group("J95-J99", "Andra sjukdomar i andningsorganen"));

        List<DiagnosisGroup> kGroups = new ArrayList<>();
        kGroups.add(group("K00-K14", "Sjukdomar i munhåla, spottkörtlar och käkar"));
        kGroups.add(group("K20-K31", "Matstrupens, magsäckens och tolvfingertarmens sjukdomar"));
        kGroups.add(group("K35-K38", "Sjukdomar i blindtarmen"));
        kGroups.add(group("K40-K46", "Bråck"));
        kGroups.add(group("K50-K52", "Icke infektiös inflammation i tunntarmen och tjocktarmen"));
        kGroups.add(group("K55-K64", "Andra sjukdomar i tarmen"));
        kGroups.add(group("K65-K67", "Sjukdomar i bukhinnan"));
        kGroups.add(group("K70-K77", "Sjukdomar i levern"));
        kGroups.add(group("K80-K87", "Sjukdomar i gallblåsan, gallvägarna och bukspottkörteln"));
        kGroups.add(group("K90-K93", "Andra sjukdomar i matsmältningsorganen"));

        List<DiagnosisGroup> lGroups = new ArrayList<>();
        lGroups.add(group("L00-L08", "Infektioner i hud och underhud"));
        lGroups.add(group("L10-L14", "Blåsdermatoser (hudsjukdomar med blåsor)"));
        lGroups.add(group("L20-L30", "Dermatit och eksem"));
        lGroups.add(group("L40-L45", "Papuloskvamösa sjukdomar"));
        lGroups.add(group("L50-L54", "Urtikaria (nässelfeber) och erytematösa tillstånd (tillstånd med hudrodnad)"));
        lGroups.add(group("L55-L59", "Strålningsrelaterade sjukdomar i hud och underhud"));
        lGroups.add(group("L60-L75", "Sjukdomar i hår, hårfolliklar, naglar, talgkörtlar och svettkörtlar"));
        lGroups.add(group("L80-L99", "Andra sjukdomar i hud och underhud"));

        List<DiagnosisGroup> mGroups = new ArrayList<>();
        mGroups.add(group("M00-M03", "Infektiösa ledsjukdomar"));
        mGroups.add(group("M05-M14", "Inflammatoriska polyartriter"));
        mGroups.add(group("M15-M19", "Artros"));
        mGroups.add(group("M20-M25", "Andra ledsjukdomar"));
        mGroups.add(group("M30-M36", "Inflammatoriska systemsjukdomar"));
        mGroups.add(group("M40-M43", "Deformerande ryggsjukdomar"));
        mGroups.add(group("M45-M49", "Spondylopatier"));
        mGroups.add(group("M50-M54", "Andra ryggsjukdomar"));
        mGroups.add(group("M60-M63", "Muskelsjukdomar"));
        mGroups.add(group("M65-M68", "Sjukdomar i ledhinnor och senor"));
        mGroups.add(group("M70-M79", "Andra sjukdomar i mjukvävnader"));
        mGroups.add(group("M80-M85", "Rubbningar i bentäthet och benstruktur"));
        mGroups.add(group("M86-M90", "Andra sjukdomar i benvävnad"));
        mGroups.add(group("M91-M94", "Sjukdomar i broskvävnad"));
        mGroups.add(group("M95-M99", "Andra sjukdomar i muskuloskeletala systemet och bindväven"));

        List<DiagnosisGroup> nGroups = new ArrayList<>();
        nGroups.add(group("N00-N08", "Glomerulussjukdomar"));
        nGroups.add(group("N10-N16", "Tubulo-interstitiella njursjukdomar"));
        nGroups.add(group("N17-N19", "Njursvikt"));
        nGroups.add(group("N20-N23", "Sten i urinvägarna"));
        nGroups.add(group("N25-N29", "Andra sjukdomar i njure och urinledare"));
        nGroups.add(group("N30-N39", "Andra sjukdomar i urinorganen"));
        nGroups.add(group("N40-N51", "Sjukdomar i de manliga könsorganen"));
        nGroups.add(group("N60-N64", "Sjukdomar i bröstkörtel"));
        nGroups.add(group("N70-N77", "Inflammatoriska sjukdomar i de kvinnliga bäckenorganen"));
        nGroups.add(group("N80-N98", "Icke inflammatoriska sjukdomar i de kvinnliga könsorganen"));
        nGroups.add(group("N99-N99", "Andra sjukliga tillstånd i urin- och könsorganen"));

        List<DiagnosisGroup> oGroups = new ArrayList<>();
        oGroups.add(group("O00-O08", "Graviditet som avslutas med abort"));
        oGroups.add(group("O10-O16", "Ödem, proteinuri (äggvita i urinen) och hypertoni under graviditet, förlossning och barnsängstid"));
        oGroups.add(group("O20-O29", "Andra sjukdomar hos den blivande modern i huvudsak sammanhängande med graviditeten"));
        oGroups.add(group("O30-O48", "Vård under graviditet på grund av problem relaterade till fostret och amnionhålan samt befarade förlossningsproblem"));
        oGroups.add(group("O60-O75", "Komplikationer vid värkarbete och förlossning"));
        oGroups.add(group("O80-O84", "Förlossning"));
        oGroups.add(group("O85-O92", "Komplikationer huvudsakligen sammanhängande med barnsängstiden"));
        oGroups.add(group("O94-O99", "Andra obstetriska tillstånd som ej klassificeras annorstädes (O94-O99)"));

        List<DiagnosisGroup> pGroups = new ArrayList<>();
        pGroups.add(group("P00-P04", "Foster och nyfödd som påverkats av tillstånd hos modern och av komplikationer vid graviditet, värkarbete och förlossning"));
        pGroups.add(group("P05-P08", "Sjukdomar som har samband med graviditetslängd och fostertillväxt"));
        pGroups.add(group("P10-P15", "Förlossningsskador"));
        pGroups.add(group("P20-P29", "Sjukdomar i andningsorgan och cirkulationsorgan specifika för den perinatala perioden"));
        pGroups.add(group("P35-P39", "Infektioner specifika för den perinatala perioden"));
        pGroups.add(group("P50-P61", "Blödningssjukdomar och blodsjukdomar hos foster och nyfödd"));
        pGroups.add(group("P70-P74", "Övergående endokrina rubbningar och ämnesomsättningsrubbningar specifika för foster och nyfödd"));
        pGroups.add(group("P75-P78", "Sjukdomar i matsmältningsorganen hos foster och nyfödd"));
        pGroups.add(group("P80-P83", "Tillstånd som engagerar hud och temperaturreglering hos foster och nyfödd"));
        pGroups.add(group("P90-P96", "Andra sjukdomar och rubbningar under den perinatala perioden"));

        List<DiagnosisGroup> qGroups = new ArrayList<>();
        qGroups.add(group("Q00-Q07", "Medfödda missbildningar av nervsystemet"));
        qGroups.add(group("Q10-Q18", "Medfödda missbildningar av öga, öra, ansikte och hals"));
        qGroups.add(group("Q20-Q28", "Medfödda missbildningar av cirkulationsorganen"));
        qGroups.add(group("Q30-Q34", "Medfödda missbildningar av andningsorganen"));
        qGroups.add(group("Q35-Q37", "Kluven läpp och gom"));
        qGroups.add(group("Q38-Q45", "Andra medfödda missbildningar av matsmältningsorganen"));
        qGroups.add(group("Q50-Q56", "Medfödda missbildningar av könsorganen"));
        qGroups.add(group("Q60-Q64", "Medfödda missbildningar av urinorganen"));
        qGroups.add(group("Q65-Q79", "Medfödda missbildningar och deformiteter av muskler och skelett"));
        qGroups.add(group("Q80-Q89", "Andra medfödda missbildningar"));
        qGroups.add(group("Q90-Q99", "Kromosomavvikelser som ej klassificeras annorstädes"));

        List<DiagnosisGroup> rGroups = new ArrayList<>();
        rGroups.add(group("R00-R09", "Symtom och sjukdomstecken från cirkulationsorganen och andningsorganen"));
        rGroups.add(group("R10-R19", "Symtom och sjukdomstecken från matsmältningsorganen och buken"));
        rGroups.add(group("R20-R23", "Symtom och sjukdomstecken från huden och underhuden"));
        rGroups.add(group("R25-R29", "Symtom och sjukdomstecken från nervsystemet och muskuloskeletala systemet"));
        rGroups.add(group("R30-R39", "Symtom och sjukdomstecken från urinorganen"));
        rGroups.add(group("R40-R46", "Symtom och sjukdomstecken avseende intellektuella funktioner, uppfattningsförmåga, känsloläge och beteende"));
        rGroups.add(group("R47-R49", "Symtom och sjukdomstecken avseende talet och rösten"));
        rGroups.add(group("R50-R69", "Allmänna symtom och sjukdomstecken"));
        rGroups.add(group("R70-R79", "Onormala fynd vid blodundersökning utan diagnos"));
        rGroups.add(group("R80-R82", "Onormala fynd vid urinundersökning utan diagnos"));
        rGroups.add(group("R83-R89", "Onormala fynd vid undersökning av andra kroppsvätskor, substanser och vävnader, utan diagnos"));
        rGroups.add(group("R90-R94", "Onormala fynd vid radiologisk diagnostik och vid funktionsundersökning utan diagnos"));
        rGroups.add(group("R95-R99", "Ofullständigt definierade och okända orsaker till död"));

        List<DiagnosisGroup> sGroups = new ArrayList<>();
        sGroups.add(group("S00-S09", "Skador på huvudet"));
        sGroups.add(group("S10-S19", "Skador på halsen"));
        sGroups.add(group("S20-S29", "Skador i bröstregionen"));
        sGroups.add(group("S30-S39", "Skador i buken, nedre delen av ryggen, ländkotpelaren och bäckenet"));
        sGroups.add(group("S40-S49", "Skador på skuldra och överarm"));
        sGroups.add(group("S50-S59", "Skador på armbåge och underarm"));
        sGroups.add(group("S60-S69", "Skador på handled och hand"));
        sGroups.add(group("S70-S79", "Skador på höft och lår"));
        sGroups.add(group("S80-S89", "Skador på knä och underben"));
        sGroups.add(group("S90-S99", "Skador på fotled och fot"));
        sGroups.add(group("T00-T07", "Skador som engagerar flera kroppsregioner"));
        sGroups.add(group("T08-T14", "Skador på icke specificerad del av bålen, extremitet eller annan kroppsregion"));
        sGroups.add(group("T15-T19", "Effekter av främmande kropp som trängt in genom naturlig öppning"));
        sGroups.add(group("T20-T25", "Brännskador och frätskador på yttre kroppsyta med specificerad lokalisation"));
        sGroups.add(group("T26-T28", "Brännskador och frätskador begränsade till ögat och inre organ"));
        sGroups.add(group("T29-T32", "Brännskador och frätskador på multipla och icke specificerade kroppsregioner"));
        sGroups.add(group("T33-T35", "Köldskada"));
        sGroups.add(group("T36-T50", "Förgiftning av droger, läkemedel och biologiska substanser"));
        sGroups.add(group("T51-T65", "Toxisk effekt av substanser med i huvudsak icke-medicinsk användning"));
        sGroups.add(group("T66-T78", "Andra och icke specificerade effekter av yttre orsaker"));
        sGroups.add(group("T79-T79", "Vissa tidiga komplikationer till skada genom yttre våld"));
        sGroups.add(group("T80-T88", "Komplikationer till kirurgiska åtgärder och medicinsk vård som ej klassificeras annorstädes"));
        sGroups.add(group("T90-T98", "Sena besvär av skador, förgiftningar och andra följder av yttre orsaker"));

        List<DiagnosisGroup> uGroups = new ArrayList<>();
        uGroups.add(group("U00-U49", "Interimistiska koder för nya sjukdomar med osäker etiologi eller koder som kan tas i bruk med kort varsel"));
        uGroups.add(group("U82-U85", "Resistens mot antimikrobiella och antineoplastiska läkemedel (U82-U85)"));
        uGroups.add(group("U98-U99", "Koder för särskilda nationella behov"));

        List<DiagnosisGroup> vGroups = new ArrayList<>();
        vGroups.add(group("V01-V09", "Fotgängare skadad i transportolycka"));
        vGroups.add(group("V10-V19", "Cyklist (förare eller passagerare) skadad i transportolycka"));
        vGroups.add(group("V20-V29", "Motorcyklist (förare eller passagerare) skadad i transportolycka"));
        vGroups.add(group("V30-V39", "Förare av eller passagerare i trehjuligt motorfordon skadad i transportolycka"));
        vGroups.add(group("V40-V49", "Förare av eller passagerare i personbil skadad i transportolycka"));
        vGroups.add(group("V50-V59", "Förare av eller passagerare i lätt lastbil skadad i transportolycka"));
        vGroups.add(group("V60-V69", "Förare av eller passagerare i tung lastbil skadad i transportolycka"));
        vGroups.add(group("V70-V79", "Förare av eller passagerare i buss skadad i transportolycka"));
        vGroups.add(group("V80-V89", "Andra transportolyckor på land"));
        vGroups.add(group("V90-V94", "Transportolyckor på vatten"));
        vGroups.add(group("V95-V97", "Transportolyckor i luften och rymden"));
        vGroups.add(group("V98-V99", "Andra och icke specificerade transportolyckor"));
        vGroups.add(group("W00-W19", "Fallolyckor"));
        vGroups.add(group("W20-W49", "Exponering för icke levande mekaniska krafter"));
        vGroups.add(group("W50-W64", "Exponering för levande mekaniska krafter"));
        vGroups.add(group("W65-W74", "Drunkning och drunkningstillbud genom olyckshändelse"));
        vGroups.add(group("W75-W84", "Annan kvävning och annat kvävningstillbud genom olyckshändelse"));
        vGroups.add(group("W85-W99", "Exponering för elektrisk ström, strålning, extrem lufttemperatur och extremt lufttryck i omgivningen"));
        vGroups.add(group("X00-X09", "Exponering för rök och öppen eld"));
        vGroups.add(group("X10-X19", "Kontakt med heta föremål och heta ämnen"));
        vGroups.add(group("X20-X29", "Kontakt med giftiga djur och växter"));
        vGroups.add(group("X30-X39", "Exponering för naturkrafter"));
        vGroups.add(group("X40-X49", "Förgiftningsolyckor och exponering för skadliga ämnen genom olyckshändelse"));
        vGroups.add(group("X50-X57", "Överansträngning och umbäranden"));
        vGroups.add(group("X58-X59", "Exponering genom olyckshändelse för andra och icke specificerade faktorer"));
        vGroups.add(group("X60-X84", "Avsiktligt självdestruktiv handling"));
        vGroups.add(group("X85-Y09", "Övergrepp av annan person"));
        vGroups.add(group("Y10-Y34", "Skadehändelser med oklar avsikt"));
        vGroups.add(group("Y35-Y36", "Polisingripande och krigshandling"));
        vGroups.add(group("Y40-Y59", "Läkemedel, droger och biologiska substanser i terapeutiskt bruk som orsak till ogynnsam effekt"));
        vGroups.add(group("Y60-Y69", "Missöden som inträffat med patienter under kirurgisk och medicinsk vård"));
        vGroups.add(group("Y70-Y82", "Missöden orsakade av medicinska instrument i diagnostiskt och terapeutiskt bruk"));
        vGroups.add(group(
                "Y83-Y84",
                "Kirurgiska och andra medicinska åtgärder som orsak till onormal reaktion eller sen komplikation hos patient utan anknytning till missöde vid operations- eller behandlingstillfället"));
        vGroups.add(group("Y85-Y89", "Sena effekter av yttre orsaker till sjukdom och död"));
        vGroups.add(group("Y90-Y98", "Bidragande faktorer som har samband med yttre orsaker till sjukdom och död, vilka klassificeras annorstädes"));

        List<DiagnosisGroup> zGroups = new ArrayList<>();
        zGroups.add(group("Z00-Z13", "Kontakt med hälso- och sjukvården för undersökning och utredning"));
        zGroups.add(group("Z20-Z29", "Potentiella hälsorisker avseende smittsamma sjukdomar"));
        zGroups.add(group("Z30-Z39", "Kontakter med hälso- och sjukvården i samband med fortplantning"));
        zGroups.add(group("Z40-Z54", "Kontakter med hälso- och sjukvården för speciella åtgärder och vård"));
        zGroups.add(group("Z55-Z65", "Potentiella hälsorisker avseende socioekonomiska och psykosociala förhållanden"));
        zGroups.add(group("Z70-Z76", "Kontakter med hälso- och sjukvården i andra situationer"));
        zGroups.add(group("Z80-Z99",
                "Potentiella hälsorisker i familjens och patientens sjukhistoria samt vissa tillstånd och förhållanden som påverkar hälsan"));

        Map<String, List<DiagnosisGroup>> subGroups = new HashMap<>();
        subGroups.put("A00-B99", aGroups);
        subGroups.put("C00-D48", cGroups);
        subGroups.put("D50-D89", dGroups);
        subGroups.put("E00-E90", eGroups);
        subGroups.put("F00-F99", fGroups);
        subGroups.put("G00-G99", gGroups);
        subGroups.put("H00-H59", hLowGroups);
        subGroups.put("H60-H95", hHighGroups);
        subGroups.put("I00-I99", iGroups);
        subGroups.put("J00-J99", jGroups);
        subGroups.put("K00-K93", kGroups);
        subGroups.put("L00-L99", lGroups);
        subGroups.put("M00-M99", mGroups);
        subGroups.put("N00-N99", nGroups);
        subGroups.put("O00-O99", oGroups);
        subGroups.put("P00-P96", pGroups);
        subGroups.put("Q00-Q99", qGroups);
        subGroups.put("R00-R99", rGroups);
        subGroups.put("S00-T98", sGroups);
        subGroups.put("V01-Y98", vGroups);
        subGroups.put("Z00-Z99", zGroups);
        subGroups.put("U00-U99", uGroups);
        return subGroups;
    }

}
