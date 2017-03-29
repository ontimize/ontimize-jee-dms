package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.net.MalformedURLException;

import com.google.api.services.drive.model.File;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

/**
 * The Class LocalDiskDmsTransferable.
 */
public class GoogleDriveDmsUploadable extends AbstractDmsUploadable {

	/** The file. */
	private final File	file;

	/**
	 * Instantiates a new local disk dms transferable.
	 *
	 * @param uri
	 *            the uri
	 * @param description
	 *            the description
	 * @param fileName
	 *            the file name
	 * @param fileSize
	 *            the file size
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	public GoogleDriveDmsUploadable(File file, String description, String fileName, Long fileSize) throws MalformedURLException {
		super(description, fileName, fileSize);
		this.file = file;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}
}
