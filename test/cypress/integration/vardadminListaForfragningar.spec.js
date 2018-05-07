import moment from 'moment'

describe('Vårdadmin lista förfrågningar', function() {

    before(() => {
        cy.deleteUtredningarForVardgivareId('ostergotland');
    });

    beforeEach(() => {
        cy.fixture('utredningar/utredning-1.json').as('utredning1');
    });

    function getCell(r,c,selector) {
        if (!selector) {
            selector = '';
        }
        return cy.get('#vardadmin-lista-forfragningar-table tr:nth-child(' + r + ') td:nth-child(' + c + ') ' + selector);
    }

    it('har 3st förfrågningar', function() {

        this.utredning1.externForfragan.landstingHsaId = 'ostergotland';
        cy.createUtredning(this.utredning1);

        var utredning2 = JSON.parse(JSON.stringify(this.utredning1));
        utredning2.utredningId = 'utredning-cypress-2';
        utredning2.externForfragan.internForfraganList[0].vardenhetHsaId = 'linkoping';
        var date2 = moment().add(1, 'days');
        utredning2.externForfragan.internForfraganList[0].besvarasSenastDatum = date2;
        cy.createUtredning(utredning2);

        var utredning3 = JSON.parse(JSON.stringify(utredning2));
        utredning3.utredningId = 'utredning-cypress-3';
        utredning3.externForfragan.internForfraganList[0].tilldeladDatum = null;
        var date3 = moment().add(10, 'days');
        utredning3.externForfragan.internForfraganList[0].besvarasSenastDatum = date3;
        cy.createUtredning(utredning3);

        var utredning4 = JSON.parse(JSON.stringify(utredning3));
        utredning4.utredningId = 'utredning-cypress-4';
        utredning4.externForfragan.internForfraganList[0].forfraganSvar = null;
        var date4 = moment().subtract(10, 'days');
        utredning4.externForfragan.internForfraganList[0].besvarasSenastDatum = date4;
        cy.createUtredning(utredning4);

        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

        cy.get('#vardadmin-lista-forfragningar-resulttext').should('have.text', 'Sökresultat: 3 av 3 förfrågningar');

        getCell(1, 1).should('have.text', 'utredning-cypress-3');
        getCell(1, 2).should('have.text', 'AFU');
        getCell(1, 3).should('have.text', 'Landstinget Östergötland');
        getCell(1, 4).should('contain', '2018-04-26');
        getCell(1, 5, '.svarsdatum-paminnelse').should('not.be.visible');
        getCell(1, 5, '.svarsdatum-passed').should('not.be.visible');
        getCell(1, 5).should('contain', date3.format('YYYY-MM-DD'));
        getCell(1, 6).should('contain', '2018-12-12');
        getCell(1, 7, '.status-kraver-atgard').should('not.be.visible');
        getCell(1, 7).should('contain', 'Accepterad, väntar på tilldelningsbeslut');

        getCell(2, 1).should('have.text', 'utredning-cypress-2');
        getCell(2, 2).should('have.text', 'AFU');
        getCell(2, 3).should('have.text', 'Landstinget Östergötland');
        getCell(2, 4).should('contain', '2018-04-26');
        getCell(2, 5, '.svarsdatum-paminnelse').should('be.visible');
        getCell(2, 5, '.svarsdatum-passed').should('not.be.visible');
        getCell(2, 5).should('contain', date2.format('YYYY-MM-DD'));
        getCell(2, 6).should('contain', '2018-12-12');
        getCell(2, 7, '.status-kraver-atgard').should('not.be.visible');
        getCell(2, 7).should('contain', 'Tilldelad, väntar på beställning');

        getCell(3, 1).should('have.text', 'utredning-cypress-4');
        getCell(3, 2).should('have.text', 'AFU');
        getCell(3, 3).should('have.text', 'Landstinget Östergötland');
        getCell(3, 4).should('contain', '2018-04-26');
        getCell(3, 5, '.svarsdatum-paminnelse').should('not.be.visible');
        getCell(3, 5, '.svarsdatum-passed').should('be.visible');
        getCell(3, 5).should('contain', date4.format('YYYY-MM-DD'));
//        getCell(3, 6).should('contain', '2018-12-12');
        getCell(3, 7, '.status-kraver-atgard').should('be.visible');
        getCell(3, 7).should('contain', 'Inkommen');

        cy.get('#vardadmin-lista-forfragningar-no-results').should('not.be.visible');
    });
/*
    it('har inga förfrågningar', function() {

        cy.deleteUtredningarForVardgivareId('ostergotland');

        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'linkoping');

        cy.get('#vardadmin-lista-forfragningar-no-results').should('be.visible');
    });*/
});