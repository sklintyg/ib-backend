
describe('Samordnare visa utredning', () => {

    var landstingHsaId = 'IFV1239877878-1043';

    before(() => {
        cy.deleteUtredningarForVardgivareId(landstingHsaId)
        cy.deleteVardenheterForVardgivare(landstingHsaId)
    });

    beforeEach(() => {
        cy.fixture('utredningar/utredning-1.json').as('utredning1');
        cy.fixture('vardenheter/vardenhet-1.json').as('vardenhet1');
    });

    var utredningsId;
    var vardenhetId;

    it('Skapa testdata', function() {
        this.utredning1.externForfragan.internForfraganList = [];
        cy.createUtredning(this.utredning1).then(function(data){
            utredningsId = data.body.entity.utredningId;
        });
        cy.createVardenhet(this.vardenhet1).then(function(data){
            vardenhetId = data.body.entity;
        });
    });

    it('Visa utredning', function() {
        cy.log(utredningsId);
        cy.log(vardenhetId);
        cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
        cy.visit('/#/app/samordnare/listaUtredningar/visaUtredning/' + utredningsId);

        // Verifiera utredningsdata
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

        // Internförfrågan lista tom
        cy.get('#internforfragan-no-results').should('be.visible');

        // Skicka internförfrågan
        cy.get('#skicka-forfragan-button').click();
        cy.get('#skicka-forfragan-modal-vardenheter-eget-landsting-input-IFV1239877878-1042').click();
        cy.get('#skicka-forfragan-modal-skicka').click();

        // Verifiera vårdenhet i listan
        cy.get('#vardenhetNamn-0').should('have.text', 'WebCert-Enhet1');
    });

});
