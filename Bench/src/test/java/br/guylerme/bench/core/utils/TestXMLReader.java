package br.guylerme.bench.core.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import org.jdom.JDOMException;
import org.jrdf.graph.AnyTriple;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.parser.RdfReader;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import br.guylerme.bench.core.beans.CPropertyBean;
import br.guylerme.bench.core.beans.ClassBean;
import br.guylerme.bench.core.beans.DataSetBean;
import br.guylerme.bench.core.beans.PropertyBean;
import br.guylerme.bench.core.beans.SchemaBean;
import br.guylerme.bench.core.dao.DAOFactory;
import br.guylerme.bench.core.dao.SchemaDAO;
import br.guylerme.bench.core.dao.exception.DataSourceConnectionException;
import br.guylerme.bench.core.dao.exception.FactoryNotFoundException;
import br.guylerme.bench.core.dao.exception.QueryException;
import br.guylerme.bench.core.dao.exception.SchemaNameAlreadyExistsException;

public class TestXMLReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * XMLReader xml = new XMLReader(); List elements = null;
		 * 
		 * try { elements = xml.getXMLElements("c:/dataset-1610.rdf"); } catch
		 * (JDOMException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 * 
		 * Iterator i = elements.iterator(); while (i.hasNext()) { Element
		 * element = (Element) i.next(); List nome1 = element.getAttributes();
		 * System.out.println(nome1); } System.out.println("FIM");
		 */
		String schemaName = "Rank Gender Race";
		List<String> validNamespaces = new ArrayList<String>();
		validNamespaces.add("Rank Gender Race");

		ComboPooledDataSource pool = new ComboPooledDataSource();

		DAOFactory dao = null;
		try {
			dao = DAOFactory.getDaoFactory();
		} catch (FactoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SchemaBean schema = null;
		try {
			schema = dao.getSchemaDao().newSchema(schemaName,
					validNamespaces);
		} catch (SchemaNameAlreadyExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (QueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DataSourceConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		PropertyBean prop = null;
		ClassBean cls = null;
		CPropertyBean cprop = null;

		RdfReader reader = new RdfReader();
		Graph graph = reader.parseRdfXml(new File("c:/dataset-1610.rdf"));

		ClosableIterable<Triple> x = graph.find(AnyTriple.ANY_TRIPLE);
		ClosableIterator<Triple> triples = x.iterator();
		try {
			while (triples.hasNext()) {
				Triple triple = triples.next();

				if (triple.getObject().toString().startsWith("\"")
						|| triple.getObject().toString().endsWith("\"")) {
					prop = new PropertyBean();
					prop.setSchemaid(schema.getSchemaid());
					prop.setValue(triple.getObject().toString());
					prop.setUri(triple.getSubject().toString());
					prop.setLabel(triple.getPredicate().toString());

					dao.getSchemaDao().newProperty(prop.getSchemaid(),
							prop.getUri(), prop.getLabel(), prop.getComment(),
							prop.getDatatype(), prop.getValue());
				} else {
					cls = new ClassBean();
					cls.setSchemaid(schema.getSchemaid());
					cls.setUri(triple.getSubject().toString());
					cls.setLabel(triple.getPredicate().toString());

					dao.getSchemaDao().newClass(cls.getSchemaid(),
							cls.getUri(), cls.getLabel(), cls.getComment());
				}

				System.out.println("Got triple: " + triple);

				System.out.println("Got Object: " + triple.getObject());
				System.out.println("Got Predicate: " + triple.getPredicate());
				System.out.println("Got Subject: " + triple.getSubject());

				BufferedWriter out = new BufferedWriter(new FileWriter(
						"c:/outfilename"));
				out.write(triple.toString());
				out.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			triples.close();
		}

		try {
			File f = new File("c:/dataset-1610.rdf");
			InputStream file = new FileInputStream(f);
			dao.getSchemaDao()
					.newDataSet(1, "c:/dataset-1610.rdf",
							"http://www.data.gov", file, f.length(),
							"dataset-1610.rdf");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("FIM");
	}
}
