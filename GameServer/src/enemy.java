import java.sql.DriverManager;
import java.sql.ResultSet;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class enemy extends Sprite implements Runnable{
	
	private Boolean moving, visible, enemyAlive, bombermanAlive,horizontal, direction; 
	private Thread t;
	private JLabel enemyLabel, bombermanLabel;
	private int limit = 0,flag1=0,flag2=0;
	private JButton animationButton;
	private bomber bomberman;
	private bomb bomb,bomb2, bomb_ex_right, bomb_ex_left, bomb_ex_up, bomb_ex_down;
	private Connection conn = null;
	private Statement stmt = null;
	private final int CLIENT_PORT = 5656;
	private int number=0;
	
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
	public void setFlag2 (int temp) {this.flag2=temp;}
	public void setBomb(bomb temp) {this.bomb= temp;}
	public void setBomb2(bomb temp) {this.bomb2= temp;}
	public void setVisible(Boolean visible) {this.visible = visible;}
	public void setEnemyAlive(Boolean temp) {this.enemyAlive=temp;}
	public void setBombermanAlive(Boolean temp) {this.bombermanAlive=temp;}
	
	public void setBombEx(bomb temp, bomb temp2, bomb temp3, bomb temp4) {
		this.bomb_ex_right= temp;
		this.bomb_ex_left= temp2;
		this.bomb_ex_up= temp3;
		this.bomb_ex_down= temp4;
	}
	
	public void setEnemyLabel(JLabel temp) {this.enemyLabel = temp;}
	public void setBombermanLabel(JLabel temp) {this.bombermanLabel = temp;}
	public void setAnimationButton(JButton temp) {this.animationButton = temp;}
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
		   this.enemyLabel= temp;
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
			
			//System.out.println(bomberman.getX()+" "+bomberman.getY());
			
			if (enemyAlive== true) {
				detectBombermanCollision();
		//		detectBombCollision();
		//		detectBombExplosion();
		//		gameEnd();
			}
			
			try {
				Thread.sleep(200);
			} catch(Exception e) { 
				
			}
		}}
		catch (Exception E)
		{
			E.printStackTrace();
		}
		
	}
	
	private void detectBombermanCollision() {
		if(this.r.intersects(bomberman.getRectangle())) {
			this.bombermanAlive =false;
			System.out.println("dead");
			//animationButton.setText("Re-start");
			//bombermanLabel.setIcon( new ImageIcon( getClass().getResource("smallninja2.png")));
			
		}
	}
	
	private void gameEnd() {
		if(this.bombermanAlive == false) {
			if (this.flag1==0) {
				JOptionPane.showMessageDialog(null, "Player 1 died!", "Ooops!", JOptionPane.INFORMATION_MESSAGE);
				//displayAllScores();
				this.flag1=1;
			}
			
			this.bombermanAlive = true;
			//this.bombermanLabel.setVisible(false);
		}
		
	}
	
	// detect bomb and change direction of enemy so they do not share same space
	private void detectBombCollision() {
		if(this.r.intersects(bomb.getRectangle())||this.r.intersects(bomb2.getRectangle())) {
			direction = !direction;
		}
	}
	
	//detect if explosion reaches enemy
	private void detectBombExplosion() {
		if(this.r.intersects(bomb_ex_right.getRectangle()) || this.r.intersects(bomb_ex_left.getRectangle()) ||
			this.r.intersects(bomb_ex_down.getRectangle()) || this.r.intersects(bomb_ex_up.getRectangle())) {
			this.moving=false;
			this.enemyAlive =false;
			//enemyLabel.setIcon( new ImageIcon( getClass().getResource("enemy2.png")));
			
		}
		
	}
	
	private void displayAllScores() {
		
		String[] id_array = new String[1] ;
		String[] name_array = new String[1];
		String[] score_array = new String[1];
		
		int counter= 0;
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbURL = "jdbc:sqlite:product.db";
			conn = DriverManager.getConnection(dbURL);
			
			if (conn != null) {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				String sql ="SELECT * FROM SCORES ORDER BY SCORE DESC"; 
                ResultSet rs = stmt.executeQuery(sql);
				while ( rs.next() ) {
					counter=counter+1;
				}
				rs.close();
			}
			
			if (conn != null) {
				System.out.println("Connected to database");
				conn.setAutoCommit(false);
				
				stmt = conn.createStatement();
				
				String sql ="SELECT * FROM SCORES ORDER BY SCORE DESC"; 
                
				ResultSet rs = stmt.executeQuery(sql);
				
				id_array = new String [counter];
				name_array = new String [counter];
				score_array = new String [counter];
				
				counter = 0;
				
				while ( rs.next() ) {
					
					int id = rs.getInt("id");
					String name = rs.getString("name");
					int score = rs.getInt("score");
					id_array[counter] = String.valueOf(id);
					name_array[counter] = name;
					score_array[counter] = String.valueOf(score);
					counter=counter+1;
				}
				
				rs.close();
				conn.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder(64);
		
		String[] record = new String[5];
		
		if(counter>5) {counter=5;}
		
		for (int i=0; i<counter; i++) {
		
			record[i] = "<td>" + String.valueOf(id_array[i]) + "</td><td>"+ String.valueOf(name_array[i]) + "</td><td>" + String.valueOf(score_array[i]) + "</td>";
		
		}
		
		sb.append("<html><table><tr><td>Player</td><td>Name</td><td>Score</td></tr>");
	    
	    for (int i=0; i<5; i++) {
	    	sb.append("<tr>").append(record[i]).append("</tr>");
	    }
	    
	    sb.append("</table></html>");
	    
		JOptionPane.showMessageDialog(null, sb, "Top Scores", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	
}
