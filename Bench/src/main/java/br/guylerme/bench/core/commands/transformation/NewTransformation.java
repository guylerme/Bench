/***********************************************************************
 * Module: NewDataSet.java Author: Owner Purpose: Defines the Class NewDataSet
 ***********************************************************************/
package br.guylerme.bench.core.commands.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.guylerme.bench.core.beans.TransformationBean;
import br.guylerme.bench.core.commands.AbstractCommand;
import br.guylerme.bench.core.commands.ReturnValue;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.TransformationDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;

/**
 * This command provides the functionality to create new transformation. To
 * execute properly, pleas provide the following parameters to execution: list
 * of Transformations that have: source - The source that will be transformed,
 * target - The source transformed by rule, rule - The transformation rule
 * 
 * @author Guylerme Figueiredo
 */
public class NewTransformation extends AbstractCommand {

	private void transform(final List<TransformationBean> transformations) {
		getLogger().debug("Transformation initializing");

		List<TransformationBean> orderedTransformation = this
				.order(transformations);

		Map<String, String> tableList = new HashMap<String, String>();

		tableList.put("ELEMENT", "ELEMENT");
		tableList.put("CLASS", "CLASS");
		tableList.put("INDIVIDUAL_CLASS", "INDIVIDUAL_CLASS");
		tableList.put("PROPERTY", "PROPERTY");
		tableList.put("CPROPERTY", "CPROPERTY");
		tableList.put("INDIVIDUAL_PROPERTY", "INDIVIDUAL_PROPERTY");
		tableList.put("INSTANCE", "INSTANCE");

		int transformationId;
		try {
			final DAOFactory dao = getDAO();
			final TransformationDAO tdao = dao.getTransformationDao();

			transformationId = tdao.getTransformationId();

			for (TransformationBean tbean : orderedTransformation) {

				tbean.setTransformationId(transformationId);

				tdao.newMapping(tbean);

				switch (tbean.getRule()) {
				case TransformationBean.GENERALIZATION:

					tableList = tdao.newGeneralization(tbean, tableList);

					break;

				case TransformationBean.SYNONYM:

					tableList = tdao.newSynonym(tbean, tableList);

					break;

				default:
					break;
				}

			}
			
			tdao.createTransformedSchema(tableList, transformationId);
			
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private List<TransformationBean> order(
			List<TransformationBean> transformations) {

		List<TransformationBean> orderedTransformations = new ArrayList<TransformationBean>();

		while (transformations.size() != orderedTransformations.size()) {

			int fim = transformations.size();

			for (int i = 0; i < fim; i++) {
				TransformationBean tbean = transformations.get(i);

				if (tbean != null
						&& !this.existsInTarget(tbean.getSource(),
								transformations)) {
					orderedTransformations.add(tbean);
					transformations.set(i, null);
				}
			}

		}

		return orderedTransformations;
	}

	private boolean existsInTarget(String source,
			List<TransformationBean> transformations) {

		for (int i = 0; i < transformations.size(); i++) {
			TransformationBean tbean = transformations.get(i);
			if (tbean != null && tbean.getTarget().equalsIgnoreCase(source))
				return true;
		}

		return false;
	}

	@Override
	public ReturnValue executeCommand(Map<String, Object> parameters)
			throws QueryException, DataSourceConnectionException,
			FactoryNotFoundException {
		ReturnValue value = null;
		// Collect the parameters

		final List<TransformationBean> transformations = (List<TransformationBean>) parameters
				.get("transformations");

		transform(transformations);
		value = new ReturnValue(false, null, null, null);

		return value;
	}

	@Override
	public String getCommandName() {
		return "New Generalization Transformation";
	}

}
