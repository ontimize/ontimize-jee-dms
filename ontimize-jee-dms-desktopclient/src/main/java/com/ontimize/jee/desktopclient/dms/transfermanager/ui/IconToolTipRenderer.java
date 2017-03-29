package com.ontimize.jee.desktopclient.dms.transfermanager.ui;

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
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;

/**
 * This class renders a JProgressBar in a table cell.
 */
public class IconToolTipRenderer extends JLabel implements TableCellRenderer {
	private static final long		serialVersionUID	= -4716351834035301680L;

	private static final ImageIcon	iconDownloading		= ImageManager.getIcon("ontimize-dms-images/downloading_16x16.gif");
	private static final ImageIcon	iconUploading		= ImageManager.getIcon("ontimize-dms-images/uploading_16x16.gif");
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

			switch ((Status) value) {
				case ON_PREPARE:
					break;
				case DOWNLOADING:
					tooltip = "Downloading";
					icon = IconToolTipRenderer.iconDownloading;
					break;
				case UPLOADING:
					tooltip = "Uploading";
					icon = IconToolTipRenderer.iconUploading;
					break;
				case PAUSED:
					tooltip = "Paused";
					icon = IconToolTipRenderer.iconPause;
					break;
				case CANCELLED:
					tooltip = "Cancelled";
					icon = IconToolTipRenderer.iconCancel;
					break;
				case COMPLETED:
					tooltip = "Complete";
					icon = IconToolTipRenderer.iconSucceed;
					break;
				case ERROR:
					tooltip = "Error";
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
			CellImageObserver observer = new CellImageObserver((TransferTable) table);
			IconToolTipRenderer.iconDownloading.setImageObserver(observer);
			IconToolTipRenderer.iconUploading.setImageObserver(observer);
		}
	}

	public static class CellImageObserver implements ImageObserver {
		protected TransferTable	table;
		protected int			columnIndex;

		public CellImageObserver(TransferTable table) {
			super();
			this.table = table;
			this.columnIndex = -1;
			int columnCount = table.getTransferModel().getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				if (table.getTransferModel().getColumnClass(i).equals(Status.class)) {
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
				AbstractDmsTransferable row = this.table.getTransferModel().getRow(i);
				switch (row.getStatus()) {
					case DOWNLOADING:
					case UPLOADING:
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