package com.ontimize.jee.desktopclient.dms.upload.web;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.upload.AbstractUploadableSelectionActionListener;
import com.utilmize.client.gui.buttons.UButton;

public class WebUploadableSelectionActionListener extends AbstractUploadableSelectionActionListener {
	private static final Logger	logger	= LoggerFactory.getLogger(WebUploadableSelectionActionListener.class);

	public WebUploadableSelectionActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected AbstractDmsUploadable acquireTransferable(ActionEvent ev) throws DmsException {
		String url = (String) this.getForm().getDataFieldValue("URL");
		String description = (String) this.getForm().getDataFieldValue("URL_DESCRIPTION");
		URI verifiedUrl;
		try {
			verifiedUrl = WebUploadableSelectionActionListener.verifyURL(url);
		} catch (MalformedURLException | URISyntaxException error) {
			throw new DmsException(error);
		}
		if (verifiedUrl == null) {
			throw new DmsException("dms.e_invalidurl");
		}
		return new WebDmsUploadable(verifiedUrl, description);
	}

	/**
	 * Verify whether an URL is valid
	 *
	 * @param fileURL
	 * @return the verified URL, null if invalid
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public static URI verifyURL(String fileURL) throws MalformedURLException, URISyntaxException {
		// Only allow HTTP or FTP URLs.
		if (!fileURL.toLowerCase().startsWith("http://") && !fileURL.toLowerCase().startsWith("https://") && !fileURL.toLowerCase().startsWith("ftp://")) {
			return null;
		}

		// Verify format of URL.
		URI verifiedUrl = new URI(fileURL);

		// Make sure URL specifies a file.
		if (verifiedUrl.toURL().getFile().length() < 2) {
			return null;
		}

		return verifiedUrl;
	}
}
