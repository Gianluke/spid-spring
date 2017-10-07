package it.italia.developers.spid.integration.service.impl;

import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.springframework.stereotype.Service;

import it.italia.developers.spid.integration.service.AuthNRequestService;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class AuthNRequestServiceImple implements AuthNRequestService {

	private static final String SAML2_NAME_ID_POLICY = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
	private static final String SAML2_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
	private static final String SAML2_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT = "https://www.spid.gov.it/SpidL2";
	private static final String SAML2_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.italia.developers.spid.integration.service.AuthNRequestService#buildAuthenticationRequest(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	public AuthnRequest buildAuthenticationRequest(String assertionConsumerServiceUrl, String issuerId, String destination) {
		// Generate ID
		DateTime issueInstant = new DateTime();
		AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();

		AuthnRequest authRequest = authRequestBuilder.buildObject(SAML2_PROTOCOL, "AuthnRequest", "samlp");
		authRequest.setIsPassive(Boolean.FALSE);
		authRequest.setIssueInstant(issueInstant);
		authRequest.setProtocolBinding(SAML2_POST_BINDING);
		authRequest.setAssertionConsumerServiceURL(assertionConsumerServiceUrl);
		authRequest.setIssuer(buildIssuer(issuerId));
		authRequest.setNameIDPolicy(buildNameIDPolicy());
		authRequest.setRequestedAuthnContext(buildRequestedAuthnContext());
		authRequest.setID(UUID.randomUUID().toString());
		authRequest.setVersion(SAMLVersion.VERSION_20);

		authRequest.setAttributeConsumingServiceIndex(1);
		authRequest.setDestination(destination);
		// TODO
		// authRequest.setSignature(newSignature);

		return authRequest;
	}

	private static RequestedAuthnContext buildRequestedAuthnContext() {

		// Create AuthnContextClassRef
		AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
		AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject(SAML2_ASSERTION, "AuthnContextClassRef", "saml");
		authnContextClassRef.setAuthnContextClassRef(SAML2_PASSWORD_PROTECTED_TRANSPORT);

		// Create RequestedAuthnContext
		RequestedAuthnContextBuilder requestedAuthnContextBuilder = new RequestedAuthnContextBuilder();
		RequestedAuthnContext requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
		requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
		requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);

		return requestedAuthnContext;
	}

	/**
	 * Build the issuer object
	 *
	 * @return Issuer object
	 */
	private static Issuer buildIssuer(String issuerId) {
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setNameQualifier(issuerId);
		issuer.setValue(issuerId);
		return issuer;
	}

	/**
	 * Build the NameIDPolicy object
	 *
	 * @return NameIDPolicy object
	 */
	private static NameIDPolicy buildNameIDPolicy() {
		NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder().buildObject();
		nameIDPolicy.setFormat(SAML2_NAME_ID_POLICY);
		return nameIDPolicy;
	}

}
