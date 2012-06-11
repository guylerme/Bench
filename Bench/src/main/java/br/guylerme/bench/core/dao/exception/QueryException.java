/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package br.guylerme.bench.core.dao.exception;

/**
 * Reports an error with a query on the datasource (may be SQLException or other
 * its not a database)
 * 
 * @author Guylerme Figueiredo
 */
public class QueryException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9137849377140628833L;

	public QueryException(final String msg) {
		super(msg);
	}

	public QueryException(final String msg, final Throwable th) {
		super(msg, th);
	}

	public QueryException(final Throwable th) {
		super(th);
	}
}
