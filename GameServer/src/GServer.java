import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GServer {

	public static void main(String[] args) throws IOException {

		final int SERVER_PORT = 5556;
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("Database Driver Loaded");
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				System.out.println("Connected to database");
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
									
				String sql = "DROP TABLE IF EXISTS MAPS"; stmt.executeUpdate(sql); conn.commit();
				sql = "DROP TABLE IF EXISTS BPOSITION"; stmt.executeUpdate(sql); conn.commit();
				sql = "DROP TABLE IF EXISTS BOMBER"; stmt.executeUpdate(sql); conn.commit();
				sql = "DROP TABLE IF EXISTS BOMBPOSITION"; stmt.executeUpdate(sql); conn.commit();
				sql = "DROP TABLE IF EXISTS ENEMY"; stmt.executeUpdate(sql); conn.commit();
				sql = "DROP TABLE IF EXISTS ENEMYDOWN"; stmt.executeUpdate(sql); conn.commit();
				sql = "DROP TABLE IF EXISTS SCORES"; stmt.executeUpdate(sql); conn.commit();
				
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ServerSocket server = new ServerSocket(SERVER_PORT);
		System.out.println("Waiting for clients to connect...");
		while(true) {
			Socket s = server.accept();
			System.out.println("client connected");
			
			GSService myService = new GSService (s);
			Thread t = new Thread(myService);
			t.start();
		}
	}
}
