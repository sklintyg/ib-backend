describe('Vårdadmin ange inställningar underleverantör', function() {
    beforeEach(() => {
        cy.fixture('appheader/updated-enhets-settings-linkoping.json').as('updatedSettings');
});

    describe('Vårdadmin  inställningar underleverantör', function() {
        it('vårdadmin skall kunna ändra  inställningar för underleverantör', function() {

            //Logga in som en vårdadministratör med användare som har flera roller/enheter
            cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

            cy.visaVEUnderleverantorKontaktInstallningModal();

            cy.get('#mottagarNamn-input').type('{selectall}' + this.updatedSettings.mottagarNamn);
            cy.get('#adress-input').type('{selectall}' + this.updatedSettings.adress);
            cy.get('#postnummer-input').type('{selectall}' + this.updatedSettings.postnummer);
            cy.get('#postort-input').type('{selectall}' + this.updatedSettings.postort);
            cy.get('#telefonnummer-input').type('{selectall}' + this.updatedSettings.telefonnummer);
            cy.get('#epost-input').type('{selectall}{backspace}' + this.updatedSettings.epost).blur();


            cy.get('#save-unit-settings-btn').click();
            cy.get('#save-unit-settings-btn').should('not.be.visible');

            cy.bytEnhet('IFV1239877878-1042');

            cy.visaVEUnderleverantorKontaktInstallningModal();
            cy.get('.ib-header-unit-settings-dialog-template').should('be.visible');
            cy.get('#mottagarNamn-input').should('not.have.text', this.updatedSettings.mottagarNamn);
            cy.get('#cancel-unit-settings-btn').click();

            //Byt tillbaka till första enheten
            cy.bytEnhet('linkoping');

            cy.visaVEUnderleverantorKontaktInstallningModal();
            cy.get('#mottagarNamn-input').should('be.visible');
            cy.get('#mottagarNamn-input').should('have.value', this.updatedSettings.mottagarNamn);
            cy.get('#adress-input').should('have.value', this.updatedSettings.adress);
            cy.get('#postnummer-input').should('have.value', this.updatedSettings.postnummer);
            cy.get('#postort-input').should('have.value', this.updatedSettings.postort);
            cy.get('#telefonnummer-input').should('have.value', this.updatedSettings.telefonnummer);
            cy.get('#epost-input').should('have.value', this.updatedSettings.epost);

        });
    });



});