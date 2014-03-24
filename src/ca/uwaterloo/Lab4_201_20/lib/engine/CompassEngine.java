package ca.uwaterloo.Lab4_201_20.lib.engine;

import ca.uwaterloo.Lab4_201_20.lib.Functions;
import ca.uwaterloo.Lab4_201_20.lib.InterfaceHandler;
import ca.uwaterloo.Lab4_201_20.lib.Functions.Config;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassEngine implements SensorEventListener {

	private float[] valsAccel;
	private float[] valsMagnetic;

	private float[] mxRotation;
	private float[] mxIncline;

	private float[] mxVals;

	private double currBearing;

	private InterfaceHandler interfaceUpdater;

	public CompassEngine() {
		valsAccel = new float[Config.QUANT_SENSOR];
		valsMagnetic = new float[Config.QUANT_SENSOR];

		mxRotation = new float[Config.QUANT_MATRIX];
		mxIncline = new float[Config.QUANT_MATRIX];

		mxVals = new float[Config.QUANT_SENSOR];

		interfaceUpdater = InterfaceHandler.getInstance();
	}

	private void UpdateMagneticValues(float[] inSensorVals_) {
		for (int i = 0; i < Config.QUANT_SENSOR; i++)
			valsMagnetic[i] = inSensorVals_[i];
	}

	private void UpdateAccelerationValues(float[] inSensorVals_) {
		for (int i = 0; i < Config.QUANT_SENSOR; i++)
			valsAccel[i] = inSensorVals_[i];
	}

	private void Process() {
		if (SensorManager.getRotationMatrix(mxRotation, mxIncline, valsAccel, valsMagnetic)) {
			SensorManager.getOrientation(mxRotation, mxVals);
			double tmp = Math.toDegrees(mxVals[Config.IDX_AZIMUTH]);
			currBearing = Functions.compassRawFilter(currBearing, tmp);
		}

		interfaceUpdater.setLabelText("Orientation: " + (int) currBearing, InterfaceHandler.UIObject.Orientation);
	}

	public double getCurrentBearing() {
		return this.currBearing;
	}

	@Override
	public void onSensorChanged(SensorEvent inSe_) {
		switch (inSe_.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			this.UpdateAccelerationValues(inSe_.values);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			this.UpdateMagneticValues(inSe_.values);
			break;
		default:
			return;
		}

		this.Process();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}
