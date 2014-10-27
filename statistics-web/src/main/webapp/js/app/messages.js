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
        
        //navigation national
        'nav.national-header': 'Nationell statistik',
        'nav.national.oversikt': 'Översikt',
        'nav.national.sjukfall-totalt':'Sjukfall, totalt',
        'nav.national.diagnosgrupp':'Diagnosgrupp',
        'nav.national.enskilt-diagnoskapitel':'Enskilt diagnoskapitel',
        'nav.national.aldersgrupp':'Åldersgrupp',
        'nav.national.sjukskrivningsgrad':'Sjukskrivningsgrad',
        'nav.national.sjukskrivningslangd':'Sjukskrivningslängd',
        'nav.national.lan':'Län',
        'nav.national.lan-andel-sjukfall-per-kon':'Andel sjukfall per kön',
        'nav.mobile.national.trigger.diagnosgrupp-diagnoskapitel': 'Diagnosgrupp och enskilt diagnoskapitel',
        'nav.mobile.national.trigger.lan-andel-per-kon': 'Län och andel sjukfall per kön',
        
       
        //navigation business
        'nav.business-header': 'Verksamhetsstatistik',
        'nav.business.oversikt': 'Översikt',
        'nav.business.sjukfall-totalt':'Sjukfall, totalt',
        'nav.business.sjukfall-vardenhet':'Sjukfall per vårdenhet',
        'nav.business.diagnosgrupp':'Diagnosgrupp',
        'nav.business.enskilt-diagnoskapitel':'Enskilt diagnoskapitel',
        'nav.business.aldersgrupp':'Åldersgrupp',
        'nav.business.aldersgrupp-pagaende':'Pågående',
        'nav.business.sjukskrivningsgrad':'Sjukskrivningsgrad',
        'nav.business.sjukskrivningslangd':'Sjukskrivningslängd',
        'nav.business.sjukskrivningslangd-pagaende':'Pågående',
        'nav.business.sjukskrivningslangd-mer-an-90-dagar':'Mer än 90 dagar',
        'nav.mobile.business.trigger.diagnosgrupp-diagnoskapitel': 'Diagnosgrupp och enskilt diagnoskapitel',
        'nav.mobile.business.trigger.per-alder-pagaende-sjukfall': 'Per åldersgrupp eller pågående sjukfall',
        'nav.mobile.business.trigger.sjukskrivningslangd-pagaende-90-dagar': 'Sjukskrivningslängd, pågående, över 90 dagar',
        
        //navigation about
        'nav.about-header': 'Om tjänsten',
        'nav.about.allmant-om-tjansten': 'Allmänt om tjänsten',
        'nav.about.inloggning-behorighet': 'Inloggning och behörighet',
        'nav.about.faq': 'Vanliga frågor och svar',
        'nav.about.kontakt-support': 'Kontakt till support',
        
        //labels
        'lbl.mobile-menu': 'Meny',
        'lbl.log-in': 'Logga in',
        'lbl.log-out': 'Logga ut',
        'lbl.for-verksamhetsstatistik': 'För verksamhetsstatistik: ',
        'lbl.aterstall': 'Återställ',
        'lbl.sok': 'Sök',
        
        //filter
        'lbl.filter.valj-verksamhetstyper': 'Välj verksamhetstyper:',
        'lbl.filter.val-av-enheter': 'Välj enheter:',
        'lbl.filter.select-all': 'Markera alla',
        'lbl.filter.modal.lan': 'Län: ',
        'lbl.filter.modal.kommuner': 'Kommuner: ',
        'lbl.filter.modal.lan': 'Län: ',
        'lbl.filter.modal.spara-stang': 'Spara och stäng',
        'lbl.filter.sla-pa-alla-rapporter': 'Val ska slå på alla rapporter',
        'lbl.filter.sum-progress-1': 'Ditt filterval kommer presentera statistik baserat på ',
        'lbl.filter.sum-progress-2': ' av totalt ',
        
        //statistics overview
        'national.overview-header': 'Nationell statistik',
        'national.widget.header.konsfordelning': 'Könsfördelning',
        'national.widget.header.forandring': 'Förändring',
        'national.widget.header.fordelning-diagnosgrupper': 'Fördelning diagnosgrupper',
        'national.widget.header.fordelning-aldersgrupper': 'Fördelning åldersgrupper',
        'national.widget.header.fordelning-sjukskrivningsgrad': 'Fördelning sjukskrivningsgrad',
        'national.widget.header.fordelning-sjukskrivningslangd': 'Fördelning sjukskrivningslängd',
        'national.widget.header.fordelning-lan': 'Fördelning per län',
        
        'business.overview-header': 'Verksamhetsstatistik',
        'business.widget.header.total-antal': 'Total antal',
        'business.widget.header.konsfordelning-sjukfall': 'Könsfördelning av sjukfall',
        'business.widget.header.fordelning-diagnosgrupper': 'Fördelning diagnosgrupper',
        'business.widget.header.fordelning-aldersgrupper': 'Fördelning åldersgrupper',
        'business.widget.header.fordelning-sjukskrivningsgrad': 'Fördelning sjukskrivningsgrad',
        'business.widget.header.fordelning-sjukskrivningslangd': 'Fördelning sjukskrivningslängd',
        
        //detail views
        'detailview.valj-annat-diagnoskapitel': 'Välj annat diagnoskapitel:',
        'detailview.val-for-rapport': 'Val för rapport',
        'detailview.dropdown-skrivut-farg': ' Skriv ut i färg',
        'detailview.dropdown-skrivut-svart-vit': ' Skriv ut i svart-vit',
        'detailview.dropdown-spara-diagram-bild-1': ' Spara ',
        'detailview.dropdown-spara-diagram-bild-2': ' som bild',
        'detailview.dropdown-spara-excel': ' Spara tabell till Excel',
        'detailview.dolj-tabell': 'Dölj tabell',
        'detailview.visa-tabell': 'Dölj tabell',

        //about texts
        'about.service': 'Allmänt om tjänsten',
        'about.cookies': '<h3>Om kakor (cookies)</h3><p>Så kallade kakor (cookies) används för att underlätta för besökaren på webbplatsen. En kaka är en textfil som lagras på din dator och som innehåller information. Denna webbplats använder så kallade sessionskakor. Sessionskakor lagras temporärt i din dators minne under tiden du är inne på en webbsida. Sessionskakor försvinner när du stänger din webbläsare. Ingen personlig information om dig sparas vid användning av sessionskakor.</p><p>Om du inte accepterar användandet av kakor kan du stänga av det via din webbläsares säkerhetsinställningar. Du kan även ställa in webbläsaren så att du får en varning varje gång webbplatsen försöker sätta en kaka på din dator.</p><p><strong>Observera!</strong> Om du stänger av kakor i din webbläsare kan du inte logga in i Webcert.</p><p>Allmän information om kakor (cookies) och lagen om elektronisk kommunikation finns på Post- och telestyrelsens webbplats.</p><p><a href="http://www.pts.se/sv/Bransch/Regler/Lagar/Lag-om-elektronisk-kommunikation/Cookies-kakor/" target="_blank">Mer om kakor (cookies) på Post- och telestyrelsens webbplats</a></p>',

        //info messages
        'info.nounsignedcertsfound': '<strong>Inga ej signerade intyg hittades.</strong>',

        //error messages
        'error.unsignedcerts.couldnotbeloaded': '<strong>Kunde inte hämta ej signerade intyg.</strong>'
    },
    'en': {
        'webcert.header': 'Statistics Application (en)'
    }
};
