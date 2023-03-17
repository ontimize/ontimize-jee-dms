package com.ontimize.jee.server.dms.strategy;

import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.server.dms.rest.IDMSNameConverter;
import com.ontimize.jee.server.dms.rest.IDMSRestControllerEndpoints;

public abstract class IDMSRestConstrollerStrategy implements IDMSRestControllerEndpoints {

    /** The Service. */
    protected IDMSService service;

    /** The Converter. */
    protected IDMSNameConverter dmsNameConverter;

// ------------------------------------------------------------------------------------------------------------------ //

    /** Constructor */
    public IDMSRestConstrollerStrategy() {}

    /**
     * The Setter method of Service.
     * @param service The service.
     */
    public void setService(final IDMSService service ){
        this.service = service;
    }

    /**
     * The Setter method of Name Converter of DMS.
     * @param dmsNameConverter The Name Converter of DMS.
     */
    public void setDmsNameConverter( final IDMSNameConverter dmsNameConverter ){
        this.dmsNameConverter = dmsNameConverter;
    }
}
