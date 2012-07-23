package br.guylerme.bench.core.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.guylerme.bench.core.beans.TransformationBean;
import br.guylerme.bench.core.dao.TransformationDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.QueryException;

public class MySQLTransformationDAO extends TransformationDAO {

	private static final String SQL_GET_TRANSFORMATION_ID = "SELECT COUNT(TRANSFORMATION_ID)+1 FROM `bench`.`transformation_mapping`;";
	private static final String SQL_NEW_TRANSFORMATION_MAPPING = "INSERT INTO `bench`.`transformation_mapping` (TRANSFORMATION_ID, SOURCE, TARGET, RULE) VALUES (?,?,?,?)";

	private MySQLDAOFactory factory;
	private Logger log = null;

	private PreparedStatement pstMatchable = null;

	protected MySQLTransformationDAO(final MySQLDAOFactory dao) {
		log = Logger.getLogger(MySQLSchemaDAO.class);
		log.debug("Instanciating a new object");
		factory = dao;
	}

	protected void close() {
		// cleans objects
		factory = null;
		if (pstMatchable != null) {
			try {
				pstMatchable.close();
			} catch (final SQLException e) {
				log.error("Unable to close a prepared statement", e);
			}
			pstMatchable = null;
		}
	}

	@Override
	public int getTransformationId() throws QueryException,
			DataSourceConnectionException {
		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		int transformationId = 0;

		try {
			pst = con.prepareStatement(SQL_GET_TRANSFORMATION_ID);

			rs = pst.executeQuery();
			if (rs.next()) {
				transformationId = rs.getInt(1);
			}
		} catch (final SQLException ex) {
			log.error(
					"An error ocurred when trying to get a schema by its name",
					ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error(
						"Error trying to close a resultset or a prepared statement",
						ex);
			}
			rs = null;
			pst = null;
		}
		return transformationId;
	}

	@Override
	public void newMapping(TransformationBean tbean) throws QueryException,
			DataSourceConnectionException {
		Connection con;
		PreparedStatement pst = null;
		ResultSet rs = null;

		// get the connection
		con = factory.getConnection();
		try {
			// get the next seq id
			pst = con.prepareStatement(SQL_NEW_TRANSFORMATION_MAPPING);
			pst.setInt(1, tbean.getTransformationId());
			pst.setString(2, tbean.getSource());
			pst.setString(3, tbean.getTarget());
			pst.setString(4, tbean.getRuleDescription());
			pst.executeUpdate();

			pst.close();

			con.commit();

		} catch (final SQLException ex) {
			log.error("Error trying to create a new Schema", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error(
						"Error trying to close a resultset or a prepared statement",
						ex);
			}
			rs = null;
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}

	}

	@Override
	public Map<String, String> newGeneralization(TransformationBean tbean,
			Map<String, String> tableList) {

		tableList.put("INSTANCE", "INSTANCE");

		// Query for element
		String SQL_ELEMENT = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), `LABEL`, `COMMENT` FROM (" + tableList.get("ELEMENT")
				+ ")";

		tableList.remove("ELEMENT");
		tableList.put("ELEMENT", SQL_ELEMENT);

		// Query for class
		String SQL_CLASS = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), MATCHABLEID FROM (" + tableList.get("CLASS") + ")";

		tableList.remove("CLASS");
		tableList.put("CLASS", SQL_CLASS);

		// Query for individual_class
		String SQL_INDIVIDUAL_CLASS = "SELECT `VALUE`, CLASS_SCHEMAID, REPLACE(CLASS_URI, \""
				+ tbean.getSource()
				+ "\", \""
				+ tbean.getTarget()
				+ "\") FROM (" + tableList.get("INDIVIDUAL_CLASS") + ")";

		tableList.remove("INDIVIDUAL_CLASS");
		tableList.put("INDIVIDUAL_CLASS", SQL_INDIVIDUAL_CLASS);

		// Query for property
		String SQL_PROPERTY = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), DATATYPE FROM (" + tableList.get("PROPERTY") + ")";

		tableList.remove("PROPERTY");
		tableList.put("PROPERTY", SQL_PROPERTY);

		// Query for cproperty
		String SQL_CPROPERTY = "SELECT CLASS_SCHEMAID, REPLACE(CLASS_URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), PROP_SCHEMAID, REPLACE(PROP_URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), MATCHABLEID FROM (" + tableList.get("CPROPERTY") + ")";

		tableList.remove("CPROPERTY");
		tableList.put("CPROPERTY", SQL_CPROPERTY);

		// Query for individual_property
		String SQL_INDIVIDUAL_PROPERTY = "SELECT `VALUE`, PROPERTY_SCHEMAID, REPLACE(PROPERTY_URI, \""
				+ tbean.getSource()
				+ "\", \""
				+ tbean.getTarget()
				+ "\"), INDIVIDUAL_CLASS_VALUE, INDIVIDUAL_CLASS_CLASS_SCHEMAID, REPLACE(INDIVIDUAL_CLASS_CLASS_URI, \""
				+ tbean.getSource()
				+ "\", \""
				+ tbean.getTarget()
				+ "\") FROM (" + tableList.get("INDIVIDUAL_PROPERTY") + ")";

		tableList.remove("INDIVIDUAL_PROPERTY");
		tableList.put("INDIVIDUAL_PROPERTY", SQL_INDIVIDUAL_PROPERTY);

		// Query for instance
		String SQL_INSTANCE = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), CLASS_SCHEMAID, REPLACE(CLASS_URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\") DATASETSEQ, MATCHABLEID FROM ("
				+ tableList.get("INSTANCE") + ")";

		tableList.remove("INSTANCE");
		tableList.put("INSTANCE", SQL_INSTANCE);

		return tableList;
	}

	@Override
	public Map<String, String> newSynonym(TransformationBean tbean,
			Map<String, String> tableList) {

		tableList.put("INSTANCE", "INSTANCE");

		// Query for element
		String SQL_ELEMENT = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), `LABEL`, `COMMENT` FROM (" + tableList.get("ELEMENT")
				+ ");";

		tableList.remove("ELEMENT");
		tableList.put("ELEMENT", SQL_ELEMENT);

		// Query for class
		String SQL_CLASS = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), MATCHABLEID FROM (" + tableList.get("CLASS") + ")";

		tableList.remove("CLASS");
		tableList.put("CLASS", SQL_CLASS);

		// Query for individual_class
		String SQL_INDIVIDUAL_CLASS = "SELECT `VALUE`, CLASS_SCHEMAID, REPLACE(CLASS_URI, \""
				+ tbean.getSource()
				+ "\", \""
				+ tbean.getTarget()
				+ "\") FROM (" + tableList.get("INDIVIDUAL_CLASS") + ");";

		tableList.remove("INDIVIDUAL_CLASS");
		tableList.put("INDIVIDUAL_CLASS", SQL_INDIVIDUAL_CLASS);

		// Query for property
		String SQL_PROPERTY = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), DATATYPE FROM (" + tableList.get("PROPERTY") + ")";

		tableList.remove("PROPERTY");
		tableList.put("PROPERTY", SQL_PROPERTY);

		// Query for cproperty
		String SQL_CPROPERTY = "SELECT CLASS_SCHEMAID, REPLACE(CLASS_URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), PROP_SCHEMAID, REPLACE(PROP_URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), MATCHABLEID FROM (" + tableList.get("CPROPERTY") + ")";

		tableList.remove("CPROPERTY");
		tableList.put("CPROPERTY", SQL_CPROPERTY);

		// Query for individual_property
		String SQL_INDIVIDUAL_PROPERTY = "SELECT `VALUE`, PROPERTY_SCHEMAID, REPLACE(PROPERTY_URI, \""
				+ tbean.getSource()
				+ "\", \""
				+ tbean.getTarget()
				+ "\"), INDIVIDUAL_CLASS_VALUE, INDIVIDUAL_CLASS_CLASS_SCHEMAID, REPLACE(INDIVIDUAL_CLASS_CLASS_URI, \""
				+ tbean.getSource()
				+ "\", \""
				+ tbean.getTarget()
				+ "\") FROM (" + tableList.get("INDIVIDUAL_PROPERTY") + ")";

		tableList.remove("INDIVIDUAL_PROPERTY");
		tableList.put("INDIVIDUAL_PROPERTY", SQL_INDIVIDUAL_PROPERTY);

		// Query for instance
		String SQL_INSTANCE = "SELECT SCHEMAID, REPLACE(URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\"), CLASS_SCHEMAID, REPLACE(CLASS_URI, \""
				+ tbean.getSource() + "\", \"" + tbean.getTarget()
				+ "\") DATASETSEQ, MATCHABLEID FROM ("
				+ tableList.get("INSTANCE") + ")";

		tableList.remove("INSTANCE");
		tableList.put("INSTANCE", SQL_INSTANCE);

		return tableList;
	}

	@Override
	public void createTransformedSchema(Map<String, String> tableList,
			int transformationId) throws DataSourceConnectionException,
			QueryException {

		String SQL_CREATE_VIEW = "";

		Set<String> chaves = tableList.keySet();

		Connection con;
		PreparedStatement pst = null;
		ResultSet rs = null;

		// get the connection
		con = factory.getConnection();
		try {

			for (Iterator<String> iterator = chaves.iterator(); iterator
					.hasNext();) {
				String chave = iterator.next();
				if (chave != null)
					SQL_CREATE_VIEW = "CREATE VIEW " + transformationId + "_"
							+ chave + " AS (" + tableList.get(chave) + ");";

				pst = con.prepareStatement(SQL_CREATE_VIEW);
				pst.execute();

				pst.close();

				con.commit();

			}

		} catch (final SQLException ex) {
			log.error("Error trying to create a new view", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error(
						"Error trying to close a resultset or a prepared statement",
						ex);
			}
			rs = null;
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}

	}
}
