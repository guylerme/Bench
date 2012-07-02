package br.guylerme.bench.core.beans;

public class PropertyBean extends ElementBean {
	private String datatype;

	private String value;

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(final String datatype) {
		this.datatype = datatype;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {

		this.value = value.split("\"")[1];
		try {
			Long.parseLong(this.value);
			this.datatype = "Number";
		} catch (NumberFormatException ex) {
			this.datatype = "String";
		}
	}
}
