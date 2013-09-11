package sa.model;

public class GravitySensor implements SensorInterface{
	private static final float GRAV_DIVIDER = 9f;
	// 0 = y
	// 1 = x
	// 2 = z (not used)
	private float[] gravData = new float[3];

	@Override
	public float getAngle() {
		float rv = 0f; 
        if(gravData[1] == 0){ 
            if(gravData[0] > 0) 
                rv = 90; 
            else if(gravData[0]  < 0) 
                rv = 270; 
            else
                rv = 0; 
            } 
        else{ 
            rv = (float)Math.toDegrees(Math.atan(gravData[0] /gravData[1])); 
            //atan only returns - pi/2 to pi/2 
            if(gravData[1] < 0) 
                rv -= 180; 
        } 
        //Log.d(TAG, "angle: " + rv);
        return rv;
	}

	@Override
	public float getVelocityX() {
		return gravData[1] / GRAV_DIVIDER;
	}

	@Override
	public float getVelocityY() {
		return gravData[0] / GRAV_DIVIDER;
	}

	@Override
	public void setData(float[] data) {
		for(int i = 0; i < gravData.length; i++)
    		gravData[i] = data[i]; 
		
	}

}
