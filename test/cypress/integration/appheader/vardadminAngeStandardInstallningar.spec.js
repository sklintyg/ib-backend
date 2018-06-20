describe('Vårdadmin ange standardinställningar', function() {
    beforeEach(() => {
        cy.fixture('appheader/updated-enhets-settings-linkoping.json').as('updatedSettings');
    });

    describe('Vårdadmin kontaktsdressinställningar', function() {
        it('vårdadmin skall kunna ändra kontaktsdressinställningar för enheten', function() {

            //Logga in som en vårdadministratör med användare som har flera roller/enheter
            cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

            cy.visaVEKontaktInstallningModal();

            cy.get('#mottagarNamn_input').type('{selectall}' + this.updatedSettings.mottagarNamn);
            cy.get('#adress_input').type('{selectall}' + this.updatedSettings.adress);
            cy.get('#postnummer_input').type('{selectall}' + this.updatedSettings.postnummer);
            cy.get('#postort_input').type('{selectall}' + this.updatedSettings.postort);
            cy.get('#telefonnummer_input').type('{selectall}' + this.updatedSettings.telefonnummer);
            cy.get('#epost_input').type('{selectall}{backspace}' + this.updatedSettings.epost).blur();


            cy.get('#save-unit-settings-btn').click();
            cy.get('#save-unit-settings-btn').should('not.be.visible');

            cy.bytEnhet('IFV1239877878-1042');

            cy.visaVEKontaktInstallningModal();
            cy.get('.ib-header-unit-settings-dialog-template').should('be.visible');
            cy.get('#mottagarNamn_input').should('not.have.text', this.updatedSettings.mottagarNamn);
            cy.get('#cancel-unit-settings-btn').click();

            //Byt tillbaka till första enheten
            cy.bytEnhet('linkoping');

            cy.visaVEKontaktInstallningModal();
            cy.get('#mottagarNamn_input').should('be.visible');
            cy.get('#mottagarNamn_input').should('have.value', this.updatedSettings.mottagarNamn);
            cy.get('#adress_input').should('have.value', this.updatedSettings.adress);
            cy.get('#postnummer_input').should('have.value', this.updatedSettings.postnummer);
            cy.get('#postort_input').should('have.value', this.updatedSettings.postort);
            cy.get('#telefonnummer_input').should('have.value', this.updatedSettings.telefonnummer);
            cy.get('#epost_input').should('have.value', this.updatedSettings.epost);

        });
    });

    describe('Vårdadmin standardsvarinställningar', function() {

        it('vårdadmin skall kunna ändra standardsvarinställningar för enheten', function() {

            const standardSvar = "Vi gör allt ni vill och lite till";

            //Logga in som en vårdadministratör med användare som har flera roller/enheter
            cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

            cy.visaVESvarInstallningModal();

            cy.get('#standardsvar_textarea').type('{selectall}' + standardSvar);


            cy.get('#save-unit-settings-btn').click();
            cy.get('#save-unit-settings-btn').should('not.be.visible');

            cy.bytEnhet('IFV1239877878-1042');

            cy.visaVESvarInstallningModal();
            cy.get('#standardsvar_textarea').should('not.have.text', standardSvar);
            cy.get('#cancel-unit-settings-btn').click();

            //Byt tillbaka till första enheten
            cy.bytEnhet('linkoping');

            cy.visaVESvarInstallningModal();

            cy.get('#standardsvar_textarea').should('have.value', standardSvar);

        });
    });

    describe('Vårdadmin notifieringsinställningar', function() {

        it('vårdadmin skall kunna spara notifieringsinställningar för enheten', function() {

            cy.deleteNotificationSettingsForHsaId('linkoping');
            
            //Logga in som en vårdadministratör med användare som har flera roller/enheter
            cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

            cy.visaNotifieringInstallningModal('ve');
            cy.get('#notification-item-NY_INTERNFORFRAGAN').should('be.checked');
            cy.get('#notification-item-NY_INTERNFORFRAGAN').click();

            cy.get('#save-unit-settings-btn').click();
            cy.get('#save-unit-settings-btn').should('not.be.visible');



            cy.visaNotifieringInstallningModal('ve');
            cy.get('#notification-item-NY_INTERNFORFRAGAN').should('not.be.checked');


        });
    });
});