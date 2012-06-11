/***********************************************************************
 * Module: Configurator.java Author: Owner Purpose: Defines the Class
 * Configurator
 ***********************************************************************/
package br.guylerme.bench.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.guylerme.bench.core.configuration.exception.BenchConfigurationException;

/** @pdOid 2a991f36-bbfe-429b-9a5b-ae4c9c41b84e */
public class Configurator {

	private static Logger log = null;
	/** @pdOid c0e6d303-5213-4afa-b4cc-0083c21eb2e5 */
	private static Properties prop;

	/** @pdOid 6861295f-24f6-4b54-9db5-16eb8875ad59 */
	public static String getProperty(final String value)
			throws BenchConfigurationException {
		loadProperties();

		log.debug("getProperty:: value = " + value);

		return prop.getProperty(value);
	}

	/** @pdOid f27d45d0-b279-4d98-ade6-5cd3db90cf81 */
	public static void loadProperties() throws BenchConfigurationException {
		if (prop == null) {
			// Configuration wasn't loaded.
			log = Logger.getLogger(Configurator.class);
			try {
				final URL url = Configurator.class
						.getResource("bench.properties");
				final String strFile = url.toURI().getPath();

				final File file = new File(strFile);
				if (!file.exists()) {
					/*
					 * TODO tratar caso do arquivo de configuração não existir
					 */
				}
				final Properties prop = new Properties();
				prop.load(new FileInputStream(file));
				// Find the property file. Now configure log4j
				PropertyConfigurator.configure(prop);
				log.debug("Configuration file loaded.");
				log.debug("Log4j configurated properly");
				Configurator.prop = prop;
			} catch (final IOException e) {
				// The file probrably was not found. Initiate the basic
				// configuration of log4j and log
				BasicConfigurator.configure();
				log.fatal(
						"A fatal problem happened when trying to load the property file.",
						e);
				throw new BenchConfigurationException();
			} catch (final URISyntaxException e) {
				BasicConfigurator.configure();
				log.fatal(
						"A fatal problem happened when trying to load the property file.",
						e);
				throw new BenchConfigurationException();
			}
		}
	}
}
