package com.ontimize.jee.desktopclient.dms.transfermanager;

public class DmsException extends Exception {
	private static final long	serialVersionUID	= 1L;

	public DmsException() {
		super();

	}

	public DmsException(String message) {
		super(message);
	}

	public DmsException(String message, Exception ex) {
		super(message, ex);
	}

}
