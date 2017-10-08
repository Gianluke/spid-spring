package it.italia.developers.spid.integration.util;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AuthenticationInfoExtractor {
	private static final String SAML2_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
    private static final String SAML2_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private static final String SAML2_NAME_ID_POLICY = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
	private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT = "https://www.spid.gov.it/SpidL2";
    private static final String SAML2_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";
    private static final String XPATH_SSO_POST_LOCATION = "/*[local-name()='EntityDescriptor']/*[local-name()='IDPSSODescriptor']/*[local-name()='SingleSignOnService'][@Binding='urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST']";  

    XPathFactory xPathfactory = XPathFactory.newInstance();

    AuthRequest authRequest = new AuthRequest();

    SPIDIntegrationUtil spidIntegrationUtil;

    public AuthenticationInfoExtractor(String entityId, String xmlServiceMetadata, SPIDIntegrationUtil spidIntegrationUtil, String assertionConsumerServiceUrl) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, IntegrationServiceException {
        super();

        this.spidIntegrationUtil = spidIntegrationUtil;
        Element domElement = spidIntegrationUtil.xmlStringToElement(xmlServiceMetadata);
        String destination = extractDestinationUrl(domElement);
        String id = extractId(domElement);

        // Caricamento IDP da entityID
		AuthnRequest buildAuthenticationRequest = buildAuthenticationRequest(assertionConsumerServiceUrl, entityId, id, destination);
		String encodedAuthnRequest = spidIntegrationUtil.encodeAndPrintAuthnRequest(buildAuthenticationRequest);
        
		// TODO caricare da metadati SP
		authRequest.setDestinationUrl(assertionConsumerServiceUrl);
		authRequest.setXmlAuthRequest(encodedAuthnRequest);
    }

    private String extractDestinationUrl(Element domElement) throws XPathExpressionException {
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile(XPATH_SSO_POST_LOCATION);
        Node foundNode = (Node) expr.evaluate(domElement, XPathConstants.NODE);
        NamedNodeMap nodeMap = foundNode.getAttributes();
        String assertionConsumerServiceUrl = nodeMap.getNamedItem("Location").getNodeValue();

        return assertionConsumerServiceUrl;

    }

    private String extractId(Element domElement) throws XPathExpressionException {
        NamedNodeMap nodeMap = domElement.getAttributes();
        String id = nodeMap.getNamedItem("ID").getNodeValue();

        return id;
    }

	public AuthnRequest buildAuthenticationRequest(String assertionConsumerServiceUrl, String issuerId, String id, String destination) {
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
		authRequest.setID(id);
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


	public AuthRequest getAuthenticationRequest() {
		return authRequest;
	}

}