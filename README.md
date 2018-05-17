# Intygsbeställning

## Komma igång med lokal installation
Den här sektionen beskriver hur man bygger applikationen för att kunna köras helt fristående.

Vi använder Gradle för att bygga applikationerna.

Börja med att skapa en lokal klon av källkodsrepositoryt:

    $ git clone https://github.com/sklintyg/ib-backend.git

Efter att man har klonat repository navigera till den klonade katalogen ib-backend och kör följande kommando:

    $ cd ib-backend
    $ ./gradlew build

Det här kommandot kommer att bygga samtliga moduler i systemet. 

När applikationen har byggt klart, kan man gå till `/web` och köra kommandot

    $ cd ib-backend
    $ ./gradlew appRun

för att starta applikationen lokalt.

Nu går det att öppna en webbläsare och surfa till 

    http://localhost:8990/welcome.html 

Observera jetty körs i gradleprocessen, så gradle "blir inte klar" förrän du stoppar servern med ^c, och applikationen är bara igång fram till dess.

För att starta applikationen i debugläge används:

    $ cd ib-backend
    $ ./gradlew appRunDebug
    
Applikationen kommer då att starta upp med debugPort = **5010**. Det är denna port du ska använda när du sätter upp din 
debug-konfiguration i din utvecklingsmiljö.

För att testa applikationen i ett mer prodlikt läge kan man även starta med en flagga för att köra i minifierat läge då css/js är packade och sammanslagna genom att starta:

    $ cd ib-backend
    $ ./gradlew clean appRunWar 

### Köra integrationstester
Vi har integrationstester skrivna med [REST-Assured](https://github.com/jayway/rest-assured)

De körs inte automatiskt vid bygge av applikationen utan man behöver köra dem med kommandot

    $ cd ib-backend
    $ ./gradlew restassured
    
Man kan exekvera enskilda tester genom exempelvis:

    $ ./gradlew restassured --tests *RequestMedicalCertificateSupplementIT.requestRequestMedicalCertificateSupplementWorks
    
För att debugga själva testet, lägg på --debug-jvm dvs:

    $ ./gradlew restassured --debug-jvm
    ... loggar ...
    Listening for transport dt_socket at address: 5005

Anslut nu remote debugging i IDEA.

För att logga från testet till stdout:

    $ ./gradlew restassured -info
