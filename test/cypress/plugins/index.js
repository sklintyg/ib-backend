// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)

const enviroments = {
	"ip30" : {
		"baseUrl": "https://ib-backend-route-test-intyg.app-ocpsbx1-ind.ocp.osl.basefarm.net",
		"env": {
			"host":"ip30",
			"requestPerformerForAssessment": "/services/request-performer-for-assessment-responder"
		}
	},
	"ip40" : {
		"baseUrl": "placeholder"
	},
	"dev" : {
		"baseUrl": "http://localhost:8991",
		"requestPerformerForAssessment": "/services/request-performer-for-assessment-responder"
	},
	"demo" : {
		"baseUrl": "placeholder"
	}	
}
const cucumber = require('cypress-cucumber-preprocessor').default
module.exports = (on, config) => { 
	on('file:preprocessor', cucumber())
  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
	const env = config.env.host || 'dev'
	// modify config values
	config.baseUrl = enviroments[env].baseUrl
	config.env = enviroments[env].env
	return config
}
