package sa.view;

import sa.controller.GameController;
import sa.model.GravitySensor;
import sa.model.Rocket;
import sa.model.RotationSensor;
import sa.model.SensorInterface;
import sa.variables.SVar;
import se.sa.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
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
	private static final String TAG = "GameActivity";
	private DrawPnl    drawPnl; 
	private GameController gc;

	private SensorInterface si;
	private int sensor;
	private SensorManager mSensorManager;
	private Sensor mGravitySensor;
	private Sensor mRotSensor;
	private float[] rotValues;
	private float[] rotMatrix;
	private Point screenSize = new Point();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  


		Bundle extra = getIntent().getExtras();

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		si = null;
		sensor = extra.getInt(getString(R.string.sensor));
		switch(sensor){
		case SVar.GRAVITY_SENSOR:
			mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			si = new GravitySensor();
			break;
		case SVar.ROTATION_VECTOR_SENSOR:
			mRotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			si = new RotationSensor();
			rotValues = new float[3];
			rotMatrix = new float[16];
			break;
		}


		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(screenSize);
		gc = new GameController(screenSize.x, screenSize.y, si);

		drawPnl = new DrawPnl(this); 
		addContentView(drawPnl, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); 
	}

	@Override
	protected void onResume(){
		super.onResume();
		switch(sensor){
		case SVar.GRAVITY_SENSOR:
			mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
			break;
		case SVar.ROTATION_VECTOR_SENSOR:
			mSensorManager.registerListener(this, mRotSensor, SensorManager.SENSOR_DELAY_NORMAL);
			break;
		}
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
			si.setData(event.values);
		}
		else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
			SensorManager.getRotationMatrixFromVector(rotMatrix, event.values);
			SensorManager.getOrientation(rotMatrix, rotValues);
			si.setData(rotValues);
			//gc.getRocket().setRotData(rotValues);
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
		private Paint textSmallPnt;
		private Paint textMedPnt;
		private Paint textLargePnt;
		private Paint rocketPnt;
		private Paint shotPnt;
		private Paint[] asteroidPnt;
		private Path rocketShape;


		public DrawPnl(Context context) { 
			super(context); 
			getHolder().addCallback(this); 

			rocketPnt = new Paint();
			rocketPnt.setColor(Color.GREEN);
			rocketPnt.setStyle(Style.FILL);
			rocketShape = new Path();

			textSmallPnt = new Paint();
			textSmallPnt.setColor(Color.BLUE);
			textSmallPnt.setTextSize(15);
			textMedPnt = new Paint();
			textMedPnt.setColor(Color.BLUE);
			textMedPnt.setTextSize(50);
			textLargePnt = new Paint();
			textLargePnt.setColor(Color.BLUE);
			textLargePnt.setTextSize(125);

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
				if(gc.getCrashed()){
					canvas.drawText(getString(R.string.crashed), 
							screenSize.x / 2 - textLargePnt.measureText(getString(R.string.crashed)) / 2, 
							screenSize.y / 2 + textLargePnt.getTextSize() / 2, 
							textLargePnt);

					//score
					canvas.drawText("score: " + gc.getScore(), 5, textMedPnt.getTextSize() + 5, textMedPnt);
				}
				else{
					//draw rocket
					setRocketShape();
					canvas.drawPath(rocketShape, rocketPnt);

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
					//score
					canvas.drawText("score: " + gc.getScore(), 5, textSmallPnt.getTextSize() + 5, textSmallPnt);
				}

			}catch(NullPointerException ignore){

			}
		}

		private void setRocketShape(){
			rocketShape.reset(); // only needed when reusing this path for a new build
			rocketShape.moveTo(gc.getRocket().getDrawPos()[0][0], gc.getRocket().getDrawPos()[0][1]);
			rocketShape.lineTo(gc.getRocket().getDrawPos()[1][0], gc.getRocket().getDrawPos()[1][1]);
			rocketShape.lineTo(gc.getRocket().getDrawPos()[2][0], gc.getRocket().getDrawPos()[2][1]);
			rocketShape.lineTo(gc.getRocket().getDrawPos()[3][0], gc.getRocket().getDrawPos()[3][1]);
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
			//private static final long TIMESTEP = 100;
			//private long timestamp;

			public DrawThread(SurfaceHolder surfaceHolder) { 
				Log.d(TAG, "drawThread created");
				this.surfaceHolder = surfaceHolder; 
				//timestamp = System.currentTimeMillis();
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
