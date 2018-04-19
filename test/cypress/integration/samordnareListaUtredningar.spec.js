describe('Samordnare lista utredningar', function() {

    it('har 3st utredningar', function() {
        cy.login('Simona Samordnare (Samordnare 1 | Intygsbeställning)');

        cy.get('#samordnare-lista-utredningar-resulttext').should('have.text', 'Sökresultat: 3 av 3 beställningar');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(1)').should('have.text', 'utredning-bootstrap-3');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(2)').should('have.text', 'AFS');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(3)').should('have.text', 'IFV1239877878-1041-namnet');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(4)').should('have.text', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(5)').should('contain', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(6) .status-kraver-atgard').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(6)').should('contain', '');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(1)').should('have.text', 'utredning-bootstrap-2');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(2)').should('have.text', 'AFS');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(3)').should('have.text', 'IFV1239877878-1041-namnet');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(4)').should('have.text', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(5)').should('contain', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(6) .status-kraver-atgard').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(6)').should('contain', '');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(1)').should('have.text', 'utredning-bootstrap-1');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(2)').should('have.text', 'AFS');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(3)').should('have.text', 'IFV1239877878-1041-namnet');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(4)').should('have.text', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(5)').should('contain', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(6) .status-kraver-atgard').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(6)').should('contain', '');

        cy.get('#samordnare-lista-utredningar-no-results').should('not.be.visible');
    });

    it('har inga utredningar', function() {
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'kronoberg');

        cy.get('#samordnare-lista-utredningar-no-results').should('be.visible');
    });
});