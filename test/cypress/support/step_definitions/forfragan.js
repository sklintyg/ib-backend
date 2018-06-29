/* global then, when, given */
let moment = require('moment');
let utredningsId;
let landstingHsaId = 'IFV1239877878-1041';
let enhetsHsaId;

const enheter = {
    "WebCert-Enhet1" : "IFV1239877878-1042",
    "WebCert-Enhet3" : "IFV1239877878-104D"
}

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

when('Försäkringskassan avbryter förfrågan med anledning {string}', (anledning) => {
    cy.endAssessment({
        assessmentIdRoot : 'Tompa Testar',
        assessmentId : utredningsId,
        endingCondition : anledning
        }).then((data) => {
            console.log(data);
        });
})


then('ska förfrågans status vara {string} för {string}', (status, roll) => {
    goToUtredning(roll, landstingHsaId, enhetsHsaId, utredningsId)
    cy.get("#utredning-header-status").should("contain", status);
})

when('samordnare direkttilldelar förfrågan till enhet {string}', (enhetsNamn) => {
    enhetsHsaId = enheter[enhetsNamn];
    
    goToUtredning('samordnare', landstingHsaId, enhetsHsaId, utredningsId)
    cy.get('#tilldela-direkt-button').click();
    cy.get('#tilldela-direkt-modal-vardenheter-eget-landsting-input-' + enhetsHsaId).click(); //Testet felar p.g.a. elm saknar id i senaste deploy
    cy.get('#tilldela-direkt-modal-meddelande_textarea').type('nån slags text');
    cy.get('#tilldela-direkt-modal-skicka').click();
})

when('samordnare tilldelar förfrågan till enhet {string}', (enhetsNamn) => {
    enhetsHsaId = enheter[enhetsNamn];
    
    goToUtredning('samordnare', landstingHsaId, enhetsHsaId, utredningsId)
    cy.get('#skicka-forfragan-button').click();
    cy.get('#skicka-forfragan-modal-vardenheter-eget-landsting-input-IFV1239877878-1042').click();
    cy.get('#skicka-forfragan-modal-meddelande_textarea').type('Skickar förfrågan den långa vägen.');
    cy.get('#skicka-forfragan-modal-skicka > span').click();
    cy.get("#utredning-header-status").should("contain", "Väntar på svar");
})

when('{string} accepterar förfrågan', (roll) => {
    goToUtredning(roll, landstingHsaId, enhetsHsaId, utredningsId)
    acceptForfragan(roll)
})

then('ska förfrågans status vara {string} för {string}', (status, roll) => {
    goToUtredning(roll, landstingHsaId, enhetsHsaId, utredningsId)
    cy.get("#utredning-header-status").should("contain", status);
})

then('ska Försäkringskassan notifieras att (vårdenheten ){string} {string} förfrågan', (vardEnhet, responseCode) => {
	let url = 'http://mocks.sm.nordicmedtest.se:43000/validate/respondToPerformerRequest/' + utredningsId;
	cy.request('POST', url).then((res) => { 
		console.log(res);
        assert.equal(false, false, 'falskt är falskt');
		assert.equal(res.body.assessmentId, utredningsId, 'Hittas assessmentId i FK-simulator? (Mocks-SM)');
		assert.equal(res.body.responseCode, responseCode, 'Hittas responseCode i FK-simulator? (Mocks-SM)');
		if (vardEnhet !== 'vården') {
            assert.equal(res.body.performerCareUnitId, enhetsHsaId, 'Hittas performerCareUnitId i FK-simulator? (Mocks-SM)'); 
            assert.equal(res.body.performerCareUnitName, vardEnhet, 'Hittas performerCareUnitName i FK-simulator? (Mocks-SM)');    
        }
		

	});
})

when('{string} avvisar förfrågan', (roll) =>{
    goToUtredning(roll, landstingHsaId, enhetsHsaId, utredningsId)
    avvisaForfragan(roll)
})

function acceptForfragan(roll){
    if (roll === 'vårdadmin') {
        cy.get('#open-accept-internforfragan-dialog-btn > span').click();
        cy.get('#accept-internforfragan-btn').click();
        cy.get("#utredning-header-status").should("contain", "Tilldelad, väntar på beställning");
    } else if (roll === 'samordnare') {
        cy.get('#accepteraBtn > span').click();
        cy.get("#utredning-header-status").should("contain", "Tilldelad, väntar på beställning");
    }
}

function avvisaForfragan(roll){
    if (roll === 'samordnare') {
        cy.get("#avvisaBtn").click();
        cy.get('#avvisa-forfragan-modal-kommentar_textarea').type('Detta duger inte.');
        cy.get("#skicka-forfragan-modal-skicka > span").click();
        cy.get("#utredning-header-status").should("contain", "Avvisad");
    } else if (roll === 'vårdadmin') {
        cy.get("#open-reject-internforfragan-dialog-btn").click();
        cy.get("#kommentar_textarea").type('Detta är under all kritik.');
        cy.get("#reject-internforfragan-btn").click();
        cy.get("#utredning-header-status").should("contain", "Avvisad");
    }
}


function goToUtredning(roll, landsting, enhet, utredning) {
    cy.login(getUser(landsting));
    if (roll === 'samordnare') {
        cy.get('#ib-vardenhet-selector-select-active-unit-' + landsting + '-link').click();    
        //cy.get('#samordnare-lista-utredningar-table').should('contain', utredning);
        cy.get('#filterFritext-input').type(utredning);
        cy.visit('/#/app/samordnare/listaUtredningar/visaUtredning/' + utredning);
        
        
    } else if (roll === 'vårdadmin') {
        cy.get('#ib-vardenhet-selector-select-active-unit-' + enhet + '-link').click(); 
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