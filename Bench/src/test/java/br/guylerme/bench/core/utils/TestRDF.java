package br.guylerme.bench.core.utils;

import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.commands.schema.NewSchema;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.exception.SchemaNameAlreadyExistsException;
import br.guylerme.bench.core.schema.CProperty;
import br.guylerme.bench.core.schema.Clss;
import br.guylerme.bench.core.schema.Schema;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TestRDF {

	private Map<String, Clss> classes = null;
	private TLongObjectHashMap<Clss> classesMatchable = null;
	private final DataSetBean dsbean = null;
	private final DAOFactory factory = null;
	private final static Logger log = null;
	private List<CProperty> properties = null;
	private final SchemaBean scbean = null;
	private OntModel schema = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputFileName = "c:/dataset-1610.rdf";

		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}

		// read the RDF/XML file
		model.read(in, null);

		ResIterator ri = model.listSubjects();
		while (ri.hasNext()) {
			Resource resource = ri.next();

			
			
			StmtIterator si = resource.listProperties();
			while (si.hasNext()) {
				Statement s = si.next();
				System.out.println(resource.getURI() + " has property "
						+ s.getPredicate() + " with value " + s.getObject());
			}

		}

	}

	private void testeCargaNewSchema() {
		// load the ontology

		NewSchema cmd = new NewSchema();

		final String path = "c:";
		final String filename = "dataset-1610.rdf";
		List<String> validNamespaces = new ArrayList<String>();
		validNamespaces.add("http://www.w3.org/2002/07/owl#");
		validNamespaces.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		validNamespaces.add("http://www.w3.org/2000/01/rdf-schema#");

		final String name = "Rank Gender Race";

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("filename", filename);
		parameters.put("validNamespace", validNamespaces);
		parameters.put("name", name);

		try {
			cmd.executeCommand(parameters);
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

	public OntModel getSchema() {

		if (schema == null) {
			try {
				// load the ontology
				final File file = factory.getSchemaDao()
						.createTemporatyOwlFile(scbean.getSchemaid(),
								dsbean.getDatasetseq());

				final String path = file.getParent();
				final String filename = file.getName();
				final ModelMaker maker = ModelFactory
						.createFileModelMaker(path);

				final Model model = maker.openModel(filename, false);

				schema = ModelFactory.createOntologyModel(
						OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
			} catch (final IOException e) {
				log.fatal(
						"An error ocurred when trying to load the jena schema",
						e);
			} catch (final DataSourceConnectionException e) {
				log.fatal(
						"An error ocurred when trying to load the jena schema",
						e);
			} catch (final QueryException e) {
				log.fatal(
						"An error ocurred when trying to load the jena schema",
						e);
			}
		}
		return schema;
	}

	private void testarPersistenciaECarga() {
		final File file = new File("c:/dataset-1610.rdf");
		String schemaName = "Rank Gender Race";
		List<String> validNamespaces = new ArrayList<String>();
		validNamespaces.add("Rank Gender Race");

		ComboPooledDataSource pool = new ComboPooledDataSource();

		DAOFactory dao = null;
		try {
			dao = DAOFactory.getDaoFactory();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SchemaBean schema2 = null;
		try {
			schema2 = dao.getSchemaDao().newSchema(schemaName, validNamespaces);
		} catch (SchemaNameAlreadyExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (QueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DataSourceConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			File f = new File("c:/dataset-1610.rdf");
			InputStream file2 = new FileInputStream(f);
			dao.getSchemaDao().newDataSet(schema2.getSchemaid(),
					"c:/dataset-1610.rdf", "http://www.data.gov", file2,
					f.length(), "dataset-1610.rdf");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final String path = file.getParent();
		final String filename = file.getName();
		final ModelMaker maker = ModelFactory.createFileModelMaker(path);

		final Model model = maker.openModel(filename, false);

		OntModel schema = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
	}

	private void testarCargaSchema() {
		SchemaBean scbean = new SchemaBean();
		scbean.setSchemaid(16);
		scbean.setName("Rank Gender Race");

		DataSetBean dsbean = new DataSetBean();
		dsbean.setDatasetseq(1);
		dsbean.setSchemaid(16);

		OntModel schema = null;

		try {
			// load the ontology
			final File file = DAOFactory
					.getDaoFactory()
					.getSchemaDao()
					.createTemporatyOwlFile(scbean.getSchemaid(),
							dsbean.getDatasetseq());

			final String path = file.getParent();
			final String filename = file.getName();
			final ModelMaker maker = ModelFactory.createFileModelMaker(path);

			final Model model = maker.openModel(filename, false);

			schema = ModelFactory.createOntologyModel(
					OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
		} catch (final IOException e) {
			log.fatal("An error ocurred when trying to load the jena schema", e);
		} catch (final DataSourceConnectionException e) {
			log.fatal("An error ocurred when trying to load the jena schema", e);
		} catch (final QueryException e) {
			log.fatal("An error ocurred when trying to load the jena schema", e);
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void testarLeituraClasses() {
		/**/
		SchemaBean scbean = new SchemaBean();
		scbean.setSchemaid(16);
		scbean.setName("Rank Gender Race");

		DataSetBean dsbean = new DataSetBean();
		dsbean.setDatasetseq(1);
		dsbean.setSchemaid(16);

		try {
			Schema schema = new Schema(scbean, dsbean,
					DAOFactory.getDaoFactory());

			schema.getSchema().listNamedClasses();

			Iterator i = schema.getAllClasses();
			Iterator j = schema.getAllCProperties();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
