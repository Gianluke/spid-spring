package it.italia.developers.spid.integration.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
<<<<<<< HEAD
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import it.italia.developers.spid.integration.Application;
=======

import org.springframework.beans.factory.annotation.Autowired;

>>>>>>> 47812e594c6af6e770b74bbfc28c4f9b42ed4fd6
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import junit.framework.Assert;

// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = { Application.class })
public class SPIDIntegrationUtilTest {

      @Autowired
      private SPIDIntegrationUtil spidIntegrationUtil;

      @Test
      public void xmlStringToXMLObjectTest() {

            try {
                  ClassLoader classLoader = getClass().getClassLoader();
                  File xmlFile = new File(classLoader.getResource("metadata/idp/telecom-metadata.xml").getFile());
                  String xmlData = new Scanner(xmlFile).useDelimiter("\\Z").next();
                  Element node = spidIntegrationUtil.xmlStringToElement(xmlData);

<<<<<<< HEAD
                  Assert.assertEquals("md:EntityDescriptor", node.getNodeName());
=======
	// @Test
	public void xmlStringToXMLObjectTest() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File xmlFile = new File(classLoader.getResource("telecom-metadata.xml").getFile());
			String xmlData = new Scanner(xmlFile).useDelimiter("\\Z").next();
			spidIntegrationUtil.xmlStringToXMLObject(xmlData);
>>>>>>> 47812e594c6af6e770b74bbfc28c4f9b42ed4fd6

            } catch (SAXException | IOException | ParserConfigurationException e) {
                  e.printStackTrace();
                  Assert.fail();
            }
      }
}
