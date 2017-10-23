package com.ontimize.jee.server.services.dms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.stereotype.Component;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.services.dms.dao.IDMSCategoryDao;
import com.ontimize.jee.server.services.dms.dao.IDMSDocumentFileDao;

/**
 * The Class DMSServiceCategoryHelper.
 */
@Component
@Lazy(value = true)
public class DMSServiceCategoryHelper extends AbstractDMSServiceHelper {

	private static final Logger		logger	= LoggerFactory.getLogger(DMSServiceCategoryHelper.class);

	@Autowired
	DefaultOntimizeDaoHelper		daoHelper;

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
	public DMSCategory categoryGetForDocument(Serializable idDocument, List<?> attributes) {
		CheckingTools.failIfNull(idDocument, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		List<?> attribs = attributes;
		if (attribs == null) {
			attribs = new ArrayList<>();
		}
		ListTools.safeAdd((List<String>) attribs, this.getColumnHelper().getCategoryIdColumn());
		ListTools.safeAdd((List<String>) attribs, this.getColumnHelper().getCategoryNameColumn());
		ListTools.safeAdd((List<String>) attribs, this.getColumnHelper().getCategoryParentColumn());

		Map<Object, Object> filter = EntityResultTools.keysvalues(this.getColumnHelper().getDocumentIdColumn(), idDocument);
		EntityResult er = this.daoHelper.query(this.categoryDao, filter, attribs);
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
	public void categoryUpdate(Serializable idCategory, Map<?, ?> av) {
		CheckingTools.failIfNull(idCategory, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> filter = new HashMap<>();
		filter.put(this.getColumnHelper().getCategoryIdColumn(), idCategory);
		this.daoHelper.update(this.categoryDao, av, filter);
	}

	/**
	 * Category delete.
	 *
	 * @param idCategory
	 *            the id category
	 */
	public void categoryDelete(Serializable idCategory) {
		CheckingTools.failIfNull(idCategory, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		Map<String, Object> filter = new HashMap<>();
		filter.put(this.getColumnHelper().getCategoryIdColumn(), idCategory);

		// Tenemos que poner a null el id_category_parent de las categorías que tenga a esta como padre
		Map<String, Object> avUpdate = new HashMap<>();
		avUpdate.put(this.getColumnHelper().getCategoryParentColumn(), new NullValue());
		Map<String, Object> kvUpdate = new HashMap<>();
		kvUpdate.put(this.getColumnHelper().getCategoryParentColumn(), idCategory);
		try {
			this.categoryDao.unsafeUpdate(avUpdate, kvUpdate);
		} catch (SQLWarningException ex) {
			DMSServiceCategoryHelper.logger.warn("Warning setting null parent categories", ex);
		}

		// Tenemos que quitar todos los ficheros de la categoria
		avUpdate = new HashMap<>();
		avUpdate.put(this.getColumnHelper().getCategoryIdColumn(), new NullValue());
		kvUpdate = new HashMap<>();
		kvUpdate.put(this.getColumnHelper().getCategoryIdColumn(), idCategory);
		try {
			this.fileDao.unsafeUpdate(avUpdate, kvUpdate);
		} catch (SQLWarningException ex) {
			DMSServiceCategoryHelper.logger.warn("Warning setting null category files", ex);
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
	public Serializable categoryInsert(Serializable idDocument, String name, Serializable idParentCategory, Map<?, ?> otherData) {
		CheckingTools.failIfNull(idDocument, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		CheckingTools.failIfNull(name, DMSNaming.ERROR_CATEGORY_NAME_MANDATORY);
		Map<Object, Object> av = new HashMap<>(otherData == null ? new HashMap<>() : otherData);
		av.put(this.getColumnHelper().getDocumentIdColumn(), idDocument);
		av.put(this.getColumnHelper().getCategoryNameColumn(), name);
		av.put(this.getColumnHelper().getCategoryParentColumn(), idParentCategory);
		return (Serializable) this.daoHelper.insert(this.categoryDao, av).get(this.getColumnHelper().getCategoryIdColumn());
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
	private DMSCategory convertCategoryResultSet(Serializable idDocument, EntityResult er) {
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
	private void expandCategory(DMSCategory root, EntityResult er, Serializable idDocument) {
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
	private List<DMSCategory> removeCategoriesForParentId(EntityResult er, DMSCategory parentCategory, Serializable idDocument) {
		List<Serializable> listIdParentCategory = (List<Serializable>) er.get(this.getColumnHelper().getCategoryParentColumn());
		List<DMSCategory> res = new ArrayList<>();
		if (listIdParentCategory != null) {
			for (int i = 0; i < listIdParentCategory.size(); i++) {
				if (ObjectTools.safeIsEquals(listIdParentCategory.get(i), parentCategory.getIdCategory())) {
					Map<? extends Serializable, ? extends Serializable> recordValues = er.getRecordValues(i);
					Serializable idCategory = recordValues.remove(this.getColumnHelper().getCategoryIdColumn());
					String categoryName = (String) recordValues.remove(this.getColumnHelper().getCategoryNameColumn());
					res.add(new DMSCategory(idDocument, idCategory, categoryName, recordValues, parentCategory));
					er.deleteRecord(i);
					i--;
				}
			}
		}
		return res;
	}
}
