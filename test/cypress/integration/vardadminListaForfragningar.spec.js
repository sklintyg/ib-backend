describe('Vårdadmin lista förfrågningar', function() {

    it('har 2st förfrågningar', function() {
        cy.login('Ingbritt Filt (Vårdadminstratör 1 | Intygsbeställning)');

        cy.get('#vardadmin-lista-forfragningar-resulttext').should('have.text', 'Sökresultat: 2 av 2 förfrågningar');

        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(1) td:nth-child(1)').should('have.text', 'utredning-bootstrap-3');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(1) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(1) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(1) td:nth-child(5)').should('contain', '2018-04-26');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(1) td:nth-child(7) .status-kraver-atgard').should('not.be.visible');

        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(2) td:nth-child(1)').should('have.text', 'utredning-bootstrap-2');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(2) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(2) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(2) td:nth-child(5)').should('contain', '2018-04-25');
        cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(2) td:nth-child(7) .status-kraver-atgard').should('not.be.visible');

        cy.get('#vardadmin-lista-forfragningar-no-results').should('not.be.visible');
    });

    it('har inga förfrågninar', function() {
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

        cy.get('#vardadmin-lista-forfragningar-no-results').should('be.visible');
    });
});