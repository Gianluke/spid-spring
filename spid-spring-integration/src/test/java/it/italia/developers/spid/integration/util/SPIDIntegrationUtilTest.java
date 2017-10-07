package it.italia.developers.spid.integration.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.italia.developers.spid.integration.Application;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import junit.framework.Assert;

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

		}
		catch (IntegrationServiceException e) {
			e.printStackTrace();
			Assert.fail();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
