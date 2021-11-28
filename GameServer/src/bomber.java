public class bomber extends Sprite{
	
	public bomber() {
		   super(75,100,"smallninja.png");
		   this.visible=false;
	}
	
	private Boolean visible;
	
	public Boolean getVisible() {return visible;}
	public void setVisible(Boolean visible) {this.visible = visible;}
	
	public void hide() { this.visible= false; }
	public void show() { this.visible= true; }
}
