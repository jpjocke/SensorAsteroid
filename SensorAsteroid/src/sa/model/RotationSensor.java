package sa.model;

import android.util.Log;

public class RotationSensor implements SensorInterface{
	private static final String TAG = "RotationSensor";
	private static final float ROT_DIVIDER = 75f;
	//0 = not used, 1 = x, 2 = y
	private float[] rotData = new float[3];
	private float angle;

	@Override
	public float getAngle() {
		angle = 0f; 
        if(-Math.toDegrees(rotData[1]) == 0){ 
            if(-Math.toDegrees(rotData[2]) > 0) 
            	angle = 90; 
            else if(-Math.toDegrees(rotData[2]) < 0) 
            	angle = 270; 
            else
            	angle = 0; 
            } 
        else{ 
        	angle = (float)Math.toDegrees(Math.atan(-Math.toDegrees(rotData[2])/-Math.toDegrees(rotData[1]))); 
            //atan only returns - pi/2 to pi/2 
            if(-Math.toDegrees(rotData[1]) < 0) 
            	angle -= 180; 
        } 
        //Log.d(TAG, "angle: " + angle);
        return angle; 
	}

	@Override
	public float getVelocityX() {
    	//float vVector = (float)(Math.abs(Math.toDegrees(rotData[1])) + Math.toDegrees(rotData[2])) / 15f;
		//return (float)Math.cos(Math.toRadians(angle)) * vVector;
		return -(float)Math.toDegrees(rotData[1]) / ROT_DIVIDER;
	}

	@Override
	public float getVelocityY() {
    	//float vVector = (float)(Math.abs(Math.toDegrees(rotData[1])) + Math.toDegrees(rotData[2])) / 15f;
		//return (float)Math.sin(Math.toRadians(angle)) * vVector;
		return -(float)Math.toDegrees(rotData[2]) / ROT_DIVIDER;
	}

	@Override
	public void setData(float[] data) {
    	for(int i = 0; i < rotData.length; i++)
    		rotData[i] = data[i]; 
    	//Log.d(TAG, "data set");
	}
}
