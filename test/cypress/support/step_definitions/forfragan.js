/* global then, when, given */
let moment = require('moment');
let utredningsId;

let landstingHsaId = 'IFV1239877878-1041';
let enhetsHsaId = ''

given('att Försäkringskassan har skickat en förfrågan AFU till samordnare', () => {
	
    let date = moment().add(1, 'days').format('YYYY-MM-DD');

	cy.requestPerformerForAssessment({
            utredningsTyp: 'AFU',
            besvaraSenastDatum: date.replace(/-/g,''),
            landstingHsaId: landstingHsaId,
            invanare: {
                ort: 'hemma'
            },
            bestallare: {
                telefon: '123465789'
            }
        }).then((data) => {
            utredningsId = data.assessmentId
        });
})
 

then('ska förfrågans status vara {string} för {string}', (status, roll) => {
    goToUtredning(roll, landstingHsaId, utredningsId)
    cy.get("#utredning-header-status").should("contain", status);
})


when('jag direkttilldelar förfrågan', () => {
    goToUtredning('samordnare', landstingHsaId, utredningsId)
    cy.get('#tilldela-direkt-button').click();
    cy.get('#tilldela-direkt-modal-vardenheter-eget-landsting-input-IFV1239877878-1042').click();
    cy.get('#tilldela-direkt-modal-meddelande_textarea').type('nån slags text');
    cy.get('#tilldela-direkt-modal-skicka').click();

})

when('samordnare accepterar förfrågan', () => {
    goToUtredning('samordnare', landstingHsaId, utredningsId)
    cy.get('#accepteraBtn > span').click();

})

then('ska förfrågans status vara {string} för {string}', (status, roll) => {
    goToUtredning(roll, landstingHsaId, utredningsId)
    cy.get("#utredning-header-status").should("contain", status);

})

then('ska förfrågans status vara {string} för {string}', (status, roll) => {
    goToUtredning(roll, landstingHsaId, utredningsId)
    cy.get("#utredning-header-status").should("contain", status);
})


function goToUtredning(roll, landsting, utredning) {
    cy.login(getUser(landsting));
    if (roll === 'samordnare') {
        cy.get('#ib-vardenhet-selector-select-active-unit-IFV1239877878-1041-link').click();    
        cy.get('#samordnare-lista-utredningar-table').should('contain', utredning);
        cy.visit('/#/app/samordnare/listaUtredningar/visaUtredning/' + utredning);
    } else if (roll === 'vårdadmin') {
        cy.get('#ib-vardenhet-selector-select-active-unit-IFV1239877878-1042-link').click(); 
        cy.get('#menu-vardadministrator-listaForfragningar').click();
        cy.get('.listaForfragningar-page').should("contain", "Fritextsökning");
        cy.get('#filterStatus-selected-item-label').click();
        cy.get('#filterStatus-ALL').click();
        cy.get('#vardadmin-lista-forfragningar-table').should('contain', utredning);
        cy.visit('/#/app/vardadmin/listaForfragningar/visaInternForfragan/' + utredning);
    }
    
    
}


function getUser(landsting) {
    let user;
    //if (roll === 'samordnare' && landsting === 'IFV1239877878-1041') {
        user = 'Harald Alltsson (Alla roller | Intygsbeställning)';
    //}
    return user;
}
