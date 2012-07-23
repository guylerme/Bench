package br.guylerme.bench.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.guylerme.bench.core.beans.TransformationBean;
import br.guylerme.bench.core.commands.transformation.NewTransformation;

public class TestTransformation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NewTransformation nt = new NewTransformation();

		List<TransformationBean> transformations = new ArrayList<TransformationBean>();

		TransformationBean t1 = new TransformationBean();
		t1.setSource("Books");
		t1.setTarget("Product");
		t1.setRule(TransformationBean.GENERALIZATION);

		transformations.add(t1);

		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("transformations", transformations);

		nt.execute(parameters);
	}

	public void testOrderingTransformation() {
		NewTransformation nt = new NewTransformation();

		List<TransformationBean> transformations = new ArrayList<TransformationBean>();

		TransformationBean t1 = new TransformationBean();
		t1.setSource("C1");
		t1.setTarget("C2");

		transformations.add(t1);

		TransformationBean t2 = new TransformationBean();
		t2.setSource("C2");
		t2.setTarget("C3");

		transformations.add(t2);

		TransformationBean t3 = new TransformationBean();
		t3.setSource("C4");
		t3.setTarget("C5");

		transformations.add(t3);

		TransformationBean t4 = new TransformationBean();
		t4.setSource("C3");
		t4.setTarget("C6");

		transformations.add(t4);

		TransformationBean t5 = new TransformationBean();
		t5.setSource("C7");
		t5.setTarget("C1");

		transformations.add(t5);

		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("transformations", transformations);

		nt.execute(parameters);

	}
}
