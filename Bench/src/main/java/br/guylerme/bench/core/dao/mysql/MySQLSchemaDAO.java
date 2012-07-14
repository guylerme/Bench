/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package br.guylerme.bench.core.dao.mysql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.guylerme.bench.core.beans.CPropertyBean;
import br.guylerme.bench.core.beans.ClassBean;
import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.ElementBean;
import br.guylerme.bench.core.beans.InstanceBean;
import br.guylerme.bench.core.beans.Matchable;
import br.guylerme.bench.core.beans.PropertyBean;
import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.exception.SchemaNameAlreadyExistsException;

import org.apache.log4j.Logger;

/**
 * @author Guylerme Figueiredo
 */
class MySQLSchemaDAO extends SchemaDAO {

	private static final String SQL_GET_ALL_DATASETS = "SELECT SCHEMAID,DATASETSEQ,FILENAME,PATH,`DATE`,`NAME` FROM `bench`.`DATASET` WHERE SCHEMAID = ? ORDER BY DATASETSEQ";
	private static final String SQL_GET_ALL_SCHEMAS = "SELECT SCHEMAID,SCHEMANAME,CREATIONDATE FROM `bench`.`SCHEMA` ORDER BY SCHEMANAME";
	private static final String SQL_GET_CLASS_INSTANCES = ""
			+ "SELECT																							"
			+ "	i.SCHEMAID,i.URI,e.LABEL,e.COMMENT,i.MATCHABLEID												"
			+ "FROM 																							"
			+ "	`bench`.`ELEMENT` e INNER JOIN `bench`.`INSTANCE` i 											"
			+ "		ON (e.SCHEMAID = i.SCHEMAID AND e.URI = i.URI) 												"
			+ "WHERE																							"
			+ "	i.CLASS_SCHEMAID = ? AND i.CLASS_URI = ? AND i.DATASETSEQ = ?									";
	private static final String SQL_GET_CLASS_PROPERTY = ""
			+ "SELECT																							"
			+ "	e.SCHEMAID,e.URI,e.LABEL,e.COMMENT,p.DATATYPE, c.MATCHABLEID									"
			+ "FROM 																							"
			+ "	`bench`.`ELEMENT` e INNER JOIN `bench`.`PROPERTY` p 											"
			+ "		ON (e.SCHEMAID = p.SCHEMAID AND e.URI = p.URI) 												"
			+ "	INNER JOIN `bench`.`CPROPERTY` c 																"
			+ "		ON (p.SCHEMAID = c.PROP_SCHEMAID AND p.URI = c.PROP_URI)									"
			+ "WHERE																							"
			+ "	c.CLASS_SCHEMAID = ? AND c.CLASS_URI = ?														";
	private static final String SQL_GET_DATASET = "SELECT SCHEMAID,DATASETSEQ,FILENAME,PATH,`DATE`,`NAME` FROM `bench`.`DATASET` WHERE SCHEMAID = ? AND DATASETSEQ = ?";
	private static final String SQL_GET_DATASET_DATA = "SELECT FILENAME,`FILE` FROM `bench`.`DATASET` WHERE SCHEMAID = ? AND DATASETSEQ = ?";
	private static final String SQL_GET_ELEMENT = ""
			+ "SELECT																							"
			+ "	e.SCHEMAID,e.URI,e.LABEL,e.COMMENT,c.URI as 'class',c.MATCHABLEID as 'matchclass',				"
			+ "	p.URI as 'property',i.URI as 'instance',														"
			+ "	p.DATATYPE,i.CLASS_URI,i.DATASETSEQ, i.MATCHABLEID as 'matchInstance'							"
			+ "FROM																								"
			+ "	`bench`.`ELEMENT` e LEFT OUTER JOIN `bench`.`CLASS` c											"
			+ "		ON (e.SCHEMAID = c.SCHEMAID AND e.URI = c.URI)												"
			+ "	LEFT OUTER JOIN `bench`.`PROPERTY` p															"
			+ "		ON (e.SCHEMAID = p.SCHEMAID AND e.URI = p.URI)												"
			+ "	LEFT OUTER JOIN `bench`.`INSTANCE` i															"
			+ "		ON (e.SCHEMAID = i.SCHEMAID AND e.URI = i.URI)												"
			+ "WHERE																							"
			+ "	e.SCHEMAID = ? AND e.URI = ?																	";
	private static final String SQL_GET_MATCHABLE_CLS = ""
			+ "SELECT 																							"
			+ "	e.SCHEMAID,e.URI,e.LABEL,e.COMMENT,c.MATCHABLEID 												"
			+ "FROM 																							"
			+ "	`bench`.`ELEMENT` e INNER JOIN `bench`.`CLASS` c 												"
			+ "		ON( e.SCHEMAID = c.SCHEMAID AND e.URI = c.URI ) 											"
			+ "WHERE 																							"
			+ "	c.MATCHABLEID= ? 																				";
	private static final String SQL_GET_MATCHABLE_INS = ""
			+ "SELECT																							"
			+ "	i.SCHEMAID,i.URI,e.LABEL,e.COMMENT,i.MATCHABLEID,i.CLASS_URI									"
			+ "FROM 																							"
			+ "	`bench`.`ELEMENT` e INNER JOIN `bench`.`INSTANCE` i 											"
			+ "		ON (e.SCHEMAID = i.SCHEMAID AND e.URI = i.URI) 							   					"
			+ "WHERE																							"
			+ "	i.MATCHABLEID = ?																				";
	private static final String SQL_GET_MATCHABLE_PROP = ""
			+ "SELECT                                                       				                    "
			+ "    e.SCHEMAID,e.URI,e.LABEL,e.COMMENT,p.DATATYPE, c.MATCHABLEID,c.CLASS_URI 				    "
			+ "FROM                                                                             				"
			+ "    `bench`.`ELEMENT` e INNER JOIN `bench`.`PROPERTY` p                          				"
			+ "        ON (e.SCHEMAID = p.SCHEMAID AND e.URI = p.URI)                           				"
			+ "    INNER JOIN `bench`.`CPROPERTY` c 					                 						"
			+ "        ON (p.SCHEMAID = c.PROP_SCHEMAID AND p.URI = c.PROP_URI)                 				"
			+ "WHERE c.MATCHABLEID = ?                                                          				";
	private static final String SQL_GET_NEXT_MACHABLE_ID = "SELECT coalesce(max(MATCHABLEID),0) + 1 FROM `bench`.`MATCHABLE`";
	private static final String SQL_GET_NEXT_SEQ = "SELECT coalesce(max(DATASETSEQ),0) + 1 FROM `bench`.`DATASET` WHERE SCHEMAID = ? ";
	private static final String SQL_GET_SCHEMA = "SELECT SCHEMAID,SCHEMANAME,CREATIONDATE FROM `bench`.`SCHEMA` WHERE SCHEMANAME = ?";
	private static final String SQL_GET_SCHEMA_BY_ID = "SELECT SCHEMAID,SCHEMANAME,CREATIONDATE FROM `bench`.`SCHEMA` WHERE SCHEMAID = ?";
	private static final String SQL_GET_SCHEMA_CLASSES = ""
			+ "SELECT 																							"
			+ "	e.SCHEMAID,e.URI,e.LABEL,e.COMMENT,c.MATCHABLEID 												"
			+ "FROM 																							"
			+ "	`bench`.`ELEMENT` e INNER JOIN `bench`.`CLASS` c												"
			+ "		ON( e.SCHEMAID = c.SCHEMAID AND e.URI = c.URI )												"
			+ "WHERE 																							"
			+ "	e.SCHEMAID= ? 																					";

	private static final String SQL_GET_VALID_NAMESPACE = "SELECT `NAMESPACE` FROM `bench`.`VALIDNAMESPACE` WHERE SCHEMAID = ?";
	private static final String SQL_NEW_CLASS = "INSERT INTO `bench`.`CLASS` (SCHEMAID,URI,MATCHABLEID) VALUES (?,?,?)";
	private static final String SQL_NEW_CPROPERTY = "INSERT INTO `bench`.`CPROPERTY` (CLASS_SCHEMAID,CLASS_URI,PROP_SCHEMAID,PROP_URI,MATCHABLEID) VALUES (?,?,?,?,?)";
	private static final String SQL_NEW_DATASET = "INSERT INTO `bench`.`DATASET` (SCHEMAID,DATASETSEQ,FILENAME,PATH,DATE,`FILE`,`NAME`) VALUES(?,?,?,?,?,?,?)";
	private static final String SQL_NEW_ELEMENT = "INSERT INTO `bench`.`ELEMENT` (SCHEMAID,URI,LABEL,COMMENT) VALUES(?,?,?,?)";
	private static final String SQL_NEW_INSTANCE = "INSERT INTO `bench`.`INSTANCE` (SCHEMAID,URI,CLASS_SCHEMAID,CLASS_URI,DATASETSEQ,MATCHABLEID) VALUES (?,?,?,?,?,?)";
	private static final String SQL_NEW_MATCHABLE = "INSERT INTO `bench`.`MATCHABLE` (MATCHABLEID) VALUES (?)";
	private static final String SQL_NEW_PROPERTY = "INSERT INTO `bench`.`PROPERTY` (SCHEMAID,URI,DATATYPE) VALUES (?,?,?)";
	/******************************************
	 * SQL Queries BEGIN
	 */
	private static final String SQL_NEW_SCHEMA = "INSERT INTO `bench`.`SCHEMA` (SCHEMANAME,CREATIONDATE) VALUES(?,?)";
	private static final String SQL_NEW_VALID_NAMESPACE = "INSERT INTO `bench`.`VALIDNAMESPACE` (SCHEMAID,`NAMESPACE`) VALUES (?,?)";
	private MySQLDAOFactory factory;
	private Logger log = null;
	// these two variables are used to optimize the matchable insertion. The
	// PreparedStatement must be closed after the end of the factory.
	private long matchableid = -1;

	private PreparedStatement pstMatchable = null;

	/******************************************
	 * SQL Queries END
	 */
	protected MySQLSchemaDAO(final MySQLDAOFactory dao) {
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
	public File createTemporatyOwlFile(final int schemaid, final int datasetseq)
			throws DataSourceConnectionException, QueryException, IOException {
		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		File tempFile = null;
		try {
			pst = con.prepareStatement(SQL_GET_DATASET_DATA);
			pst.setInt(1, schemaid);
			pst.setInt(2, datasetseq);
			rs = pst.executeQuery();

			if (rs.next()) {
				final InputStream is = rs.getBinaryStream(2);
				// File file = new File(path + "\\tempfile.owl");
				tempFile = File.createTempFile("mysql", "Owldata");
				final FileOutputStream otemp = new FileOutputStream(tempFile);
				final byte buf[] = new byte[1024];
				int len;

				while ((len = is.read(buf)) > 0) {
					otemp.write(buf, 0, len);
				}
				otemp.close();
				is.close();
			}
		} catch (final SQLException ex) {
			log.error("An error ocurred when trying to get a dataset.", ex);
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
		return tempFile;
	}

	@Override
	public List<DataSetBean> getAllDataSets(final int schemaid)
			throws DataSourceConnectionException, QueryException {
		final List<DataSetBean> list = new LinkedList<DataSetBean>();

		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		DataSetBean bean = null;
		try {
			pst = con.prepareStatement(SQL_GET_ALL_DATASETS);
			pst.setInt(1, schemaid);
			rs = pst.executeQuery();
			while (rs.next()) {
				bean = new DataSetBean();
				bean.setDatasetseq(rs.getInt("DATASETSEQ"));
				bean.setDate(rs.getDate("DATE"));
				bean.setFilename(rs.getString("FILENAME"));
				bean.setName(rs.getString("NAME"));
				bean.setPath(rs.getString("PATH"));
				bean.setSchemaid(schemaid);

				list.add(bean);
			}
		} catch (final SQLException ex) {
			log.error("An error ocurred when trying to get all schemas", ex);
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
		return list;
	}

	@Override
	public List<SchemaBean> getAllSchemas()
			throws DataSourceConnectionException, QueryException {
		final List<SchemaBean> list = new LinkedList<SchemaBean>();

		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		SchemaBean schema = null;
		try {
			pst = con.prepareStatement(SQL_GET_ALL_SCHEMAS);

			rs = pst.executeQuery();
			while (rs.next()) {
				schema = new SchemaBean();
				schema.setSchemaid(rs.getInt(1));
				schema.setName(rs.getString(2));
				schema.setCreationDate(rs.getDate(3));
				list.add(schema);
			}
		} catch (final SQLException ex) {
			log.error("An error ocurred when trying to get all schemas", ex);
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
		return list;
	}

	@Override
	public Set<InstanceBean> getClassInstances(final int schemaid,
			final int dataSetSeq, final String classURI)
			throws DataSourceConnectionException, QueryException {
		final Set<InstanceBean> set = new HashSet<InstanceBean>();

		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(SQL_GET_CLASS_INSTANCES);
			pst.setInt(1, schemaid);
			pst.setString(2, classURI);
			pst.setInt(3, dataSetSeq);
			rs = pst.executeQuery();

			while (rs.next()) {
				final InstanceBean bean = new InstanceBean();
				// fill the bean
				bean.setSchemaid(rs.getInt("SCHEMAID"));
				bean.setUri(rs.getString("URI"));
				bean.setLabel(rs.getString("LABEL"));
				bean.setComment(rs.getString("COMMENT"));
				bean.setMatchableid(rs.getLong("MATCHABLEID"));
				bean.setClassURI(classURI);
				// add the bean to the set
				set.add(bean);
			}
		} catch (final SQLException ex) {
			log.error(
					"An error ocurred when trying to get the set of instances of a class",
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
		return set;
	}

	@Override
	public Set<CPropertyBean> getClassProperties(final int schemaid,
			final String classURI) throws DataSourceConnectionException,
			QueryException {

		final Set<CPropertyBean> set = new HashSet<CPropertyBean>();

		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(SQL_GET_CLASS_PROPERTY);
			pst.setInt(1, schemaid);
			pst.setString(2, classURI);
			rs = pst.executeQuery();

			while (rs.next()) {
				final CPropertyBean bean = new CPropertyBean();
				// fill the bean
				bean.setSchemaid(rs.getInt(1));
				bean.setUri(rs.getString(2));
				bean.setLabel(rs.getString(3));
				bean.setComment(rs.getString(4));
				bean.setDatatype(rs.getString(5));
				bean.setMatchableid(rs.getLong(6));
				bean.setClassURI(classURI);
				// add the bean to the set
				set.add(bean);
			}
		} catch (final SQLException ex) {
			log.error(
					"An error ocurred when trying to get the set of classes of the schema with id "
							+ schemaid, ex);
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
		return set;
	}

	@Override
	public DataSetBean getDataSet(final int schemaId, final int dataSetSeq)
			throws DataSourceConnectionException, QueryException {
		// SCHEMAID,DATASETSEQ,FILENAME,PATH,DATE,[FILE]
		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		DataSetBean dsbean = null;
		try {
			pst = con.prepareStatement(SQL_GET_DATASET);
			pst.setInt(1, schemaId);
			pst.setInt(2, dataSetSeq);
			rs = pst.executeQuery();

			if (rs.next()) {
				dsbean = new DataSetBean();
				dsbean.setDatasetseq(rs.getInt("DATASETSEQ"));
				dsbean.setDate(rs.getDate("DATE"));
				dsbean.setFilename(rs.getString("FILENAME"));
				dsbean.setPath(rs.getString("PATH"));
				dsbean.setName(rs.getString("NAME"));
				dsbean.setSchemaid(schemaId);
			}
		} catch (final SQLException ex) {
			log.error("An error ocurred when trying to get a dataset.", ex);
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
		return dsbean;
	}

	@Override
	public ElementBean getElement(final String uri, final int schemaid)
			throws DataSourceConnectionException, QueryException {
		log.debug("Trying to get an element");
		ElementBean bean = null;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		con = factory.getConnection();
		try {
			pst = con.prepareStatement(SQL_GET_ELEMENT);
			pst.setInt(1, schemaid);
			pst.setString(2, uri);
			rs = pst.executeQuery();

			if (rs.next()) {
				log.debug("The element was found. Creating the bean and indentifying the type");
				// ok, the element exists
				// verify what type of element is it
				if (rs.getObject("class") != null) {
					// is a class
					log.debug("The element is a class");
					final ClassBean cbean = new ClassBean();
					cbean.setMatchableid(rs.getLong("matchclass"));
					// put the matchableid
					bean = cbean;
				} else if (rs.getObject("property") != null) {
					// is a property
					log.debug("The element is a property");
					final PropertyBean pbean = new PropertyBean();
					pbean.setDatatype(rs.getString("datatype"));
					bean = pbean;
				} else if (rs.getObject("instance") != null) {
					// is a instace
					log.debug("The element is an instance");
					final InstanceBean ibean = new InstanceBean();
					ibean.setClassURI(rs.getString("class_uri"));
					ibean.setDataSetSeq(rs.getInt("datasetseq"));
					ibean.setMatchableid(rs.getLong("matchInstance"));
					bean = ibean;
				}
				// now treats the element itself
				bean.setUri(rs.getString(2));
				bean.setSchemaid(rs.getInt(1));
				bean.setLabel(rs.getString(3));
				bean.setComment(rs.getString(4));
			}
		} catch (final SQLException ex) {
			log.error("Error trying to get an element", ex);
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
				log.error("Error trying to close a prepared statement", ex);
			}
			rs = null;
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}
		return bean;
	}

	@Override
	public Matchable getMatchable(final long matchable)
			throws DataSourceConnectionException, QueryException {
		Matchable mat = null;
		try {
			// try a property
			PreparedStatement pst = factory.getConnection().prepareStatement(
					SQL_GET_MATCHABLE_PROP);
			pst.setLong(1, matchable);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				// is a cproperty
				final CPropertyBean bean = new CPropertyBean();
				// fill the bean
				bean.setSchemaid(rs.getInt(1));
				bean.setUri(rs.getString(2));
				bean.setLabel(rs.getString(3));
				bean.setComment(rs.getString(4));
				bean.setDatatype(rs.getString(5));
				bean.setMatchableid(rs.getLong(6));
				bean.setClassURI(rs.getString(7));
				mat = bean;
			}
			rs.close();
			pst.close();

			// try a instance
			if (mat == null) {
				pst = factory.getConnection().prepareStatement(
						SQL_GET_MATCHABLE_INS);
				pst.setLong(1, matchable);
				rs = pst.executeQuery();
				if (rs.next()) {
					final InstanceBean bean = new InstanceBean();
					// fill the bean
					bean.setSchemaid(rs.getInt("SCHEMAID"));
					bean.setUri(rs.getString("URI"));
					bean.setLabel(rs.getString("LABEL"));
					bean.setComment(rs.getString("COMMENT"));
					bean.setMatchableid(rs.getLong("MATCHABLEID"));
					bean.setClassURI(rs.getString("CLASS_URI"));
					mat = bean;
				}
			}
			rs.close();
			pst.close();
			// try a class
			if (mat == null) {
				pst = factory.getConnection().prepareStatement(
						SQL_GET_MATCHABLE_CLS);
				pst.setLong(1, matchable);
				rs = pst.executeQuery();
				if (rs.next()) {
					final ClassBean bean = new ClassBean();
					bean.setSchemaid(rs.getInt(1));
					bean.setUri(rs.getString(2));
					bean.setLabel(rs.getString(3));
					bean.setComment(rs.getString(4));
					bean.setMatchableid(rs.getLong("MATCHABLEID"));
					mat = bean;
				}
			}

		} catch (final SQLException e) {
			throw new QueryException(e);
		}
		return mat;
	}

	private synchronized long getNextMatchableid()
			throws DataSourceConnectionException, SQLException {
		if (matchableid == -1) {
			final Connection con = factory.getConnection();
			ResultSet rs;
			PreparedStatement pst = con
					.prepareStatement(SQL_GET_NEXT_MACHABLE_ID);
			rs = pst.executeQuery();
			rs.next();
			matchableid = rs.getLong(1);
			rs.close();
			pst.close();
			rs = null;
			pst = null;
		} else {
			matchableid++;
		}
		return matchableid;
	}

	@Override
	public SchemaBean getSchema(final int schemaId)
			throws DataSourceConnectionException, QueryException {
		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		SchemaBean schema = null;
		try {
			pst = con.prepareStatement(SQL_GET_SCHEMA_BY_ID);
			pst.setInt(1, schemaId);
			rs = pst.executeQuery();
			if (rs.next()) {
				schema = new SchemaBean();
				schema.setSchemaid(rs.getInt(1));
				schema.setName(rs.getString(2));
				schema.setCreationDate(rs.getDate(3));
			}
		} catch (final SQLException ex) {
			log.error("An error ocurred when trying to get a schema by its id",
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
		return schema;
	}

	@Override
	public SchemaBean getSchema(final String schemaName)
			throws DataSourceConnectionException, QueryException {

		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		SchemaBean schema = null;
		try {
			pst = con.prepareStatement(SQL_GET_SCHEMA);
			pst.setString(1, schemaName);
			rs = pst.executeQuery();
			if (rs.next()) {
				schema = new SchemaBean();
				schema.setSchemaid(rs.getInt(1));
				schema.setName(rs.getString(2));
				schema.setCreationDate(rs.getDate(3));
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
		return schema;
	}

	@Override
	public Set<ClassBean> getSchemaClasses(final int schemaid)
			throws DataSourceConnectionException, QueryException {
		final Set<ClassBean> set = new HashSet<ClassBean>();

		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(SQL_GET_SCHEMA_CLASSES);
			pst.setInt(1, schemaid);
			rs = pst.executeQuery();

			while (rs.next()) {
				final ClassBean bean = new ClassBean();
				// fill the bean
				bean.setSchemaid(rs.getInt(1));
				bean.setUri(rs.getString(2));
				bean.setLabel(rs.getString(3));
				bean.setComment(rs.getString(4));
				bean.setMatchableid(rs.getLong("MATCHABLEID"));
				// add the bean to the set
				set.add(bean);
			}
		} catch (final SQLException ex) {
			log.error(
					"An error ocurred when trying to get the set of classes of the schema with id "
							+ schemaid, ex);
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
		return set;
	}

	@Override
	public List<String> getValidNamespaces(final int schemaId)
			throws DataSourceConnectionException, QueryException {
		final Connection con = factory.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		final List<String> list = new LinkedList<String>();
		try {
			pst = con.prepareStatement(SQL_GET_VALID_NAMESPACE);
			pst.setInt(1, schemaId);
			rs = pst.executeQuery();

			if (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (final SQLException ex) {
			log.error(
					"An error ocurred when trying to get the list of valid namespaces of the schema with id "
							+ schemaId, ex);
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
		return list;
	}

	@Override
	public boolean newClass(final int schemaId, final String URI,
			final String label, final String comment)
			throws DataSourceConnectionException, QueryException {
		log.debug("Client asks for a new class with URI value equals to " + URI);

		Connection con = factory.getConnection();
		PreparedStatement pst = null;
		try {
			// first create the element
			newElement(schemaId, URI, label, comment);
			// Create the machable object
			final long mid = newMatchable();

			// with the element created, create the class instance

			pst = con.prepareStatement(SQL_NEW_CLASS);
			pst.setInt(1, schemaId);
			pst.setString(2, URI);
			pst.setLong(3, mid);

			if (!this.exists("CLASS", schemaId, URI))
				pst.executeUpdate();

		} catch (final SQLException ex) {
			log.error("Error trying to create a new Class", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error("Error trying to close a prepared statement", ex);
			}
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}
		return true;
	}

	@Override
	public boolean newCProperty(final int schemaId, final String classURI,
			final String propertyURI) throws DataSourceConnectionException,
			QueryException {
		log.debug("Client asks for a new CProperty with Class URI equals to "
				+ classURI + " and property URI equals to " + propertyURI);

		Connection con = factory.getConnection();
		PreparedStatement pst = null;
		try {
			// Create a new tuple of CProperty on the database
			final long mid = newMatchable();
			pst = con.prepareStatement(SQL_NEW_CPROPERTY);
			pst.setInt(1, schemaId);
			pst.setString(2, classURI);
			pst.setInt(3, schemaId);
			pst.setString(4, propertyURI);
			pst.setLong(5, mid);
			pst.executeUpdate();

			con.commit();

		} catch (final SQLException ex) {
			log.error("Error trying to create a new CProperty", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error("Error trying to close a prepared statement", ex);
			}
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}
		return true;
	}

	@Override
	public DataSetBean newDataSet(final int schemaid, final String filename,
			final String path, final InputStream file, final long fileLength,
			final String name) throws DataSourceConnectionException,
			QueryException {

		Connection con;
		PreparedStatement pst = null;
		ResultSet rs = null;
		DataSetBean dsbean = null;

		// get the connection
		con = factory.getConnection();
		try {
			// get the next seq id
			pst = con.prepareStatement(SQL_GET_NEXT_SEQ);
			pst.setInt(1, schemaid);
			rs = pst.executeQuery();
			rs.next();
			final int newSeq = rs.getInt(1);
			rs.close();
			pst.close();

			log.debug("The new dataset will have the sequence id:" + newSeq);

			// now insert the new dataset
			pst = con.prepareStatement(SQL_NEW_DATASET);
			pst.setInt(1, schemaid);
			pst.setInt(2, newSeq);
			pst.setString(3, filename);
			pst.setString(4, path);
			pst.setString(5, name);
			final Date date = new Date();
			final java.sql.Date datesql = new java.sql.Date(date.getTime());
			pst.setDate(5, datesql);
			pst.setBinaryStream(6, file, (int) fileLength);
			pst.setString(7, name);
			pst.executeUpdate();

			con.commit();

			// everything ok
			dsbean = new DataSetBean();
			dsbean.setDatasetseq(newSeq);
			dsbean.setDate(date);
			dsbean.setSchemaid(schemaid);
			dsbean.setFilename(filename);
			dsbean.setPath(path);
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

		return dsbean;
	}

	private void newElement(final int schemaId, final String URI,
			final String label, final String comment)
			throws DataSourceConnectionException, SQLException {
		log.debug("Creating a new element of URI " + URI);
		Connection con = null;
		PreparedStatement pst = null;

		con = factory.getConnection();
		pst = con.prepareStatement(SQL_NEW_ELEMENT);
		pst.setInt(1, schemaId);
		pst.setString(2, URI);
		pst.setString(3, label);
		pst.setString(4, comment);

		if (!this.exists("ELEMENT", schemaId, URI))
			pst.executeUpdate();

		con.commit();
	}

	@Override
	public boolean newInstance(final int schemaId, final String URI,
			final String label, final String comment, final String classURI,
			final int dataSetSeq) throws DataSourceConnectionException,
			QueryException {
		log.debug("Client asks for a new instance with URI value equals to "
				+ URI + ". Another info: Schemaid" + schemaId + ". classURI: "
				+ classURI + ". dataSetSeq: " + dataSetSeq);

		Connection con = factory.getConnection();
		PreparedStatement pst = null;
		try {
			// first create the element
			// this action could fail (because the same instance could be in
			// more than one dataset
			try {
				newElement(schemaId, URI, label, comment);
			} catch (final SQLException e) {
				// lets ignore it
			}

			// with the element created, create the class instance
			final long mid = newMatchable();
			pst = con.prepareStatement(SQL_NEW_INSTANCE);
			pst.setInt(1, schemaId);
			pst.setString(2, URI);
			pst.setInt(3, schemaId);
			pst.setString(4, classURI);
			pst.setInt(5, dataSetSeq);
			pst.setLong(6, mid);

			pst.executeUpdate();
		} catch (final SQLException ex) {
			log.error("Error trying to create a new Instance", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error("Error trying to close a prepared statement", ex);
			}
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}
		return true;
	}

	/**
	 * Create a new matchableid and return the id V2 (first attempt to optimize)
	 * This class must be executed in a non-concurrency way. So we use the
	 * synchronized keyword
	 * 
	 * @return
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	private synchronized long newMatchable()
			throws DataSourceConnectionException, QueryException {
		log.debug("Create a new matchable object");
		Connection con;
		ResultSet rs = null;
		long id = -1;

		// get the connection
		con = factory.getConnection();
		try {
			// get the next seq id
			id = getNextMatchableid();

			// now insert the new matchable object
			if (pstMatchable == null) {
				pstMatchable = con.prepareStatement(SQL_NEW_MATCHABLE);
			}
			pstMatchable.setLong(1, id);
			pstMatchable.executeUpdate();

			// everything ok
		} catch (final SQLException ex) {
			log.error("Error trying to create a new matchable object", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (final SQLException ex) {
				log.error(
						"Error trying to close a resultset or a prepared statement",
						ex);
			}
			rs = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}

		return id;

	}

	@Override
	public boolean newProperty(final int schemaId, final String URI,
			final String label, final String comment, final String datatype)
			throws DataSourceConnectionException, QueryException {
		log.debug("Client asks for a new property with URI value equals to "
				+ URI);
		// A property may be related with various classes. So, search if the
		// property is already saved.
		final ElementBean bean = getElement(URI, schemaId);
		if (bean != null) {
			log.debug("An element with the same URI already exists. Validate if its a property or not.");
			// two cases: if the elementbean is a instance of propertybean (wich
			// is correct)
			// and is not a instance (witch is a error.)
			if (bean instanceof PropertyBean) {
				return true;
			} else {
				return false;
			}
		}
		// if we reach here, we have to create the property
		log.debug("Creating a new property");
		Connection con = factory.getConnection();
		PreparedStatement pst = null;
		try {
			// first create the element
			newElement(schemaId, URI, label, comment);

			// with the element created, create the class instance

			pst = con.prepareStatement(SQL_NEW_PROPERTY);
			pst.setInt(1, schemaId);
			pst.setString(2, URI);
			pst.setString(3, datatype);

			if (!this.exists("PROPERTY", schemaId, URI))
				pst.executeUpdate();

			con.commit();
		} catch (final SQLException ex) {
			log.error("Error trying to create a new Property", ex);
			throw new QueryException(ex);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (final SQLException ex) {
				log.error("Error trying to close a prepared statement", ex);
			}
			pst = null;
			con = null; // does not close the connection (It will be closed by
			// the command)
		}
		return true;
	}

	@Override
	public SchemaBean newSchema(final String schemaName,
			final List<String> validNamespaces)
			throws SchemaNameAlreadyExistsException,
			DataSourceConnectionException, QueryException {

		log.debug("Client asks for a new schema with the following values:"
				+ "schemaName = " + schemaName);
		// verify if the schemaName already exists
		SchemaBean sbean = null;
		if (this.getSchema(schemaName) != null) {
			log.debug("The schema name already exists in the datasource");
			throw new SchemaNameAlreadyExistsException();
		}
		Connection con = factory.getConnection();
		PreparedStatement pst = null;
		PreparedStatement pstName = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(SQL_NEW_SCHEMA,
					Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, schemaName);
			final Date schemadate = new Date();
			final java.sql.Date sqldate = new java.sql.Date(
					schemadate.getTime());
			pst.setDate(2, sqldate);
			pst.executeUpdate();

			con.commit();

			rs = pst.getGeneratedKeys();
			rs.next();
			final int schemaid = rs.getInt(1);

			// now save the valid namespaces
			if (validNamespaces != null && validNamespaces.size() > 0) {
				final Iterator<String> namespaces = validNamespaces.iterator();
				pstName = con.prepareStatement(SQL_NEW_VALID_NAMESPACE);
				pstName.setInt(1, schemaid);
				while (namespaces.hasNext()) {
					final String namespace = namespaces.next();
					pstName.setString(2, namespace);
					pstName.executeUpdate();
				}
			}

			// create the bean
			sbean = new SchemaBean();
			sbean.setName(schemaName);
			sbean.setSchemaid(schemaid);
			sbean.setCreationDate(schemadate);

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
				if (pstName != null) {
					pstName.close();
				}

			} catch (final SQLException ex) {
				log.error(
						"Error trying to close a resultset or a prepared statement",
						ex);
			}
			rs = null;
			pst = null;
			pstName = null;
			con = null;
		}
		return sbean;
	}

	private boolean exists(String tableName, long schemaId, String uri) {
		Connection con = null;
		try {
			con = factory.getConnection();
		} catch (DataSourceConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PreparedStatement pst = null;
		PreparedStatement pstName = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement("SELECT * FROM `" + tableName
					+ "` WHERE SCHEMAID = " + schemaId + " AND URI = '" + uri
					+ "';");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			rs = pst.executeQuery();

			if (rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
