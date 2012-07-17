package br.guylerme.bench.test.dao.oracle;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.mysql.MySQLDAOFactory;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ComboPooledDataSource pool = new ComboPooledDataSource();
		
		DAOFactory dao = null;
		try {
			dao = DAOFactory.getDaoFactory();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*try {
			((MySQLDAOFactory) dao).getConnection();
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
}
