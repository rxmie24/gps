package ca.uwaterloo.Lab4_201_20;

import java.util.Arrays;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.uwaterloo.Lab4_201_20.R;
import ca.uwaterloo.Lab4_201_20.lib.*;
import ca.uwaterloo.Lab4_201_20.lib.engine.CompassEngine;
import ca.uwaterloo.Lab4_201_20.lib.engine.MapEngine;
import ca.uwaterloo.Lab4_201_20.lib.engine.PedometerEngine;
import ca.uwaterloo.Lab4_201_20.mapper.MapLoader;
import ca.uwaterloo.Lab4_201_20.mapper.MapView;
import ca.uwaterloo.Lab4_201_20.mapper.NavigationalMap;

public class MainActivity extends Activity {

	MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView = new MapView(getApplicationContext(), 500, 500, 25, 25);

		setContentView(R.layout.activity_main);

		registerForContextMenu(mapView);
		LinearLayout layoutMain = (LinearLayout) findViewById(R.id.ll);
		layoutMain.setOrientation(LinearLayout.VERTICAL);

		NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null), "Lab-room-peninsula.svg");

		this.InitializeLabels(layoutMain, map);

		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		CompassEngine compass = new CompassEngine();
		MapEngine mapEngine = new MapEngine(map, mapView, Functions.Config.MAP_X_WIDTH, Functions.Config.MAP_Y_HEIGHT);
		mapView.addListener(mapEngine);

		PedometerEngine pedometer = new PedometerEngine(compass, mapEngine);

		sensorManager.registerListener(compass, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(compass, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(pedometer, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST);

		this.InitializeResetButton(layoutMain, pedometer);
	}

	// In your Activity :
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		mapView.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item) || mapView.onContextItemSelected(item);
	}

	public void InitializeLabels(LinearLayout layoutMain, NavigationalMap navMap) {
		mapView.setMap(navMap);
		layoutMain.addView(mapView);

		InterfaceHandler interfaceUpdater = InterfaceHandler.getInstance();

		interfaceUpdater.setLabel((TextView) findViewById(R.id.Steps), InterfaceHandler.UIObject.StepCounter);
		interfaceUpdater.setLabel((TextView) findViewById(R.id.Orientation), InterfaceHandler.UIObject.Orientation);
		interfaceUpdater.setLabel((TextView) findViewById(R.id.NorthSteps), InterfaceHandler.UIObject.NorthSteps);
		interfaceUpdater.setLabel((TextView) findViewById(R.id.EastSteps), InterfaceHandler.UIObject.EastSteps);
		interfaceUpdater.setLabel((TextView) findViewById(R.id.DestinationReached),
				InterfaceHandler.UIObject.DestinationReached);
	}

	public void InitializeResetButton(LinearLayout layoutMain, OnClickListener pedometer) {
		Button resetBtn = new Button(getApplicationContext());
		layoutMain.addView(resetBtn);
		resetBtn.setText("Reset Steps");
		resetBtn.setOnClickListener(pedometer);
	}
}
