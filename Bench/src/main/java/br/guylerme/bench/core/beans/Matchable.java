package br.guylerme.bench.core.beans;

/**
 * Interface that identifies a matchable element
 * 
 * @author Guylerme Figueiredo
 */
public interface Matchable
{
	/**
	 * Get the matchable element context
	 * 
	 * @return
	 */
	public abstract String getContext();

	/**
	 * Get the id of the matchable element
	 * 
	 * @return
	 */
	public abstract long getMatchableid();

	/**
	 * Get the matchable element URI
	 * 
	 * @return
	 */
	public abstract String getUri();

	/**
	 * Define the id of the matchable element
	 * 
	 * @param id
	 */
	public abstract void setMatchableid(long id);
}
