package br.guylerme.bench.core.beans;

public class InstanceBean extends ElementBean implements Matchable
{

	private String	classURI;
	private int		dataSetSeq;
	private long	matchableid;

	public String getClassURI()
	{
		return classURI;
	}

	public String getContext()
	{
		return getClassURI();
	}

	public int getDataSetSeq()
	{
		return dataSetSeq;
	}

	public long getMatchableid()
	{
		return matchableid;
	}

	public void setClassURI(final String classURI)
	{
		this.classURI = classURI;
	}

	public void setDataSetSeq(final int dataSetSeq)
	{
		this.dataSetSeq = dataSetSeq;
	}

	public void setMatchableid(final long matchableid)
	{
		this.matchableid = matchableid;
	}
}
