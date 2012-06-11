/***********************************************************************
 * Module: DataSetBean.java Author: Owner Purpose: Defines the Class DataSetBean
 ***********************************************************************/
package br.guylerme.bench.core.beans;

import java.util.Date;

/** @pdOid 09fdc5ed-962f-4cd7-b148-bd4f66c40df7 */
public class DataSetBean
{

	/** @pdOid 584431eb-8498-4fde-a520-fa09f2990d66 */
	private int		datasetseq;
	/** @pdOid 3cbc0c43-b394-4da4-8731-cad72610ebd8 */
	private Date	date;
	/** @pdOid 76fe5178-81f8-4521-876a-e1355dac0a15 */
	private String	filename;

	private String	name;

	/** @pdOid 22575a92-65f4-4c4e-bfe3-c38eedb7ea74 */
	private String	path;

	/** @pdOid 4e3c58af-1aa3-47ce-9940-4560b0472bf2 */
	private int		schemaid;

	public int getDatasetseq()
	{
		return datasetseq;
	}

	public Date getDate()
	{
		return date;
	}

	public String getFilename()
	{
		return filename;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	public String getPath()
	{
		return path;
	}

	public int getSchemaid()
	{
		return schemaid;
	}

	public void setDatasetseq(final int datasetseq)
	{
		this.datasetseq = datasetseq;
	}

	public void setDate(final Date date)
	{
		this.date = date;
	}

	public void setFilename(final String filename)
	{
		this.filename = filename;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	public void setPath(final String path)
	{
		this.path = path;
	}

	public void setSchemaid(final int schemaid)
	{
		this.schemaid = schemaid;
	}
}
