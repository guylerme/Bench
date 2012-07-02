package br.guylerme.bench.core.commands.schema;

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
 * Return all information a schema Param: schemaid - the id of the schema Return
 * the following values. "schema" - (SchemaBean) the schema "datasets" -
 * (List<DataSetBean>) a list of datasets
 * 
 * @author Raphael do Vale
 */
public class GetSchema extends AbstractCommand {
	@Override
	public ReturnValue executeCommand(final Map<String, Object> parameters)
			throws QueryException, DataSourceConnectionException,
			FactoryNotFoundException

	{
		ReturnValue values = null;

		final int schemaid = (Integer) parameters.get("schemaid");
		// get all schemas.
		final SchemaDAO sdao = getDAO().getSchemaDao();
		// now get the schemas
		final SchemaBean bean = sdao.getSchema(schemaid);

		values = new ReturnValue();
		values.addValue("schema", bean);

		final List<DataSetBean> list = sdao.getAllDataSets(schemaid);
		values.addValue("datasets", list);

		return values;
	}

	@Override
	public String getCommandName() {
		return "Get Schema";
	}
}
