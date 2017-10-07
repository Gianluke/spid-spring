package it.italia.developers.spid.spidspringrest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.model.ResponseDecoded;
import it.italia.developers.spid.spidspringrest.model.ExtraInfo;
import it.italia.developers.spid.spidspringrest.model.SpidProviders;

@RestController()
public class SpidSpringRestController {

	@ApiOperation(value = "Elenco Providers SPID", notes = "Servizio rest per ottenere l'elenco dei provider abilitati", response = SpidProviders.class)
	@RequestMapping(value="list-providers", method = RequestMethod.GET)
	public SpidProviders listIdProviders() {
		SpidProviders retVal = new SpidProviders();
		retVal.setIdentityProviders(ENTRY_MOCK);
		retVal.setExtraInfo(EXTRA_MOCK);
		return retVal;
	}

	@ApiOperation(value = "Iserimento della richiesta di autorizzazione", notes = "Servizio rest per ottenere la richiesta di autorizzazione", response = AuthRequest.class)
	@RequestMapping(value="auth-spid", method = RequestMethod.GET)
	public AuthRequest authRequest(
			@RequestParam(name = "entityId", required = true) @ApiParam(value = "Entity Id dell'Idp", required = true) final String entityId) {
		AuthRequest retVal = new AuthRequest();
		retVal.setDestinationUrl("https://posteid.poste.it/jod-fs/ssoservicepost");
		retVal.setXmlAuthRequest("<TEST>" + entityId + "</TEST>");

		return retVal;
	}

	@ApiOperation(value = "Iserimento della richiesta di autorizzazione", notes = "Servizio rest per decodificare i dati dell'utente autenticato", response = ResponseDecoded.class)
	@RequestMapping(value="send-response", method = RequestMethod.POST, consumes= { MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseDecoded decodeResponse(
			@ApiParam(value = "Valore cifrato della Response di Spid", required = true)
			@RequestBody final String responseEncripted) {
		System.out.println(responseEncripted);

		ResponseDecoded retVal = new ResponseDecoded();
		retVal.setNome("TEST");
		retVal.setCognome("TEST");

		return retVal;
	}


	private static final List<it.italia.developers.spid.integration.model.IdpEntry> ENTRY_MOCK = new ArrayList<IdpEntry>();
	static {
		ENTRY_MOCK.add(new IdpEntry("posteid", "https://posteid.poste.it", "Poste ID"));
		ENTRY_MOCK.add(new IdpEntry("timid", "https://login.id.tim.it/affwebservices/public/saml2sso", "TIM ID"));
		ENTRY_MOCK.add(new IdpEntry("namirialid", "https://idp.namirialtsp.com/idp", "Namirial ID"));
		ENTRY_MOCK.add(new IdpEntry("infocertid", "https://identity.infocert.it", "InfoCert ID"));
		ENTRY_MOCK.add(new IdpEntry("sielteid", "https://identity.sieltecloud.it", "Sielte ID"));
		ENTRY_MOCK.add(new IdpEntry("spiditalia", "https://spid.register.it", "SpidItalia"));
		ENTRY_MOCK.add(new IdpEntry("arubaid", "https://loginspid.aruba.it", "SpidItalia ID"));
	}

	private static final List<ExtraInfo> EXTRA_MOCK = new ArrayList<ExtraInfo>();
	static {
		EXTRA_MOCK.add(new ExtraInfo("Maggiori informazioni", "https://www.spid.gov.it/"));
		EXTRA_MOCK.add(new ExtraInfo("Non hai SPID?", "https://www.spid.gov.it/richiedi-spid"));
		EXTRA_MOCK.add(new ExtraInfo("Serve aiuto?", "https://www.spid.gov.it/serve-aiuto"));
	}
}
