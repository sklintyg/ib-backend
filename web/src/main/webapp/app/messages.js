/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* jshint maxlen: false, unused: false */
var ibMessages = {
    'sv': {

        'common.logout': 'Logga ut',
        'common.yes': 'Ja',
        'common.no': 'Nej',
        'common.yes.caps': 'JA',
        'common.no.caps': 'NEJ',
        'common.ok': 'OK',
        'common.cancel': 'Avbryt',
        'common.confirm': 'Bekräfta',
        'common.goback': 'Tillbaka',
        'common.save': 'Spara',
        'common.change': 'Ändra',
        'common.print': 'Skriv ut',
        'common.close': 'Stäng',
        'common.date': 'Datum',
        'common.reset': 'Återställ',
        'common.approve': 'Godkänn',
        'common.show': 'Visa',
        'common.select': 'Välj',
        'common.selected': 'Vald',

        'common.label.loading': 'Laddar',

        'common.no.results': 'Inget resultat hittades för den valda filtreringen. Överväg att ändra filtreringen för att utöka resultatet.',
        'common.more.results': 'Hämta fler träffar',

        //System role
        'systemrole.login.label': 'Välj systemroll',
        'systemrole.login.description': 'Du har behörighet för flera systemroller. Välj det du vill logga in på nu. Du kan byta uppdrag även efter inloggning.',
        'systemrole.change.modal.label': 'Byt systemroll / Välj enhet',
        'systemrole.change.modal.description': 'Nedan ser du de enheter som du har behörighet till. Du ser även vilken roll som är knuten till enheten. Klicka på den enhet du vill byta till.',

        //Unit settings dialogs
        'unitsettings.spinner.loading': 'Hämtar standsinställningar...',
        'unitsettings.kontakt.change.enhet.modal.label': 'Kontaktuppgifter till vårdenheten',
        'unitsettings.kontakt.change.enhet.adress.desc': 'Denna adress är den som handlingar ska skickas till när du väljer vårdenhet som leverantör i svar på förfrågan. Du kan alltid ändra adressen i en enskild förfrågan innan du svarar.',
        'unitsettings.kontakt.change.underleverantor.modal.label': 'Kontaktuppgifter till underleverantör',
        'unitsettings.kontakt.change.underleverantor.adress.desc': 'Denna adress är den som handlingar ska skickas till när du väljer underleverantör som leverantör i svar på förfrågan. Du kan alltid ändra adressen i en enskild förfrågan innan du svarar.',
        'unitsettings.kontakt.form.mottagarnamn': 'Mottagare',
        'unitsettings.kontakt.form.adress': 'Adress',
        'unitsettings.kontakt.form.postnummer': 'Postnummer',
        'unitsettings.kontakt.form.postort': 'Postort',
        'unitsettings.kontakt.form.telefonnummer': 'Telefon',
        'unitsettings.kontakt.form.epost': 'E-post',
        'unitsettings.kontakt.form.fetchfromhsa': 'Hämta kontaktuppgifter från HSA katalogen',

        'unitsettings.svar.change.modal.label': 'Standardsvar för vårdenheten',
        'unitsettings.svar.change.standardsvar.title': 'Hantera standardsvar',
        'unitsettings.svar.change.standardsvar.desc': 'Denna text visas som standardkommentar när du svarar på en förfrågan. Du kan alltid ändra kommentaren i en enskild förfrågan innan du svarar.',

        'unitsettings.notifiering.modal.title.ve' : 'Notifieringsinställningar för vårdenheten',
        'unitsettings.notifiering.title.ve' : 'Hantera notifieringar',
        'unitsettings.notifiering.desc.ve' : 'Ange vilka notifieringar som vårdenheten eller eventuell underleverantör ska ta emot. Notifieringar kommer att skickas till den e-postadress som anges som kontaktuppgifter i svar på en förfrågan.',

        'unitsettings.notifiering.modal.title.vg' : 'Notifieringsinställningar för landstinget',
        'unitsettings.notifiering.title.vg' : 'Hantera notifieringar',
        'unitsettings.notifiering.desc.vg' : 'Ange vilka notifieringar som landstinget ska ta emot samt till vilken e-postadress som notifieringarna ska skickas.',
        'unitsettings.notifiering.vg.epost.label': 'E-post',
        'unitsettings.notifiering.vg.epost.placeholder': 'E-postadress för att ta emot notifieringar',

        // Skicka forfragan modal
        'skicka.forfragan.vardenheter.no.results': 'Det finns inga vårdenheter tillagda i landstinget.',
        'skicka.forfragan.vardenheter.error':'Ett tekniskt fel uppstod när information om vårdenheter skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',
        'skicka.forfragan.vardenheter.heading': 'Nedan visas de vårdenheter som finns avtalade för landstinget. Förfrågan kan skickas till flera vårdenheter samtidigt.',
        'skicka.forfragan.vardenheter.validation': 'Minst en vårdenhet måste väljas',
        'skicka.forfragan.redanskickad.tooltip': 'En förfrågan har redan skickats till enheten.',
        'skicka.forfragan.egna.label': 'Vårdenheter i det egna landstingets regi',
        'skicka.forfragan.annat.label': 'Vårdenheter i andra landstings regi',
        'skicka.forfragan.privat.label': 'Vårdenheter i privat regi',
        'skicka.forfragan.meddelande.label': 'Meddelande',
        'skicka.forfragan.meddelande.help': 'Meddelandet visas för vårdenheten och för Försäkringskassans handläggare.',
        'skicka.forfragan.meddelande.placeholder': 'Skriv meddelande här',

        // Tilldela direkt modal
        'tilldela.direkt.vardenheter.heading': 'Nedan visas de vårdenheter som finns avtalade för landstinget. Den enhet som väljs blir direkttilldelad förfrågan.',

        // Avvisa externförfrågan modal
        'avvisa.forfragan.kommentar.label': 'Lämna en kommentar till beslutet.',
        'avvisa.forfragan.kommentar.placeholder': 'Kommentar',
        'avvisa.forfragan.kommentar.validation': 'Kommentar måste anges',

        // Lägg till besök modal
        'lagg-till-besok.label.header': 'Lägg till besök',
        'lagg-till-besok.label.date': 'Datum',
        'lagg-till-besok.label.from': 'Från',
        'lagg-till-besok.label.to': 'Till',
        'lagg-till-besok.label.profession': 'Profession',
        'lagg-till-besok.label.name': 'Namn',
        'lagg-till-besok.label.interpreter': 'Tolk bokad',
        'lagg-till-besok.label.kallelseskickad': 'Kallelse skickad',
        'lagg-till-besok.label.kallelseskickadhelp': 'Datum anger då kallelsen skickades till invånaren eller då invånaren kontaktades per telefon',
        'lagg-till-besok.label.viamail': 'Per post',
        'lagg-till-besok.label.viaphone': 'Per telefon',
        'lagg-till-besok.info.profession': 'Om annan profession än läkare har valts så kommer utredningstypen att ändras till "AFU utvidgad".',
        'lagg-till-besok.info.kallelsedatum': 'Kallelsedatum är mindre än ${0} arbetsdagar innan besöksdatum. Om invånaren gör en sen avbokning eller uteblir kommer besöket inte att ersättas.',
        'lagg-till-besok.error.teknisktfel': 'Besöket kunde inte sparas på grund av tekniskt fel. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',
        'lagg-till-besok.error.teknisktfelafu': 'Utredningstypen kunde inte uppdateras på grund av tekniskt fel. Utredningen är fortsatt AFU.\n Rapportera en avvikelse orsakad av vården för besöket och lägg till det igen vid ett senare tillfälle.\n Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',

        // Redovisa besök modal
        'redovisabesok.info': 'Alla besök som har genomförts måste vården redovisa. För besök som ej har genomförts ska en avvikelse rapporteras.\n' +
        '\n' +
        'För de besök där tolk har varit inbokad måste vården även redovisa om tolk faktiskt deltog vid besöket eller inte.\n' +
        '\n' +
        'Markera nedan om besöket är genomfört och markera om tolk deltog för respektive besök.\n' +
        '\n' +
        'Endast bokade besök där starttidpunkten har passerats visas i listan.',
        'redovisabesok.error.teknisktfel': 'Ett fel uppstod vid redovisning av besök',
        'redovisabesok.error.validation': 'Tolkens deltagande måste anges för genomförda besök och besöket måste markeras som genomfört.',

        // Avvikelse modal
        'avvikelse.info': '<p>När en avvikelse registreras skickas meddelande till Försäkringskassans handläggare som kommer fatta ett beslut om hur återstående besök ska hanteras.</p>Besöket ska inte avbokas utan beslut från Försäkringskassans handläggare.',
        'avvikelse.label.orsakadav': 'Orsakad av',
        'avvikelse.label.invanareuteblev': 'Invånare uteblev',
        'avvikelse.label.datum': 'Avvikelse uppstod',
        'avvikelse.label.tid': 'kl.',
        'avvikelse.label.kommentar': 'Kommentar',
        'avvikelse.error.teknisktfel': 'Avvikelsen kunde inte rapporteras på grund av tekniskt fel. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',

        // Registrera mottagen handling modal
        'registrera-mottagen-handling.label.title': 'Datum för mottagna handlingar',
        'registrera-mottagen-handling.label.info': 'Här anges datumet för när handlingarna mottogs. Detta datum visas inte för Försäkringskassan utan är till för vården att kunna följa händelser kring utredningen.\n' +
        '\n' + 'Avser handlingar som tillhör beställningen.',

        // Registrera mottagen komplettering modal
        'registrera-mottagen-komplettering.label.title': 'Datum för mottagna kompletteringar',
        'registrera-mottagen-komplettering.label.info': 'Här anges datumet för när den kompletterande frågeställningen mottogs. Detta datum visas inte för Försäkringskassan utan är till för vården för att kunna följa händelser kring utredningen.\n' +
        '\n' + 'Avser kompletterande frågeställning avseende utlåtandet.',

        // Registrera skickat utlåtande modal
        'registrera-skickat-utlatande.label.title': 'Datum för skickat utlåtande',
        'registrera-skickat-utlatande.label.info': 'Här anges datumet för när utlåtande skickades. Detta datum visas inte för Försäkringskassan utan är till för vården att kunna följa händelser kring utredningen.',
        
        // Lägg till anteckning modal
        'skapa-anteckning.modal.info': 'Anteckningar för inte innehålla någon medicinsk information.\n' +
        '\n' + 'Anteckningarna visas endast för vårdadministratörer på samma vårdenhet.',
        
        //Registrerade vårdenheter
        'hantera-enheter.label.table.vardenheter.column.namn': 'Namn',
        'hantera-enheter.label.table.vardenheter.column.postadress': 'Postadress',
        'hantera-enheter.label.table.vardenheter.column.postnummer': 'Postnummer',
        'hantera-enheter.label.table.vardenheter.column.postort': 'Postort',
        'hantera-enheter.label.table.vardenheter.column.telefon': 'Telefon',
        'hantera-enheter.label.table.vardenheter.column.epost': 'Epost',
        'hantera-enheter.label.table.vardenheter.column.regiForm': 'Regiform',
        'hantera-enheter.filter.fritext.label': 'Fritextsökning',
        'hantera-enheter.filter.fritext.placeholder': 'Skriv sökord',
        'hantera-enheter.filter.addunit.label': 'Lägg till vårdenhet',
        'hantera-enheter.addunit.searchterm.hint': 'HSA-Id',
        'hantera-enheter.addunit.searchterm.label': 'Sök vårdenhet med HSA-id.',
        'hantera-enheter.addunit.searchterm.searchbtn.label': 'Sök',
        'hantera-enheter.addunit.selectregi.label': 'Regi',
        'hantera-enheter.addunit.result.alreadyexists': 'Vårdenheten är redan tillagd.',
        'hantera-enheter.addunit.result.nomatch': 'Ingen matchande vårdenhet kunde hittas. Kontrollera att angivet HSA id är korrekt.',
        'hantera-enheter.addunit.result.invalidunittype': 'Hittades i hsa men är inte en vårdenhet.',
        'hantera-enheter.addunit.result.searcherror': 'Ett tekniskt fel uppstod när vårdenheten skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',

        // Lista utredningar tabell
        'label.table.utredningar.loading': 'Pågående utredningar hämtas',
        'label.table.utredningar.column.id': 'Id',
        'label.table.utredningar.column.typ': 'Utredningstyp',
        'label.table.utredningar.column.vardenhet': 'Vårdenhet',
        'label.table.utredningar.column.fas': 'Fas',
        'label.table.utredningar.column.slutdatumfas': 'Slutdatum',
        'label.table.utredningar.column.status': 'Status',

        // Lista utredningar filter
        'utredningar.filter.fritext.label': 'Fritextsökning',
        'utredningar.filter.fritext.placeholder': 'Skriv sökord',
        'utredningar.filter.fas.label': 'Fas',
        'utredningar.filter.slutdatumfas.label': 'Slutdatum',
        'utredningar.filter.slutdatumfas.placeholder': 'Visa alla',
        'utredningar.filter.status.label': 'Status',
        'utredningar.filter.resetfilter.label': 'Återställ sökfilter',

        // Lista förfrågningar tabell
        'label.table.forfragningar.column.id': 'Id',
        'label.table.forfragningar.column.typ': 'Utredningstyp',
        'label.table.forfragningar.column.landsting': 'Landsting',
        'label.table.forfragningar.column.inkommet': 'Inkommet',
        'label.table.forfragningar.column.inkommet.tooltip': 'Avser datumet då förfrågan inkom till vårdenheten.',
        'label.table.forfragningar.column.svarsdatum': 'Svarsdatum',
        'label.table.forfragningar.column.svarsdatum.tooltip': 'Datum då vårdenheten senast ska svara landstinget.',
        'label.table.forfragningar.column.planeringsdatum': 'Planeringsdatum',
        'label.table.forfragningar.column.planeringsdatum.tooltip': 'Potentiellt slutdatum för utredningen om beställning sker idag.',
        'label.table.forfragningar.column.status': 'Status',

        // Visa utredningar - förfrågningar tabell
        'label.table.utredning.forfragningar.column.vardenhet': 'Vårdenhet',
        'label.table.utredning.forfragningar.column.status': 'Status förfrågan',
        'label.table.utredning.forfragningar.column.mojligtstartdatum': 'Möjligt startdatum',

        // Visa utredningar - händelser tabell
        'label.table.utredning.handelser.column.handelseDatum': 'Händelsedatum',
        'label.table.utredning.handelser.column.typ': 'Typ',
        'label.table.utredning.handelser.column.anvandare': 'Användare',
        'label.table.utredning.handelser.column.handelse': 'Händelse',
        'label.table.utredning.handelser.column.kommentar': 'Kommentar',

        // Visa beställning - besök tabell
        'label.table.bestallning.besok.column.besoktid': 'Besökstid',
        'label.table.bestallning.besok.column.profession': 'Profession',
        'label.table.bestallning.besok.column.namn': 'Namn',
        'label.table.bestallning.besok.column.tolk': 'Tolk',
        'label.table.bestallning.besok.column.kallelse': 'Kallelse skickad',
        'label.table.bestallning.besok.column.status': 'Status',
        'label.table.bestallning.besok.column.ersatts': 'Ersätts',

        // Visa utredningar - anteckningar tabell
        'label.table.bestallning.anteckningar.column.skapat': 'Datum',
        'label.table.bestallning.anteckningar.column.anvandare': 'Användare',
        'label.table.bestallning.anteckningar.column.text': 'Anteckning',
        'label.table.bestallning.anteckningar.empty': 'Inga anteckningar skapade.',

        // Lista pågående utredningar (vårdadmin) tabell
        'label.table.bestallningar.column.id': 'Id',
        'label.table.bestallningar.column.typ': 'Utredningstyp',
        'label.table.bestallningar.column.vardgivareNamn': 'Landsting',
        'label.table.bestallningar.column.patientId': 'Personnummer',
        'label.table.bestallningar.column.patientNamn': 'Namn',
        'label.table.bestallningar.column.slutdatumfas': 'Slutdatum',
        'label.table.bestallningar.column.status': 'Status',

        // Lista bestallningar filter
        'bestallningar.filter.fritext.label': 'Fritextsökning',
        'bestallningar.filter.fritext.placeholder': 'Skriv sökord',
        'bestallningar.filter.vardgivarenamn.label': 'Landsting',
        'bestallningar.filter.slutdatumfas.label': 'Slutdatum',
        'bestallningar.filter.slutdatumfas.placeholder': 'Visa alla',
        'bestallningar.filter.status.label': 'Status',
        'bestallningar.filter.resetfilter.label': 'Återställ sökfilter',

        // Lista förfrågningar filter
        'forfragningar.filter.fritext.label': 'Fritextsökning',
        'forfragningar.filter.fritext.placeholder': 'Skriv sökord',
        'forfragningar.filter.vardgivarenamn.label': 'Landsting',
        'forfragningar.filter.inkommet.label': 'Inkommet datum',
        'forfragningar.filter.inkommet.placeholder': 'Alla valda',
        'forfragningar.filter.svarsdatum.label': 'Svarsdatum',
        'forfragningar.filter.svarsdatum.placeholder': 'Alla valda',
        'forfragningar.filter.planeringsdatum.label': 'Planeringsdatum',
        'forfragningar.filter.planeringsdatum.placeholder': 'Alla valda',

        'forfragningar.filter.status.label': 'Status',
        'forfragningar.filter.resetfilter.label': 'Återställ sökfilter',

        // Lista avslutade utredningar filter
        'avslutade.utredningar.filter.fritext.label': 'Fritextsökning',
        'avslutade.utredningar.filter.fritext.placeholder': 'Skriv sökord',
        'avslutade.utredningar.filter.vardgivarenamn.label': 'Landsting',
        'avslutade.utredningar.filter.avslutsdatum.label': 'Avslutsdatum',
        'avslutade.utredningar.filter.avslutsdatum.placeholder': 'Alla valda',
        'avslutade.utredningar.filter.ersatts.label': 'Ersätts',
        'avslutade.utredningar.filter.fakturerad.label': 'Fakturerat',
        'avslutade.utredningar.filter.betald.label': 'Betald',
        'avslutade.utredningar.filter.utbetaldfk.label': 'Utbetald FK',
        'avslutade.utredningar.filter.status.label': 'Status',
        'avslutade.utredningar.filter.resetfilter.label': 'Återställ sökfilter',

        // Lista avslutade utredningar (samordnare) tabell
        'label.table.avslutade.utredningar.loading': 'Avslutade utredningar hämtas',
        'label.table.avslutade.utredningar.column.id': 'Id',
        'label.table.avslutade.utredningar.column.typ': 'Utredningstyp',
        'label.table.avslutade.utredningar.column.vardenhetNamn': 'Vårdenhet',
        'label.table.avslutade.utredningar.column.status': 'Status',
        'label.table.avslutade.utredningar.column.avslutsdatum': 'Avslutsdatum',
        'label.table.avslutade.utredningar.column.ersatts': 'Ersätts',
        'label.table.avslutade.utredningar.column.fakturerad': 'Fakturerad',
        'label.table.avslutade.utredningar.column.betald': 'Betald',
        'label.table.avslutade.utredningar.column.utbetaldfk': 'Utbetald FK',

        // Lista avslutade bestallningar filter
        'avslutade.bestallningar.filter.fritext.label': 'Fritextsökning',
        'avslutade.bestallningar.filter.fritext.placeholder': 'Skriv sökord',
        'avslutade.bestallningar.filter.vardgivarenamn.label': 'Landsting',
        'avslutade.bestallningar.filter.avslutsdatum.label': 'Avslutsdatum',
        'avslutade.bestallningar.filter.avslutsdatum.placeholder': 'Alla valda',
        'avslutade.bestallningar.filter.ersatts.label': 'Ersätts',
        'avslutade.bestallningar.filter.fakturerad.label': 'Fakturerad',
        'avslutade.bestallningar.filter.utbetald.label': 'Utbetald',
        'avslutade.bestallningar.filter.status.label': 'Status',
        'avslutade.bestallningar.filter.resetfilter.label': 'Återställ sökfilter',

        // Lista avslutade beställningar (vårdadmin) tabell
        'label.table.avslutade.bestallningar.column.id': 'Id',
        'label.table.avslutade.bestallningar.column.typ': 'Utredningstyp',
        'label.table.avslutade.bestallningar.column.vardgivareNamn': 'Landsting',
        'label.table.avslutade.bestallningar.column.status': 'Status',
        'label.table.avslutade.bestallningar.column.avslutsdatum': 'Avslutsdatum',
        'label.table.avslutade.bestallningar.column.ersatts': 'Ersätts',
        'label.table.avslutade.bestallningar.column.fakturerad': 'Fakturerad',
        'label.table.avslutade.bestallningar.column.utbetald': 'Utbetald',

        //Internförfrågningar
        'internforfragan.besvara.form.acceptera.title': 'Acceptera förfrågan',
        'internforfragan.besvara.form.avvisa.title': 'Avvisa förfrågan',
        'internforfragan.besvara.form.borjadatum.label': 'Möjligt startdatum',
        'internforfragan.besvara.form.kontaktinformation.label': 'Kontaktinformation',
        'internforfragan.besvara.form.kontaktinformation.label.help': 'Vid beställning skickas handlingarna till adressen som angivits här.',
        'internforfragan.besvara.form.utforaretyp.label': 'Val av leverantör',
        'internforfragan.besvara.form.utforaretyp.view.label': 'Leverantör',
        'internforfragan.besvara.form.utforarenamn.label': 'Mottagare',
        'internforfragan.besvara.form.utforareadress.label': 'Adress',
        'internforfragan.besvara.form.utforarepostnr.label': 'Postnummer',
        'internforfragan.besvara.form.utforarepostort.label': 'Postort',
        'internforfragan.besvara.form.utforaretelefon.label': 'Telefon',
        'internforfragan.besvara.form.utforareepost.label': 'E-post',
        'internforfragan.besvara.form.kommentar.label': 'Meddelande',
        'internforfragan.besvara.form.kommentar.label.help': 'Meddelandet visas för Försäkringskassan och eventuell samordnare hos Landstinget.',
        'internforfragan.besvara.acceptbtn.label': 'Acceptera förfrågan',
        'internforfragan.besvara.acceptbtn.send.label': 'Skicka',

        'internforfragan.besvara.rejectbtn.label': 'Avvisa förfrågan',
        'internforfragan.besvara.rejectbtn.send.label': 'Skicka',
        'internforfragan.besvara.rejectbtn.rejectProhibited.tooltip': 'Som enda vårdenhet i landstinget är det inte möjligt att avvisa förfrågan',
        'internforfragan.besvara.reject.form.label.help': 'Meddelandet visas för samordnare hos Landstinget och eventuellt för handläggare hos Försäkringskassan.',

        //Errors for reporting IO / backend error responses for REST requests
        'server.error.changeunit.title': 'Kunde inte byta systemroll',
        'server.error.changeunit.text': 'Försök igen eller kontakta support',
        'server.error.getvardenhetpreference.title': 'Kunde inte hämta vårdenhetens uppgifter',
        'server.error.getvardenhetpreference.text': 'Försök igen eller kontakta support',
        'server.error.setvardenhetpreference.title': 'Kunde inte spara vårdenhetens uppgifter',
        'server.error.setvardenhetpreference.text': 'Försök igen eller kontakta support',
        'server.error.getutredning.title': 'Kunde inte hämta utredning',
        'server.error.getutredning.text': 'Försök igen eller kontakta support',
        'server.error.createinternforfragan.title': 'Kunde inte skicka förfrågan',
        'server.error.createinternforfragan.text': 'Försök igen eller kontakta support',
        'server.error.acceptexternforfragan.title': 'Kunde inte acceptera förfrågan',
        'server.error.acceptexternforfragan.text': 'Försök igen eller kontakta support',
        'server.error.acceptexternforfragan.hsa.text': 'Ett tekniskt fel uppstod när vårdenheten skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',
        'server.error.acceptexternforfragan.myndighet.text': 'Det uppstod ett fel vid anropet till Försäkringskassan. Dina ändringar har inte sparats. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',
        'server.error.tilldeladirekt.title': 'Kunde inte tilldela direkt',
        'server.error.tilldeladirekt.text': 'Försök igen eller kontakta support',
        'server.error.updateregiform.title': 'Kunde inte spara ny regiform',
        'server.error.updateregiform.text': 'Försök igen eller kontakta support',
        'server.error.deletevardenhet.title': 'Kunde inte ta bort vårdenheten',
        'server.error.deletevardenhet.text': 'Försök igen eller kontakta support',
        'server.error.addvardenhet.title': 'Kunde inte spara vårdenhet',
        'server.error.addvardenhet.text': 'Försök igen eller kontakta support',
        'server.error.listvardenhet.text': 'Ett tekniskt fel uppstod när information om vårdenheterna skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',
        'server.error.accepterainternforfragan.title': 'Kunde inte acceptera förfrågan',
        'server.error.accepterainternforfragan.text': 'Försök igen eller kontakta support',
        'server.error.accepterainternforfragan.hsa.text': 'Ett tekniskt fel uppstod när vårdenheten skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',
        'server.error.accepterainternforfragan.myndighet.text': 'Det uppstod ett fel vid anropet till Försäkringskassan. Dina ändringar har inte sparats. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',
        'server.error.avvisainternforfragan.title': 'Kunde inte avvisa förfrågan',
        'server.error.avvisainternforfragan.text': 'Försök igen eller kontakta support',
        'server.error.avvisainternforfragan.hsa.text': 'Ett tekniskt fel uppstod när vårdenheten skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>',
        'server.error.avvisainternforfragan.myndighet.text': 'Det uppstod ett fel vid anropet till Försäkringskassan. Dina ändringar har inte sparats. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>.',
        'server.error.gethsainfo.title': 'Kunde inte hämta adressuppgifter från hsa',
        'server.error.gethsainfo.text': 'Försök igen eller kontakta support',
        'server.error.getnotifieringpreference.title': 'Kunde inte hämta notifieringsintällningar',
        'server.error.getnotifieringpreference.text': 'Försök igen eller kontakta support',
        'server.error.setnotifieringpreference.title': 'Kunde inte spara notifieringsintällningar',
        'server.error.setnotifieringpreference.text': 'Försök igen eller kontakta support',
        'server.error.registerreceived.title': 'Kunde inte registrera mottagen handling',
        'server.error.registerreceived.text': 'Försök igen eller kontakta support',
        'server.error.registersentutlatande.title': 'Kunde inte registrera skickat utlåtande',
        'server.error.registersentutlatande.text': 'Försök igen eller kontakta support',
        'server.error.registerkompletteringreceived.title': 'Kunde inte registrera mottagen komplettering',
        'server.error.registerkompletteringreceived.text': 'Försök igen eller kontakta support',
        'server.error.listavslutadeutredningar.title': 'Kunde inte hämta listan',
        'server.error.listavslutadeutredningar.text': 'Försök igen eller kontakta support',
        'server.error.savefaktura.title': 'Kunde inte spara faktura',
        'server.error.savefaktura.text': 'Försök igen eller kontakta support',
        'server.error.savebetald.title': 'Kunde inte spara betalning',
        'server.error.savebetald.text': 'Försök igen eller kontakta support',
        'server.error.saveutbetald.title': 'Kunde inte spara utbetalning',
        'server.error.saveutbetald.text': 'Försök igen eller kontakta support',
        'server.error.saveAnteckning.title': 'Kunde inte spara anteckning',
        'server.error.saveAnteckning.text': 'Försök igen eller kontakta support',
        'common.error.spi.fel01': 'Ett tekniskt fel uppstod när vårdenheten skulle hämtas. Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning och i andra hand <LINK:ineraNationellKundservice>'
    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
