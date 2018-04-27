describe('smoketest appheader funktioner', function() {

    it('Användare mer flera roller att välja bland', function() {

        //Logga in som en vårdadministratör med användare som har flera roller/enheter
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

        cy.get('ib-header-actions #settingsLinkBtn').should('be.visible');

        //öppna och stäng hjälpen
        cy.get('ib-header-actions #aboutLinkBtn').click();
        cy.get('#ib-about-modal-body').should('be.visible');
        cy.get('#close-ib-about-btn').click();
        cy.get('#ib-about-modal-body').should('not.be.visible');

        cy.get('ib-header-actions #logoutLinkBtn').should('be.visible');

        cy.get('ib-header-user #ib-header-user-name').should('have.text', 'Harald Alltsson');
        cy.get('ib-header-user #ib-header-user-role').contains('FMU Vårdadministratör');

        //Byt till samordnare på Webcert-Vårdgivare 1
        cy.bytEnhet('IFV1239877878-1041');

        cy.get('ib-header-actions #settingsLinkBtn').should('not.be.visible');
        cy.get('ib-header-actions #aboutLinkBtn').should('be.visible');
        cy.get('ib-header-actions #logoutLinkBtn').should('be.visible');

        cy.get('ib-header-user #ib-header-user-name').should('have.text', 'Harald Alltsson');
        cy.get('ib-header-user #ib-header-user-role').contains('FMU Samordnare');

    });

    it('Användare med endast 1 roll', function() {

        //Logga in som en vårdadministratör med användare som har flera roller/enheter
        cy.login('Ingbritt Filt (Vårdadminstratör 1 | Intygsbeställning)');
        cy.get('ib-header-user #ib-header-user-name').should('have.text', 'Ingbritt Filt');
        cy.get('ib-header-user #ib-header-user-role').contains('FMU Vårdadministratör');

        cy.get('ib-header-actions #settingsLinkBtn').should('be.visible');
        cy.get('ib-header-actions #aboutLinkBtn').should('be.visible');
        cy.get('ib-header-actions #logoutLinkBtn').should('be.visible');
    });

});