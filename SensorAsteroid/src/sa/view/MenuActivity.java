package sa.view;

import sa.variables.SVar;
import se.sa.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MenuActivity extends Activity {
	private int sensor = SVar.ROTATION_VECTOR_SENSOR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	public void onClick(View v){
		if(v == findViewById(R.id.menuStartBtn)){
			Intent in = new Intent(this, GameActivity.class);
			in.putExtra(getString(R.string.sensor), sensor);
			startActivity(in);
		}
		else if(v == findViewById(R.id.menuGravRadio)){
			sensor = SVar.GRAVITY_SENSOR;
		}
		else if(v == findViewById(R.id.menuRotRadio)){
			sensor = SVar.ROTATION_VECTOR_SENSOR;
		}
	}

}
