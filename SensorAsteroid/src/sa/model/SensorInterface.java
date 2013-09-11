package sa.model;

public interface SensorInterface {
	public float getAngle();
	public float getVelocityX();
	public float getVelocityY();
	public void setData(float[] data);
}
