/* global then, when, given */
let moment = require('moment');
let utredningsId;

let landstingHsaId = 'IFV1239877878-1041';

given('att Försäkringskassan har skickat en förfrågan AFU till samordnare', () => {
	
    let date = moment().add(1, 'days').format('YYYY-MM-DD');

	cy.requestHealthPerformerAssessment({
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
    cy.login(getUser(roll, landstingHsaId));
    cy.visit('/#/app/samordnare/listaUtredningar/visaUtredning/' + utredningsId);
console.log(status);
console.log(roll);
})      





function getUser(roll, landsting) {
    let user;
    if (roll === 'samordnare' && landsting === 'IFV1239877878-1041') {
        user = 'Gunnel Grävling (Samordnare 2 | Intygsbeställning)';
    }
    return user;
}