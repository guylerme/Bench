/***********************************************************************
 * Module: NewDataSet.java Author: Owner Purpose: Defines the Class NewDataSet
 ***********************************************************************/
package br.guylerme.bench.core.commands.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.guylerme.bench.core.beans.CPropertyBean;
import br.guylerme.bench.core.beans.ClassBean;
import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.ElementBean;
import br.guylerme.bench.core.commands.AbstractCommand;
import br.guylerme.bench.core.commands.ReturnValue;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;


import br.guylerme.bench.core.commands.schema.exception.InvalidDataSetException;
import br.guylerme.bench.core.commands.schema.exception.SchemaDoesNotExistsException;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This command provides the functionality to create new datasets. To execute
 * properly, pleas provide the following parameters to execution: schemaid - The
 * id of the schema (Integer) path - The path of the file (String) name - The
 * name witch indentify the new dataset filename - the filename of the owl file
 * (String)
 * 
 * @author Raphael do Vale (rgomes@inf.puc-rio.br)
 */
public class NewDataSet extends AbstractCommand
{

	private void createDataSet(
			final int schemaid,
			final String filename,
			final String path,
			final String name)
			throws SchemaDoesNotExistsException,
			FactoryNotFoundException,
			DataSourceConnectionException,
			QueryException,
			InvalidDataSetException
	{
		getLogger().debug("Creating a dataset to the schemaid " + schemaid);
		// load the OWL file
		OntModel omodel;
		final File ont = new File(path + "\\" + filename);

		if (!ont.exists())
		{
			throw new SchemaDoesNotExistsException("The file was not found");
		}

		// create a new Jena Ontology
		final ModelMaker maker = ModelFactory.createFileModelMaker(path);

		final Model model = maker.openModel(filename, false);
		omodel = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MICRO_RULE_INF,
				model);
		getLogger().debug("Jena ontology instanced");
		// validate the schema
		DAOFactory dao = getDAO();
		SchemaDAO sdao = dao.getSchemaDao();
		List<String> namespaces = sdao.getValidNamespaces(schemaid);
		validateDataset(schemaid, omodel, namespaces);
		// now the schema is valid. So, create the dataset and the instances
		FileInputStream file;
		try
		{
			file = new FileInputStream(ont);

			final DataSetBean dbean = sdao.newDataSet(
					schemaid,
					filename,
					path,
					file,
					ont.length(),
					name);
			file.close();
			file = null;

			getLogger().debug("Dataset created");
			// ok, now the cake
			ExtendedIterator<Individual> individuals = omodel.listIndividuals();
			// getting the list of valid namespaces

			try
			{
				getLogger().debug(
						"Now interating with the individuals of the schemas");
				while (individuals.hasNext())
				{
					Individual indi = individuals.next();
					if (namespaces.contains(indi.getNameSpace()))
					{
						getLogger().debug(
								"New individual found. URI: " + indi.getURI());
						// TODO on an ontology, a instance could have more then
						// one
						// rdf:type. What we do?
						sdao.newInstance(
								schemaid,
								indi.getURI(),
								indi.getLabel(null),
								indi.getComment(null),
								indi.getOntClass(true).getURI(),
								dbean.getDatasetseq());
						getLogger().debug("Individual saved");
					}
					else
					{
						getLogger().debug(
								"A new individual was found but its namespace is not valid. Individual URI: "
										+ indi.getURI()
										+ ". Individual namespace: "
										+ indi.getNameSpace());
					}
					indi = null;
				}
				getLogger().debug("All individuals were saved");
				getLogger().info(
						"A new dataset were created for the schemaid "
								+ schemaid + ". The file used was: " + path
								+ "\\" + filename);
			}
			finally
			// free the resources
			{
				individuals.close();
				individuals = null;
			}

		}
		catch (final IOException ex)
		{
			throw new SchemaDoesNotExistsException("The file was not found");
		}
		finally
		// free the resources
		{
			namespaces = null;
			dao = null;
			sdao = null;
			omodel.close();
			omodel = null;
		}
	}

	@Override
	public ReturnValue executeCommand(final Map<String, Object> parameters)
			throws QueryException,
			DataSourceConnectionException,
			FactoryNotFoundException
	{
		ReturnValue value = null;
		// Collect the parameters

		final int schemaid = (Integer) parameters.get("schemaid");
		final String filename = (String) parameters.get("filename");
		final String path = (String) parameters.get("path");
		final String name = (String) parameters.get("name");
		try
		{
			createDataSet(schemaid, filename, path, name);
			value = new ReturnValue(false, null, null, null);

		}
		catch (final InvalidDataSetException e)
		{
			value = new ReturnValue(true, e.getMessage(), e, null);
		}
		catch (final SchemaDoesNotExistsException e)
		{
			value = new ReturnValue(true, e.getMessage(), e, null);
		}
		return value;
	}

	@Override
	public String getCommandName()
	{
		return "New DataSet";
	}

	private boolean searchElement(
			final String uri,
			final Set<? extends ElementBean> list)
	{
		final Iterator<? extends ElementBean> iterator = list.iterator();
		while (iterator.hasNext())
		{
			final ElementBean bean = iterator.next();
			if (bean.getUri().equals(uri))
			{
				list.remove(bean);
				return true;
			}
		}
		return false;
	}

	/**
	 * Validate the dataset with the schema. This method walk through all
	 * elements of the saved schema comparing with the elements of the dataset.
	 * If something is different, return false
	 * 
	 * @param schemaid
	 *            - the id of the schema on the database
	 * @param ontology
	 *            - the Jena ontology representing the dataset
	 * @throws InvalidDataSetException
	 *             if the dataset is invalid
	 * @throws QueryException
	 * @throws DataSourceConnectionException
	 * @throws FactoryNotFoundException
	 */
	private void validateDataset(
			final int schemaid,
			final OntModel omodel,
			final List<String> namespaces)
			throws InvalidDataSetException,
			DataSourceConnectionException,
			QueryException,
			FactoryNotFoundException
	{
		// To validate a dataset, whe have to get all classes and verify if they
		// (and its properties) are saved on the database.
		// get the schemas classes
		final DAOFactory dao = getDAO();
		final SchemaDAO sdao = dao.getSchemaDao();

		Set<ClassBean> classes = sdao.getSchemaClasses(schemaid);

		ExtendedIterator<OntClass> iclasses = omodel.listNamedClasses();

		try
		{
			while (iclasses.hasNext())
			{

				OntClass oclass = iclasses.next();
				if (namespaces.contains(oclass.getNameSpace()))
				{
					getLogger().debug(
							"Verifying if the class " + oclass.getURI()
									+ " exists in the datasource");
					// for each class, I verify if it exists on the database and
					// remove of the set classes
					if (searchElement(oclass.getURI(), classes))
					{
						// the class exists
						// now gets the properties
						getLogger().debug("The class exists");

						Set<CPropertyBean> properties = sdao
								.getClassProperties(schemaid, oclass.getURI());
						ExtendedIterator<OntProperty> iproperty = oclass
								.listDeclaredProperties(false);

						try
						{
							while (iproperty.hasNext())
							{
								OntProperty oprop = iproperty.next();

								if (namespaces.contains(oprop.getNameSpace()))
								{
									getLogger()
											.debug(
													"Verifying if the property "
															+ oprop.getURI()
															+ " exists in the datasource");
									// if the property does not exists
									if (!searchElement(
											oprop.getURI(),
											properties))
									{
										// throws the exception
										throw new InvalidDataSetException(
												"The dataset is invalid because the property "
														+ oprop.getURI()
														+ " does not exists on class "
														+ oclass.getURI()
														+ " on the datasource");
									}
								}
								oprop = null;
							}

							// verify if the Set properties is clean. If not,
							// the schemas are different.
							if (properties.size() > 0)
							{
								final Iterator<CPropertyBean> props = properties
										.iterator();
								String faultProps = "";
								while (props.hasNext())
								{
									faultProps += props.next().getUri();
									if (props.hasNext())
									{
										faultProps += ",";
									}
								}
								throw new InvalidDataSetException(
										"The dataset is invalid because the property(ies) "
												+ faultProps
												+ " does not exists on class "
												+ oclass.getURI()
												+ " on the new dataset");
							}
						}
						finally
						// free the resources
						{
							iproperty.close();
							iproperty = null;
							properties = null;
						}

					}
					else
					{
						// the class did not exists. Throw error
						throw new InvalidDataSetException(
								"The dataset is invalid because the class "
										+ oclass.getURI()
										+ " does not exists on the datasource");
					}
				}
				oclass = null;
			}
			// if the set classes still have classes, something is wrong.
			if (classes.size() > 0)
			{
				final Iterator<ClassBean> clas = classes.iterator();
				String faultclasses = "";
				while (clas.hasNext())
				{
					faultclasses += clas.next().getUri();
					if (clas.hasNext())
					{
						faultclasses += ",";
					}
				}
				throw new InvalidDataSetException(
						"The dataset is invalid because the class(es) "
								+ faultclasses
								+ " does not exists the new dataset");
			}

		}
		finally
		// free the resources
		{
			classes = null;
			iclasses.close();
			iclasses = null;
		}
		// if we reach here, everything is ok. End the method.
		getLogger().debug("The dataset is valid for the schema");
	}
}
