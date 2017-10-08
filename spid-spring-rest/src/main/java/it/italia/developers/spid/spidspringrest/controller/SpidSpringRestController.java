package it.italia.developers.spid.spidspringrest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.ResponseDecoded;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.spidspringrest.model.ExtraInfo;
import it.italia.developers.spid.spidspringrest.model.SpidProviders;

@RestController()
public class SpidSpringRestController {

	@Value("${spid.spring.rest.assertionConsumerServiceIndex}")
	private Integer assertionConsumerServiceIndex;

	@Autowired
	private SPIDIntegrationService spidIntegrationService;

	@ApiOperation(value = "Elenco Providers SPID", notes = "Servizio REST per ottenere l'elenco dei provider abilitati", response = SpidProviders.class)
	@RequestMapping(value = "list-providers", method = RequestMethod.GET)
	public SpidProviders listIdProviders() throws IntegrationServiceException {
		SpidProviders retVal = new SpidProviders();
		retVal.setIdentityProviders(spidIntegrationService.getAllIdpEntry());
		retVal.setExtraInfo(EXTRA_INFO);
		return retVal;
	}

	@ApiOperation(value = "Inserimento della richiesta di autorizzazione", notes = "Servizio REST per ottenere la richiesta di autorizzazione", response = AuthRequest.class)
	@RequestMapping(value = "auth-spid", method = RequestMethod.GET)
	public AuthRequest authRequest(@RequestParam(name = "entityId", required = true) @ApiParam(value = "Entity Id dell'Idp", required = true) final String entityId)
			throws IntegrationServiceException {
		AuthRequest retVal = spidIntegrationService.buildAuthenticationRequest(entityId, assertionConsumerServiceIndex);

		return retVal;
	}

	@ApiOperation(value = "Iserimento della richiesta di autorizzazione", notes = "Servizio rest per decodificare i dati dell'utente autenticato", response = ResponseDecoded.class)
	@RequestMapping(value = "send-response", method = RequestMethod.POST, consumes = { MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseDecoded decodeResponse(@ApiParam(value = "Valore cifrato della Response di Spid", required = true) @RequestBody final String responseEncripted) {
		System.out.println(responseEncripted);

		ResponseDecoded retVal = new ResponseDecoded();
		retVal.setNome("TEST");
		retVal.setCognome("TEST");

		return retVal;
	}

	@RequestMapping(value = "send-response-test", method = RequestMethod.POST)
	public ResponseDecoded decodeResponseTest(HttpServletRequest request) {

		ResponseDecoded retVal = new ResponseDecoded();
		retVal.setNome("TEST");
		retVal.setCognome("TEST");

		return retVal;
	}

	private static final List<ExtraInfo> EXTRA_INFO = new ArrayList<ExtraInfo>();
	static {
		EXTRA_INFO.add(new ExtraInfo("Maggiori informazioni", "https://www.spid.gov.it/"));
		EXTRA_INFO.add(new ExtraInfo("Non hai SPID?", "https://www.spid.gov.it/richiedi-spid"));
		EXTRA_INFO.add(new ExtraInfo("Serve aiuto?", "https://www.spid.gov.it/serve-aiuto"));
	}
}
