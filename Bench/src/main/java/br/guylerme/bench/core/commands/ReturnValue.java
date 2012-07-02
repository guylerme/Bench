package br.guylerme.bench.core.commands;

import gnu.trove.THashMap;

import java.util.Map;

/**
 * This class encapsulates all values returned by a command
 * 
 * @author Raphael do Vale (rgomes@inf.puc-rio.br)
 */
public class ReturnValue
{

	private boolean				error		= false;
	private Throwable			exception	= null;
	private String				msgError	= null;
	private Map<String, Object>	values		= null;

	/**
	 * This constructor should be used only if there aren´t errors and no
	 * parameters (or you wanna use addParameter method)
	 */
	public ReturnValue()
	{
		error = false;
		msgError = null;
		exception = null;
		values = new THashMap<String, Object>();
	}

	/**
	 * @param error
	 *            If the command returned error or not
	 * @param msgError
	 *            the message if the command return error
	 * @param exception
	 *            the exception if an exception happens
	 * @param values
	 *            the return values in form of a map
	 */
	public ReturnValue(
			final boolean error,
			final String msgError,
			final Throwable exception,
			Map<String, Object> values)
	{
		this.error = error;
		this.msgError = msgError;
		this.exception = exception;
		if (values == null)
		{
			values = new THashMap<String, Object>();
		}
		this.values = values;
	}

	/**
	 * This constructor should be used only if there aren´t errors.
	 * 
	 * @param values
	 */
	public ReturnValue(Map<String, Object> values)
	{
		error = false;
		msgError = null;
		exception = null;
		if (values == null)
		{
			values = new THashMap<String, Object>();
		}
		this.values = values;
	}

	public void addValue(final String name, final Object parameter)
	{
		values.put(name, parameter);
	}

	public Throwable getException()
	{
		return exception;
	}

	public String getMsgError()
	{
		return msgError;
	}

	public Object getValue(final String name)
	{
		return values.get(name);
	}

	public Map<String, Object> getValues()
	{
		return values;
	}

	public boolean isError()
	{
		return error;
	}
}
