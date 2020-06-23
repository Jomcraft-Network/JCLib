package net.jomcraft.jclib;

import java.sql.SQLException;

public class JCLibConnectionRequest implements DBRequestHandler {

	@Override
	public DBRequestHandler establishCon(MySQL connection) {
		JCLib.mysql = connection;
		return this;
	}

	@Override
	public void sendVoidQuery(String query) {
		try {
			JCLib.mysql.update(query);
		} catch (ClassNotFoundException | SQLException e) {
			JCLib.getLog().error("Exception while keeping alive: ", e);
		}
	}

	@Override
	public void shutdown() {
		JCLib.mysql.close();
	}
}