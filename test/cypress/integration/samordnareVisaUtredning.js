
describe('Samordnare visa utredning', () => {

    var landstingHsaId = 'IFV1239877878-1043';

    before(() => {
        cy.deleteUtredningarForVardgivareId(landstingHsaId)
    });

    beforeEach(() => {
        cy.fixture('utredningar/utredning-1.json').as('utredning1');
    });

    var utredningsId;

    it('Skapa testdata', function() {
        this.utredning1.externForfragan.internForfraganList = [];
        cy.createUtredning(this.utredning1).then(function(data){
            utredningsId = data.body.entity;
        });
    });

    it('Visa utredning', function() {
        cy.log(utredningsId);
        cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
        cy.visit('/#/app/samordnare/listaUtredningar/visaUtredning/' + utredningsId);

        cy.get('#inkomDatum').should('have.text', '2018-05-04');
        cy.get('#besvarasSenastDatum').should('have.text', '2018-07-02');
        cy.get('#bostadsort').should('contain', 'hemma');
        cy.get('#tidigareUtredd').should('contain', 'Nej');
        cy.get('#behovAvTolk').should('contain', 'Nej');
        cy.get('#sarskildaBehov').should('contain', 'Massor');
        cy.get('#kommentar').should('contain', 'externForfragan kommentar');
        cy.get('#handlaggareNamn').should('contain', 'En handläggare');
        cy.get('#handlaggareTelefonnummer').should('contain', '123465789');
        cy.get('#handlaggareEpost').should('contain', 'En handläggare email');

        cy.get('#internforfragan-no-results').should('be.visible');

        cy.get('#skicka-forfragan-button').click();

    });

});