/***********************************************************************
 * Module: SchemaBean.java Author: Owner Purpose: Defines the Class SchemaBean
 ***********************************************************************/
package br.guylerme.bench.core.beans;

/** @pdOid bfdba69a-06bb-404c-a332-9877d438d8c7 */
public class TransformationBean {

	private int transformationId;

	private String source;

	/** @pdOid 076cea95-e7b9-43fd-83c5-2286ee456b9e */
	private String target;

	/** @pdOid a4923e54-1aad-42c5-844b-2a7b1369c0dc */
	private int rule;

	public static final int GENERALIZATION = 0;
	public static final int SYNONYM = 1;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getRule() {
		return rule;
	}

	public void setRule(int rule) {
		this.rule = rule;
	}

	public String getRuleDescription() {

		switch (this.rule) {
		case 0:
			return "GENERALIZATION";
		case 1:
			return "SYONYM";
		default:
			return "";
		}

	}

	public int getTransformationId() {
		return transformationId;
	}

	public void setTransformationId(int transformationId) {
		this.transformationId = transformationId;
	}

}
