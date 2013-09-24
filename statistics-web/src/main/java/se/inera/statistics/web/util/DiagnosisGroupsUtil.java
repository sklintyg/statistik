package se.inera.statistics.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import se.inera.statistics.web.model.DiagnosisGroup;

public class DiagnosisGroupsUtil {

    private static final Map<String, List<DiagnosisGroup>> subGroups = initSubGroups();

    public static String getGroupIdForCode(String icd10Code){
        Set<Entry<String,List<DiagnosisGroup>>> entrySet = subGroups.entrySet();
        for (Entry<String, List<DiagnosisGroup>> entry : entrySet) {
            List<DiagnosisGroup> value = entry.getValue();
            for (DiagnosisGroup diagnosisGroup : value) {
                if (diagnosisGroup.isCodeInGroup(icd10Code)){
                    return entry.getKey();
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }
    
    public static DiagnosisGroup getSubGroupForCode(String icd10Code){
        Set<Entry<String,List<DiagnosisGroup>>> entrySet = subGroups.entrySet();
        for (Entry<String, List<DiagnosisGroup>> entry : entrySet) {
            List<DiagnosisGroup> value = entry.getValue();
            for (DiagnosisGroup diagnosisGroup : value) {
                if (diagnosisGroup.isCodeInGroup(icd10Code)){
                    return diagnosisGroup;
                }
            }
        }
        throw new IllegalArgumentException("ICD-10-SE code not found: " + icd10Code);
    }
    
    public static List<DiagnosisGroup> getAllDiagnosisGroups(){
        ArrayList<DiagnosisGroup> groups = new ArrayList<>();
        groups.add(new DiagnosisGroup("A00-B99", "Vissa infektionssjukdomar och parasitsjukdomar"));
        groups.add(new DiagnosisGroup("C00-D48", "Tumörer"));
        groups.add(new DiagnosisGroup("D50-D89", "Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet"));
        groups.add(new DiagnosisGroup("E00-E90", "Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar"));
        groups.add(new DiagnosisGroup("F00-F99", "Psykiska sjukdomar och syndrom samt beteendestörningar"));
        groups.add(new DiagnosisGroup("G00-G99", "Sjukdomar i nervsystemet"));
        groups.add(new DiagnosisGroup("H00-H59", "Sjukdomar i ögat och närliggande organ"));
        groups.add(new DiagnosisGroup("H60-H95", "Sjukdomar i örat och mastoidutskottet"));
        groups.add(new DiagnosisGroup("I00-I99", "Cirkulationsorganens sjukdomar"));
        groups.add(new DiagnosisGroup("J00-J99", "Andningsorganens sjukdomar"));
        groups.add(new DiagnosisGroup("K00-K93", "Matsmältningsorganens sjukdomar"));
        groups.add(new DiagnosisGroup("L00-L99", "Hudens och underhudens sjukdomar"));
        groups.add(new DiagnosisGroup("M00-M99", "Sjukdomar i muskuloskeletala systemet och bindväven"));
        groups.add(new DiagnosisGroup("N00-N99", "Sjukdomar i urin- och könsorganen"));
        groups.add(new DiagnosisGroup("O00-O99", "Graviditet, förlossning och barnsängstid"));
        groups.add(new DiagnosisGroup("P00-P96", "Vissa perinatala tillstånd"));
        groups.add(new DiagnosisGroup("Q00-Q99", "Medfödda missbildningar, deformiteter och kromosomavvikelser"));
        groups.add(new DiagnosisGroup("R00-R99", "Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes"));
        groups.add(new DiagnosisGroup("S00-T98", "Skador, förgiftningar och vissa andra följder av yttre orsaker"));
        groups.add(new DiagnosisGroup("V01-Y98", "Yttre orsaker till sjukdom och död"));
        groups.add(new DiagnosisGroup("Z00-Z99", "Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården"));
        groups.add(new DiagnosisGroup("U00-U99", "Koder för särskilda ändamål"));
        return groups;
    }
    
    /**
     * Only used for creating mock data. 
     * TODO Remove method when mock data is no longer needed. 
     */
    public static List<DiagnosisGroup> getSubGroups(String groupId){
        if (subGroups.containsKey(groupId)){
            return subGroups.get(groupId);
        } else {
            return null;
        }
    }

    private static Map<String, List<DiagnosisGroup>> initSubGroups() {
        List<DiagnosisGroup> aGroups = new ArrayList<>();
        aGroups.add(new DiagnosisGroup("A00-A09", "Infektionssjukdomar utgående från mag-tarmkanalen"));
        aGroups.add(new DiagnosisGroup("A15-A19", "Tuberkulos"));
        aGroups.add(new DiagnosisGroup("A20-A28", "Vissa djurburna bakteriesjukdomar"));
        aGroups.add(new DiagnosisGroup("A30-A49", "Andra bakteriesjukdomar"));
        aGroups.add(new DiagnosisGroup("A50-A64", "Huvudsakligen sexuellt överförda infektioner"));
        aGroups.add(new DiagnosisGroup("A65-A69", "Andra spiroketsjukdomar"));
        aGroups.add(new DiagnosisGroup("A70-A74", "Andra sjukdomar orsakade av klamydia"));
        aGroups.add(new DiagnosisGroup("A75-A79", "Sjukdomar orsakade av rickettsiaarter"));
        aGroups.add(new DiagnosisGroup("A80-A89", "Virussjukdomar i centrala nervsystemet"));
        aGroups.add(new DiagnosisGroup("A90-A99", "Febersjukdomar orsakade av virus överförda av leddjur och virusorsakade hemorragiska febrar"));
        aGroups.add(new DiagnosisGroup("B00-B09", "Virussjukdomar med hudutslag och slemhinneutslag"));
        aGroups.add(new DiagnosisGroup("B15-B19", "Virushepatit"));
        aGroups.add(new DiagnosisGroup("B20-B24", "Sjukdom orsakad av humant immunbristvirus [HIV]"));
        aGroups.add(new DiagnosisGroup("B25-B34", "Andra virussjukdomar"));
        aGroups.add(new DiagnosisGroup("B35-B49", "Svampsjukdomar"));
        aGroups.add(new DiagnosisGroup("B50-B64", "Protozosjukdomar"));
        aGroups.add(new DiagnosisGroup("B65-B83", "Masksjukdomar"));
        aGroups.add(new DiagnosisGroup("B85-B89", "Lusangrepp, acarinos (angrepp av kvalster) och andra infestationer"));
        aGroups.add(new DiagnosisGroup("B90-B94", "Sena effekter av infektionssjukdomar och parasitsjukdomar"));
        aGroups.add(new DiagnosisGroup("B95-B98", "Bakterier, virus och andra infektiösa organismer"));
        aGroups.add(new DiagnosisGroup("B99-B99", "Andra infektionssjukdomar"));

        List<DiagnosisGroup> cGroups = new ArrayList<>();
        cGroups.add(new DiagnosisGroup("C00-C14", "Maligna tumörer i läpp, munhåla och svalg"));
        cGroups.add(new DiagnosisGroup("C15-C26", "Maligna tumörer i matsmältningsorganen"));
        cGroups.add(new DiagnosisGroup("C30-C39", "Maligna tumörer i andningsorganen och brösthålans organ"));
        cGroups.add(new DiagnosisGroup("C40-C41", "Maligna tumörer i ben och ledbrosk"));
        cGroups.add(new DiagnosisGroup("C43-C44", "Melanom och andra maligna tumörer i huden"));
        cGroups.add(new DiagnosisGroup("C45-C49", "Maligna tumörer i mesotelial (kroppshåletäckande) vävnad och mjukvävnad"));
        cGroups.add(new DiagnosisGroup("C50-C50", "Malign tumör i bröstkörtel"));
        cGroups.add(new DiagnosisGroup("C51-C58", "Maligna tumörer i de kvinnliga könsorganen"));
        cGroups.add(new DiagnosisGroup("C60-C63", "Maligna tumörer i de manliga könsorganen"));
        cGroups.add(new DiagnosisGroup("C64-C68", "Maligna tumörer i urinorganen"));
        cGroups.add(new DiagnosisGroup("C69-C72", "Maligna tumörer i öga, hjärnan och andra delar av centrala nervsystemet"));
        cGroups.add(new DiagnosisGroup("C73-C75", "Maligna tumörer i tyreoidea och andra endokrina körtlar"));
        cGroups.add(new DiagnosisGroup("C76-C80", "Maligna tumörer med ofullständigt angivna, sekundära och icke specificerade lokalisationer"));
        cGroups.add(new DiagnosisGroup("C81-C96", "Maligna tumörer i lymfatisk, blodbildande och besläktad vävnad"));
        cGroups.add(new DiagnosisGroup("C97-C97", "Flera (primära) maligna tumörer med olika utgångspunkter"));
        cGroups.add(new DiagnosisGroup("D00-D09", "Cancer in situ (lokalt begränsad cancer utgången från epitel)"));
        cGroups.add(new DiagnosisGroup("D10-D36", "Benigna tumörer"));
        cGroups.add(new DiagnosisGroup("D37-D48", "Tumörer av osäker eller okänd natur"));

        List<DiagnosisGroup> dGroups = new ArrayList<>();
        dGroups.add(new DiagnosisGroup("D50-D53", "Nutritionsanemier"));
        dGroups.add(new DiagnosisGroup("D55-D59", "Hemolytiska anemier (blodbrist på grund av ökad nedbrytning av röda blodkroppar)"));
        dGroups.add(new DiagnosisGroup("D60-D64", "Aplastisk anemi (blodbrist på grund av upphörd eller minskad blodbildning i benmärgen) och andra anemier"));
        dGroups.add(new DiagnosisGroup("D65-D69", "Koagulationsrubbningar, purpura (punktformiga blödningar i huden mm) och andra blödningstillstånd"));
        dGroups.add(new DiagnosisGroup("D70-D77", "Andra sjukdomar i blod och blodbildande organ"));
        dGroups.add(new DiagnosisGroup("D80-D89", "Vissa rubbningar i immunsystemet"));

        List<DiagnosisGroup> eGroups = new ArrayList<>();
        eGroups.add(new DiagnosisGroup("E00-E07", "Sjukdomar i sköldkörteln"));
        eGroups.add(new DiagnosisGroup("E10-E14", "Diabetes (sockersjuka)"));
        eGroups.add(new DiagnosisGroup("E15-E16", "Andra rubbningar i glukosreglering och bukspottkörtelns inre sekretion"));
        eGroups.add(new DiagnosisGroup("E20-E35", "Sjukdomar i andra endokrina körtlar"));
        eGroups.add(new DiagnosisGroup("E40-E46", "Näringsbrist"));
        eGroups.add(new DiagnosisGroup("E50-E64", "Andra näringsbristtillstånd"));
        eGroups.add(new DiagnosisGroup("E65-E68", "Fetma och andra övernäringstillstånd"));
        eGroups.add(new DiagnosisGroup("E70-E90", "Ämnesomsättningssjukdomar"));
        
        List<DiagnosisGroup> fGroups = new ArrayList<>();
        fGroups.add(new DiagnosisGroup("F00-F09", "Organiska, inklusive symtomatiska, psykiska störningar"));
        fGroups.add(new DiagnosisGroup("F10-F19", "Psykiska störningar och beteendestörningar orsakade av psykoaktiva substanser"));
        fGroups.add(new DiagnosisGroup("F20-F29", "Schizofreni, schizotypa störningar och vanföreställningssyndrom"));
        fGroups.add(new DiagnosisGroup("F30-F39", "Förstämningssyndrom"));
        fGroups.add(new DiagnosisGroup("F40-F48", "Neurotiska, stressrelaterade och somatoforma syndrom"));
        fGroups.add(new DiagnosisGroup("F50-F59", "Beteendestörningar förenade med fysiologiska rubbningar och fysiska faktorer"));
        fGroups.add(new DiagnosisGroup("F60-F69", "Personlighetsstörningar och beteendestörningar hos vuxna"));
        fGroups.add(new DiagnosisGroup("F70-F79", "Psykisk utvecklingsstörning"));
        fGroups.add(new DiagnosisGroup("F80-F89", "Störningar av psykisk utveckling"));
        fGroups.add(new DiagnosisGroup("F90-F98", "Beteendestörningar och emotionella störningar med debut vanligen under barndom och ungdomstid"));
        fGroups.add(new DiagnosisGroup("F99-F99", "Ospecificerad psykisk störning"));

        List<DiagnosisGroup> gGroups = new ArrayList<>();
        gGroups.add(new DiagnosisGroup("G00-G09", "Inflammatoriska sjukdomar i centrala nervsystemet"));
        gGroups.add(new DiagnosisGroup("G10-G14", "Systemiska atrofier som primärt engagerar centrala nervsystemet"));
        gGroups.add(new DiagnosisGroup("G20-G26", "Basalgangliesjukdomar och rörelserubbningar"));
        gGroups.add(new DiagnosisGroup("G30-G32", "Andra degenerativa sjukdomar i nervsystemet"));
        gGroups.add(new DiagnosisGroup("G35-G37", "Myelinförstörande sjukdomar i centrala nervsystemet"));
        gGroups.add(new DiagnosisGroup("G40-G47", "Episodiska och paroxysmala sjukdomar"));
        gGroups.add(new DiagnosisGroup("G50-G59", "Sjukdomar i nerver, nervrötter och nervplexus"));
        gGroups.add(new DiagnosisGroup("G60-G64", "Polyneuropatier (samtidig sjukdom i flera perifera nerver) och andra sjukdomar i perifera nervsystemet"));
        gGroups.add(new DiagnosisGroup("G70-G73", "Neuromuskulära transmissionsrubbningar (rubbningar i överföring av impulser mellan nerver och muskler) och sjukdomar i muskler"));
        gGroups.add(new DiagnosisGroup("G80-G83", "Cerebral pares och andra förlamningssyndrom"));
        gGroups.add(new DiagnosisGroup("G90-G99", "Andra sjukdomar i nervsystemet"));

        List<DiagnosisGroup> hLowGroups = new ArrayList<>();
        hLowGroups.add(new DiagnosisGroup("H00-H06", "Sjukdomar i ögonlock, tårapparat och ögonhåla"));
        hLowGroups.add(new DiagnosisGroup("H10-H13", "Sjukdomar i bindehinnan"));
        hLowGroups.add(new DiagnosisGroup("H15-H22", "Sjukdomar i senhinnan, hornhinnan, regnbågshinnan och ciliarkroppen"));
        hLowGroups.add(new DiagnosisGroup("H25-H28", "Sjukdomar i linsen"));
        hLowGroups.add(new DiagnosisGroup("H30-H36", "Sjukdomar i åderhinnan och näthinnan"));
        hLowGroups.add(new DiagnosisGroup("H40-H42", "Glaukom (grön starr)"));
        hLowGroups.add(new DiagnosisGroup("H43-H45", "Sjukdomar i glaskroppen och ögongloben"));
        hLowGroups.add(new DiagnosisGroup("H46-H48", "Sjukdomar i synnerven och synbanorna"));
        hLowGroups.add(new DiagnosisGroup("H49-H52", "Sjukdomar i ögonmusklerna, förändringar i de binokulära rörelserna samt ögats ackommodation och refraktion"));
        hLowGroups.add(new DiagnosisGroup("H53-H54", "Synstörningar och blindhet"));
        hLowGroups.add(new DiagnosisGroup("H55-H59", "Andra sjukdomar i ögat och närliggande organ"));

        List<DiagnosisGroup> hHighGroups = new ArrayList<>();
        hHighGroups.add(new DiagnosisGroup("H60-H62", "Sjukdomar i ytterörat och hörselgången"));
        hHighGroups.add(new DiagnosisGroup("H65-H75", "Sjukdomar i mellanörat och mastoidutskottet"));
        hHighGroups.add(new DiagnosisGroup("H80-H83", "Sjukdomar i innerörat"));
        hHighGroups.add(new DiagnosisGroup("H90-H95", "Andra öronsjukdomar"));
        
        List<DiagnosisGroup> iGroups = new ArrayList<>();
        iGroups.add(new DiagnosisGroup("I00-I02", "Akut reumatisk feber"));
        iGroups.add(new DiagnosisGroup("I05-I09", "Kroniska reumatiska hjärtsjukdomar"));
        iGroups.add(new DiagnosisGroup("I10-I15", "Hypertonisjukdomar (högt blodtryck och därmed sammanhängande sjukdomar)"));
        iGroups.add(new DiagnosisGroup("I20-I25", "Ischemiska hjärtsjukdomar (sjukdomar orsakade av otillräcklig blodtillförsel till hjärtmuskeln)"));
        iGroups.add(new DiagnosisGroup("I26-I28", "Sjukdomstillstånd inom lungcirkulationen"));
        iGroups.add(new DiagnosisGroup("I30-I52", "Andra former av hjärtsjukdom"));
        iGroups.add(new DiagnosisGroup("I60-I69", "Sjukdomar i hjärnans kärl"));
        iGroups.add(new DiagnosisGroup("I70-I79", "Sjukdomar i artärer, arterioler (småartärer) och kapillärer"));
        iGroups.add(new DiagnosisGroup("I80-I89", "Sjukdomar i vener, lymfkärl och lymfkörtlar som ej klassificeras annorstädes"));
        iGroups.add(new DiagnosisGroup("I95-I99", "Andra och icke specificerade sjukdomar i cirkulationsorganen"));
        
        List<DiagnosisGroup> jGroups = new ArrayList<>();
        jGroups.add(new DiagnosisGroup("J00-J06", "Akuta infektioner i övre luftvägarna"));
        jGroups.add(new DiagnosisGroup("J09-J18", "Influensa och lunginflammation"));
        jGroups.add(new DiagnosisGroup("J20-J22", "Andra akuta infektioner i nedre luftvägarna"));
        jGroups.add(new DiagnosisGroup("J30-J39", "Andra sjukdomar i övre luftvägarna"));
        jGroups.add(new DiagnosisGroup("J40-J47", "Kroniska sjukdomar i nedre luftvägarna"));
        jGroups.add(new DiagnosisGroup("J60-J70", "Lungsjukdomar av yttre orsaker"));
        jGroups.add(new DiagnosisGroup("J80-J84", "Andra lungsjukdomar som huvudsakligen engagerar interstitiet (lungornas stödjevävnad)"));
        jGroups.add(new DiagnosisGroup("J85-J86", "Variga och nekrotiska tillstånd i nedre luftvägarna"));
        jGroups.add(new DiagnosisGroup("J90-J94", "Andra sjukdomar i lungsäcken"));
        jGroups.add(new DiagnosisGroup("J95-J99", "Andra sjukdomar i andningsorganen"));
        
        List<DiagnosisGroup> kGroups = new ArrayList<>();
        kGroups.add(new DiagnosisGroup("K00-K14", "Sjukdomar i munhåla, spottkörtlar och käkar"));
        kGroups.add(new DiagnosisGroup("K20-K31", "Matstrupens, magsäckens och tolvfingertarmens sjukdomar"));
        kGroups.add(new DiagnosisGroup("K35-K38", "Sjukdomar i blindtarmen"));
        kGroups.add(new DiagnosisGroup("K40-K46", "Bråck"));
        kGroups.add(new DiagnosisGroup("K50-K52", "Icke infektiös inflammation i tunntarmen och tjocktarmen"));
        kGroups.add(new DiagnosisGroup("K55-K64", "Andra sjukdomar i tarmen"));
        kGroups.add(new DiagnosisGroup("K65-K67", "Sjukdomar i bukhinnan"));
        kGroups.add(new DiagnosisGroup("K70-K77", "Sjukdomar i levern"));
        kGroups.add(new DiagnosisGroup("K80-K87", "Sjukdomar i gallblåsan, gallvägarna och bukspottkörteln"));
        kGroups.add(new DiagnosisGroup("K90-K93", "Andra sjukdomar i matsmältningsorganen"));
        
        List<DiagnosisGroup> lGroups = new ArrayList<>();
        lGroups.add(new DiagnosisGroup("L00-L08", "Infektioner i hud och underhud"));
        lGroups.add(new DiagnosisGroup("L10-L14", "Blåsdermatoser (hudsjukdomar med blåsor)"));
        lGroups.add(new DiagnosisGroup("L20-L30", "Dermatit och eksem"));
        lGroups.add(new DiagnosisGroup("L40-L45", "Papuloskvamösa sjukdomar"));
        lGroups.add(new DiagnosisGroup("L50-L54", "Urtikaria (nässelfeber) och erytematösa tillstånd (tillstånd med hudrodnad)"));
        lGroups.add(new DiagnosisGroup("L55-L59", "Strålningsrelaterade sjukdomar i hud och underhud"));
        lGroups.add(new DiagnosisGroup("L60-L75", "Sjukdomar i hår, hårfolliklar, naglar, talgkörtlar och svettkörtlar"));
        lGroups.add(new DiagnosisGroup("L80-L99", "Andra sjukdomar i hud och underhud"));
        
        List<DiagnosisGroup> mGroups = new ArrayList<>();
        mGroups.add(new DiagnosisGroup("M00-M03", "Infektiösa ledsjukdomar"));
        mGroups.add(new DiagnosisGroup("M05-M14", "Inflammatoriska polyartriter"));
        mGroups.add(new DiagnosisGroup("M15-M19", "Artros"));
        mGroups.add(new DiagnosisGroup("M20-M25", "Andra ledsjukdomar"));
        mGroups.add(new DiagnosisGroup("M30-M36", "Inflammatoriska systemsjukdomar"));
        mGroups.add(new DiagnosisGroup("M40-M43", "Deformerande ryggsjukdomar"));
        mGroups.add(new DiagnosisGroup("M45-M49", "Spondylopatier"));
        mGroups.add(new DiagnosisGroup("M50-M54", "Andra ryggsjukdomar"));
        mGroups.add(new DiagnosisGroup("M60-M63", "Muskelsjukdomar"));
        mGroups.add(new DiagnosisGroup("M65-M68", "Sjukdomar i ledhinnor och senor"));
        mGroups.add(new DiagnosisGroup("M70-M79", "Andra sjukdomar i mjukvävnader"));
        mGroups.add(new DiagnosisGroup("M80-M85", "Rubbningar i bentäthet och benstruktur"));
        mGroups.add(new DiagnosisGroup("M86-M90", "Andra sjukdomar i benvävnad"));
        mGroups.add(new DiagnosisGroup("M91-M94", "Sjukdomar i broskvävnad"));
        mGroups.add(new DiagnosisGroup("M95-M99", "Andra sjukdomar i muskuloskeletala systemet och bindväven"));
        
        List<DiagnosisGroup> nGroups = new ArrayList<>();
        nGroups.add(new DiagnosisGroup("N00-N08", "Glomerulussjukdomar"));
        nGroups.add(new DiagnosisGroup("N10-N16", "Tubulo-interstitiella njursjukdomar"));
        nGroups.add(new DiagnosisGroup("N17-N19", "Njursvikt"));
        nGroups.add(new DiagnosisGroup("N20-N23", "Sten i urinvägarna"));
        nGroups.add(new DiagnosisGroup("N25-N29", "Andra sjukdomar i njure och urinledare"));
        nGroups.add(new DiagnosisGroup("N30-N39", "Andra sjukdomar i urinorganen"));
        nGroups.add(new DiagnosisGroup("N40-N51", "Sjukdomar i de manliga könsorganen"));
        nGroups.add(new DiagnosisGroup("N60-N64", "Sjukdomar i bröstkörtel"));
        nGroups.add(new DiagnosisGroup("N70-N77", "Inflammatoriska sjukdomar i de kvinnliga bäckenorganen"));
        nGroups.add(new DiagnosisGroup("N80-N98", "Icke inflammatoriska sjukdomar i de kvinnliga könsorganen"));
        nGroups.add(new DiagnosisGroup("N99-N99", "Andra sjukliga tillstånd i urin- och könsorganen"));

        List<DiagnosisGroup> oGroups = new ArrayList<>();
        oGroups.add(new DiagnosisGroup("O00-O08", "Graviditet som avslutas med abort"));
        oGroups.add(new DiagnosisGroup("O10-O16", "Ödem, proteinuri (äggvita i urinen) och hypertoni under graviditet, förlossning och barnsängstid"));
        oGroups.add(new DiagnosisGroup("O20-O29", "Andra sjukdomar hos den blivande modern i huvudsak sammanhängande med graviditeten"));
        oGroups.add(new DiagnosisGroup("O30-O48", "Vård under graviditet på grund av problem relaterade till fostret och amnionhålan samt befarade förlossningsproblem"));
        oGroups.add(new DiagnosisGroup("O60-O75", "Komplikationer vid värkarbete och förlossning"));
        oGroups.add(new DiagnosisGroup("O80-O84", "Förlossning"));
        oGroups.add(new DiagnosisGroup("O85-O92", "Komplikationer huvudsakligen sammanhängande med barnsängstiden"));
        oGroups.add(new DiagnosisGroup("O94-O99", "Andra obstetriska tillstånd som ej klassificeras annorstädes (O94-O99)"));
        
        List<DiagnosisGroup> pGroups = new ArrayList<>();
        pGroups.add(new DiagnosisGroup("P00-P04", "Foster och nyfödd som påverkats av tillstånd hos modern och av komplikationer vid graviditet, värkarbete och förlossning"));
        pGroups.add(new DiagnosisGroup("P05-P08", "Sjukdomar som har samband med graviditetslängd och fostertillväxt"));
        pGroups.add(new DiagnosisGroup("P10-P15", "Förlossningsskador"));
        pGroups.add(new DiagnosisGroup("P20-P29", "Sjukdomar i andningsorgan och cirkulationsorgan specifika för den perinatala perioden"));
        pGroups.add(new DiagnosisGroup("P35-P39", "Infektioner specifika för den perinatala perioden"));
        pGroups.add(new DiagnosisGroup("P50-P61", "Blödningssjukdomar och blodsjukdomar hos foster och nyfödd"));
        pGroups.add(new DiagnosisGroup("P70-P74", "Övergående endokrina rubbningar och ämnesomsättningsrubbningar specifika för foster och nyfödd"));
        pGroups.add(new DiagnosisGroup("P75-P78", "Sjukdomar i matsmältningsorganen hos foster och nyfödd"));
        pGroups.add(new DiagnosisGroup("P80-P83", "Tillstånd som engagerar hud och temperaturreglering hos foster och nyfödd"));
        pGroups.add(new DiagnosisGroup("P90-P96", "Andra sjukdomar och rubbningar under den perinatala perioden"));
        
        List<DiagnosisGroup> qGroups = new ArrayList<>();
        qGroups.add(new DiagnosisGroup("Q00-Q07", "Medfödda missbildningar av nervsystemet"));
        qGroups.add(new DiagnosisGroup("Q10-Q18", "Medfödda missbildningar av öga, öra, ansikte och hals"));
        qGroups.add(new DiagnosisGroup("Q20-Q28", "Medfödda missbildningar av cirkulationsorganen"));
        qGroups.add(new DiagnosisGroup("Q30-Q34", "Medfödda missbildningar av andningsorganen"));
        qGroups.add(new DiagnosisGroup("Q35-Q37", "Kluven läpp och gom"));
        qGroups.add(new DiagnosisGroup("Q38-Q45", "Andra medfödda missbildningar av matsmältningsorganen"));
        qGroups.add(new DiagnosisGroup("Q50-Q56", "Medfödda missbildningar av könsorganen"));
        qGroups.add(new DiagnosisGroup("Q60-Q64", "Medfödda missbildningar av urinorganen"));
        qGroups.add(new DiagnosisGroup("Q65-Q79", "Medfödda missbildningar och deformiteter av muskler och skelett"));
        qGroups.add(new DiagnosisGroup("Q80-Q89", "Andra medfödda missbildningar"));
        qGroups.add(new DiagnosisGroup("Q90-Q99", "Kromosomavvikelser som ej klassificeras annorstädes"));
        
        List<DiagnosisGroup> rGroups = new ArrayList<>();
        rGroups.add(new DiagnosisGroup("R00-R09", "Symtom och sjukdomstecken från cirkulationsorganen och andningsorganen"));
        rGroups.add(new DiagnosisGroup("R10-R19", "Symtom och sjukdomstecken från matsmältningsorganen och buken"));
        rGroups.add(new DiagnosisGroup("R20-R23", "Symtom och sjukdomstecken från huden och underhuden"));
        rGroups.add(new DiagnosisGroup("R25-R29", "Symtom och sjukdomstecken från nervsystemet och muskuloskeletala systemet"));
        rGroups.add(new DiagnosisGroup("R30-R39", "Symtom och sjukdomstecken från urinorganen"));
        rGroups.add(new DiagnosisGroup("R40-R46", "Symtom och sjukdomstecken avseende intellektuella funktioner, uppfattningsförmåga, känsloläge och beteende"));
        rGroups.add(new DiagnosisGroup("R47-R49", "Symtom och sjukdomstecken avseende talet och rösten"));
        rGroups.add(new DiagnosisGroup("R50-R69", "Allmänna symtom och sjukdomstecken"));
        rGroups.add(new DiagnosisGroup("R70-R79", "Onormala fynd vid blodundersökning utan diagnos"));
        rGroups.add(new DiagnosisGroup("R80-R82", "Onormala fynd vid urinundersökning utan diagnos"));
        rGroups.add(new DiagnosisGroup("R83-R89", "Onormala fynd vid undersökning av andra kroppsvätskor, substanser och vävnader, utan diagnos"));
        rGroups.add(new DiagnosisGroup("R90-R94", "Onormala fynd vid radiologisk diagnostik och vid funktionsundersökning utan diagnos"));
        rGroups.add(new DiagnosisGroup("R95-R99", "Ofullständigt definierade och okända orsaker till död"));

        List<DiagnosisGroup> sGroups = new ArrayList<>();
        sGroups.add(new DiagnosisGroup("S00-S09", "Skador på huvudet"));
        sGroups.add(new DiagnosisGroup("S10-S19", "Skador på halsen"));
        sGroups.add(new DiagnosisGroup("S20-S29", "Skador i bröstregionen"));
        sGroups.add(new DiagnosisGroup("S30-S39", "Skador i buken, nedre delen av ryggen, ländkotpelaren och bäckenet"));
        sGroups.add(new DiagnosisGroup("S40-S49", "Skador på skuldra och överarm"));
        sGroups.add(new DiagnosisGroup("S50-S59", "Skador på armbåge och underarm"));
        sGroups.add(new DiagnosisGroup("S60-S69", "Skador på handled och hand"));
        sGroups.add(new DiagnosisGroup("S70-S79", "Skador på höft och lår"));
        sGroups.add(new DiagnosisGroup("S80-S89", "Skador på knä och underben"));
        sGroups.add(new DiagnosisGroup("S90-S99", "Skador på fotled och fot"));
        sGroups.add(new DiagnosisGroup("T00-T07", "Skador som engagerar flera kroppsregioner"));
        sGroups.add(new DiagnosisGroup("T08-T14", "Skador på icke specificerad del av bålen, extremitet eller annan kroppsregion"));
        sGroups.add(new DiagnosisGroup("T15-T19", "Effekter av främmande kropp som trängt in genom naturlig öppning"));
        sGroups.add(new DiagnosisGroup("T20-T25", "Brännskador och frätskador på yttre kroppsyta med specificerad lokalisation"));
        sGroups.add(new DiagnosisGroup("T26-T28", "Brännskador och frätskador begränsade till ögat och inre organ"));
        sGroups.add(new DiagnosisGroup("T29-T32", "Brännskador och frätskador på multipla och icke specificerade kroppsregioner"));
        sGroups.add(new DiagnosisGroup("T33-T35", "Köldskada"));
        sGroups.add(new DiagnosisGroup("T36-T50", "Förgiftning av droger, läkemedel och biologiska substanser"));
        sGroups.add(new DiagnosisGroup("T51-T65", "Toxisk effekt av substanser med i huvudsak icke-medicinsk användning"));
        sGroups.add(new DiagnosisGroup("T66-T78", "Andra och icke specificerade effekter av yttre orsaker"));
        sGroups.add(new DiagnosisGroup("T79-T79", "Vissa tidiga komplikationer till skada genom yttre våld"));
        sGroups.add(new DiagnosisGroup("T80-T88", "Komplikationer till kirurgiska åtgärder och medicinsk vård som ej klassificeras annorstädes"));
        sGroups.add(new DiagnosisGroup("T90-T98", "Sena besvär av skador, förgiftningar och andra följder av yttre orsaker"));

        List<DiagnosisGroup> uGroups = new ArrayList<>();
        uGroups.add(new DiagnosisGroup("U00-U49", "Interimistiska koder för nya sjukdomar med osäker etiologi eller koder som kan tas i bruk med kort varsel"));
        uGroups.add(new DiagnosisGroup("U82-U85", "Resistens mot antimikrobiella och antineoplastiska läkemedel (U82-U85)"));
        uGroups.add(new DiagnosisGroup("U98-U99", "Koder för särskilda nationella behov"));
        
        List<DiagnosisGroup> vGroups = new ArrayList<>();
        vGroups.add(new DiagnosisGroup("V01-V09", "Fotgängare skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V10-V19", "Cyklist (förare eller passagerare) skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V20-V29", "Motorcyklist (förare eller passagerare) skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V30-V39", "Förare av eller passagerare i trehjuligt motorfordon skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V40-V49", "Förare av eller passagerare i personbil skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V50-V59", "Förare av eller passagerare i lätt lastbil skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V60-V69", "Förare av eller passagerare i tung lastbil skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V70-V79", "Förare av eller passagerare i buss skadad i transportolycka"));
        vGroups.add(new DiagnosisGroup("V80-V89", "Andra transportolyckor på land"));
        vGroups.add(new DiagnosisGroup("V90-V94", "Transportolyckor på vatten"));
        vGroups.add(new DiagnosisGroup("V95-V97", "Transportolyckor i luften och rymden"));
        vGroups.add(new DiagnosisGroup("V98-V99", "Andra och icke specificerade transportolyckor"));
        vGroups.add(new DiagnosisGroup("W00-W19", "Fallolyckor"));
        vGroups.add(new DiagnosisGroup("W20-W49", "Exponering för icke levande mekaniska krafter"));
        vGroups.add(new DiagnosisGroup("W50-W64", "Exponering för levande mekaniska krafter"));
        vGroups.add(new DiagnosisGroup("W65-W74", "Drunkning och drunkningstillbud genom olyckshändelse"));
        vGroups.add(new DiagnosisGroup("W75-W84", "Annan kvävning och annat kvävningstillbud genom olyckshändelse"));
        vGroups.add(new DiagnosisGroup("W85-W99", "Exponering för elektrisk ström, strålning, extrem lufttemperatur och extremt lufttryck i omgivningen"));
        vGroups.add(new DiagnosisGroup("X00-X09", "Exponering för rök och öppen eld"));
        vGroups.add(new DiagnosisGroup("X10-X19", "Kontakt med heta föremål och heta ämnen"));
        vGroups.add(new DiagnosisGroup("X20-X29", "Kontakt med giftiga djur och växter"));
        vGroups.add(new DiagnosisGroup("X30-X39", "Exponering för naturkrafter"));
        vGroups.add(new DiagnosisGroup("X40-X49", "Förgiftningsolyckor och exponering för skadliga ämnen genom olyckshändelse"));
        vGroups.add(new DiagnosisGroup("X50-X57", "Överansträngning och umbäranden"));
        vGroups.add(new DiagnosisGroup("X58-X59", "Exponering genom olyckshändelse för andra och icke specificerade faktorer"));
        vGroups.add(new DiagnosisGroup("X60-X84", "Avsiktligt självdestruktiv handling"));
        vGroups.add(new DiagnosisGroup("X85-Y09", "Övergrepp av annan person"));
        vGroups.add(new DiagnosisGroup("Y10-Y34", "Skadehändelser med oklar avsikt"));
        vGroups.add(new DiagnosisGroup("Y35-Y36", "Polisingripande och krigshandling"));
        vGroups.add(new DiagnosisGroup("Y40-Y59", "Läkemedel, droger och biologiska substanser i terapeutiskt bruk som orsak till ogynnsam effekt"));
        vGroups.add(new DiagnosisGroup("Y60-Y69", "Missöden som inträffat med patienter under kirurgisk och medicinsk vård"));
        vGroups.add(new DiagnosisGroup("Y70-Y82", "Missöden orsakade av medicinska instrument i diagnostiskt och terapeutiskt bruk"));
        vGroups.add(new DiagnosisGroup("Y83-Y84", "Kirurgiska och andra medicinska åtgärder som orsak till onormal reaktion eller sen komplikation hos patient utan anknytning till missöde vid operations- eller behandlingstillfället"));
        vGroups.add(new DiagnosisGroup("Y85-Y89", "Sena effekter av yttre orsaker till sjukdom och död"));
        vGroups.add(new DiagnosisGroup("Y90-Y98", "Bidragande faktorer som har samband med yttre orsaker till sjukdom och död, vilka klassificeras annorstädes"));
        
        List<DiagnosisGroup> zGroups = new ArrayList<>();
        zGroups.add(new DiagnosisGroup("Z00-Z13", "Kontakt med hälso- och sjukvården för undersökning och utredning"));
        zGroups.add(new DiagnosisGroup("Z20-Z29", "Potentiella hälsorisker avseende smittsamma sjukdomar"));
        zGroups.add(new DiagnosisGroup("Z30-Z39", "Kontakter med hälso- och sjukvården i samband med fortplantning"));
        zGroups.add(new DiagnosisGroup("Z40-Z54", "Kontakter med hälso- och sjukvården för speciella åtgärder och vård"));
        zGroups.add(new DiagnosisGroup("Z55-Z65", "Potentiella hälsorisker avseende socioekonomiska och psykosociala förhållanden"));
        zGroups.add(new DiagnosisGroup("Z70-Z76", "Kontakter med hälso- och sjukvården i andra situationer"));
        zGroups.add(new DiagnosisGroup("Z80-Z99", "Potentiella hälsorisker i familjens och patientens sjukhistoria samt vissa tillstånd och förhållanden som påverkar hälsan"));

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
