package com.ontimize.jee.desktopclient.dms.upload.web;

import java.net.MalformedURLException;
import java.net.URI;

import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;

/**
 * The Class LocalDiskDmsTransferable.
 */
public class WebDmsUploadable extends AbstractDmsUploadable {

	/** The file. */
	private final URI	uri;

	/**
	 * Instantiates a new local disk dms transferable.
	 *
	 * @param uri
	 *            the uri
	 * @param description
	 *            the description
	 * @throws MalformedURLException
	 */
	public WebDmsUploadable(URI uri, String description) throws MalformedURLException {
		super(description, WebDmsUploadable.extractFileName(uri.toURL().getPath()), null);
		this.uri = uri;
	}

	private static String extractFileName(String path) {
		int indx = path.lastIndexOf('/');
		if (indx >= 0) {
			return path.substring(indx + 1);
		}
		return path;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public URI getUri() {
		return this.uri;
	}
}
