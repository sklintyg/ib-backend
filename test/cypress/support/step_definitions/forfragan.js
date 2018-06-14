/* global then, when, given */
let moment = require('moment');
let utredningsId;

given('att Försäkringskassan har skickat en förfrågan AFU till samordnare', () => {
	
    let date = moment().add(1, 'days').format('YYYY-MM-DD');

	cy.requestHealthPerformerAssessment({
            utredningsTyp: 'AFU',
            besvaraSenastDatum: date.replace(/-/g,''),
            landstingHsaId: 'IFV1239877878-1041',
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
 
when('jag är inloggad som samordnare på Intygsbeställningen', () => {
	cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
    cy.visit('/#/app/samordnare/listaUtredningar/visaUtredning/' + utredningsId);
})        