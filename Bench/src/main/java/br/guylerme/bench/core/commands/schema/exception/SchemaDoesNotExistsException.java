/***********************************************************************
 * Module: SchemaDoesNotExistsException.java Author: Owner Purpose: Defines the
 * Class SchemaDoesNotExistsException
 ***********************************************************************/
package br.guylerme.bench.core.commands.schema.exception;

/** @pdOid b2ed7442-6efc-4caa-9f14-39f06ed294c2 */
public class SchemaDoesNotExistsException extends Exception
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1546233704224342447L;

	public SchemaDoesNotExistsException(final String string)
	{
		super(string);
	}
}
