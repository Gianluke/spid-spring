package it.italia.developers.spid.integration.service.impl;

import java.util.Set;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.model.ResponseDecoded;
import it.italia.developers.spid.integration.service.AuthNRequestService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class AuthNRequestServiceImpl implements AuthNRequestService {

	private static final String SAML2_NAME_ID_POLICY = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
	private static final String SAML2_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
	private static final String SAML2_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT = "https://www.spid.gov.it/SpidL2";
	private static final String SAML2_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";

	@Value("${spid.spring.integration.sp.assertionConsumerServiceUrl}")
	private String assertionConsumerServiceUrl;

	@Value("${spid.spring.integration.sp.issuerId}")
	private String issuerId;

	@Autowired
	private SPIDIntegrationUtil spidIntegrationUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.italia.developers.spid.integration.service.AuthNRequestService#buildAuthenticationRequest(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public AuthnRequest buildAuthenticationRequest(String assertionConsumerServiceUrl, String issuerId, String destination) {
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

		// firma la request
		authRequest.setSignature(spidIntegrationUtil.getSignature());

		return authRequest;
	}

	private RequestedAuthnContext buildRequestedAuthnContext() {

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
	 * Costruisce lo issuer object
	 *
	 * @return Issuer object
	 */
	private Issuer buildIssuer(String issuerId) {
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setNameQualifier(issuerId);
		issuer.setValue(issuerId);
		return issuer;
	}

	/**
	 * Costruisce il NameIDPolicy object
	 *
	 * @return NameIDPolicy object
	 */
	private NameIDPolicy buildNameIDPolicy() {
		NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder().buildObject();
		nameIDPolicy.setFormat(SAML2_NAME_ID_POLICY);
		return nameIDPolicy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.italia.developers.spid.integration.service.AuthNRequestService#buildAuthenticationRequest(java
	 * .lang.String)
	 */
	@Override
	public AuthRequest buildAuthenticationRequest(String entityId) throws IntegrationServiceException {

		AuthRequest authRequest = new AuthRequest();

		// Caricamento IDP da entityID
		AuthnRequest buildAuthenticationRequest = buildAuthenticationRequest(assertionConsumerServiceUrl, issuerId, entityId);
		String encodedAuthnRequest = spidIntegrationUtil.encodeAndPrintAuthnRequest(buildAuthenticationRequest);

		// TODO caricare da metadati SP
		authRequest.setDestinationUrl("https://spid.lecce.it/spid-spring-rest/send-response");
		authRequest.setXmlAuthRequest(encodedAuthnRequest);

		return authRequest;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.italia.developers.spid.integration.service.AuthNRequestService#getAllIdpEntry()
	 */
	@Override
	public Set<IdpEntry> getAllIdpEntry() throws IntegrationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.italia.developers.spid.integration.service.AuthNRequestService#processAuthenticationResponse(
	 * java.lang.String)
	 */
	@Override
	public ResponseDecoded processAuthenticationResponse(String authResponse) throws IntegrationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
