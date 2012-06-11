package br.guylerme.bench.test.dao.mysql;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.mysql.MySQLDAOFactory;

public class MySQLSchemaDAOTest {

	@Test
	public void testCreateTemporatyOwlFile() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.createTemporatyOwlFile(1, 1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetAllDataSets() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getAllDataSets(1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetAllSchemas() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getAllSchemas());
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetClassInstances() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getClassInstances(1, 1, "Teste"));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetClassProperties() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getClassProperties(1, "Teste"));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDataSet() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getDataSet(1, 1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetElement() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getElement("Teste", 1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetMatchable() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getMatchable(1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetSchemaInt() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getSchema(1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetSchemaString() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getSchema("Teste Schema"));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetSchemaClasses() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getSchemaClasses(1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetValidNamespaces() {
		try {
			assertNotNull(MySQLDAOFactory.getDaoFactory().getSchemaDao()
					.getValidNamespaces(1));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNewClass() {
		try {
			assertNotNull(MySQLDAOFactory
					.getDaoFactory()
					.getSchemaDao()
					.newClass(5, "Teste 5 uri", "Teste 5 label",
							"Teste 5 Comment"));
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNewCProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewDataSet() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testNewSchema() {
		fail("Not yet implemented");
	}

	@Test
	public void testMySQLSchemaDAO() {
		fail("Not yet implemented");
	}

}
