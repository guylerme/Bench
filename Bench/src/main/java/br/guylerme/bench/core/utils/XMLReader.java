package br.guylerme.bench.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLReader {

	public List getXMLElements(String path) throws JDOMException, IOException {

		File f = new File(path);
		SAXBuilder sb = new SAXBuilder();
		Document d = sb.build(f);
		Element mural = d.getRootElement();
		List elements = mural.getChildren();

		return elements;
		/*
		 * Iterator i = elements.iterator(); while (i.hasNext()) { Element
		 * element = (Element) i.next(); List nome1 = element.getAttributes();
		 * System.out.println(nome1); }
		 */
	}
}
