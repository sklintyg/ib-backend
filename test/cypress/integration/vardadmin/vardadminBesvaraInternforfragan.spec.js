import moment from 'moment'

describe('Vårdadmin besvarar internförfrågningar', function() {
    var utredningId1, utredningId2;

    function fillFields() {
        cy.get('#adress_input').type('{selectall}Vårdgatan 3');
        cy.get('#postnummer_input').type('{selectall}11122');
        cy.get('#postort_input').type('{selectall}Vårdby');
    }

    before(() => {
        cy.deleteUtredningarForVardgivareId('ostergotland');
        //Create 2 utredningar with internforfragningar to answer
        cy.fixture('utredningar/utredning-1.json').then((utredning)  => {
            utredning.externForfragan.landstingHsaId = 'ostergotland';
            utredning.externForfragan.internForfraganList[0].vardenhetHsaId = 'linkoping';
            utredning.externForfragan.internForfraganList[0].forfraganSvar = null;
            utredning.externForfragan.internForfraganList[0].tilldeladDatum = null;
            utredning.externForfragan.internForfraganList[0].besvarasSenastDatum = moment().add(1, 'days');
            cy.createUtredning(utredning).then(response => { utredningId1 = response.body.actualEntity.utredningId});

            var utredning2 = JSON.parse(JSON.stringify(utredning));
            utredning2.utredningId = null;
            utredning2.externForfragan.internForfraganList[0].besvarasSenastDatum = moment().add(2, 'days');
            cy.createUtredning(utredning2).then(response => { utredningId2 = response.body.actualEntity.utredningId});

        });
    });

    it('Acceptera en internförfrågan', function() {
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');
        cy.visit('/#/app/vardadmin/listaForfragningar');
        cy.get('#vardadmin-lista-forfragningar-resulttext').should('have.text', 'Sökresultat: 2 av 2 förfrågningar');
        cy.get('#visa-internforfragan-' + utredningId1).click();
        cy.get('#open-accept-internforfragan-dialog-btn').click();
        cy.get('#adress_input').type('{selectall}{backspace}');
        cy.get('#accept-internforfragan-btn').should('be.disabled');
        fillFields();
        cy.get('#accept-internforfragan-btn').should('be.enabled');
        cy.get('#accept-internforfragan-btn').click();
        cy.get('#open-accept-internforfragan-dialog-btn').should('be.disabled');
        cy.get('#open-reject-internforfragan-dialog-btn').should('be.disabled');
        cy.get('#internforfragan-accepterad-alert').should('be.visible');

    });

    it('Avvisa en internförfrågan', function() {
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');
        cy.visit('/#/app/vardadmin/listaForfragningar');
        cy.get('#vardadmin-lista-forfragningar-resulttext').should('have.text', 'Sökresultat: 2 av 2 förfrågningar');
        cy.get('#visa-internforfragan-' + utredningId2).click();
        cy.get('#open-reject-internforfragan-dialog-btn').click();
        cy.get('#kommentar_textarea').type('{selectall}{backspace}');
        cy.get('#reject-internforfragan-btn').should('be.disabled');
        cy.get('#kommentar_textarea').type('Vi kan inte ta detta');
        cy.get('#reject-internforfragan-btn').should('be.enabled');
        cy.get('#reject-internforfragan-btn').click();
        cy.get('#open-reject-internforfragan-dialog-btn').should('be.disabled');
        cy.get('#open-accept-internforfragan-dialog-btn').should('be.disabled');
        cy.get('#internforfragan-avbojd-alert').should('be.visible');
    });


});
