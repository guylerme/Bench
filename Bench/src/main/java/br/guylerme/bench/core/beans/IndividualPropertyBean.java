package br.guylerme.bench.core.beans;

/**
 * @author Guylerme Figueiredo
 */
public class IndividualPropertyBean extends ElementBean implements Matchable {
	private String value;
	private String uri;
	private String classValue;
	private String classUri;
	private long matchableId;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getClassValue() {
		return classValue;
	}

	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	public String getClassUri() {
		return classUri;
	}

	public void setClassUri(String classUri) {
		this.classUri = classUri;
	}

	@Override
	public String getContext() {

		return "T";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public long getMatchableid() {

		return matchableId;
	}

	@Override
	public void setMatchableid(long matchableId) {
		this.matchableId = matchableId;

	}

}
