package it.italia.developers.spid.integration.service.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.italia.developers.spid.integration.Application;
import it.italia.developers.spid.integration.service.AuthNRequestService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
public class AuthNRequestServiceTest {

	private final Logger log = LoggerFactory.getLogger(AuthNRequestServiceTest.class.getName());

	@Autowired
	private AuthNRequestService authNRequestService;

	@Autowired
	private SPIDIntegrationUtil spidIntegrationUtil;

	@Test
	public void generateAuthNRequest() {

		AuthnRequest authnRequest = authNRequestService.buildAuthenticationRequest("https://spid.lecce.it/spid-spring-rest/send-response", "https://spid.lecce.it", "idp.spid.gov.it");

		try {
			// encode request
			String flatAuthnRequest = spidIntegrationUtil.printAuthnRequest(authnRequest);
			log.info("FLATAUTHNREQUEST: " + flatAuthnRequest);
			System.out.println("FLATAUTHNREQUEST: " + flatAuthnRequest);

			String encodedAuthnRequest = spidIntegrationUtil.encodeAndPrintAuthnRequest(authnRequest);
			log.info("ENCODEDAUTHNREQUEST: " + encodedAuthnRequest);

			System.out.println("ENCODEDAUTHNREQUEST: " + encodedAuthnRequest);
		}
		catch (MarshallingException e) {
			log.error("generateAuthNRequest :: " + e.getMessage(), e);
		}
		catch (IOException e) {
			log.error("generateAuthNRequest :: " + e.getMessage(), e);
		}
		catch (ConfigurationException e) {
			log.error("generateAuthNRequest :: " + e.getMessage(), e);
		}

	}

}
