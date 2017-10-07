package it.italia.developers.spid.integration.service.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml2.core.AuthnRequest;
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

		AuthnRequest authnRequest = authNRequestService.buildAuthenticationRequest("https://www.comune.bari.it/Shibboleth.sso/SAML2/POST", "https://posteid.poste.it", "https://www.comune.bari.it/sp");

		try {
			String printAuthnRequest = spidIntegrationUtil.printAuthnRequest(authnRequest);
			log.info("printAuthnRequest: " + printAuthnRequest);

			System.out.println("printAuthnRequest: " + printAuthnRequest);
		}
		catch (MarshallingException e) {
			log.error("generateAuthNRequest :: " + e.getMessage(), e);
		}

	}

}
