Feature: FMU-AF02

  Vi kontrollerar AF02 flödet

Scenario: Normal tilldelning - [Förfrågan till accept]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	Then ska förfrågans status vara "Förfrågan inkommen" för "samordnare"
	
	When jag tilldelar förfrågan till enhet "WebCert-Enhet1"
	Then ska förfrågans status vara "Väntar på svar" för "samordnare"
	Then ska förfrågans status vara "Inkommen" för "vårdadmin"
	
	When "vårdadmin" accepterar förfrågan
	Then ska förfrågans status vara "Väntar på svar" för "samordnare"
	Then ska förfrågans status vara "Inkommen" för "vårdadmin"
	Then ska Försäkringskassan notifieras att vårdenheten "WebCert-Enhet1" "ACCEPTERAT" förfrågan

Scenario: Direkt tilldelning - [Förfrågan till accept]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	Then ska förfrågans status vara "Förfrågan inkommen" för "samordnare"
	
	When jag direkttilldelar förfrågan till enhet "WebCert-Enhet1"
	Then ska förfrågans status vara "Tilldela utredning" för "samordnare"
	Then ska förfrågans status vara "Direkttilldelad" för "vårdadmin"
	
	When "samordnare" accepterar förfrågan
	Then ska förfrågans status vara "Tilldelad, väntar på beställning" för "samordnare"
	Then ska förfrågans status vara "Tilldelad, väntar på beställning" för "vårdadmin"
	Then ska Försäkringskassan notifieras att vårdenheten "WebCert-Enhet1" "ACCEPTERAT" förfrågan

