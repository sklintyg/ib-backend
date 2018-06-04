describe('Vårdadmin ange standardinställningar', function() {
    beforeEach(() => {
        cy.fixture('appheader/updated-enhets-settings-linkoping.json').as('updatedSettings');
});


    describe('Landstingssamordnare notifieringsinställningar', function() {
        const saveButtonId = '#save-unit-settings-btn';

        it('Landstingssamordnare skall kunna spara notifieringsinställningar för landstinget', function() {

            cy.deleteNotificationSettingsForHsaId('IFV1239877878-1043');

            //Logga in som en vårdadministratör med användare som har flera roller/enheter
            cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');

            cy.visaNotifieringInstallningModal('vg');

            cy.get('#notification-item-NY_EXTERNFORFRAGAN').should('be.checked');
            cy.get('#notification-item-NY_EXTERNFORFRAGAN').click();
            cy.get(saveButtonId).should('be.enabled');
            cy.get('#epost_input').type('{selectall}invalidemail');
            cy.get(saveButtonId).should('not.be.enabled');
            cy.get('#epost_input').type('{selectall}example@example.com');

            cy.get(saveButtonId).should('be.enabled');
            cy.get(saveButtonId).click();




            cy.visaNotifieringInstallningModal('vg');
            cy.get('#notification-item-NY_EXTERNFORFRAGAN').should('not.be.checked');
            cy.get('#epost_input').should('have.value', 'example@example.com');


        });
    });
});