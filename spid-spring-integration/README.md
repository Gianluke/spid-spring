# spid-spring-integration

Si tratta di una libreria sviluppata utilizzando le funzionalità di OpenSAML v3 che agevola eventuali applicazioni basate su Spring nel compito di aderire al circuito SPID e quindi di consentire ai propri utenti di autenticarsi usando le proprie credenziali SPID. La libreria è costituita da un file JAR utilizzabile come dipendenza in un'applicazione Spring strutturata come segue:

 - la libreria espone le sue funzionalità attraverso i metodi pubblici di un'interfaccia SPIDIntegrationService:
 - `List<IdpEntry> getAllIdpEntry() throws IntegrationServiceException;`
 - `AuthRequest buildAuthenticationRequest(String entityId, int assertionConsumerServiceIndex) throws IntegrationServiceException;`
