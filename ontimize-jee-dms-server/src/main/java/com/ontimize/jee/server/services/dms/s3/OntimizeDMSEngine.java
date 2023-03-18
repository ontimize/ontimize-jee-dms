package com.ontimize.jee.server.services.dms.s3;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.services.s3.IS3Repository;
import com.ontimize.jee.server.configuration.OntimizeConfiguration;
import com.ontimize.jee.server.services.dms.IDMSServiceServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class OntimizeDMSEngine implements IDMSServiceServer, InitializingBean {

    //CONSTANTS

    /** The CONSTANTLOGGER */
    private static final Logger logger = LoggerFactory.getLogger( OntimizeDMSEngine.class );



    //PROPERTIES

    /** The ontimize configuration. */
    @Autowired
    protected OntimizeConfiguration ontimizeConfiguration;

    /** The name of the S3 Bucket */
    @Value( "${ontimize.dms.aws.bucket:}" )
    private String bucket;

    /** The proxy of S3 repository */
    @Qualifier( "S3RepositoryProxy" )
    private @Autowired IS3Repository s3Repository;

// ------------------------------------------------------------------------------------------------------------------ \\

    public OntimizeDMSEngine() {
        super();
    }


    @Override
    public InputStream fileGetContent(Serializable fileId) throws DmsException {
        return null;
    }

    @Override
    public DocumentIdentifier fileInsert(Serializable documentId, Map<?, ?> av, InputStream file) throws DmsException {
        return null;
    }

    @Override
    public EntityResult fileQuery(Map<?, ?> criteria, List<?> attributes) throws DmsException {
        return null;
    }

    @Override
    public void fileDelete(Serializable fileId) throws DmsException {

    }

    @Override
    public DocumentIdentifier fileUpdate(Serializable fileId, Map<?, ?> attributesValues, InputStream file) throws DmsException {
        return null;
    }

    @Override
    public DocumentIdentifier fileVersionOverrideContent(Serializable fileVersionId, InputStream file) throws DmsException {
        return null;
    }

    @Override
    public EntityResult fileGetVersions(Serializable fileId, Map<?, ?> kv, List<?> attributes) throws DmsException {
        return null;
    }

    @Override
    public EntityResult fileVersionQuery(Serializable fileVersionId, List<?> attributes) throws DmsException {
        return null;
    }

    @Override
    public InputStream fileGetContentOfVersion(Serializable fileVersionId) throws DmsException {
        return null;
    }

    @Override
    public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion) throws DmsException {

    }

    @Override
    public EntityResult documentQuery(List<?> attributes, Map<?, ?> criteria) throws DmsException {
        return null;
    }

    @Override
    public DocumentIdentifier documentInsert(Map<?, ?> av) throws DmsException {
        return null;
    }

    @Override
    public void documentUpdate(Serializable documentId, Map<?, ?> attributesValues) throws DmsException {

    }

    @Override
    public void documentDelete(Serializable documentId) throws DmsException {

    }

    @Override
    public void documentAddProperties(Serializable documentId, Map<String, String> properties) throws DmsException {

    }

    @Override
    public void documentDeleteProperties(Serializable documentId, List<String> propertyKeys) throws DmsException {

    }

    @Override
    public String documentGetProperty(Serializable documentId, String propertyKey) throws DmsException {
        return null;
    }

    @Override
    public Map<String, String> documentGetProperties(Serializable documentId, Map<?, ?> kv) throws DmsException {
        return null;
    }

    @Override
    public EntityResult documentGetFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes) throws DmsException {
        return null;
    }

    @Override
    public EntityResult documentGetAllFiles(Serializable documentId, Map<?, ?> kv, List<?> attributes) throws DmsException {
        return null;
    }

    @Override
    public EntityResult getRelatedDocument(Serializable documentId) throws DmsException {
        return null;
    }

    @Override
    public void setRelatedDocuments(Serializable documentId, Serializable otherDocumentId) throws DmsException {

    }

    @Override
    public DMSCategory categoryGetForDocument(Serializable documentId, List<?> attributes) throws DmsException {
        return null;
    }

    @Override
    public Serializable categoryInsert(Serializable documentId, String name, Serializable parentCategoryId, Map<?, ?> otherData) throws DmsException {
        return null;
    }

    @Override
    public void categoryUpdate(Serializable idCategory, Map<?, ?> av) throws DmsException {

    }

    @Override
    public void categoryDelete(Serializable idCategory) throws DmsException {

    }

    @Override
    public void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles) throws DmsException {

    }

    @Override
    public Path fileGetPath(Serializable fileId) throws DmsException {
        return null;
    }

    @Override
    public Path fileGetPathOfVersion(Serializable fileVersionId) throws DmsException {
        return null;
    }

    @Override
    public void updateSettings() throws DmsException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
