package sa.model;

public class Shot extends MoveAbleObject{
	public static final float SPEED = 10f;
	public static final float MAX_DISTANCE = 400;
	private float distance;
	

	public Shot(int areaX, int areaY, float radius) {
		super(areaX, areaY, radius);
	}
	
	@Override
	public void updatePos(){
		distance += Math.abs(vX) + Math.abs(vY);
		super.updatePos();
	}

	public boolean isDead(){
		if(distance > MAX_DISTANCE)
			return true;
		return false;
	}
}
