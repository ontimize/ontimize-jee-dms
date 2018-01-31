package com.ontimize.jee.server.dms.rest;

import java.util.List;
import java.util.Map;

import com.ontimize.jee.server.dms.model.OFile;

public interface IDMSNameConverter {

	public Object getFileIdColumn();

	public Object getFileNameColumn();

	public Object getFileSizeColumn();

	public Object getCategoryIdColumn();

	public OFile createOFile(Map<?, ?> params);

	public List<?> getFileColumns(List<?> columns);

	public List<?> getCategoryColumns(List<?> columns);

}
