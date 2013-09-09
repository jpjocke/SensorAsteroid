package sa.model;

public class Asteroid extends MoveAbleObject{
	private int level = 4;
	
	public Asteroid(int areaX, int areaY, int level){
		super(areaX, areaY, level * 5);
		this.level = level;
	}

	public int getLevel(){ return level;}
}
