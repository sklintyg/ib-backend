Feature: FMU-AF02

  Vi kontrollerar AF02 flödet

Scenario: Normal tilldelning - [Förfrågan till accept]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare
	Then ska förfrågans status vara "Förfrågan inkommen" för "samordnare"
	
	When samordnare tilldelar förfrågan till enhet "WebCert-Enhet1"
	Then ska förfrågans status vara "Inkommen" för "vårdadmin"
	
	When "vårdadmin" accepterar förfrågan
	Then ska förfrågans status vara "Tilldelad, väntar på beställning" för "samordnare"
	Then ska Försäkringskassan notifieras att vårdenheten "WebCert-Enhet1" "ACCEPTERAT" förfrågan

Scenario: Direkt tilldelning - [Förfrågan till accept]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	Then ska förfrågans status vara "Förfrågan inkommen" för "samordnare"
	
	When samordnare direkttilldelar förfrågan till enhet "WebCert-Enhet1"
	Then ska förfrågans status vara "Tilldela utredning" för "samordnare"
	Then ska förfrågans status vara "Direkttilldelad" för "vårdadmin"
	
	When "samordnare" accepterar förfrågan
	Then ska förfrågans status vara "Tilldelad, väntar på beställning" för "vårdadmin"
	Then ska Försäkringskassan notifieras att vårdenheten "WebCert-Enhet1" "ACCEPTERAT" förfrågan

Scenario: Samordnare avvisar förfrågan direkt - [Förfrågan till reject]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	When "samordnare" avvisar förfrågan
	Then ska Försäkringskassan notifieras att "vården" "AVVISAT" förfrågan

Scenario: Samordnare avvisar direkttilldelad förfrågan - [Förfrågan till reject]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	When samordnare direkttilldelar förfrågan till enhet "WebCert-Enhet1"
	When "samordnare" avvisar förfrågan
	Then ska Försäkringskassan notifieras att "vården" "AVVISAT" förfrågan

Scenario: Vårdadmin och samordnare avvisar en tilldelad förfrågan - [Förfrågan till reject]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	When samordnare tilldelar förfrågan till enhet "WebCert-Enhet1"
	When "vårdadmin" avvisar förfrågan
	When "samordnare" avvisar förfrågan
	Then ska Försäkringskassan notifieras att "vården" "AVVISAT" förfrågan

Scenario: Försäkringskassan avbryter tilldelad utredning
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare
	When samordnare tilldelar förfrågan till enhet "WebCert-Enhet1"
	When "vårdadmin" accepterar förfrågan	
	When Försäkringskassan avbryter förfrågan med anledning "INGEN_BESTALLNING"
	Then ska förfrågans status vara "Avbruten" för "samordnare"