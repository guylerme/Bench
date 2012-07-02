/***********************************************************************
 * Module: NewSchema.java Author: Owner Purpose: Defines the Class NewSchema
 ***********************************************************************/
package br.guylerme.bench.core.commands.schema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.commands.AbstractCommand;
import br.guylerme.bench.core.commands.ReturnValue;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.exception.SchemaNameAlreadyExistsException;

import br.guylerme.bench.core.commands.schema.exception.SchemaDoesNotExistsException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This command provides the functionality to create new schemas. To execute
 * properly, pleas provide the following parameters to execution: path - The
 * path of the file (String) filename - the filename of the owl file (String)
 * validNamespace - a list of the valid namespaces on the ontology
 * (List<String>) name - The name of the schema
 * 
 * @author Raphael do Vale (rgomes@inf.puc-rio.br)
 */
public class NewSchema extends AbstractCommand {

	@SuppressWarnings("unchecked")
	@Override
	public ReturnValue executeCommand(final Map<String, Object> parameters)
			throws QueryException, DataSourceConnectionException,
			FactoryNotFoundException {
		ReturnValue value = null;
		// Collect the parameters

		final String path = (String) parameters.get("path");
		final String filename = (String) parameters.get("filename");
		final List<String> validNamespaces = (List<String>) parameters
				.get("validNamespace");
		final String name = (String) parameters.get("name");

		try {
			value = newSchema(path, filename, validNamespaces, name);
		} catch (final SchemaDoesNotExistsException e) {
			value = new ReturnValue(true, e.getMessage(), e, null);
		} catch (final SchemaNameAlreadyExistsException e) {
			value = new ReturnValue(true, e.getMessage(), e, null);
		} catch (final IOException e) {
			value = new ReturnValue(true, e.getMessage(), e, null);
		}
		return value;
	}

	@Override
	public String getCommandName() {
		return "New Schema";
	}

	
	private ReturnValue newSchema(final String path, final String filename,
	List<String> validNamespaces, final String name)
	throws SchemaDoesNotExistsException, FactoryNotFoundException,
	DataSourceConnectionException, QueryException,
	SchemaNameAlreadyExistsException, IOException 
	{

			
				System.out.println(resource.getURI() + " has property "
						+ s.getPredicate() + " with value " + s.getObject());
			}

		}
		
		
		
		
		Model model;

		// load the jena ontology
		final InputStream  in = FileManager.get().open(path + "\\" + filename);

		if (in ==null) {
			throw new SchemaDoesNotExistsException("The file was not found");
		}
		
		// create an empty model
				model = ModelFactory.createDefaultModel();
				
				// read the RDF/XML file
				model.read(in, null);
				

		// Search for the schema name on the database. If exists, return error.
		final DAOFactory dao = getDAO();
		final SchemaDAO sdao = dao.getSchemaDao();
		SchemaBean sbean = sdao.getSchema(name);
		// Create the schema on the database (and verify if the schema already
		// exists)

		sbean = sdao.newSchema(name, validNamespaces);

		// parse all classes
		ResIterator ri = model.listSubjects();
		while (ri.hasNext()) {
			Resource resource = ri.next();

			final String namespace = resource.getNameSpace();
			if (validNamespaces.contains(namespace)) {
				// is a valid namespace, continue
				getLogger().debug("New class found:" + resource.getURI());
				final String curi = resource.getURI();
				final String clabel = resource.getLocalName();
				final String ccomment = "";
				sdao.newClass(sbean.getSchemaid(), curi, clabel, ccomment);

				getLogger().debug(
						"The new class was saved. Searching for properties");

				// with the class saved, search for the properties
				StmtIterator si = resource.listProperties();
				
				while (si.hasNext()) {
					Statement s = si.next();
					
					final String pnamespace = s.getPredicate().getNameSpace();
					if (validNamespaces.contains(pnamespace)) {
						getLogger().debug(
								"New property found: " + s.getPredicate().getURI());
						final String puri = s.getPredicate().getURI();
						final String plabel = s.getPredicate().getLocalName();
						final String pcomment = "";
						final String datatype = s.getPredicate().;
						sdao.newProperty(sbean.getSchemaid(), puri, plabel,
								pcomment, datatype);
						// now insert the cproperty
						sdao.newCProperty(sbean.getSchemaid(), curi, puri);
						oproperty = null;
					}
				}
				iproperties.close();
				iproperties = null;
			}
		}
		iclasses.close();
		iclasses = null;
		validNamespaces = null;
		omodel.close();
		omodel = null;

		// now create a new dataset
		final NewDataSet newdataset = new NewDataSet();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("schemaid", sbean.getSchemaid());
		map.put("filename", filename);
		map.put("path", path);
		map.put("name", "Initial dataset");

		final ReturnValue value = newdataset.execute(map, this);
		return value;
	}
	
	/**
	 * Create and parse a new schema on the database
	 * 
	 * @param path
	 *            full path for the OWL file
	 * @param filename
	 *            the filename (including the extension) of the schema
	 * @param validNamespaces
	 *            array of strings containing all valid namespaces (if another
	 *            namespace appear on the parsing proccess, it will be ignored)
	 * @param name
	 *            the name of the schema. This will be the the identifier of the
	 *            schema on the database
	 * @throws FactoryNotFoundException
	 * @throws QueryException
	 * @throws DataSourceConnectionException
	 * @throws SchemaNameAlreadyExistsException
	 * @throws IOException
	 * @pdOid b617924b-c6d3-4844-b7d6-11820e501f8d
	 */
	private ReturnValue newOwlSchema(final String path, final String filename,
			List<String> validNamespaces, final String name)
			throws SchemaDoesNotExistsException, FactoryNotFoundException,
			DataSourceConnectionException, QueryException,
			SchemaNameAlreadyExistsException, IOException {
		OntModel omodel;

		// load the jena ontology
		final File ont = new File(path + "\\" + filename);

		if (!ont.exists()) {
			throw new SchemaDoesNotExistsException("The file was not found");
		}

		// create a new Jena Ontology
		final ModelMaker maker = ModelFactory.createFileModelMaker(path);
		final Model model = maker.openModel(filename, false);
		omodel = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);

		// Search for the schema name on the database. If exists, return error.
		final DAOFactory dao = getDAO();
		final SchemaDAO sdao = dao.getSchemaDao();
		SchemaBean sbean = sdao.getSchema(name);
		// Create the schema on the database (and verify if the schema already
		// exists)

		sbean = sdao.newSchema(name, validNamespaces);

		// parse all classes
		ExtendedIterator<OntClass> iclasses = omodel.listNamedClasses();
		while (iclasses.hasNext()) {
			final OntClass oclass = iclasses.next();

			final String namespace = oclass.getNameSpace();
			if (validNamespaces.contains(namespace)) {
				// is a valid namespace, continue
				getLogger().debug("New class found:" + oclass.getURI());
				final String curi = oclass.getURI();
				final String clabel = oclass.getLabel(null);
				final String ccomment = oclass.getComment(null);
				sdao.newClass(sbean.getSchemaid(), curi, clabel, ccomment);

				getLogger().debug(
						"The new class was saved. Searching for properties");

				// with the class saved, search for the properties
				ExtendedIterator<OntProperty> iproperties = oclass
						.listDeclaredProperties(false);

				while (iproperties.hasNext()) {
					OntProperty oproperty = iproperties.next();
					final String pnamespace = oproperty.getNameSpace();
					if (validNamespaces.contains(pnamespace)) {
						getLogger().debug(
								"New property found: " + oproperty.getURI());
						final String puri = oproperty.getURI();
						final String plabel = oproperty.getLabel(null);
						final String pcomment = oproperty.getComment(null);
						final String datatype = oproperty.getRange().getURI();
						sdao.newProperty(sbean.getSchemaid(), puri, plabel,
								pcomment, datatype);
						// now insert the cproperty
						sdao.newCProperty(sbean.getSchemaid(), curi, puri);
						oproperty = null;
					}
				}
				iproperties.close();
				iproperties = null;
			}
		}
		iclasses.close();
		iclasses = null;
		validNamespaces = null;
		omodel.close();
		omodel = null;

		// now create a new dataset
		final NewDataSet newdataset = new NewDataSet();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("schemaid", sbean.getSchemaid());
		map.put("filename", filename);
		map.put("path", path);
		map.put("name", "Initial dataset");

		final ReturnValue value = newdataset.execute(map, this);
		return value;
	}
}
