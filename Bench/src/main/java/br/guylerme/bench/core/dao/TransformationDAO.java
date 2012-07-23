/***********************************************************************
 * Module: SchemaDAO.java Author: Owner Purpose: Defines the Class SchemaDAO
 ***********************************************************************/
package br.guylerme.bench.core.dao;

import java.util.Map;

import br.guylerme.bench.core.beans.TransformationBean;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.QueryException;

/**
 * This class manage all schemas entities (creation of schemas and datasets,
 * data recovery, etc)
 * 
 * @author Guylerme Figueiredo
 */
public abstract class TransformationDAO {

	public abstract int getTransformationId() throws QueryException,
			DataSourceConnectionException;

	public abstract void newMapping(TransformationBean tbean)
			throws QueryException, DataSourceConnectionException;

	public abstract Map<String, String> newGeneralization(
			TransformationBean transformation, Map<String, String> tableList);

	public abstract Map<String, String> newSynonym(TransformationBean tbean,
			Map<String, String> tableList);

	public abstract void createTransformedSchema(Map<String, String> tableList,
			int transformationId) throws DataSourceConnectionException,
			QueryException;
}
