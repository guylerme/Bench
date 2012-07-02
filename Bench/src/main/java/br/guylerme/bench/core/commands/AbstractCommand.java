/***********************************************************************
 * Module: AbstractCommand.java Author: Owner Purpose: Defines the Class
 * AbstractCommand
 ***********************************************************************/
package br.guylerme.bench.core.commands;

import gnu.trove.THashMap;

import java.util.Date;
import java.util.Map;

import br.guylerme.bench.core.configuration.Configurator;
import br.guylerme.bench.core.configuration.exception.BenchConfigurationException;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;

import org.apache.log4j.Logger;

/*
 * Class responsible for all command executions. It manages the database
 * connection and its transactions. Beside that, it manages the logging tool
 * too. You can use the methods "start" and "stop" to take a count of the time
 * elapsed.
 * 
 * 
 * @author Guylerme Figueiredo
 * 
 * @pdOid 39608c67-060a-4c31-814f-b4ec0ee7c691
 */
public abstract class AbstractCommand {

	/** @pdRoleInfo migr=no name=DAOFactory assc=association1 mult=1..1 */
	private DAOFactory factory;
	private Logger log = null;
	Map<String, Object> parameters;
	/** @pdOid 1053220d-c1ba-4842-8519-9889573399e9 */
	private long startTime;

	/** @pdOid dea5502d-23c7-41a7-b3c4-c8d0b22ff223 */
	private long stopTime;
	/**
	 * The information if another command asks for execution of this command
	 */
	private AbstractCommand superCommand = null;

	/** @pdOid 8d72959c-42e6-4216-a844-6b1d7499c86e */
	protected AbstractCommand() {
		// get the logger

		try {
			Configurator.loadProperties();
		} catch (final BenchConfigurationException e) {
			e.printStackTrace();
		}
		log = Logger.getLogger(this.getClass());
		log.debug("New command beeing instanciated: " + getCommandName());
	}

	public void addParameter(final String name, final Object value) {
		if (parameters == null) {
			parameters = new THashMap<String, Object>();
		}
		parameters.put(name, value);
	}

	/**
	 * Execute the command using the parameter list used with addParameter
	 * method.
	 * 
	 * @return
	 */
	public final ReturnValue execute() {
		if (parameters == null) {
			parameters = new THashMap<String, Object>();
		}
		return this.execute(parameters, null);
	}

	public final ReturnValue execute(final AbstractCommand cmd) {
		if (parameters == null) {
			parameters = new THashMap<String, Object>();
		}
		return this.execute(parameters, cmd);
	}

	/**
	 * Execute the command
	 * 
	 * @param parameters
	 *            the map containing the parameters
	 * @return
	 */
	public final ReturnValue execute(final Map<String, Object> parameters) {
		return execute(parameters, null);
	}

	public final ReturnValue execute(final Map<String, Object> parameters,
			final AbstractCommand supercommand) {
		log.debug("The command " + getName() + " started its execution");
		if (supercommand != null) {
			factory = supercommand.factory;
			superCommand = supercommand;
		}
		ReturnValue values = null;
		try {
			startTime = (new Date().getTime());
			// Execute the command
			values = executeCommand(parameters);

		} catch (final QueryException e) {
			final String msgError = "A problem with a query on the datasource happens. Please contact the support.";
			log.fatal(msgError, e);
			values = new ReturnValue(true, msgError, e, null);
		} catch (final DataSourceConnectionException e) {
			final String msgError = "A problem with the connection between the sistem and the datasouce happens. Please contact the support.";
			log.fatal(msgError, e);
			values = new ReturnValue(true, msgError, e, null);
		} catch (final FactoryNotFoundException e) {
			final String msgError = "A problem with the configuration file happens and it was not possible to collect datasource's configuration information";
			log.fatal(msgError, e);
			values = new ReturnValue(true, msgError, e, null);
		}  catch (final Throwable t) {
			// Ok, if we reach here, a totally unexpected error happens.
			final String msgError = "An unexpected error happened. Please contact the support.";
			log.fatal(msgError, t);
			values = new ReturnValue(true, msgError, t, null);
		} finally {
			stopTime = (new Date().getTime());
		}
		// if this is a sub-command it does not have the power to rollback or
		// commit a transaction. The super-command might decide.
		final Long timeelapsed = (stopTime - startTime) / 1000;

		if (values == null) {
			// It should not happened. Send a error message.
			final String msg = "The command "
					+ this.getClass().getName()
					+ " was not implemented correctly. The return value was null.";
			values = new ReturnValue(true, msg, null, null);
			log.fatal(msg);
			if (supercommand == null && factory != null) {
				factory.cancel();
			}
		} else if (values.isError()) {
			log.error(values.getException().getMessage(), values.getException());
			log.info("Command " + getName() + " canceled. Time elapsed: "
					+ timeelapsed + " seconds.");
			if (supercommand == null && factory != null) {
				factory.cancel();
			}
		} else {
			log.info("Command " + getName() + " finished. Time elapsed: "
					+ timeelapsed + " seconds.");
			if (supercommand == null) {
				factory.confirm();
			}
		}

		// now asks for the garbage collector.
		factory = null;
		Runtime.getRuntime().gc();
		return values;
	}

	/**
	 * The command class must implement this method to execute a command
	 * 
	 * @param parameters
	 * @return
	 * @throws SimilarityException
	 */
	public abstract ReturnValue executeCommand(Map<String, Object> parameters)
			throws QueryException, DataSourceConnectionException,
			FactoryNotFoundException;

	/**
	 * The command class must implement this method to return the name of the
	 * command
	 * 
	 * @return
	 */
	public abstract String getCommandName();

	public DAOFactory getDAO() throws FactoryNotFoundException {
		if (factory == null) {
			factory = DAOFactory.getDaoFactory();
		}
		return factory;
	}

	/** @pdOid a8bcd1db-1606-4b15-9d41-8ea8e0a5738c */
	protected final Logger getLogger() {
		return log;
	}

	public String getName() {
		String name = getCommandName();
		if (superCommand != null) {
			name += " (" + superCommand.getName() + ")";
		}
		return name;
	}
}
