package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.ontimize.jee.desktopclient.dms.upload.cloud.ICloudManager;

/**
 * A basic class for download and upload text files with google drive API.
 *
 * @see https://developers.google.com/drive/examples/java#making_authorized_api_requests
 * @see https://developers.google.com/drive/quickstart-java"urn:ietf:wg:oauth:2.0:oob";
 */
public class GoogleDriveManager implements ICloudManager<File> {
	private static final Logger				logger					= LoggerFactory.getLogger(GoogleDriveManager.class);
	protected static final String			APPLICATION_NAME		= "Application";
	protected static GoogleDriveManager		instance;
	protected static final String			CLIENTSECRETS_LOCATION	= "/ontimize-dms-gdrive/client_secrets.json";

	// AccessType
	protected static final String			AUTO					= "auto";
	protected static final String			FORCE					= "force";

	// ApprovalPrompt
	protected static final String			ONLINE					= "online";
	protected static final String			OFFLINE					= "offline";

	// RedirectUri
	protected static final String			REDIRECT_URI			= "urn:ietf:wg:oauth:2.0:oob";
	protected static final String			REDIRECT_URI_AUTO		= "urn:ietf:wg:oauth:2.0:oob:auto";

	// Scopes
	protected static final List<String>		SCOPES					= Arrays.asList(DriveScopes.DRIVE);

	protected HttpTransport					httpTransport;
	protected JsonFactory					jsonFactory;
	protected GoogleAuthorizationCodeFlow	flow;
	protected Drive							service					= null;
	protected String						token					= null;

	/**
	 * Initialize initials attributes.
	 *
	 * @param String
	 *            basic configuration parameters.
	 */
	public GoogleDriveManager() {
		this.createFlow();
	}

	public static GoogleDriveManager getInstance() {
		if (GoogleDriveManager.instance == null) {
			GoogleDriveManager.instance = new GoogleDriveManager();
		}
		return GoogleDriveManager.instance;
	}

	@Override
	public String getRootFolderId() throws IOException {
		return this.service.about().get().execute().getRootFolderId();
	}

	public boolean isSyncronized() {
		return this.service != null;
	}

	public void reset() {
		GoogleDriveManager.instance = null;
		this.flow = null;
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
	private void createFlow() {
		try {
			this.reset();
			this.httpTransport = new NetHttpTransport();
			this.jsonFactory = new JacksonFactory();
			InputStreamReader readerSecretsLocation = new InputStreamReader(
					GoogleDriveManager.class.getResourceAsStream(GoogleDriveManager.CLIENTSECRETS_LOCATION));

			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(this.jsonFactory, readerSecretsLocation);
			this.flow = new GoogleAuthorizationCodeFlow.Builder(this.httpTransport, this.jsonFactory, clientSecrets, GoogleDriveManager.SCOPES)
					.setAccessType(GoogleDriveManager.ONLINE).setApprovalPrompt(GoogleDriveManager.AUTO).build();
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
		return this.flow.newAuthorizationUrl().setRedirectUri(GoogleDriveManager.REDIRECT_URI_AUTO).build();
	}

	/**
	 * Get root URL
	 *
	 * @return
	 */
	public String getRootUrl() {
		return this.service.getRootUrl();
	}

	/**
	 * Set the authorization code and create the service.
	 *
	 * @param String
	 *            authorization code.
	 */
	public void setAuthorizationCode(String code) throws IOException {
		GoogleTokenResponse response = this.flow.newTokenRequest(code).setRedirectUri(GoogleDriveManager.REDIRECT_URI_AUTO).execute();
		GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

		// Create a new authorized API client
		this.service = new Drive.Builder(this.httpTransport, this.jsonFactory, credential).setApplicationName(GoogleDriveManager.APPLICATION_NAME)
				.build();
	}

	/**
	 * Upload a text file.
	 *
	 * @param String
	 *            path of the file.
	 * @return String name of the file in google drive.
	 */
	public String uploadTextFile(String filePath, String title) throws IOException {
		File body = new File();
		body.setTitle(title);
		body.setDescription("A test document");
		body.setMimeType("text/plain");
		java.io.File fileContent = new java.io.File(filePath);
		FileContent mediaContent = new FileContent("text/plain", fileContent);
		File file = this.service.files().insert(body, mediaContent).execute();
		return file.getId();
	}

	/**
	 * Get the content of a file.
	 *
	 * @param File
	 *            to get the content.
	 * @return String content of the file.
	 */
	public String downloadTextFile(File gFile) throws IOException {
		GenericUrl url = new GenericUrl(gFile.getDownloadUrl());
		HttpResponse response = this.service.getRequestFactory().buildGetRequest(url).execute();
		try {
			return new Scanner(response.getContent()).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	/**
	 * Get the content of a file.
	 *
	 * @param String
	 *            the file ID.
	 * @return String content of the file.
	 */
	public String downloadTextFile(String fileID) throws IOException {
		File file = this.service.files().get(fileID).execute();
		return this.downloadTextFile(file);
	}

	/**
	 * Retrieve a list of File resources.
	 *
	 * @param service
	 *            Drive API service instance.
	 * @return List of File resources.
	 * @author Google
	 * @throws IOException
	 */
	public List<File> retrieveAllFiles() throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = null;

		request = this.service.files().list();

		do {
			try {
				FileList files = request.execute();

				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				GoogleDriveManager.logger.error("An error occurred: ", e);
				request.setPageToken(null);
			}
		} while ((request.getPageToken() != null) && (request.getPageToken().length() > 0));

		return result;
	}

	/**
	 * Download a file's content.
	 *
	 * @param file
	 *            Drive File instance.
	 * @return InputStream containing the file's content if successful, {@code null} otherwise.
	 */
	@Override
	public InputStream downloadFile(File file) throws IOException {
		if ((file.getDownloadUrl() != null) && (file.getDownloadUrl().length() > 0)) {
			HttpResponse resp = this.service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
			return resp.getContent();
		} else {
			// The file doesn't have any content stored on Drive.
			return null;
		}
	}

	/**
	 * Retrieve files in a folder.
	 *
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<File> retrieveFilesInFolder(String folderName) throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = this.service.files().list();
		request.setQ("'" + folderName + "' in parents");
		do {
			try {
				FileList files = request.execute();
				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				GoogleDriveManager.logger.error("An error occurred: ", e);
				request.setPageToken(null);
			}
		} while ((request.getPageToken() != null) && (request.getPageToken().length() > 0));
		Collections.sort(result, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.getMimeType().endsWith(".folder")) {
					if (o2.getMimeType().endsWith(".folder")) {
						return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
					} else {
						return -1;
					}
				} else if (o2.getMimeType().endsWith(".folder")) {
					return 1;
				} else {
					return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
				}
			}
		});
		return result;
	}

	@Override
	public boolean isFolder(File file) {
		return file.getMimeType().endsWith(".folder");
	}

	@Override
	public String getFolderId(File file) {
		return file.getId();
	}


	public static final File	BACK_FILE	= new File();
	static {
		GoogleDriveManager.BACK_FILE.setTitle("..");
	}

	@Override
	public File getBackFile() {
		return GoogleDriveManager.BACK_FILE;
	}

	public static final GoogleDriveTableRenderer	TABLECELLRENDERER	= new GoogleDriveTableRenderer();
	@Override
	public TableCellRenderer getTableRenderer() {
		return GoogleDriveManager.TABLECELLRENDERER;
	}
}