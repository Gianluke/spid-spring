package it.italia.developers.spid.integration.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	public AuthRequest buildAuthenticationRequest(String entityId, int assertionConsumerServiceIndex) throws IntegrationServiceException {
		AuthenticationInfoExtractor authenticationInfoExtractor = new AuthenticationInfoExtractor(entityId, spidIntegrationUtil, assertionConsumerServiceIndex);
		AuthRequest authRequest = authenticationInfoExtractor.getAuthenticationRequest();
		return authRequest;
	}

	@Override
	public List<IdpEntry> getAllIdpEntry() throws IntegrationServiceException {
		List<IdpEntry> idpEntries = new ArrayList<IdpEntry>();

		Properties properties = new Properties();
		try (InputStream propertiesInputStream = getClass().getResourceAsStream("/idplist.properties")) {
			properties.load(propertiesInputStream);
			idpEntries = propertiesToIdPEntry(properties);
		}
		catch (FileNotFoundException e) {
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
		for (String key : keys) {
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
