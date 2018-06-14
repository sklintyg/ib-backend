Feature: FMU-AF02

  Vi kontrollerar AF02 flödet

Scenario: Direkt tilldelning - [Förfrågan till accept]
	#Förfrågan
	Given att Försäkringskassan har skickat en förfrågan AFU till samordnare 
	When jag är inloggad som samordnare på Intygsbeställningen
	Then ska förfrågans status vara "Tilldela utredning"
	
	#When jag tilldelar förfrågan 
	#Then ska förfrågans status vara "Direkt tilldelad"
	
	#Given att en vårdadmin är inloggad 
	#When vårdadmin accepterar förfrågan
	#Then ska förfrågans status vara "Direkt tilldelad"
	#Then ska Försäkringskassan notifieras att förfrågan är accepterad