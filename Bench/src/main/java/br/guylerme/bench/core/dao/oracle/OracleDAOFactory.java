/***********************************************************************
 * Module: SQLServerDAOFactory.java Author: Owner Purpose: Defines the Class
 * SQLServerDAOFactory
 ***********************************************************************/
package br.guylerme.bench.core.dao.oracle;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import br.guylerme.bench.core.configuration.Configurator;
import br.guylerme.bench.core.configuration.exception.BenchConfigurationException;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.TransformationDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/** @pdOid 1a1963bb-cb77-4b9a-92e0-2970662eb551 */
public class OracleDAOFactory extends DAOFactory {

	/****************************************************
	 * Static attributes
	 */
	private static ComboPooledDataSource pool = null;

	// array with queries to clean the database
	private static String[] SQL_CLEAN_DATABASE = {
			"delete from bench.\"VALIDNAMESPACE\"			",
			"delete from bench.\"INDIVIDUAL_PROPERTY\"		",
			"delete from bench.\"INDIVIDUAL_CLASS\"			",
			"delete from bench.\"CPROPERTY\"				",
			"delete from bench.\"PROPERTY\"					",
			"delete from bench.\"INSTANCE\"					",
			"delete from bench.\"CLASS\"					",
			"delete from bench.\"ELEMENT\"					",
			"delete from bench.\"DATASET\"					",
			"delete from bench.\"SCHEMA\"					",
			"delete from bench.\"MATCHABLE\"				" };

	/****************************************************
	 * static methods
	 */
	private static Connection getPoolConnection() throws SQLException,
			BenchConfigurationException, PropertyVetoException {
		// if the pool = null, create a new pool.
		if (pool == null) {
			pool = new ComboPooledDataSource();
			pool.setDriverClass("oracle.jdbc.driver.OracleDriver");
			String jdbcUrl = Configurator.getProperty("jdbcurl");
			pool.setJdbcUrl(jdbcUrl);
			String username = Configurator.getProperty("username");
			pool.setUser(username);
			String password = Configurator.getProperty("password");
			pool.setPassword(password);
			String database = Configurator.getProperty("database");
			pool.setDataSourceName(database);
			pool.setAcquireIncrement(Integer.parseInt(Configurator
					.getProperty("acquireIncrement")));
			pool.setMaxPoolSize(Integer.parseInt(Configurator
					.getProperty("maxPoolSize")));
			pool.setMinPoolSize(Integer.parseInt(Configurator
					.getProperty("minPoolSize")));
		}
		// get a coonection
		return pool.getConnection();
	}

	private Connection con = null;

	private Logger log = null;

	/****************************************************
	 * Non-static attributes
	 */
	private OracleSchemaDAO schema = null;
	private OracleTransformationDAO genTransformation = null;

	/****************************************************
	 * Non-static methods
	 */
	// gets a connection and starts a transaction
	public OracleDAOFactory() {
		log = Logger.getLogger(OracleDAOFactory.class);
		log.debug("Starting a new SQLServerDAOFactory instante");
	}

	@Override
	public void cancel() {
		close(false);
	}

	@Override
	public void clean() {
		log.debug("Cleaning the database");
		try {
			final Connection con = getConnection();
			final Statement st = con.createStatement();
			final int tam = SQL_CLEAN_DATABASE.length;

			for (int counter = 0; counter < tam; counter++) {
				st.executeUpdate(SQL_CLEAN_DATABASE[counter]);
			}
			confirm();
		} catch (final DataSourceConnectionException e) {
			log.error("Error trying to clean the database", e);
		} catch (final SQLException e) {
			log.error("Error trying to clean the database", e);
		}

	}

	/**
	 * Complements the confirm and cancel function
	 */
	private void close(final boolean sucess) {
		log.debug("Closing the SQLServerDAOFactory");
		if (con != null) {
			try {
				if (sucess) {
					con.commit();
				} else {
					con.rollback();
				}
			} catch (final SQLException ex) {
				log.error("Error trying to commit or rollback a connection", ex);
			}
		}
		try {
			con.close();
		} catch (final SQLException e) {
			log.error("Error trying to return the connection to the pool");
		}
		con = null;
		log = null;
		// for each "subDao", call close method
		if (schema != null) {
			schema.close();
			schema = null;
		}

	}

	@Override
	public void confirm() {
		close(true);
	}

	/**
	 * Gets the connection allocated to this instance. If noone connection is
	 * assigned, get one on the pool.
	 * 
	 * @return Connection or null if a error was found
	 */
	protected Connection getConnection() throws DataSourceConnectionException {
		if (con == null) {
			try {
				con = getPoolConnection();
				// define auto commit as false (must commit or rollback)
				con.setAutoCommit(false);
			} catch (final PropertyVetoException ex) {
				con = null;
				final String msg = "Error loading the JDBC SQL Server driver.";
				log.error(msg, ex);
				throw new DataSourceConnectionException(msg, ex);
			} catch (final SQLException ex) {
				con = null;
				final String msg = "An error ocurred when trying to get a connection";
				log.error(msg, ex);
				throw new DataSourceConnectionException(msg, ex);
			} catch (final BenchConfigurationException ex) {
				con = null;
				final String msg = "An error ocurred when trying to get a value from config file";
				log.error(msg, ex);
				throw new DataSourceConnectionException(msg, ex);
			}
		}
		return con;
	}

	@Override
	public SchemaDAO getSchemaDao() {
		if (schema == null) {
			// pass the Factory to get the connection
			schema = new OracleSchemaDAO(this);
		}
		return schema;
	}

	@Override
	public TransformationDAO getTransformationDao() {
		if (genTransformation == null) {
			// pass the Factory to get the connection
			genTransformation = new OracleTransformationDAO(this);
		}
		return genTransformation;
	}

}
