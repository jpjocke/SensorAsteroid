package sa.view;

import sa.controller.GameController;
import sa.model.Rocket;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class GameActivity extends Activity implements SensorEventListener{
	private static final String TAG = "ballDraw";
	private DrawPnl    drawPnl; 
	private GameController gc;
	private SensorManager mSensorManager;
	private Sensor mGravitySensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  


		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager.getSensorList(Sensor.TYPE_GRAVITY).size() == 0){
			Log.d(TAG, "missing gravity sensor");
			finish();
		}
		else{
			mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		}


		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		gc = new GameController(size.x, size.y);

		drawPnl = new DrawPnl(this); 
		addContentView(drawPnl, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); 
	}

	@Override
	protected void onResume(){
		super.onResume();
		mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;
		if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
			gc.getRocket().setGravityData(event.values);
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent e) { 
		final int action = e.getAction(); 
		switch (action & MotionEvent.ACTION_MASK) { 
		case MotionEvent.ACTION_DOWN: { 
			gc.shoot();
			break; 
		} 
		} 
		return true;
	}

	private class DrawPnl extends SurfaceView implements SurfaceHolder.Callback{
		private DrawThread drawThread;
		private Paint rocketPnt;
		private Paint shotPnt;
		private Paint[] asteroidPnt;


		public DrawPnl(Context context) { 
			super(context); 
			getHolder().addCallback(this); 

			rocketPnt = new Paint();
			rocketPnt.setColor(Color.BLUE);
			shotPnt = new Paint();
			shotPnt.setColor(Color.WHITE);
			asteroidPnt = new Paint[4];
			for(int i = 0; i < asteroidPnt.length; i++){
				asteroidPnt[i] = new Paint();
				asteroidPnt[i].setColor(Color.rgb(255, i * 25, i * 15));
			}
			drawThread = new DrawThread(getHolder());
			drawThread.setRunning(true); 

		}

		public void doDraw(Canvas canvas) {
			try{
				canvas.drawColor(Color.BLACK);
				if(gc.getCrashed())
					canvas.drawText("CRASHED", 100, 100, rocketPnt);
				//draw rocket
				canvas.drawLine(gc.getRocket().getDrawPos()[0][0], gc.getRocket().getDrawPos()[0][1], gc.getRocket().getDrawPos()[1][0], gc.getRocket().getDrawPos()[1][1], rocketPnt);
				canvas.drawLine(gc.getRocket().getDrawPos()[1][0], gc.getRocket().getDrawPos()[1][1], gc.getRocket().getDrawPos()[2][0], gc.getRocket().getDrawPos()[2][1], rocketPnt);
				canvas.drawLine(gc.getRocket().getDrawPos()[2][0], gc.getRocket().getDrawPos()[2][1], gc.getRocket().getDrawPos()[3][0], gc.getRocket().getDrawPos()[3][1], rocketPnt);
				canvas.drawLine(gc.getRocket().getDrawPos()[3][0], gc.getRocket().getDrawPos()[3][1], gc.getRocket().getDrawPos()[0][0], gc.getRocket().getDrawPos()[0][1], rocketPnt);

				//asteroids
				for(int i = 0; i < gc.getAsteroids().size(); i++){
					canvas.drawCircle(gc.getAsteroids().get(i).getPosX(), 
							gc.getAsteroids().get(i).getPosY(), 
							gc.getAsteroids().get(i).getRadius(), 
							asteroidPnt[gc.getAsteroids().get(i).getLevel() - 1]);
				}
				//shots
				for(int i = 0; i < gc.getShots().size(); i++){
					canvas.drawCircle(gc.getShots().get(i).getPosX(), 
							gc.getShots().get(i).getPosY(), 
							gc.getShots().get(i).getRadius(), 
							shotPnt);
				}
			}catch(NullPointerException ignore){

			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			startDrawThread();
		}

		private void startDrawThread(){
			if(drawThread.getState() == Thread.State.NEW) 
				drawThread.start(); 
			else if(drawThread.getState() == Thread.State.TERMINATED){ 
				drawThread = new DrawThread(getHolder()); 
				drawThread.setRunning(true); 
				drawThread.start(); 
			} 
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			boolean retry = true; 
			drawThread.setRunning(false); 
			while (retry) { 
				try { 
					drawThread.join(); 
					retry = false; 
				} catch (InterruptedException e) { 
					// we will try it again and again... 
				} 
			} 

		}

		/************************************************************************************** 
		 * The thread that draws the screen 
		 **************************************************************************************/
		private class DrawThread extends Thread{ 
			private SurfaceHolder    surfaceHolder; 
			private boolean    run = false; 
			private static final long TIMESTEP = 100;
			private long timestamp;

			public DrawThread(SurfaceHolder surfaceHolder) { 
				Log.d(TAG, "drawThread created");
				this.surfaceHolder = surfaceHolder; 
				timestamp = System.currentTimeMillis();
			} 

			public void setRunning(boolean run) { 
				this.run = run; 
			} 

			public SurfaceHolder getSurfaceHolder() { 
				return surfaceHolder; 
			} 

			@Override
			public void run() { 
				Log.d(TAG, "drawThread started");
				Canvas c; 
				while (run) { 
					c = null; 
					try { 
						gc.updMdl();
						c = surfaceHolder.lockCanvas(null); 
						synchronized (surfaceHolder) { 
							drawPnl.doDraw(c); 
						} 
					} finally { 
						if (c != null) { 
							surfaceHolder.unlockCanvasAndPost(c); 
						} 
					} 
				} 

				Log.d(TAG, "drawThread ended");
			} 
		}
	}

}
