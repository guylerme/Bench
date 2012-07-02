package br.guylerme.bench.core.schema;

import gnu.trove.TLongObjectHashMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.guylerme.bench.core.beans.CPropertyBean;
import br.guylerme.bench.core.beans.ClassBean;
import br.guylerme.bench.core.beans.InstanceBean;
import br.guylerme.bench.core.beans.Matchable;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.QueryException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class Clss implements Matchable
{

	private ClassBean						clBean				= null;
	private DAOFactory						factory				= null;
	private Map<String, Instance>			instances			= null;
	private OntClass						jenaClass			= null;
	private Logger							log					= null;
	private Map<String, CProperty>			properties			= null;
	private TLongObjectHashMap<CProperty>	propertiesMatchable	= null;
	private Schema							schema				= null;

	protected Clss(
			final ClassBean bean,
			final Schema schema,
			final DAOFactory factory)
	{
		clBean = bean;
		this.schema = schema;
		this.factory = factory;
		log = Logger.getLogger(this.getClass());
	}

	public Iterator<Instance> getAllInstances()
	{
		return getIndividuals().values().iterator();
	}

	public Iterator<CProperty> getAllProperties()
	{
		return getProperties().values().iterator();
	}

	public ClassBean getClBean()
	{
		return clBean;
	}

	public String getContext()
	{
		return getClBean().getContext();
	}

	public CProperty getCProperty(final long matchableid)
	{
		return getPropertiesMatchable().get(matchableid);
	}

	public CProperty getCProperty(final String uri)
	{
		return getProperties().get(uri);
	}

	private Map<String, Instance> getIndividuals()
	{
		if (instances == null)
		{
			try
			{
				instances = new HashMap<String, Instance>();
				final Set<InstanceBean> sInstanceBean = factory
						.getSchemaDao()
						.getClassInstances(
								schema.getSchemaBean().getSchemaid(),
								schema.getDataSetBean().getDatasetseq(),
								getClBean().getUri());

				final Iterator<InstanceBean> it = sInstanceBean.iterator();

				while (it.hasNext())
				{
					final InstanceBean bean = it.next();
					final Instance ins = new Instance(
							bean,
							this,
							schema,
							factory);
					instances.put(bean.getUri(), ins);
				}
			}
			catch (final DataSourceConnectionException e)
			{
				log
						.fatal(
								"It was not possible to recover the instances of a class.",
								e);
				properties = null;
			}
			catch (final QueryException e)
			{
				log
						.fatal(
								"It was not possible to recover the instances of a class.",
								e);
				properties = null;
			}
		}
		return instances;
	}

	public Instance getInstance(final String uri)
	{
		return getIndividuals().get(uri);
	}

	public OntClass getJenaClass()
	{
		if (jenaClass == null)
		{
			// load the class
			// to run faster, asks the schema to load all classes.
			schema.loadJenaClasses();
		}
		return jenaClass;
	}

		public long getMatchableid()
	{
		return clBean.getMatchableid();
	}

	private Map<String, CProperty> getProperties()
	{
		loadProperties();
		return properties;
	}

	private TLongObjectHashMap<CProperty> getPropertiesMatchable()
	{
		loadProperties();
		return propertiesMatchable;
	}

	public String getUri()
	{
		return getClBean().getUri();
	}

	protected void loadJenaIndividuals()
	{
		ExtendedIterator<? extends OntResource> iterindiv = getJenaClass()
				.listInstances(true);

		while (iterindiv.hasNext())
		{
			final OntResource res = iterindiv.next();
			if (res.isIndividual())
			{
				final Individual indi = (Individual) res;
				final Instance ins = getIndividuals().get(indi.getURI());
				if (ins != null)
				{
					ins.setJenaIndividual(indi);
				}
			}
		}
		iterindiv.close();
		iterindiv = null;
	}

	protected void loadJenaProperties()
	{
		// to do this, we ask jena all named classes
		ExtendedIterator<OntProperty> list = jenaClass.listDeclaredProperties();

		while (list.hasNext())
		{
			// for each existing class, we associate the jena class
			final OntProperty oprop = list.next();
			final CProperty cp = getProperties().get(oprop.getURI());

			if (cp != null)
			{
				cp.setJenaProperty(oprop);
			}
		}
		list.close();
		list = null;
	}

	private void loadProperties()
	{
		if (properties == null || propertiesMatchable == null)
		{
			try
			{
				properties = new HashMap<String, CProperty>();
				propertiesMatchable = new TLongObjectHashMap<CProperty>();
				final Set<CPropertyBean> sCPropertyBean = factory
						.getSchemaDao()
						.getClassProperties(
								schema.getSchemaBean().getSchemaid(),
								clBean.getUri());

				final Iterator<CPropertyBean> it = sCPropertyBean.iterator();

				while (it.hasNext())
				{
					final CPropertyBean bean = it.next();
					final CProperty cp = new CProperty(
							bean,
							this,
							schema,
							factory);
					properties.put(bean.getUri(), cp);
					propertiesMatchable.put(cp.getMatchableid(), cp);
				}
			}
			catch (final DataSourceConnectionException e)
			{
				log
						.fatal(
								"It was not possible to recover the properties of a class.",
								e);
				properties = null;
				propertiesMatchable = null;
			}
			catch (final QueryException e)
			{
				log
						.fatal(
								"It was not possible to recover the properties of a class.",
								e);
				properties = null;
				propertiesMatchable = null;
			}
		}
	}

	protected void setJenaClass(final OntClass jenaClass)
	{
		this.jenaClass = jenaClass;
	}

	public void setMatchableid(final long id)
	{
		clBean.setMatchableid(id);
	}
}
