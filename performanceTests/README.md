# Intygsbeställningar prestandatest

## Målmiljö

## Konfiguration och loggning
Om man vill finjustera hur gatling beter sig så finns följande konfigurationsfiler:

- src/test/resources/gatling.conf
- src/test/resources/logback-test.xml

Det man ofta vill komma åt är felloggar när ens tester börjar spruta ur sig 500 Server Error eller påstår att de inte kan parsa ut saker ur svaren. Öppna då logback-test.xml och kommentera in följande:

    <!-- Uncomment for logging ALL HTTP request and responses  -->
    <!-- <logger name="io.gatling.http" level="TRACE" />    -->
    <!-- Uncomment for logging ONLY FAILED HTTP request and responses -->
    <!-- <logger name="io.gatling.http" level="DEBUG" /> -->    
 
Som framgår ovan så kan man slå på antingen all HTTP eller enbart failade request/responses. Ovärderligt då Gatling inte ger särskilt mycket hjälp annat än HTTP Status när något går fel på servern. 

## Hur startar jag en simulering

### Välj målmiljö
Ett alternativ är att öppna build.gradle och redigera certificate.baseUrl i ext-blocket

- "http://localhost:8990"

Alternativt kan man ange -Dcertificate.baseUrl=....... på kommandoraden.

### Exekvering från command-lineß

Från command-line fungerar följande, för att köra ett specifikt scenario:

  gradle gatlingSingleTest -DgatlingSimulation=[NAMN_PÅ_TESTKLASSEN]
  
För att köra alla simulationer:

  gradle gatling


## Hur följer jag upp utfallet?
Medan testet kör skriver Gatling ut lite progress-info på command-line men den ger ganska rudimentär information. Det intressanta är att titta på rapporterna som genereras efter att testerna slutförts. Dessa finns under mappen:

/results

Varje körning hamnar i en egen mapp och har en index.html-fil där utfallet av simulationen redovisas.