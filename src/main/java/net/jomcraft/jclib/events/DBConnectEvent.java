package net.jomcraft.jclib.events;

import net.minecraftforge.eventbus.api.Event;

public class DBConnectEvent extends Event {
	
    private final String databaseName;
    private final Result result;
    
    public DBConnectEvent(String dbName, Result result) {
    	this.databaseName = dbName;
    	this.result = result;
    }
    
    public String getDBName() {
    	return this.databaseName;
    }
    
    @Override
    public Result getResult() {
    	return this.result;
    }
    
    @Override
    public boolean isCancelable() {
    	return false;
    }

}
