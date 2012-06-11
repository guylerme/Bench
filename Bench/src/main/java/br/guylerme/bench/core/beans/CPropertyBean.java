package br.guylerme.bench.core.beans;

public class CPropertyBean extends PropertyBean implements Matchable
{

	private String	classURI;
	private long	matchableid;

	public String getClassURI()
	{
		return classURI;
	}

	public String getContext()
	{
		return getClassURI();
	}

	public long getMatchableid()
	{
		return matchableid;
	}

	public void setClassURI(final String classURI)
	{
		this.classURI = classURI;
	}

	public void setMatchableid(final long matchableid)
	{
		this.matchableid = matchableid;
	}
}
