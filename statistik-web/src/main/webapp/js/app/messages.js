/* jshint maxlen: false, unused: false */
var stMessages = {
    'sv': {
    	//generals
        'statistics.header': 'Statistiktjänsten',
        'statistics.header.extra-text': 'Statistiktjänst för ordinerad sjukskrivning',
        'statistics.hidden-header.sidans-huvudnavigering': 'Sidans huvudnavigering',
        'statistics.hidden-header.nationell-navigering': 'Navigering för nationell statistik',
        'statistics.hidden-header.business-navigering': 'Navigering för verksamhetsstatistik',
        'statistics.hidden-header.about-navigering': 'Navigering för information om tjänsten',
        
        //navigation
        'nav.national-header': 'Nationell statistik',
        'nav.business-header': 'Verksamhetsstatistik',
        'nav.about-header': 'Om tjänsten',
        'nav.oversikt': 'Översikt',
        'nav.sjukfall-totalt':'Sjukfall, totalt',
        'nav.diagnos':'Diagnos',
        'nav.diagnosgrupp':'Diagnosgrupp',
        'nav.enskilt-diagnoskapitel':'Enskilt diagnoskapitel',
        'nav.tio-vanligaste-diagnoserna': 'Tio vanligaste diagnoserna',
        'nav.jamfor-vanliga-diagnoser': 'Jämför vanliga diagnoser',
        'nav.aldersgrupp':'Åldersgrupp',
        'nav.sjukskrivningsgrad':'Sjukskrivningsgrad',
        'nav.sjukskrivningslangd':'Sjukskrivningslängd',
        'nav.lan':'Län',
        'nav.andel-per-kon': 'Andel per kön',
        'nav.lan-andel-sjukfall-per-kon':'Andel sjukfall per kön',
        'nav.mobile.trigger.diagnosgrupp-diagnoskapitel': 'Diagnosgrupp och enskilt diagnoskapitel',
        'nav.mobile.trigger.lan-andel-per-kon': 'Län och andel sjukfall per kön',
        'nav.vardenhet':'Vårdenhet',
        'nav.lakare':'Läkare',
        'nav.lakarbefattning':'Läkarbefattning',
        'nav.lakare-per-namn':'Läkare per namn',
        'nav.lakaralder-kon':'Läkare',
        'nav.pagaende':'Pågående',
        'nav.sjukskrivningslangd-mer-an-90-dagar':'Mer än 90 dagar',
        'nav.mobile.trigger.per-alder-pagaende-sjukfall': 'Per åldersgrupp eller pågående sjukfall',
        'nav.mobile.trigger.sjukskrivningslangd-pagaende-90-dagar': 'Sjukskrivningslängd, pågående, över 90 dagar',
        'nav.allmant-om-tjansten': 'Allmänt om tjänsten',
        'nav.inloggning-behorighet': 'Inloggning och behörighet',
        'nav.faq': 'Vanliga frågor och svar',
        'nav.kontakt-support': 'Kontakt till support',
        
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
        
        //filter
        'lbl.filter.valj-verksamhetstyper': 'Välj verksamhetstyper:',
        'lbl.filter.val-av-enheter': 'Välj enheter:',
        'lbl.filter.select-all': 'Markera alla',
        'lbl.filter.modal.lan': 'Län: ',
        'lbl.filter.modal.kommuner': 'Kommuner: ',
        'lbl.filter.modal.enheter': 'Enheter: ',
        'lbl.filter.modal.spara-stang': 'Spara och stäng',
        'lbl.filter.anvand-filter-pa-alla': 'Använd filtrering för alla rapporter',
        'lbl.filter.sum-progress-1': 'Genom filtreringen visas statistik från ',
        'lbl.filter.sum-progress-2': ' av totalt ',
        'lbl.filter.sum-progress-3': ' vårdenheter.',
        
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
        'business.widget.header.total-antal': 'Total antal',
        'business.widget.header.konsfordelning-sjukfall': 'Könsfördelning av sjukfall',
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
        'overview.widget.fordelning-sjukskrivningslangd.overgar-90': 'Antal sjukfall som är 91 dagar eller längre',
        'overview.widget.fordelning-sjukskrivningslangd.overgar-90-3-manader': 'Procentuell förändring av antal sjukfall som är längre än 90 dagar jämfört med föregående tre månader.',
        
        //detail views
        'lbl.valj-annat-diagnoskapitel': 'Välj annat diagnoskapitel:',
        'lbl.valj-annat-diagnosavsnitt': 'Välj diagnosavsnitt',
        'dropdown.val.rapport': 'Spara/Skriv ut',
        'dropdown.val.skrivut-farg': ' Skriv ut i färg',
        'dropdown.val.skrivut-svart-vitt': ' Skriv ut i svartvitt',
        'dropdown.val.spara-diagram-bild-1': ' Spara ',
        'dropdown.val.spara-diagram-bild-2': ' som bild',
        'dropdown.val.spara-excel': ' Spara tabell till Excel',
        'alert.diagnosgrupp.information': 'För en given månad kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg under samma månad så hämtas diagnos från varje intyg. Om intygen har olika diagnosgrupper kommer sjukfallet finnas med en gång för varje diagnosgrupp för respektive månad. Exempel: Om ett sjukfall innehåller två intyg för maj månad, där intyg ett sätter diagnosen M54 och intyg två efter vidare utredning sätter diagnosen F32, så kommer sjukfallet både räknas med i gruppen för Muskuloskeleta sjukdomar (M00-M99) och i gruppen för Psykiska sjukdomar (F00-F99) i graf och tabell för maj månad.',

        //help texts
        'help.sick-leave-length-current': 'Vad innebär pågående sjukfall?<br/>Denna rapport syftar till att visa så aktuell information om sjukfallen möjligt. Alla sjukfall som pågår någon gång under aktuell månad hämtas. Rapporten kan inte ta hänsyn till vilken dag det är i månaden. I slutet på månaden kommer fortfarande sjukfall som avslutats under månadens gång visas som pågående.',
        'help.age-group-current'        : 'Vad innebär pågående sjukfall?<br/>Denna rapport syftar till att visa så aktuell information om sjukfallen möjligt. Alla sjukfall som pågår någon gång under aktuell månad hämtas. Rapporten kan inte ta hänsyn till vilken dag det är i månaden. I slutet på månaden kommer fortfarande sjukfall som avslutats under månadens gång visas som pågående.',
        'help.lakare-alder-och-kon'     : 'Hjälptext för sjukfall per läkares ålder och kön',

        //login view
        'login.header': 'Logga in för verksamhetsstatistik',
        'login.for-att-fortsatta': 'För att fortsätta till de inloggade sidorna för verksamhetsstatistik måste du logga in med ditt SITHS-kort.',
        'login.alert-info.behorighet': 'Detta kan bero på att du inte har rätt behörighet för att se statistik för verksamheten.',
        'login.lbl.gor-foljande': 'Gör följande:',
        'login.instruktioner-for-login': 'Sätt <span style="text-decoration: underline;">SITHS-kortet i din kortläsare</span> och klicka sedan på knappen "SITHS login"',
        'login.btn.siths-login': 'SITHS login',

        //about texts
        'about.service': 'Allmänt om tjänsten',
        'about.cookies': '<h3>Om kakor (cookies)</h3><p>Så kallade kakor (cookies) används för att underlätta för besökaren på webbplatsen. En kaka är en textfil som lagras på din dator och som innehåller information. Denna webbplats använder så kallade sessionskakor. Sessionskakor lagras temporärt i din dators minne under tiden du är inne på en webbsida. Sessionskakor försvinner när du stänger din webbläsare. Ingen personlig information om dig sparas vid användning av sessionskakor.</p><p>Om du inte accepterar användandet av kakor kan du stänga av det via din webbläsares säkerhetsinställningar. Du kan även ställa in webbläsaren så att du får en varning varje gång webbplatsen försöker sätta en kaka på din dator.</p><p><strong>Observera!</strong> Om du stänger av kakor i din webbläsare kan du inte logga in i Webcert.</p><p>Allmän information om kakor (cookies) och lagen om elektronisk kommunikation finns på Post- och telestyrelsens webbplats.</p><p><a href="http://www.pts.se/sv/Bransch/Regler/Lagar/Lag-om-elektronisk-kommunikation/Cookies-kakor/" target="_blank">Mer om kakor (cookies) på Post- och telestyrelsens webbplats</a></p>',

        //error messages
        'error.data-for-vald-rapport-kan-ej-visas': 'Data för vald rapport går ej att visa',
        'error.data-for-vald-rapport-kan-ej-visas.information': 'Rapporten kan ej visas. Om felet kvarstår kan du kontakta <a ng-href="#/om/kontakt">support</a> eller gå till startsidan och försöka igen.',
        'error.sidan-kan-inte-visas': 'Sidan du söker går ej att hitta (404)',
        'error.sidan-kan-inte-visas.information': 'Kontrollera eventuella stavfel eller gå till <a ng-href="#/">startsidan</a>. Du kan även kontakta <a ng-href="#/om/kontakt">support</a>.',
        'error.unsignedcerts.couldnotbeloaded': '<strong>Kunde inte hämta ej signerade intyg.</strong>'

    },
    'en': {
        'webcert.header': 'Statistics Application (en)'
    }
};
