
Cypress.Commands.add("createUtredning", (utredning) => {
    return cy.request('POST', '/api/test/utredningar/', utredning)
});

Cypress.Commands.add("deleteUtredning", (utredningId) => {
    return cy.request('DELETE', '/api/test/utredningar/' + utredningId)
});

Cypress.Commands.add("deleteUtredningarForVardgivareId", (vardgivareId) => {
    return cy.request('DELETE', '/api/test/utredningar/vardgivare/' + vardgivareId)
});