import java.sql.DriverManager;
import java.sql.ResultSet;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class enemy extends Sprite implements Runnable{
	
	private Boolean moving, visible, enemyAlive, bombermanAlive,horizontal, direction; 
	private Thread t;
	private int limit = 0,flag1=0,flag2=0,flag3=0,flag4=0;
	private String name;
	private bomber bomberman;
	private bomb bomb,bomb_ex_right, bomb_ex_left, bomb_ex_up, bomb_ex_down;
	private Connection conn = null;
	private Statement stmt = null;
	private final int CLIENT_PORT = 5656;
	private int number=0,score=0;
	
	
	public Boolean getMoving() {return moving;}
	public Boolean getEnemyAlive() {return enemyAlive;} 
	public int getLimit() {return limit;}
	public int getFlag1() {return flag1;}
	public int getFlag2() {return flag2;}
	public Boolean getBombermanAlive() {return bombermanAlive;}
	public Boolean getVisible() {return visible;}
	
	public void setMoving(Boolean moving) {	this.moving = moving;}
	
	//Work with bomb, bomb explosion and bomberman features
	public void setBomberman (bomber temp) {this.bomberman=temp;}
	public void setFlag1 (int temp) {this.flag1=temp;}
	public void setBomb(bomb temp) {this.bomb= temp;}
	public void setVisible(Boolean visible) {this.visible = visible;}
	public void setEnemyAlive(Boolean temp) {this.enemyAlive=temp;}
	public void setBombermanAlive(Boolean temp) {this.bombermanAlive=temp;}
	
	public void setBombEx(bomb temp, bomb temp2, bomb temp3, bomb temp4) {
		this.bomb_ex_right= temp;
		this.bomb_ex_left= temp2;
		this.bomb_ex_up= temp3;
		this.bomb_ex_down= temp4;
	}
	
	public void setLimit(int temp) {this.limit = temp;}
	
	public void hide() { this.visible= false; }
	public void show() { this.visible= true; }
	
	public enemy() {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.visible=false;
		   this.horizontal=true;
		   this.bombermanAlive=true;
	}
	
	public enemy(Boolean horizontal) {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.visible=false;
		   this.horizontal=horizontal;
		   this.bombermanAlive=true;
	}
	
	public enemy(Boolean horizontal, int number) {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.visible=false;
		   this.horizontal=horizontal;
		   this.bombermanAlive=true;
		   this.number=number;
	}
	
	public enemy(JLabel temp) {
		   super(75,100,"enemy.png");
		   this.moving=false;
		   this.direction=true;
		   this.enemyAlive=true;
		   this.horizontal=true;
		   this.visible=false;
		   this.bombermanAlive=true;
	}
	
	
	public void moveEnemy() {
		t = new Thread(this,"Enemy Thread");
		t.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
		Socket s3 = new Socket("localhost", CLIENT_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s3.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		this.moving = true;
		
		while(moving) {
			//movement routine
			
			//get current X/Y
			int tx= this.x;
			int ty = this.y;
			
			if(horizontal) {
			//make enemy move horizontally
				if (direction) {
					tx += 20;
					limit += 20;
					if(limit>=100) {
						direction=false;
					}
				} else {
					tx -= 20;
					limit -= 20;
					if(limit<=-100) {
						direction=true;
					}
				}
				//make enemy move vertically	
			}else {
				if (direction) {
					ty += 20;
					limit += 20;
					if(limit>=100) {
						direction=false;
					}
				} else {
					ty -= 20;
					limit -= 20;
					if(limit<=-100) {
						direction=true;
					}
				}
			}
			
			this.setX(tx);
			this.setY(ty);
			
			String commandOut= "ENEMY"+ " "+ number + " "+ this.x + " " + this.y;
			out.println(commandOut);
			out.flush();
			
			retrieveCoordinates();
			retrieveBombPosition();
			
			if (enemyAlive== true) {
				detectBombermanCollision();
				detectBombCollision();
				detectBombExplosion();
		//		gameEnd();
			}
			
			try {
				Thread.sleep(200);
			} catch(Exception e) { 
				
			}
		}
		}
		catch (Exception E)
		{
			E.printStackTrace();
		}
		
	}
	
	
	/////////////////////////////////////////////////////DATABASE//////////////////////////////////////////////////////
	
	public void updateEnemyAlive1 () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				
				String sql = "UPDATE ENEMY SET ALIVE = "+1+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				System.out.println("enemy1 has died");
  				
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
	
	public void updateEnemyAlive2 () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				
				String sql = "UPDATE ENEMY SET ALIVE2 = "+1+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				System.out.println("enemy2 has died");
  				
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
	
	public void updateEnemyAlive3 () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				
				String sql = "UPDATE ENEMY SET ALIVE3 = "+1+" WHERE ID='"+1+"'";
				stmt.executeUpdate(sql);
  				conn.commit();
  				
  				System.out.println("enemy3 has died");
  				
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
	
	public void updateEnemyAlive4 () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				
				String sql = "UPDATE ENEMY SET ALIVE4 = "+1+" WHERE ID='"+1+"'";
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
	
	public void retrieveBombPosition() {
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM BOMBPOSITION WHERE ID="+1+"";
                ResultSet rs = stmt.executeQuery(sql);
				
  				while ( rs.next() ) {
					bomb.setX(rs.getInt("BOMBX")) ;
					bomb.setY(rs.getInt("BOMBY")) ;
					bomb_ex_right.setX(rs.getInt("BOMB2X")) ;
					bomb_ex_right.setY(rs.getInt("BOMB2Y")) ;
					bomb_ex_left.setX(rs.getInt("BOMB3X")) ;
					bomb_ex_left.setY(rs.getInt("BOMB3Y")) ;
					bomb_ex_up.setX(rs.getInt("BOMB4X")) ;
					bomb_ex_up.setY(rs.getInt("BOMB4Y")) ;
					bomb_ex_down.setX(rs.getInt("BOMB5X")) ;
					bomb_ex_down.setY(rs.getInt("BOMB5Y")) ;
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

	private void detectBombermanCollision() throws IOException{
		
		
			Socket s4 = new Socket("localhost", CLIENT_PORT);
			
			//Initialize data stream to send data out
			OutputStream outstream = s4.getOutputStream();
			PrintWriter out = new PrintWriter(outstream);
			
		if(this.r.intersects(bomberman.getRectangle())) {
			this.bombermanAlive =false;
			System.out.println("dead");
			//animationButton.setText("Re-start");
			if (flag1==0) {
			retrieveNameScores();
			String commandOut= "BOMBERMAND "+name+" "+score;
			out.println(commandOut);
			out.flush();
			flag1=1;
			}
		}
		
	}
	
	private void gameEnd() {
		if(this.bombermanAlive == false) {
			if (this.flag1==0) {
				JOptionPane.showMessageDialog(null, "Player died!", "Ooops!", JOptionPane.INFORMATION_MESSAGE);
				//displayAllScores();
				this.flag1=1;
			}
			
			this.bombermanAlive = true;
			//this.bombermanLabel.setVisible(false);
		}
		
	}
	
	// detect bomb and change direction of enemy so they do not share same space
	private void detectBombCollision() {
		if(this.r.intersects(bomb.getRectangle())) {
			direction = !direction;
		}
	}
	
	//detect if explosion reaches enemy
	private void detectBombExplosion() throws IOException{
		
		Socket s4 = new Socket("localhost", CLIENT_PORT);
		
		//Initialize data stream to send data out
		OutputStream outstream = s4.getOutputStream();
		PrintWriter out = new PrintWriter(outstream);
		
		if(this.r.intersects(bomb_ex_right.getRectangle()) || this.r.intersects(bomb_ex_left.getRectangle()) ||
			this.r.intersects(bomb_ex_down.getRectangle()) || this.r.intersects(bomb_ex_up.getRectangle())) {
			this.moving=false;
			this.enemyAlive =false;
			
			if (number==0) {
				updateEnemyAlive1();
			}else if (number==1) {
				updateEnemyAlive2();
			}else if (number==2) {
				updateEnemyAlive3();
			}else if (number==3) {
				updateEnemyAlive4();
			}
			
			String commandOut= "BOMBENEMY"+ " "+ number;
			out.println(commandOut);
			out.flush();
			//enemyLabel.setIcon( new ImageIcon( getClass().getResource("enemy2.png")));
			
		}
		
	}
	
	public void retrieveNameScores () {
		
		try {
			Class.forName("org.sqlite.JDBC");
			
			
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM SCORES WHERE ID="+1+"";
                ResultSet rs = stmt.executeQuery(sql);
				
  				while ( rs.next() ) {
					name = rs.getString("NAME");
					score = rs.getInt("SCORE");
				}
  				
  				System.out.println(name);
  				
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
	
}
