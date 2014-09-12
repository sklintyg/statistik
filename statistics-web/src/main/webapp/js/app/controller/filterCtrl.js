 'use strict';

 app.filterCtrl = function ($scope) {

    $scope.enhets = {subs: [  
    { name: "A00-B99 Vissa infektionssjukdomar och parasitsjukdomar", subs: [
      {name: "A00-A09 Infektionssjukdomar utgående från mag-tarmkanalen", subs: [
      {name: "A00 Kolera"},
      {name: "A01 Tyfoidfeber och paratyfoidfeber"},
    {name: "A02 Andra salmonellainfektioner"},
    {name: "A03 Shigellos (bakteriell dysenteri, rödsot)"},
    {name: "A04 Andra bakteriella tarminfektioner"},
    {name: "A05 Annan matförgiftning orsakad av bakterier som ej klassificeras annorstädes"},
    {name: "A06 Amöbainfektion"},
    {name: "A07 Andra protozosjukdomar i tarmen"},
    {name: "A08 Tarminfektioner orsakade av virus och andra specificerade organismer"},
      {name: "A09 Annan gastroenterit och kolit av infektiös och ospecificerad orsak"}
    ]},
    {name: "A15-A19 Tuberkulos", subs: [
    {name: "A15 Tuberkulos i andningsorganen, bakteriologiskt och histologiskt verifierad"},
    {name: "A16 Tuberkulos i andningsorganen, ej verifierad bakteriologiskt eller histologiskt"},
    {name: "A17 Tuberkulos i nervsystemet"},
    {name: "A18 Tuberkulos i andra organ"},
    {name: "A19 Miliartuberkulos (utspridd tuberkulos)"}
    ]},
    {name: "A20-A28 Vissa djurburna bakteriesjukdomar", subs: [
    {name: "A20 Pest"},
    {name: "A21 Tularemi (harpest)"},
    {name: "A22 Mjältbrand"},
    {name: "A23 Undulantfeber"},
    {name: "A24 Rots och melioidos"},
    {name: "A25 Råttbettsfeber"},
    {name: "A26 Erysipeloid"},
    {name: "A27 Leptospiros"},
    {name: "A28 Andra djurburna bakteriesjukdomar som ej klassificeras annorstädes"}
    ]},
    {name: "A30-A49 Andra bakteriesjukdomar", subs: [
    {name: "A30 Lepra"},
    {name: "A31 Sjukdomar orsakade av andra mykobakterier"},
    {name: "A32 Listerios"},
    {name: "A33 Stelkramp hos nyfödd"},
    {name: "A34 Obstetrisk stelkramp"},
    {name: "A35 Annan stelkramp"},
    {name: "A36 Difteri"},
    {name: "A37 Kikhosta"},
    {name: "A38 Scharlakansfeber"},
    {name: "A39 Meningokockinfektion"},
    {name: "A40 Sepsis orsakad av streptokocker"},
    {name: "A41 Annan sepsis"},
    {name: "A42 Aktinomykos (strålsvamp)"},
    {name: "A43 Nokardios"},
    {name: "A44 Bartonellos"},
    {name: "A46 Rosfeber"},
    {name: "A48 Andra bakteriesjukdomar som ej klassificeras annorstädes"},
    {name: "A49 Bakterieinfektion med icke specificerad lokalisation"}
    ]},
    {name: "A50-A64 Huvudsakligen sexuellt överförda infektioner", subs: [
    {name: "A50 Medfödd syfilis"},
    {name: "A51 Tidig syfilis"},
    {name: "A52 Sen syfilis"},
    {name: "A53 Annan och icke specificerad syfilis"},
    {name: "A54 Gonokockinfektion"},
    {name: "A55 Lymfogranulom (veneriskt) orsakat av klamydia"},
    {name: "A56 Andra sexuellt överförda klamydiasjukdomar"},
    {name: "A57 Chankroid (mjuk schanker)"},
    {name: "A58 Granuloma inguinale"},
    {name: "A59 Trikomonasinfektion"},
    {name: "A60 Anogenital infektion med herpes simplex-virus"},
    {name: "A63 Andra huvudsakligen sexuellt överförda sjukdomar som ej klassificeras annorstädes"},
    {name: "A64 Icke specificerad sexuellt överförd sjukdom"}
    ]},
    {name: "A65-A69 Andra spiroketsjukdomar", subs: [
    {name: "A65 Icke venerisk syfilis"},
    {name: "A66 Yaws"},
    {name: "A67 Pinta"},
    {name: "A68 Återfallsfeber"},
    {name: "A69 Andra spiroketinfektioner"}
    ]},
    {name: "A70-A74 Andra sjukdomar orsakade av klamydia", subs: [
    {name: "A70 Infektion orsakad av Chlamydia psittaci (papegojsjuka)"},
    {name: "A71 Trakom"},
    {name: "A74 Andra sjukdomar orsakade av klamydier"}
    ]},
    {name: "A75-A79 Sjukdomar orsakade av rickettsiaarter", subs: [
    {name: "A75 Fläcktyfus överförd av löss, loppor och kvalster"},
    {name: "A77 Rickettsiasjukdom överförd av fästingar"},
    {name: "A78 Q-feber"},
    {name: "A79 Andra rickettsiasjukdomar"}
    ]},
    {name: "A80-A89 Virussjukdomar i centrala nervsystemet", subs: [
    {name: "A80 Akut polio (barnförlamning)"},
    {name: "A81 Atypisk virusinfektion i centrala nervsystemet"},
    {name: "A82 Rabies (vattuskräck)"},
    {name: "A83 Virusencefalit överförd av myggor"},
    {name: "A84 Virusencefalit överförd av fästingar"},
    {name: "A85 Andra virusencefaliter som ej klassificeras annorstädes"},
    {name: "A86 Icke specificerad virusencefalit"},
    {name: "A87 Virusmeningit"},
    {name: "A88 Andra virusinfektioner i centrala nervsystemet som ej klassificeras annorstädes"},
    {name: "A89 Icke specificerad virusinfektion i centrala nervsystemet"}
    ]},
    {name: "A90-A99 Febersjukdomar orsakade av virus överförda av leddjur och virusorsakade hemorragiska febrar", subs: [
    {name: "A90 Denguefeber (klassisk dengue)"},
    {name: "A91 Hemorragisk denguefeber"},
    {name: "A92 Andra febersjukdomar orsakade av virus överförda av myggor"},
    {name: "A93 Andra febersjukdomar orsakade av virus överförda av leddjur, som ej klassificeras annorstädes"},
    {name: "A94 Icke specificerad febersjukdom orsakad av virus överfört av leddjur"},
    {name: "A95 Gula febern"},
    {name: "A96 Arenaviral hemorragisk feber"},
    {name: "A98 Andra hemorragiska febersjukdomar orsakade av virus som ej klassificeras annorstädes"},
    {name: "A99 Icke specificerad hemorragisk febersjukdom orsakad av virus"}
    ]},
    {name: "B00-B09 Virussjukdomar med hudutslag och slemhinneutslag", subs: [
    {name: "B00 Herpes simplex-infektioner"},
    {name: "B01 Vattkoppor"},
    {name: "B02 Bältros"},
    {name: "B03 Smittkoppor"},
    {name: "B04 Apkoppor"},
    {name: "B05 Mässling"},
    {name: "B06 Röda hund"},
    {name: "B07 Virusvårtor"},
    {name: "B08 Andra virussjukdomar med hud- och slemhinneutslag som ej klassificeras annorstädes"},
    {name: "B09 Icke specificerad virusinfektion med hud- och slemhinneutslag"}
    ]},
    {name: "B15-B19 Virushepatit", subs: [
    {name: "B15 Akut hepatit A"},
    {name: "B16 Akut hepatit B"},
    {name: "B17 Annan akut virushepatit"},
    {name: "B18 Kronisk virushepatit"},
    {name: "B19 Icke specificerad virushepatit"}
    ]},
    {name: "B20-B24 Sjukdom orsakad av humant immunbristvirus [HIV]", subs: [
    {name: "B20 Sjukdom orsakad av humant immunbristvirus [HIV] tillsammans med infektions- och parasitsjukdom"},
    {name: "B21 Sjukdom orsakad av humant immunbristvirus [HIV] tillsammans med maligna tumörer"},
    {name: "B22 Sjukdom orsakad av humant immunbristvirus [HIV] tillsammans med andra specificerade sjukdomar"},
    {name: "B23 Sjukdom orsakad av humant immunbristvirus [HIV] tillsammans med andra tillstånd"},
    {name: "B24 Icke specificerad sjukdom orsakad av humant immunbristvirus [HIV]"}
    ]},
    {name: "B25-B34 Andra virussjukdomar", subs: [
    {name: "B25 Cytomegalvirussjukdom"},
    {name: "B26 Påssjuka"},
    {name: "B27 Körtelfeber"},
    {name: "B30 Viruskonjunktivit"},
    {name: "B33 Andra virussjukdomar som ej klassificeras annorstädes"},
    {name: "B34 Virussjukdom med icke specificerad lokalisation"}
    ]},
    {name: "B35-B49 Svampsjukdomar", subs: [
    {name: "B35 Dermatofytos (hudsvampsjukdom)"},
    {name: "B36 Andra ytliga mykoser"},
    {name: "B37 Candidainfektion"},
    {name: "B38 Koccidioidomykos"},
    {name: "B39 Histoplasmos"},
    {name: "B40 Blastomykos"},
    {name: "B41 Parakoccidioidomykos"},
    {name: "B42 Sporotrikos"},
    {name: "B43 Kromomykos och feomykotisk abscess"},
    {name: "B44 Aspergillos"},
    {name: "B45 Kryptokockos"},
    {name: "B46 Zygomykos"},
    {name: "B47 Mycetom"},
    {name: "B48 Andra mykoser som ej klassificeras annorstädes"},
    {name: "B49 Icke specificerad mykos"}
    ]},
    {name: "B50-B64 Protozosjukdomar", subs: [
    {name: "B50 Malaria orsakad av Plasmodium falciparum"},
    {name: "B51 Malaria orsakad av Plasmodium vivax"},
    {name: "B52 Malaria orsakad av Plasmodium malariae"},
    {name: "B53 Annan parasitologiskt verifierad malaria"},
    {name: "B54 Malaria, ospecificerad"},
    {name: "B55 Leishmanios"},
    {name: "B56 Afrikansk trypanosomiasis (sömnsjuka)"},
    {name: "B57 Chagas sjukdom"},
    {name: "B58 Toxoplasmos"},
    {name: "B59 Pneumocystos"},
    {name: "B60 Andra protozosjukdomar som ej klassificeras annorstädes"},
    {name: "B64 Icke specificerad protozosjukdom"}
    ]},
    {name: "B65-B83 Masksjukdomar", subs: [
    {name: "B65 Schistosomiasis"},
    {name: "B66 Andra trematodinfektioner (infektioner med flundror och andra sugmaskar)"},
    {name: "B67 Blåsmasksjuka"},
    {name: "B68 Taeniainfektion"},
    {name: "B69 Cysticerkos"},
    {name: "B70 Infektion med binnikemaskar"},
    {name: "B71 Andra bandmaskinfektioner"},
    {name: "B72 Dracontiasis"},
    {name: "B73 Onchocerciasis"},
    {name: "B74 Filariainfektion"},
    {name: "B75 Trikinos (sjukdom orsakad av trikiner)"},
    {name: "B76 Hakmasksjukdom"},
    {name: "B77 Spolmaskinfektion"},
    {name: "B78 Strongyloidesinfektion"},
    {name: "B79 Piskmaskinfektion"},
    {name: "B80 Springmaskinfektion"},
    {name: "B81 Andra tarmmaskinfektioner som ej klassificeras annorstädes"},
    {name: "B82 Infektion med icke specificerade tarmparasiter"},
    {name: "B83 Andra masksjukdomar"}
    ]},
    {name: "B85-B89 Lusangrepp, acarinos (angrepp av kvalster) och andra infestationer", subs: [
    {name: "B85 Lusangrepp"},
    {name: "B86 Skabb"},
    {name: "B87 Infestation av fluglarver"},
    {name: "B88 Andra infestationer"},
    {name: "B89 Icke specificerade parasitsjukdomar"}
    ]},
    {name: "B90-B94 Sena effekter av infektionssjukdomar och parasitsjukdomar", subs: [
    {name: "B90 Sena effekter av tuberkulos"},
    {name: "B91 Sena effekter av polio"},
    {name: "B92 Sena effekter av lepra"},
    {name: "B94 Sena effekter av andra och icke specificerade infektionssjukdomar och parasitsjukdomar"}
    ]},
    {name: "B95-B98 Bakterier, virus och andra infektiösa organismer", subs: [
    {name: "B95 Streptokocker och stafylokocker som orsak till sjukdomar som klassificeras i andra kapitel"},
    {name: "B96 Vissa andra specificerade bakterier som orsak till sjukdomar som klassificeras i andra kapitel"},
    {name: "B97 Virus som orsak till sjukdomar som klassificeras i andra kapitel"},
    {name: "B98 Andra specificerade infektiösa organismer som orsak till sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "B99-B99 Andra infektionssjukdomar", subs: [
    {name: "B99 Andra och icke specificerade infektionssjukdomar"}
    ]}
  ]},
  {name: "C00-D48 Tumörer", subs: [
    {name: "C00-C14 Maligna tumörer i läpp, munhåla och svalg", subs: [
    {name: "C00 Malign tumör i läpp"},
    {name: "C01 Malign tumör i tungbasen"},
    {name: "C02 Malign tumör i annan och icke specificerad del av tungan"},
    {name: "C03 Malign tumör i tandköttet"},
    {name: "C04 Malign tumör i munbotten"},
    {name: "C05 Malign tumör i gom"},
    {name: "C06 Malign tumör i annan och icke specificerad del av munhålan"},
    {name: "C07 Malign tumör i parotiskörtel"},
    {name: "C08 Malign tumör i andra och icke specificerade stora spottkörtlar"},
    {name: "C09 Malign tumör i tonsill"},
    {name: "C10 Malign tumör i orofarynx (mellansvalget)"},
    {name: "C11 Malign tumör i rinofarynx (övre svalgrummet)"},
    {name: "C12 Malign tumör i fossa piriformis"},
    {name: "C13 Malign tumör i hypofarynx (svalget i höjd med struphuvudet)"},
    {name: "C14 Malign tumör med annan och ofullständigt angiven lokalisation i läpp, munhåla och svalg"}
    ]},
    {name: "C15-C26 Maligna tumörer i matsmältningsorganen", subs: [
    {name: "C15 Malign tumör i matstrupen"},
    {name: "C16 Malign tumör i magsäcken"},
    {name: "C17 Malign tumör i tunntarmen"},
    {name: "C18 Malign tumör i tjocktarmen"},
    {name: "C19 Malign tumör i rektosigmoidala gränszonen"},
    {name: "C20 Malign tumör i ändtarmen"},
    {name: "C21 Malign tumör i anus och analkanalen"},
    {name: "C22 Malign tumör i levern och intrahepatiska gallgångarna"},
    {name: "C23 Malign tumör i gallblåsan"},
    {name: "C24 Malign tumör i andra och icke specificerade delar av gallvägarna"},
    {name: "C25 Malign tumör i pankreas"},
    {name: "C26 Malign tumör med annan och ofullständigt angiven lokalisation i matsmältningsorganen"}
    ]},
    {name: "C30-C39 Maligna tumörer i andningsorganen och brösthålans organ", subs: [
    {name: "C30 Malign tumör i näshåla och mellanöra"},
    {name: "C31 Malign tumör i näsans bihålor"},
    {name: "C32 Malign tumör i struphuvudet"},
    {name: "C33 Malign tumör i luftstrupen"},
    {name: "C34 Malign tumör i bronk och lunga"},
    {name: "C37 Malign tumör i tymus"},
    {name: "C38 Malign tumör i hjärtat, mediastinum (lungmellanrummet) och lungsäcken"},
    {name: "C39 Maligna tumörer med annan och ofullständigt angiven lokalisation i andningsorganen och brösthålans organ"}
    ]},
    {name: "C40-C41 Maligna tumörer i ben och ledbrosk", subs: [
    {name: "C40 Malign tumör i ben och extremitetsledbrosk"},
    {name: "C41 Malign tumör i ben och ledbrosk med annan och icke specificerad lokalisation"}
    ]},
    {name: "C43-C44 Melanom och andra maligna tumörer i huden", subs: [
    {name: "C43 Malignt melanom i huden"},
    {name: "C44 Andra maligna tumörer i huden"}
    ]},
    {name: "C45-C49 Maligna tumörer i mesotelial (kroppshåletäckande) vävnad och mjukvävnad", subs: [
    {name: "C45 Mesoteliom"},
    {name: "C46 Kaposis sarkom"},
    {name: "C47 Malign tumör i perifera nerver och autonoma nervsystemet"},
    {name: "C48 Malign tumör i bukhinnan och retroperitonealrummet (utrymmet bakom bukhinnan)"},
    {name: "C49 Malign tumör i annan bindväv och mjukvävnad"}
    ]},
    {name: "C50-C50 Malign tumör i bröstkörtel", subs: [
    {name: "C50 Malign tumör i bröstkörtel"}
    ]},
    {name: "C51-C58 Maligna tumörer i de kvinnliga könsorganen", subs: [
    {name: "C51 Malign tumör i vulva"},
    {name: "C52 Malign tumör i vagina"},
    {name: "C53 Malign tumör i livmoderhalsen"},
    {name: "C54 Malign tumör i livmoderkroppen"},
    {name: "C55 Malign tumör i livmodern med icke specificerad lokalisation"},
    {name: "C56 Malign tumör i äggstock"},
    {name: "C57 Malign tumör i andra och icke specificerade kvinnliga könsorgan"},
    {name: "C58 Malign tumör i moderkakan"}
    ]},
    {name: "C60-C63 Maligna tumörer i de manliga könsorganen", subs: [
    {name: "C60 Malign tumör i penis"},
    {name: "C61 Malign tumör i prostata"},
    {name: "C62 Malign tumör i testikel"},
    {name: "C63 Malign tumör i andra och icke specificerade manliga könsorgan"}
    ]},
    {name: "C64-C68 Maligna tumörer i urinorganen", subs: [
    {name: "C64 Malign tumör i njure med undantag för njurbäcken"},
    {name: "C65 Malign tumör i njurbäcken"},
    {name: "C66 Malign tumör i uretär (urinledare)"},
    {name: "C67 Malign tumör i urinblåsan"},
    {name: "C68 Malign tumör i andra och icke specificerade urinorgan"}
    ]},
    {name: "C69-C72 Maligna tumörer i öga, hjärnan och andra delar av centrala nervsystemet", subs: [
    {name: "C69 Malign tumör i öga och närliggande vävnader"},
    {name: "C70 Malign tumör i centrala nervsystemets hinnor"},
    {name: "C71 Malign tumör i hjärnan"},
    {name: "C72 Malign tumör i ryggmärgen, kranialnerver och andra delar av centrala nervsystemet"}
    ]},
    {name: "C73-C75 Maligna tumörer i tyreoidea och andra endokrina körtlar", subs: [
    {name: "C73 Malign tumör i tyreoidea"},
    {name: "C74 Malign tumör i binjure"},
    {name: "C75 Malign tumör i andra endokrina körtlar och därmed besläktade vävnader"}
    ]},
    {name: "C76-C80 Maligna tumörer med ofullständigt angivna, sekundära och icke specificerade lokalisationer", subs: [
    {name: "C76 Malign tumör med annan och ofullständigt angiven lokalisation"},
    {name: "C77 Sekundär malign tumör (metastas) och icke specificerad malign tumör i lymfkörtlar"},
    {name: "C78 Sekundär malign tumör (metastas) i andningsorganen och matsmältningsorganen"},
    {name: "C79 Sekundär malign tumör (metastas) med andra och icke specificerade lokalisationer"},
    {name: "C80 Malign tumör utan specificerad lokalisation"}
    ]},
    {name: "C81-C96 Maligna tumörer i lymfatisk, blodbildande och besläktad vävnad", subs: [
    {name: "C81 Hodgkins lymfom"},
    {name: "C82 Follikulärt lymfom"},
    {name: "C83 Icke-follikulärt lymfom"},
    {name: "C84 Mogna T/NK-cellslymfom"},
    {name: "C85 Andra och icke specificerade typer av non-Hodgkin-lymfom"},
    {name: "C86 Andra specificerade typer av T/NK-cellslymfom"},
    {name: "C88 Maligna immunoproliferativa sjukdomar"},
    {name: "C90 Myelom och maligna plasmacellstumörer"},
    {name: "C91 Lymfatisk leukemi"},
    {name: "C92 Myeloisk leukemi"},
    {name: "C93 Monocytleukemi"},
    {name: "C94 Andra leukemier med specificerad celltyp"},
    {name: "C95 Leukemi med icke specificerad celltyp"},
    {name: "C96 Övriga och icke specificerade maligna tumörer i lymfoid, blodbildande och besläktad vävnad"}
    ]},
    {name: "C97-C97 Flera (primära) maligna tumörer med olika utgångspunkter", subs: [
    {name: "C97 Flera (primära) maligna tumörer med olika utgångspunkter"}
    ]},
    {name: "D00-D09 Cancer in situ (lokalt begränsad cancer utgången från epitel)", subs: [
    {name: "D00 Cancer in situ i munhåla, esofagus och magsäck"},
    {name: "D01 Cancer in situ i andra och icke specificerade delar av matsmältningsorganen"},
    {name: "D02 Cancer in situ i mellanöra och andningsorgan"},
    {name: "D03 Melanom in situ"},
    {name: "D04 Cancer in situ i huden"},
    {name: "D05 Cancer in situ i bröstkörtel"},
    {name: "D06 Cancer in situ i livmoderhalsen"},
    {name: "D07 Cancer in situ i andra och icke specificerade könsorgan"},
    {name: "D09 Cancer in situ med annan och icke specificerad lokalisation"}
    ]},
    {name: "D10-D36 Benigna tumörer", subs: [
    {name: "D10 Benign tumör i munhåla och svalg"},
    {name: "D11 Benign tumör i de stora spottkörtlarna"},
    {name: "D12 Benign tumör i tjocktarm, ändtarm, anus och analkanal"},
    {name: "D13 Benign tumör i andra och ofullständigt angivna delar av matsmältningsorganen"},
    {name: "D14 Benign tumör i mellanöra och andningsorgan"},
    {name: "D15 Benign tumör i andra och icke specificerade organ i brösthålan"},
    {name: "D16 Benign tumör i ben och ledbrosk"},
    {name: "D17 Lipom (fettsvulst)"},
    {name: "D18 Hemangiom (blodkärlssvulst) och lymfangiom (lymfkärlssvulst), alla lokalisationer"},
    {name: "D19 Benign tumör i mesotelial (kroppshåletäckande) vävnad"},
    {name: "D20 Benign tumör i mjukvävnad i retroperitonealrummet (utrymmet bakom bukhinnan) och i peritoneum (bukhinnan)"},
    {name: "D21 Andra benigna tumörer i bindväv och annan mjukvävnad"},
    {name: "D22 Melanocytnevus"},
    {name: "D23 Andra benigna tumörer i huden"},
    {name: "D24 Benign tumör i bröstkörtel"},
    {name: "D25 Uterusmyom (muskelsvulst i livmodern)"},
    {name: "D26 Andra benigna tumörer i livmodern"},
    {name: "D27 Benign tumör i ovarium"},
    {name: "D28 Benign tumör i andra och icke specificerade kvinnliga könsorgan"},
    {name: "D29 Benign tumör i de manliga könsorganen"},
    {name: "D30 Benign tumör i urinorganen"},
    {name: "D31 Benign tumör i öga och närliggande vävnader"},
    {name: "D32 Benign tumör i centrala nervsystemets hinnor"},
    {name: "D33 Benign tumör i hjärnan och andra delar av centrala nervsystemet"},
    {name: "D34 Benign tumör i tyreoidea (sköldkörteln)"},
    {name: "D35 Benign tumör i andra och icke specificerade endokrina körtlar"},
    {name: "D36 Benign tumör med annan och icke specificerad lokalisation"}
    ]},
    {name: "D37-D48 Tumörer av osäker eller okänd natur", subs: [
    {name: "D37 Tumör av osäker eller okänd natur i munhålan och matsmältningsorganen"},
    {name: "D38 Tumör av osäker eller okänd natur i mellanöra, andningsorganen och bröstkorgens organ"},
    {name: "D39 Tumör av osäker eller okänd natur i de kvinnliga könsorganen"},
    {name: "D40 Tumör av osäker eller okänd natur i de manliga könsorganen"},
    {name: "D41 Tumör av osäker eller okänd natur i urinorganen"},
    {name: "D42 Tumör av osäker eller okänd natur i centrala nervsystemets hinnor"},
    {name: "D43 Tumör av osäker eller okänd natur i hjärnan och andra delar av centrala nervsystemet"},
    {name: "D44 Tumör av osäker eller okänd natur i de endokrina körtlarna"},
    {name: "D45 Polycythaemia vera (sjuklig ökning av antalet röda blodkroppar)"},
    {name: "D46 Myelodysplastiska syndrom"},
    {name: "D47 Andra tumörer av osäker eller okänd natur i lymfatisk, blodbildande och besläktad vävnad"},
    {name: "D48 Tumör av osäker eller okänd natur med annan och icke specificerad lokalisation"}
    ]}
  ]},
  {name: "D50-D89 Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet", subs: [
    {name: "D50-D53 Nutritionsanemier", subs: [
    {name: "D50 Järnbristanemi"},
    {name: "D51 Anemi på grund av vitamin B12-brist"},
    {name: "D52 Folatbristanemi"},
    {name: "D53 Andra nutritionsanemier"}
    ]},
    {name: "D55-D59 Hemolytiska anemier (blodbrist på grund av ökad nedbrytning av röda blodkroppar)", subs: [
    {name: "D55 Anemi orsakad av enzymrubbningar"},
    {name: "D56 Talassemi (medelhavsanemi)"},
    {name: "D57 Sicklecellssjukdomar"},
    {name: "D58 Andra ärftliga hemolytiska anemier (ärftlig blodbrist på grund av ökad nedbrytning av röda blodkroppar)"},
    {name: "D59 Förvärvad hemolytisk anemi (förvärvad blodbrist på grund av ökad nedbrytning av röda blodkroppar)"}
    ]},
    {name: "D60-D64 Aplastisk anemi (blodbrist på grund av upphörd eller minskad blodbildning i benmärgen) och andra anemier", subs: [
    {name: "D60 Förvärvad isolerad aplasi av röda blodkroppar [Aquired pure red cell aplasia]"},
    {name: "D61 Andra aplastiska anemier (annan blodbrist på grund av upphörd eller minskad blodbildning i benmärgen)"},
    {name: "D62 Anemi efter akut större blödning"},
    {name: "D63 Anemi vid kroniska sjukdomar som klassificeras annorstädes"},
    {name: "D64 Andra anemier"}
    ]},
    {name: "D65-D69 Koagulationsrubbningar, purpura (punktformiga blödningar i huden mm) och andra blödningstillstånd", subs: [
    {name: "D65 Disseminerad intravasal koagulation [defibrineringssyndrom]"},
    {name: "D66 Ärftlig brist på faktor VIII"},
    {name: "D67 Ärftlig brist på faktor IX"},
    {name: "D68 Andra koagulationsrubbningar"},
    {name: "D69 Purpura (punktformiga blödningar i huden mm) och andra blödningstillstånd"}
    ]},
    {name: "D70-D77 Andra sjukdomar i blod och blodbildande organ", subs: [
    {name: "D70 Agranulocytos"},
    {name: "D71 Funktionella rubbningar hos polymorfkärniga neutrofila celler (vissa vita blodkroppar)"},
    {name: "D72 Andra sjukdomar i vita blodkroppar"},
    {name: "D73 Sjukdomar i mjälten"},
    {name: "D74 Methemoglobinemi"},
    {name: "D75 Andra sjukdomar i blod och blodbildande organ"},
    {name: "D76 Andra specificerade sjukdomar som engagerar lymforetikulär och retikulohistiocytär vävnad"},
    {name: "D77 Andra förändringar i blod och blodbildande organ vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "D80-D89 Vissa rubbningar i immunsystemet", subs: [
    {name: "D80 Immunbrist med huvudsakligen antikroppsdefekter"},
    {name: "D81 Kombinerade immunbristtillstånd"},
    {name: "D82 Immunbrist i kombination med andra omfattande defekter"},
    {name: "D83 Vanlig variabel immunbrist"},
    {name: "D84 Andra immunbristtillstånd"},
    {name: "D86 Sarkoidos"},
    {name: "D89 Andra rubbningar i immunsystemet som ej klassificeras annorstädes"}
    ]}
  ]},
  {name: "E00-E90 Endokrina sjukdomar, nutritionsrubbningar och ämnesomsättningssjukdomar", subs: [
    {name: "E00-E07 Sjukdomar i sköldkörteln", subs: [
    {name: "E00 Medfött jodbristsyndrom"},
    {name: "E01 Jodbristrelaterade sköldkörtelsjukdomar och därmed sammanhängande tillstånd"},
    {name: "E02 Subklinisk jodbristhypotyreos (underfunktion av sköldkörteln)"},
    {name: "E03 Annan hypotyreos (underfunktion av sköldkörteln)"},
    {name: "E04 Annan atoxisk struma (struma utan överfunktion)"},
    {name: "E05 Tyreotoxikos [hypertyreos] (överfunktion av sköldkörteln)"},
    {name: "E06 Sköldkörtelinflammation"},
    {name: "E07 Andra sjukdomar i sköldkörteln"}
    ]},
    {name: "E10-E14 Diabetes (sockersjuka)", subs: [
    {name: "E10 Diabetes mellitus typ 1"},
    {name: "E11 Diabetes mellitus typ 2"},
    {name: "E12 Näringsbristrelaterad diabetes"},
    {name: "E13 Annan specificerad diabetes"},
    {name: "E14 Icke specificerad diabetes"}
    ]},
    {name: "E15-E16 Andra rubbningar i glukosreglering och bukspottkörtelns inre sekretion", subs: [
    {name: "E15 Icke diabetiskt hypoglykemiskt koma (djup medvetslöshet på grund av glukosbrist)"},
    {name: "E16 Andra rubbningar i bukspottkörtelns inre sekretion"}
    ]},
    {name: "E20-E35 Sjukdomar i andra endokrina körtlar", subs: [
    {name: "E20 Hypoparatyreoidism (underfunktion av bisköldkörtel)"},
    {name: "E21 Hyperparatyreoidism (överfunktion av bisköldkörtel) och andra sjukdomar i bisköldkörtlarna"},
    {name: "E22 Hyperfunktion av hypofysen"},
    {name: "E23 Hypofunktion och andra sjukdomar i hypofysen"},
    {name: "E24 Cushings syndrom (överproduktion av binjurebarkhormoner)"},
    {name: "E25 Adrenogenitala rubbningar (rubbningar i binjurens produktion av könshormon)"},
    {name: "E26 Hyperaldosteronism (överproduktion av aldosteron)"},
    {name: "E27 Andra sjukdomar i binjurarna"},
    {name: "E28 Rubbningar i äggstockarnas funktion"},
    {name: "E29 Rubbningar i testiklarnas funktion"},
    {name: "E30 Pubertetsstörningar som ej klassificeras annorstädes"},
    {name: "E31 Samtidig rubbning i flera inresekretoriska organ"},
    {name: "E32 Sjukdomar i tymus"},
    {name: "E34 Andra endokrina rubbningar"},
    {name: "E35 Endokrina rubbningar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "E40-E46 Näringsbrist", subs: [
    {name: "E40 Svår proteinundernäring [Kwashiorkor]"},
    {name: "E41 Svår energiundernäring"},
    {name: "E42 Svår protein-energiundernäring"},
    {name: "E43 Icke specificerad svår undernäring"},
    {name: "E44 Protein-energiundernäring av måttlig och lätt grad"},
    {name: "E45 Försenad utveckling efter protein-energiundernäring"},
    {name: "E46 Icke specificerad protein-energiundernäring"}
    ]},
    {name: "E50-E64 Andra näringsbristtillstånd", subs: [
    {name: "E50 A-vitaminbrist"},
    {name: "E51 Tiaminbrist"},
    {name: "E52 Niacinbrist [pellagra]"},
    {name: "E53 Brist på andra vitaminer i B-gruppen"},
    {name: "E54 Askorbinsyrabrist"},
    {name: "E55 D-vitaminbrist"},
    {name: "E56 Andra vitaminbristtillstånd"},
    {name: "E58 Dietbetingad kalciumbrist"},
    {name: "E59 Dietbetingad selenbrist"},
    {name: "E60 Dietbetingad zinkbrist"},
    {name: "E61 Brist på andra grundämnen i födan"},
    {name: "E63 Andra näringsbristtillstånd"},
    {name: "E64 Sena effekter av undernäring och andra näringsbristtillstånd"}
    ]},
    {name: "E65-E68 Fetma och andra övernäringstillstånd", subs: [
    {name: "E65 Lokaliserad fetma"},
    {name: "E66 Fetma"},
    {name: "E67 Annan övernäring"},
    {name: "E68 Sena effekter av övernäring"}
    ]},
    {name: "E70-E90 Ämnesomsättningssjukdomar", subs: [
    {name: "E70 Rubbningar i omsättningen av aromatiska aminosyror"},
    {name: "E71 Rubbningar i omsättningen av grenade aminosyror och av fettsyror"},
    {name: "E72 Andra rubbningar i omsättningen av aminosyror"},
    {name: "E73 Laktosintolerans"},
    {name: "E74 Andra rubbningar i kolhydratomsättningen"},
    {name: "E75 Rubbningar i sfingolipidomsättningen och andra rubbningar i fettupplagringen"},
    {name: "E76 Rubbningar i omsättningen av glukosaminoglykaner"},
    {name: "E77 Rubbningar i glykoproteinomsättningen"},
    {name: "E78 Rubbning i omsättningen av lipoprotein och andra lipidemier"},
    {name: "E79 Rubbningar i purin- och pyrimidinomsättningen"},
    {name: "E80 Rubbningar i omsättningen av porfyrin och bilirubin"},
    {name: "E83 Rubbningar i mineralomsättningen"},
    {name: "E84 Cystisk fibros"},
    {name: "E85 Amyloidos"},
    {name: "E86 Minskad vätskevolym"},
    {name: "E87 Andra rubbningar i vätske-, elektrolyt- och syrabasbalans"},
    {name: "E88 Andra ämnesomsättningssjukdomar"},
    {name: "E89 Endokrina rubbningar och ämnesomsättningssjukdomar efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "E90 Rubbningar i nutrition och ämnesomsättning vid sjukdomar som klassificeras annorstädes"}
    ]}
  ]},
  {name: "F00-F99 Psykiska sjukdomar och syndrom samt beteendestörningar", subs: [
    {name: "F00-F09 Organiska, inklusive symtomatiska, psykiska störningar", subs: [
    {name: "F00 Demens vid Alzheimers sjukdom"},
    {name: "F01 Vaskulär demens"},
    {name: "F02 Demens vid andra sjukdomar som klassificeras annorstädes"},
    {name: "F03 Ospecificerad demens"},
    {name: "F04 Organiska amnesisyndrom ej framkallade av alkohol eller andra psykoaktiva substanser"},
    {name: "F05 Delirium ej framkallat av alkohol eller andra psykoaktiva substanser"},
    {name: "F06 Andra psykiska störningar orsakade av hjärnskada, cerebral dysfunktion eller kroppslig sjukdom"},
    {name: "F07 Personlighets- och beteendestörningar orsakade av hjärnsjukdom, hjärnskada eller cerebral dysfunktion"},
    {name: "F09 Ospecificerad organisk eller symtomatisk psykisk störning"}
    ]},
    {name: "F10-F19 Psykiska störningar och beteendestörningar orsakade av psykoaktiva substanser", subs: [
    {name: "F10 Psykiska störningar och beteendestörningar orsakade av alkohol"},
    {name: "F11 Psykiska störningar och beteendestörningar orsakade av opiater"},
    {name: "F12 Psykiska störningar och beteendestörningar orsakade av cannabis"},
    {name: "F13 Psykiska störningar och beteendestörningar orsakade av sedativa och hypnotika"},
    {name: "F14 Psykiska störningar och beteendestörningar orsakade av kokain"},
    {name: "F15 Psykiska störningar och beteendestörningar orsakade av andra stimulantia, däribland koffein"},
    {name: "F16 Psykiska störningar och beteendestörningar orsakade av hallucinogener"},
    {name: "F17 Psykiska störningar och beteendestörningar orsakade av tobak"},
    {name: "F18 Psykiska störningar och beteendestörningar orsakade av flyktiga lösningsmedel"},
    {name: "F19 Psykiska störningar och beteendestörningar orsakade av flera droger i kombination och av andra psykoaktiva substanser"}
    ]},
    {name: "F20-F29 Schizofreni, schizotypa störningar och vanföreställningssyndrom", subs: [
    {name: "F20 Schizofreni"},
    {name: "F21 Schizotyp störning"},
    {name: "F22 Kroniska vanföreställningssyndrom"},
    {name: "F23 Akuta och övergående psykotiska syndrom"},
    {name: "F24 Inducerat vanföreställningssyndrom"},
    {name: "F25 Schizoaffektiva syndrom"},
    {name: "F28 Andra icke organiska psykotiska störningar"},
    {name: "F29 Ospecificerad icke organisk psykos"}
    ]},
    {name: "F30-F39 Förstämningssyndrom", subs: [
    {name: "F30 Manisk episod"},
    {name: "F31 Bipolär sjukdom"},
    {name: "F32 Depressiv episod"},
    {name: "F33 Recidiverande depressioner"},
    {name: "F34 Kroniska förstämningssyndrom"},
    {name: "F38 Andra förstämningssyndrom"},
    {name: "F39 Ospecificerat förstämningssyndrom"}
    ]},
    {name: "F40-F48 Neurotiska, stressrelaterade och somatoforma syndrom", subs: [
    {name: "F40 Fobiska syndrom"},
    {name: "F41 Andra ångestsyndrom"},
    {name: "F42 Tvångssyndrom"},
    {name: "F43 Anpassningsstörningar och reaktion på svår stress"},
    {name: "F44 Dissociativa syndrom"},
    {name: "F45 Somatoforma syndrom"},
    {name: "F48 Andra neurotiska syndrom"}
    ]},
    {name: "F50-F59 Beteendestörningar förenade med fysiologiska rubbningar och fysiska faktorer", subs: [
    {name: "F50 Ätstörningar"},
    {name: "F51 Icke organiska sömnstörningar"},
    {name: "F52 Sexuell dysfunktion, ej orsakad av organisk störning eller sjukdom"},
    {name: "F53 Psykiska störningar och beteendestörningar sammanhängande med barnsängstiden, vilka ej klassificeras annorstädes"},
    {name: "F54 Psykologiska faktorer och beteendefaktorer med betydelse för störningar eller sjukdomar som klassificeras annorstädes"},
    {name: "F55 Missbruk av substanser som ej är beroendeframkallande"},
    {name: "F59 Ospecificerade beteendesyndrom förenade med fysiologiska störningar och fysiska faktorer"}
    ]},
    {name: "F60-F69 Personlighetsstörningar och beteendestörningar hos vuxna", subs: [
    {name: "F60 Specifika personlighetsstörningar"},
    {name: "F61 Personlighetsstörningar av blandtyp och andra personlighetsstörningar"},
    {name: "F62 Kroniska personlighetsförändringar ej orsakade av hjärnskada eller hjärnsjukdom"},
    {name: "F63 Impulskontrollstörningar"},
    {name: "F64 Könsidentitetsstörningar"},
    {name: "F65 Störningar av sexuell preferens"},
    {name: "F66 Psykiska störningar och beteendestörningar sammanhängande med sexuell utveckling och orientering"},
    {name: "F68 Andra störningar av personlighet och beteende hos vuxna"},
    {name: "F69 Ospecificerad störning av personlighet och beteende hos vuxna"}
    ]},
    {name: "F70-F79 Psykisk utvecklingsstörning", subs: [
    {name: "F70 Lindrig psykisk utvecklingsstörning"},
    {name: "F71 Medelsvår psykisk utvecklingsstörning"},
    {name: "F72 Svår psykisk utvecklingsstörning"},
    {name: "F73 Grav psykisk utvecklingsstörning"},
    {name: "F78 Annan psykisk utvecklingsstörning"},
    {name: "F79 Ospecificerad psykisk utvecklingsstörning"}
    ]},
    {name: "F80-F89 Störningar av psykisk utveckling", subs: [
    {name: "F80 Specifika störningar av tal- och språkutvecklingen"},
    {name: "F81 Specifika utvecklingsstörningar av inlärningsfärdigheter"},
    {name: "F82 Specifik motorisk utvecklingsstörning"},
    {name: "F83 Blandade specifika utvecklingsstörningar"},
    {name: "F84 Genomgripande utvecklingsstörningar"},
    {name: "F88 Andra specificerade störningar av psykisk utveckling"},
    {name: "F89 Ospecificerad störning av psykisk utveckling"}
    ]},
    {name: "F90-F98 Beteendestörningar och emotionella störningar med debut vanligen under barndom och ungdomstid", subs: [
    {name: "F90 Hyperaktivitetsstörningar"},
    {name: "F91 Beteendestörningar av utagerande slag"},
    {name: "F92 Blandade störningar av beteende och känsloliv"},
    {name: "F93 Emotionella störningar med debut särskilt under barndomen"},
    {name: "F94 Störningar av social funktion med debut särskilt under barndom och ungdomstid"},
    {name: "F95 Tics"},
    {name: "F98 Andra beteendestörningar och emotionella störningar med debut vanligen under barndom och ungdomstid"}
    ]},
    {name: "F99-F99 Ospecificerad psykisk störning", subs: [
    {name: "F99 Psykisk störning ej specificerad på annat sätt"}
    ]}
  ]},
  {name: "G00-G99 Sjukdomar i nervsystemet", subs: [
    {name: "G00-G09 Inflammatoriska sjukdomar i centrala nervsystemet", subs: [
    {name: "G00 Bakteriell meningit som ej klassificeras annorstädes"},
    {name: "G01 Meningit vid bakteriesjukdomar som klassificeras annorstädes"},
    {name: "G02 Meningit vid andra infektions- och parasitsjukdomar som klassificeras annorstädes"},
    {name: "G03 Meningit av andra och icke specificerade orsaker"},
    {name: "G04 Encefalit, myelit och encefalomyelit"},
    {name: "G05 Encefalit, myelit och encefalomyelit vid sjukdomar som klassificeras annorstädes"},
    {name: "G06 Abscess och granulom i skallen och ryggradskanalen"},
    {name: "G07 Abscess och granulom i skallen och ryggradskanalen vid sjukdomar som klassificeras annorstädes"},
    {name: "G08 Flebit och tromboflebit i skallens och ryggradskanalens venösa hålrum"},
    {name: "G09 Sena effekter av inflammatoriska sjukdomar i centrala nervsystemet"}
    ]},
    {name: "G10-G14 Systemiska atrofier som primärt engagerar centrala nervsystemet", subs: [
    {name: "G10 Huntingtons sjukdom"},
    {name: "G11 Hereditär ataxi (ärftlig koordinationsrubbning)"},
    {name: "G12 Spinal muskelatrofi och besläktade syndrom"},
    {name: "G13 Systemiska atrofier som primärt engagerar centrala nervsystemet vid sjukdomar som klassificeras annorstädes"},
    {name: "G14 Postpoliosyndrom"}
    ]},
    {name: "G20-G26 Basalgangliesjukdomar och rörelserubbningar", subs: [
    {name: "G20 Parkinsons sjukdom"},
    {name: "G21 Sekundär parkinsonism"},
    {name: "G22 Parkinsonism vid sjukdomar som klassificeras annorstädes"},
    {name: "G23 Andra degenerativa sjukdomar i basala ganglierna"},
    {name: "G24 Dystoni"},
    {name: "G25 Andra basalgangliesjukdomar och rörelserubbningar"},
    {name: "G26 Basalganglietillstånd och rörelserubbningar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "G30-G32 Andra degenerativa sjukdomar i nervsystemet", subs: [
    {name: "G30 Alzheimers sjukdom"},
    {name: "G31 Andra degenerativa sjukdomar i nervsystemet som ej klassificeras annorstädes"},
    {name: "G32 Andra degenerativa tillstånd i nervsystemet vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "G35-G37 Myelinförstörande sjukdomar i centrala nervsystemet", subs: [
    {name: "G35 Multipel skleros"},
    {name: "G36 Annan akut utbredd myelinförstöring"},
    {name: "G37 Andra myelinförstörande sjukdomar i centrala nervsystemet"}
    ]},
    {name: "G40-G47 Episodiska och paroxysmala sjukdomar", subs: [
    {name: "G40 Epilepsi"},
    {name: "G41 Status epilepticus"},
    {name: "G43 Migrän"},
    {name: "G44 Andra huvudvärkssyndrom"},
    {name: "G45 Övergående cerebral ischemi (otillräcklig blodtillförsel till hjärnan) och besläktade syndrom"},
    {name: "G46 Vaskulära syndrom i hjärnan vid cerebrovaskulära sjukdomar (I60-I67²)"},
    {name: "G47 Sömnstörningar"}
    ]},
    {name: "G50-G59 Sjukdomar i nerver, nervrötter och nervplexus", subs: [
    {name: "G50 Sjukdomar i trigeminusnerven"},
    {name: "G51 Sjukdomar i facialisnerven"},
    {name: "G52 Sjukdomar i andra kranialnerver"},
    {name: "G53 Förändringar i kranialnerver vid sjukdomar som klassificeras annorstädes"},
    {name: "G54 Sjukdomar i nervrötter och nervplexus"},
    {name: "G55 Kompression av nervrötter och nervplexus vid sjukdomar som klassificeras annorstädes"},
    {name: "G56 Mononeuropati (sjukdom i en enda perifer nerv) i övre extremitet"},
    {name: "G57 Mononeuropati (sjukdom i en enda perifer nerv) i nedre extremitet"},
    {name: "G58 Andra mononeuropatier (sjukdomar i en enda perifer nerv)"},
    {name: "G59 Mononeuropati (sjukdom i en enda perifer nerv) vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "G60-G64 Polyneuropatier (samtidig sjukdom i flera perifera nerver) och andra sjukdomar i perifera nervsystemet", subs: [
    {name: "G60 Hereditär och idiopatisk neuropati (sjukdom i perifer nerv, ärftlig och av okänd orsak)"},
    {name: "G61 Inflammatorisk polyneuropati (inflammatorisk sjukdom i flera perifera nerver samtidigt)"},
    {name: "G62 Andra polyneuropatier (sjukdomar i flera perifera nerver samtidigt)"},
    {name: "G63 Polyneuropati (sjukdom i flera perifera nerver samtidigt) vid sjukdomar som klassificeras annorstädes"},
    {name: "G64 Andra sjukdomar i perifera nervsystemet"}
    ]},
    {name: "G70-G73 Neuromuskulära transmissionsrubbningar (rubbningar i överföring av impulser mellan nerver och muskler) och sjukdomar i muskler", subs: [
    {name: "G70 Myasthenia gravis och andra neuromuskulära transmissionsrubbningar (rubbningar i överföring av impulser mellan nerver och muskler)"},
    {name: "G71 Primära muskelsjukdomar"},
    {name: "G72 Andra myopatier"},
    {name: "G73 Neuromuskulära transmissionsrubbningar (rubbningar i överföringen av impulser mellan nerver och muskler) och muskelförändringar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "G80-G83 Cerebral pares och andra förlamningssyndrom", subs: [
    {name: "G80 Cerebral pares"},
    {name: "G81 Ensidig förlamning"},
    {name: "G82 Parapares och tetrapares (förlamning av båda nedre extremiteterna och förlamning av alla fyra extremiteterna)"},
    {name: "G83 Andra förlamningssyndrom"}
    ]},
    {name: "G90-G99 Andra sjukdomar i nervsystemet", subs: [
    {name: "G90 Sjukdomar i autonoma nervsystemet"},
    {name: "G91 Hydrocefalus (vattenskalle, ökad mängd hjärnkammarvatten)"},
    {name: "G92 Toxisk encefalopati (hjärnsjukdom)"},
    {name: "G93 Andra sjukdomar i hjärnan"},
    {name: "G94 Andra tillstånd i hjärnan vid sjukdomar som klassificeras annorstädes"},
    {name: "G95 Andra sjukdomar i ryggmärgen"},
    {name: "G96 Andra sjukdomar i centrala nervsystemet"},
    {name: "G97 Sjukdomar i nervsystemet orsakade av kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "G98 Andra sjukdomar i nervsystemet som ej klassificeras annorstädes"},
    {name: "G99 Andra tillstånd i nervsystemet vid sjukdomar som klassificeras annorstädes"}
    ]}
  ]},
  {name: "H00-H59 Sjukdomar i ögat och närliggande organ", subs: [
    {name: "H00-H06 Sjukdomar i ögonlock, tårapparat och ögonhåla", subs: [
    {name: "H00 Hordeolum (vagel) och chalazion"},
    {name: "H01 Andra ögonlocksinflammationer"},
    {name: "H02 Andra ögonlockssjukdomar"},
    {name: "H03 Förändringar i ögonlock vid sjukdomar som klassificeras annorstädes"},
    {name: "H04 Sjukdomar i tårapparaten"},
    {name: "H05 Sjukdomar i ögonhålan"},
    {name: "H06 Förändringar i tårapparaten och ögonhålan vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H10-H13 Sjukdomar i bindehinnan", subs: [
    {name: "H10 Bindehinneinflammation"},
    {name: "H11 Andra sjukdomar i bindehinnan"},
    {name: "H13 Förändringar i bindehinnan vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H15-H22 Sjukdomar i senhinnan, hornhinnan, regnbågshinnan och ciliarkroppen", subs: [
    {name: "H15 Sjukdomar i senhinnan"},
    {name: "H16 Hornhinneinflammation"},
    {name: "H17 Ärr och grumlingar i hornhinnan"},
    {name: "H18 Andra sjukdomar i hornhinnan"},
    {name: "H19 Förändringar i senhinnan och hornhinnan vid sjukdomar som klassificeras annorstädes"},
    {name: "H20 Inflammation i regnbågshinnan och ciliarkroppen"},
    {name: "H21 Andra förändringar i regnbågshinnan och ciliarkroppen"},
    {name: "H22 Förändringar i regnbågshinnan och ciliarkroppen vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H25-H28 Sjukdomar i linsen", subs: [
    {name: "H25 Katarakt (grå starr) vid högre ålder"},
    {name: "H26 Andra former av katarakt (grå starr)"},
    {name: "H27 Andra linsförändringar"},
    {name: "H28 Katarakt (grå starr) och andra linsförändringar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H30-H36 Sjukdomar i åderhinnan och näthinnan", subs: [
    {name: "H30 Inflammation i åderhinnan och näthinnan"},
    {name: "H31 Andra förändringar i åderhinnan"},
    {name: "H32 Förändringar i både åderhinnan och näthinnan vid sjukdomar som klassificeras annorstädes"},
    {name: "H33 Näthinneavlossning och näthinnehål"},
    {name: "H34 Ocklusion av retinala blodkärl"},
    {name: "H35 Andra sjukliga förändringar i näthinnan"},
    {name: "H36 Förändringar i näthinnan vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H40-H42 Glaukom (grön starr)", subs: [
    {name: "H40 Glaukom (grön starr)"},
    {name: "H42 Glaukom (grön starr) vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H43-H45 Sjukdomar i glaskroppen och ögongloben", subs: [
    {name: "H43 Sjukliga förändringar i glaskroppen"},
    {name: "H44 Sjukliga förändringar i ögongloben"},
    {name: "H45 Förändringar i glaskroppen och ögongloben vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H46-H48 Sjukdomar i synnerven och synbanorna", subs: [
    {name: "H46 Synnervsinflammation"},
    {name: "H47 Andra förändringar i synnerven eller synbanorna"},
    {name: "H48 Förändringar i synnerven och synbanorna vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H49-H52 Sjukdomar i ögonmusklerna, förändringar i de binokulära rörelserna samt ögats ackommodation och refraktion", subs: [
    {name: "H49 Paralytisk strabism (ögonmuskelförlamning)"},
    {name: "H50 Annan strabism (skelning)"},
    {name: "H51 Andra förändringar i de binokulära rörelserna"},
    {name: "H52 Förändringar i ögats refraktion och ackommodation"}
    ]},
    {name: "H53-H54 Synstörningar och blindhet", subs: [
    {name: "H53 Synstörningar"},
    {name: "H54 Synnedsättning inklusive blindhet (i ett öga eller båda ögonen)"}
    ]},
    {name: "H55-H59 Andra sjukdomar i ögat och närliggande organ", subs: [
    {name: "H55 Nystagmus och andra oregelbundna ögonrörelser"},
    {name: "H57 Andra sjukdomar i ögat och närliggande organ"},
    {name: "H58 Andra förändringar i ögat och närliggande organ vid sjukdomar som klassificeras annorstädes"},
    {name: "H59 Förändringar i ögat och närliggande organ efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"}
    ]}
  ]},
  {name: "H60-H95 Sjukdomar i örat och mastoidutskottet", subs: [
    {name: "H60-H62 Sjukdomar i ytterörat och hörselgången", subs: [
    {name: "H60 Extern otit (inflammation i ytterörat och yttre hörselgången)"},
    {name: "H61 Andra sjukdomar i ytterörat"},
    {name: "H62 Förändringar i ytterörat vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H65-H75 Sjukdomar i mellanörat och mastoidutskottet", subs: [
    {name: "H65 Icke varig inflammation i mellanörat"},
    {name: "H66 Varig och icke specificerad mellanöreinflammation"},
    {name: "H67 Mellanöreinflammation vid sjukdomar som klassificeras annorstädes"},
    {name: "H68 Inflammation och tilltäppning av örontrumpeten"},
    {name: "H69 Andra sjukdomar i örontrumpeten"},
    {name: "H70 Inflammation i mastoidutskottet och besläktade sjukdomar"},
    {name: "H71 Mellanörekolesteatom"},
    {name: "H72 Perforation av trumhinnan"},
    {name: "H73 Andra sjukdomar i trumhinnan"},
    {name: "H74 Andra sjukdomar i mellanörat och mastoidutskottet"},
    {name: "H75 Andra tillstånd i mellanörat och mastoidutskottet vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "H80-H83 Sjukdomar i innerörat", subs: [
    {name: "H80 Otoskleros"},
    {name: "H81 Rubbningar i balansapparatens funktion"},
    {name: "H82 Yrselsyndrom vid sjukdomar som klassificeras annorstädes"},
    {name: "H83 Andra sjukdomar i innerörat"}
    ]},
    {name: "H90-H95 Andra öronsjukdomar", subs: [
    {name: "H90 Ledningshinder och sensorineural hörselnedsättning"},
    {name: "H91 Annan hörselnedsättning"},
    {name: "H92 Öronvärk och öronflytning"},
    {name: "H93 Andra sjukdomar i örat som ej klassificeras annorstädes"},
    {name: "H94 Andra tillstånd i örat vid sjukdomar som klassificeras annorstädes"},
    {name: "H95 Sjukdomar i örat och i mastoidutskottet efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"}
    ]}
  ]},
  {name: "I00-I99 Cirkulationsorganens sjukdomar", subs: [
    {name: "I00-I02 Akut reumatisk feber", subs: [
    {name: "I00 Akut reumatisk feber utan uppgift om hjärtsjukdom"},
    {name: "I01 Akut reumatisk feber med hjärtsjukdom"},
    {name: "I02 Reumatisk korea (danssjuka)"}
    ]},
    {name: "I05-I09 Kroniska reumatiska hjärtsjukdomar", subs: [
    {name: "I05 Reumatiska mitralisklaffel"},
    {name: "I06 Reumatiska aortaklaffel"},
    {name: "I07 Reumatiska trikuspidalisklaffel"},
    {name: "I08 Multipla klaffel"},
    {name: "I09 Andra reumatiska hjärtsjukdomar"}
    ]},
    {name: "I10-I15 Hypertonisjukdomar (högt blodtryck och därmed sammanhängande sjukdomar)", subs: [
    {name: "I10 Essentiell hypertoni (högt blodtryck utan känd orsak)"},
    {name: "I11 Hypertoni med hjärtsjukdom"},
    {name: "I12 Hypertoni med njursjukdom"},
    {name: "I13 Hypertoni med hjärt- och njursjukdom"},
    {name: "I15 Sekundär hypertoni (högt blodtryck som följd av annan sjukdom)"}
    ]},
    {name: "I20-I25 Ischemiska hjärtsjukdomar (sjukdomar orsakade av otillräcklig blodtillförsel till hjärtmuskeln)", subs: [
    {name: "I20 Anginösa bröstsmärtor (kärlkramp i bröstet)"},
    {name: "I21 Akut hjärtinfarkt"},
    {name: "I22 Reinfarkt (återinsjuknande i akut hjärtinfarkt)"},
    {name: "I23 Vissa komplikationer till akut hjärtinfarkt"},
    {name: "I24 Andra akuta ischemiska hjärtsjukdomar"},
    {name: "I25 Kronisk ischemisk hjärtsjukdom"}
    ]},
    {name: "I26-I28 Sjukdomstillstånd inom lungcirkulationen", subs: [
    {name: "I26 Lungemboli"},
    {name: "I27 Andra sjukdomstillstånd inom lungcirkulationen"},
    {name: "I28 Andra sjukdomar i lungkärlen"}
    ]},
    {name: "I30-I52 Andra former av hjärtsjukdom", subs: [
    {name: "I30 Akut perikardit (hjärtsäcksinflammation)"},
    {name: "I31 Andra sjukdomar i perikardiet (hjärtsäcken)"},
    {name: "I32 Perikardit (hjärtsäcksinflammation) vid sjukdomar som klassificeras annorstädes"},
    {name: "I33 Akut och subakut endokardit (inflammatorisk hjärtklaffsjukdom)"},
    {name: "I34 Icke reumatiska mitralisklaffsjukdomar"},
    {name: "I35 Icke reumatiska aortaklaffsjukdomar"},
    {name: "I36 Icke reumatiska trikuspidalisklaffsjukdomar"},
    {name: "I37 Pulmonalisklaffsjukdomar"},
    {name: "I38 Endokardit (inflammation i hjärtklaff), icke specificerad klaff"},
    {name: "I39 Endokardit (inflammation i hjärtklaff) och hjärtklafförändringar vid sjukdomar som klassificeras annorstädes"},
    {name: "I40 Akut myokardit"},
    {name: "I41 Myokardit vid sjukdomar som klassificeras annorstädes"},
    {name: "I42 Kardiomyopati (hjärtmuskelsjukdom)"},
    {name: "I43 Hjärtmuskelsjukdom vid sjukdomar som klassificeras annorstädes"},
    {name: "I44 Atrioventrikulärt block och vänstersidigt grenblock (retledningsrubbning mellan förmak och kammare respektive i den vänstra skänkeln)"},
    {name: "I45 Andra retledningsrubbningar"},
    {name: "I46 Hjärtstillestånd"},
    {name: "I47 Paroxysmal takykardi"},
    {name: "I48 Förmaksflimmer och förmaksfladder"},
    {name: "I49 Andra hjärtarytmier"},
    {name: "I50 Hjärtinsufficiens"},
    {name: "I51 Ofullständigt preciserade hjärtsjukdomar och komplikationer till hjärtsjukdom"},
    {name: "I52 Andra hjärtsjukdomar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "I60-I69 Sjukdomar i hjärnans kärl", subs: [
    {name: "I60 Subaraknoidalblödning (blödning under spindelvävshinnan)"},
    {name: "I61 Hjärnblödning"},
    {name: "I62 Annan icke traumatisk intrakraniell blödning"},
    {name: "I63 Cerebral infarkt"},
    {name: "I64 Akut cerebrovaskulär sjukdom ej specificerad som blödning eller infarkt"},
    {name: "I65 Ocklusion och stenos av precerebrala artärer som ej lett till cerebral infarkt"},
    {name: "I66 Ocklusion och stenos av cerebrala artärer som ej lett till cerebral infarkt"},
    {name: "I67 Andra cerebrovaskulära sjukdomar"},
    {name: "I68 Förändringar i hjärnans kärl vid sjukdomar som klassificeras annorstädes"},
    {name: "I69 Sena effekter av cerebrovaskulär sjukdom"}
    ]},
    {name: "I70-I79 Sjukdomar i artärer, arterioler (småartärer) och kapillärer", subs: [
    {name: "I70 Ateroskleros (åderförkalkning)"},
    {name: "I71 Aortaaneurysm (aortabråck)"},
    {name: "I72 Annat aneurysm (artärbråck) och dissektion"},
    {name: "I73 Andra sjukdomar i perifera kärl"},
    {name: "I74 Emboli och trombos i artär"},
    {name: "I77 Andra sjukdomar i artärer och arterioler (småartärer)"},
    {name: "I78 Sjukdomar i kapillärerna"},
    {name: "I79 Förändringar i artärer, arterioler (småartärer) och kapillärer vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "I80-I89 Sjukdomar i vener, lymfkärl och lymfkörtlar som ej klassificeras annorstädes", subs: [
    {name: "I80 Flebit (inflammation i ven) och tromboflebit (inflammation och blodpropp i ven)"},
    {name: "I81 Trombos i portavenen"},
    {name: "I82 Annan venös emboli och trombos"},
    {name: "I83 Varicer (åderbråck) i nedre extremiteterna"},
    {name: "I85 Esofagusvaricer (åderbråck i matstrupen)"},
    {name: "I86 Varicer (åderbråck) med annan lokalisation"},
    {name: "I87 Andra sjukdomar i venerna"},
    {name: "I88 Icke specifik lymfadenit (inflammation i lymfkörtel)"},
    {name: "I89 Andra icke infektiösa sjukdomar i lymfkärlen och lymfkörtlarna"}
    ]},
    {name: "I95-I99 Andra och icke specificerade sjukdomar i cirkulationsorganen", subs: [
    {name: "I95 Hypotoni"},
    {name: "I97 Sjukdomar i cirkulationsorganen efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "I98 Andra tillstånd i cirkulationsorganen vid sjukdomar som klassificeras annorstädes"},
    {name: "I99 Andra och icke specificerade sjukdomar i cirkulationsorganen"}
    ]}
  ]},
  {name: "J00-J99 Andningsorganens sjukdomar", subs: [
    {name: "J00-J06 Akuta infektioner i övre luftvägarna", subs: [
    {name: "J00 Akut nasofaryngit (förkylning)"},
    {name: "J01 Akut sinuit (bihåleinflammation)"},
    {name: "J02 Akut faryngit (halskatarr)"},
    {name: "J03 Akut tonsillit (tonsillinflammation)"},
    {name: "J04 Akut laryngit (inflammation i struphuvudet) och trakeit (inflammation i luftstrupen)"},
    {name: "J05 Akut obstruktiv laryngit (pseudokrupp) och epiglottit (inflammation i struplocket)"},
    {name: "J06 Akut övre luftvägsinfektion med multipel och icke specificerad lokalisation"}
    ]},
    {name: "J09-J18 Influensa och lunginflammation", subs: [
    {name: "J09 Influensa orsakad av vissa identifierade influensavirus"},
    {name: "J10 Influensa orsakad av annat identifierat influensavirus"},
    {name: "J11 Influensa, virus ej identifierat"},
    {name: "J12 Viruspneumoni som ej klassificeras annorstädes"},
    {name: "J13 Pneumoni orsakad av Streptococcus pneumoniae"},
    {name: "J14 Pneumoni orsakad av Haemophilus influenzae"},
    {name: "J15 Bakteriell pneumoni som ej klassificeras annorstädes"},
    {name: "J16 Pneumoni orsakad av andra infektiösa organismer som ej klassificeras annorstädes"},
    {name: "J17 Pneumoni vid sjukdomar som klassificeras annorstädes"},
    {name: "J18 Pneumoni orsakad av icke specificerad mikroorganism"}
    ]},
    {name: "J20-J22 Andra akuta infektioner i nedre luftvägarna", subs: [
    {name: "J20 Akut bronkit"},
    {name: "J21 Akut bronkiolit (katarr i de små luftvägarna)"},
    {name: "J22 Icke specificerad akut infektion i nedre luftvägarna"}
    ]},
    {name: "J30-J39 Andra sjukdomar i övre luftvägarna", subs: [
    {name: "J30 Vasomotorisk och allergisk rinit (snuva)"},
    {name: "J31 Kronisk rinit (snuva), nasofaryngit (katarr i näsa och svalg) och faryngit (halskatarr)"},
    {name: "J32 Kronisk sinuit (bihåleinflammation)"},
    {name: "J33 Näspolyp"},
    {name: "J34 Andra sjukdomar i näshåla och nässinus"},
    {name: "J35 Kroniska sjukdomar i tonsiller (halsmandlar) och adenoider ('körtlar bakom näsan')"},
    {name: "J36 Halsböld"},
    {name: "J37 Kronisk laryngit och laryngotrakeit (inflammation i struphuvudet och struphuvudet-luftstrupen)"},
    {name: "J38 Sjukdomar i stämbanden och i struphuvudet som ej klassificeras annorstädes"},
    {name: "J39 Andra sjukdomar i övre luftvägarna"}
    ]},
    {name: "J40-J47 Kroniska sjukdomar i nedre luftvägarna", subs: [
    {name: "J40 Bronkit icke specificerad som akut eller kronisk"},
    {name: "J41 Kronisk bronkit utan luftvägsobstruktion"},
    {name: "J42 Icke specificerad kronisk bronkit"},
    {name: "J43 Lungemfysem"},
    {name: "J44 Kroniskt obstruktiv lungsjukdom [KOL]"},
    {name: "J45 Astma"},
    {name: "J46 Akut svår astma"},
    {name: "J47 Bronkiektasier (lokala utvidgningar av luftrören)"}
    ]},
    {name: "J60-J70 Lungsjukdomar av yttre orsaker", subs: [
    {name: "J60 Pneumokonios (dammlunga) orsakad av stenkolsdamm"},
    {name: "J61 Pneumokonios (dammlunga) orsakad av asbest och andra mineralfibrer"},
    {name: "J62 Pneumokonios (dammlunga) orsakad av damm innehållande kisel"},
    {name: "J63 Pneumokonios (dammlunga) orsakad av annat oorganiskt damm"},
    {name: "J64 Icke specificerad pneumokonios (dammlunga)"},
    {name: "J65 Pneumokonios (dammlunga) förenad med tuberkulos"},
    {name: "J66 Luftvägssjukdom orsakad av specificerat organiskt damm"},
    {name: "J67 Hypersensitivitetspneumonit (spridda, icke infektionsbetingade inflammatoriska förändringar i lungorna) orsakad av organiskt damm"},
    {name: "J68 Sjukliga tillstånd i lungorna orsakade av kemikalier, gaser, rök och ånga"},
    {name: "J69 Pneumonit (spridda, icke infektionsbetingade inflammatoriska förändringar i lungorna) orsakad av fasta och flytande ämnen"},
    {name: "J70 Sjukliga tillstånd i lungorna av andra yttre orsaker"}
    ]},
    {name: "J80-J84 Andra lungsjukdomar som huvudsakligen engagerar interstitiet (lungornas stödjevävnad)", subs: [
    {name: "J80 Akut andningssviktsyndrom hos vuxen"},
    {name: "J81 Lungödem"},
    {name: "J82 Lungeosinofili som ej klassificeras annorstädes"},
    {name: "J84 Andra interstitiella lungsjukdomar (sjukdomar i lungornas stödjevävnad)"}
    ]},
    {name: "J85-J86 Variga och nekrotiska tillstånd i nedre luftvägarna", subs: [
    {name: "J85 Abscess i lunga och mediastinum (lungmellanrummet)"},
    {name: "J86 Empyem (varansamling i lungsäcken)"}
    ]},
    {name: "J90-J94 Andra sjukdomar i lungsäcken", subs: [
    {name: "J90 Utgjutning i lungsäcken som ej klassificeras annorstädes"},
    {name: "J91 Utgjutning i lungsäcken vid tillstånd som klassificeras annorstädes"},
    {name: "J92 Pleuraplack (lokal förtjockning av lungsäcksbladen)"},
    {name: "J93 Pneumotorax (luft i lungsäcken)"},
    {name: "J94 Andra pleurala sjukdomar"}
    ]},
    {name: "J95-J99 Andra sjukdomar i andningsorganen", subs: [
    {name: "J95 Sjukdomar i andningsorganen efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "J96 Respiratorisk insufficiens (andningssvikt) som ej klassificeras annorstädes"},
    {name: "J98 Andra sjukdomar i andningsorganen"},
    {name: "J99 Lungförändringar vid sjukdomar som klassificeras annorstädes"}
    ]}
  ]},
  {name: "K00-K93 Matsmältningsorganens sjukdomar", subs: [
    {name: "K00-K14 Sjukdomar i munhåla, spottkörtlar och käkar", subs: [
    {name: "K00 Rubbningar i tändernas utveckling och frambrott"},
    {name: "K01 Retinerade tänder"},
    {name: "K02 Tandkaries"},
    {name: "K03 Andra sjukdomar i tändernas hårdvävnader"},
    {name: "K04 Sjukdomar i tandpulpan och de periradikulära vävnaderna (vävnaderna kring tandrötterna)"},
    {name: "K05 Sjukdomar i tandköttet och de parodontala vävnaderna (vävnaderna kring tänderna)"},
    {name: "K06 Andra sjukdomar i tandköttet och tandlöst alveolarutskott"},
    {name: "K07 Tand- och käkmissbildningar med malocklusion (felaktig sammanbitning)"},
    {name: "K08 Andra sjukdomar och tillstånd i tänderna och omgivande vävnader"},
    {name: "K09 Cystor i mun- och käkregionen som ej klassificeras annorstädes"},
    {name: "K10 Andra sjukdomar i käkarna"},
    {name: "K11 Sjukdomar i spottkörtlarna"},
    {name: "K12 Stomatit (inflammation i munslemhinnan) och besläktade tillstånd"},
    {name: "K13 Andra sjukdomar i läpparna och i munslemhinnan"},
    {name: "K14 Sjukdomar i tungan"}
    ]},
    {name: "K20-K31 Matstrupens, magsäckens och tolvfingertarmens sjukdomar", subs: [
    {name: "K20 Esofagit (inflammation i matstrupen)"},
    {name: "K21 Gastroesofageal refluxsjukdom (återflöde av maginnehåll till matstrupen)"},
    {name: "K22 Andra sjukdomar i matstrupen"},
    {name: "K23 Förändringar i matstrupen vid sjukdomar som klassificeras annorstädes"},
    {name: "K25 Sår i magsäcken"},
    {name: "K26 Sår i tolvfingertarmen"},
    {name: "K27 Sår i magsäcken eller tolvfingertarmen utan angiven lokalisation"},
    {name: "K28 Recidivsår efter gastroenterostomi (operation av magsäcken)"},
    {name: "K29 Inflammation i magsäcken och tolvfingertarmen"},
    {name: "K30 Funktionell dyspepsi"},
    {name: "K31 Andra sjukdomar i magsäcken och tolvfingertarmen"}
    ]},
    {name: "K35-K38 Sjukdomar i blindtarmen", subs: [
    {name: "K35 Akut appendicit"},
    {name: "K36 Annan appendicit"},
    {name: "K37 Icke specificerad appendicit"},
    {name: "K38 Andra sjukdomar i appendix"}
    ]},
    {name: "K40-K46 Bråck", subs: [
    {name: "K40 Ljumskbråck"},
    {name: "K41 Femoralbråck"},
    {name: "K42 Navelbråck"},
    {name: "K43 Främre bukväggsbråck"},
    {name: "K44 Diafragmabråck"},
    {name: "K45 Andra bukbråck"},
    {name: "K46 Icke specificerat bukbråck"}
    ]},
    {name: "K50-K52 Icke infektiös inflammation i tunntarmen och tjocktarmen", subs: [
    {name: "K50 Crohns sjukdom [regional enterit]"},
    {name: "K51 Ulcerös kolit"},
    {name: "K52 Annan icke infektiös inflammation i magsäcken och tarmen"}
    ]},
    {name: "K55-K64 Andra sjukdomar i tarmen", subs: [
    {name: "K55 Kärlsjukdomar i tarmen"},
    {name: "K56 Paralytisk ileus (tarmvred) och tarmpassagehinder utan uppgift om bråck"},
    {name: "K57 Divertikel (fickbildning) i tarmen"},
    {name: "K58 Irritabel tarm"},
    {name: "K59 Andra funktionsrubbningar i tarmen"},
    {name: "K60 Fissur och fistel i stolgångs- och ändtarmsområdet"},
    {name: "K61 Abscess i stolgångs- och ändtarmsområdet"},
    {name: "K62 Andra sjukdomar i stolgången och ändtarmen"},
    {name: "K63 Andra sjukdomar i tarmen"},
    {name: "K64 Hemorrojder och perianal venös trombos"}
    ]},
    {name: "K65-K67 Sjukdomar i bukhinnan", subs: [
    {name: "K65 Peritonit"},
    {name: "K66 Andra sjukdomar i bukhinnan"},
    {name: "K67 Sjukdomstillstånd i bukhinnan vid infektionssjukdomar som klassificeras annorstädes"}
    ]},
    {name: "K70-K77 Sjukdomar i levern", subs: [
    {name: "K70 Leversjukdom orsakad av alkohol"},
    {name: "K71 Toxisk leversjukdom"},
    {name: "K72 Leversvikt som ej klassificeras annorstädes"},
    {name: "K73 Kronisk hepatit som ej klassificeras annorstädes"},
    {name: "K74 Leverfibros och levercirros"},
    {name: "K75 Andra inflammatoriska leversjukdomar"},
    {name: "K76 Andra leversjukdomar"},
    {name: "K77 Leversjukdomar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "K80-K87 Sjukdomar i gallblåsan, gallvägarna och bukspottkörteln", subs: [
    {name: "K80 Gallstenssjukdom"},
    {name: "K81 Gallblåseinflammation"},
    {name: "K82 Andra sjukdomar i gallblåsan"},
    {name: "K83 Andra sjukdomar i gallvägarna"},
    {name: "K85 Akut pankreatit"},
    {name: "K86 Andra sjukdomar i pankreas"},
    {name: "K87 Förändringar i gallblåsan, gallvägarna och pankreas vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "K90-K93 Andra sjukdomar i matsmältningsorganen", subs: [
    {name: "K90 Malabsorption (bristfälligt näringsupptagande från tarmen)"},
    {name: "K91 Sjukdomar i matsmältningsorganen efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "K92 Andra sjukdomar i matsmältningsorganen"},
    {name: "K93 Förändringar i andra matsmältningsorgan vid sjukdomar som klassificeras annorstädes"}
    ]}
  ]},
  {name: "L00-L99 Hudens och underhudens sjukdomar", subs: [
    {name: "L00-L08 Infektioner i hud och underhud", subs: [
    {name: "L00 Exfoliativ dermatit (brännskadeliknande hudavflagning) orsakad av stafylokocker"},
    {name: "L01 Impetigo (svinkoppor)"},
    {name: "L02 Kutan abscess, furunkel och karbunkel (varbildning i huden, böld)"},
    {name: "L03 Cellulit (inflammation i underhudens bindväv)"},
    {name: "L04 Akut lymfkörtelinflammation"},
    {name: "L05 Pilonidalcysta (hårsäckscysta)"},
    {name: "L08 Andra lokala infektioner i hud och underhud"}
    ]},
    {name: "L10-L14 Blåsdermatoser (hudsjukdomar med blåsor)", subs: [
    {name: "L10 Pemfigus"},
    {name: "L11 Andra akantolytiska tillstånd"},
    {name: "L12 Pemfigoid"},
    {name: "L13 Andra blåsdermatoser"},
    {name: "L14 Blåsdermatos vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "L20-L30 Dermatit och eksem", subs: [
    {name: "L20 Atopiskt eksem (böjveckseksem)"},
    {name: "L21 Seborroisk dermatit (eksem)"},
    {name: "L22 Blöjdermatit"},
    {name: "L23 Allergisk kontaktdermatit"},
    {name: "L24 Irritativ kontaktdermatit"},
    {name: "L25 Ospecificerad kontaktdermatit"},
    {name: "L26 Exfoliativ dermatit"},
    {name: "L27 Dermatit orsakad av förtärda eller på annat sätt tillförda ämnen"},
    {name: "L28 Enkel kronisk lichen och prurigo"},
    {name: "L29 Pruritus (klåda)"},
    {name: "L30 Annan dermatit"}
    ]},
    {name: "L40-L45 Papuloskvamösa sjukdomar", subs: [
    {name: "L40 Psoriasis"},
    {name: "L41 Parapsoriasis"},
    {name: "L42 Pityriasis rosea"},
    {name: "L43 Lichen ruber planus"},
    {name: "L44 Andra papuloskvamösa sjukdomar"},
    {name: "L45 Papuloskvamösa tillstånd vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "L50-L54 Urtikaria (nässelfeber) och erytematösa tillstånd (tillstånd med hudrodnad)", subs: [
    {name: "L50 Urtikaria (nässelfeber)"},
    {name: "L51 Erythema multiforme"},
    {name: "L52 Erythema nodosum (knölros)"},
    {name: "L53 Andra erytematösa tillstånd (hudrodnader)"},
    {name: "L54 Erytem vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "L55-L59 Strålningsrelaterade sjukdomar i hud och underhud", subs: [
    {name: "L55 Solbränna"},
    {name: "L56 Andra akuta hudförändringar orsakade av ultraviolett strålning"},
    {name: "L57 Hudförändringar orsakade av kronisk exponering för icke joniserande strålning"},
    {name: "L58 Dermatit orsakad av radioaktiv strålning"},
    {name: "L59 Andra strålningsrelaterade sjukdomar i hud och underhud"}
    ]},
    {name: "L60-L75 Sjukdomar i hår, hårfolliklar, naglar, talgkörtlar och svettkörtlar", subs: [
    {name: "L60 Nagelsjukdomar"},
    {name: "L62 Nagelåkommor vid sjukdomar som klassificeras annorstädes"},
    {name: "L63 Alopecia areata (fläckformigt håravfall)"},
    {name: "L64 Androgen alopeci (manligt håravfall)"},
    {name: "L65 Annat icke ärrbildande håravfall"},
    {name: "L66 Ärrbildande håravfall"},
    {name: "L67 Hårfärgs- och hårskaftsabnormiteter"},
    {name: "L68 Hypertrikos (onormalt riklig behåring)"},
    {name: "L70 Akne"},
    {name: "L71 Rosacea"},
    {name: "L72 Follikulära cystor i hud och underhud"},
    {name: "L73 Andra sjukdomar i hudens och underhudens folliklar"},
    {name: "L74 Ekkrina svettsjukdomar"},
    {name: "L75 Apokrina svettsjukdomar"}
    ]},
    {name: "L80-L99 Andra sjukdomar i hud och underhud", subs: [
    {name: "L80 Vitiligo (pigmentfattiga fläckar i huden)"},
    {name: "L81 Andra pigmentrubbningar"},
    {name: "L82 Seborroisk keratos"},
    {name: "L83 Acanthosis nigricans"},
    {name: "L84 Liktornar och hudförhårdnader"},
    {name: "L85 Annan epidermal hudförtjockning"},
    {name: "L86 Keratodermi vid sjukdomar som klassificeras annorstädes"},
    {name: "L87 Transepidermala eliminationsrubbningar"},
    {name: "L88 Pyoderma gangraenosum"},
    {name: "L89 Trycksår"},
    {name: "L90 Atrofiska hudsjukdomar"},
    {name: "L91 Hypertrofiska hudsjukdomar"},
    {name: "L92 Granulomatösa sjukdomar i hud och underhud"},
    {name: "L93 Lupus erythematosus"},
    {name: "L94 Andra lokaliserade bindvävssjukdomar"},
    {name: "L95 Vaskulit (kärlväggsinflammation) begränsad till huden som ej klassificeras annorstädes"},
    {name: "L97 Bensår som ej klassificeras annorstädes"},
    {name: "L98 Andra sjukdomar i hud och underhud som ej klassificeras annorstädes"},
    {name: "L99 Andra tillstånd i hud och underhud vid sjukdomar som klassificeras annorstädes"}
    ]}
  ]},
  {name: "M00-M99 Sjukdomar i muskuloskeletala systemet och bindväven", subs: [
    {name: "M00-M03 Infektiösa ledsjukdomar", subs: [
    {name: "M00 Varig artrit"},
    {name: "M01 Direktinfektioner av led vid infektionssjukdomar och parasitsjukdomar som klassificeras annorstädes"},
    {name: "M02 Reaktiva artriter"},
    {name: "M03 Postinfektiösa och reaktiva artriter vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "M05-M14 Inflammatoriska polyartriter", subs: [
    {name: "M05 Seropositiv reumatoid artrit"},
    {name: "M06 Annan reumatoid artrit"},
    {name: "M07 Ledsjukdomar vid psoriasis och tarmsjukdomar"},
    {name: "M08 Juvenil artrit"},
    {name: "M09 Juvenil artrit vid sjukdomar som klassificeras annorstädes"},
    {name: "M10 Gikt"},
    {name: "M11 Andra kristallartropatier"},
    {name: "M12 Andra specificerade artropatier"},
    {name: "M13 Annan artrit"},
    {name: "M14 Artropatier vid andra sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "M15-M19 Artros", subs: [
    {name: "M15 Polyartros"},
    {name: "M16 Höftledsartros"},
    {name: "M17 Knäartros"},
    {name: "M18 Artros i första karpometakarpalleden"},
    {name: "M19 Andra artroser"}
    ]},
    {name: "M20-M25 Andra ledsjukdomar", subs: [
    {name: "M20 Förvärvade deformiteter i fingrar och tår"},
    {name: "M21 Andra förvärvade deformiteter av extremiteter"},
    {name: "M22 Sjukdomar i patella"},
    {name: "M23 Andra sjukliga förändringar i knäled"},
    {name: "M24 Andra specificerade rubbningar i leder"},
    {name: "M25 Andra ledsjukdomar som ej klassificeras annorstädes"}
    ]},
    {name: "M30-M36 Inflammatoriska systemsjukdomar", subs: [
    {name: "M30 Polyarteritis nodosa och besläktade tillstånd"},
    {name: "M31 Andra nekrotiserande vaskulopatier (kärlsjukdomar)"},
    {name: "M32 Systemisk lupus erythematosus [SLE]"},
    {name: "M33 Dermatopolymyosit"},
    {name: "M34 Systemisk skleros"},
    {name: "M35 Andra inflammatoriska systemsjukdomar"},
    {name: "M36 Systemiska sjukdomar med led- och bindvävsengagemang vilka klassificeras annorstädes"}
    ]},
    {name: "M40-M43 Deformerande ryggsjukdomar", subs: [
    {name: "M40 Kyfos och lordos"},
    {name: "M41 Skolios"},
    {name: "M42 Osteokondros (degenerativ brosk-bensjukdom) i kotpelaren"},
    {name: "M43 Andra deformerande ryggsjukdomar"}
    ]},
    {name: "M45-M49 Spondylopatier", subs: [
    {name: "M45 Pelvospondylit [Bechterews sjukdom]"},
    {name: "M46 Andra inflammatoriska sjukdomar i ryggraden"},
    {name: "M47 Spondylos"},
    {name: "M48 Andra spondylopatier"},
    {name: "M49 Spondylopatier vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "M50-M54 Andra ryggsjukdomar", subs: [
    {name: "M50 Sjukdomar i halskotpelarens mellankotskivor"},
    {name: "M51 Andra sjukdomar i mellankotskivorna"},
    {name: "M53 Andra ryggsjukdomar som ej klassificeras annorstädes"},
    {name: "M54 Ryggvärk"}
    ]},
    {name: "M60-M63 Muskelsjukdomar", subs: [
    {name: "M60 Myosit (muskelinflammation)"},
    {name: "M61 Muskelkalcifikation och muskelossifikation (förkalkning och förbening i muskel)"},
    {name: "M62 Andra muskelsjukdomar"},
    {name: "M63 Muskelsjukdomar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "M65-M68 Sjukdomar i ledhinnor och senor", subs: [
    {name: "M65 Synovit och tenosynovit (inflammation i ledhinnor och senor)"},
    {name: "M66 Spontanruptur av ledhinna och sena"},
    {name: "M67 Andra sjukdomar i ledhinna och sena"},
    {name: "M68 Sjukdomstillstånd i ledhinnor och senor vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "M70-M79 Andra sjukdomar i mjukvävnader", subs: [
    {name: "M70 Sjukdomar i mjukvävnader som har samband med användning, överansträngning och tryck"},
    {name: "M71 Andra sjukdomar i bursor (slemsäckar)"},
    {name: "M72 Fibroplastiska sjukdomar (fibroplasier)"},
    {name: "M73 Förändringar i mjukvävnader vid sjukdomar som klassificeras annorstädes"},
    {name: "M75 Sjukdomstillstånd i skulderled"},
    {name: "M76 Entesopatier (sjukdomar i perifera ligament- och muskelfästen) i nedre extremitet, med undantag för fot"},
    {name: "M77 Andra entesopatier (sjukdomar i perifera ligament- och muskelfästen)"},
    {name: "M79 Andra sjukdomstillstånd i mjukvävnader som ej klassificeras annorstädes"}
    ]},
    {name: "M80-M85 Rubbningar i bentäthet och benstruktur", subs: [
    {name: "M80 Osteoporos med patologiska frakturer"},
    {name: "M81 Osteoporos utan patologisk fraktur"},
    {name: "M82 Osteoporos vid sjukdomar som klassificeras annorstädes"},
    {name: "M83 Osteomalaci hos vuxen"},
    {name: "M84 Kontinuitetsavbrott i benvävnad"},
    {name: "M85 Andra rubbningar i bentäthet och benstruktur"}
    ]},
    {name: "M86-M90 Andra sjukdomar i benvävnad", subs: [
    {name: "M86 Osteomyelit (benröta)"},
    {name: "M87 Osteonekros (benvävnadsdöd)"},
    {name: "M88 Pagets deformerande bensjukdom"},
    {name: "M89 Andra sjukdomstillstånd i benvävnad"},
    {name: "M90 Sjukdomstillstånd i benvävnad vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "M91-M94 Sjukdomar i broskvävnad", subs: [
    {name: "M91 Juvenil osteokondros (ben-broskdegeneration hos unga) i höft och bäcken"},
    {name: "M92 Annan juvenil osteokondros"},
    {name: "M93 Andra sjukdomar i ben- och broskvävnad"},
    {name: "M94 Andra brosksjukdomar"}
    ]},
    {name: "M95-M99 Andra sjukdomar i muskuloskeletala systemet och bindväven", subs: [
    {name: "M95 Andra förvärvade deformiteter i muskuloskeletala systemet och bindväven"},
    {name: "M96 Muskuloskeletala sjukdomar efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "M99 Biomekanisk dysfunktion som ej klassificeras annorstädes"}
    ]}
  ]},
  {name: "N00-N99 Sjukdomar i urin- och könsorganen", subs: [
    {name: "N00-N08 Glomerulussjukdomar", subs: [
    {name: "N00 Akut glomerulonefrit"},
    {name: "N01 Snabbt progredierande glomerulonefrit"},
    {name: "N02 Recidiverande och bestående hematuri"},
    {name: "N03 Kronisk glomerulonefrit"},
    {name: "N04 Nefrotiskt syndrom"},
    {name: "N05 Icke specificerad glomerulonefrit"},
    {name: "N06 Isolerad proteinuri med specificerad morfologisk skada"},
    {name: "N07 Hereditär nefropati som ej klassificeras annorstädes"},
    {name: "N08 Glomerulära sjukdomstillstånd vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "N10-N16 Tubulo-interstitiella njursjukdomar", subs: [
    {name: "N10 Akut tubulo-interstitiell nefrit"},
    {name: "N11 Kronisk tubulo-interstitiell nefrit"},
    {name: "N12 Tubulo-interstitiell nefrit, ej specificerad som akut eller kronisk"},
    {name: "N13 Avflödeshinder och reflux i urinvägarna"},
    {name: "N14 Tubulo-interstitiella och tubulära njursjukdomar orsakade av läkemedel och tungmetaller"},
    {name: "N15 Andra tubulo-interstitiella njursjukdomar"},
    {name: "N16 Tubulo-interstitiella njursjukdomar vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "N17-N19 Njursvikt", subs: [
    {name: "N17 Akut njursvikt"},
    {name: "N18 Kronisk njursvikt"},
    {name: "N19 Njursvikt, icke specificerad som akut eller kronisk"}
    ]},
    {name: "N20-N23 Sten i urinvägarna", subs: [
    {name: "N20 Sten i njure och uretär (urinledare)"},
    {name: "N21 Sten i de nedre urinvägarna"},
    {name: "N22 Sten i urinvägarna vid sjukdomar som klassificeras annorstädes"},
    {name: "N23 Icke specificerad njurkolik"}
    ]},
    {name: "N25-N29 Andra sjukdomar i njure och urinledare", subs: [
    {name: "N25 Sjukdomar orsakade av nedsatt funktion i njurtubuli"},
    {name: "N26 Icke specificerad skrumpnjure"},
    {name: "N27 Liten njure av okänd orsak"},
    {name: "N28 Andra sjukdomar i njure och uretär (urinledare) som ej klassificeras annorstädes"},
    {name: "N29 Andra sjukdomstillstånd i njure och uretär (urinledare) vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "N30-N39 Andra sjukdomar i urinorganen", subs: [
    {name: "N30 Cystit (blåskatarr)"},
    {name: "N31 Neuromuskulär blåsfunktionsrubbning som ej klassificeras annorstädes"},
    {name: "N32 Andra sjukdomar i urinblåsan"},
    {name: "N33 Sjukliga tillstånd i urinblåsan vid sjukdomar som klassificeras annorstädes"},
    {name: "N34 Uretrit (inflammation i urinröret) och uretrasyndrom"},
    {name: "N35 Uretrastriktur (urinrörsförträngning)"},
    {name: "N36 Andra sjukdomar i uretra (urinröret)"},
    {name: "N37 Sjukliga tillstånd i uretra (urinröret) vid sjukdomar som klassificeras annorstädes"},
    {name: "N39 Andra sjukdomar i urinorganen"}
    ]},
    {name: "N40-N51 Sjukdomar i de manliga könsorganen", subs: [
    {name: "N40 Prostataförstoring"},
    {name: "N41 Inflammatoriska sjukdomar i prostata"},
    {name: "N42 Andra sjukdomar i prostata"},
    {name: "N43 Hydrocele (vattenbråck) och spermatocele (spermiecysta)"},
    {name: "N44 Torsion av testikel"},
    {name: "N45 Testikelinflammation och bitestikelinflammation"},
    {name: "N46 Infertilitet hos man"},
    {name: "N47 Förhudssjukdomar"},
    {name: "N48 Andra sjukdomar i penis"},
    {name: "N49 Inflammatoriska sjukdomar i de manliga könsorganen som ej klassificeras annorstädes"},
    {name: "N50 Andra sjukdomar i de manliga könsorganen"},
    {name: "N51 Sjukliga tillstånd i de manliga könsorganen vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "N60-N64 Sjukdomar i bröstkörtel", subs: [
    {name: "N60 Benign bröstkörteldysplasi"},
    {name: "N61 Inflammatoriska sjukdomar i bröstkörtel"},
    {name: "N62 Hypertrofi av bröstkörtel"},
    {name: "N63 Icke specificerad knuta i bröstkörtel"},
    {name: "N64 Andra sjukdomar i bröstkörtel"}
    ]},
    {name: "N70-N77 Inflammatoriska sjukdomar i de kvinnliga bäckenorganen", subs: [
    {name: "N70 Salpingit och ooforit (inflammation i äggledare och äggstock)"},
    {name: "N71 Inflammatorisk sjukdom i livmodern utom livmoderhalsen"},
    {name: "N72 Inflammatorisk sjukdom i livmoderhalsen"},
    {name: "N73 Andra inflammatoriska sjukdomar i det kvinnliga bäckenet"},
    {name: "N74 Inflammatoriska tillstånd i det kvinnliga bäckenet vid sjukdomar som klassificeras annorstädes"},
    {name: "N75 Sjukdomar i Bartholins körtel"},
    {name: "N76 Annan inflammation i vagina och vulva"},
    {name: "N77 Vulvovaginal ulceration och inflammation vid sjukdomar som klassificeras annorstädes"}
    ]},
    {name: "N80-N98 Icke inflammatoriska sjukdomar i de kvinnliga könsorganen", subs: [
    {name: "N80 Endometrios (felbelägen livmoderslemhinna)"},
    {name: "N81 Framfall av livmodern och slidan"},
    {name: "N82 Fistlar som engagerar de kvinnliga könsorganen"},
    {name: "N83 Icke inflammatoriska sjukdomar i äggstockar, äggledare och breda ligament"},
    {name: "N84 Polyp i de kvinnliga könsorganen"},
    {name: "N85 Andra icke inflammatoriska sjukdomar i livmodern, utom livmoderhalsen"},
    {name: "N86 Erosion och ektropi i cervix uteri (livmoderhalsen)"},
    {name: "N87 Dysplasi i cervix uteri (livmoderhalsen)"},
    {name: "N88 Andra icke inflammatoriska sjukdomar i cervix uteri (livmoderhalsen)"},
    {name: "N89 Andra icke inflammatoriska sjukdomar i vagina"},
    {name: "N90 Andra icke inflammatoriska sjukdomar i vulva och perineum (mellangården)"},
    {name: "N91 Utebliven, sparsam och gles menstruation"},
    {name: "N92 Riklig, frekvent och oregelbunden menstruation"},
    {name: "N93 Annan onormal blödning från livmodern och slidan"},
    {name: "N94 Smärtor och andra symtom som har samband med de kvinnliga könsorganen och menstruationscykeln"},
    {name: "N95 Sjukliga tillstånd i samband med klimakteriet"},
    {name: "N96 Kvinna med habituella aborter (upprepade missfall)"},
    {name: "N97 Kvinnlig infertilitet (ofruktsamhet)"},
    {name: "N98 Komplikationer i samband med assisterad befruktning"}
    ]},
    {name: "N99-N99 Andra sjukliga tillstånd i urin- och könsorganen", subs: [
    {name: "N99 Sjukliga tillstånd i urin- och könsorganen efter kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"}
    ]}
  ]},
  {name: "O00-O99 Graviditet, förlossning och barnsängstid", subs: [
    {name: "O00-O08 Graviditet som avslutas med abort", subs: [
    {name: "O00 Utomkvedshavandeskap"},
    {name: "O01 Druvbörd"},
    {name: "O02 Annat onormalt utfall av befruktningen"},
    {name: "O03 Spontanabort"},
    {name: "O04 Legal abort"},
    {name: "O05 Annan abort"},
    {name: "O06 Icke specificerad abort"},
    {name: "O07 Misslyckat försök till abort"},
    {name: "O08 Komplikationer efter abort, utomkvedshavandeskap och druvbörd"}
    ]},
    {name: "O10-O16 Ödem, proteinuri (äggvita i urinen) och hypertoni under graviditet, förlossning och barnsängstid", subs: [
    {name: "O10 Tidigare konstaterad hypertoni som komplikation under graviditet, förlossning och barnsängstid"},
    {name: "O11 Preeklampsi som tillstöter till kronisk hypertoni"},
    {name: "O12 Graviditetsödem och proteinuri utan hypertoni"},
    {name: "O13 Graviditetshypertoni"},
    {name: "O14 Preeklampsi"},
    {name: "O15 Graviditetskramper"},
    {name: "O16 Icke specificerad hypertoni hos modern"}
    ]},
    {name: "O20-O29 Andra sjukdomar hos den blivande modern i huvudsak sammanhängande med graviditeten", subs: [
    {name: "O20 Blödning i tidig graviditet"},
    {name: "O21 Ihållande kräkningar under graviditeten"},
    {name: "O22 Venösa komplikationer och hemorrojder under graviditeten"},
    {name: "O23 Infektioner i urin- och könsorganen under graviditeten"},
    {name: "O24 Diabetes (sockersjuka) under graviditeten"},
    {name: "O25 Undernäring under graviditeten"},
    {name: "O26 Vård av modern för andra tillstånd i huvudsak sammanhängande med graviditeten"},
    {name: "O28 Onormala fynd vid undersökning av den blivande modern"},
    {name: "O29 Komplikationer vid anestesi under graviditet"}
    ]},
    {name: "O30-O48 Vård under graviditet på grund av problem relaterade till fostret och amnionhålan samt befarade förlossningsproblem", subs: [
    {name: "O30 Flerbördsgraviditet"},
    {name: "O31 Komplikationer specifika för flerbördsgraviditet"},
    {name: "O32 Vård av blivande moder på grund av känt eller misstänkt onormalt fosterläge"},
    {name: "O33 Vård av blivande moder på grund av känt eller misstänkt missförhållande mellan bäcken- och fosterstorlek"},
    {name: "O34 Vård av blivande moder på grund av känd eller misstänkt abnormitet i bäckenorganen"},
    {name: "O35 Vård av blivande moder på grund av känd eller misstänkt abnormitet eller skada hos fostret"},
    {name: "O36 Vård av blivande moder på grund av andra kända eller misstänkta problem hos fostret"},
    {name: "O40 Onormalt stor mängd fostervatten"},
    {name: "O41 Andra problem hänförbara till fostervattnet och hinnorna"},
    {name: "O42 För tidig hinnbristning"},
    {name: "O43 Onormala tillstånd i moderkakan"},
    {name: "O44 Föreliggande moderkaka"},
    {name: "O45 För tidig avlossning av moderkakan"},
    {name: "O46 Blödning före förlossningen som ej klassificeras annorstädes"},
    {name: "O47 Förvärkar och hotande förtidsbörd"},
    {name: "O48 Överburenhet"}
    ]},
    {name: "O60-O75 Komplikationer vid värkarbete och förlossning", subs: [
    {name: "O60 För tidigt värkarbete och prematurbörd"},
    {name: "O61 Försök till igångsättande av förlossningsarbetet"},
    {name: "O62 Värkrubbningar"},
    {name: "O63 Utdragen förlossning"},
    {name: "O64 Förlossningshinder orsakat av onormalt fosterläge och felaktig fosterbjudning"},
    {name: "O65 Förlossningshinder orsakat av abnormitet i moderns bäcken"},
    {name: "O66 Annat förlossningshinder"},
    {name: "O67 Värkarbete och förlossning komplicerade av blödning under förlossningsarbetet, som ej klassificeras annorstädes"},
    {name: "O68 Värkarbete och förlossning komplicerade av fetal distress (syrebrist hos fostret)"},
    {name: "O69 Värkarbete och förlossning med navelsträngskomplikationer"},
    {name: "O70 Skador i bäckenbotten under förlossningen"},
    {name: "O71 Andra förlossningsskador på modern"},
    {name: "O72 Blödning i efterbördsskedet"},
    {name: "O73 Kvarhållen moderkaka eller kvarhållna hinnor utan blödning"},
    {name: "O74 Komplikationer vid anestesi under värkarbete och förlossning"},
    {name: "O75 Andra komplikationer till värkarbete och förlossning som ej klassificeras annorstädes"}
    ]},
    {name: "O80-O84 Förlossning", subs: [
    {name: "O80 Spontanförlossning vid enkelbörd"},
    {name: "O81 Enkelbördsförlossning med tång eller sugklocka"},
    {name: "O82 Kejsarsnittsförlossning, enkelbörd"},
    {name: "O83 Annan enkelbördsförlossning med förlossningshjälp"},
    {name: "O84 Förlossning vid flerbörd"}
    ]},
    {name: "O85-O92 Komplikationer huvudsakligen sammanhängande med barnsängstiden", subs: [
    {name: "O85 Barnsängsfeber"},
    {name: "O86 Andra infektioner under barnsängstiden"},
    {name: "O87 Venösa komplikationer och hemorrojder under barnsängstiden"},
    {name: "O88 Obstetrisk emboli"},
    {name: "O89 Komplikationer vid anestesi under barnsängstiden"},
    {name: "O90 Komplikationer under barnsängstiden som ej klassificeras annorstädes"},
    {name: "O91 Infektioner i bröstkörtel och bröstvårta i samband med barnsbörd"},
    {name: "O92 Andra bröstkörtelsjukdomar och rubbningar i laktationen i samband med barnsbörd"}
    ]},
    {name: "O94-O99 Andra obstetriska tillstånd som ej klassificeras annorstädes (O94-O99)", subs: [
    {name: "O94 Sena besvär av komplikation till graviditet, förlossning och barnsängstid"},
    {name: "O95 Obstetrisk död av icke specificerad orsak"},
    {name: "O96 Död av obstetrisk orsak som inträffar mer än 42 dagar men mindre än ett år efter förlossningen"},
    {name: "O97 Död av följdtillstånd efter obstetriska orsaker"},
    {name: "O98 Infektionssjukdomar och parasitsjukdomar hos modern som klassificeras annorstädes men som komplicerar graviditet, förlossning och barnsängstid"},
    {name: "O99 Andra sjukdomar hos modern som klassificeras annorstädes men som komplicerar graviditet, förlossning och barnsängstid"}
    ]}
  ]},
  {name: "P00-P96 Vissa perinatala tillstånd", subs: [
    {name: "P00-P04 Foster och nyfödd som påverkats av tillstånd hos modern och av komplikationer vid graviditet, värkarbete och förlossning", subs: [
    {name: "P00 Foster och nyfödd som påverkats av tillstånd hos modern, vilket ej behöver ha samband med den aktuella graviditeten"},
    {name: "P01 Foster och nyfödd som påverkats av graviditetskomplikationer hos modern"},
    {name: "P02 Foster och nyfödd som påverkats av komplikationer från moderkaka, navelsträng och fosterhinnor"},
    {name: "P03 Foster och nyfödd som påverkats av andra komplikationer under värkarbete och förlossning"},
    {name: "P04 Foster och nyfödd som påverkats av skadlig inverkan överförd genom placenta eller bröstmjölk"}
    ]},
    {name: "P05-P08 Sjukdomar som har samband med graviditetslängd och fostertillväxt", subs: [
    {name: "P05 Tillväxthämning under fosterlivet och undernäring hos fostret"},
    {name: "P07 Rubbningar i samband med underburenhet och låg födelsevikt som ej klassificeras annorstädes"},
    {name: "P08 Rubbningar i samband med överburenhet och hög födelsevikt"}
    ]},
    {name: "P10-P15 Förlossningsskador", subs: [
    {name: "P10 Intrakraniell skada och blödning orsakad av förlossningsskada"},
    {name: "P11 Andra förlossningsskador i centrala nervsystemet"},
    {name: "P12 Förlossningsskada på huvudet"},
    {name: "P13 Förlossningsskada på skelettet"},
    {name: "P14 Förlossningsskada i perifera nervsystemet"},
    {name: "P15 Andra förlossningsskador"}
    ]},
    {name: "P20-P29 Sjukdomar i andningsorgan och cirkulationsorgan specifika för den perinatala perioden", subs: [
    {name: "P20 Intrauterin hypoxi (syrebrist före förlossningen)"},
    {name: "P21 Syrebrist vid förlossningen"},
    {name: "P22 Respiratorisk distress (andningssvårigheter) hos nyfödd"},
    {name: "P23 Medfödd pneumoni (lunginflammation)"},
    {name: "P24 Aspirationssyndrom i nyföddhetsperioden"},
    {name: "P25 Interstitiellt emfysem och besläktade tillstånd i den perinatala perioden"},
    {name: "P26 Lungblödning under den perinatala perioden"},
    {name: "P27 Kronisk sjukdom i andningsorganen under den perinatala perioden"},
    {name: "P28 Andra andningsrubbningar under den perinatala perioden"},
    {name: "P29 Sjukdomar i cirkulationsorganen under den perinatala perioden"}
    ]},
    {name: "P35-P39 Infektioner specifika för den perinatala perioden", subs: [
    {name: "P35 Medfödda virussjukdomar"},
    {name: "P36 Bakteriell sepsis hos nyfödd"},
    {name: "P37 Andra medfödda infektionssjukdomar och parasitsjukdomar"},
    {name: "P38 Navelinfektion hos nyfödd med eller utan lätt blödning"},
    {name: "P39 Andra infektioner specifika för den perinatala perioden"}
    ]},
    {name: "P50-P61 Blödningssjukdomar och blodsjukdomar hos foster och nyfödd", subs: [
    {name: "P50 Blödning hos foster"},
    {name: "P51 Navelblödning hos nyfödd"},
    {name: "P52 Intrakraniell, icke traumatisk blödning hos foster och nyfödd"},
    {name: "P53 Blödningssjukdom hos foster och nyfödd"},
    {name: "P54 Andra blödningar hos nyfödd"},
    {name: "P55 Hemolytisk sjukdom (blodkroppssönderfall) hos foster och nyfödd"},
    {name: "P56 Hydrops fetalis (vattensvullnad hos foster) orsakad av hemolytisk sjukdom (blodkroppssönderfall)"},
    {name: "P57 Kärnikterus"},
    {name: "P58 Gulsot hos nyfödd orsakad av annan höggradig hemolys (blodkroppssönderfall)"},
    {name: "P59 Gulsot hos nyfödd av andra och icke specificerade orsaker"},
    {name: "P60 Disseminerad intravasal koagulation [DIC] hos foster och nyfödd"},
    {name: "P61 Andra blodsjukdomar under den perinatala perioden"}
    ]},
    {name: "P70-P74 Övergående endokrina rubbningar och ämnesomsättningsrubbningar specifika för foster och nyfödd", subs: [
    {name: "P70 Övergående rubbningar i kolhydratomsättningen specifika för foster och nyfödd"},
    {name: "P71 Övergående rubbningar i kalcium- och magnesiumomsättningen hos nyfödd"},
    {name: "P72 Andra övergående endokrina sjukdomar hos nyfödd"},
    {name: "P74 Andra övergående elektrolytrubbningar och ämnesomsättningsrubbningar hos nyfödd"}
    ]},
    {name: "P75-P78 Sjukdomar i matsmältningsorganen hos foster och nyfödd", subs: [
    {name: "P75 Mekoniumileus vid cystisk fibros"},
    {name: "P76 Annat tarmhinder hos nyfödd"},
    {name: "P77 Nekrotiserande enterokolit (tarminflammation med vävnadsdöd) hos foster och nyfödd"},
    {name: "P78 Andra sjukdomar i matsmältningsorganen hos foster och nyfödd"}
    ]},
    {name: "P80-P83 Tillstånd som engagerar hud och temperaturreglering hos foster och nyfödd", subs: [
    {name: "P80 Undertemperatur hos nyfödd"},
    {name: "P81 Andra störningar i temperaturregleringen hos nyfödd"},
    {name: "P83 Andra tillstånd som engagerar huden, specifika för foster och nyfödd"}
    ]},
    {name: "P90-P96 Andra sjukdomar och rubbningar under den perinatala perioden", subs: [
    {name: "P90 Kramper hos nyfödd"},
    {name: "P91 Andra cerebrala rubbningar hos nyfödd"},
    {name: "P92 Uppfödningsproblem hos nyfödd"},
    {name: "P93 Reaktioner och förgiftningar orsakade av läkemedel som tillförts foster och nyfödd"},
    {name: "P94 Muskeltonusrubbningar hos nyfödd"},
    {name: "P95 Fosterdöd av icke specificerad orsak"},
    {name: "P96 Andra tillstånd under den perinatala perioden"}
    ]}
  ]},
  {name: "Q00-Q99 Medfödda missbildningar, deformiteter och kromosomavvikelser", subs: [
    {name: "Q00-Q07 Medfödda missbildningar av nervsystemet", subs: [
    {name: "Q00 Anencefali (avsaknad av hjärna) och liknande missbildningar"},
    {name: "Q01 Encefalocele (hjärnbråck)"},
    {name: "Q02 Mikrocefali (liten hjärna)"},
    {name: "Q03 Medfödd hydrocefalus (vattenskalle)"},
    {name: "Q04 Andra medfödda missbildningar av hjärnan"},
    {name: "Q05 Spina bifida (kluven ryggrad)"},
    {name: "Q06 Andra medfödda missbildningar av ryggmärgen"},
    {name: "Q07 Andra medfödda missbildningar i nervsystemet"}
    ]},
    {name: "Q10-Q18 Medfödda missbildningar av öga, öra, ansikte och hals", subs: [
    {name: "Q10 Medfödda missbildningar av ögonlock, tårsäck och tårkanal samt ögonhåla"},
    {name: "Q11 Anoftalmos (avsaknad av öga), mikroftalmos (förminskat öga) och makroftalmos (förstorat öga)"},
    {name: "Q12 Medfödd linsmissbildning"},
    {name: "Q13 Medfödda missbildningar i ögats främre segment"},
    {name: "Q14 Medfödda missbildningar i ögats bakre segment"},
    {name: "Q15 Andra medfödda missbildningar av öga"},
    {name: "Q16 Medfödda missbildningar av öra som orsakar nedsatt hörsel"},
    {name: "Q17 Andra medfödda missbildningar av öra"},
    {name: "Q18 Andra medfödda missbildningar av ansiktet och halsen"}
    ]},
    {name: "Q20-Q28 Medfödda missbildningar av cirkulationsorganen", subs: [
    {name: "Q20 Medfödda missbildningar av hjärtats kamrar och förbindelser"},
    {name: "Q21 Medfödda missbildningar av hjärtats skiljeväggar"},
    {name: "Q22 Medfödda missbildningar av pulmonalis- och trikuspidalisklaffar"},
    {name: "Q23 Medfödda missbildningar av aorta- och mitralisklaffar"},
    {name: "Q24 Andra medfödda hjärtmissbildningar"},
    {name: "Q25 Medfödda missbildningar av de stora artärerna"},
    {name: "Q26 Medfödda missbildningar av de stora venerna"},
    {name: "Q27 Andra medfödda missbildningar av perifera kärlsystemet"},
    {name: "Q28 Andra medfödda missbildningar av cirkulationsorganen"}
    ]},
    {name: "Q30-Q34 Medfödda missbildningar av andningsorganen", subs: [
    {name: "Q30 Medfödda missbildningar av näsan"},
    {name: "Q31 Medfödda missbildningar av struphuvudet"},
    {name: "Q32 Medfödda missbildningar av luftstrupe och bronk"},
    {name: "Q33 Medfödda lungmissbildningar"},
    {name: "Q34 Andra medfödda missbildningar av andningsorganen"}
    ]},
    {name: "Q35-Q37 Kluven läpp och gom", subs: [
    {name: "Q35 Kluven gom"},
    {name: "Q36 Kluven läpp"},
    {name: "Q37 Kluven gom med kluven läpp"}
    ]},
    {name: "Q38-Q45 Andra medfödda missbildningar av matsmältningsorganen", subs: [
    {name: "Q38 Andra medfödda missbildningar av tunga, mun och svalg"},
    {name: "Q39 Medfödda missbildningar av matstrupen"},
    {name: "Q40 Andra medfödda missbildningar i övre delen av matsmältningskanalen"},
    {name: "Q41 Medfödd avsaknad, atresi och stenos av tunntarmen"},
    {name: "Q42 Medfödd avsaknad, atresi och stenos av tjocktarmen"},
    {name: "Q43 Andra medfödda missbildningar av tarmen"},
    {name: "Q44 Medfödda missbildningar av gallblåsan, gallgångarna och levern"},
    {name: "Q45 Andra medfödda missbildningar av matsmältningsorganen"}
    ]},
    {name: "Q50-Q56 Medfödda missbildningar av könsorganen", subs: [
    {name: "Q50 Medfödda missbildningar av äggstockar, äggledare och breda ligament"},
    {name: "Q51 Medfödda missbildningar av livmodern och livmoderhalsen"},
    {name: "Q52 Andra medfödda missbildningar av de kvinnliga könsorganen"},
    {name: "Q53 Icke nedstigen testikel"},
    {name: "Q54 Hypospadi"},
    {name: "Q55 Andra medfödda missbildningar av de manliga könsorganen"},
    {name: "Q56 Obestämt kön och pseudohermafroditism"}
    ]},
    {name: "Q60-Q64 Medfödda missbildningar av urinorganen", subs: [
    {name: "Q60 Avsaknad och annan medfödd underutveckling av njure"},
    {name: "Q61 Cystnjuresjukdom"},
    {name: "Q62 Medfödda avflödeshinder i njurbäcken och medfödda missbildningar i urinledare"},
    {name: "Q63 Andra medfödda missbildningar av njure"},
    {name: "Q64 Andra medfödda missbildningar av urinorganen"}
    ]},
    {name: "Q65-Q79 Medfödda missbildningar och deformiteter av muskler och skelett", subs: [
    {name: "Q65 Medfödda höftdeformiteter"},
    {name: "Q66 Medfödda missbildningar av fötterna"},
    {name: "Q67 Medfödda muskuloskeletala deformiteter av skalle, ansikte, kotpelare och bröstkorg"},
    {name: "Q68 Andra medfödda deformiteter av muskler och skelett"},
    {name: "Q69 Övertaliga fingrar och tår"},
    {name: "Q70 Sammanväxning av fingrar och tår"},
    {name: "Q71 Reduktionsmissbildningar av övre extremitet"},
    {name: "Q72 Reduktionsmissbildningar av nedre extremitet"},
    {name: "Q73 Reduktionsmissbildningar av icke specificerad extremitet"},
    {name: "Q74 Andra medfödda missbildningar av extremiteterna"},
    {name: "Q75 Andra medfödda missbildningar av skallens och ansiktets ben"},
    {name: "Q76 Medfödda missbildningar av kotpelaren och bröstkorgens ben"},
    {name: "Q77 Osteokondrodysplasi med bristande tillväxt av rörben och kotpelare"},
    {name: "Q78 Andra osteokondrodysplasier"},
    {name: "Q79 Medfödd missbildning av muskler och skelett som ej klassificeras annorstädes"}
    ]},
    {name: "Q80-Q89 Andra medfödda missbildningar", subs: [
    {name: "Q80 Medfödd iktyos"},
    {name: "Q81 Epidermolysis bullosa"},
    {name: "Q82 Andra medfödda missbildningar av huden"},
    {name: "Q83 Medfödda missbildningar av bröstkörtel"},
    {name: "Q84 Andra medfödda missbildningar av täckvävnad"},
    {name: "Q85 Fakomatoser som ej klassificeras annorstädes"},
    {name: "Q86 Medfödda missbildningssyndrom orsakade av kända yttre orsaker som ej klassificeras annorstädes"},
    {name: "Q87 Andra specificerade medfödda missbildningssyndrom som engagerar multipla organsystem"},
    {name: "Q89 Andra medfödda missbildningar som ej klassificeras annorstädes"}
    ]},
    {name: "Q90-Q99 Kromosomavvikelser som ej klassificeras annorstädes", subs: [
    {name: "Q90 Downs syndrom"},
    {name: "Q91 Edwards syndrom och Pataus syndrom"},
    {name: "Q92 Andra autosomala trisomier och partiella trisomier som ej klassificeras annorstädes"},
    {name: "Q93 Autosomala monosomier och deletioner som ej klassificeras annorstädes"},
    {name: "Q95 Balanserade rearrangemang och kromosom-markörer som ej klassificeras annorstädes"},
    {name: "Q96 Turners syndrom"},
    {name: "Q97 Andra könskromosomavvikelser, kvinnlig fenotyp, som ej klassificeras annorstädes"},
    {name: "Q98 Andra könskromosomavvikelser, manlig fenotyp, som ej klassificeras annorstädes"},
    {name: "Q99 Andra kromosomavvikelser som ej klassificeras annorstädes"}
    ]}
  ]},
  {name: "R00-R99 Symtom, sjukdomstecken och onormala kliniska fynd och laboratoriefynd som ej klassificeras annorstädes", subs: [
    {name: "R00-R09 Symtom och sjukdomstecken från cirkulationsorganen och andningsorganen", subs: [
    {name: "R00 Onormal hjärtrytm"},
    {name: "R01 Blåsljud och andra hjärtljud"},
    {name: "R02 Gangrän som ej klassificeras annorstädes"},
    {name: "R03 Onormalt fynd vid blodtrycksmätning utan diagnos"},
    {name: "R04 Blödning från luftvägarna"},
    {name: "R05 Hosta"},
    {name: "R06 Onormal andning"},
    {name: "R07 Smärtor i luftstrupe och bröstkorg"},
    {name: "R09 Andra symtom och sjukdomstecken från cirkulations- och andningsorganen"}
    ]},
    {name: "R10-R19 Symtom och sjukdomstecken från matsmältningsorganen och buken", subs: [
    {name: "R10 Smärtor från buk och bäcken"},
    {name: "R11 Illamående och kräkningar"},
    {name: "R12 Halsbränna"},
    {name: "R13 Sväljningssvårigheter"},
    {name: "R14 Flatulens och besläktade tillstånd"},
    {name: "R15 Fecesinkontinens"},
    {name: "R16 Förstorad lever och förstorad mjälte som ej klassificeras annorstädes"},
    {name: "R17 Icke specificerad gulsot"},
    {name: "R18 Ascites (vätska i buken)"},
    {name: "R19 Andra symtom och sjukdomstecken från matsmältningsorganen och buken"}
    ]},
    {name: "R20-R23 Symtom och sjukdomstecken från huden och underhuden", subs: [
    {name: "R20 Störningar i hudkänseln"},
    {name: "R21 Icke specificerade hudutslag"},
    {name: "R22 Lokaliserad svullnad, knöl och resistens i huden och underhudsvävnaden"},
    {name: "R23 Andra förändringar i huden"}
    ]},
    {name: "R25-R29 Symtom och sjukdomstecken från nervsystemet och muskuloskeletala systemet", subs: [
    {name: "R25 Abnorma ofrivilliga rörelser"},
    {name: "R26 Gångrubbningar och rörelserubbningar"},
    {name: "R27 Annan koordinationsrubbning"},
    {name: "R29 Andra symtom och sjukdomstecken från nervsystemet och muskuloskeletala systemet"}
    ]},
    {name: "R30-R39 Symtom och sjukdomstecken från urinorganen", subs: [
    {name: "R30 Smärta vid vattenkastning"},
    {name: "R31 Icke specificerad hematuri (blod i urinen)"},
    {name: "R32 Icke specificerad urininkontinens"},
    {name: "R33 Urinretention (urinstämma)"},
    {name: "R34 Anuri och oliguri (ingen urin eller liten urinmängd)"},
    {name: "R35 Polyuri (stor mängd urin)"},
    {name: "R36 Flytning från uretra (urinröret)"},
    {name: "R39 Andra symtom och sjukdomstecken från urinorganen"}
    ]},
    {name: "R40-R46 Symtom och sjukdomstecken avseende intellektuella funktioner, uppfattningsförmåga, känsloläge och beteende", subs: [
    {name: "R40 Somnolens, stupor och koma"},
    {name: "R41 Andra symtom och sjukdomstecken som engagerar uppfattningsförmåga och varseblivning"},
    {name: "R42 Yrsel och svindel"},
    {name: "R43 Rubbningar av lukt och smak"},
    {name: "R44 Andra symtom och sjukdomstecken avseende förnimmelser och varseblivning"},
    {name: "R45 Symtom och sjukdomstecken som avser känsloläget"},
    {name: "R46 Symtom och sjukdomstecken avseende utseende och uppträdande"}
    ]},
    {name: "R47-R49 Symtom och sjukdomstecken avseende talet och rösten", subs: [
    {name: "R47 Talstörningar som ej klassificeras annorstädes"},
    {name: "R48 Dyslexi (lässvårigheter) och andra symboldysfunktioner som ej klassificeras annorstädes"},
    {name: "R49 Röststörningar"}
    ]},
    {name: "R50-R69 Allmänna symtom och sjukdomstecken", subs: [
    {name: "R50 Feber av annan och okänd orsak"},
    {name: "R51 Huvudvärk"},
    {name: "R52 Smärta och värk som ej klassificeras annorstädes"},
    {name: "R53 Sjukdomskänsla och trötthet"},
    {name: "R54 Senilitet"},
    {name: "R55 Svimning och kollaps"},
    {name: "R56 Kramper som ej klassificeras annorstädes"},
    {name: "R57 Chock som ej klassificeras annorstädes"},
    {name: "R58 Blödning som ej klassificeras annorstädes"},
    {name: "R59 Lymfkörtelförstoring"},
    {name: "R60 Ödem som ej klassificeras annorstädes"},
    {name: "R61 Hyperhidros (överdriven svettning)"},
    {name: "R62 Utebliven förväntad normal kroppslig utveckling"},
    {name: "R63 Symtom och sjukdomstecken som har samband med födo- och vätskeintag"},
    {name: "R64 Utmärgling"},
    {name: "R65 Systemiskt inflammatoriskt svarssyndrom"},
    {name: "R68 Andra allmänna symtom och sjukdomstecken"},
    {name: "R69 Okända och icke specificerade orsaker till sjuklighet"}
    ]},
    {name: "R70-R79 Onormala fynd vid blodundersökning utan diagnos", subs: [
    {name: "R70 Förhöjd sänkningsreaktion och onormal plasmaviskositet"},
    {name: "R71 Onormala röda blodkroppar"},
    {name: "R72 Onormala vita blodkroppar som ej klassificeras annorstädes"},
    {name: "R73 Förhöjd glukoshalt i blodet"},
    {name: "R74 Onormala nivåer av serumenzymer"},
    {name: "R75 Positiv HIV-serologi utan säker infektion med humant immunbristvirus [HIV]"},
    {name: "R76 Andra onormala immunologiska fynd i serum"},
    {name: "R77 Andra onormala plasmaproteinfynd"},
    {name: "R78 Fynd av droger och andra substanser som normalt ej finns i blodet"},
    {name: "R79 Annan onormal blodkemi"}
    ]},
    {name: "R80-R82 Onormala fynd vid urinundersökning utan diagnos", subs: [
    {name: "R80 Isolerad proteinuri (äggvita i urinen)"},
    {name: "R81 Glukosuri (socker i urinen)"},
    {name: "R82 Andra onormala fynd vid urinundersökning"}
    ]},
    {name: "R83-R89 Onormala fynd vid undersökning av andra kroppsvätskor, substanser och vävnader, utan diagnos", subs: [
    {name: "R83 Onormala fynd i cerebrospinalvätskan"},
    {name: "R84 Onormala fynd i prov från andningsorganen och från bröstkorgen"},
    {name: "R85 Onormala fynd i prov från matsmältningsorganen och bukhålan"},
    {name: "R86 Onormala fynd i prov från de manliga könsorganen"},
    {name: "R87 Onormala fynd i prov från de kvinnliga könsorganen"},
    {name: "R89 Onormala fynd i prov från andra organ, organsystem och vävnader"}
    ]},
    {name: "R90-R94 Onormala fynd vid radiologisk diagnostik och vid funktionsundersökning utan diagnos", subs: [
    {name: "R90 Onormala fynd vid radiologisk diagnostik avseende centrala nervsystemet"},
    {name: "R91 Onormala fynd vid radiologisk diagnostik avseende lunga"},
    {name: "R92 Onormala fynd vid radiologisk diagnostik avseende bröstkörtel"},
    {name: "R93 Onormala fynd vid radiologisk diagnostik avseende andra kroppsstrukturer"},
    {name: "R94 Onormala resultat av funktionsundersökningar"}
    ]},
    {name: "R95-R99 Ofullständigt definierade och okända orsaker till död", subs: [
    {name: "R95 Plötslig spädbarnsdöd"},
    {name: "R96 Annan plötslig död av okänd orsak"},
    {name: "R98 Obevittnad död"},
    {name: "R99 Andra ofullständigt definierade och icke specificerade orsaker till död"}
    ]}
  ]},
  {name: "S00-T98 Skador, förgiftningar och vissa andra följder av yttre orsaker", subs: [
    {name: "S00-S09 Skador på huvudet", subs: [
    {name: "S00 Ytlig skada på huvudet"},
    {name: "S01 Sårskada på huvudet"},
    {name: "S02 Skallfraktur och fraktur på ansiktsben"},
    {name: "S03 Luxation och distorsion i huvudets leder och ligament"},
    {name: "S04 Skada på kranialnerver"},
    {name: "S05 Skada på ögat och ögonhålan"},
    {name: "S06 Intrakraniell skada"},
    {name: "S07 Klämskada på huvudet"},
    {name: "S08 Traumatisk amputation av del av huvudet"},
    {name: "S09 Andra och icke specificerade skador på huvudet"}
    ]},
    {name: "S10-S19 Skador på halsen", subs: [
    {name: "S10 Ytlig skada på halsen"},
    {name: "S11 Sårskada på halsen"},
    {name: "S12 Fraktur på halskotpelaren och halsens ben"},
    {name: "S13 Luxation och distorsion av leder och ligament i hals och halskotpelare"},
    {name: "S14 Skada på nerver och på ryggmärgen i halsregionen"},
    {name: "S15 Skada på blodkärl i halsregionen"},
    {name: "S16 Skada på muskel och sena i halsregionen"},
    {name: "S17 Klämskada på halsen"},
    {name: "S18 Traumatisk amputation på halsnivå"},
    {name: "S19 Andra och icke specificerade skador i halsregionen"}
    ]},
    {name: "S20-S29 Skador i bröstregionen", subs: [
    {name: "S20 Ytlig skada i bröstregionen"},
    {name: "S21 Sårskada på bröstregionen"},
    {name: "S22 Fraktur på revben, bröstbenet och bröstkotpelaren"},
    {name: "S23 Luxation och distorsion av bröstkorgens leder och ligament"},
    {name: "S24 Skada på nerver och ryggmärg i bröstregionen"},
    {name: "S25 Skada på blodkärl i bröstkorgen"},
    {name: "S26 Skada på hjärtat"},
    {name: "S27 Skada på andra och icke specificerade organ i brösthålan"},
    {name: "S28 Klämskada på bröstkorgen och traumatisk amputation av del av bröstkorgen"},
    {name: "S29 Andra och icke specificerade skador på bröstkorgen"}
    ]},
    {name: "S30-S39 Skador i buken, nedre delen av ryggen, ländkotpelaren och bäckenet", subs: [
    {name: "S30 Ytlig skada på buken, nedre delen av ryggen och bäckenet"},
    {name: "S31 Sårskada på buken, nedre delen av ryggen och bäckenet"},
    {name: "S32 Fraktur på ländkotpelaren och bäckenet"},
    {name: "S33 Luxation och distorsion i leder och ligament i lumbalkotpelaren och bäckenet"},
    {name: "S34 Skada på nerver och lumbala delen av ryggmärgen på buknivå, bäckennivå och nedre delen av ryggen"},
    {name: "S35 Skada på blodkärl på buknivå, bäckennivå och nedre delen av ryggen"},
    {name: "S36 Skada på organ i bukhålan"},
    {name: "S37 Skada på urin- och bäckenorganen"},
    {name: "S38 Klämskador och traumatisk amputation av del av buken, nedre delen av ryggen och bäckenet"},
    {name: "S39 Andra och icke specificerade skador på buken, nedre delen av ryggen och bäckenet"}
    ]},
    {name: "S40-S49 Skador på skuldra och överarm", subs: [
    {name: "S40 Ytlig skada på skuldra och överarm"},
    {name: "S41 Sårskada på skuldra och överarm"},
    {name: "S42 Fraktur på skuldra och överarm"},
    {name: "S43 Luxation och distorsion i skuldergördelns leder och ligament"},
    {name: "S44 Skada på nerver på skulder- och överarmsnivå"},
    {name: "S45 Skada på blodkärl på skulder- och överarmsnivå"},
    {name: "S46 Skada på muskel och sena på skulder- och överarmsnivå"},
    {name: "S47 Klämskada på skuldra och överarm"},
    {name: "S48 Traumatisk amputation av skuldra och överarm"},
    {name: "S49 Andra och icke specificerade skador på skuldra och överarm"}
    ]},
    {name: "S50-S59 Skador på armbåge och underarm", subs: [
    {name: "S50 Ytlig skada på underarm"},
    {name: "S51 Sårskada på underarm"},
    {name: "S52 Fraktur på underarm"},
    {name: "S53 Luxation och distorsion i armbågens leder och ligament"},
    {name: "S54 Skada på nerver på underarmsnivå"},
    {name: "S55 Skada på blodkärl på underarmsnivå"},
    {name: "S56 Skada på muskel och sena på underarmsnivå"},
    {name: "S57 Klämskada på underarm"},
    {name: "S58 Traumatisk amputation av underarm"},
    {name: "S59 Andra och icke specificerade skador på underarm"}
    ]},
    {name: "S60-S69 Skador på handled och hand", subs: [
    {name: "S60 Ytlig skada på handled och hand"},
    {name: "S61 Sårskada på handled och hand"},
    {name: "S62 Fraktur på handled och hand"},
    {name: "S63 Luxation och distorsion i leder och ligament på handleds- och handnivå"},
    {name: "S64 Skada på nerver på handleds- och handnivå"},
    {name: "S65 Skada på blodkärl på handleds- och handnivå"},
    {name: "S66 Skada på muskel och sena på handleds- och handnivå"},
    {name: "S67 Klämskada på handled och hand"},
    {name: "S68 Traumatisk amputation av handled och hand"},
    {name: "S69 Andra och icke specificerade skador på handled och hand"}
    ]},
    {name: "S70-S79 Skador på höft och lår", subs: [
    {name: "S70 Ytlig skada på höft och lår"},
    {name: "S71 Sårskada på höft och lår"},
    {name: "S72 Fraktur på lårben"},
    {name: "S73 Luxation och distorsion i höftled och höftligament"},
    {name: "S74 Skada på nerver på höft- och lårnivå"},
    {name: "S75 Skada på blodkärl på höft- och lårnivå"},
    {name: "S76 Skada på muskel och sena på höft- och lårnivå"},
    {name: "S77 Klämskador på höft och lår"},
    {name: "S78 Traumatisk amputation av höft och lår"},
    {name: "S79 Andra och icke specificerade skador på höft och lår"}
    ]},
    {name: "S80-S89 Skador på knä och underben", subs: [
    {name: "S80 Ytlig skada på underben"},
    {name: "S81 Sårskada på underben"},
    {name: "S82 Fraktur på underben inklusive fotled"},
    {name: "S83 Luxation och distorsion i knäets leder och ligament"},
    {name: "S84 Skada på nerver på underbensnivå"},
    {name: "S85 Skada på blodkärl på underbensnivå"},
    {name: "S86 Skada på muskel och sena på underbensnivå"},
    {name: "S87 Klämskada på underben"},
    {name: "S88 Traumatisk amputation av underben"},
    {name: "S89 Andra och icke specificerade skador på underben"}
    ]},
    {name: "S90-S99 Skador på fotled och fot", subs: [
    {name: "S90 Ytlig skada på fotled och fot"},
    {name: "S91 Sårskada på fotled och fot"},
    {name: "S92 Fraktur på fot med undantag för fotled"},
    {name: "S93 Luxation och distorsion i leder och ligament på fotleds- och fotnivå"},
    {name: "S94 Skada på nerver på fotleds- och fotnivå"},
    {name: "S95 Skada på blodkärl på fotleds- och fotnivå"},
    {name: "S96 Skada på muskel och sena på fotleds- och fotnivå"},
    {name: "S97 Klämskada på fotled och fot"},
    {name: "S98 Traumatisk amputation av fotled och fot"},
    {name: "S99 Andra och icke specificerade skador på fotled och fot"}
    ]},
    {name: "T00-T07 Skador som engagerar flera kroppsregioner", subs: [
    {name: "T00 Ytliga skador som engagerar flera kroppsregioner"},
    {name: "T01 Sårskador som engagerar flera kroppsregioner"},
    {name: "T02 Frakturer som engagerar flera kroppsregioner"},
    {name: "T03 Luxationer och distorsioner som engagerar flera kroppsregioner"},
    {name: "T04 Klämskador som engagerar flera kroppsregioner"},
    {name: "T05 Traumatiska amputationer som engagerar flera kroppsregioner"},
    {name: "T06 Andra skador som engagerar flera kroppsregioner som ej klassificeras annorstädes"},
    {name: "T07 Icke specificerade multipla skador"}
    ]},
    {name: "T08-T14 Skador på icke specificerad del av bålen, extremitet eller annan kroppsregion", subs: [
    {name: "T08 Fraktur på ryggraden på icke specificerad nivå"},
    {name: "T09 Andra skador på ryggraden och bålen på icke specificerad nivå"},
    {name: "T10 Fraktur på övre extremitet på icke specificerad nivå"},
    {name: "T11 Andra skador på övre extremitet på icke specificerad nivå"},
    {name: "T12 Fraktur på nedre extremiteter på icke specificerad nivå"},
    {name: "T13 Andra skador på nedre extremitet på icke specificerad nivå"},
    {name: "T14 Skada på icke specificerad kroppsregion"}
    ]},
    {name: "T15-T19 Effekter av främmande kropp som trängt in genom naturlig öppning", subs: [
    {name: "T15 Främmande kropp i ögats yttre delar"},
    {name: "T16 Främmande kropp i örat"},
    {name: "T17 Främmande kropp i andningsvägarna"},
    {name: "T18 Främmande kropp i matsmältningskanalen"},
    {name: "T19 Främmande kropp i urinorganen och könsorganen"}
    ]},
    {name: "T20-T25 Brännskador och frätskador på yttre kroppsyta med specificerad lokalisation", subs: [
    {name: "T20 Brännskada och frätskada på huvudet och halsen"},
    {name: "T21 Brännskada och frätskada på bålen"},
    {name: "T22 Brännskada och frätskada på skuldra och övre extremitet utom handled och hand"},
    {name: "T23 Brännskada och frätskada på handled och hand"},
    {name: "T24 Brännskada och frätskada på höft och nedre extremitet utom fotled och fot"},
    {name: "T25 Brännskada och frätskada på fotled och fot"}
    ]},
    {name: "T26-T28 Brännskador och frätskador begränsade till ögat och inre organ", subs: [
    {name: "T26 Brännskada och frätskada begränsad till ögat och närliggande organ"},
    {name: "T27 Brännskada och frätskada i andningsvägarna"},
    {name: "T28 Brännskada och frätskada i andra inre organ"}
    ]},
    {name: "T29-T32 Brännskador och frätskador på multipla och icke specificerade kroppsregioner", subs: [
    {name: "T29 Brännskador och frätskador på flera kroppsregioner"},
    {name: "T30 Brännskada och frätskada på icke specificerad kroppsregion"},
    {name: "T31 Brännskador klassificerade med hänsyn till storleken på den kroppsyta som engagerats"},
    {name: "T32 Frätskador klassificerade med hänsyn till storleken på den kroppsyta som engagerats"}
    ]},
    {name: "T33-T35 Köldskada", subs: [
    {name: "T33 Ytlig köldskada"},
    {name: "T34 Köldskada med vävnadsnekros"},
    {name: "T35 Köldskada som engagerar flera kroppsregioner och icke specificerad köldskada"}
    ]},
    {name: "T36-T50 Förgiftning av droger, läkemedel och biologiska substanser", subs: [
    {name: "T36 Förgiftning med systemiska antibiotika"},
    {name: "T37 Förgiftning med andra läkemedel för systemiskt bruk mot infektionssjukdomar och parasitsjukdomar"},
    {name: "T38 Förgiftning med hormoner och deras syntetiska substitut och antagonister som ej klassificeras annorstädes"},
    {name: "T39 Förgiftning med smärtstillande läkemedel av icke opiatkaraktär, febernedsättande läkemedel samt antiinflammatoriska och antireumatiska läkemedel"},
    {name: "T40 Förgiftning med narkotiska och psykodysleptiska medel [hallucinogener]"},
    {name: "T41 Förgiftning med anestetika och gaser för terapeutiskt bruk"},
    {name: "T42 Förgiftning med antiepileptika och medel vid parkinsonism samt lugnande medel och sömnmedel"},
    {name: "T43 Förgiftning med psykotropa medel som ej klassificeras annorstädes"},
    {name: "T44 Förgiftning med läkemedel som företrädesvis påverkar det autonoma nervsystemet"},
    {name: "T45 Förgiftning med läkemedel med företrädesvis systemisk och hematologisk effekt som ej klassificeras annorstädes"},
    {name: "T46 Förgiftning med läkemedel som företrädesvis påverkar hjärt-kärlsystemet"},
    {name: "T47 Förgiftning med läkemedel som företrädesvis påverkar mag-tarmkanalen"},
    {name: "T48 Förgiftning med läkemedel som företrädesvis påverkar glatt muskulatur, skelettmuskulatur och andningsorganen"},
    {name: "T49 Förgiftningar med medel företrädesvis använda för lokalterapi vid hud- och slemhinnesjukdomar, vid ögon-, öron-, näs-, hals-, och tandsjukdomar"},
    {name: "T50 Förgiftning med diuretika samt andra och icke specificerade droger, läkemedel och biologiska substanser"}
    ]},
    {name: "T51-T65 Toxisk effekt av substanser med i huvudsak icke-medicinsk användning", subs: [
    {name: "T51 Toxisk effekt av alkohol"},
    {name: "T52 Toxisk effekt av organiska lösningsmedel"},
    {name: "T53 Toxisk effekt av halogenderivat av alifatiska och aromatiska kolväten"},
    {name: "T54 Toxisk effekt av frätande substanser"},
    {name: "T55 Toxisk effekt av såpor och rengöringsmedel"},
    {name: "T56 Toxisk effekt av metaller"},
    {name: "T57 Toxisk effekt av andra oorganiska substanser"},
    {name: "T58 Toxisk effekt av kolmonoxid"},
    {name: "T59 Toxisk effekt av andra gaser, rök och ångor"},
    {name: "T60 Toxisk effekt av pesticider"},
    {name: "T61 Toxisk effekt av giftiga substanser i fisk och skaldjur"},
    {name: "T62 Toxisk effekt av andra giftiga substanser i födoämnen"},
    {name: "T63 Toxisk effekt av kontakt med giftiga djur"},
    {name: "T64 Toxisk effekt av aflatoxin och andra mykotoxinföroreningar i föda"},
    {name: "T65 Toxisk effekt av andra och icke specificerade substanser"}
    ]},
    {name: "T66-T78 Andra och icke specificerade effekter av yttre orsaker", subs: [
    {name: "T66 Icke specificerade effekter av strålning"},
    {name: "T67 Effekter av värme och ljus"},
    {name: "T68 Hypotermi"},
    {name: "T69 Andra effekter av nedsatt temperatur"},
    {name: "T70 Effekter av lufttryck och vattentryck"},
    {name: "T71 Kvävning"},
    {name: "T73 Effekter av annan bristsituation"},
    {name: "T74 Misshandelssyndrom"},
    {name: "T75 Effekter av andra yttre orsaker"},
    {name: "T78 Ogynnsamma effekter som ej klassificeras annorstädes"}
    ]},
    {name: "T79-T79 Vissa tidiga komplikationer till skada genom yttre våld", subs: [
    {name: "T79 Vissa tidiga komplikationer till skada genom yttre våld som ej klassificeras annorstädes"}
    ]},
    {name: "T80-T88 Komplikationer till kirurgiska åtgärder och medicinsk vård som ej klassificeras annorstädes", subs: [
    {name: "T80 Komplikationer efter infusion, transfusion och injektion i behandlingssyfte"},
    {name: "T81 Komplikationer till kirurgiska och medicinska ingrepp som ej klassificeras annorstädes"},
    {name: "T82 Komplikationer orsakade av kardiella och vaskulära proteser, implantat och transplantat"},
    {name: "T83 Komplikationer av proteser, implantat och transplantat i urinorganen och könsorganen"},
    {name: "T84 Komplikationer av inre ortopediska proteser, implantat och transplantat"},
    {name: "T85 Komplikationer av andra inre proteser, implantat och transplantat"},
    {name: "T86 Funktionssvikt och avstötning av transplanterade organ och vävnader"},
    {name: "T87 Komplikationer specifika för replantation (återfastsättning) och amputation"},
    {name: "T88 Andra komplikationer och ogynnsamma effekter av kirurgiska åtgärder och medicinsk vård som ej klassificeras annorstädes"}
    ]},
    {name: "T90-T98 Sena besvär av skador, förgiftningar och andra följder av yttre orsaker", subs: [
    {name: "T90 Sena besvär av skador på huvudet"},
    {name: "T91 Sena besvär av skada på hals och bål"},
    {name: "T92 Sena besvär av skador på övre extremitet"},
    {name: "T93 Sena besvär av skador på nedre extremitet"},
    {name: "T94 Sena besvär av skador som engagerar flera och icke specificerade kroppsregioner"},
    {name: "T95 Sena besvär av brännskador, frätskador och köldskador"},
    {name: "T96 Sena besvär av förgiftning orsakad av droger, läkemedel och biologiska substanser"},
    {name: "T97 Sena besvär av toxiska effekter av substanser med i huvudsak icke medicinsk användning"},
    {name: "T98 Sena besvär av andra och icke specificerade effekter av yttre orsaker"}
    ]}
  ]},
  {name: "U00-U99 Koder för särskilda ändamål", subs: [
    {name: "U00-U49 Interimistiska koder för nya sjukdomar med osäker etiologi eller koder som kan tas i bruk med kort varsel", subs: [
    {name: "U04 Svår akut respiratorisk sjukdom [SARS]"},
    {name: "U06 Kod som kan tas i bruk med kort varsel"},
    {name: "U07 Kod som kan tas i bruk med kort varsel"}
    ]},
    {name: "U82-U85 Resistens mot antimikrobiella och antineoplastiska läkemedel (U82-U85)", subs: [
    {name: "U82 Resistens mot betalaktamantibiotika"},
    {name: "U83 Resistens mot andra antibiotika"},
    {name: "U84 Resistens mot andra antimikrobiella läkemedel"},
    {name: "U85 Resistens mot antineoplastiska läkemedel"}
    ]},
    {name: "U98-U99 Koder för särskilda nationella behov", subs: [
    {name: "U98 Tilläggskoder för hjärtinfarkt"},
    {name: "U99 Diagnosinformation saknas"}
    ]}
  ]},
  {name: "V01-Y98 Yttre orsaker till sjukdom och död", subs: [
    {name: "W00-W19 Fallolyckor", subs: [
    {name: "W00 Fall i samma plan i samband med is och snö"},
    {name: "V01 Fotgängare skadad i kollision med cykel"},
    {name: "W01 Fall i samma plan genom halkning, snavning eller snubbling"},
    {name: "V02 Fotgängare skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "W02 Fall i samband med användning av skridskor, skidor, rullskridskor, skateboard (rullbräda) eller snowboard"},
    {name: "V03 Fotgängare skadad i kollision med personbil eller lätt lastbil"},
    {name: "W03 Annat fall i samma plan genom kollision med eller knuff av annan person"},
    {name: "V04 Fotgängare skadad i kollision med tung lastbil eller buss"},
    {name: "W04 Fall när man blir buren eller får stöd av andra personer"},
    {name: "V05 Fotgängare skadad i kollision med tåg"},
    {name: "W05 Fall från rullstol"},
    {name: "V06 Fotgängare skadad i kollision med annat icke motordrivet fordon"},
    {name: "W06 Fall från säng"},
    {name: "W07 Fall från stol"},
    {name: "W08 Fall från andra möbler"},
    {name: "V09 Fotgängare skadad i transportolycka"},
    {name: "W09 Fall från lekredskap på lekplats"},
    {name: "V10 Cyklist skadad i kollision med fotgängare eller djur"},
    {name: "W10 Fall i och från trappa och trappsteg"},
    {name: "V11 Cyklist skadad i kollision med annan cykel"},
    {name: "W11 Fall på och från stege"},
    {name: "V12 Cyklist skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "W12 Fall på och från byggnadsställning"},
    {name: "V13 Cyklist skadad i kollision med personbil eller lätt lastbil"},
    {name: "W13 Fall ut ur, från eller genom byggnad eller byggnadskonstruktion"},
    {name: "V14 Cyklist skadad i kollision med tung lastbil eller buss"},
    {name: "W14 Fall från träd"},
    {name: "V15 Cyklist skadad i kollision med tåg"},
    {name: "W15 Fall från stup"},
    {name: "V16 Cyklist skadad i kollision med annat icke motordrivet fordon"},
    {name: "W16 Dykning eller hopp i vatten med annan skada än drunkning eller drunkningstillbud"},
    {name: "V17 Cyklist skadad i kollision med fast eller stillastående föremål"},
    {name: "W17 Annat fall från ett plan till ett annat"},
    {name: "V18 Cyklist i transportolycka, ej kollision"},
    {name: "W18 Annat fall i samma plan"},
    {name: "V19 Cyklist skadad i transportolycka"},
    {name: "W19 Fall, ospecificerat"},
    {name: "V20 Motorcyklist skadad i kollision med fotgängare eller djur"},
    {name: "V21 Motorcyklist skadad i kollision med cykel"},
    {name: "V22 Motorcyklist skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "V23 Motorcyklist skadad i kollision med personbil eller lätt lastbil"},
    {name: "V24 Motorcyklist skadad i kollision med tung lastbil eller buss"},
    {name: "V25 Motorcyklist skadad i kollision med tåg"},
    {name: "V26 Motorcyklist skadad i kollision med annat ej motordrivet fordon"},
    {name: "V27 Motorcyklist skadad i kollision med fast eller stillastående föremål"},
    {name: "V28 Motorcyklist skadad i transportolycka, ej kollision"},
    {name: "V29 Motorcyklist skadad i transportolycka"},
    {name: "V30 Förare av eller passagerare i trehjuligt motorfordon i kollision med fotgängare eller djur"},
    {name: "V31 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med cykel"},
    {name: "V32 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med annat två- eller trehjuligt motorfordon"},
    {name: "V33 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med personbil eller lätt lastbil"},
    {name: "V34 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med tung lastbil eller buss"},
    {name: "V35 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med tåg"},
    {name: "V36 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med annat icke motordrivet fordon"},
    {name: "V37 Förare av eller passagerare i trehjuligt motorfordon skadad i kollision med fast eller stillastående föremål"},
    {name: "V38 Förare av eller passagerare i trehjuligt motorfordon skadad i transportolycka, ej kollision"},
    {name: "V39 Förare av eller passagerare i trehjuligt motorfordon skadad i transportolycka"},
    {name: "V40 Förare av eller passagerare i personbil skadad i kollision med fotgängare eller djur"},
    {name: "V41 Förare av eller passagerare i personbil skadad i kollision med cykel,"},
    {name: "V42 Förare av eller passagerare i personbil skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "V43 Förare av eller passagerare i personbil skadad i kollision med personbil eller lätt lastbil"},
    {name: "V44 Förare av eller passagerare i personbil skadad i kollision med tung lastbil eller buss"},
    {name: "V45 Förare av eller passagerare i personbil skadad i kollision med tåg"},
    {name: "V46 Förare av eller passagerare i personbil skadad i kollision med annat icke motordrivet fordon"},
    {name: "V47 Förare av eller passagerare i personbil skadad i kollision med fast eller stillastående föremål"},
    {name: "V48 Förare av eller passagerare i personbil skadad i transportolycka, ej kollision"},
    {name: "V49 Förare av eller passagerare i personbil skadad i transportolycka"},
    {name: "V50 Förare av eller passagerare i lätt lastbil skadad i kollision med fotgängare eller djur"},
    {name: "V51 Förare av eller passagerare i lätt lastbil skadad i kollision med cykel"},
    {name: "V52 Förare av eller passagerare i lätt lastbil skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "V53 Förare av eller passagerare i lätt lastbil skadad i kollision med personbil eller lätt lastbil"},
    {name: "V54 Förare av eller passagerare i lätt lastbil skadad i kollision med tung lastbil eller buss"},
    {name: "V55 Förare av eller passagerare i lätt lastbil skadad i kollision med tåg"},
    {name: "V56 Förare av eller passagerare i lätt lastbil skadad i kollision med annat icke motordrivet fordon"},
    {name: "V57 Förare av eller passagerare i lätt lastbil skadad i kollision med fast eller stillastående föremål"},
    {name: "V58 Förare av eller passagerare i lätt lastbil skadad i transportolycka, ej kollision"},
    {name: "V59 Förare av eller passagerare i lätt lastbil skadad i transportolycka"},
    {name: "V60 Förare av eller passagerare i tung lastbil skadad i kollision med fotgängare eller djur"},
    {name: "V61 Förare av eller passagerare i tung lastbil skadad i kollision med cykel"},
    {name: "V62 Förare av eller passagerare i tung lastbil skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "V63 Förare av eller passagerare i tung lastbil skadad i kollision med personbil eller lätt lastbil"},
    {name: "V64 Förare av eller passagerare i tung lastbil skadad i kollision med tung lastbil eller buss"},
    {name: "V65 Förare av eller passagerare i tung lastbil skadad i kollision med tåg"},
    {name: "V66 Förare av eller passagerare i tung lastbil skadad i kollision med annat icke motordrivet fordon"},
    {name: "V67 Förare av eller passagerare i tung lastbil skadad i kollision med fast eller stillastående föremål"},
    {name: "V68 Förare av eller passagerare i tung lastbil skadad i transportolycka, ej kollision"},
    {name: "V69 Förare av eller passagerare i tung lastbil skadad i transportolycka"},
    {name: "V70 Förare av eller passagerare i buss skadad i kollision med fotgängare eller djur"},
    {name: "V71 Förare av eller passagerare i buss skadad i kollision med cykel"},
    {name: "V72 Förare av eller passagerare i buss skadad i kollision med två- eller trehjuligt motorfordon"},
    {name: "V73 Förare av eller passagerare i buss skadad i kollison med personbil eller lätt lastbil"},
    {name: "V74 Förare av eller passagerare i buss skadad i kollision med tung lastbil eller buss"},
    {name: "V75 Förare av eller passagerare i buss skadad i kollision med tåg"},
    {name: "V76 Förare av eller passagerare i buss skadad i kollision med annat icke motordrivet fordon"},
    {name: "V77 Förare av eller passagerare i buss skadad i kollision med fast eller stillastående föremål"},
    {name: "V78 Förare av eller passagerare i buss skadad i transportolycka, ej kollision"},
    {name: "V79 Förare av eller passagerare i buss skadad i transportolycka"},
    {name: "V80 Ryttare eller person i åkdon draget av djur skadad i transportolycka"},
    {name: "V81 Tågförare eller tågpassagerare skadad i transportolycka"},
    {name: "V82 Spårvagnsförare eller spårvagnspassagerare skadad i transportolycka"},
    {name: "V83 Förare av eller passagerare i eller på industrifordon skadad i transportolycka"},
    {name: "V84 Förare eller passagerare i eller på jordbruksfordon skadad i transportolycka"},
    {name: "V85 Förare eller passagerare i eller på specialfordon skadad i transportolycka"},
    {name: "V86 Förare eller passagerare i terrängfordon skadad i transportolycka"},
    {name: "V87 Trafikolycka där olyckstypen är specificerad men där olycksoffrets transportsätt är okänt"},
    {name: "V88 Olycka, ej trafik, där olyckstypen är specificerad men där olycksoffrets transportsätt är okänt"},
    {name: "V89 Olycka med motorfordon eller ej motordrivet fordon, fordonstyp ej specificerad"},
    {name: "V90 Olycka med vattenfarkost som orsak till drunkning och drunkningstillbud"},
    {name: "V91 Olycka med vattenfarkost som leder till annan skada"},
    {name: "V92 Drunkning och drunkningstillbud i samband med transport på vatten utan att vattenfarkost är direkt engagerad i olyckan"},
    {name: "V93 Olycka ombord som lett till annan skada än drunkning och drunkningstillbud utan att olyckan kan karakteriseras som olycka med vattenfarkost"},
    {name: "V94 Andra och icke specificerade transportolyckor på vatten"},
    {name: "V95 Olycka med motordriven luftfarkost med skada på ombordvarande"},
    {name: "V96 Olycka med ej motordriven luftfarkost med skada på ombordvarande"},
    {name: "V97 Andra specificerade olyckor vid lufttransport"},
    {name: "V99 Transportolycka, ospecificerad"}
    ]},
    {name: "W20-W49 Exponering för icke levande mekaniska krafter", subs: [
    {name: "W20 Träffad av kastat eller fallande föremål"},
    {name: "W21 Slagit sig mot eller träffad av sportredskap"},
    {name: "W22 Slagit sig mot eller träffad av andra föremål"},
    {name: "W23 Fångad av, klämd eller pressad i eller mellan föremål"},
    {name: "W24 Kontakt med utrustning för kraftöverföring och lyft som ej klassificeras annorstädes"},
    {name: "W25 Kontakt med vasst glasföremål"},
    {name: "W26 Kontakt med dolk, kniv eller svärd"},
    {name: "W27 Kontakt med ej motordrivet handverktyg"},
    {name: "W28 Kontakt med motordriven gräsklippare"},
    {name: "W29 Kontakt med annat motordrivet handverktyg och hushållsmaskin"},
    {name: "W30 Kontakt med jordbruksmaskin"},
    {name: "W31 Kontakt med annan och icke specificerad maskin"},
    {name: "W32 Skott från pistol och revolver"},
    {name: "W33 Skott från gevär, hagelbössa och tyngre skjutvapen"},
    {name: "W34 Skott från andra och icke specificerade skjutvapen"},
    {name: "W35 Explosion i ångpanna"},
    {name: "W36 Explosion av gascylinder"},
    {name: "W37 Explosion av däck, rör eller slang (under övertryck)"},
    {name: "W38 Explosion av andra specificerade föremål under övertryck"},
    {name: "W39 Explosion av fyrverkeripjäs"},
    {name: "W40 Explosion av andra sprängämnen"},
    {name: "W41 Exponering för högtrycksstråle"},
    {name: "W42 Exponering för buller"},
    {name: "W43 Exponering för vibrationer"},
    {name: "W44 Främmande kropp som trängt in i eller genom öga eller naturlig kroppsöppning"},
    {name: "W45 Främmande kropp som trängt in genom huden"},
    {name: "W46 Kontakt med injektionsnål"},
    {name: "W49 Exponerad för andra och icke specificerade icke levande mekaniska krafter"}
    ]},
    {name: "W50-W64 Exponering för levande mekaniska krafter", subs: [
    {name: "W50 Slagen, sparkad, biten eller riven av annan person"},
    {name: "W51 Slagit sig mot eller törnat emot annan person"},
    {name: "W52 Klämd, knuffad eller nedtrampad av folkmassa"},
    {name: "W53 Biten av råtta"},
    {name: "W54 Biten eller angripen av hund"},
    {name: "W55 Biten eller angripen av annat däggdjur"},
    {name: "W56 Biten eller angripen av vattendjur"},
    {name: "W57 Bett och stick av icke giftig insekt och andra icke giftiga leddjur"},
    {name: "W58 Biten eller angripen av krokodil eller alligator"},
    {name: "W59 Biten eller angripen av andra kräldjur"},
    {name: "W60 Kontakt med törnen och taggar på växter samt vassa blad"},
    {name: "W64 Exponering för andra och icke specificerade levande mekaniska krafter"}
    ]},
    {name: "W65-W74 Drunkning och drunkningstillbud genom olyckshändelse", subs: [
    {name: "W65 Drunkning och drunkningstillbud i badkar"},
    {name: "W66 Drunkning och drunkningstillbud efter fall ned i badkar"},
    {name: "W67 Drunkning och drunkningstillbud i simbassäng"},
    {name: "W68 Drunkning och drunkningstillbud efter fall ned i simbassäng"},
    {name: "W69 Drunkning och drunkningstillbud i hav, sjö och vattendrag"},
    {name: "W70 Drunkning och drunkningstillbud efter fall i hav, sjö och vattendrag"},
    {name: "W73 Annan specificerad drunkning och annat specificerat drunkningstillbud"},
    {name: "W74 Drunkning och drunkningstillbud, ospecificerat"}
    ]},
    {name: "W75-W84 Annan kvävning och annat kvävningstillbud genom olyckshändelse", subs: [
    {name: "W75 Kvävning, kvävningstillbud och strypning i sängen"},
    {name: "W76 Annan strypning och hängning genom olyckshändelse"},
    {name: "W77 Kvävning och kvävningstillbud av jord- eller sandmassor"},
    {name: "W78 Inhalation av maginnehåll"},
    {name: "W79 Inhalation och nedsväljning av föda som orsakat andningshinder"},
    {name: "W80 Inhalation och nedsväljning av andra föremål eller ämnen som orsakat andningshinder"},
    {name: "W81 Instängd eller fångad i utrymme med låg syrgashalt"},
    {name: "W83 Annan specificerad kvävning och annat specificerat kvävningstillbud"},
    {name: "W84 Kvävning och kvävningstillbud, ospecificerat"}
    ]},
    {name: "W85-W99 Exponering för elektrisk ström, strålning, extrem lufttemperatur och extremt lufttryck i omgivningen", subs: [
    {name: "W85 Exponering för kraftledningar"},
    {name: "W86 Exponering för annan specificerad elektrisk ström"},
    {name: "W87 Exponering för icke specificerad elektrisk ström"},
    {name: "W88 Exponering för joniserande strålning"},
    {name: "W89 Exponering för av människa framställt synligt och ultraviolett ljus"},
    {name: "W90 Exponering för annan icke joniserande strålning"},
    {name: "W91 Exponering för icke specificerad strålning"},
    {name: "W92 Exponering för av människa framställd extrem hetta"},
    {name: "W93 Exponering för av människa framställd extrem kyla"},
    {name: "W94 Exponering för högt och lågt lufttryck och för ändringar i lufttryck"},
    {name: "W99 Exponering för andra och icke specificerade, av människa orsakade miljöfaktorer"}
    ]},
    {name: "X00-X09 Exponering för rök och öppen eld", subs: [
    {name: "X00 Exponering för okontrollerad eld i byggnad eller byggnadskonstruktion"},
    {name: "X01 Exponering för icke kontrollerad eld med undantag för eld i byggnad eller byggnadskonstruktion"},
    {name: "X02 Exponering för kontrollerad eld i byggnad och byggnadskonstruktion"},
    {name: "X03 Exponering för kontrollerad eld, med undantag för eld i byggnad eller byggnadskonstruktion"},
    {name: "X04 Exponering för plötslig antändning av mycket eldfängt material eller ämne"},
    {name: "X05 Antändning av nattdräkter"},
    {name: "X06 Antändning av andra klädesplagg"},
    {name: "X08 Exponering för annan specificerad rök och öppen eld"},
    {name: "X09 Exponering för icke specificerad rök och öppen eld"}
    ]},
    {name: "X10-X19 Kontakt med heta föremål och heta ämnen", subs: [
    {name: "X10 Kontakt med het dryck, föda, fett och matolja"},
    {name: "X11 Kontakt med hett kranvatten"},
    {name: "X12 Kontakt med andra heta vätskor"},
    {name: "X13 Kontakt med vattenånga och andra heta ångor"},
    {name: "X14 Kontakt med het luft och heta gaser"},
    {name: "X15 Kontakt med heta hushållsredskap"},
    {name: "X16 Kontakt med utrustning för uppvärmning av byggnad, heta värmeelement och rörledningar"},
    {name: "X17 Kontakt med heta motorer, maskiner och verktyg"},
    {name: "X18 Kontakt med andra heta metallföremål"},
    {name: "X19 Kontakt med andra och icke specificerade heta föremål och heta ämnen"}
    ]},
    {name: "X20-X29 Kontakt med giftiga djur och växter", subs: [
    {name: "X20 Kontakt med giftiga ormar och ödlor"},
    {name: "X21 Kontakt med giftiga spindlar"},
    {name: "X22 Kontakt med skorpioner"},
    {name: "X23 Kontakt med bålgetingar, getingar och bin"},
    {name: "X24 Kontakt med giftiga tusenfotingar (tropiska)"},
    {name: "X25 Kontakt med andra giftiga leddjur"},
    {name: "X26 Kontakt med giftiga vattendjur och vattenväxter"},
    {name: "X27 Kontakt med andra specificerade giftiga djur"},
    {name: "X28 Kontakt med andra specificerade giftiga växter"},
    {name: "X29 Kontakt med giftigt djur eller giftig växt, ospecificerat"}
    ]},
    {name: "X30-X39 Exponering för naturkrafter", subs: [
    {name: "X30 Exponering för extrem naturlig värme"},
    {name: "X31 Exponering för extrem naturlig köld"},
    {name: "X32 Exponering för solljus"},
    {name: "X33 Blixtnedslag"},
    {name: "X34 Jordbävning"},
    {name: "X35 Vulkanutbrott"},
    {name: "X36 Snöskred, lavin, jordskred och andra rörelser i jordytan"},
    {name: "X37 Katastrofartat oväder"},
    {name: "X38 Översvämning"},
    {name: "X39 Exponering för andra och icke specificerade naturkrafter"}
    ]},
    {name: "X40-X49 Förgiftningsolyckor och exponering för skadliga ämnen genom olyckshändelse", subs: [
    {name: "X40 Oavsiktlig förgiftning med och exponering för smärtstillande läkemedel av icke opiatkaraktär, febernedsättande medel och medel mot reumatism"},
    {name: "X41 Oavsiktlig förgiftning med och exponering för antiepileptika, lugnande läkemedel och sömnmedel, medel mot parkinsonism samt psykotropa medel som ej klassificeras annorstädes"},
    {name: "X42 Oavsiktlig förgiftning med och exponering för narkotiska medel och hallucinogener som ej klassificeras annorstädes"},
    {name: "X43 Oavsiktlig förgiftning med och exponering för andra läkemedel som påverkar det autonoma nervsystemet"},
    {name: "X44 Oavsiktlig förgiftning med och exponering för andra och icke specificerade droger, läkemedel och biologiska substanser"},
    {name: "X45 Oavsiktlig förgiftning med och exponering för alkoholer"},
    {name: "X46 Oavsiktlig förgiftning med och exponering för organiska lösningsmedel och halogenerade kolväten och deras ångor"},
    {name: "X47 Oavsiktlig förgiftning med och exponering för andra gaser och ångor"},
    {name: "X48 Oavsiktlig förgiftning med och exponering för pesticider"},
    {name: "X49 Oavsiktlig förgiftning med och exponering för andra och icke specificerade kemiska ämnen och skadliga substanser"}
    ]},
    {name: "X50-X57 Överansträngning och umbäranden", subs: [
    {name: "X50 Överansträngning och påfrestande eller upprepade rörelser"},
    {name: "X51 Resor och förflyttningar med överansträngning och umbäranden"},
    {name: "X52 Utdragen vistelse i tyngdlös miljö"},
    {name: "X53 Brist på föda"},
    {name: "X54 Brist på vatten"},
    {name: "X57 Umbäranden, ospecificerade"}
    ]},
    {name: "X58-X59 Exponering genom olyckshändelse för andra och icke specificerade faktorer", subs: [
    {name: "X58 Exponering för andra specificerade faktorer"},
    {name: "X59 Exponering för icke specificerad faktor"}
    ]},
    {name: "X60-X84 Avsiktligt självdestruktiv handling", subs: [
    {name: "X60 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för smärtstillande läkemedel av icke opiatkaraktär, febernedsättande medel och medel mot reumatism"},
    {name: "X61 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för antiepileptika, lugnande läkemedel och sömnmedel, medel mot parkinsonism samt psykotropa medel som ej klassificeras annorstädes"},
    {name: "X62 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för narkotiska medel och hallucinogener som ej klassificeras annorstädes"},
    {name: "X63 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för andra läkemedel som påverkar det autonoma nervsystemet"},
    {name: "X64 Avsiktligt självdestruktiv handling genom förgiftning med och exponeringen för andra och icke specificerade droger, läkemedel och biologiska substanser"},
    {name: "X65 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för alkoholer"},
    {name: "X66 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för organiska lösningsmedel och halogenerade kolväten och deras ångor"},
    {name: "X67 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för andra gaser och ångor"},
    {name: "X68 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för pesticider"},
    {name: "X69 Avsiktligt självdestruktiv handling genom förgiftning med och exponering för andra och icke specificerade kemiska ämnen och skadliga substanser"},
    {name: "X70 Avsiktligt sjävdestruktiv handling genom hängning, strypning och kvävning"},
    {name: "X71 Avsiktligt självdestruktiv handling genom dränkning"},
    {name: "X72 Avsiktligt självdestruktiv handling genom skott från pistol och revolver"},
    {name: "X73 Avsiktligt självdestruktiv handling genom skott från gevär, hagelgevär och tyngre skjutvapen"},
    {name: "X74 Avsiktligt självdestruktiv handling genom skott från annat och icke specificerat skjutvapen"},
    {name: "X75 Avsiktligt självdestruktiv handling med sprängämnen"},
    {name: "X76 Avsiktligt självdestruktiv handling med rök och öppen eld"},
    {name: "X77 Avsiktligt självdestruktiv handling med vattenånga, andra heta ångor och heta föremål"},
    {name: "X78 Avsiktligt självdestruktiv handling med skärande eller stickande föremål"},
    {name: "X79 Avsiktligt självdestruktiv handling med trubbigt föremål"},
    {name: "X80 Avsiktlig självdestruktiv handling genom hopp eller fall från höjd"},
    {name: "X81 Avsiktligt självdestruktiv handling genom att kasta sig framför eller lägga sig framför föremål i rörelse"},
    {name: "X82 Avsiktligt självdestruktiv handling med motorfordon"},
    {name: "X83 Avsiktligt självdestruktiv handling med andra specificerade metoder"},
    {name: "X84 Avsiktligt självdestruktiv handling med icke specificerade metoder"}
    ]},
    {name: "X85-Y09 Övergrepp av annan person", subs: [
    {name: "X85 Övergrepp genom förgiftning med läkemedel och biologiska substanser"},
    {name: "X86 Övergrepp med frätande ämnen"},
    {name: "X87 Övergrepp med pesticider"},
    {name: "X88 Övergrepp med gaser eller ångor"},
    {name: "X89 Övergrepp med andra specificerade kemiska ämnen och skadliga substanser"},
    {name: "X90 övergrepp med icke specificerade kemiska ämnen"},
    {name: "X91 Övergrepp genom hängning, strypning och kvävning"},
    {name: "X92 Övergrepp genom dränkning"},
    {name: "X93 Övergrepp genom skott från pistol och revolver"},
    {name: "X94 Övergrepp genom skott från gevär, hagelgevär och tyngre skjutvapen"},
    {name: "X95 Övergrepp genom skott från annat och icke specificerat skjutvapen"},
    {name: "X96 Övergrepp med sprängämnen"},
    {name: "X97 Övergrepp med rök och öppen eld"},
    {name: "X98 Övergrepp med vattenånga, andra heta ångor och heta föremål"},
    {name: "X99 Övergrepp med skärande eller stickande föremål"},
    {name: "Y00 Övergrepp med trubbigt föremål"},
    {name: "Y01 Övergrepp genom knuff från höjd"},
    {name: "Y02 Övergrepp genom att offret placerats eller knuffats framför föremål i rörelse"},
    {name: "Y03 Övergrepp med motorfordon"},
    {name: "Y04 Övergrepp genom obeväpnat våld"},
    {name: "Y05 Sexuellt övergrepp av obeväpnad person"},
    {name: "Y06 Försummelse och vanvård"},
    {name: "Y07 Annan misshandel"},
    {name: "Y08 övergrepp med andra specificerade metoder"},
    {name: "Y09 Övergrepp med icke specificerade metoder"}
    ]},
    {name: "Y10-Y34 Skadehändelser med oklar avsikt", subs: [
    {name: "Y10 Förgiftning med och exponering för smärtstillande läkemedel av icke opiatkaraktär, febernedsättande medel och medel mot reumatism, med oklar avsikt"},
    {name: "Y11 Förgiftning med och exponering för antiepileptika, lugnande läkemedel och sömnmedel, medel mot parkinsonism samt psykotropa medel som ej klassificeras annorstädes, med oklar avsikt"},
    {name: "Y12 Förgiftning med och exponering för narkotiska medel och hallucinogener som ej klassificeras annorstädes, med oklar avsikt"},
    {name: "Y13 Förgiftning med och exponering för andra läkemedel som påverkar det autonoma nervsystemet, med oklar avsikt"},
    {name: "Y14 Förgiftning med och exponering för andra och icke specificerade droger, läkemedel och biologiska substanser, med oklar avsikt"},
    {name: "Y15 Förgiftning med och exponering för alkoholer, med oklar avsikt"},
    {name: "Y16 Förgiftning med och exponering för organiska lösningsmedel och halogenerade kolväten och deras ångor, med oklar avsikt"},
    {name: "Y17 Förgiftning med och exponering för andra gaser och ångor, med oklar avsikt"},
    {name: "Y18 Förgiftning med och exponering för pesticider, med oklar avsikt"},
    {name: "Y19 Förgiftning med och exponering för andra och icke specificerade kemiska ämnen och skadliga substanser, med oklar avsikt"},
    {name: "Y20 Hängning, strypning och kvävning, med oklar avsikt"},
    {name: "Y21 Drunkning eller dränkning, med oklar avsikt"},
    {name: "Y22 Skott från pistol och revolver, med oklar avsikt"},
    {name: "Y23 Skott från gevär, hagelgevär och tyngre skjutvapen, med oklar avsikt"},
    {name: "Y24 Skott från annat eller icke specificerat skjutvapen, med oklar avsikt"},
    {name: "Y25 Skadehändelse med sprängämne, med oklar avsikt"},
    {name: "Y26 Exponering för rök och öppen eld, med oklar avsikt"},
    {name: "Y27 Exponering för vattenånga, andra heta ångor och heta föremål, med oklar avsikt"},
    {name: "Y28 Skadehändelse med skärande eller stickande föremål, med oklar avsikt"},
    {name: "Y29 Skadehändelse med trubbigt föremål, med oklar avsikt"},
    {name: "Y30 Fall, hopp eller knuff från höjd, med oklar avsikt"},
    {name: "Y31 Fallit eller hoppat, sprungit eller lagt sig framför föremål i rörelse, med oklar avsikt"},
    {name: "Y32 Skadehändelse med motorfordon, med oklar avsikt"},
    {name: "Y33 Andra specificerade skadehändelser, med oklar avsikt"},
    {name: "Y34 Icke specificerad skadehändelse, med oklar avsikt"}
    ]},
    {name: "Y35-Y36 Polisingripande och krigshandling", subs: [
    {name: "Y35 Polisingripande och annat legalt ingripande"},
    {name: "Y36 Krigshandling"}
    ]},
    {name: "Y40-Y59 Läkemedel, droger och biologiska substanser i terapeutiskt bruk som orsak till ogynnsam effekt", subs: [
    {name: "Y40 Antibiotika för systemiskt bruk"},
    {name: "Y41 Andra medel mot infektioner och parasiter för systemiskt bruk"},
    {name: "Y42 Hormoner och deras syntetiska substitut och antagonister som ej klassificeras annorstädes"},
    {name: "Y43 Läkemedel med företrädesvis systemisk verkan"},
    {name: "Y44 Läkemedel som företrädesvis påverkar blodets komponenter"},
    {name: "Y45 Smärtstillande och febernedsättande läkemedel samt antiinflammatoriska och antireumatiska läkemedel"},
    {name: "Y46 Läkemedel mot epilepsi och vid parkinsonism"},
    {name: "Y47 Lugnande medel, sömnmedel och läkemedel mot oro"},
    {name: "Y48 Anestetika och gaser för terapeutiskt bruk"},
    {name: "Y49 Psykotropa medel som ej klassificeras annorstädes"},
    {name: "Y50 Medel som stimulerar centrala nervsystemet, som ej klassificeras annorstädes"},
    {name: "Y51 Medel som företrädesvis påverkar det autonoma nervsystemet"},
    {name: "Y52 Medel som företrädesvis påverkar hjärt-kärlsystemet"},
    {name: "Y53 Medel som företrädesvis påverkar mag-tarmkanalen"},
    {name: "Y54 Medel som företrädesvis påverkar vattenbalansen och mineral- och urinsyraomsättningen"},
    {name: "Y55 Medel som företrädesvis påverkar den glatta muskulaturen och skelettmuskulaturen samt andningsorganen"},
    {name: "Y56 Läkemedel företrädesvis använda för lokalterapi vid hud- och slemhinnesjukdomar, vid ögon-, öron-, hals- och tandsjukdomar"},
    {name: "Y57 Andra och icke specificerade läkemedel och droger"},
    {name: "Y58 Bakterievacciner"},
    {name: "Y59 Andra och icke specificerade vacciner och biologiska substanser"}
    ]},
    {name: "Y60-Y69 Missöden som inträffat med patienter under kirurgisk och medicinsk vård", subs: [
    {name: "Y60 Skärskada, punktion, perforation eller blödning oavsiktligt tillfogad under kirurgisk och medicinsk behandling"},
    {name: "Y61 Kvarlämnat främmande föremål i kroppen vid kirurgisk och medicinsk behandling"},
    {name: "Y62 Bristande sterilitet vid kirurgisk och medicinsk behandling"},
    {name: "Y63 Felaktig dosering vid kirurgisk och medicinsk behandling"},
    {name: "Y64 Förorenade medicinska och biologiska substanser"},
    {name: "Y65 Annat missöde vid kirurgisk och medicinsk behandling"},
    {name: "Y66 Kirurgisk och medicinsk behandling ej utförd"},
    {name: "Y69 Icke specificerat missöde vid kirurgisk och medicinsk behandling"}
    ]},
    {name: "Y70-Y82 Missöden orsakade av medicinska instrument i diagnostiskt och terapeutiskt bruk", subs: [
    {name: "Y70 Anestesiutrustning som orsakat missöden"},
    {name: "Y71 Instrument och materiel använda vid kardiovaskulära tillstånd och ingrepp som orsakat missöden"},
    {name: "Y72 Instrument och materiel använda vid oto-rino-laryngologiska tillstånd och ingrepp som orsakat missöden"},
    {name: "Y73 Instrument och materiel vid gastroenterologiska och urologiska tillstånd och ingrepp som orsakat missöden"},
    {name: "Y74 Instrument och materiel använda i annan sjukvård och egenvård som orsakat missöden"},
    {name: "Y75 Instrument och materiel använda vid neurologiska tillstånd och ingrepp som orsakat missöden"},
    {name: "Y76 Instrument och materiel använda vid obstetriska och gynekologiska tillstånd och ingrepp som orsakat missöden"},
    {name: "Y77 Instrument och materiel använda vid oftalmologiska tillstånd och ingrepp som orsakat missöden"},
    {name: "Y78 Instrument och materiel använda vid radiologiska undersökningar som orsakat missöden"},
    {name: "Y79 Instrument och materiel använda vid ortopediska tillstånd och ingrepp som orsakat missöden"},
    {name: "Y80 Instrument och materiel använda i fysikalisk medicin som orsakat missöden"},
    {name: "Y81 Instrument och materiel använda i allmänkirurgi och plastikkirurgi som orsakat missöden"},
    {name: "Y82 Andra och icke specificerade medicinska instrument och annan och icke specificerad materiel som orsakat missöden"}
    ]},
    {name: "Y83-Y84 Kirurgiska och andra medicinska åtgärder som orsak till onormal reaktion eller sen komplikation hos patient utan anknytning till missöde vid operations- eller behandlingstillfället", subs: [
    {name: "Y83 Kirurgisk operation och andra kirurgiska ingrepp som orsak till onormal reaktion eller sen komplikation hos patient utan anknytning till missöde vid tiden för åtgärden"},
    {name: "Y84 Andra medicinska åtgärder som orsak till onormal reaktion eller sen komplikation hos patient utan anknytning till missöde vid tiden för åtgärden"}
    ]},
    {name: "Y85-Y89 Sena effekter av yttre orsaker till sjukdom och död", subs: [
    {name: "Y85 Sena effekter av transportolyckor"},
    {name: "Y86 Sena effekter av andra olyckor"},
    {name: "Y87 Sena effekter av avsiktligt självdestruktiv handling, övergrepp och skadehändelse med oklar avsikt"},
    {name: "Y88 Sena effekter av kirurgisk och medicinsk behandling som yttre orsak"},
    {name: "Y89 Sena effekter av andra yttre orsaker"}
    ]},
    {name: "Y90-Y98 Bidragande faktorer som har samband med yttre orsaker till sjukdom och död, vilka klassificeras annorstädes", subs: [
    {name: "Y90 Tecken på alkoholpåverkan, fastställd genom mätning av blodets alkoholhalt"},
    {name: "Y91 Tecken på alkoholpåverkan med kliniskt fastställd intoxikationsgrad"},
    {name: "Y95 Nosokomial infektion"},
    {name: "Y96 Arbetsmiljö som orsak till sjukdom"},
    {name: "Y97 Förorening i yttre miljön som orsak till sjukdom"},
    {name: "Y98 Livsstil som orsak till sjukdom"}
    ]}
  ]},
  {name: "Z00-Z99 Faktorer av betydelse för hälsotillståndet och för kontakter med hälso- och sjukvården", subs: [
    {name: "Z00-Z13 Kontakt med hälso- och sjukvården för undersökning och utredning", subs: [
    {name: "Z00 Allmän undersökning och utredning av personer utan besvär eller utan att diagnos registrerats"},
    {name: "Z01 Andra speciella undersökningar och utredningar av personer utan besvär eller utan att diagnos registrerats"},
    {name: "Z02 Kontakt med hälso- och sjukvården av administrativa skäl"},
    {name: "Z03 Medicinsk observation och bedömning för misstänkta sjukdomar och tillstånd"},
    {name: "Z04 Undersökning och observation av andra skäl"},
    {name: "Z08 Kontrollundersökning efter behandling för malign tumör"},
    {name: "Z09 Kontrollundersökning efter behandling för andra tillstånd än maligna tumörer"},
    {name: "Z10 Rutinmässig allmän hälsokontroll av definierade befolkningsgrupper"},
    {name: "Z11 Riktad hälsokontroll avseende infektionssjukdomar och parasitsjukdomar"},
    {name: "Z12 Riktad hälsokontroll avseende tumörer"},
    {name: "Z13 Riktad hälsokontroll avseende andra sjukdomar och tillstånd"}
    ]},
    {name: "Z20-Z29 Potentiella hälsorisker avseende smittsamma sjukdomar", subs: [
    {name: "Z20 Kontakt med och exponering för smittsamma sjukdomar"},
    {name: "Z21 Asymtomatisk infektion med humant immunbristvirus [HIV]"},
    {name: "Z22 Bärare av agens för infektionssjukdom"},
    {name: "Z23 Kontakt för vaccination avseende enstaka bakteriesjukdomar"},
    {name: "Z24 Kontakt för vaccination avseende vissa enstaka virussjukdomar"},
    {name: "Z25 Kontakt för vaccination avseende andra enstaka virussjukdomar"},
    {name: "Z26 Kontakt för vaccination avseende andra enstaka infektionssjukdomar"},
    {name: "Z27 Kontakt för vaccination avseende kombinationer av infektionssjukdomar"},
    {name: "Z28 Ej genomförd vaccination"},
    {name: "Z29 Behov av andra förebyggande åtgärder"}
    ]},
    {name: "Z30-Z39 Kontakter med hälso- och sjukvården i samband med fortplantning", subs: [
    {name: "Z30 Födelsekontroll"},
    {name: "Z31 Fertilitetsfrämjande åtgärder"},
    {name: "Z32 Graviditetsundersökning"},
    {name: "Z33 Graviditet som bifynd"},
    {name: "Z34 Övervakning av normal graviditet"},
    {name: "Z35 Övervakning av högriskgraviditet"},
    {name: "Z36 Undersökning av foster före förlossningen"},
    {name: "Z37 Förlossningsutfall"},
    {name: "Z38 Levande födda barn efter plats för födsel"},
    {name: "Z39 Vård och undersökning av moder efter förlossningen"}
    ]},
    {name: "Z40-Z54 Kontakter med hälso- och sjukvården för speciella åtgärder och vård", subs: [
    {name: "Z40 Profylaktisk kirurgi"},
    {name: "Z41 Åtgärd i annat syfte än att återställa hälsan"},
    {name: "Z42 Plastikkirurgisk eftervård"},
    {name: "Z43 Tillsyn av konstgjord kroppsöppning"},
    {name: "Z44 Inpassning och justering av yttre protes"},
    {name: "Z45 Justering och skötsel av implanterat hjälpmedel"},
    {name: "Z46 Insättning, utprovning och justering av andra hjälpmedel"},
    {name: "Z47 Annan ortopedisk eftervård"},
    {name: "Z48 Annan kirurgisk eftervård"},
    {name: "Z49 Dialysvård"},
    {name: "Z51 Annan medicinsk vård"},
    {name: "Z52 Donatorer av organ och vävnader"},
    {name: "Z53 Kontakt med hälso- och sjukvården för specificerade åtgärder som ej genomförts"},
    {name: "Z54 Konvalescens"}
    ]},
    {name: "Z55-Z65 Potentiella hälsorisker avseende socioekonomiska och psykosociala förhållanden", subs: [
    {name: "Z55 Problem i samband med utbildning, läs- och skrivkunnighet"},
    {name: "Z56 Problem i samband med anställning och arbetslöshet"},
    {name: "Z57 Yrkesmässig exponering för riskfaktorer"},
    {name: "Z58 Problem som har samband med fysisk miljö"},
    {name: "Z59 Problem som har samband med bostadsförhållanden och ekonomiska omständigheter"},
    {name: "Z60 Problem som har samband med social miljö"},
    {name: "Z61 Problem som har samband med negativa händelser under barndomen"},
    {name: "Z62 Andra problem som har samband med uppfostran"},
    {name: "Z63 Andra problem som har samband med den primära stödgruppen, inkluderande familjeförhållanden"},
    {name: "Z64 Problem som har samband med vissa psykosociala förhållanden"},
    {name: "Z65 Problem som har samband med andra psykosociala förhållanden"}
    ]},
    {name: "Z70-Z76 Kontakter med hälso- och sjukvården i andra situationer", subs: [
    {name: "Z70 Rådgivning om sexuell orientering, sexuellt beteende samt inställningen till sexualiteten"},
    {name: "Z71 Kontakt med hälso- och sjukvården för medicinsk och annan rådgivning som ej klassificeras annorstädes"},
    {name: "Z72 Problem som har samband med livsstil"},
    {name: "Z73 Problem som har samband med svårigheter att kontrollera livssituationen"},
    {name: "Z74 Problem som har samband med beroende av vårdgivare"},
    {name: "Z75 Problem som har samband med vårdresurser och annan hälso- och sjukvård"},
    {name: "Z76 Kontakt med hälso- och sjukvården under andra omständigheter"}
    ]},
    {name: "Z80-Z99 Potentiella hälsorisker i familjens och patientens sjukhistoria samt vissa tillstånd och förhållanden som påverkar hälsan", subs: [
    {name: "Z80 Malign tumör i familjens sjukhistoria"},
    {name: "Z81 Psykisk störning och beteenderubbning i familjens sjukhistoria"},
    {name: "Z82 Vissa funktionshinder och kroniska sjukdomar i familjens sjukhistoria"},
    {name: "Z83 Andra specificerade sjukdomar i familjens sjukhistoria"},
    {name: "Z84 Andra tillstånd i familjens sjukhistoria"},
    {name: "Z85 Malign tumör i den egna sjukhistorien"},
    {name: "Z86 Vissa andra sjukdomar i den egna sjukhistorien"},
    {name: "Z87 Andra sjukdomar och tillstånd i den egna sjukhistorien"},
    {name: "Z88 Överkänslighet för läkemedel och biologiska substanser i den egna sjukhistorien"},
    {name: "Z89 Förvärvad avsaknad av extremitet"},
    {name: "Z90 Förvärvad avsaknad av organ som ej klassificeras annorstädes"},
    {name: "Z91 Riskfaktorer i den egna sjukhistorien som ej klassificeras annorstädes"},
    {name: "Z92 Medicinsk behandling i den egna sjukhistorien"},
    {name: "Z93 Tillstånd med konstgjord kroppsöppning"},
    {name: "Z94 Organ eller vävnad ersatt genom transplantation"},
    {name: "Z95 Förekomst av implantat och transplantat i hjärta och kärl"},
    {name: "Z96 Förekomst av andra funktionella implantat"},
    {name: "Z97 Förekomst av proteser och andra hjälpmedel"},
    {name: "Z98 Andra postoperativa tillstånd"},
    {name: "Z99 Beroende av maskinella och andra hjälpmedel som ej klassificeras annorstädes"}
    ]}
  ]} ]};
    
    $scope.itemClicked = function(item, itemRoot) {
        if (item.allSelected) {
            deselectAll(item);
        } else if (item.someSelected) {
            selectAll(item);
        } else {
            selectAll(item);
        }
        updateState(itemRoot);
    }
    
    function deselectAll(item) {
		if (!item.hide) {
			item.allSelected = false;
			item.someSelected = false;
			if (item.subs) {
				for(var i = 0; i < item.subs.length; i++) {
					deselectAll(item.subs[i]);
            	}
        	}
		}
    }
    
    function selectAll(item) {
		if (!item.hide) {
	        item.allSelected = true;
	        item.someSelected = false;
	        if (item.subs) {
	            for(var i = 0; i < item.subs.length; i++) {
	                selectAll(item.subs[i]);
	            }
	        }
		}
    }
    
    function updateState(item) {
        if (item.subs) {
            var someSelected = false;
            var allSelected = true;
            for(var i = 0; i < item.subs.length; i++) {
                var currItem = item.subs[i];
				if (!currItem.hide) {
	                updateState(currItem);
	                someSelected = someSelected || currItem.someSelected || currItem.allSelected;
	                allSelected = allSelected && currItem.allSelected;
				}
            }
            if (allSelected) {
                item.allSelected = true;
                item.someSelected = false;
            } else {
                item.allSelected = false;
                item.someSelected = someSelected ? true : false;
            }
        }
    }
    
    function isItemHidden(item, searchText) {
        if (item.name.toLowerCase().indexOf(searchText) >= 0) {
            return false;
        }
        if (!item.subs) {
            return true;
        }
        for(var i = 0; i < item.subs.length; i++) {
            if (!isItemHidden(item.subs[i], searchText)) {
                return false;
            }
        }
        return true;
    }
    
    $scope.filterMenuItems = function(items, text) {
        var searchText = text.toLowerCase();
        var mappingFunc = function(item) {
            if (item.subs) {
                ControllerCommons.map(item.subs, mappingFunc);
            }
            item.hide =  isItemHidden(item, searchText);
        };
        ControllerCommons.map(items, mappingFunc);
        ControllerCommons.map(items, updateState);
    }
    
    $scope.selectedLeavesCount = function(node) {
        var c = 0;
        if (node.subs) {
            ControllerCommons.map(node.subs, function(item) {
                c += $scope.selectedLeavesCount(item);
            });
        } else {
            c += node.allSelected ? 1 : 0;
        }
        return c;
    };

    $scope.selectedKapitelCount = function(node) {
        var c = 0;
        ControllerCommons.map(node.subs, function(item) {
            if ($scope.selectedLeavesCount(item) > 0) {
            	c++;
            };
        });
        return c;
    };

    $scope.selectedAvsnittCount = function(node) {
        var c = 0;
        ControllerCommons.map(node.subs, function(item) {
	        ControllerCommons.map(item.subs, function(item) {
				if ($scope.selectedLeavesCount(item) > 0) {
					c++;
				};
			});
        });
        return c;
    };
 };
