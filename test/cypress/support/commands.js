// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add("login", (loginId, unitId) => {
    cy.visit('/welcome.html');
    cy.get('#jsonSelect').select(loginId);
    cy.get('#loginBtn').click();
    cy.window().should('have.property', 'disableAnimations').then((disableAnimations) => {
        disableAnimations();
    });
    if (unitId) {
        cy.get('#ib-vardenhet-selector-select-active-unit-' + unitId + '-link').click();
    }
    cy.get('#cookie-usage-consent-btn').click();
    cy.get('#cookie-usage-consent-btn').should('not.be.visible');

});