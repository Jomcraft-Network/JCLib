package net.jomcraft.jclib.events;

import net.minecraftforge.eventbus.api.Event.Result;

public class DBConnectEvent {
	
    private final String databaseName;
    private final Result result;
    
    public DBConnectEvent(String dbName, Result result) {
    	this.databaseName = dbName;
    	this.result = result;
    }
    
    public String getDBName() {
    	return this.databaseName;
    }

    public Result getResult() {
    	return this.result;
    }
}