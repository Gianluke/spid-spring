package it.italia.developers.spid.integration.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class SPIDIntegrationServiceImpl implements SPIDIntegrationService {
	
	private static final String SAML2_NAME_ID_POLICY = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
	private static final String SAML2_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
	private static final String SAML2_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT = "https://www.spid.gov.it/SpidL2";
	private static final String SAML2_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";
	private static final String SPID_SPRING_INTEGRATION_IDP_PREFIX = "spid.spring.integration.idp.";
	private static final String SPID_SPRING_INTEGRATION_IDP_KEYS = "spid.spring.integration.idp.keys";

	
	@Value("${spid.spring.integration.sp.assertionConsumerServiceUrl}")
	private String assertionConsumerServiceUrl;

	@Value("${spid.spring.integration.sp.issuerId}")
	private String issuerId;

	@Autowired
	private SPIDIntegrationUtil spidIntegrationUtil;

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
		// TODO caricamento da XML
		authRequest.setID("_abdd8d0-370e-4f76-b281-8eebb276faef");
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
		issuer.setFormat(SAML2_NAME_ID_POLICY);
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

	@Override
	public List<IdpEntry> getAllIdpEntry() throws IntegrationServiceException {
		List<IdpEntry> idpEntries = new ArrayList<IdpEntry>();

		Properties properties = new Properties();
		try (InputStream propertiesInputStream = getClass().getResourceAsStream("/idplist.properties")) {
			properties.load(propertiesInputStream);
			idpEntries = propertiesToIdPEntry(properties);
		} catch (FileNotFoundException e) {
			throw new IntegrationServiceException(e);
		}
		catch (IOException e) {
			throw new IntegrationServiceException(e);
		}
		return idpEntries;
	}

	private List<IdpEntry> propertiesToIdPEntry(Properties properties) {
		List<IdpEntry> idpEntries = new ArrayList<IdpEntry>();

		String keysProperty = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_KEYS);
		String[] keys = keysProperty.split(",");
		for (String key: keys) {
			IdpEntry idpEntry = new IdpEntry();
			String name = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".name");
			idpEntry.setName(name);
			String imageUrl = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".imageUrl");
			idpEntry.setImageUrl(imageUrl);
			String entityId = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".entityId");
			idpEntry.setEntityId(entityId);
			idpEntry.setIdentifier(key);
			idpEntries.add(idpEntry);								
		}

		return idpEntries;
	}

	@Override
	public ResponseDecoded processAuthenticationResponse(String authResponse) throws IntegrationServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
