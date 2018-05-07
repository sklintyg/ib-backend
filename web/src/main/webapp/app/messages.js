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
        'common.goback': 'Tillbaka',
        'common.save': 'Spara',
        'common.change': 'Ändra',
        'common.print': 'Skriv ut',
        'common.close': 'Stäng',
        'common.date': 'Datum',
        'common.reset': 'Återställ',
        'common.approve': 'Godkänn',
        'common.show': 'Visa',

        'common.label.loading': 'Laddar',

        'common.no.results': 'Inget resultat hittades för den valda filtreringen. Överväg att ändra filtreringen för att utöka resultatet.',
        'common.more.results': 'Hämta fler träffar',

        //System role
        'systemrole.login.label': 'Välj systemroll',
        'systemrole.login.description': 'Du har behörighet för flera systemroller. Välj det du vill logga in på nu. Du kan byta uppdrag även efter inloggning.',
        'systemrole.change.modal.label': 'Byt systemroll / Välj enhet',
        'systemrole.change.modal.description': 'Nedan ser du de enheter som du har behörighet till. Du ser även vilken roll som är knuten till enheten. Klicka på den enhet du vill byta till.',

        //Unit settings dialog
        'unitsettings.spinner.loading': 'Hämtar standsinställningar...',
        'unitsettings.change.modal.label': 'Inställningar',
        'unitsettings.change.adress.title': 'Hantera kontaktuppgifter',
        'unitsettings.change.adress.desc': 'Denna adress är den som handlingar ska skickas till som standard. Du kan alltid ändra adressen i en enskild förfrågan innan du svarar.',
        'unitsettings.form.mottagarnamn': 'Mottagare',
        'unitsettings.form.adress': 'Postadress',
        'unitsettings.form.postnummer': 'Postkod',
        'unitsettings.form.postort': 'Postort',
        'unitsettings.form.telefonnummer': 'Telefonnummer',
        'unitsettings.form.epost': 'Epost',
        'unitsettings.change.standardsvar.title': 'Hantera standardsvar',
        'unitsettings.change.standardsvar.desc': 'Denna text visas som standardkommentar när du svarar på en förfrågan. Du kan alltid ändra kommentaren i en enskild förfrågan innan du svarar.',

        // Lista utredningar tabell
        'label.table.utredningar.column.id': 'Id',
        'label.table.utredningar.column.typ': 'Utredningstyp',
        'label.table.utredningar.column.vardenhet': 'Vårdenhet',
        'label.table.utredningar.column.fas': 'Fas',
        'label.table.utredningar.column.slutdatumfas': 'Slutdatum fas',
        'label.table.utredningar.column.status': 'Status',

        // Lista utredningar filter
        'utredningar.filter.fritext.label': 'Fritextsökning',
        'utredningar.filter.fritext.placeholder': 'Skriv sökord',
        'utredningar.filter.fas.label': 'Fas',
        'utredningar.filter.slutdatumfas.label': 'Slutdatum fas',
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

        // Lista pågående utredningar (vårdadmin) tabell
        'label.table.bestallningar.column.id': 'Id',
        'label.table.bestallningar.column.typ': 'Utredningstyp',
        'label.table.bestallningar.column.vardgivareNamn': 'Landsting',
        'label.table.bestallningar.column.patientId': 'Personnummer',
        'label.table.bestallningar.column.patientNamn': 'Namn',
        'label.table.bestallningar.column.slutdatumfas': 'Slutdatum fas',
        'label.table.bestallningar.column.status': 'Status',

        // Lista bestallningar filter
        'bestallningar.filter.fritext.label': 'Fritextsökning',
        'bestallningar.filter.fritext.placeholder': 'Skriv sökord',
        'bestallningar.filter.vardgivarenamn.label': 'Landsting',
        'bestallningar.filter.slutdatumfas.label': 'Slutdatum fas',
        'bestallningar.filter.slutdatumfas.placeholder': 'Visa alla',
        'bestallningar.filter.status.label': 'Status',
        'bestallningar.filter.resetfilter.label': 'Återställ sökfilter',

        //Errors for reporting IO / backend error responses for REST requests
        'server.error.changeunit.title': 'Kunde inte byta systemroll',
        'server.error.changeunit.text': 'Försök igen eller kontakta support',
        'server.error.getvardenhetpreference.title': 'Kunde inte hämta vårdenhetens uppgifter',
        'server.error.getvardenhetpreference.text': 'Försök igen eller kontakta support',
        'server.error.setvardenhetpreference.title': 'Kunde inte spara vårdenhetens uppgifter',
        'server.error.setvardenhetpreference.text': 'Försök igen eller kontakta support',
        'server.error.getutredning.title': 'Kunde inte hämta utredning',
        'server.error.getutredning.text': 'Försök igen eller kontakta support'
    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
