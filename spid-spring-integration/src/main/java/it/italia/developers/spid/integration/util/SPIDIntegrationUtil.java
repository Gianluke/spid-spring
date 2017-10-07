package it.italia.developers.spid.integration.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
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

	public Credential getCredential() {

		String certificateAliasName = "<saml-signing-key-alias>";
		KeyStore ks = getKeyStore();

		// Get Private Key Entry From Certificate
		KeyStore.PrivateKeyEntry pkEntry = null;
		try {
			pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(certificateAliasName, new KeyStore.PasswordProtection("<saml-signing-key-password>".toCharArray()));
		}
		catch (NoSuchAlgorithmException e) {
			log.error("Failed to Get Private Entry From the keystore", e);
		}
		catch (UnrecoverableEntryException e) {
			log.error("Failed to Get Private Entry From the keystore", e);
		}
		catch (KeyStoreException e) {
			log.error("Failed to Get Private Entry From the keystore", e);
		}
		PrivateKey pk = pkEntry.getPrivateKey();

		X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(certificate);
		credential.setPrivateKey(pk);

		log.info("Private Key" + pk.toString());

		return credential;
	}

	public KeyStore getKeyStore() {

		String passwordString = "<saml-keystore-password>";
		String fileName = "<path-to-saml-keystore-file>";

		KeyStore ks = null;
		FileInputStream fis = null;
		char[] password = passwordString.toCharArray();

		// Get Default Instance of KeyStore
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		}
		catch (KeyStoreException e) {
			log.error("Error while Intializing Keystore", e);
		}

		// Read Ketstore as file Input Stream
		try {
			fis = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e) {
			log.error("Unable to found KeyStore with the given keystoere name ::" + fileName, e);
		}

		// Load KeyStore
		try {
			ks.load(fis, password);
		}
		catch (NoSuchAlgorithmException e) {
			log.error("Failed to Load the KeyStore:: ", e);
		}
		catch (CertificateException e) {
			log.error("Failed to Load the KeyStore:: ", e);
		}
		catch (IOException e) {
			log.error("Failed to Load the KeyStore:: ", e);
		}

		// Close InputFileStream
		try {
			fis.close();
		}
		catch (IOException e) {
			log.error("Failed to close file stream:: ", e);
		}
		return ks;
	}

}
