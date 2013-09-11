package sa.model;

public class Rocket extends MoveAbleObject{
	private static final String TAG = "Rocket";
	private static final float FRICTION = 0.98f;
	private static final float MAX_ROT = 20f;
	private static final float ROT_FILTER = 90;
	private static final float NORMALIZE = 0;
	private static final float MAX_SPEED = 8;
	private float [][] drawPos;
	private SensorInterface si;
	
	public Rocket(int areaX, int areaY, SensorInterface si){
		super(areaX, areaY, 15);
		pX = aX / 2;
		pY = aY / 2;
		drawPos = new float[4][2];
		this.si = si;
	}
	
	@Override
	public void updatePos(){
		applyVelocity();
		super.updatePos();
		calcDrawPos();
	}
	
	private void applyVelocity(){
		vX += si.getVelocityX();
		vX *= FRICTION;
		if(vX > MAX_SPEED)
			vX = MAX_SPEED;
		else if(vX < -MAX_SPEED)
			vX = -MAX_SPEED;
		vY += si.getVelocityY();
		vY *= FRICTION;
		if(vY > MAX_SPEED)
			vY = MAX_SPEED;
		else if(vY < -MAX_SPEED)
			vY = -MAX_SPEED;
	}
	
	public float[][] getDrawPos(){
		return drawPos;
	}
	
	private void calcDrawPos(){
		drawPos[0][0] = pX + (float)(Math.cos(Math.toRadians(si.getAngle() - NORMALIZE)) * (radius));
		drawPos[0][1] = pY + (float)(Math.sin(Math.toRadians(si.getAngle() - NORMALIZE)) * (radius));

		drawPos[1][0] = pX + (float)(Math.cos(Math.toRadians(si.getAngle() + 135 - NORMALIZE)) * (radius));
		drawPos[1][1] = pY + (float)(Math.sin(Math.toRadians(si.getAngle() + 135 - NORMALIZE)) * (radius));
		
		drawPos[2][0] = pX + (float)(Math.cos(Math.toRadians(si.getAngle() + 180 - NORMALIZE)) * (radius / 2));
		drawPos[2][1] = pY + (float)(Math.sin(Math.toRadians(si.getAngle() + 180 - NORMALIZE)) * (radius / 2));
		
		drawPos[3][0] = pX + (float)(Math.cos(Math.toRadians(si.getAngle() + 225 - NORMALIZE)) * (radius));
		drawPos[3][1] = pY + (float)(Math.sin(Math.toRadians(si.getAngle() + 225 - NORMALIZE)) * (radius));
	}

    public float getRocketAngle(){
    	return si.getAngle() - NORMALIZE;
    }
}
