package br.guylerme.bench.core.beans;

public abstract class ElementBean
{
	private String	comment;
	private String	label;
	private int		schemaid;
	private String	uri;

	public String getComment()
	{
		return comment;
	}

	public String getLabel()
	{
		return label;
	}

	public int getSchemaid()
	{
		return schemaid;
	}

	public String getUri()
	{
		return uri;
	}

	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	public void setLabel(final String label)
	{
		this.label = label;
	}

	public void setSchemaid(final int schemaid)
	{
		this.schemaid = schemaid;
	}

	public void setUri(final String uri)
	{
		this.uri = uri;
	}

	/*
	 * @Override public boolean equals(Object o) { if( o instanceof ElementBean
	 * ) { // compare the two keys ElementBean bean = (ElementBean) o; if(
	 * (this.schemaid == bean.getSchemaid()) && (this.uri == bean.getUri()))
	 * return true; } return false; }
	 */
}
