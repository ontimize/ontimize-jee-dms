package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxClient.Downloader;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager;

/**
 * A basic class for download and upload text files with dropbox API.
 *
 */
public class DropboxManager implements ICloudManager<DbxEntry> {
	private static final Logger		logger				= LoggerFactory.getLogger(DropboxManager.class);
	protected static final String	APPLICATION_NAME	= "OntimizeEEdrpbx";

	// Get your app key and secret from the Dropbox developers website.
	final static String				APP_KEY				= "6fwbab6m3ehjfed";
	final static String				APP_SECRET			= "l8jhgqcb1lcfxyv";
	protected static DropboxManager	instance;

	public static DropboxManager getInstance() {
		if (DropboxManager.instance == null) {
			DropboxManager.instance = new DropboxManager();
		}
		return DropboxManager.instance;
	}

	protected HttpTransport			httpTransport;
	protected JsonFactory			jsonFactory;
	protected DbxWebAuthNoRedirect	webAuth;
	protected DbxClient				service	= null;
	protected String				token	= null;

	/**
	 * Initialize initials attributes.
	 *
	 * @param String
	 *            basic configuration parameters.
	 */
	public DropboxManager() {
		this.getFlow();
	}

	@Override
	public String getRootFolderId() {
		return "/";
	}

	public boolean isSyncronized() {
		return this.service != null;
	}

	public void reset() {
		DropboxManager.instance = null;
		this.webAuth = null;
		this.service = null;
		this.httpTransport = null;
		this.jsonFactory = null;
	}

	/**
	 * Build an authorization flow and store it as a static class attribute.
	 *
	 * @return GoogleAuthorizationCodeFlow instance.
	 * @throws IOException
	 *             Unable to load client_secrets.json.
	 */
	private void getFlow() {
		try {
			this.reset();
			DbxAppInfo appInfo = new DbxAppInfo(DropboxManager.APP_KEY, DropboxManager.APP_SECRET);
			DbxRequestConfig config = new DbxRequestConfig(DropboxManager.APPLICATION_NAME, Locale.getDefault().toString());
			this.webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Get the authorization URL for authorize the application.
	 *
	 * @return String URL for authorize the application.
	 */
	public String getAuthorizationUrl() {
		return this.webAuth.start();
	}

	/**
	 * Set the authorization code and create the service.
	 *
	 * @param String
	 *            authorization code.
	 * @throws DbxException
	 */
	public void setAuthorizationCode(String code) throws DbxException {
		DbxAuthFinish authFinish = this.webAuth.finish(code);
		this.token = authFinish.accessToken;
		DbxRequestConfig config = new DbxRequestConfig(DropboxManager.APPLICATION_NAME, Locale.getDefault().toString());
		this.service = new DbxClient(config, this.token);
		System.out.println("Linked account: " + this.service.getAccountInfo().displayName);
	}

	/**
	 * Download a file's content.
	 *
	 * @param file
	 *            Drive File instance.
	 * @return InputStream containing the file's content if successful, {@code null} otherwise.
	 * @throws DbxException
	 */
	@Override
	public InputStream downloadFile(DbxEntry file) throws DbxException {
		Downloader downloader = this.service.startGetFile(file.path, null);
		return downloader.body;
	}

	/**
	 * Retrieve files in a folder.
	 *
	 * @param folderName
	 * @return
	 * @throws IOException
	 * @throws DbxException
	 */
	@Override
	public List<DbxEntry> retrieveFilesInFolder(String folderName) throws DbxException {
		DbxEntry.WithChildren listing = this.service.getMetadataWithChildren(folderName);
		return listing.children;
	}

	@Override
	public boolean isFolder(DbxEntry file) {
		return file.isFolder();
	}

	@Override
	public String getFolderId(DbxEntry file) {
		return file.path;
	}

	public static final DbxEntry.Folder	BACK_FOLDER	= new DbxEntry.Folder("/..", null, false);

	@Override
	public DbxEntry getBackFile() {
		return DropboxManager.BACK_FOLDER;
	}

	public static final DropboxTableRenderer	RENDERER	= new DropboxTableRenderer();

	@Override
	public TableCellRenderer getTableRenderer() {
		return DropboxManager.RENDERER;
	}
}