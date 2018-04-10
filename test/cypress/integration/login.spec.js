describe('Inloggning', function() {

    it('som Vårdadministratör', function() {
        cy.login('Ingbritt Filt (Vårdadminstratör 1 | Intygsbeställning)', 'IFV1239877878-1042');

        cy.get('body').should('contain', 'Din roll är FMU Vårdadministratör');
    })
});