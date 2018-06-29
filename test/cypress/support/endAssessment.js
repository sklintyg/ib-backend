
import xml2js from 'xml2js'


Cypress.Commands.add("endAssessment", (requestData) => {


    var url = Cypress.env('endAssessment');
	console.log('!!url: ' + url);
    var body = createRequestBody(requestData);
	//var request = afuBody();
    return cy.request('POST', url, body).then((response) => {
        cy.log(response.body);
        assert.equal(response.status, 200);
        return new Cypress.Promise((resolve, reject) => {
            xml2js.parseString(response.body, function (err, result) {
                if (err) {
                    reject(err);
                }
                else {
                    var response = result['soap:Envelope']['soap:Body'][0]['ns2:EndAssessmentResponse'][0];
                    assert.equal(response['ns2:result'][0].resultCode, 'OK');
                    resolve();
                }
            });
        });
    });

});

function shuffle(a) {
    var j, x, i;
    for (i = a.length - 1; i > 0; i--) {
        j = Math.floor(Math.random() * (i + 1));
        x = a[i];
        a[i] = a[j];
        a[j] = x;
    }
    return a;
}


function createRequestBody(requestData) {

    if (!requestData.endingCondition) {
        requestData.endingCondition = shuffle(['JAV', 'INGEN_BESTALLNING', 'UTREDNING_AVBRUTEN'])[0];
    }

    var request = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:riv:itintegration:registry:1" xmlns:urn1="urn:riv-application:intygsbestallning:certificate:order:EndAssessmentResponder:1" xmlns:urn2="urn:riv-application:intygsbestallning:certificate:order:1">
   <soapenv:Header>
      <urn:LogicalAddress>TSTNMT2321000156-B4T</urn:LogicalAddress>
   </soapenv:Header>
   <soapenv:Body>
      <urn1:EndAssessment>
         <urn1:assessmentId>
            <urn2:root>` + requestData.assessmentIdRoot + `</urn2:root>
            <urn2:extension>` + requestData.assessmentId + `</urn2:extension>
         </urn1:assessmentId>
         <urn1:endingCondition>
            <urn2:code>` + requestData.endingCondition + `</urn2:code>
            <urn2:codeSystem>c9017f72-4231-40b7-8999-a3a3c09a6780</urn2:codeSystem>
         </urn1:endingCondition>
         
      </urn1:EndAssessment>
   </soapenv:Body>
</soapenv:Envelope>`;

    return request;
}