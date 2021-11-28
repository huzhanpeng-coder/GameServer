import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

public class GSService implements Runnable {
	final int CLIENT_PORT = 5656;
	private Connection conn = null;
	private Statement stmt = null;
	
	private int[][] map = { {3,3,1,3,1,1,1,1,1,3,3,3},{3,2,1,3,2,3,1,2,1,3,2,3},{3,1,3,3,3,3,1,1,1,1,3,1},{3,3,3,3,2,1,3,2,1,1,3,1},{3,1,3,3,1,3,3,1,1,1,3,1},{3,2,3,1,2,1,3,2,1,1,2,1},{3,1,3,1,1,1,3,1,1,1,3,1} };
	private long map1,map2,map3,map4,map5,map6,map7;
	private long bomber1, bomber2, bomber3, bomber4, bomber5, bomber6, bomber7;
	private int[][]bombermanPosition = { {1,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3},{3,3,3,3,3,3,3,3,3,3,3,3} };
	private enemy enemy[] = new enemy[4];
	private bomb bomb_ex[] = new bomb[5];
	private bomber bomberman;
	private long bman1,bman2,bman3,bman4,bman5,bman6,bman7;
	private Socket s;
	private Scanner in;
	private int xCoordinate=25, yCoordinate=0;
	private walls bricks=new walls();
	private walls bricksArray[][] = new walls[7][12];
	
	public GSService (Socket aSocket) {
		this.s = aSocket;
	}
	
	public void arrayToLongMap() {
		 long offset = 1;
		 for(int i = map[0].length - 1; i >= 0; i--) {
		        map1 += map[0][i]*offset;
		        offset *= 10;
		   }
		offset = 1;
		 for(int i = map[1].length - 1; i >= 0; i--) {
		        map2 += map[1][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[2].length - 1; i >= 0; i--) {
		        map3 += map[2][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[3].length - 1; i >= 0; i--) {
		        map4 += map[3][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[4].length - 1; i >= 0; i--) {
		        map5 += map[4][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[5].length - 1; i >= 0; i--) {
		        map6 += map[5][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[6].length - 1; i >= 0; i--) {
		        map7 += map[6][i]*offset;
		        offset *= 10;
		   }
	}
	
	public void arrayToLongBomber() {
		 long offset = 1;
		 bomber1=0;
		 bomber2=0;
		 bomber3=0;
		 bomber4=0;
		 bomber5=0;
		 bomber6=0;
		 bomber7=0;
		 
		 for(int i = map[0].length - 1; i >= 0; i--) {
		        bomber1 += bombermanPosition[0][i]*offset;
		        offset *= 10;
		   }
		offset = 1;
		 for(int i = map[1].length - 1; i >= 0; i--) {
		        bomber2 += bombermanPosition[1][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[2].length - 1; i >= 0; i--) {
		        bomber3 += bombermanPosition[2][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[3].length - 1; i >= 0; i--) {
		        bomber4 += bombermanPosition[3][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[4].length - 1; i >= 0; i--) {
		        bomber5 += bombermanPosition[4][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[5].length - 1; i >= 0; i--) {
		        bomber6 += bombermanPosition[5][i]*offset;
		        offset *= 10;
		   }
		 offset = 1;
		 for(int i = map[6].length - 1; i >= 0; i--) {
		        bomber7 += bombermanPosition[6][i]*offset;
		        offset *= 10;
		   }
	}
	
	////////////////////////////////////////////////////////////main ////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void run() {
		
		enemy[0] = new enemy(true,0);
		enemy[1] = new enemy(true,1);
		enemy[2] = new enemy(false,2);
		enemy[3] = new enemy(false,3);
		
		enemy[0].setCoordinates(125, 300);
		enemy[1].setCoordinates(425, 200);
		enemy[2].setCoordinates(625, 500);
		enemy[3].setCoordinates(1025, 300);
		
		arrayToLongBomber();
		arrayToLongMap();
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
									
				/*
				 * String sql = "DROP TABLE MAPS"; stmt.executeUpdate(sql); conn.commit();
				 */
				
				String sql = "CREATE TABLE IF NOT EXISTS MAPS " +
				             "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
						     " MAP1 BIGINT NOT NULL, " + " MAP2 BIGINT NOT NULL, " + " MAP3 BIGINT NOT NULL, " + " MAP4 BIGINT NOT NULL, " + 
						     " MAP5 BIGINT NOT NULL, " + " MAP6 BIGINT NOT NULL, " + " MAP7 BIGINT NOT NULL) ";
				
				stmt.executeUpdate(sql);
				conn.commit();
				System.out.println("Table MAP Created Successfully");
				
				sql ="SELECT * FROM MAPS"; 
                ResultSet rs = stmt.executeQuery(sql);
                
                if (rs.next() == false) {
                	sql = "INSERT INTO MAPS (MAP1, MAP2, MAP3, MAP4, MAP5, MAP6, MAP7) VALUES " + 
                            "("+ map1+","+ map2+","+ map3+","+ map4+" ,"+ map5+","+ map6+","+ map7+")";
    				stmt.executeUpdate(sql);
    				conn.commit();
    			}
                
				rs.close();
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
									
			
				//sql = "DROP TABLE BPOSITION"; stmt.executeUpdate(sql); conn.commit();
				
				
				String sql = "CREATE TABLE IF NOT EXISTS BPOSITION " +
				             "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
						     " BPO1 BIGINT NOT NULL, " + " BPO2 BIGINT NOT NULL, " + " BPO3 BIGINT NOT NULL, " + " BPO4 BIGINT NOT NULL, " + 
						     " BPO5 BIGINT NOT NULL, " + " BPO6 BIGINT NOT NULL, " + " BPO7 BIGINT NOT NULL) ";
				
				stmt.executeUpdate(sql);
				conn.commit();
				System.out.println("Table BPO Created Successfully");
				
				sql ="SELECT * FROM BPOSITION"; 
                ResultSet rs = stmt.executeQuery(sql);
                
                if (rs.next() == false) {
                	sql = "INSERT INTO BPOSITION (BPO1, BPO2, BPO3, BPO4, BPO5, BPO6, BPO7) VALUES " + 
                            "("+ bomber1+","+ bomber2+","+ bomber3+","+ bomber4+" ,"+ bomber5+","+ bomber6+","+ bomber7+")";
    				stmt.executeUpdate(sql);
    				conn.commit();
    				System.out.println("bpositions Created Successfully");
    			} 
                
  				rs.close();
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
									
				/*
				 * String sql = "DROP TABLE MAPS"; stmt.executeUpdate(sql); conn.commit();
				 */
				
				String sql = "CREATE TABLE IF NOT EXISTS BOMBER " +
				             "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
						     " X INT NOT NULL, " + " Y INT NOT NULL) ";
				
				stmt.executeUpdate(sql);
				conn.commit();
				System.out.println("Table BOMBER Created Successfully");
				
				sql ="SELECT * FROM BOMBER"; 
                ResultSet rs = stmt.executeQuery(sql);
                
                if (rs.next() == false) {
                	sql = "INSERT INTO BOMBER (X, Y) VALUES " + 
                            "("+ xCoordinate+","+ yCoordinate+")";
    				stmt.executeUpdate(sql);
    				conn.commit();
    				System.out.println("coordinates Created Successfully");
                }
                
				rs.close();
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		try {
			in = new Scanner(s.getInputStream());
			processRequest( );
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//processing the requests
	public void processRequest () throws IOException {
		//if next request is empty then return
		while(true) {
			if(!in.hasNext( )){
				return;
			}
			String command = in.next();
			if (command.equals("Quit")) {
				return;
			} else {
				executeCommand(command);
			}
		}
	}
	
	///////////////////////////////////////////////COMMANDS ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void executeCommand(String command) throws IOException{
		
		bomberman = new bomber();
		
		for (int i=0; i< 7 ; i++) {
			
			for (int j=0; j< 12 ; j++) {
				if( map[i][j]==1) {
					bricksArray[i][j] = new walls();
					}else if( map[i][j]==2) {
						bricksArray[i][j] = new walls();
					} else {
							bricksArray[i][j] = new walls();
						}
				if( j==0 ) {
					bricks.setX(bricks.getX());
					bricksArray[i][j].setCoordinates(bricks.getX(), bricks.getY());
				}else {
					bricks.setX(bricks.getX()+100);
					bricksArray[i][j].setCoordinates(bricks.getX(), bricks.getY());
					}
			}
			bricks.setX(0);
			bricks.setY(bricks.getY()+100);
		}
		//send a response
		Socket s2 = new Socket("localhost", CLIENT_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s2.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		
		if ( command.equals("PLAYER")) {
			int playerNo = in.nextInt();
			String playerAction = in.next();
			System.out.println("Player "+playerNo+" "+playerAction);
		
			if (playerAction.equals("UP")) {
				retrieveBPosition ();
				inToArray();
				retrieveCoordinates();
				movingUp();
				updateCoordinates();
				String commandOut = "PLAYER "+playerAction+" "+String.valueOf(bomberman.getX())+" "+ String.valueOf(bomberman.getY());
				System.out.println("Sending: " + commandOut);
				out.println(commandOut);
				out.flush();	
			
			}
			
			if (playerAction.equals("DOWN")) {
				retrieveBPosition ();
				inToArray();
				retrieveCoordinates();
				movingDown();
				updateCoordinates();
				String commandOut = "PLAYER "+playerAction+" "+String.valueOf(bomberman.getX())+" "+ String.valueOf(bomberman.getY());
				System.out.println("Sending: " + commandOut);
				out.println(commandOut);
				out.flush();	
			
			}
			
			if (playerAction.equals("LEFT")) {
				retrieveBPosition ();
				inToArray();
				retrieveCoordinates();
				movingLeft();
				updateCoordinates();
				
				String commandOut = "PLAYER "+playerAction+" "+String.valueOf(bomberman.getX())+" "+ String.valueOf(bomberman.getY());
				System.out.println("Sending: " + commandOut);
				out.println(commandOut);
				out.flush();	
			
			}
			
			if (playerAction.equals("RIGHT")) {
				retrieveBPosition ();
				inToArray();
				retrieveCoordinates();
				movingRight();
				updateCoordinates();
				
				String commandOut = "PLAYER "+playerAction+" "+String.valueOf(bomberman.getX())+" "+ String.valueOf(bomberman.getY());
				System.out.println("asdasdSending: " + commandOut);
				out.println(commandOut);
				out.flush();	
			
			}

			s2.close();

		}
		
		if ( command.equals("MOVE")) {
			int enemyNo = in.nextInt();
			
			retrieveCoordinates();
			System.out.println("asdasd"+bomberman.getX());
			
			enemy[0].setBomberman(bomberman);
			enemy[1].setBomberman(bomberman);
			enemy[2].setBomberman(bomberman);
			enemy[3].setBomberman(bomberman);
			
			if (!enemy[enemyNo].getMoving()) { //check and make enemies move
				//start moving
				enemy[enemyNo].moveEnemy();
			}
		}
		
		if ( command.equals("BOMB")) {
			
			retrieveCoordinates();
			
			int positionX=bomberman.getX();
			int positionY=bomberman.getY();
			
			String commandOut = "BOMB "+"0 " +String.valueOf(positionX)+" "+ String.valueOf(positionY);
			System.out.println("Sending BOMB: " + commandOut);
			out.println(commandOut);
			out.flush();	
			
			
			Timer timer = new Timer();
			
			TimerTask task = new TimerTask(){
				public void run() {
					
					String commandOut = "BOMB "+"0 " +String.valueOf(positionX)+" "+ String.valueOf(positionY);
					System.out.println("Sending BOMB: " + commandOut);
					out.println(commandOut);
					out.flush();
					
					for (int i=0; i< map.length ; i++) {
						for (int j=0; j< map[i].length ; j++) {
					
							if ((bricksArray[i][j].getX() == (positionX-25)) && (bricksArray[i][j].getY() == (positionY-100))
							&& map[i][j]!=2) {
								commandOut = "BOMB "+"1 " +String.valueOf(positionX)+" "+ String.valueOf(positionY-100);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								commandOut = "WALLS " +String.valueOf(i)+" "+ String.valueOf(j);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								retrieveMap();
								inToArrayMap();
								map[i][j]=3;
								arrayToLongMap();
								updateMap();
							}
							
							if ((bricksArray[i][j].getX() == (positionX-25)) && (bricksArray[i][j].getY() == (positionY+100))
									&& map[i][j]!=2) {
								commandOut = "BOMB "+"2 " +String.valueOf(positionX)+" "+ String.valueOf(positionY+100);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								commandOut = "WALLS " +String.valueOf(i)+" "+ String.valueOf(j);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								retrieveMap();
								inToArrayMap();
								map[i][j]=3;
								arrayToLongMap();
								updateMap();
							}
							
							if ((bricksArray[i][j].getX() == (positionX+75)) && (bricksArray[i][j].getY() == (positionY))
									&& map[i][j]!=2) {
								commandOut = "BOMB "+"3 " +String.valueOf(positionX+100)+" "+ String.valueOf(positionY);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								commandOut = "WALLS " +String.valueOf(i)+" "+ String.valueOf(j);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								retrieveMap();
								inToArrayMap();
								map[i][j]=3;
								arrayToLongMap();
								updateMap();
							}
							
							if ((bricksArray[i][j].getX() == (positionX-125)) && (bricksArray[i][j].getY() == (positionY))
									&& map[i][j]!=2) {
								commandOut = "BOMB "+"4 " +String.valueOf(positionX-100)+" "+ String.valueOf(positionY);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								commandOut = "WALLS " +String.valueOf(i)+" "+ String.valueOf(j);
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
								retrieveMap();
								inToArrayMap();
								map[i][j]=3;
								arrayToLongMap();
								updateMap();
							}
							
						}
					}
					
					for (int i=0; i< map.length ; i++) {
						for (int j=0; j< map[i].length ; j++) {
							retrieveCoordinates();
							if (  (  (bomberman.getX() == (positionX)) && (bomberman.getY() == (positionY)) )  ||
								  (  (bomberman.getX() == (positionX)) && (bomberman.getY() == (positionY-100)) )  ||
								  (  (bomberman.getX() == (positionX)) && (bomberman.getY() == (positionY+100)) )  ||
								  (  (bomberman.getX() == (positionX+75)) && (bomberman.getY() == (positionY)) )  ||
								  (  (bomberman.getX() == (positionX-125)) && (bomberman.getY() == (positionY)) )) {
								commandOut = "BOMBERMAND ";
								System.out.println("Sending BOMB: " + commandOut);
								out.println(commandOut);
								out.flush();
							}
						}
					}
					
				}
			};
			
			TimerTask task2 = new TimerTask(){
				public void run() {
					
					for (int i=0; i< 5 ; i++) {
						String commandOut = "BOMB "+i +" " +String.valueOf(-100)+" "+ String.valueOf(-100);
						System.out.println("Sending BOMB: " + commandOut);
						out.println(commandOut);
						out.flush();
					}
				}
			};	
			
			timer.schedule(task, 2000);
			timer.schedule(task2,4000);
		}
			
			
			
		///////////////////////////////////////////////////DATABASE/////////////////////////////////////////////////////////	
		
		
		if ( command.equals("NAME")) {
			
			String name = in.next();
			try {
				Class.forName("org.sqlite.JDBC");
				String dbURL = "jdbc:sqlite:product.db";
				conn = DriverManager.getConnection(dbURL);
				if (conn != null) {
					conn.setAutoCommit(false);
					stmt = conn.createStatement();
					/*
					 * String sql = "DROP TABLE PLAYERS"; stmt.executeUpdate(sql); conn.commit();
					 */
					String sql = "CREATE TABLE IF NOT EXISTS PLAYERS " +
					             "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
							     " NAME VARCHAR(255) NOT NULL) ";
					
					stmt.executeUpdate(sql);
					conn.commit();
					System.out.println("Table PLAYERS Created Successfully");
					
					sql = "INSERT INTO PLAYERS (NAME) VALUES " + 
                            "('"+ name+"')";
    				stmt.executeUpdate(sql);
    				conn.commit();
    				
	    			conn.close();
				}
				
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			String commandOut = "NAME " + name;
			out.println(commandOut);
			out.flush();	
		}
		
	}
	
	
	////////////////////////////////////////////////////////////////////UPDATE ///////////////////////////////////////
	public void updateBPosition () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
				
				String sql = "UPDATE BPOSITION SET BPO1 = "+bomber1+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BPOSITION SET BPO2 = "+bomber2+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BPOSITION SET BPO3 = "+bomber3+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BPOSITION SET BPO4 = "+bomber4+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BPOSITION SET BPO5 = "+bomber5+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BPOSITION SET BPO6 = "+bomber6+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BPOSITION SET BPO7 = "+bomber7+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
      			System.out.println("values updated Successfully");
                
      			System.out.println(bomber1);
  				System.out.println(bomber2);
  				System.out.println(bomber3);
  				System.out.println(bomber4);
  				System.out.println(bomber5);
  				System.out.println(bomber6);
  				System.out.println(bomber7);
      			
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void updateCoordinates () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
				
				String sql = "UPDATE BOMBER SET X = "+bomberman.getX()+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE BOMBER SET Y = "+bomberman.getY()+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				System.out.println("values updated Successfully");
                
  				System.out.println(bomberman.getX()+"asdasd");
  				System.out.println(bomberman.getY()+"asdasd");
  				
  				
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void updateMap () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
				
				String sql = "UPDATE MAPS SET MAP1 = "+map1+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE MAPS SET MAP2 = "+map2+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE MAPS SET MAP3 = "+map3+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE MAPS SET MAP4 = "+map4+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE MAPS SET MAP5 = "+map5+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE MAPS SET MAP6 = "+map6+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				sql = "UPDATE MAPS SET MAP7 = "+map7+" WHERE ID="+1+"";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	//////////////////////////////////////////////////////////RETRIEVE/////////////////////////////////////////////
	
	public void retrieveMap () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM MAPS WHERE ID="+1+"";
                ResultSet rs = stmt.executeQuery(sql);
				
  				while ( rs.next() ) {
					map1 = rs.getLong("MAP1");
					map2 = rs.getLong("MAP2");
					map3 = rs.getLong("MAP3");
					map4 = rs.getLong("MAP4");
					map5 = rs.getLong("MAP5");
					map6 = rs.getLong("MAP6");
					map7 = rs.getLong("MAP7");
				}
  				
  				rs.close();
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	

	public void retrieveBPosition () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM BPOSITION WHERE ID="+1+"";
                ResultSet rs = stmt.executeQuery(sql);
				
  				while ( rs.next() ) {
					bman1 = rs.getLong("BPO1");
					bman2 = rs.getLong("BPO2");
					bman3 = rs.getLong("BPO3");
					bman4 = rs.getLong("BPO4");
					bman5 = rs.getLong("BPO5");
					bman6 = rs.getLong("BPO6");
					bman7 = rs.getLong("BPO7");
				}
  				
  				
  				rs.close();
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void retrieveCoordinates () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM BOMBER WHERE ID="+1+"";
                ResultSet rs = stmt.executeQuery(sql);
				
  				while ( rs.next() ) {
					bomberman.setX(rs.getInt("X"));
					bomberman.setY(rs.getInt("Y"));
				}
  				
  				System.out.println(bomberman.getX());
  				System.out.println(bomberman.getY());
  				
  				rs.close();
                conn.close();
			}
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////ARRAYANDLONGS/////////////////////////////////////////////
	
	public void inToArrayMap() {
		
		int j= 11;
		while (map1>0){

			map[0][j]=(int)(map1%10);
			map[1][j]=(int)(map2%10);
			map[2][j]=(int)(map3%10);
			map[3][j]=(int)(map4%10);
			map[4][j]=(int)(map5%10);
			map[5][j]=(int)(map6%10);
			map[6][j]=(int)(map7%10);
			map1/=10;
			map2/=10;
			map3/=10;
			map4/=10;
			map5/=10;
			map6/=10;
			map7/=10;
			j--;
		}
	}

	public void inToArray() {
		
		int j= 11;
		while (bman1>0){

			bombermanPosition[0][j]=(int)(bman1%10);
			bombermanPosition[1][j]=(int)(bman2%10);
			bombermanPosition[2][j]=(int)(bman3%10);
			bombermanPosition[3][j]=(int)(bman4%10);
			bombermanPosition[4][j]=(int)(bman5%10);
			bombermanPosition[5][j]=(int)(bman6%10);
			bombermanPosition[6][j]=(int)(bman7%10);
			bman1/=10;
			bman2/=10;
			bman3/=10;
			bman4/=10;
			bman5/=10;
			bman6/=10;
			bman7/=10;
			j--;
		}
		
		
	}
	/////////////////////////////////////////////////////////////////CONTROLS///////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void movingDown() {
		
			
			loop :  // Loop to handle the position of bomberman and positions available in the map
			for (int i=0; i< map.length ; i++) {
				for (int j=0; j< map[i].length ; j++) {
					
					if(bombermanPosition[i][j]==1) { // check for current bomberman position
						
						retrieveMap();
						inToArrayMap();
						
						if(map[i+1][j]==3) {    // check if the next space is available
							
							bombermanPosition[i][j]=3;		//change current position to 0
							bombermanPosition[i+1][j]=1;	//the new bomberman position
							
							arrayToLongBomber();
							updateBPosition ();
							// inToArray();
							bomberman.setY(bomberman.getY()+GameProperties.CHARACTER_STEP);
							//bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
							break loop;// Once the position for bomberman has changed then finish loop to keep bomberman from moving further
						}
					}
				}
			}
		
	}
	
	public void movingUp() {
			
			loop :
				for (int i=0; i< map.length ; i++) {
					for (int j=0; j< map[i].length ; j++) {
					
						if(bombermanPosition[i][j]==1) {
							retrieveMap();
							inToArrayMap();
							if(map[i-1][j]==3) {
								bombermanPosition[i][j]=3;
								bombermanPosition[i-1][j]=1;
								
								arrayToLongBomber();
								updateBPosition ();
								
								bomberman.setY(bomberman.getY()-GameProperties.CHARACTER_STEP);
								//bombermanLabel.setLocation(bomberman.getX(), bomberman.getY());
								break loop;
							}
						}
					}
				}	
		
	}
	
	
	public void movingRight() {
			
			loop :
			for (int i=0; i< map.length ; i++) {
				for (int j=0; j< map[i].length ; j++) {
				
					if(bombermanPosition[i][j]==1) {
						retrieveMap();
						inToArrayMap();
						if(map[i][j+1]==3) {
							bombermanPosition[i][j]=3;
							bombermanPosition[i][j+1]=1;
							
							arrayToLongBomber();
							updateBPosition ();
							
							bomberman.setX(bomberman.getX()+GameProperties.CHARACTER_STEP);
							break loop;
						}
					}
				}
			}
	
	}
	
	public void movingLeft() {
			
			loop :
				for (int i=0; i< map.length ; i++) {
					for (int j=0; j< map[i].length ; j++) {
					
						if(bombermanPosition[i][j]==1) {
							retrieveMap();
							inToArrayMap();
							if(map[i][j-1]==3) {
								bombermanPosition[i][j]=3;
								bombermanPosition[i][j-1]=1;
								
								arrayToLongBomber();
								updateBPosition ();
								
								bomberman.setX(bomberman.getX()-GameProperties.CHARACTER_STEP);
								break loop;
							}
						}
					}
				}
	}
}

