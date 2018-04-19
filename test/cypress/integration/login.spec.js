describe('Inloggning', function() {

    it('som Vårdadministratör', function() {
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'IFV1239877878-1042');

        cy.get('body').should('contain', 'Lista förfrågningar');
    })
});