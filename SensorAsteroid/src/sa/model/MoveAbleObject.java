package sa.model;

public class MoveAbleObject {
	protected float pX,pY;
	protected float vX, vY;
	protected int aX, aY;
	protected float radius;
	
	public MoveAbleObject(int areaX, int areaY, float radius){
		aX = areaX;
		aY = areaY;
		this.radius = radius;
	}
	
	public void updatePos(){
		pX += vX;
		pY += vY;
		if(pX < 0 - radius)
			pX += aX + 2 * radius;
		else if(pX > aX + radius)
			pX -= (aX + 2 * radius);
		if(pY < 0 - radius)
			pY += aY + 2 * radius;
		else if(pY > aY + radius)
			pY -= (aY + 2 * radius);
	}
	
	public void setSpeed(float x, float y){
		vX = x;
		vY = y;
	}
	
	public void setPos(float x, float y){
		pX = x;
		pY = y;
	}
	
	public float getPosX(){
		return pX;
	}
	
	public float getPosY(){
		return pY;
	}
	
	public float getRadius(){
		return radius;
	}
	
	public boolean detectCollision(MoveAbleObject check){
		if(Math.sqrt((pX - check.pX) * (pX - check.pX) + (pY - check.pY) * (pY - check.pY)) < radius + check.radius)
			return true;
		return false;
	}

}
