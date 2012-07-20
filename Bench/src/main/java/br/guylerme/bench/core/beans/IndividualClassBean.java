package br.guylerme.bench.core.beans;

/**
 * @author Guylerme Figueiredo
 */
public class IndividualClassBean extends ElementBean implements Matchable {
	private String value;
	private long matchableId;

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
