# spid-spring

Questo progetto rappresenta la risposta alla issue ["Sviluppo estensione Java Spring per SPID #1"](https://github.com/italia/spid-spring/issues/1) del team hack.developers 2017 di Lecce.

Il codice sorgente è suddiviso nei due seguenti moduli maven.
1. **spid-spring-integration** è una libreria JAR che fornisce un supporto alle web application Spring che hanno la necessità di integrarsi in single sign-on con un Identity Provider SPID.
2. **spid-spring-rest** è una applicazione Spring Boot che funge da proof of concept dell'estensione Java Spring per SPID implementata.

Essa espone i servizi per un ipotetico client che consentono di conoscere la lista degli Identity Provider ufficiali, di produrre una request per uno specifico Identity Provider e di ottenere facilmente il contenuto dell response finale inviata dall'Identity Provider.

