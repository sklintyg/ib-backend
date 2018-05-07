
import xml2js from 'xml2js'

Cypress.Commands.add("requestHealthPerformerAssesment", (requestData) => {

    var url = 'http://localhost:8991/services/request-healthcare-performer-for-assessment-responder';
    var request = createRequest(requestData);

    return cy.request('POST', url, request).then((response) => {
        cy.log(response.body);
        assert.equal(response.status, 200);
        return new Cypress.Promise((resolve, reject) => {
            xml2js.parseString(response.body, function (err, result) {
                if (err) {
                    reject(err);
                }
                else {
                    var response = result['soap:Envelope']['soap:Body'][0]['ns2:RequestHealthcarePerformerForAssessmentResponse'][0];
                    assert.equal(response['ns2:result'][0].resultCode, 'OK');
                    var assessmentId = response['ns2:assessmentId'][0].extension[0];
                    cy.log(assessmentId);
                    resolve(cy.wrap({'assessmentId':assessmentId}));
                }
            });
        });
    });

});

function createRequest(requestData) {
    var request = '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:riv:itintegration:registry:1" xmlns:urn1="urn:riv-application:intygsbestallning:certificate:order:RequestHealthcarePerformerForAssessmentResponder:1" xmlns:urn2="urn:riv-application:intygsbestallning:certificate:order:1">' +
        '<soapenv:Header>' +
            '<urn:LogicalAddress>?</urn:LogicalAddress>' +
        '</soapenv:Header>' +
            '<soapenv:Body>' +
                '<urn1:RequestHealthcarePerformerForAssessment>' +
                    '<urn1:certificateType>' +
                        '<urn2:code>' + requestData.utredningsTyp + '</urn2:code>' +
                        '<urn2:codeSystem></urn2:codeSystem>' +
                    '</urn1:certificateType>' +
                    '<urn1:lastResponseDate>' + requestData.besvaraSenastDatum + '</urn1:lastResponseDate>' +
                    '<urn1:coordinatingCountyCouncilId>' +
                        '<urn2:root></urn2:root>' +
                        '<urn2:extension>' + requestData.landstingHsaId + '</urn2:extension>' +
                    '</urn1:coordinatingCountyCouncilId>' +
/*                    // <!--Optional:-->
                    '<urn1:comment>' + requestData.kommentar + '</urn1:comment>' +*/
/*                    // <!--Optional:-->
                    '<urn1:needForInterpreter>' + requestData.behovAvTolk + '</urn1:needForInterpreter>' +
                    // <!--Optional:-->
                    '<urn1:interpreterLanguage>' +
                        '<urn2:code>?</urn2:code>' +
                        '<urn2:codeSystem>?</urn2:codeSystem>' +
                        // <!--Optional:-->
                        '<urn2:codeSystemName>?</urn2:codeSystemName>' +
                        // <!--Optional:-->
                        '<urn2:codeSystemVersion>?</urn2:codeSystemVersion>' +
                        // <!--Optional:-->
                        '<urn2:displayName>?</urn2:displayName>' +
                    '</urn1:interpreterLanguage>' +*/
                    '<urn1:authorityAdministrativeOfficial>' +
/*                        // <!--Optional:-->
                        '<urn2:fullName>?</urn2:fullName>' +*/
                        '<urn2:phoneNumber>' + requestData.bestallare.telefon + '</urn2:phoneNumber>' +
/*                        // <!--Optional:-->
                        '<urn2:email>?</urn2:email>' +*/
                        '<urn2:authority>' +
                            '<urn2:code>FKASSA</urn2:code>' +
                            '<urn2:codeSystem>769bb12b-bd9f-4203-a5cd-fd14f2eb3b80</urn2:codeSystem>' +
/*                            // <!--Optional:-->
                            '<urn2:codeSystemName>?</urn2:codeSystemName>' +
                            // <!--Optional:-->
                            '<urn2:codeSystemVersion>?</urn2:codeSystemVersion>' +
                            // <!--Optional:-->
                            '<urn2:displayName>?</urn2:displayName>' +*/
                        '</urn2:authority>' +
/*                        // <!--Optional:-->
                        '<urn2:officeName>?</urn2:officeName>' +
                        // <!--Optional:-->
                        '<urn2:officeCostCenter>?</urn2:officeCostCenter>' +
                        // <!--Optional:-->
                        '<urn2:officeAddress>' +
                            // <!--Optional:-->
                            '<urn2:postalAddress>?</urn2:postalAddress>' +
                            // <!--Optional:-->
                            '<urn2:postalCode>?</urn2:postalCode>' +
                            // <!--Optional:-->
                            '<urn2:postalCity>?</urn2:postalCity>' +
                        '</urn2:officeAddress>' +*/
                    '</urn1:authorityAdministrativeOfficial>' +
                    '<urn1:citizen>' +
                        '<urn2:postalCity>' + requestData.invanare.ort + '</urn2:postalCity>' +
/*                        // <!--Optional:-->
                        '<urn2:specialNeeds>?</urn2:specialNeeds>' +
                        '<!--Zero or more repetitions:-->' +
                        '<urn2:earlierAssessmentPerformer>' +
                            '<urn2:root>?</urn2:root>' +
                            '<urn2:extension>?</urn2:extension>' +
                        '</urn2:earlierAssessmentPerformer>' +*/
                    '</urn1:citizen>' +
                '</urn1:RequestHealthcarePerformerForAssessment>' +
            '</soapenv:Body>' +
        '</soapenv:Envelope>';

    return request;
}
