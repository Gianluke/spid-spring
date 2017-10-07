package it.italia.developers.spid.integration.service;

import org.opensaml.saml2.core.AuthnRequest;

/**
 * @author Gianluca Pindinelli
 *
 */
public interface AuthNRequestService {

	/**
	 * Build Saml2 AuthNRequest
	 *
	 * @param assertionConsumerServiceUrl
	 * @param issuerId
	 * @param destination
	 * @return
	 */
	AuthnRequest buildAuthenticationRequest(String assertionConsumerServiceUrl, String issuerId, String destination);

}
