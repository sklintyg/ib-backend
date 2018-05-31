describe('Vårdadmin ange standardinställningar', function() {
    beforeEach(() => {
        cy.fixture('appheader/updated-enhets-settings-linkoping.json').as('updatedSettings');
    });


    it('vårdadmin skall kunna spara kontaktsdressinställningar för enheten', function() {

        //Logga in som en vårdadministratör med användare som har flera roller/enheter
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

       cy.visaVEKontaktInstallningModal();

        cy.get('#mottagarNamn-input').type('{selectall}' + this.updatedSettings.mottagarNamn);
        cy.get('#adress-input').type('{selectall}' + this.updatedSettings.adress);
        cy.get('#postnummer-input').type('{selectall}' + this.updatedSettings.postnummer);
        cy.get('#postort-input').type('{selectall}' + this.updatedSettings.postort);
        cy.get('#telefonnummer-input').type('{selectall}' + this.updatedSettings.telefonnummer);
        cy.get('#epost-input').type('{selectall}' + this.updatedSettings.epost);


        cy.get('#save-unit-settings-btn').click();
        cy.get('#save-unit-settings-btn').should('not.be.visible');

        cy.bytEnhet('IFV1239877878-1042');

        cy.get('#expand-unitmenu-btn').click();
        cy.get('#unitmenu-ve-contact-settings-link').click();
        cy.get('.ib-header-unit-settings-dialog-template').should('be.visible');
        cy.get('#mottagarNamn-input').should('not.have.text', this.updatedSettings.mottagarNamn);
        cy.get('#cancel-unit-settings-btn').click();

        //Byt tillbaka till första enheten
        cy.bytEnhet('linkoping');

        cy.visaVEKontaktInstallningModal();
        cy.get('#mottagarNamn-input').should('be.visible');
        cy.get('#mottagarNamn-input').should('have.value', this.updatedSettings.mottagarNamn);
        cy.get('#adress-input').should('have.value', this.updatedSettings.adress);
        cy.get('#postnummer-input').should('have.value', this.updatedSettings.postnummer);
        cy.get('#postort-input').should('have.value',  this.updatedSettings.postort);
        cy.get('#telefonnummer-input').should('have.value',  this.updatedSettings.telefonnummer);
        cy.get('#epost-input').should('have.value',  this.updatedSettings.epost);

    });


    it('vårdadmin skall kunna spara standardsvarinställningar för enheten', function() {

        const standardSvar = "Vi gör allt ni vill och lite till";

        //Logga in som en vårdadministratör med användare som har flera roller/enheter
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

        cy.visaVESvarInstallningModal();

        cy.get('#standardsvar-textarea').type('{selectall}' + standardSvar);


        cy.get('#save-unit-settings-btn').click();
        cy.get('#save-unit-settings-btn').should('not.be.visible');

        cy.bytEnhet('IFV1239877878-1042');

        cy.visaVESvarInstallningModal();
        cy.get('#standardsvar-textarea').should('not.have.text', standardSvar);
        cy.get('#cancel-unit-settings-btn').click();

        //Byt tillbaka till första enheten
        cy.bytEnhet('linkoping');

        cy.visaVESvarInstallningModal();

        cy.get('#standardsvar-textarea').should('have.value', standardSvar);

    });

});