package sa.model;

import java.text.DecimalFormat;

import android.util.Log;

public class Rocket extends MoveAbleObject{
	private static final String TAG = "Rocket";
	private static final float FRICTION = 0.98f;
	private static final float MAX_ROT = 20f;
	private static final float ROT_FILTER = 90;
	private static final float NORMALIZE = 270;
	private float [][] drawPos;
	private float angle, wantedAngle;
	// 0 = y
	// 1 = x
	// 2 = z (not used)
	private float[] gravityData;
	
	public Rocket(int areaX, int areaY){
		super(areaX, areaY, 15);
		pX = aX / 2;
		pY = aY / 2;
		gravityData = new float[3];
		drawPos = new float[4][2];
	}
	
	@Override
	public void updatePos(){
		applyVelocity();
		applyRotation();
		super.updatePos();
		calcDrawPos();
	}
	
	private void applyVelocity(){
		vX += gravityData[1] / 15;
		vX *= FRICTION;
		vY += gravityData[0] / 15;
		vY *= FRICTION;
	}
	
	private void applyRotation(){
		wantedAngle = calcGravityAngle();
		DecimalFormat mF = new DecimalFormat("###.##");
		float dif = Math.abs(wantedAngle - angle);
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
	}
	
	public float[][] getDrawPos(){
		return drawPos;
	}
	
	private void calcDrawPos(){
		drawPos[0][0] = pX + (float)(Math.cos(Math.toRadians(angle - NORMALIZE)) * (radius));
		drawPos[0][1] = pY + (float)(Math.sin(Math.toRadians(angle - NORMALIZE)) * (radius));

		drawPos[1][0] = pX + (float)(Math.cos(Math.toRadians(angle + 135 - NORMALIZE)) * (radius));
		drawPos[1][1] = pY + (float)(Math.sin(Math.toRadians(angle + 135 - NORMALIZE)) * (radius));
		
		drawPos[2][0] = pX + (float)(Math.cos(Math.toRadians(angle + 180 - NORMALIZE)) * (radius / 2));
		drawPos[2][1] = pY + (float)(Math.sin(Math.toRadians(angle + 180 - NORMALIZE)) * (radius / 2));
		
		drawPos[3][0] = pX + (float)(Math.cos(Math.toRadians(angle + 225 - NORMALIZE)) * (radius));
		drawPos[3][1] = pY + (float)(Math.sin(Math.toRadians(angle + 225 - NORMALIZE)) * (radius));
	}

	/** 
     * Calculates the angle for the rocket based on gravityData
     * @param x X value 
     * @param y Y value 
     * @return Degree of the angle 
     */
    private  float calcGravityAngle(){ 
        float rv = 0f; 
        if(gravityData[1] == 0){ 
            if(gravityData[0] > 0) 
                rv = 90; 
            else if(gravityData[0]  < 0) 
                rv = 270; 
            else
                rv = 0; 
            } 
        else{ 
            rv = (float)Math.toDegrees(Math.atan(gravityData[0] /gravityData[1])); 
            //atan only returns - pi/2 to pi/2 
            if(gravityData[1] < 0) 
                rv -= 180; 
        } 
        //Log.d(TAG, "angle: " + rv);
        return rv + NORMALIZE; //normalize so angles are between 0 - 360
    } 
    
    public float getRocketAngle(){
    	return angle - NORMALIZE;
    }
    
    public void setGravityData(float[] gravity){
    	for(int i = 0; i < gravityData.length; i++)
    		gravityData[i] = gravity[i]; 
    }
}
