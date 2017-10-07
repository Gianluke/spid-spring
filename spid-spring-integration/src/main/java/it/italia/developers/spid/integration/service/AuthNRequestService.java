package it.italia.developers.spid.integration.service;

import java.util.Set;

import org.opensaml.saml2.core.AuthnRequest;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;

/**
 * @author Gianluca Pindinelli
 *
 */
public interface AuthNRequestService {

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
	Set<IdpEntry> getAllIdpEntry() throws IntegrationServiceException;

}
