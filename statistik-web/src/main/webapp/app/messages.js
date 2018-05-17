/* jshint maxlen: false, unused: false */
var stMessages = {
    'sv': {
    	//generals
        'statistics.header': 'Intygsstatistik',

        //navigation
        'nav.national-header': 'Nationell statistik',
        'nav.landsting-header': 'Landstingsstatistik',
        'nav.business-header': 'Verksamhetsstatistik',
        'nav.about-header': 'Om tjänsten',

        'nav.sjukfall-header': 'Sjukfall',
        'nav.intyg-header': 'Intyg',
        'nav.kommunikation-header': 'Ärendekommunikation',

        'nav.oversikt': 'Översikt',
        'nav.sjukfall-totalt':'Sjukfall, totalt',
        'nav.diagnosgrupp':'Diagnosgrupp',
        'nav.enskilt-diagnoskapitel':'Enskilt diagnoskapitel',
        'nav.jamfor-diagnoser':'Jämför valfria diagnoser',
        'nav.jamfor-vanliga-diagnoser': 'Jämför valfria diagnoser',
        'nav.aldersgrupp':'Åldersgrupp',
        'nav.sjukskrivningsgrad':'Sjukskrivningsgrad',
        'nav.sjukskrivningslangd':'Sjukskrivningslängd',
        'nav.lan':'Län',
        'nav.lan-andel-sjukfall-per-kon':'Könsfördelning per län',
        'nav.vardenhet':'Vårdenhet',
        'nav.lakare':'Läkare',
        'nav.lakarbefattning':'Läkarbefattning',
        'nav.lakaralder-kon':'Läkarålder och -kön',
        'nav.sjukskrivningslangd-mer-an-90-dagar':'Mer än 90 dagar',
        'nav.allmant-om-tjansten': 'Allmänt om tjänsten',
        'nav.inloggning-behorighet': 'Inloggning och behörighet',
        'nav.faq': 'Vanliga frågor och svar',
        'nav.kontakt-support': 'Kontakt till support',
        'nav.landsting.filuppladdning': 'Filuppladdning',
        'nav.landsting.listningsjamforelse': 'Vårdenhet, listningar',
        'nav.landsting.om': 'Om landstingsstatistik',
        'nav.meddelanden': 'Meddelanden, totalt',
        'nav.meddelandenperamne': 'Meddelanden, totalt',
        'nav.meddelandenperamneochenhet': 'Vårdenhet',
        'nav.intygpertyp': 'Intyg, totalt',
        'nav.andelkompletteringar': 'Komplettering, andel intyg',

        //labels
        'lbl.mobile-menu': 'Meny',
        'lbl.log-in': 'Logga in',
        'lbl.log-out': 'Logga ut',
        'lbl.for-verksamhetsstatistik': 'För verksamhetsstatistik: ',
        'lbl.aterstall': 'Återställ',
        'lbl.clear': 'Rensa',
        'lbl.filtrera': 'Filtrera',
        'lbl.tabell': 'Tabell',
        'lbl.dolj-tabell': 'Dölj tabellen',
        'lbl.visa-tabell': 'Visa tabellen',
        'lbl.diagram': 'Diagram',
        'lbl.dolj-diagram': 'Dölj diagram',
        'lbl.visa-diagram': 'Visa diagram',
        'lbl.diagramtyp-tidsserie': 'Tidsserie',
        'lbl.diagramtyp-tvarsnitt': 'Tvärsnitt',


        //filter
        'lbl.filter.btn.intygstyper': 'Intygstyper',
        'lbl.filter.pdf.intygstyper': 'Valda intygstyper',
        'lbl.filter.btn.sjukskrivningslangd': 'Sjukskrivningslängder',
        'lbl.filter.pdf.sjukskrivningslangd': 'Valda sjukskrivningslängder',
        'lbl.filter.btn.aldersgrupp': 'Åldersgrupper',
        'lbl.filter.pdf.aldersgrupp': 'Valda åldersgrupper',
        'lbl.filter.pdf.diagnoser': 'Valda diagnoser',
        'lbl.filter.pdf.enheter': 'Valda enheter',
        'lbl.filter.pdf.enheteralla': 'Enheter',
        'lbl.filter.btn.datumintervall': 'Tidsintervall',
        'lbl.filter.btn.showall': 'Visa alla aktiva filter',
        'lbl.filter.inactive': 'Detta filter går inte att applicera på vald rapport.',

        'lbl.filter.verksamhet.link': 'Visa snabbval av enheter baserat på verksamhetstyper',
        'lbl.filter.verksamhet.link.collapse': 'Dölj snabbval av enheter baserat på verksamhetstyper',
        'lbl.filter.verksamhet.search.hint': 'Sök efter län, kommun eller enhet...',
        'lbl.filter.verksamhet.quickselection.title': 'Välj alla enheter med verksamhetstyp',
        'lbl.filter.verksamhet.select-all': 'Markera alla enheter',
        'lbl.filter.modal.title': 'Aktiva filter',
        'lbl.filter.val-av-enheter': 'Välj enheter',
        'lbl.filter.val-av-diagnoser': 'Välj diagnoser',
        'lbl.filter.val-av-diagnoser.select-all': 'Markera alla diagnoser',
        'lbl.filter.val-av-diagnoser.search.hint': 'Sök efter diagnos...',
        'lbl.filter.val-av-tidsintervall-fran': 'Från',
        'lbl.filter.val-av-tidsintervall-till': 'Till',
        'lbl.filter.val-av-diagnoser-knapp': 'Diagnoser',
        'lbl.filter.modal.lan': 'Län',
        'lbl.filter.modal.kommuner': 'Kommuner',
        'lbl.filter.modal.enheter': 'Enheter',
        'lbl.filter.modal.kapitel': 'Diagnoskapitel',
        'lbl.filter.modal.avsnitt': 'Diagnosavsnitt',
        'lbl.filter.modal.kategorier': 'Diagnoskategorier',
        'lbl.filter.modal.leaves': 'Diagnoskoder',
        'lbl.filter.modal.spara-stang': 'Spara och stäng',
        'alert.filter.date-invalid': 'Du har angett en månad som inte finns. Kontrollera att du fyllt i rätt månad.',
        'alert.filter.date.wrong-order': 'Tidsintervallens fråndatum får inte inträffa senare än tilldatum. Vänligen ändra från- eller tilldatum.',
        'alert.filter.date.empty': 'Ett datumfält verkar vara tomt. Du behöver fylla i båda datumfälten eller lämna båda tomma.',
        'alert.filter.date.before': 'Det finns ingen statistik innan ${date}. Vänligen ange ett senare datum.',
        'alert.filter.date.after': 'Det finns ingen statistik för framtiden. Vänligen ange ett tidigare datum.',

        //Multi-select
        'info.multiselect.loading': 'Laddar information, var god vänta.',

        //national statistics overview
        'national.overview-header': 'Nationell statistik',
        'national.overview-header2': 'Utvecklingen i Sverige de senaste tre månaderna, ',
        'national.widget.header.konsfordelning': 'Fördelning mellan kön',
        'national.widget.header.forandring': 'Förändring',
        'national.widget.total-antal.help': 'Totalt antal sjukfall under perioden ',
        'national.widget.header.fordelning-diagnosgrupper': 'Fördelning diagnosgrupper',
        'national.widget.header.fordelning-aldersgrupper': 'Fördelning åldersgrupper',
        'national.widget.header.fordelning-sjukskrivningsgrad': 'Fördelning sjukskrivningsgrad',
        'national.widget.header.fordelning-sjukskrivningslangd': 'Fördelning sjukskrivningslängd',
        'national.widget.header.fordelning-lan': 'Fördelning per län',

        //business statistics overview
        'business.overview-header': 'Verksamhetsstatistik',
        'business.widget.header.total-antal': 'Totalt antal',
        'business.widget.header.konsfordelning-sjukfall': 'Fördelning mellan kön',
        'business.widget.header.fordelning-diagnosgrupper': 'Fördelning diagnosgrupper',
        'business.widget.header.fordelning-aldersgrupper': 'Fördelning åldersgrupper',
        'business.widget.header.fordelning-sjukskrivningsgrad': 'Fördelning sjukskrivningsgrad',
        'business.widget.header.fordelning-sjukskrivningslangd': 'Fördelning sjukskrivningslängd',

        //general overview
        'overview.widget.table.column.diagnosgrupp': 'Diagnosgrupp',
        'overview.widget.table.column.aldersgrupp': 'Åldersgrupp',
        'overview.widget.table.column.sjukskrivningsgrad': 'Sjukskrivningsgrad',
        'overview.widget.table.column.lan': 'Län',
        'overview.widget.table.column.antal': 'Antal',
        'overview.widget.table.column.forandring': 'Förändring',
        'overview.widget.fordelning-sjukskrivningslangd.overgar-90': 'Antal sjukfall som är 91 dagar eller längre.',
        'overview.widget.fordelning-sjukskrivningslangd.overgar-90-3-manader': 'Procentuell förändring av antal sjukfall som är längre än 90 dagar jämfört med samma tremånadersperiod föregående år.',

        //detail views
        'lbl.valj-annat-diagnoskapitel': 'Välj diagnoskapitel:',
        'lbl.valj-annat-diagnosavsnitt': 'Välj diagnosavsnitt',
        'lbl.valj-annan-diagnoskategori': 'Välj diagnoskategori',
        'button.label.save-as-pdf': 'Spara som PDF',
        'dropdown.val.rapport': 'Spara som',
        'dropdown.val.skrivut-pdf': ' PDF',
        'dropdown.val.spara-diagram-bild-1': ' Bild (',
        'dropdown.val.spara-diagram-bild-2': ')',
        'dropdown.val.spara-excel': ' Excel',
        'alert.diagnosgrupp.information': 'För en given månad kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg under samma månad så hämtas diagnos från varje intyg. Om intygen har olika diagnosgrupper kommer sjukfallet finnas med en gång för varje diagnosgrupp för respektive månad. Exempel: Om ett sjukfall innehåller två intyg för maj månad, där intyg ett sätter diagnosen M54 och intyg två efter vidare utredning sätter diagnosen F32, så kommer sjukfallet både räknas med i gruppen för Muskuloskeleta sjukdomar (M00-M99) och i gruppen för Psykiska sjukdomar (F00-F99) i graf och tabell för maj månad.',
        'alert.dxs.max.selected': 'Du har valt fler än %0 %1 vilket gör att diagrammet inte kommer kunna visas. Överväg att filtrera ytterligare eller gå vidare för att se statistiken i tabellformat utan diagram.',

        'comparediagnoses.choose-level': 'Välj indelning:',
        'comparediagnoses.level.section': 'Diagnosavsnitt',
        'comparediagnoses.level.chapter': 'Diagnoskapitel',
        'comparediagnoses.level.category': 'Diagnoskategorier',
        'comparediagnoses.level.code': 'Diagnoskoder',
        'comparediagnoses.lbl.val-av-diagnoser': 'Välj diagnoser som din rapport ska baseras på:',

        'table.warning.title' : 'Varning!',
        'table.warning.text' : 'Tabellen kan inte visas i sin helhet. Spara ner tabellen som Excel för att se all data.',

        // Titles
        'title.lan': 'Antal sjukfall per 1000 invånare fördelat på län',
        'title.lan.gender': 'Andel sjukfall per kön fördelat på län',
        'title.vardenhet': 'Antal sjukfall fördelat på vårdenhet',
        'title.sickleave': 'Antal sjukfall',
        'title.diagnosisgroup': 'Antal sjukfall fördelat på diagnosgrupp',
        'title.diagnosgroup': 'Antal sjukfall för',
        'title.diagnoscompare': 'Jämförelse av valfria diagnoser',
        'title.agegroup': 'Antal sjukfall fördelat på åldersgrupp',
        'title.degreeofsickleave': 'Antal sjukfall fördelat på sjukskrivningsgrad',
        'title.sickleavelength': 'Antal sjukfall fördelat på sjukskrivningslängd',
        'title.sickleavelength90': 'Antal sjukfall som är längre än 90 dagar',
        'title.lakare': 'Antal sjukfall fördelat på läkare',
        'title.lakaregender': 'Antal sjukfall fördelat på läkares ålder och kön',
        'title.lakare-befattning': 'Antal sjukfall fördelat på läkarbefattning',
        'title.differentierat': 'Antal sjukfall inom differentierat intygande',
        'title.vardenhet-listning': 'Antal sjukfall per 1000 listningar fördelat på vårdenhet',
        'title.meddelanden' : 'Antal inkomna meddelanden',
        'title.meddelandenperamne' : 'Antal inkomna meddelanden fördelat på ämne',
        'title.meddelandenperamneochenhet' : 'Antal inkomna meddelanden fördelat på vårdenhet',
        'title.intyg' : 'Antal intyg',
        'title.intygstyp' : 'Antal utfärdade intyg fördelat på intygstyp',
        'title.andelkompletteringar' : 'Andel utfärdade intyg som fått en kompletteringsbegäran',

        //help texts
        'help.nationell.overview' : 'Intygsstatistik är en webbtjänst som visar samlad statistik över alla elektroniskt utfärdade läkarintyg och läkarutlåtanden. Tjänsten visar dessutom statistik över den tillhörande ärendekommunikationen som sker emellan intyg-/utlåtandeutfärdare och Försäkringskassan. Statistiken är uppdelad i nationell statistik som är tillgänglig för alla, och verksamhetsstatistik som bara går att se med särskild behörighet inom hälso- och sjukvården.',
        'help.nationell.diagnosisgroup': 'Denna rapport visar statistik uppdelad i sju övergripande diagnosgrupper. I varje grupp ingår olika kapitel med diagnoskoder. Diagnoskoderna kommer från klassificeringssystemet ICD-10-SE som används för att gruppera sjukdomar för att kunna göra översiktliga statistiska sammanställningar och analyser.',
        'help.nationell.diagnosgroup': 'I denna rapport kan du själv välja vilket diagnoskapitel du vill se statistik för. Klassificeringssystemet ICD-10-SE delar in alla diagnoser i 22 diagnoskapitel.',
        'help.nationell.degreeofsickleave': 'Denna rapport visar statistik fördelad på sjukskrivningsgrad, vilket motsvarar hur stor del av patientens arbetsförmåga som är nedsatt. Sjukskrivningsgraden anges i procent i förhållande till patientens aktuella arbetstid.',
        'help.nationell.sickleavelength': 'Denna rapport visar statistik fördelad på sjukfallens längd. Längden räknas fram utifrån de sjukskrivningsperioder som anges i de läkarintyg som sjukfallet består av.',
        'help.nationell.lan': 'Denna rapport visar statistik fördelad på län. Information om vilket län en enhet som utfärdar intyg ligger i hämtas från HSA-katalogen. Uppgifterna i HSA är inte kvalitetssäkrade av Intygsstatistik och information kan saknas då det inte är obligatoriskt för vårdenheten att ange länstillhörighet.',
        'help.nationell.lan.gender': 'Denna rapport visar statistik över könsfördelningen för sjukfall fördelat på län. Information om vilket län en enhet som utfärdar intyg ligger i hämtas från HSA-katalogen. Uppgifterna i HSA är inte kvalitetssäkrade av Intygsstatistik och information kan saknas då det inte är obligatoriskt för vårdenheten att ange länstillhörighet.',
        'help.nationell.meddelandenperamne': 'Denna rapport visar statistik fördelad på meddelandens ämne. Ämnet indikerar syftet med meddelandet och sätts av Försäkringskassan. Observera att statistiken i denna rapport endast innefattar de meddelanden som tillhör Försäkringskassans intyg (med undantag för FK 7263).',

        'help.verksamhet.vardenhet': 'Denna rapport visar statistik för de vårdenheter som du har behörighet till och som utfärdar läkarintyg. Vårdenheternas namn hämtas från HSA-katalogen.',
        'help.verksamhet.diagnosgroup': 'I denna rapport kan du själv välja vilken typ av diagnoser du vill se statistik för. Klassificeringssystemet ICD-10-SE delar in alla diagnoser i 22 diagnoskapitel. Varje diagnoskapitel innehåller flera diagnosavsnitt som i sin tur omfattar än mer detaljerade diagnoskategorier.',
        'help.verksamhet.sickleavelength': 'Om ett sjukfall startat på en annan vårdenhet inom samma vårdgivare räknas den totala sjukskrivningslängden ihop.',
        'help.verksamhet.lakare': '	Denna rapport visar statistik för enskilda intygsskrivande läkare. Namn hämtas från HSA-katalogen. Om läkaren inte går att slå upp i HSA-katalogen eller om läkaren inte har något namn angivet så visas istället läkarens HSA-id.',
        'help.verksamhet.lakaregender': 'Denna rapport visar statistik uppdelad i olika läkargrupper utifrån den intygsskrivande läkarens ålder och kön. Information om ålder och kön hämtas från HSA-katalogen.',
        'help.verksamhet.lakare-befattning': 'Denna rapport visar statistik fördelad på befattning hos den intygsskrivande läkaren. Information om befattning hämtas från HSA-katalogen. Uppgifterna i HSA är inte kvalitetssäkrade av Intygsstatistik och information kan saknas då det inte är obligatoriskt för vårdenheten att ange befattning. Om en läkare har flera olika läkarbefattningar räknas sjukfallet med i statistiken för var och en av de befattningarna.',
        'help.verksamhet.meddelandenperamneochenhet': 'Denna rapport visar statistik för de vårdenheter som du har behörighet till och som utfärdar läkarintyg och läkarutlåtanden. Vårdenheternas namn hämtas från HSA-katalogen. Observera att statistiken i denna rapport endast innefattar de meddelanden som tillhör Försäkringskassans intyg (med undantag för FK 7263).',

        'help.landsting.sjukfall-totalt': 'Denna rapport visar sammanlagd statistik för de vårdenheter som tillhör landstinget och som utfärdar läkarintyg. Information om vilka vårdenheter som ingår i landstinget har rapporteras in av landstinget självt %0.',
        'help.landsting.vardenhet': 'Denna rapport visar statistik för de vårdenheter som tillhör landstinget och som utfärdar läkarintyg. Information om vilka vårdenheter som ingår i landstinget har rapporterats in av landstinget självt %0. Vårdenheternas namn hämtas från HSA-katalogen.',
        'help.landsting.vardenhet-listning1': 'Denna rapport visar statistik för de vårdenheter som tillhör landstinget och för vilka antal listade patienter rapporterats in. Antal sjukfall per 1000 listningar är ett mått som gör att det går att jämföra antalet sjukfall på olika vårdenheter trots att vårdenheterna inte är lika stora. Detta eftersom antalet sjukfall sätts i relation till antalet listade patienter.',
        'help.landsting.vardenhet-listning2': 'Information om vilka vårdenheter som ingår i landstinget och uppgift om antal listade patienter på respektive vårdenhet har rapporteras in av landstinget självt %0. Vårdenheternas namn hämtas från HSA-katalogen.',
        'help.landsting.meddelandenperamne': 'Denna rapport visar sammanlagd statistik för de vårdenheter som tillhör landstinget och som utfärdar läkarintyg. Information om vilka vårdenheter som ingår i landstinget har rapporteras in av landstinget självt %0. Ämnet på meddelanden sätts av Försäkringskassan. Observera att statistiken i denna rapport endast innefattar de meddelanden som tillhör Försäkringskassans intyg (med undantag för FK 7263).',
        'help.landsting.meddelandenperamneochenhet': 'Denna rapport visar statistik för de vårdenheter som tillhör landstinget och som utfärdar läkarintyg och läkarutlåtanden. Information om vilka vårdenheter som ingår i landstinget har rapporterats in av landstinget självt %0. Vårdenheternas namn hämtas från HSA-katalogen. Observera att statistiken i denna rapport endast innefattar de meddelanden som tillhör Försäkringskassans intyg (med undantag för FK 7263).',
        'help.landsting.intygpertyp': 'Denna rapport visar sammanlagd statistik för de vårdenheter som tillhör landstinget och som utfärdar läkarintyg. Information om vilka vårdenheter som ingår i landstinget har rapporteras in av landstinget självt %0.',

        'help.diagnosissubgroup.showdetailoptions' : 'Observera att om du väljer diagnoskategori visas endast de sjukfall som har en diagnos på fyr- eller femställig nivå i ICD-10-SE, t.ex. F430 eller F438A. Sjukfall med en diagnos på treställig nivå, t.ex. F43, visas inte.',

        //info texts
        'info.emptyresponse' : 'Ingen data tillgänglig. Det beror på att det inte finns någon data för verksamheten.',
        'info.hidden-table': 'Rapporten innehåller mycket data, vilket kan göra tabellen svår att läsa och långsam att ladda, därför är den dold. Filtrera för att minska mängden data och öka läsbarheten eller klicka nedan för att visa tabellen.',

        //login view
        'login.header.verksamhet': 'Logga in för verksamhetsstatistik',
        'login.header': 'Logga in',
        'login.alert-info.behorighet': 'Det verkar som att du saknar medarbetaruppdraget "Statistik" i HSA.',
        'login.alert-inaktivitet': 'Du har loggats ut på grund av inaktivitet. Vänligen logga in på nytt.',
        'login.alert-info': 'Info!',
        'login.alert-danger': 'Ajdå!',
        'login.instruktioner-for-login': 'För att logga in behöver du ett SITHS-kort samt medarbetaruppdraget "Statistik" i HSA-katalogen.',
        'login.btn.siths-login': 'Logga in',
        'login.siths.problem': 'Problem med inloggning med SITHS-kort?',
        'login.siths.problem-link': 'Läs mer om hur du kan felsöka.',

        'selectvg.header': 'Välj vårdgivare',
        'login.instruktioner-for-valj-vg': 'Du har behörighet till vårdenheter som tillhör flera olika vårdgivare. Välj den vårdgivare som du vill se verksamhetsstatistik för. <br> Observera att du bara kan se statistik för de vårdenheter som du har behörighet till inom vald vårdgivare.',
        'login.valj-vg.help.text' : 'Beräkningen av statistik baseras på personuppgifter i de elektroniska intyg som hälso- och sjukvården utfärdar. Med beaktande av sekretessen inom hälso- och sjukvården är det i huvudregel förbjudet att lämna ut och samköra personuppgifter från olika vårdgivare. Lagregler möjliggör inte utlämnande av personuppgifter mellan vårdgivare vilket resulterar i att användare av Intygsstatistik inte kan få tillgång till uppgifter från olika vårdgivare utan att genomföra separata val.',
        'login.valj-vg.help.link': 'Varför behöver jag välja vårdgivare?',
        'login.valj-vg.change-after-login' : 'Du kan byta mellan olika vårdgivare även efter inloggning.',
        'login.valj-vg.title' : 'Välj vårdgivare',
        'login.valj-vg.change': 'Byt vårdgivare',

        //about texts
        'about.service': 'Om Intygsstatistik',
        'about.cookies': '<h3>Om kakor (cookies)</h3><p>Så kallade kakor (cookies) används för att underlätta för besökaren på webbplatsen. En kaka är en textfil som lagras på din dator och som innehåller information. Denna webbplats använder så kallade sessionskakor. Sessionskakor lagras temporärt i din dators minne under tiden du är inne på en webbsida. Sessionskakor försvinner när du stänger din webbläsare. Ingen personlig information om dig sparas vid användning av sessionskakor.</p><p>Om du inte accepterar användandet av kakor kan du stänga av det via din webbläsares säkerhetsinställningar. Du kan även ställa in webbläsaren så att du får en varning varje gång webbplatsen försöker sätta en kaka på din dator.</p><p><strong>Observera!</strong> Om du stänger av kakor i din webbläsare kan du inte logga in i Webcert.</p><p>Allmän information om kakor (cookies) och lagen om elektronisk kommunikation finns på Post- och telestyrelsens webbplats.</p><p><LINK:ptsCookiesMerOm></p>',

        //error messages
        'error.unsignedcerts.couldnotbeloaded': '<strong>Kunde inte hämta ej signerade intyg.</strong>',

        //file upload
        'upload.filesize.error': 'Filen är för stor. Max tillåten storlek är {{maxFilesize}}MB',
        'upload.filetype.error': 'Felaktig filtyp. Formaten som stöds är .xls och .xlsx',
        'alert.upload.failed': 'Uppladdningen misslyckades!',
        'alert.upload.success': 'Uppladdningen lyckades!',
        'info.upload.dropfile.here': 'Släpp fil här för att ladda upp eller ',
        'info.upload.dropfile.here.add': 'lägg till',
        'info.upload.fallbacktext': 'Din webläsare stödjer inte filuppladdning via "dra och släpp". Vänligen använd knappen nedan för att ladda upp filen.',

        'faq.stats.1.title': 'Vad innehåller Intygsstatistik?',
        'faq.stats.1.body': '<p>Innehållet i Intygsstatistik baseras främst på de elektroniska läkarintyg som utfärdas när en läkare ' +
            'bedömer att en individ behöver exempelvis sjukskrivas eller är i behov av aktivitetsersättning. ' +
            'Tjänsten innehåller information från samtliga elektroniska läkarintyg som utfärdats inom hälso- och sjukvården efter oktober 2013. ' +
            'Intygsstatistik registrerar inte utfallet av intyget, det vill säga vilken bedömning Försäkringskassan gör.</p>' +
            '<p>Utöver läkarintygen innehåller Intygsstatistik dessutom statistik över den ärendekommunikation som sker mellan intygsutfärdare och Försäkringskassan. ' +
            'Tjänsten innehåller information från samtliga meddelanden som har skickats elektroniskt som ett ärende från Försäkringskassan till intygsutfärdaren.</p>' +
            '<p>Utifrån intygsinformationen hämtar Intygsstatistik därtill mer information om läkaren som skrivit intyget och om vårdenheten som han eller hon arbetar på. ' +
            'Dessa uppgifter hämtas från HSA-katalogen och syftet är att kunna ta fram mer värdefull och intressant statistik.</p>',

        'faq.stats.2.title': 'Vilka intyg filtreras bort från statistiken?',
        'faq.stats.2.body': '<p>Läkarintyg som inte motsvarar Intygsstatistiks kvalitetskrav filtreras bort och kommer inte med i några statistikrapporter. ' +
            'Filtreringen görs för att öka kvaliteten i den resulterande statistiken.</p>' +
            '<p>Kraven som ställs på läkarintyg för att de ska kunna vara del av statistiken är:</p>' +
            '<ul>' +
            '<li>Patientens födelsedatum i läkarintyget (utifrån personnummer eller samordningsnummer) måste motsvara ett riktigt datum.</li>' +
            '<li>Intyget får inte vara makulerat.</li>' +
            '<li>Den enhet inom hälso- och sjukvården som utfärdar intyget måste finnas i den nationella HSA-katalogen ' +
            '(en elektronisk katalog som innehåller kvalitetssäkrade uppgifter om bland annat enheter och personer i hälso- och sjukvården).</li>' +
            '<li>Den enhet inom hälso- och sjukvården som utfärdar intyget måste vara kopplad till en vårdgivare i den nationella HSA-katalogen.</li>' +
            '</ul>' +
            '<p>Ytterligare krav som ställs endast på Läkarintyg för sjukpenning är:</p>' +
            '<ul>' +
            '<li>Startdatumet för sjukskrivningsperioden måste vara efter 2009-12-31.</li>' +
            '<li>Start- eller slutdatum för sjukskrivningsperioden måste vara mindre än fem år fram i tiden.</li>' +
            '<li>Slutdatum för sjukskrivningsperioden måste vara efter startdatumet.</li>' +
            '</ul>',
        'faq.stats.3.title': 'Vad är nationell statistik?',
        'faq.stats.3.body': '<p>Nationell statistik är statistik för alla elektroniska läkarintyg som utfärdats samt alla elektroniska meddelanden som har mottagits av landets olika vårdgivare. ' +
                'Statistiken avidentifieras och lämnas ut från respektive vårdgivare för att sammanställas på nationell nivå. ' +
                'För att statistiken inte ska kunna kopplas till enskilda personer så appliceras tröskelvärden i den nationella statistiken.</p>' +
            '<p>Tröskelvärden innebär att om en vårdgivare har färre än fem sjukfall/intyg/meddelanden i en viss grupp räknas inte dessa in i statistiken. ' +
            'Det kan leda till en liten felmarginal i statistiken då det presenterade antalet kan vara något lägre än det faktiska antalet på grund av att vissa sjukfall/intyg/meddelanden kan ha filtrerats bort.</p>',

        'faq.stats.4.title': 'Varför ser jag ingen statistik för den valda verksamheten?',
        'faq.stats.4.body': '<p>Det beror på att det i dagsläget inte finns inrapporterad data för verksamheten eller att du gjort ett för begränsat filterval.</p>',


        'faq.report.1.title': 'Hur skriver jag ut en rapport?',
        'faq.report.1.body': '<p>På varje rapportsida finns en knapp med texten ”Spara som” ovanför diagrammet. ' +
            'När du väljer "PDF" laddas ett pdf-dokument ner till din dator. Dokumentet innehåller rapportens diagram, tabell och uppgifter om eventuella filtreringar. ' +
            'När dokumentet laddats ner kan du öppna det och skriva ut det.</p>',

        'faq.report.2.title': 'Hur exporterar jag tabellen till Excel?',
        'faq.report.2.body': '<p>På varje rapportsida finns en knapp med texten ”Spara som” ovanför diagrammet. ' +
            'När du väljer ”Excel” laddas tabellen ner till din dator och går sedan att öppna i Excel.</p>',

        'faq.sickness.1.title': 'Vad är ett sjukfall?',
        'faq.sickness.1.body': '<p>Intygsstatistik omfattar ett sjukfall de elektroniska läkarintyg (FK 7263 och FK 7804) som utfärdats för en viss patient vid en ' +
            'sjukskrivning och som följer på varandra med max fem dagars uppehåll. ' +
            'Intygen måste även vara utfärdade av samma vårdgivare för att räknas till samma sjukfall. ' +
            'Om det är mer än fem dagar mellan två intyg eller om två intyg är utfärdade av olika vårdgivare, så räknas det som två sjukfall.</p>' +
            '<p>Exempel: Om intyg 1 gäller till den 14 augusti och intyg 2 gäller från den 17 augusti räknas de båda intygen till samma sjukfall. ' +
            'Om intyg 2 istället hade varit giltigt från den 21 augusti skulle intygen ha räknats som två skilda sjukfall.</p>',


        'faq.technical.1.title': 'Hur ofta uppdateras Intygsstatistik?',
        'faq.technical.1.body': '<p>Intygsstatistik uppdateras löpande med nya läkarintyg och meddelanden. Den nationella statistiken uppdateras vid varje månadsskifte. ' +
            'Statistiken för verksamhetsuppföljning som kräver inloggning uppdateras en gång per dygn.</p>',

        'faq.technical.2.title': 'Vilka olika typer av behörighet finns för att logga in i Intygsstatistik?',
        'faq.technical.2.body': '<p>Den allmänna delen av Intygsstatistik som visar nationell statistik kräver inte någon registrering eller behörighet. ' +
            'Om du har ansvar för verksamhetsuppföljning inom hälso- och sjukvården kan du ta del av statistik som är särskilt riktad till ditt uppföljningsområde. ' +
            'Uppföljningsområde kan vara en eller flera vårdenheter, eller hela vårdgivaren om du har ett övergripande uppföljningsansvar. ' +
            'Det krävs att du har ett medarbetaruppdrag för statistik som delas ut av din verksamhetschef och sätts av din HSA-administratör. För att kunna logga in och se statistik krävs:</p>' +
            '<ul>' +
            '<li>ett SITHS-kort med pinkod</li>' +
            '<li>en kortläsare med tillhörande programvara, NetID</li>' +
            '<li>ett medarbetaruppdrag i HSA som ger dig rätt att ta del av statistik</li>' +
            '</ul>' +
            '<p><LINK:sarskildBehorighetStatistik></p>'

    },
    'en': {
        'webcert.header': 'Statistics Application (en)'
    }
};
