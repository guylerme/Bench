/***********************************************************************
 * Module: GetSchemas.java Author: Owner Purpose: Defines the Class GetSchemas
 ***********************************************************************/

package br.guylerme.bench.core.commands.schema;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.commands.AbstractCommand;
import br.guylerme.bench.core.commands.ReturnValue;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;

/** @pdOid 06322f23-9d9a-45d6-a5f7-8045b033f33c */
/**
 * Return all schemas saved on the datasource. If the param "getDatasets" is
 * true, create a list with all datasets of all schemas Return the following
 * values. "schemas" - (List<SchemaBean>) a list of schemas "datasets" -
 * (List<List<DataSetBean>>) a list of dataset, in the same order of the schema
 * list (only created if getDatasets = true)
 * 
 * @author Owner
 */
public class GetSchemas extends AbstractCommand {

	@Override
	public ReturnValue executeCommand(final Map<String, Object> parameters)
			throws QueryException, DataSourceConnectionException,
			FactoryNotFoundException {
		ReturnValue value = null;
		// verify if we have to get the datasets too.
		boolean datasets;
		if (parameters.get("getDatasets") != null) {
			datasets = (Boolean) parameters.get("getDatasets");
		} else {
			datasets = false;
		}

		// get all schemas.
		final SchemaDAO sdao = getDAO().getSchemaDao();
		// sdao.
		// now get the schemas
		final List<SchemaBean> schemasList = sdao.getAllSchemas();
		final Map<String, Object> returnValues = new THashMap<String, Object>();
		returnValues.put("schemas", schemasList);
		// if we have to show the datasets, iterate over schemas
		if (datasets) {

			final Iterator<SchemaBean> ischemas = schemasList.iterator();
			final List<List<DataSetBean>> datasetsList = new ArrayList<List<DataSetBean>>();

			while (ischemas.hasNext()) {
				final SchemaBean bean = ischemas.next();
				datasetsList.add(sdao.getAllDataSets(bean.getSchemaid()));
			}
			returnValues.put("datasets", datasetsList);
		}
		value = new ReturnValue(false, null, null, returnValues);
		return value;
	}

	@Override
	public String getCommandName() {
		return "Get Schemas";
	}
}
