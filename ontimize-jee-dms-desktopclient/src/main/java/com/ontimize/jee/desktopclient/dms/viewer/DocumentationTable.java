package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.gui.table.TableSorter;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.upload.OpenUploadableChooserActionListener;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.utilmize.client.gui.field.table.UTable;
import com.utilmize.client.gui.field.table.render.UXmlByteSizeCellRenderer;

/**
 * The Class DocumentationTable.
 */
public class DocumentationTable extends UTable implements InteractionManagerModeListener {
	private static final Logger		logger						= LoggerFactory.getLogger(DocumentationTable.class);
	protected static final String	AVOID_PARENT_KEYS_NULL		= "avoidparentkeysnull";

	private final DocumentationTree	categoryTree				= new DocumentationTree();
	private Serializable			currentIdDocument			= null;
	private Map<Object, Object>		currentFilter				= null;
	// ñapa
	private boolean					deleting					= false;
	private boolean					ignoreEvents				= false;
	private boolean					ignoreCheckRefreshThread	= false;
	private boolean					categoryPanel				= true;
	protected String				form_id_dms_doc_field;

	public DocumentationTable(Hashtable<String, Object> params) throws Exception {
		super(params);
		this.getJTable().setFillsViewportHeight(true);
		this.setRendererForColumn(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, new UXmlByteSizeCellRenderer());
		this.categoryPanel = ParseUtilsExtended.getBoolean((String) params.get("categorypanel"), true);

		this.categoryTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				if (!DocumentationTable.this.isIgnoreEvents()) {
					DocumentationTable.this.refreshIfNeededInThread();
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(this.categoryTree);
		this.mainPanel.add(scrollPane, BorderLayout.WEST);
		this.getJTable().setDragEnabled(true);
		this.getJTable().setDropMode(DropMode.INSERT_ROWS);
		this.getJTable().setTransferHandler(new DocumentationTableTransferHandler(this));
		scrollPane.setVisible(this.categoryPanel);
	}

	@Override
	protected void installDetailFormListener() {
		super.installDetailFormListener();
		try {
			this.buttonPlus2.removeActionListener(this.addRecordListener);
			this.buttonPlus.removeActionListener(this.addRecordListener);
			this.addRecordListener = new OpenUploadableChooserActionListener(this, EntityResultTools.keysvalues("documentationtable", "documentationtable"));
			this.buttonPlus.addActionListener(this.addRecordListener);
			this.buttonPlus2.addActionListener(this.addRecordListener);
		} catch (Exception e) {
			DocumentationTable.logger.error(null, e);
		}
	}

	@Override
	public void init(Hashtable params) throws Exception {
		if (!params.containsKey("detailformopener")) {
			params.put("detailformopener", DocumentationTableDetailFormOpener.class.getName());
			params.put("form", "dummy");
		}
		this.form_id_dms_doc_field = ParseUtilsExtended.getString((String) params.get("form_id_dms_doc_field"), DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		if (!params.containsKey("parentkeys")) {
			params.put("parentkeys", this.form_id_dms_doc_field + ":" + DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		}
		super.init(params);
		if (!this.parentkeys.contains(this.form_id_dms_doc_field)) {
			this.hParentkeyEquivalences.put(this.form_id_dms_doc_field, DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
			this.parentkeys.add(this.form_id_dms_doc_field);
		}
		if (this.keyFields == null) {
			this.keyFields = new Vector<>(1);
		}
		this.keyFields.clear();
		this.keyFields.add(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		this.keyFields.add(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
	}

	@Override
	public void refreshInThread(final int delay) {
		if (this.currentFilter != null) {
			DocumentationTable.this.currentFilter.clear();
		}
		this.currentIdDocument = null;
		this.refreshIfNeededInThread();
	}

	@Override
	public void openInNewWindow(int[] modelSelectedRows) {
		// do nothing
	}

	@Override
	public void refreshInEDT(boolean autoSizeColumns) {
		try {
			this.checkRefreshThread();
			this.requeryDocuments();
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.OK));
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, DocumentationTable.logger);
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.ERROR));
		}
	}

	/**
	 * Refreshes the rows passed as parameter
	 *
	 * @param viewRowIndexes
	 *            the row indexes
	 */
	@Override
	public void refreshRows(int[] viewRowIndexes) {
		try {
			this.checkRefreshThread();
			Arrays.sort(viewRowIndexes);
			Vector<Object> vRowsValues = new Vector<Object>();
			for (int k = 0; k < viewRowIndexes.length; k++) {
				int viewRow = viewRowIndexes[k];
				Hashtable<Object, Object> kv = this.getParentKeyValues();
				// Put the row keys
				Vector<?> vKeys = this.getKeys();
				for (int i = 0; i < vKeys.size(); i++) {
					Object oKey = vKeys.get(i);
					kv.put(oKey, this.getRowKey(viewRow, oKey.toString()));
				}
				EntityResult res = this.doQueryDocuments(kv, this.attributes);
				Hashtable<?, ?> hRowData = res.getRecordValues(0);
				vRowsValues.add(vRowsValues.size(), hRowData);
			}
			// Update rows data
			this.deleteRows(viewRowIndexes);

			this.addRows(viewRowIndexes, vRowsValues);
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, DocumentationTable.logger);
		}
	}

	/**
	 * Refreshes the row passed as parameter.
	 *
	 * @param viewRowIndex
	 *            the index to refresh
	 * @param oldkv
	 */
	@Override
	public void refreshRow(int viewRowIndex, Hashtable oldkv) {
		try {
			this.checkRefreshThread();
			// Entity ent = this.locator.getEntityReference(this.getEntityName());
			Hashtable<Object, Object> kv = this.getParentKeyValues();
			// Put the row keys
			Vector<?> vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				Object oKey = vKeys.get(i);
				if ((oldkv != null) && oldkv.containsKey(oKey)) {
					kv.put(oKey, oldkv.get(oKey));
				} else {
					kv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
			}
			long t = System.currentTimeMillis();
			EntityResult res = this.doQueryDocuments(kv, this.attributes);
			if (res.isEmpty()) {
				this.deleteRow(viewRowIndex);
			} else {
				long t2 = System.currentTimeMillis();
				// Update row data
				Hashtable<?, ?> hRowData = res.getRecordValues(0);
				Hashtable<Object, Object> newkv = new Hashtable<>();
				for (int i = 0; i < vKeys.size(); i++) {
					Object oKey = vKeys.get(i);
					newkv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
				this.updateRowData(hRowData, newkv);

				long t3 = System.currentTimeMillis();
				DocumentationTable.logger.trace("Table: Query time: {}  ,  deleteRow-addRow time: {}", (t2 - t), (t3 - t2));
			}
		} catch (Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, DocumentationTable.logger);
		}
	}

	protected EntityResult doQueryDocuments(Map<?, ?> filter, List<?> attrs) {
		if (!filter.containsKey(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT)) {
			return new EntityResult();
		}
		return BeansFactory.getBean(IDMSService.class).fileQuery(filter, attrs);
	}

	protected void requeryDocuments() {
		Serializable idDms = (Serializable) this.parentForm.getDataFieldValue(this.form_id_dms_doc_field);
		if (idDms == null) {
			this.deleteData();
			this.currentIdDocument = null;
			return;
		}
		// Consider to refresh tree ----------------------------------------
		if (!ObjectTools.safeIsEquals(idDms, this.currentIdDocument)) {
			this.currentIdDocument = idDms;
			boolean oldIgnoreEvents = this.isIgnoreEvents();
			try {
				this.setIgnoreEvents(true);
				this.categoryTree.refreshModel(this.currentIdDocument);
			} finally {
				this.setIgnoreEvents(oldIgnoreEvents);
			}
		}

		// Refresh table --------------------------------------------------
		Hashtable<?, ?> kv = this.getParentKeyValues();
		if (ObjectTools.safeIsEquals(this.currentFilter, kv)) {
			return;
		}
		this.currentFilter = (Map<Object, Object>) kv;
		final EntityResult er = BeansFactory.getBean(IDMSService.class).fileQuery(kv, this.getAttributeList());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DocumentationTable.this.setValue(er, false);
			}
		});
	}

	@Override
	public Hashtable getParentKeyValues() {
		Hashtable<Object, Object> kv = super.getParentKeyValues();
		Object idCategory = this.getCurrentIdCategoryToFilter();
		if (idCategory != null) {
			kv.put(DMSNaming.CATEGORY_ID_CATEGORY, idCategory);
		}
		return kv;
	}

	protected Serializable getCurrentIdCategoryToFilter() {
		TreePath selectionPath = this.categoryTree.getSelectionPath();
		if (selectionPath == null) {
			return null;
		}
		Object ob = selectionPath.getLastPathComponent();
		if (ob instanceof DMSCategory) {
			DMSCategory category = (DMSCategory) ob;
			if (category.getIdCategory() == null) {
				// categoría raíz
				return new SearchValue(SearchValue.NULL, null);
			} else {
				return category.getIdCategory();
			}
		} else {
			return null;
		}
	}

	@Override
	public void setParentForm(Form form) {
		FormComponent idDmsField = form.getElementReference(this.form_id_dms_doc_field);
		if (idDmsField == null) {
			throw new RuntimeException("Field ID_DMS_DOC is mandatory in form");
		}
		((DataField) idDmsField).addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getNewValue() == null) {
					DocumentationTable.this.deleteData();
				} else if (!ObjectTools.safeIsEquals(e.getOldValue(), e.getNewValue())) {
					DocumentationTable.this.refreshInThread(0);
				}
			}
		});
		super.setParentForm(form);
		form.getDataComponentList().remove(this.getAttribute());
	}

	@Override
	public void deleteData() {
		if (this.deleting) {
			return;
		}
		try {
			this.deleting = true;
			super.deleteData();
			this.categoryTree.deleteData();
		} finally {
			this.deleting = false;
		}
	}

	/**
	 * Deletes from the entity the specified row.
	 *
	 * @param rowIndex
	 *            the row index
	 * @return the result of the execution of the delete instruction
	 * @throws Exception
	 * @see Entity#delete(Hashtable, int)
	 */
	@Override
	public EntityResult deleteEntityRow(int rowIndex) throws Exception {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
		} else if (this.dataBaseRemove) {
			IDMSService service = BeansFactory.getBean(IDMSService.class);
			Serializable fileId = (Serializable) this.getRowKey(rowIndex, DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
			service.fileDelete(fileId);
		}
		return new EntityResult();
	}

	public Serializable getCurrentIdDocument() {
		return this.currentIdDocument;
	}

	public void refreshIfNeededInThread() {
		try {
			this.silentDeleteData();

			// Minimum required filter
			Object idDms = this.parentForm.getDataFieldValue(this.form_id_dms_doc_field);
			if (idDms == null) {
				return;
			}

			if ((this.uRefreshThread != null) && this.uRefreshThread.isAlive()) {
				DocumentationTable.logger.warn("A thread is already refreshing. Ensure to invoke to checkRefreshThread() to cancel it.");
			}
			this.hideInformationPanel();
			this.uRefreshThread = new DocumentationTableRefreshThread(this);
			this.uRefreshThread.setDelay(0);
			this.uRefreshThread.start();
		} catch (Exception e) {
		}
	}

	private void silentDeleteData() {
		try {
			this.ignoreCheckRefreshThread = true;
			super.deleteData();
		} finally {
			this.ignoreCheckRefreshThread = false;
		}
	}

	@Override
	public void checkRefreshThread() {
		if (!this.ignoreCheckRefreshThread) {
			super.checkRefreshThread();
		}
	}

	public DocumentationTree getCategoryTree() {
		return this.categoryTree;
	}

	public Serializable getCurrentIdCategory() {
		Serializable idCategory = this.getCurrentIdCategoryToFilter();
		if ((idCategory instanceof SearchValue) || (idCategory instanceof NullValue)) {
			idCategory = null;
		}
		return idCategory;
	}

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent e) {
		if (e.getInteractionManagerMode() == InteractionManager.INSERT) {
			this.setEnabled(false);
		} else if (e.getInteractionManagerMode() == InteractionManager.UPDATE) {
			this.setEnabled(true);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (this.getParentForm().getInteractionManager().getCurrentMode() == InteractionManager.INSERT) {
			enabled = false;
		}
		super.setEnabled(enabled);
	}

	public boolean isIgnoreEvents() {
		return this.ignoreEvents;
	}

	/**
	 * Disable events on tree selection, because we are just setting root values.
	 *
	 * @param ignoreEvents
	 */
	public void setIgnoreEvents(boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
	}

	@Override
	public EntityResult updateTable(Hashtable keysValues, int viewColumnIndex, TableCellEditor tableCellEditor, Hashtable otherData, Object previousData) throws Exception {
		Map<String, Object> av = new Hashtable<>();
		TableSorter model = (TableSorter) this.table.getModel();
		String col = model.getColumnName(this.table.convertColumnIndexToModel(viewColumnIndex));
		Object newData = tableCellEditor.getCellEditorValue();
		if (newData != null) {
			av.put(col, newData);
		} else {
			if ((tableCellEditor != null) && (tableCellEditor instanceof com.ontimize.gui.table.CellEditor)) {
				com.ontimize.gui.table.CellEditor cE = (com.ontimize.gui.table.CellEditor) tableCellEditor;
				av.put(col, new NullValue(cE.getSQLDataType()));
			}
		}

		if (otherData != null) {
			av.putAll(otherData);
		}

		// To include calculted values in the update operation
		Hashtable rowData = this.getRowDataForKeys(keysValues);

		Vector<String> calculatedColumns = model.getCalculatedColumnsName();
		for (int i = 0; i < calculatedColumns.size(); i++) {
			String column = calculatedColumns.get(i);
			if (rowData.containsKey(column)) {
				av.put(column, rowData.get(column));
			}
		}

		Hashtable kv = (Hashtable) keysValues.clone();

		// Keys and parentkeys
		Vector vKeys = this.getKeys();
		for (int i = 0; i < vKeys.size(); i++) {
			Object atr = vKeys.get(i);
			if (atr.equals(col)) {
				Object oKeyValue = previousData;
				if (oKeyValue != null) {
					kv.put(atr, oKeyValue);
				}
			}
		}
		// Parentkeys with equivalences
		Vector vParentkeys = this.getParentKeys();
		for (int i = 0; i < vParentkeys.size(); i++) {
			Object atr = vParentkeys.get(i);
			Object oParentkeyValue = this.parentForm.getDataFieldValueFromFormCache(atr.toString());
			if (oParentkeyValue != null) {
				// since 5.2074EN-0.4
				// when equivalences, we must get equivalence value for
				// parentkey insteadof atr
				kv.put(this.getParentkeyEquivalentValue(atr), oParentkeyValue);
			}
		}

		IDMSService service = BeansFactory.getBean(IDMSService.class);
		Serializable fileId = (Serializable) kv.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		service.fileUpdate(fileId, av, null);
		return new EntityResult();
	}
}
