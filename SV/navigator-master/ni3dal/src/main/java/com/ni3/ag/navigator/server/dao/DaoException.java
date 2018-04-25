package com.ni3.ag.navigator.server.dao;

public class DaoException extends RuntimeException{

	private static final long serialVersionUID = -2768340361091122559L;

	public DaoException()
	{
		super();
	}

	public DaoException(final String message)
	{
		super(message);
	}

	public DaoException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public DaoException(final Throwable cause)
	{
		super(cause);
	}

}
