package it.italia.developers.spid.integration.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
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
import org.xml.sax.SAXException;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.model.ResponseDecoded;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.AuthenticationInfoExtractor;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class SPIDIntegrationServiceImpl implements SPIDIntegrationService {
	private static final String SPID_SPRING_INTEGRATION_IDP_PREFIX = "spid.spring.integration.idp.";
	private static final String SPID_SPRING_INTEGRATION_IDP_KEYS = "spid.spring.integration.idp.keys";

	
	@Value("${spid.spring.integration.sp.assertionConsumerServiceUrl}")
	private String assertionConsumerServiceUrl;

	@Value("${spid.spring.integration.sp.issuerId}")
	private String issuerId;

	@Autowired
	SPIDIntegrationUtil spidIntegrationUtil;

	@Override
	public AuthRequest buildAuthenticationRequest(String entityId) throws IntegrationServiceException {
		try {
			String xmlServiceMetadata = retrieveXMLServiceMetadata(entityId);
			AuthenticationInfoExtractor authenticationInfoExtractor;
			String assertionConsumerServiceUrl = "https://spid.lecce.it/spid-spring-rest/send-response";
			Integer assertionConsumerServiceIndex = 1;
			authenticationInfoExtractor = new AuthenticationInfoExtractor(entityId, xmlServiceMetadata, spidIntegrationUtil, assertionConsumerServiceUrl, assertionConsumerServiceIndex);
			AuthRequest authRequest = authenticationInfoExtractor.getAuthenticationRequest();
			return authRequest;
		} catch (XPathExpressionException | SAXException | IOException | ParserConfigurationException e) {
			throw new IntegrationServiceException(e);
		}
	}

	private String retrieveXMLServiceMetadata(String entityId) throws IntegrationServiceException {
		Properties properties = new Properties();
		try (InputStream propertiesInputStream = getClass().getResourceAsStream("/idplist.properties")) {
			properties.load(propertiesInputStream);
			String keysProperty = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_KEYS);
			String[] keys = keysProperty.split(",");
			for (String key: keys) {
				String entityIdFromProperties = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".entityId");
				String xmlServiceMetadata = null;
				if (entityId.equals(entityIdFromProperties)) {
					String xmlMetadataFileName = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".file");
					xmlServiceMetadata = resourceNameToString(xmlMetadataFileName);
				}

				if (xmlServiceMetadata != null) {
					return xmlServiceMetadata;
				}
			}
		} catch (FileNotFoundException e) {
			throw new IntegrationServiceException(e);
		}
		catch (IOException e) {
			throw new IntegrationServiceException(e);
		}
		
		throw new IntegrationServiceException("Metadata file not found for the specified entityId.");
	}

	private String resourceNameToString(String resourceName) throws IOException {
		try (InputStream resourceInputStream = getClass().getResourceAsStream("/metadata/idp/" + resourceName)) {
			if (resourceInputStream == null) {
				return null;
			}

			try (Scanner scanner = new Scanner(resourceInputStream)) {
				String resourceContent = scanner.useDelimiter("\\Z").next();
				return resourceContent;
			}
		}
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
