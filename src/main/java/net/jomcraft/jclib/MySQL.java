package net.jomcraft.jclib;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class MySQL {
       
        private static String HOST = "";
        private static String DATABASE = "";
        private static String USER = "";
        private static String PASSWORD = "";
       
        public static Connection con;
       
        public MySQL(String host, String database, String user, String password) {
                HOST = host;
                DATABASE = database;
                USER = user;
                PASSWORD = password;
               
                connect();
        }
 
        public static void connect() {
                try {
                	try {
						Class.forName("org.mariadb.jdbc.Driver");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
                	
                        con = DriverManager.getConnection("jdbc:mariadb://" + HOST + ":" + ConfigFile.COMMON.port.get() + "/" + DATABASE + "?autoReconnect=true&characterEncoding=utf-8", USER, PASSWORD );
                     
                } catch (SQLException e) {
                	e.printStackTrace();
                	System.exit(1);
                }
        }
        
        public static void close() {
                try {
                        if(con != null) {
                                con.close();
                             
                        }
                } catch (SQLException e) {
                	e.printStackTrace();
                	System.exit(1);
                }
        }
       
        public static void update(String qry) {
                try {
                        Statement st = con.createStatement();
                        st.executeUpdate(qry);
                        st.close();
                } catch (SQLException e) {
                	
                	if(e.getMessage().startsWith("Could not create")) {
                        
                		e.printStackTrace();
                        	System.exit(1);
                        }else {
                        	connect();
                        	
                        }
                        
                }
        }
       
        public static ResultSet query(String qry) {
                ResultSet rs = null;
               
                try {
                        Statement st = con.createStatement();
                        rs = st.executeQuery(qry);
                } catch (SQLException e) {
                        
                	if(e.getMessage().startsWith("Could not create")) {
                		e.printStackTrace();
                        	System.exit(1);
                        }else {
                        	connect();
                        
                        }
                       
                }
                return rs;
        }
}