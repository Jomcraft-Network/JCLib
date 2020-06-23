package net.jomcraft.jclib;

public interface DBRequestHandler {
	
	public DBRequestHandler establishCon(MySQL connection);
	
	public void sendVoidQuery(String query);
	
	public void shutdown();

}
