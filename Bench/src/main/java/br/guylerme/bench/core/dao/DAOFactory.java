/***********************************************************************
 * Module: DAOFactory.java Author: Owner Purpose: Defines the Class DAOFactory
 ***********************************************************************/

package br.guylerme.bench.core.dao;

import br.guylerme.bench.core.configuration.Configurator;
import br.guylerme.bench.core.configuration.exception.BenchConfigurationException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;

import org.apache.log4j.Logger;

/** @pdOid de32e728-f298-4f13-af11-d9af7fe641a3 */
public abstract class DAOFactory {
	/** @pdOid 83a82996-bcd2-4046-978e-6061da2c68e6 */
	private static Class<?> factory;

	private static Logger log = Logger.getLogger(DAOFactory.class);

	/** @pdOid 15ff8355-ffd9-447c-ba7f-e5ed0f349484 */
	public static DAOFactory getDaoFactory() throws FactoryNotFoundException {
		try {
			if (factory == null) {
				log.debug("Load the information of the dao factory");
				final String factoryName = Configurator
						.getProperty("daoFactoryClassName");
				final Class<?> factory = Class.forName(factoryName);
				if (DAOFactory.class.isAssignableFrom(factory)) {
					log.debug("The information of the dao factory loaded properly");
					DAOFactory.factory = factory;
				} else {
					throw new FactoryNotFoundException(
							"The factory is not assignable from DaoFactory",
							null);
				}
			}
			// verify if the factory is a extension of this class
			return (DAOFactory) factory.newInstance();

		} catch (final BenchConfigurationException e) {
			throw new FactoryNotFoundException("Can't get the property data", e);
		} catch (final ClassNotFoundException e) {
			throw new FactoryNotFoundException(
					"Can't found the dao factory class", e);
		} catch (final InstantiationException e) {
			throw new FactoryNotFoundException(
					"Can't instantiate the factory class", e);
		} catch (final IllegalAccessException e) {
			throw new FactoryNotFoundException(
					"Can't access the factory class", e);
		}
	}

	/**
	 * Cancel the operation (aka rollback in databases) and prepare the class to
	 * garbage collector (closing connections and 'nulling' object references.
	 */
	public abstract void cancel();

	/**
	 * Clean all tuples of the schema.
	 */
	public abstract void clean();

	/**
	 * Confirm the operation (aka commit in databases) and prepare the class to
	 * garbage collector (closing connections and 'nulling' object references.
	 */
	public abstract void confirm();

	/** @pdOid f8b09cef-87aa-4e20-9eaa-576b02b1e900 */
	public abstract SchemaDAO getSchemaDao();

}
