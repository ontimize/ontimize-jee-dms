package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.net.MalformedURLException;

import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

/**
 * The Class LocalDiskDmsTransferable.
 */
public class GoogleDriveDmsUploadable extends AbstractDmsUploadable {

	private static final long	serialVersionUID	= 1L;
	/** The file. */
	private final GoogleFile file;

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
	public GoogleDriveDmsUploadable(GoogleFile file, String description, String fileName, Long fileSize) throws MalformedURLException {
		super(description, fileName, fileSize);
		this.file = file;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public GoogleFile getFile() {
		return this.file;
	}
}
