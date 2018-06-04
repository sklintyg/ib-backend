Cypress.Commands.add("deleteNotificationSettingsForHsaId", (hsaId) => {
    return cy.request('DELETE', '/api/test/notificationpreferences/' + hsaId)
});