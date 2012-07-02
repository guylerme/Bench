package br.guylerme.bench.core.schema;

import gnu.trove.TLongObjectHashMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.guylerme.bench.core.beans.ClassBean;
import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.QueryException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Schema {

	private Map<String, Clss> classes = null;
	private TLongObjectHashMap<Clss> classesMatchable = null;
	private final DataSetBean dsbean;
	private final DAOFactory factory;
	private final Logger log;
	private List<CProperty> properties = null;
	private final SchemaBean scbean;
	private OntModel schema = null;

	public Schema(final SchemaBean scbean, final DataSetBean dsbean,
			final DAOFactory factory) {
		this.scbean = scbean;
		this.dsbean = dsbean;
		this.factory = factory;
		log = Logger.getLogger(this.getClass());
	}

	public Iterator<Clss> getAllClasses() {
		return getClasses().values().iterator();
	}

	public Iterator<CProperty> getAllCProperties() {
		log.debug("Getting all cproperties of the schema");
		// if its null, we load the structure.
		if (properties == null) {
			// load
			properties = new LinkedList<CProperty>();
			// to do this, we iterate in all classes of the schema and get all
			// its properties
			Iterator<Clss> classes = getAllClasses();

			while (classes.hasNext()) {
				final Clss clas = classes.next();
				Iterator<CProperty> itprop = clas.getAllProperties();

				while (itprop.hasNext()) {
					properties.add(itprop.next());
				}
				itprop = null;
			}
			classes = null;
		}

		return properties.iterator();
	}

	public Clss getClass(final long matchableid) {
		loadClasses();
		return classesMatchable.get(matchableid);
	}

	public Clss getClass(final String uri) {
		return getClasses().get(uri);
	}

	private Map<String, Clss> getClasses() {
		loadClasses();
		return classes;
	}

	private TLongObjectHashMap<Clss> getClassesMatchable() {
		loadClasses();
		return classesMatchable;
	}

	public DataSetBean getDataSetBean() {
		return dsbean;
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

	public SchemaBean getSchemaBean() {
		return scbean;
	}

	private void loadClasses() {
		if (classes == null || classesMatchable == null) {
			try {
				classes = new HashMap<String, Clss>();
				classesMatchable = new TLongObjectHashMap<Clss>();
				final Set<ClassBean> sClassesBean = factory.getSchemaDao()
						.getSchemaClasses(scbean.getSchemaid());

				final Iterator<ClassBean> it = sClassesBean.iterator();

				while (it.hasNext()) {
					final ClassBean bean = it.next();
					final Clss newClass = new Clss(bean, this, factory);
					classes.put(bean.getUri(), newClass);
					classesMatchable.put(newClass.getMatchableid(), newClass);
				}
			} catch (final DataSourceConnectionException e) {
				log.fatal(
						"It was not possible to recover the classes of a schema.",
						e);
				classes = null;
				classesMatchable = null;
			} catch (final QueryException e) {
				log.fatal(
						"It was not possible to recover the classes of a schema.",
						e);
				classes = null;
				classesMatchable = null;
			}
		}
	}

	protected void loadJenaClasses() {
		// to do this, we ask jena all named classes
		ExtendedIterator<OntClass> list = getSchema().listNamedClasses();

		while (list.hasNext()) {
			// for each existing class, we associate the jena class
			final OntClass oclass = list.next();
			final Clss clss = getClasses().get(oclass.getURI());
			if (clss != null) {
				clss.setJenaClass(oclass);
			}
		}
		list.close();
		list = null;
	}
}
