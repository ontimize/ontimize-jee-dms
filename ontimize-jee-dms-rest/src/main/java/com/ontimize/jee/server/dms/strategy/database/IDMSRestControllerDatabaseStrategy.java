package com.ontimize.jee.server.dms.strategy.database;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.dms.strategy.IDMSRestConstrollerStrategy;
import com.ontimize.jee.server.rest.FileListParameter;
import com.ontimize.jee.server.rest.InsertParameter;
import com.ontimize.jee.server.rest.QueryParameter;
import com.ontimize.jee.server.rest.UpdateFileParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IDMSRestControllerDatabaseStrategy extends IDMSRestConstrollerStrategy {

    //CONSTANTS

    /** The Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger( IDMSRestControllerDatabaseStrategy.class );

    protected static final String CATEGORY_FILTER_KEY = "FM_FOLDER_PARENT_KEY";

// ------------------------------------------------------------------------------------------------------------------ //

    /** Constructor. */
    public IDMSRestControllerDatabaseStrategy(){ super(); }

// ------------------------------------------------------------------------------------------------------------------ //
// ----------------------------------------------| ENDPOINTS |------------------------------------------------------- //
// ------------------------------------------------------------------------------------------------------------------ //

    @Override
    public ResponseEntity<List<? extends OFile>> documentGetFiles( final Integer workspaceId, final QueryParameter queryParam ){
        try {
            List<OFile> files = new ArrayList<>();
            Map<Object, Object> filter = queryParam.getFilter();
            List<Object> columns = queryParam.getColumns();

            Object parentCategory = null;
            if (filter.containsKey(CATEGORY_FILTER_KEY)) {
                parentCategory = filter.remove(CATEGORY_FILTER_KEY);
                filter.put(this.dmsNameConverter.getCategoryIdColumn(), parentCategory);
            } else {
                filter.put(this.dmsNameConverter.getCategoryIdColumn(), new SearchValue(SearchValue.NULL, null));
            }

            // Get categories and add them as folders
            DMSCategory categories = this.service
                    .categoryGetForDocument(workspaceId, this.getCategoryColumns(columns));
            this.getCategoriesAsFiles(categories, parentCategory, files, false);

            // Get files and add them
            EntityResult er = this.service.documentGetFiles(workspaceId, filter, this.getFileColumns(columns));
            if (EntityResult.OPERATION_WRONG != er.getCode()) {
                for (int i = 0; i < er.calculateRecordNumber(); i++) {
                    files.add(this.dmsNameConverter.createOFile(er.getRecordValues(i)));
                }
            }
            return new ResponseEntity<List<? extends OFile>>(files, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public @ResponseBody void fileGetContent( final Integer fileId, final HttpServletResponse response ) {
        InputStream fis = null;
        try {
            Map<Object, Object> criteria = new HashMap<>();
            criteria.put(this.dmsNameConverter.getFileIdColumn(), fileId);
            EntityResult er = this.service.fileQuery(criteria, this.getFileColumns(null));
            if (EntityResult.OPERATION_WRONG != er.getCode() && er.calculateRecordNumber() > 0) {
                fis = this.service.fileGetContent(fileId);
                FileCopyUtils.copy(fis, response.getOutputStream());

                String fileName = (String) er.getRecordValues(0).get(this.dmsNameConverter.getFileNameColumn());
                Number fileSize = (Number) er.getRecordValues(0).get(this.dmsNameConverter.getFileSizeColumn());

                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentLengthLong(fileSize.longValue());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error("{}", e.getMessage(), e);
                }
            }
        }
    }



    @Override
    public ResponseEntity<Map<String, String>> fileGetContent( final Integer workspaceId, final List<OFile> files ) {
        ZipOutputStream zos = null;
        try {
            File zipFile = File.createTempFile("download_", ".zip");
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            this.addFilesToZip(workspaceId, files, zos, null);

            if (zipFile != null) {
                String zipFileName = zipFile.getName();
                Map<String, String> response = new HashMap<>();
                response.put("file", zipFileName);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LOGGER.error("{}", e.getMessage(), e);
            }
        }
    }



    @Override
    public @ResponseBody void fileGetZip( final String file, final HttpServletResponse response ){
        InputStream fis = null;
        File zipFile = null;
        try {
            zipFile = new File(System.getProperty("java.io.tmpdir"), file + ".zip");
            fis = new FileInputStream(zipFile);
            FileCopyUtils.copy(fis, response.getOutputStream());

            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");
            response.setContentLengthLong(file.length());
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (zipFile != null) {
                zipFile.delete();
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error("{}", e.getMessage(), e);
                }
            }
        }
    }



    @Override
    public ResponseEntity<DocumentIdentifier> fileInsert( final Integer workspaceId, final MultipartFile multipart, final Object folderId ){
        try {
            InputStream is = new ByteArrayInputStream(multipart.getBytes());
            Map<Object, Object> av = new HashMap<>();
            av.put(this.dmsNameConverter.getFileNameColumn(), multipart.getOriginalFilename());
            if (folderId != null) {
                av.put(this.dmsNameConverter.getCategoryIdColumn(), folderId);
            }
            DocumentIdentifier id = this.service.fileInsert(workspaceId, av, is);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<Void> delete( final Integer workspaceId, final FileListParameter deleteParameter ) throws DmsException {
        try {
            List<OFile> fileList = deleteParameter.getFileList();
            for (OFile file : fileList) {
                if (file.isDirectory()) {
                    List<OFile> categoryFileList = this.getCategoryFiles(workspaceId, file.getId(), true);
                    for (OFile cFile : categoryFileList) {
                        this.deleteOFile(cFile);
                    }
                }
                this.deleteOFile(file);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<Void> folderInsert( final Integer workspaceId, final String name, final InsertParameter insertParam ){
        try {
            Map<Object, Object> data = insertParam.getData();
            Object categoryId = null;
            if (data.containsKey(CATEGORY_FILTER_KEY)) {
                categoryId = data.remove(CATEGORY_FILTER_KEY);
            }
            this.service.categoryInsert(workspaceId, name, (Serializable) categoryId, null);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<Void> fileUpdate( final UpdateFileParameter updateFileParameter ){
        try {
            OFile file = updateFileParameter.getFile();
            if (file != null) {
                Map<Object, Object> data = updateFileParameter.getParams();
                Map<String, Object> av = new HashMap<>();
                if (data.containsKey("name")) {
                    if (file.isDirectory()) {
                        av.put(DMSNaming.CATEGORY_CATEGORY_NAME, data.get("name"));
                        this.service.categoryUpdate(file.getId(), av);
                    } else {
                        av.put(DMSNaming.DOCUMENT_FILE_NAME, data.get("name"));
                        this.service.fileUpdate(file.getId(), av, this.service.fileGetContent(file.getId()));
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<Number> createDocument( final String name ){
        try {
            Map<String, Object> av = new HashMap<>();
            av.put(DMSNaming.DOCUMENT_DOCUMENT_NAME, name == null ? "docname" : name);
            DocumentIdentifier documentIdentifier = this.service.documentInsert(av);
            return new ResponseEntity<>((Number) documentIdentifier.getDocumentId(), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("{}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


// ------------------------------------------------------------------------------------------------------------------ //
// ----------------------------------------------| CONVERTERS |------------------------------------------------------ //
// ------------------------------------------------------------------------------------------------------------------ //

    protected List<?> getFileColumns( final List<?> columns ) {
        if (this.dmsNameConverter != null) {
            return this.dmsNameConverter.getFileColumns(columns);
        }
        return columns;
    }

    protected List<?> getCategoryColumns( final List<?> columns ) {
        if (this.dmsNameConverter != null) {
            return this.dmsNameConverter.getCategoryColumns(columns);
        }
        return columns;
    }

// ------------------------------------------------------------------------------------------------------------------ //
// -----------------------------------------------| UTILITIES |------------------------------------------------------ //
// ------------------------------------------------------------------------------------------------------------------ //

    protected void getCategoriesAsFiles( final DMSCategory category, final Object parentId, final List<OFile> files, final boolean recursive) {
        for (DMSCategory cat : category.getChildren()) {
            Object catId = category.getIdCategory();
            if ((catId == null && parentId == null) || (catId != null && parentId != null && catId.equals(parentId))) {
                files.add(this.categoryToFile(cat));
                if (recursive) {
                    this.getCategoriesAsFiles(cat, cat.getIdCategory(), files, recursive);
                }
            } else {
                this.getCategoriesAsFiles(cat, parentId, files, recursive);
            }
        }
    }

    protected OFile categoryToFile( final DMSCategory cat ) {
        OFile dmsFile = new OFile();
        dmsFile.setId((Integer) cat.getIdCategory());
        dmsFile.setName(cat.getName());
        dmsFile.setDirectory(true);
        return dmsFile;
    }

    protected void deleteOFile( final OFile file ) throws DmsException {
        if (file.isDirectory()) {
            this.service.categoryDelete(file.getId());
        } else {
            this.service.fileDelete(file.getId());
        }
    }

    /**
     * Get the files contained in a category
     * @param documentId the document identifier
     * @param categoryId the category identifier
     * @param includeSubCategories include the files in sub-categories or not
     * @return
     * @throws DmsException
     */
    protected List<OFile> getCategoryFiles( final Integer documentId, final Integer categoryId, final boolean includeSubCategories )
            throws DmsException {
        List<OFile> files = new ArrayList<>();

        DMSCategory categories = this.service.categoryGetForDocument(documentId, this.getCategoryColumns(null));
        this.getCategoriesAsFiles(categories, categoryId, files, includeSubCategories);

        Map<Object, Object> filter = new HashMap<>();
        List<Object> categoriesIds = new ArrayList<>();
        categoriesIds.add(categoryId);
        if (includeSubCategories) {
            for (OFile file : files) {
                categoriesIds.add(file.getId());
            }
        }

        filter.put(this.dmsNameConverter.getCategoryIdColumn(), new SearchValue(SearchValue.IN, categoriesIds));

        EntityResult er = this.service.documentGetFiles(documentId, filter, this.getFileColumns(null));
        if (EntityResult.OPERATION_WRONG != er.getCode()) {
            for (int i = 0; i < er.calculateRecordNumber(); i++) {
                files.add(this.dmsNameConverter.createOFile(er.getRecordValues(i)));
            }
        }
        return files;
    }

    protected void addFilesToZip( final Integer documentId, final List<OFile> oFiles, final ZipOutputStream zos, String parentPath )
            throws DmsException, IOException {
        if (parentPath == null) {
            parentPath = "";
        }
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), parentPath);
        for (OFile oFile : oFiles) {
            if (oFile.isDirectory()) {
                File directory = new File(tmpDir, oFile.getName());
                directory.mkdirs();
                List<OFile> categoryFiles = this.getCategoryFiles(documentId, oFile.getId(), false);
                String folderName = parentPath + directory.getName() + File.separatorChar;
                zos.putNextEntry(new ZipEntry(folderName));
                this.addFilesToZip(documentId, categoryFiles, zos, folderName);
            } else {
                byte[] buffer = new byte[1024];
                Map<Object, Object> criteria = new HashMap<>();
                criteria.put(this.dmsNameConverter.getFileIdColumn(), oFile.getId());
                EntityResult er = this.service.fileQuery(criteria, this.getFileColumns(null));
                if (EntityResult.OPERATION_WRONG != er.getCode() && er.calculateRecordNumber() > 0) {
                    InputStream fis = this.service.fileGetContent(oFile.getId());
                    String fileName = parentPath + oFile.getName();
                    zos.putNextEntry(new ZipEntry(fileName));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
        }
    }
}