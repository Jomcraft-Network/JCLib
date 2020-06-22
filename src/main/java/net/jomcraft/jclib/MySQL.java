package net.jomcraft.jclib;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class MySQL {
       
        private static String HOST = "";
        private String DATABASE = "";
        private static String USER = "";
        private static String PASSWORD = "";
       
        public Connection con;
       
        public MySQL(String host, String database, String user, String password) throws ClassNotFoundException, SQLException {
                HOST = host;
                this.DATABASE = database;
                USER = user;
                PASSWORD = password;
               
                connect();
        }
 
		public void connect() throws ClassNotFoundException, SQLException {
			try {
				try {
					Class.forName("org.mariadb.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					throw e;
				}

				this.con = DriverManager.getConnection("jdbc:mariadb://" + HOST + ":" + ConfigFile.COMMON.port.get() + "/" + DATABASE + "?autoReconnect=true&characterEncoding=utf-8", USER, PASSWORD);

			} catch (SQLException e) {
				throw e;
			}
		}
        
		public void close() {
			try {
				if (this.con != null) {
					this.con.close();
				}
			} catch (SQLException e) {
				JCLib.getLog().error("Couldn't close the MySQL-connection: ", e);
			}
		}
       
		public void update(final String qry) throws ClassNotFoundException, SQLException {
			try (final Statement st = con.createStatement()){
				st.executeUpdate(qry);

			} catch (SQLException e) {

				if (e.getMessage().startsWith("Could not create")) {
					JCLib.getLog().error("Couldn't send and update: ", e);
				} else {
					this.connect();
				}

			}
		}
       
		public ResultSet query(final String qry) throws ClassNotFoundException, SQLException {
			ResultSet rs = null;

			try (final Statement st = con.createStatement()){
				rs = st.executeQuery(qry);
			} catch (SQLException e) {

				if (e.getMessage().startsWith("Could not create")) {
					JCLib.getLog().error("Couldn't send a query: ", e);
				} else {
					this.connect();
				}

			}
			return rs;
		}
}