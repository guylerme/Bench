/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package br.guylerme.bench.core.dao.exception;

/**
 * If an error with a connection between the datasource and the DAO Factory
 * happens, this exception is thrown
 * 
 * @author Guylerme Figueiredo
 */
public class DataSourceConnectionException extends Exception
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2198192845060252634L;

	public DataSourceConnectionException(final String msg)
	{
		super(msg);
	}

	public DataSourceConnectionException(final String msg, final Throwable th)
	{
		super(msg, th);
	}

	public DataSourceConnectionException(final Throwable th)
	{
		super(th);
	}
}
