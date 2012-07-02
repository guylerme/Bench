package br.guylerme.bench.core.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jrdf.graph.AnyTriple;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Triple;
import org.jrdf.parser.RdfReader;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;

public class RDFUtil {

	public List<Triple> getTriples(String filePath) {

		List<Triple> tripleList = new ArrayList<Triple>();

		RdfReader reader = new RdfReader();
		Graph graph = reader.parseRdfXml(new File(filePath));

		ClosableIterable<Triple> x = graph.find(AnyTriple.ANY_TRIPLE);
		ClosableIterator<Triple> triples = x.iterator();
		try {
			while (triples.hasNext()) {
				Triple triple = triples.next();
				tripleList.add(triple);
			}

		} finally {
			triples.close();
		}

		return tripleList;

	}
}
