package it.italia.developers.spid.integration.util;

import it.italia.developers.spid.integration.Application;
import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
public class SPIDIntegrationUtilTest {

    @Autowired
    private SPIDIntegrationUtil spidIntegrationUtil;

    @Test
    public void xmlStringToXMLObjectTest() {
		try {
            ClassLoader classLoader = getClass().getClassLoader();
            File xmlFile = new File(classLoader.getResource("telecom-metadata.xml").getFile());
            String xmlData = new Scanner(xmlFile).useDelimiter("\\Z").next();            
            spidIntegrationUtil.xmlStringToXMLObject(xmlData);
            
		} catch (SAXException | IOException | ParserConfigurationException | UnmarshallingException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }
}
