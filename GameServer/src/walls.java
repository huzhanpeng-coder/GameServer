
public class walls extends Sprite{

	public walls() {
		   super(100,100,"walls.png");
	 }
	
	private Boolean visible;
	
	public Boolean getVisible() {return visible;}
	public void setVisible(Boolean visible) {this.visible = visible;}
	
	public void hide() { this.visible= false; }
	public void show() { this.visible= true; }
}