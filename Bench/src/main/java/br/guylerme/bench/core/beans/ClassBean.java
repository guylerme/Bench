package br.guylerme.bench.core.beans;

/**
 * @author Guylerme Figueiredo
 */
public class ClassBean extends ElementBean implements Matchable
{
	private long	matchableid;

	public String getContext()
	{
		return "T";
	}

	public long getMatchableid()
	{
		return matchableid;
	}

	public void setMatchableid(final long matchableid)
	{
		this.matchableid = matchableid;
	}

}
