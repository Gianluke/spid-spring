package it.italia.developers.spid.integration.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Gianluca Pindinelli
 *
 */
@Component
public class SPIDIntegrationUtil {

	private final Logger log = LoggerFactory.getLogger(SPIDIntegrationUtil.class.getName());

	public SPIDIntegrationUtil() {
		try {
			DefaultBootstrap.bootstrap();
		}
		catch (ConfigurationException e) {
			log.error("SPIDIntegrationUtil :: " + e.getMessage(), e);
		}
	}

	/**
	 * Encode AuthNRequest.
	 *
	 * @param authnRequest
	 * @return
	 * @throws MarshallingException
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public String encodeAuthnRequest(AuthnRequest authnRequest) throws MarshallingException, IOException, ConfigurationException {

		String requestMessage = printAuthnRequest(authnRequest);
		Deflater deflater = new Deflater(Deflater.DEFLATED, true);
		ByteArrayOutputStream byteArrayOutputStream = null;
		DeflaterOutputStream deflaterOutputStream = null;

		byteArrayOutputStream = new ByteArrayOutputStream();
		deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
		deflaterOutputStream.write(requestMessage.getBytes()); // compressing
		deflaterOutputStream.close();

		String encodedRequestMessage = Base64.encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);

		encodedRequestMessage = URLEncoder.encode(encodedRequestMessage, "UTF-8").trim(); // encoding string

		return encodedRequestMessage;

	}

	/**
	 * Print AuthnRequest.
	 *
	 * @param authnRequest
	 * @return
	 * @throws MarshallingException
	 */
	public String printAuthnRequest(AuthnRequest authnRequest) throws MarshallingException {

		Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(authnRequest); // object to DOM converter
		org.w3c.dom.Element authDOM = marshaller.marshall(authnRequest); // converting to a DOM
		StringWriter requestWriter = new StringWriter();
		requestWriter = new StringWriter();
		XMLHelper.writeNode(authDOM, requestWriter);
		String authnRequestString = requestWriter.toString(); // DOM to string

		return authnRequestString;

	}

}
