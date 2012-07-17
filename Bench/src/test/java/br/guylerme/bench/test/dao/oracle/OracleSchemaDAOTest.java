package br.guylerme.bench.test.dao.oracle;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.oracle.OracleDAOFactory;

public class OracleSchemaDAOTest {

	@Test
	public void testCreateTemporatyOwlFile() {
		try {
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory.getDaoFactory().getSchemaDao()
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
			assertNotNull(OracleDAOFactory
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
