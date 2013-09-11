package sa.controller;

import java.util.ArrayList;

import sa.model.*;

public class GameController {
	private static final int STARTING_ASTEROIDS = 0;
	private static final long RELOAD_TIME = 250;
	private ArrayList<Asteroid> asteroids;
	private ArrayList<Shot> shots;
	private long lastShot;
	private Rocket rocket;
	private int aX, aY;

	private boolean crashed = false;

	public GameController(int areaX, int areaY, SensorInterface si){
		aX = areaX;
		aY = areaY;
		rocket = new Rocket(aX, aY, si);
		shots = new ArrayList<Shot>();
		asteroids = new ArrayList<Asteroid>();
		for(int i = 0; i < STARTING_ASTEROIDS; i++){
			asteroids.add(new Asteroid(aX, aY, 4));
			asteroids.get(i).setSpeed((float)((Math.random() - 0.5) * 5), (float)((Math.random() - 0.5) * 5));
			asteroids.get(i).setPos((float)(Math.random() * aX), 0);
		}
	}

	public void updMdl(){
		if(!crashed){
			rocket.updatePos();
			for(int i = 0; i < asteroids.size(); i++){
				asteroids.get(i).updatePos();
				if(rocket.detectCollision(asteroids.get(i))){
					crashed = true;
				}
			}
			for(int i = shots.size() - 1; i >= 0; i--){
				shots.get(i).updatePos();
				boolean hit = false;
				for(int j = 0; j < asteroids.size(); j++){
					if(shots.get(i).detectCollision(asteroids.get(j))){
						if(asteroids.get(j).getLevel() != 1){
							for(int k = 0; k < asteroids.get(j).getLevel(); k++){
								Asteroid a = new Asteroid(aX, aY, asteroids.get(j).getLevel() - 1);
								a.setSpeed((float)((Math.random() - 0.5) * 5), (float)((Math.random() - 0.5) * 5));
								a.setPos(asteroids.get(j).getPosX(), asteroids.get(j).getPosY());
								asteroids.add(a);
							}
						}
						hit = true;
						asteroids.remove(j);
						shots.remove(i);
						break;
					}
				}
				if(!hit){
					if(shots.get(i).isDead())
						shots.remove(i);
				}
			}
		}
	}
	
	public void shoot(){
		if(lastShot + RELOAD_TIME < System.currentTimeMillis()){
			Shot s = new Shot(aX, aY, 3);
			s.setPos(rocket.getPosX() + (float)(Math.cos(Math.toRadians(rocket.getRocketAngle())) * (rocket.getRadius())), 
					rocket.getPosY() + (float)(Math.sin(Math.toRadians(rocket.getRocketAngle())) * (rocket.getRadius())));
			

			s.setSpeed((float)(Math.cos(Math.toRadians(rocket.getRocketAngle())) * (Shot.SPEED)), 
					(float)(Math.sin(Math.toRadians(rocket.getRocketAngle())) * (Shot.SPEED)));
			
			shots.add(s);
			lastShot = System.currentTimeMillis();
		}
	}

	public Rocket getRocket(){return rocket;}
	public ArrayList<Asteroid> getAsteroids(){return asteroids;}
	public ArrayList<Shot> getShots(){return shots;}
	public boolean getCrashed(){return crashed;}
}
