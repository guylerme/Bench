/***********************************************************************
 * Module: SchemaDAO.java Author: Owner Purpose: Defines the Class SchemaDAO
 ***********************************************************************/
package br.guylerme.bench.core.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import br.guylerme.bench.core.beans.CPropertyBean;
import br.guylerme.bench.core.beans.ClassBean;
import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.ElementBean;
import br.guylerme.bench.core.beans.InstanceBean;
import br.guylerme.bench.core.beans.Matchable;
import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.exception.SchemaNameAlreadyExistsException;

/**
 * This class manage all schemas entities (creation of schemas and datasets,
 * data recovery, etc)
 * 
 * @author Guylerme Figueiredo
 */
public abstract class SchemaDAO {

	/**
	 * Create a temporary file with the dataset ontology
	 * 
	 * @param schemaid
	 *            the id of the schema
	 * @param datasetseq
	 *            the sequence id of the dataset
	 * @return The file created
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract File createTemporatyOwlFile(int schemaid, int datasetseq)
			throws DataSourceConnectionException, QueryException, IOException;

	/**
	 * Get all datasets of a given schema
	 * 
	 * @param schemaid
	 * @return
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract List<DataSetBean> getAllDataSets(int schemaid)
			throws DataSourceConnectionException, QueryException;

	/**
	 * Get all schemas registered in the datasource
	 * 
	 * @return a list of schemas
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract List<SchemaBean> getAllSchemas()
			throws DataSourceConnectionException, QueryException;

	/**
	 * Return all instances of a given class in a specific dataset
	 * 
	 * @param schemaid
	 * @param dataSetSeq
	 * @param classURI
	 * @return a list of instances
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract Set<InstanceBean> getClassInstances(int schemaid,
			int dataSetSeq, String classURI)
			throws DataSourceConnectionException, QueryException;

	/**
	 * Return all properties of the given class
	 * 
	 * @param schemaid
	 * @param classURI
	 * @return a list of properties
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract Set<CPropertyBean> getClassProperties(int schemaid,
			String classURI) throws DataSourceConnectionException,
			QueryException;

	public abstract DataSetBean getDataSet(int schemaId, int dataSetSeq)
			throws DataSourceConnectionException, QueryException;

	/**
	 * Return the elementbean corresponding to the element. For example, if the
	 * URI represents a class, its returned a ClassBean. Etc.
	 * 
	 * @param uri
	 *            the URI of the element
	 * @param schemaid
	 *            the schemaid of the element
	 * @return the elementbean implementation corresponding the URI or Null if
	 *         nothing found.
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract ElementBean getElement(String uri, int schemaid)
			throws DataSourceConnectionException, QueryException;

	/**
	 * Return the matchable object corresponding the element.
	 * 
	 * @param matchable
	 * @return
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract Matchable getMatchable(long matchable)
			throws DataSourceConnectionException, QueryException;

	public abstract SchemaBean getSchema(int schemaId)
			throws DataSourceConnectionException, QueryException;

	/**
	 * @param schemaName
	 * @pdOid 9510aab9-2f07-4c81-a36e-6862e0bed01f
	 */
	public abstract SchemaBean getSchema(String schemaName)
			throws DataSourceConnectionException, QueryException;

	/**
	 * Return all classes of the given schema
	 * 
	 * @param schemaid
	 * @return a list with of classes
	 * @throws DataSourceConnectionException
	 * @throws QueryException
	 */
	public abstract Set<ClassBean> getSchemaClasses(int schemaid)
			throws DataSourceConnectionException, QueryException;

	public abstract List<String> getValidNamespaces(int schemaId)
			throws DataSourceConnectionException, QueryException;

	/** @pdOid bc5c2562-8c5b-4e2f-b94d-8016baf0944e */
	public abstract boolean newClass(int schemaId, String URI, String label,
			String comment) throws DataSourceConnectionException,
			QueryException;

	/** @pdOid 7250a729-4894-4717-b054-dc9158d37f74 */
	public abstract boolean newCProperty(int schemaId, String classURI,
			String propertyURI) throws DataSourceConnectionException,
			QueryException;

	/**
	 * @param schemaid
	 * @param filename
	 * @param path
	 * @pdOid 2480b477-a26f-4e6e-b5a8-49735f32d15e
	 */
	public abstract DataSetBean newDataSet(int schemaid, String filename,
			String path, InputStream file, long fileLength, String name)
			throws DataSourceConnectionException, QueryException;

	/** @pdOid 3d0bdd2b-5563-4c4b-a656-50864ebfc7cb */
	public abstract boolean newInstance(int schemaId, String URI, String label,
			String comment, String classURI, int dataSetSeq)
			throws DataSourceConnectionException, QueryException;

	/**
	 * @param schemaName
	 * @pdOid 93e4ce51-0c45-4b9d-90dc-6d4d77bdfd04
	 */
	public abstract SchemaBean newSchema(String schemaName,
			List<String> validNamespaces)
			throws SchemaNameAlreadyExistsException, QueryException,
			DataSourceConnectionException;

	public boolean newProperty(int schemaId, String URI, String label,
			String comment, String datatype, String value)
			throws DataSourceConnectionException, QueryException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean newProperty(int schemaId, String URI, String label,
			String comment, String datatype)
			throws DataSourceConnectionException, QueryException {
		// TODO Auto-generated method stub
		return false;
	}
}
