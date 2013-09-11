package sa.controller;

import java.util.ArrayList;

import sa.model.*;
import sa.variables.SVar;

public class GameController {
	private static final int STARTING_ASTEROIDS = 2;
	private static final long RELOAD_TIME = 150;
	private ArrayList<Asteroid> asteroids;
	private ArrayList<Shot> shots;
	private long lastShot;
	private Rocket rocket;
	private int aX, aY;
	private int score = 0;

	private boolean crashed = false;

	public GameController(int areaX, int areaY, SensorInterface si){
		aX = areaX;
		aY = areaY;
		rocket = new Rocket(aX, aY, si);
		shots = new ArrayList<Shot>();
		asteroids = new ArrayList<Asteroid>();
		for(int i = 0; i < STARTING_ASTEROIDS; i++){
			asteroids.add(createNewAsteroid(SVar.ASTEROID_MAX_LEVEL));
		}
	}

	public void updMdl(){
		if(!crashed){
			rocket.updatePos();
			if(asteroids.size() == 0)
				asteroids.add(createNewAsteroid(SVar.ASTEROID_MAX_LEVEL));
				
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
						updateScore(asteroids.get(j).getLevel());
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
	
	private Asteroid createNewAsteroid(int level){
		Asteroid a = new Asteroid(aX, aY, level);
		a.setSpeed((float)((Math.random() - 0.5) * 5), (float)((Math.random() - 0.5) * 5));
		a.setPos((float)(Math.random() * aX), 0);
		return a;
	}
	
	private void updateScore(int level){
		score += SVar.ASTEROID_SCORE[level - 1];
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
	public int getScore(){return score;}
}
