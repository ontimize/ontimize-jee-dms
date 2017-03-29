package com.ontimize.jee.server.services.dms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.stereotype.Component;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSCategoryDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;

/**
 * The Class DMSServiceCategoryHelper.
 */
@Component
@Lazy(value = true)
public class DMSServiceCategoryHelper {


	@Autowired
	DefaultOntimizeDaoHelper daoHelper;

	/** The category dao. */
	@Autowired
	protected IDMSCategoryDao		categoryDao;

	/** The category dao. */
	@Autowired
	protected IDMSDocumentFileDao	fileDao;

	/** The dms file helper. */
	@Autowired
	protected DMSServiceFileHelper	dmsFileHelper;

	/**
	 * Category get for document.
	 *
	 * @param idDocument
	 *            the id document
	 * @param attributes
	 *            the attributes
	 * @return the DMS category
	 */
	public DMSCategory categoryGetForDocument(Object idDocument, List<?> attributes) {
		CheckingTools.failIfNull(idDocument, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		if (attributes == null) {
			attributes = new ArrayList<Object>();
		}
		if (!attributes.contains(DMSNaming.CATEGORY_CATEGORY_NAME)) {
			((List<Object>) attributes).add(DMSNaming.CATEGORY_CATEGORY_NAME);
		}
		if (!attributes.contains(DMSNaming.CATEGORY_ID_CATEGORY)) {
			((List<Object>) attributes).add(DMSNaming.CATEGORY_ID_CATEGORY);
		}
		if (!attributes.contains(DMSNaming.CATEGORY_ID_CATEGORY_PARENT)) {
			((List<Object>) attributes).add(DMSNaming.CATEGORY_ID_CATEGORY_PARENT);
		}
		Map<String, Object> filter = new HashMap<>();
		filter.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, idDocument);
		EntityResult er = this.daoHelper.query(this.categoryDao, filter, attributes);
		return this.convertCategoryResultSet(idDocument, er);
	}

	/**
	 * Category update.
	 *
	 * @param idCategory
	 *            the id category
	 * @param av
	 *            the av
	 */
	public void categoryUpdate(Object idCategory, Map<?, ?> av) {
		CheckingTools.failIfNull(idCategory, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(DMSNaming.CATEGORY_ID_CATEGORY, idCategory);
		this.daoHelper.update(this.categoryDao, av, filter);
	}

	/**
	 * Category delete.
	 *
	 * @param idCategory
	 *            the id category
	 */
	public void categoryDelete(Object idCategory) {
		CheckingTools.failIfNull(idCategory, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(DMSNaming.CATEGORY_ID_CATEGORY, idCategory);


		//Tenemos que poner a null el id_category_parent de las categorías que tenga a esta como padre
		Map<String, Object> avUpdate = new HashMap<String, Object>();
		avUpdate.put(DMSNaming.CATEGORY_ID_CATEGORY_PARENT,new NullValue());
		Map<String, Object> kvUpdate = new HashMap<String, Object>();
		kvUpdate.put(DMSNaming.CATEGORY_ID_CATEGORY_PARENT, idCategory);
		try {
			this.categoryDao.unsafeUpdate(avUpdate, kvUpdate);
		} catch (SQLWarningException ex) {
			// do nothing
		}

		// Tenemos que quitar todos los ficheros de la categoria
		avUpdate = new HashMap<String, Object>();
		avUpdate.put(DMSNaming.CATEGORY_ID_CATEGORY, new NullValue());
		kvUpdate = new HashMap<String, Object>();
		kvUpdate.put(DMSNaming.CATEGORY_ID_CATEGORY, idCategory);
		try {
			this.fileDao.unsafeUpdate(avUpdate, kvUpdate);
		} catch (SQLWarningException ex) {
			// do nothing
		}
		this.daoHelper.delete(this.categoryDao, filter);
	}

	/**
	 * Category insert.
	 *
	 * @param idDocument
	 *            the id document
	 * @param name
	 *            the name
	 * @param idParentCategory
	 *            the id parent category
	 * @param otherData
	 *            the other data
	 * @return the object
	 */
	public Object categoryInsert(Object idDocument, String name, Object idParentCategory, Map<?, ?> otherData) {
		CheckingTools.failIfNull(idDocument, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		CheckingTools.failIfNull(name, DMSNaming.ERROR_CATEGORY_NAME_MANDATORY);
		Map<Object, Object> av = new HashMap<>(otherData == null ? new HashMap<Object, Object>() : otherData);
		av.put(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, idDocument);
		av.put(DMSNaming.CATEGORY_CATEGORY_NAME, name);
		av.put(DMSNaming.CATEGORY_ID_CATEGORY_PARENT, idParentCategory);
		return this.daoHelper.insert(this.categoryDao, av).get(DMSNaming.CATEGORY_ID_CATEGORY);
	}

	/**
	 * Convert category result set.
	 *
	 * @param idDocument
	 *            the id document
	 * @param er
	 *            the er
	 * @return the DMS category
	 */
	private DMSCategory convertCategoryResultSet(Object idDocument, EntityResult er) {
		DMSCategory root = new DMSCategory(idDocument, null, "/", null, null);
		this.expandCategory(root, er, idDocument);
		return root;
	}

	/**
	 * Expand category.
	 *
	 * @param root
	 *            the root
	 * @param er
	 *            the er
	 * @param idDocument
	 *            the id document
	 */
	private void expandCategory(DMSCategory root, EntityResult er, Object idDocument) {
		List<DMSCategory> categories = this.removeCategoriesForParentId(er, root, idDocument);
		root.setChildren(categories);
		for (DMSCategory category : root.getChildren()) {
			this.expandCategory(category, er, idDocument);
		}
	}

	/**
	 * Removes the categories for parent id.
	 *
	 * @param er
	 *            the er
	 * @param parentCategory
	 *            the parent category
	 * @param idDocument
	 *            the id document
	 * @return the list
	 */
	private List<DMSCategory> removeCategoriesForParentId(EntityResult er, DMSCategory parentCategory, Object idDocument) {
		List<Object> listIdParentCategory = (List<Object>) er.get(DMSNaming.CATEGORY_ID_CATEGORY_PARENT);
		List<DMSCategory> res = new ArrayList<DMSCategory>();
		if (listIdParentCategory != null) {
			for (int i = 0; i < listIdParentCategory.size(); i++) {
				if (ObjectTools.safeIsEquals(listIdParentCategory.get(i), parentCategory.getIdCategory())) {
					Map<?, ?> recordValues = er.getRecordValues(i);
					Object idCategory = recordValues.remove(DMSNaming.CATEGORY_ID_CATEGORY);
					String categoryName = (String) recordValues.remove(DMSNaming.CATEGORY_CATEGORY_NAME);
					res.add(new DMSCategory(idDocument, idCategory, categoryName, recordValues, parentCategory));
					er.deleteRecord(i);
					i--;
				}
			}
		}
		return res;
	}
}
