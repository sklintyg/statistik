/* jshint maxlen: false, unused: false */
var stMessages = {
    'sv': {
    	//generals
        'statistics.header': 'Statistiktjänsten',
        'statistics.header.extra-text': 'Statistiktjänst för ordinerad sjukskrivning',
        'statistics.hidden-header.sidans-huvudnavigering': 'Sidans huvudnavigering',
        'statistics.hidden-header.nationell-navigering': 'Navigering för nationell statistik',
        'statistics.hidden-header.landsting-navigering': 'Navigering för landstingsstatistik',
        'statistics.hidden-header.business-navigering': 'Navigering för verksamhetsstatistik',
        'statistics.hidden-header.about-navigering': 'Navigering för information om tjänsten',
        
        //navigation
        'nav.national-header': 'Nationell statistik',
        'nav.landsting-header': 'Landstingsstatistik',
        'nav.business-header': 'Verksamhetsstatistik',
        'nav.about-header': 'Om tjänsten',
        'nav.oversikt': 'Översikt',
        'nav.sjukfall-totalt':'Sjukfall, totalt',
        'nav.diagnos':'Diagnos',
        'nav.diagnosgrupp':'Diagnosgrupp',
        'nav.enskilt-diagnoskapitel':'Enskilt diagnoskapitel',
        'nav.jamfor-diagnoser':'Jämför valfria diagnoser',
        'nav.tio-vanligaste-diagnoserna': 'Tio omfattande diagnoser',
        'nav.jamfor-vanliga-diagnoser': 'Jämför valfria diagnoser',
        'nav.aldersgrupp':'Åldersgrupp',
        'nav.sjukskrivningsgrad':'Sjukskrivningsgrad',
        'nav.sjukskrivningslangd':'Sjukskrivningslängd',
        'nav.lan':'Län',
        'nav.andel-per-kon': 'Andel per kön',
        'nav.lan-andel-sjukfall-per-kon':'Andel sjukfall per kön',
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
        'nav.mobile.trigger.diagnosgrupp-diagnoskapitel': 'Diagnosgrupp, enskilt diagnoskapitel och jämför valfria diagnoser',
        'nav.mobile.trigger.lan-andel-per-kon': 'Län och andel sjukfall per kön',
        'nav.mobile.trigger.sjukskrivningslangd-90-dagar': 'Sjukskrivningslängd och mer än 90 dagar',

        //labels
        'lbl.mobile-menu': 'Meny',
        'lbl.log-in': 'Logga in',
        'lbl.log-out': 'Logga ut',
        'lbl.for-verksamhetsstatistik': 'För verksamhetsstatistik: ',
        'lbl.aterstall': 'Återställ',
        'lbl.sok': 'Sök',
        'lbl.gor-urval': 'Gör urval',
        'lbl.dolj-tabell': 'Dölj tabell',
        'lbl.visa-tabell': 'Visa tabell',
        'lbl.visa-knappgrupp-serier': 'Visa som',
        'lbl.diagramtyp-knappgrupp': 'Typ av diagram',


        //filter
        'lbl.filter.valj-verksamhetstyper': 'Välj verksamhetstyper:',
        'lbl.filter.val-av-enheter': 'Välj enheter:',
        'lbl.filter.val-av-diagnoser': 'Välj diagnoser:',
        'lbl.filter.val-av-tidsintervall': 'Välj tidsintervall',
        'lbl.filter.val-av-tidsintervall-fran': 'Från:',
        'lbl.filter.val-av-tidsintervall-till': 'Till:',
        'lbl.filter.val-av-diagnoser-knapp': 'Diagnoser',
        'lbl.filter.select-all': 'Markera alla',
        'lbl.filter.modal.lan': 'Län: ',
        'lbl.filter.modal.kommuner': 'Kommuner: ',
        'lbl.filter.modal.enheter': 'Enheter: ',
        'lbl.filter.modal.kapitel': 'Kapitel: ',
        'lbl.filter.modal.avsnitt': 'Avsnitt: ',
        'lbl.filter.modal.kategorier': 'Kategorier: ',
        'lbl.filter.modal.spara-stang': 'Spara och stäng',
        'lbl.filter.sum-progress-1': 'Genom filtreringen visas statistik från ',
        'lbl.filter.sum-progress-2': ' av totalt ',
        'lbl.filter.sum-progress-3': ' vårdenheter.',
        'alert.filter.date-invalid': 'Felaktigt tidsintervall har angetts',

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
        'dropdown.val.oversikt': 'Skriv ut',
        'overview.widget.table.column.diagnosgrupp': 'Diagnosgrupp',
        'overview.widget.table.column.aldersgrupp': 'Åldersgrupp',
        'overview.widget.table.column.sjukskrivningsgrad': 'Sjukskrivningsgrad',
        'overview.widget.table.column.lan': 'Län',
        'overview.widget.table.column.antal': 'Antal',
        'overview.widget.table.column.forandring': 'Förändring',
        'overview.widget.fordelning-sjukskrivningslangd.overgar-90': 'Antal sjukfall som är 91 dagar eller längre.',
        'overview.widget.fordelning-sjukskrivningslangd.overgar-90-3-manader': 'Procentuell förändring av antal sjukfall som är längre än 90 dagar jämfört med föregående tre månader.',
        
        //detail views
        'lbl.valj-annat-diagnoskapitel': 'Välj diagnoskapitel:',
        'lbl.valj-annat-diagnosavsnitt': 'Välj diagnosavsnitt',
        'lbl.valj-annan-diagnoskategori': 'Välj diagnoskategori',
        'dropdown.val.rapport': 'Spara/Skriv ut',
        'dropdown.val.skrivut-farg': ' Skriv ut i färg',
        'dropdown.val.skrivut-svart-vitt': ' Skriv ut i svartvitt',
        'dropdown.val.spara-diagram-bild-1': ' Spara ',
        'dropdown.val.spara-diagram-bild-2': ' som bild',
        'dropdown.val.spara-excel': ' Spara tabell till Excel',
        'alert.diagnosgrupp.information': 'För en given månad kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg under samma månad så hämtas diagnos från varje intyg. Om intygen har olika diagnosgrupper kommer sjukfallet finnas med en gång för varje diagnosgrupp för respektive månad. Exempel: Om ett sjukfall innehåller två intyg för maj månad, där intyg ett sätter diagnosen M54 och intyg två efter vidare utredning sätter diagnosen F32, så kommer sjukfallet både räknas med i gruppen för Muskuloskeleta sjukdomar (M00-M99) och i gruppen för Psykiska sjukdomar (F00-F99) i graf och tabell för maj månad.',
        'comparediagnoses.lbl.val-av-diagnoser': 'Välj vilken eller vilka diagnoser som din rapport ska baseras på:',

        //help texts
        'alert.lan-andel-sjukfall-per-kon.questionmark' : 'I tabellen visas andel sjukfall i procent per kön och län. Antal sjukfall anges inom parentes.',
        'alert.lakarkon-alder.questionmark' : 'Diagrammet visar antalet sjukfall för olika grupper av läkare. Läkarna grupperas utifrån ålder och om de är kvinna eller man. Statistiken visar även antal sjukfall för män respektive kvinnor.',
        'alert.lakare-befattning.information' : 'Uppgift om vilken läkarbefattning som utfärdat ett intyg hämtas från HSA-katalogen. Uppgifterna i HSA är inte kvalitetssäkrade och information kan saknas då det inte är obligatoriskt för vårdenheten att ange befattning. En läkare kan även ha fler befattningar i HSA. Det gör att samma sjukfall kan visas flera gånger i graf och tabell. Om en läkare som sjukskrivit en patient har två eller fler läkarbefattningar visas sjukfallet en gång för varje läkarbefattning.',
        'alert.vardenhet.information' : 'Om ett sjukfall har flera intyg som utfärdats på olika vårdenheter räknas det en gång för respektive vårdenhet. Statistiken visas endast för den som har behörighet att följa upp statistik från dessa vårdenheter.',
        'alert.degreeofsickleave.information' : "När ett sjukfall har flera intyg under samma månad hämtas uppgift om sjukskrivningsgrad från det senaste intyget. Om detta intyg innehåller flera olika sjukskrivningsgrader hämtas den senaste sjukskrivningsgraden för den månaden. För ett sjukfall som varar flera månader så hämtas sjukskrivningsgrad för varje månad.",
        'alert.diagnosisgroup.information' : "När ett sjukfall har flera intyg under samma månad hämtas uppgift om diagnos från det senaste intyget. För ett sjukfall som varar flera månader så hämtas diagnos för varje månad. I tabellen visas statistiken på diagnoskapitelnivå, men i grafen är statistiken aggregerad för att underlätta presentationen.",
        'alert.diagnosissubgroup.information' : "När ett sjukfall har flera intyg under samma månad hämtas uppgift om diagnos från det senaste intyget. För ett sjukfall som varar flera månader så hämtas diagnos för varje månad.",
        'help.sickleavelength' : 'Sjukskrivningslängden räknas fram utifrån de tidsperioder som anges i sjukfallets läkarintyg. Oavsett om det är hel- eller deltidssjukskrivning räknas 1 dag alltid som 1 dag i statistiken.',
        'help.diagnosisgroup' : 'Diagnoskoder används för att gruppera sjukdomar för att kunna göra översiktliga statistiska sammanställningar och analyser. Statistiktjänsten är uppdelad i sju övergripande diagnosgrupper. I varje grupp ingår olika kapitel med diagnoskoder. Diagnoskoderna finns i klassificeringssystemet ICD-10-SE.',
        'help.diagnosissubgroup' : 'Ett diagnoskapitel innehåller flera avsnitt med sjukdomar som i sin tur omfattar olika diagnoskoder. Det finns totalt 21 diagnoskapitel. Grafen visar endast de sex vanligaste förekommande avsnitten eller diagnoserna uppdelade på kvinnor respektive män. I tabellen visas samtliga inom valt kapitel eller avsnitt.',
        'help.degreeofsickleave' : 'Sjukskrivningsgrad visar hur stor del av patientens arbetsförmåga som är nedsatt. Sjukskrivningsgraden anges i procent i förhållande till patientens aktuella arbetstid.',
        'help.casespermonth' : 'Ett sjukfall innehåller en patients alla läkarintyg om intygen följer varandra med max fem dagars uppehåll. Läkarintygen måste också vara utfärdade av samma vårdgivare. Om det är fler än fem dagar mellan intygen räknas det nya intyget som ett nytt sjukfall.',
        'help.landsting-enhet-listningar' : 'Antal sjukfall per 1000 listningar är ett mått som gör att det går att jämföra antalet sjukfall på olika vårdenheter trots att vårdenheterna inte är lika stora. Detta eftersom antalet sjukfall sätts i relation till antalet listade patienter. Information om antal listade patienter på respektive vårdenhet har rapporterats in av landstinget.',

        //info texts
        'info.lan.information' : 'Uppgift om vilket län ett sjukfall tillhör är hämtat från HSA-katalogen. Uppgifterna i HSA är inte kvalitetssäkrade och information kan saknas då det inte är obligatoriskt för vårdenheten att ange länstillhörighet.',
        'info.sickleavelength' : 'Om ett sjukfall startat på en annan vårdenhet (inom vårdgivaren) kommer den tiden läggas ihop med tiden som sjukfallet är på din vårdenhet. Däremot kommer inte en fortsättning på ett sjukfall efter att det lämnat din vårdenhet att synas i din statistik.',
        'info.emptyreponse' : 'Ingen data tillgänglig. Det kan bero på att det inte finns någon data för verksamheten eller att du har angivit en alltför restriktiv filtrering.',

        //login view
        'login.header': 'Logga in för verksamhetsstatistik',
        'login.alert-info.behorighet': 'Data för verksamheten är inte tillgänglig. Detta kan bero på att du inte har rätt behörighet för att se statistik för verksamhet. Logga in med rätt behörighet för att se data.',
        'login.alert-inaktivitet': 'Du har blivit utloggad på grund av inaktivitet.',
        'login.alert-logga.in': 'Du måste logga in igen för att komma till sidorna för verksamhetsstatistik.',
        'login.lbl.gor-foljande': 'Gör följande:',
        'login.instruktioner-for-login': 'Sätt <span style="text-decoration: underline;">SITHS-kortet i din kortläsare</span> och klicka sedan på knappen "SITHS login"',
        'login.btn.siths-login': 'SITHS login',

        //about texts
        'about.service': 'Om Statistiktjänsten',
        'about.cookies': '<h3>Om kakor (cookies)</h3><p>Så kallade kakor (cookies) används för att underlätta för besökaren på webbplatsen. En kaka är en textfil som lagras på din dator och som innehåller information. Denna webbplats använder så kallade sessionskakor. Sessionskakor lagras temporärt i din dators minne under tiden du är inne på en webbsida. Sessionskakor försvinner när du stänger din webbläsare. Ingen personlig information om dig sparas vid användning av sessionskakor.</p><p>Om du inte accepterar användandet av kakor kan du stänga av det via din webbläsares säkerhetsinställningar. Du kan även ställa in webbläsaren så att du får en varning varje gång webbplatsen försöker sätta en kaka på din dator.</p><p><strong>Observera!</strong> Om du stänger av kakor i din webbläsare kan du inte logga in i Webcert.</p><p>Allmän information om kakor (cookies) och lagen om elektronisk kommunikation finns på Post- och telestyrelsens webbplats.</p><p><a href="http://www.pts.se/sv/Bransch/Regler/Lagar/Lag-om-elektronisk-kommunikation/Cookies-kakor/" target="_blank">Mer om kakor (cookies) på Post- och telestyrelsens webbplats</a></p>',

        //error messages
        'error.unsignedcerts.couldnotbeloaded': '<strong>Kunde inte hämta ej signerade intyg.</strong>',

        //file upload
        'upload.filesize.error': 'Filen är för stor. Max tillåten storlek är {{maxFilesize}}MB',
        'upload.filetype.error': 'Felaktig filtyp. Formaten som stöds är .xls och .xlsx',
        'alert.upload.failed': 'Uppladdningen misslyckades!',
        'alert.upload.success': 'Uppladdningen lyckades!',
        'info.upload.dropfile.here': 'Släpp fil här för att ladda upp eller ',
        'info.upload.dropfile.here.add': 'lägg till',
        'info.upload.fallbacktext': 'Din webläsare stödjer inte filuppladdning via "dra och släpp". Vänligen använd knappen nedan för att ladda upp filen.'
    },
    'en': {
        'webcert.header': 'Statistics Application (en)'
    }
};
