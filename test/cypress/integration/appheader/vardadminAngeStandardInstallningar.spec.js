describe('Vårdadmin ange standardinställningar', function() {
    beforeEach(() => {
        cy.fixture('appheader/updated-enhets-settings-linkoping.json').as('updatedSettings');
    });


    it('vårdadmin skall kunna spara enhetsinställningar', function() {

        //Logga in som en vårdadministratör med användare som har flera roller/enheter
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

        cy.get('ib-header-actions #settingsLinkBtn').click();
        cy.get('#save-unit-settings-btn').should('be.visible');

        cy.get('#mottagarNamn-input').type('{selectall}' + this.updatedSettings.mottagarNamn);
        cy.get('#adress-input').type('{selectall}' + this.updatedSettings.adress);
        cy.get('#postnummer-input').type('{selectall}' + this.updatedSettings.postnummer);
        cy.get('#postort-input').type('{selectall}' + this.updatedSettings.postort);
        cy.get('#telefonnummer-input').type('{selectall}' + this.updatedSettings.telefonnummer);
        cy.get('#epost-input').type('{selectall}' + this.updatedSettings.epost);
        cy.get('#standardsvar-textarea').type('{selectall}' + this.updatedSettings.standardsvar);

        cy.get('#save-unit-settings-btn').click();
        cy.get('#save-unit-settings-btn').should('not.be.visible');

        cy.bytEnhet('IFV1239877878-1042');

        cy.get('ib-header-actions #settingsLinkBtn').click();
        cy.get('.ib-header-unit-settings-dialog-template').should('be.visible');
        cy.get('#mottagarNamn-input').should('not.have.text', this.updatedSettings.mottagarNamn);
        cy.get('#cancel-unit-settings-btn').click();

        cy.bytEnhet('linkoping');

        cy.get('ib-header-actions #settingsLinkBtn').click();
        cy.get('.ib-header-unit-settings-dialog-template').should('be.visible');
        cy.get('#mottagarNamn-input').should('be.visible');
        cy.get('#mottagarNamn-input').should('have.value', this.updatedSettings.mottagarNamn);
        cy.get('#adress-input').should('have.value', this.updatedSettings.adress);
        cy.get('#postnummer-input').should('have.value', this.updatedSettings.postnummer);
        cy.get('#postort-input').should('have.value',  this.updatedSettings.postort);
        cy.get('#telefonnummer-input').should('have.value',  this.updatedSettings.telefonnummer);
        cy.get('#epost-input').should('have.value',  this.updatedSettings.epost);
        cy.get('#standardsvar-textarea').should('have.value',  this.updatedSettings.standardsvar);

    });


});