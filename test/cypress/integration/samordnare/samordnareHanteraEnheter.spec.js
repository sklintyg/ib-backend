
describe('Samordnare hantera enheter', () => {

    var landstingHsaId = 'IFV1239877878-1043';
    const VE_TO_ADD = 'TSTNMT2321000156-1077';

before(() => {
    cy.deleteVardenheterForVardgivare(landstingHsaId)
});

beforeEach(() => {
    cy.fixture('vardenheter/vardenhet-1.json').as('vardenhet1');
});

var vardenhetId;

it('Skapa testdata', function() {


    cy.createVardenhet(this.vardenhet1).then(function(data){
        vardenhetId = data.body.entity;
    });
});

it('Visa vardenheter', function() {
    cy.log(vardenhetId);
    cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
    cy.visit('/#/app/samordnare/hanteraEnheter');

    // Verifiera lista
    cy.get('#samordnare-lista-vardenheter-resulttext').should('have.text', 'Sökresultat: 1 av 1 vårdenheter');
});

it('CRUD enheter', function() {

    cy.log(vardenhetId);
    cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
    cy.visit('/#/app/samordnare/hanteraEnheter');

    cy.get('#samordnare-lista-vardenheter-resulttext').should('have.text', 'Sökresultat: 1 av 1 vårdenheter');

    //Öppna lägg till modal
    cy.get('#hantera-enheter-filter-add-unit').click();
    cy.get('#add-enhet-searchterm-input').type('{selectall}' + VE_TO_ADD);
    cy.get('#add-enhet-search-btn').click();
    cy.get('#add-enhet-save-btn').should('be.disabled');

    //Välj regiform
    cy.get('#selectRegiFormDropDown-selected-item-label').click();
    cy.get('#selectRegiFormDropDown-EGET_LANDSTING').click();

    //save
    cy.get('#add-enhet-save-btn').click();

    //should appear in list
    cy.get('#samordnare-lista-vardenheter-resulttext').should('have.text', 'Sökresultat: 2 av 2 vårdenheter');
    cy.get('#row-'+ VE_TO_ADD + ' td').contains('NMT vg3 ve1');
    cy.get('#row-'+ VE_TO_ADD + ' td').contains('Eget landsting');

    //Lets change regiform..
    cy.get('#editVardenhetMenu-' + VE_TO_ADD).click();
    cy.get('#editVardenhetMenu-' + VE_TO_ADD +'-edit').click();
    cy.get('#regiFromDropDown-selected-item-label').click();
    cy.get('#regiFromDropDown-PRIVAT').click();
    cy.get('#edit-enhet-save-btn').click();
    cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
    cy.visit('/#/app/samordnare/hanteraEnheter');
    cy.get('#row-'+ VE_TO_ADD + ' td').contains('Privat');

    //.. and finally delete it
    cy.get('#editVardenhetMenu-' + VE_TO_ADD).click();
    cy.get('#editVardenhetMenu-' + VE_TO_ADD +'-delete').click();
    cy.get('#delete-enhet-confirm-btn').click();
    cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');
    cy.visit('/#/app/samordnare/hanteraEnheter');
    cy.get('#row-'+ VE_TO_ADD).should('not.exist');
    cy.get('#samordnare-lista-vardenheter-resulttext').should('have.text', 'Sökresultat: 1 av 1 vårdenheter');

});


});
