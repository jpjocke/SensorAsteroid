package sa.model;

import android.util.Log;

public class Rocket extends MoveAbleObject{
	private static final String TAG = "Rocket";
	private static final float FRICTION = 0.98f;
	private static final float MAX_ROT = 12f;
	private static final float ROT_FILTER = 90;
	private static final float NORMALIZE = 0;
	private static final float MAX_SPEED = 8;
	private float [][] drawPos;
	private SensorInterface si;
	private float wantedAngle, angle;

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
		applyRotation();
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
		drawPos[0][0] = pX + (float)(Math.cos(Math.toRadians(getRocketAngle() - NORMALIZE)) * (radius));
		drawPos[0][1] = pY + (float)(Math.sin(Math.toRadians(getRocketAngle() - NORMALIZE)) * (radius));

		drawPos[1][0] = pX + (float)(Math.cos(Math.toRadians(getRocketAngle() + 135 - NORMALIZE)) * (radius));
		drawPos[1][1] = pY + (float)(Math.sin(Math.toRadians(getRocketAngle() + 135 - NORMALIZE)) * (radius));

		drawPos[2][0] = pX + (float)(Math.cos(Math.toRadians(getRocketAngle() + 180 - NORMALIZE)) * (radius / 2));
		drawPos[2][1] = pY + (float)(Math.sin(Math.toRadians(getRocketAngle() + 180 - NORMALIZE)) * (radius / 2));

		drawPos[3][0] = pX + (float)(Math.cos(Math.toRadians(getRocketAngle() + 225 - NORMALIZE)) * (radius));
		drawPos[3][1] = pY + (float)(Math.sin(Math.toRadians(getRocketAngle() + 225 - NORMALIZE)) * (radius));
	}

	public float getRocketAngle(){
		return angle;
		//return si.getAngle() - NORMALIZE;
	}

	private void applyRotation(){
		wantedAngle = si.getAngle() - NORMALIZE;
		//DecimalFormat mF = new DecimalFormat("###.##");
		float dif = Math.abs(wantedAngle - getRocketAngle());
		if(dif > ROT_FILTER){
			angle = wantedAngle;
			//Log.d(TAG, "wanted: " + mF.format(wantedAngle) + " angle: " + mF.format(angle) + " dif: " + mF.format(dif) + " ROTATE SET");
		}
		else{
			if((angle < wantedAngle && wantedAngle < angle + 180) || (angle - 360 < wantedAngle && wantedAngle < angle + 180 - 360)){
				//if((angle > wantedAngle && wantedAngle < angle + 180) || wantedAngle < angle - 180 ){
				if(dif > MAX_ROT){
					angle += MAX_ROT;
					//Log.d(TAG, "wanted: " + mF.format(wantedAngle) + " angle: " + mF.format(angle) + " dif: " + mF.format(dif) + " ROTATE MAX +");
				}
				else{
					angle += dif;
					//Log.d(TAG, "wanted: " + mF.format(wantedAngle) + " angle: " + mF.format(angle) + " dif: " + mF.format(dif) + " ROTATE DIF +");
				}
			}
			else{
				if(dif > MAX_ROT){
					angle -= MAX_ROT;
					//Log.d(TAG, "wanted: " + mF.format(wantedAngle) + " angle: " + mF.format(angle) + " dif: " + mF.format(dif) + " ROTATE MAX -");
				}
				else{
					angle -= dif;
					//Log.d(TAG, "wanted: " + mF.format(wantedAngle) + " angle: " + mF.format(angle) + " dif: " + mF.format(dif) + " ROTATE DIF -");
				}
			}
		}
		if(angle > 360)
			angle -= 360;
		if(angle < 0)
			angle += 360;
		if(vY > MAX_SPEED)
			vY = MAX_SPEED;
		else if(vY < -MAX_SPEED)
			vY = -MAX_SPEED;
		//Log.d(TAG, "angle: " + angle);
	}
}
