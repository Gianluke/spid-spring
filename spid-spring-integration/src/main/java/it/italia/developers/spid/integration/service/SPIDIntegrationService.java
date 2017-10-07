package it.italia.developers.spid.integration.service;

import java.util.List;
import java.util.Set;

import org.opensaml.saml2.core.AuthnRequest;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.model.ResponseDecoded;

/**
 * @author Gianluca Pindinelli
 *
 */
public interface SPIDIntegrationService {

	/**
	 * Costruisce l'oggetto Saml2 AuthNRequest.
	 *
	 * @param assertionConsumerServiceUrl
	 * @param issuerId
	 * @param destination
	 * @return
	 */
	AuthnRequest buildAuthenticationRequest(String assertionConsumerServiceUrl, String issuerId, String destination) throws IntegrationServiceException;

	/**
	 * Costruisce l'oggetto Saml2 AuthNRequest a partire dall'entityID dell'i-esimo IDP.
	 *
	 * @param entityId
	 * @return
	 */
	AuthRequest buildAuthenticationRequest(String entityId) throws IntegrationServiceException;

	/**
	 * Carica la lista di tutti gli IDP presenti.
	 *
	 * @return
	 * @throws IntegrationServiceException
	 */
	List<IdpEntry> getAllIdpEntry() throws IntegrationServiceException;

	/**
	 * Costruisce la risposta decodificata a partire dall'auth response SAML2.
	 *
	 * @param authResponse
	 * @return
	 * @throws IntegrationServiceException
	 */
	ResponseDecoded processAuthenticationResponse(String authResponse) throws IntegrationServiceException;

}
