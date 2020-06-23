package net.jomcraft.jclib;

interface DBRequestHandler {
	
	public DBRequestHandler establishCon(MySQL connection);
	
	public void sendVoidQuery(String query);
	
	public void shutdown();

}
