package com.ontimize.jee.server.services.dms;

import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.CheckingTools;

/**
 * The Class DMSConfiguration.
 */
public class DMSConfiguration {

	/** The engine. */
	private IDMSService	engine;

	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	public IDMSService getEngine() {
		CheckingTools.failIfNull(this.engine, "No dms engine defined");
		return this.engine;
	}

	/**
	 * Sets the engine.
	 *
	 * @param engine
	 *            the engine
	 */
	public void setEngine(IDMSService engine) {
		this.engine = engine;
	}
}

