package net.jomcraft.jclib;

public class ConnectionRequest {
	
	private final String dbName;
	private final DBRequestHandler handler;
	
	public ConnectionRequest(String dbName, DBRequestHandler handler) {
		this.dbName = dbName;
		this.handler = handler;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public DBRequestHandler getHandler() {
		return handler;
	}
}