package com.ontimize.jee.server.services.dms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;

/**
 * This class will manage all behaviour around DMS columns naming. <br>From client side will be used allways configured values in DMSNaming. <br>To dao side will by applied this
 * columns transformation. <br>By default same values. <br><br>Moreove this allows to have column extensions, indicating extends columns for each dao (document, file, version,
 * ....)
 */
public class DMSColumnHelper {

	private Map<String, String> columnsMapping;

	public DMSColumnHelper() {
		this.initColumnsMapping();
	}

	protected void initColumnsMapping() {
		if (this.columnsMapping == null) {
			this.columnsMapping = new HashMap<>();
		}

		this.addColumnMapping(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		this.addColumnMapping(DMSNaming.DOCUMENT_UPDATE_DATE, DMSNaming.DOCUMENT_UPDATE_DATE);
		this.addColumnMapping(DMSNaming.DOCUMENT_UPDATE_BY, DMSNaming.DOCUMENT_UPDATE_BY);
		this.addColumnMapping(DMSNaming.DOCUMENT_DOCUMENT_NAME, DMSNaming.DOCUMENT_DOCUMENT_NAME);
		this.addColumnMapping(DMSNaming.DOCUMENT_OWNER_ID, DMSNaming.DOCUMENT_OWNER_ID);
		this.addColumnMapping(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION, DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION);
		this.addColumnMapping(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS, DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS);

		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_NAME, DMSNaming.DOCUMENT_FILE_NAME);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_TYPE, DMSNaming.DOCUMENT_FILE_TYPE);

		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH, DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_VERSION, DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL, DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE, DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE);
		this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER, DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER);

		this.addColumnMapping(DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT, DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT);
		this.addColumnMapping(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER, DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER);
		this.addColumnMapping(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD, DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD);

		this.addColumnMapping(DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY, DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY);
		this.addColumnMapping(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY);
		this.addColumnMapping(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE, DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE);

		this.addColumnMapping(DMSNaming.CATEGORY_ID_CATEGORY, DMSNaming.CATEGORY_ID_CATEGORY);
		this.addColumnMapping(DMSNaming.CATEGORY_CATEGORY_NAME, DMSNaming.CATEGORY_CATEGORY_NAME);
		this.addColumnMapping(DMSNaming.CATEGORY_ID_CATEGORY_PARENT, DMSNaming.CATEGORY_ID_CATEGORY_PARENT);
	}

	public Map<String, String> getColumnsMapping() {
		return this.columnsMapping;
	}

	public void setColumnsMapping(Map<String, String> columnsMapping) {
		this.columnsMapping = columnsMapping;
	}

	public String getColumnEquivalence(String column) {
		return this.columnsMapping.containsKey(column) ? this.columnsMapping.get(column) : column;
	}

	public void addColumnMapping(String column, String daoColumn) {
		this.addColumnMapping(column, daoColumn, false);
	}

	public void addColumnMapping(String column, String daoColumn, boolean ignoreIfExists) {
		MapTools.safePut(this.columnsMapping, column, daoColumn, ignoreIfExists);
	}

	////// DOCUMENT methods ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getDocumentColumns() {
		return EntityResultTools.attributes(//
				DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, //
				DMSNaming.DOCUMENT_UPDATE_DATE, //
				DMSNaming.DOCUMENT_UPDATE_BY, //
				DMSNaming.DOCUMENT_DOCUMENT_NAME, //
				DMSNaming.DOCUMENT_OWNER_ID, //
				DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION, //
				DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS//
				);
	}

	public String getDocumentIdColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
	}

	public String getDocumentNameColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_NAME);
	}

	public String getDocumentOwnerColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_OWNER_ID);
	}

	public String getDocumentUpdateDateColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_UPDATE_DATE);
	}

	public String getDocumentUpdateByColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_UPDATE_BY);
	}

	public String getDocumentDescriptionColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION);
	}

	public String getDocumentKeywordsColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS);
	}

	////// FILE methods ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getFileColumns() {
		return EntityResultTools.attributes(//
				DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, //
				DMSNaming.DOCUMENT_FILE_NAME, //
				DMSNaming.DOCUMENT_FILE_TYPE, //
				// Foreign columns
				DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, //
				DMSNaming.CATEGORY_ID_CATEGORY//
				);
	}

	public String getFileIdColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
	}

	public String getFileNameColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_NAME);
	}

	////// VERSION methods ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getVersionColumns() {
		return EntityResultTools.attributes(//
				DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION, //
				DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH, //
				DMSNaming.DOCUMENT_FILE_VERSION_VERSION, //
				DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION, //
				DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, //
				DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, //
				DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL, //
				DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE, //
				DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER, //
				// Foreign columns
				DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE//
				);
	}

	public String getVersionIdColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
	}

	public String getVersionVersionColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
	}

	public String getVersionActiveColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE);
	}

	public String getVersionSizeColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE);
	}

	public String getVersionDescriptionColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION);
	}

	public String getVersionPathColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH);
	}

	public String getVersionThumbnailColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL);
	}

	public String getVersionAddedDateColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE);
	}

	public String getVersionAddedUserColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER);
	}

	////// CATEGORY methods ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getCategoryColumns() {
		return EntityResultTools.attributes(//
				DMSNaming.CATEGORY_ID_CATEGORY, //
				DMSNaming.CATEGORY_CATEGORY_NAME, //
				DMSNaming.CATEGORY_ID_CATEGORY_PARENT, //
				// Foreign columns
				DMSNaming.DOCUMENT_ID_DMS_DOCUMENT//
				);
	}

	public String getCategoryIdColumn() {
		return this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY);
	}

	public String getCategoryNameColumn() {
		return this.getColumnEquivalence(DMSNaming.CATEGORY_CATEGORY_NAME);
	}

	public String getCategoryParentColumn() {
		return this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY_PARENT);
	}

	////// PROPERTY methods ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getPropertyColumns() {
		return EntityResultTools.attributes(//
				DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY, //
				DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY, //
				DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE, //
				// Foreign columns
				DMSNaming.DOCUMENT_ID_DMS_DOCUMENT//
				);
	}

	public String getPropertyIdColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY);
	}

	public String getPropertyKeyColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY);
	}

	public String getPropertyValueColumn() {
		return this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE);
	}

	////// DOCUMENT RELATIONS methods ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getDocumentRelatedColumns() {
		return EntityResultTools.attributes(//
				DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT, //
				DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER, //
				DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD);
	}

	public String getDocumentRelatedIdColumn() {
		return this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT);
	}

	public String getDocumentRelatedMasterColumn() {
		return this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER);
	}

	public String getDocumentRelatedChildColumn() {
		return this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD);
	}
}