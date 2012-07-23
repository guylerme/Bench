package br.guylerme.bench.core.schema;

import org.apache.log4j.Logger;

import br.guylerme.bench.core.beans.TransformationBean;
import br.guylerme.bench.core.dao.DAOFactory;

public class Transformation {

	private TransformationBean tbean;
	private DAOFactory factory;
	private Logger log;

	public Transformation(final TransformationBean tbean,
			final DAOFactory factory) {
		this.tbean = tbean;
		this.factory = factory;
		log = Logger.getLogger(this.getClass());
	}

	public String getSource() {
		return tbean.getSource();
	}

	public String getTarget() {
		return tbean.getTarget();
	}

	public int getRule() {
		return tbean.getRule();
	}

}
