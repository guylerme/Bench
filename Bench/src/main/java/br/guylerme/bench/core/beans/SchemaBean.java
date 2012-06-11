/***********************************************************************
 * Module: SchemaBean.java Author: Owner Purpose: Defines the Class SchemaBean
 ***********************************************************************/
package br.guylerme.bench.core.beans;

import java.util.Date;

/** @pdOid bfdba69a-06bb-404c-a332-9877d438d8c7 */
public class SchemaBean
{

	private Date	creationDate;

	/** @pdOid 076cea95-e7b9-43fd-83c5-2286ee456b9e */
	private String	name;

	/** @pdOid a4923e54-1aad-42c5-844b-2a7b1369c0dc */
	private int		schemaid;

	public Date getCreationDate()
	{
		return creationDate;
	}

	public String getName()
	{
		return name;
	}

	public int getSchemaid()
	{
		return schemaid;
	}

	public void setCreationDate(final Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public void setSchemaid(final int schemaid)
	{
		this.schemaid = schemaid;
	}

}
