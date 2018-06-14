import moment from 'moment'

describe('Samordnare lista utredningar', function() {

    var landstingHsaId = 'IFV1239877878-1041';

    before(() => {
        cy.deleteUtredningarForVardgivareId(landstingHsaId)
    });

    beforeEach(() => {
        cy.fixture('utredningar/utredning-1.json').as('utredning1');
    });


    var utredningsId1, utredningsId2, utredningsId3;

    var date0,date1,date2,date3;

    it('Skapa utredningar', function() {

        date0 = moment().add(30, 'days');
        this.utredning1.externForfragan.besvarasSenastDatum = date0;
        cy.createUtredning(this.utredning1);

        date1 = moment().add(1, 'days').format('YYYY-MM-DD');
        cy.requestHealthPerformerAssessment({
            utredningsTyp: 'AFU',
            besvaraSenastDatum: date1.replace(/-/g,''),
            landstingHsaId: landstingHsaId,
            invanare: {
                ort: 'hemma'
            },
            bestallare: {
                telefon: '123465789'
            }
        }).then((data) => {
            utredningsId1 = data.assessmentId;
        });

        date2 = moment().add(20, 'days').format('YYYY-MM-DD');
        cy.requestHealthPerformerAssessment({
            utredningsTyp: 'AFU_UTVIDGAD',
            besvaraSenastDatum: date2.replace(/-/g,''),
            landstingHsaId: landstingHsaId,
            invanare: {
                ort: 'hemma'
            },
            bestallare: {
                telefon: '123465789'
            }
        }).then((data) => {
            utredningsId2 = data.assessmentId;
        });

        date3 = moment().add(35, 'days').format('YYYY-MM-DD');
        cy.requestHealthPerformerAssessment({
            utredningsTyp: 'AFU',
            besvaraSenastDatum: date3.replace(/-/g,''),
            landstingHsaId: landstingHsaId,
            invanare: {
                ort: 'hemma'
            },
            bestallare: {
                telefon: '123465789'
            }
        }).then((data) => {
            utredningsId3 = data.assessmentId;
        });
    });

    it('har 4st utredningar', function() {
        cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');

        cy.get('#samordnare-lista-utredningar-resulttext').should('have.text', 'Sökresultat: 4 av 4 beställningar');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(1)').should('have.text', utredningsId3);
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(2)').should('have.text', 'AFU');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(3)').should('have.text', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(4)').should('have.text', 'Förfrågan');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(5)').should('contain', date3);
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(6) .status-kraver-atgard').should('be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(1) td:nth-child(6)').should('contain', 'Förfrågan inkommen');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(2)').should('have.text', 'AFU');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(3)').should('have.text', 'WebCert-Enhet1');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(4)').should('have.text', 'Förfrågan');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(5)').should('contain', date0.format('YYYY-MM-DD'));
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(6) .status-kraver-atgard').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(2) td:nth-child(6)').should('contain', 'Tilldelad, väntar på beställning');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(1)').should('have.text', utredningsId2);
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(2)').should('have.text', 'AFU_UTVIDGAD');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(3)').should('have.text', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(4)').should('have.text', 'Förfrågan');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(5) .slutdatum-paminnelse').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(5)').should('contain', date2);
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(6) .status-kraver-atgard').should('be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(3) td:nth-child(6)').should('contain', 'Förfrågan inkommen');

        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(1)').should('have.text', utredningsId1);
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(2)').should('have.text', 'AFU');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(3)').should('have.text', '');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(4)').should('have.text', 'Förfrågan');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(5) .slutdatum-paminnelse').should('be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(5) .slutdatum-passed').should('not.be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(5)').should('contain', date1);
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(6) .status-kraver-atgard').should('be.visible');
        cy.get('#samordnare-lista-utredningar-table tr:nth-child(4) td:nth-child(6)').should('contain', 'Förfrågan inkommen');

        cy.get('#samordnare-lista-utredningar-no-results').should('not.be.visible');
    });

    it('filter', function() {
        cy.login('Gunnel Grävling (Samordnare 2 | Intygsbeställning)');

        cy.get('#filterFritext-input').type("webcert-enhet");
        cy.get('#samordnare-lista-utredningar-resulttext').should('have.text', 'Sökresultat: 1 av 1 beställningar');
        cy.get('#samordnare-lista-utredningar-no-results').should('not.be.visible');

        cy.get('#filterFritext-input').type("finns ej");
        cy.get('#samordnare-lista-utredningar-resulttext').should('have.text', 'Sökresultat: 0 av 0 beställningar');
        cy.get('#samordnare-lista-utredningar-no-results').should('be.visible');
    });

    it('har inga utredningar', function() {
        cy.login('Harald Alltsson (Alla roller | Intygsbeställning)', 'kronoberg');

        cy.get('#samordnare-lista-utredningar-no-results').should('be.visible');
    });

});
