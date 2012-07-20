/***********************************************************************
 * Module: NewDataSet.java Author: Owner Purpose: Defines the Class NewDataSet
 ***********************************************************************/
package br.guylerme.bench.core.commands.schema;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import br.guylerme.bench.core.commands.AbstractCommand;
import br.guylerme.bench.core.commands.ReturnValue;
import br.guylerme.bench.core.commands.schema.exception.InvalidDataSetException;
import br.guylerme.bench.core.commands.schema.exception.SchemaDoesNotExistsException;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

/**
 * This command provides the functionality to create new individuals elements.
 * To execute properly, pleas provide the following parameters to execution:
 * schemaid - The id of the schema (Integer) path - The path of the file
 * (String) name - The name witch indentify the new individual elements filename
 * - the filename of the owl file (String)
 * 
 * @author Guylerme Figueiredo
 */
public class NewIndividualElements extends AbstractCommand {

	private void createElementIndividual(final int schemaid,
			final String filename, final String path)
			throws SchemaDoesNotExistsException, FactoryNotFoundException,
			DataSourceConnectionException, QueryException,
			InvalidDataSetException, SQLException, OntologyLoadException {
		getLogger().debug("Creating a dataset to the schemaid " + schemaid);
		// load the OWL file
		InputStream in = null;
		try {
			in = new FileInputStream(path + "\\" + filename);
		} catch (FileNotFoundException e) {
			throw new SchemaDoesNotExistsException("The file was not found");

		}

		// create a new Protege Ontology
		OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(in);

		getLogger().debug("Protege ontology instanced");

		String className;
		String classValue;
		String propertyName;
		String propertyValue;

		DAOFactory dao = getDAO();
		SchemaDAO sdao = dao.getSchemaDao();

		// get the Class list
		Collection classes = owlModel.getUserDefinedOWLNamedClasses();
		for (Iterator it = classes.iterator(); it.hasNext();) {
			OWLNamedClass cls = (OWLNamedClass) it.next();
			Collection instances = cls.getInstances(false);

			for (Iterator jt = instances.iterator(); jt.hasNext();) {
				OWLIndividual individual = (OWLIndividual) jt.next();

				Collection propList = individual.getRDFProperties();

				Iterator itt = propList.iterator();

				while (itt.hasNext()) {

					RDFProperty datatypeProp = (RDFProperty) itt.next();
					className = individual.getRDFType().getName(); // classe
					classValue = individual.getBrowserText(); // instancia da
																// classe
					propertyName = datatypeProp.getName(); // propriedade
					propertyValue = individual.getPropertyValue(datatypeProp)
							.toString(); // instancia da propriedade

					// insert new element individual for class
					sdao.newIndividualClass(className, schemaid, "CLASS",
							classValue);

					// insert new element individual for class
					sdao.newIndividualProperty(propertyName, schemaid,
							"PROPERTY", propertyValue, className, classValue);
				}

			}
		}

	}

	@Override
	public ReturnValue executeCommand(Map<String, Object> parameters)
			throws QueryException, DataSourceConnectionException,
			FactoryNotFoundException {
		ReturnValue value = null;
		// Collect the parameters

		final int schemaid = (Integer) parameters.get("schemaid");
		final String filename = (String) parameters.get("filename");
		final String path = (String) parameters.get("path");

		try {
			createElementIndividual(schemaid, filename, path);
			value = new ReturnValue(false, null, null, null);

		} catch (final InvalidDataSetException e) {
			value = new ReturnValue(true, e.getMessage(), e, null);
		} catch (final SchemaDoesNotExistsException e) {
			value = new ReturnValue(true, e.getMessage(), e, null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OntologyLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public String getCommandName() {
		return "New Element Individual";
	}

}
