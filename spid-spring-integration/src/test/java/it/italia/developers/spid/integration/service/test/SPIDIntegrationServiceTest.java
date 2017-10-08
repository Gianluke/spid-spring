package it.italia.developers.spid.integration.service.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml2.core.AuthnRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.italia.developers.spid.integration.Application;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

import java.util.List;

/**
 * @author Gianluca Pindinelli
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
public class SPIDIntegrationServiceTest {

	private final Logger log = LoggerFactory.getLogger(SPIDIntegrationServiceTest.class.getName());

	@Autowired
	private SPIDIntegrationService spidIntegrationService;

	@Autowired
	private SPIDIntegrationUtil spidIntegrationUtil;

	@Test
	public void buildAuthenticationRequestTest() {
		try {
			AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest("https://loginspid.aruba.it");
			Assert.assertNotNull(authRequest.getXmlAuthRequest());
		} catch (IntegrationServiceException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void getAllIdpEntryTest() {
		try {
			List<IdpEntry> idpEntries =  spidIntegrationService.getAllIdpEntry();
			Assert.assertTrue(idpEntries.size() > 0);
		} catch (IntegrationServiceException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}
