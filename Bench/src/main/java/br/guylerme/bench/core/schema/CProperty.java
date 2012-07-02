package br.guylerme.bench.core.schema;

import br.guylerme.bench.core.beans.CPropertyBean;
import br.guylerme.bench.core.beans.Matchable;
import br.guylerme.bench.core.dao.DAOFactory;

import com.hp.hpl.jena.ontology.OntProperty;

public class CProperty implements Matchable {
	private Clss clss = null;
	private CPropertyBean cpbean = null;
	private OntProperty ontProperty = null;

	protected CProperty(final CPropertyBean bean, final Clss clss,
			final Schema schema, final DAOFactory factory) {
		cpbean = bean;
		this.clss = clss;
	}

	public CPropertyBean getBean() {
		return cpbean;
	}

	public String getContext() {
		return getBean().getContext();
	}

	public long getMatchableid() {
		return cpbean.getMatchableid();
	}

	public OntProperty getOntProperty() {
		if (ontProperty == null) {
			clss.loadJenaProperties();
		}
		return ontProperty;
	}

	public String getUri() {
		return getBean().getUri();
	}

	protected void setJenaProperty(final OntProperty oprop) {
		ontProperty = oprop;
	}

	public void setMatchableid(final long id) {
		cpbean.setMatchableid(id);
	}
}
