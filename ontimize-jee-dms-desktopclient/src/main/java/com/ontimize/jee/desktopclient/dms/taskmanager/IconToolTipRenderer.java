package com.ontimize.jee.desktopclient.dms.taskmanager;

import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.ontimize.gui.images.ImageManager;

/**
 * This class renders a JProgressBar in a table cell.
 */
public class IconToolTipRenderer extends JLabel implements TableCellRenderer {
	private static final long		serialVersionUID	= -4716351834035301680L;

	private static final ImageIcon	iconRunning			= ImageManager.getIcon("ontimize-dms-images/loading_16x16.gif");
	private static final ImageIcon	iconPause			= ImageManager.getIcon("ontimize-dms-images/pause_16x16.png");
	private static final ImageIcon	iconCancel			= ImageManager.getIcon("ontimize-dms-images/cancel_16x16.png");
	private static final ImageIcon	iconError			= ImageManager.getIcon("ontimize-dms-images/error_16x16.png");
	private static final ImageIcon	iconSucceed			= ImageManager.getIcon("ontimize-dms-images/succeed_16x16.png");

	private static HashSet<JTable>	registeredTables	= new HashSet<JTable>();

	/**
	 * Constructor for ProgressRenderer.
	 */
	public IconToolTipRenderer() {
		super();
		this.setOpaque(false);
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}

	/**
	 * Returns this JProgressBar as the renderer for the given table cell.
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		this.registerTable(table);
		Icon icon = null;
		String tooltip = "";
		if (value != null) {

			switch ((TaskStatus) value) {
				case ON_PREPARE:
					break;
				case RUNNING:
					tooltip = "task.Running";
					icon = IconToolTipRenderer.iconRunning;
					break;
				case PAUSED:
					tooltip = "task.Paused";
					icon = IconToolTipRenderer.iconPause;
					break;
				case CANCELLED:
					tooltip = "task.Cancelled";
					icon = IconToolTipRenderer.iconCancel;
					break;
				case COMPLETED:
					tooltip = "task.Complete";
					icon = IconToolTipRenderer.iconSucceed;
					break;
				case ERROR:
					tooltip = "task.Error";
					icon = IconToolTipRenderer.iconError;
					break;
			}
		}
		this.setIcon(icon);
		this.setToolTipText(tooltip);
		return this;
	}

	private void registerTable(JTable table) {
		boolean add = IconToolTipRenderer.registeredTables.add(table);
		if (add) {
			CellImageObserver observer = new CellImageObserver((TaskTable) table);
			IconToolTipRenderer.iconRunning.setImageObserver(observer);
		}
	}

	public static class CellImageObserver implements ImageObserver {
		protected TaskTable	table;
		protected int			columnIndex;

		public CellImageObserver(TaskTable table) {
			super();
			this.table = table;
			this.columnIndex = -1;
			int columnCount = table.getTaskModel().getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				if (table.getTaskModel().getColumnClass(i).equals(TaskStatus.class)) {
					this.columnIndex = i;
					break;
				}
			}
		}

		@Override
		public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
			if (this.columnIndex == -1) {
				return (flags & (ImageObserver.ALLBITS | ImageObserver.ABORT)) == 0;
			}

			for (int i = 0; i < this.table.getModel().getRowCount(); i++) {
				ITask row = this.table.getTaskModel().getRow(i);
				switch (row.getStatus()) {
					case RUNNING:
						if ((flags & (ImageObserver.FRAMEBITS | ImageObserver.ALLBITS)) != 0) {
							Rectangle rect = this.table.getCellRect(i, this.columnIndex, false);
							this.table.repaint(rect);
						}
						break;
					default:
						break;
				}
			}
			return (flags & (ImageObserver.ALLBITS | ImageObserver.ABORT)) == 0;
		}
	}
}