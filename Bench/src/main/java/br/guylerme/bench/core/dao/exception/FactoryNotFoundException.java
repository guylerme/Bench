/***********************************************************************
 * Module: FactoryNotFoundException.java Author: Owner Purpose: Defines the
 * Class FactoryNotFoundException
 ***********************************************************************/

package br.guylerme.bench.core.dao.exception;

/** @pdOid c122760b-26e6-46bc-a41d-e84a0f9fb8b2 */
public class FactoryNotFoundException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5262090592522006277L;

	public FactoryNotFoundException(final String msg, final Throwable exception)
	{
		super(msg, exception);
	}
}
