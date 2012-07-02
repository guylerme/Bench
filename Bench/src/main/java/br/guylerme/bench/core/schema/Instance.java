package br.guylerme.bench.core.schema;

import br.guylerme.bench.core.beans.InstanceBean;
import br.guylerme.bench.core.beans.Matchable;
import br.guylerme.bench.core.dao.DAOFactory;

import com.hp.hpl.jena.ontology.Individual;

public class Instance implements Matchable
{
	private Clss			clss		= null;
	private InstanceBean	ibean		= null;
	private Individual		individual	= null;

	protected Instance(
			final InstanceBean bean,
			final Clss clss,
			final Schema schema,
			final DAOFactory factory)
	{
		ibean = bean;
		this.clss = clss;
	}

	public InstanceBean getBean()
	{
		return ibean;
	}

	public String getContext()
	{
		return getBean().getContext();
	}

	public Individual getIndividual()
	{
		if (individual == null)
		{
			clss.loadJenaIndividuals();
		}
		return individual;
	}

	public long getMatchableid()
	{
		return ibean.getMatchableid();
	}

	public String getUri()
	{
		return getBean().getUri();
	}

	protected void setJenaIndividual(final Individual in)
	{
		individual = in;
	}

	public void setMatchableid(final long id)
	{
		ibean.setMatchableid(id);
	}
}
