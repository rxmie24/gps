package ca.uwaterloo.Lab4_201_20.lib.engine;

import ca.uwaterloo.Lab4_201_20.lib.Functions;
import ca.uwaterloo.Lab4_201_20.lib.InterfaceHandler;
import ca.uwaterloo.Lab4_201_20.lib.Functions.Config;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.view.View.OnClickListener;

public class PedometerEngine implements SensorEventListener, OnClickListener {

	private InterfaceHandler _interfaceUpdater;

	private int _stepCount;
	private double _northStepCount, _eastStepCount;

	private CompassEngine _compass;
	private MapEngine _mapEngine;

	private boolean _fwdTrack, _bwdTrack;

	public PedometerEngine(CompassEngine inCompass_, MapEngine inMapEngine_) {
		_interfaceUpdater = InterfaceHandler.getInstance();

		this._stepCount = 0;

		this._compass = inCompass_;
		this._mapEngine = inMapEngine_;

		_fwdTrack = true;
		_bwdTrack = false;
	}

	private void Process(float[] inVals_) {
		float[] cleanedArr = Functions.lowPassFilter(inVals_, Config.FILTER_DT, Config.FILTER_RC);

		FSMLogic(cleanedArr, Functions.getMagnitude(cleanedArr));

		_interfaceUpdater.setLabelText("Steps: " + _stepCount, InterfaceHandler.UIObject.StepCounter);

		/*
		 * if (this._compass != null) {
		 * _interfaceUpdater.setLabelText(Config.LABEL_NORTHSTEPS +
		 * _northStepCount, InterfaceHandler.UIObject.NorthSteps);
		 * _interfaceUpdater.setLabelText(Config.LABEL_EASTSTEPS +
		 * _eastStepCount, InterfaceHandler.UIObject.EastSteps); }
		 */
	}

	private void FSMLogic(float[] inCleaned_, float inMagnitude_) {
		if (_fwdTrack && !_bwdTrack)
			if (inCleaned_[Config.IDX_CHOSEN_AXIS] > Config.DISCREPENCY_VALUE_MAIN
					&& inMagnitude_ < Config.DISCREPENCY_VALUE_MAGN) {
				_fwdTrack = false;
				_bwdTrack = true;
			}

		if (!_fwdTrack && _bwdTrack)
			if (inCleaned_[Config.IDX_CHOSEN_AXIS] < (-1) * Config.DISCREPENCY_VALUE_MAIN) {
				_fwdTrack = true;
				_bwdTrack = false;

				_stepCount++;

				if (this._compass != null) {
					double tmpBearing = _compass.getCurrentBearing();

					this._northStepCount += Math.cos(Math.toRadians(tmpBearing));
					this._eastStepCount += Math.sin(Math.toRadians(tmpBearing));

					this._mapEngine.updatePosition(tmpBearing);
				}
			}
	}

	@Override
	public void onClick(View arg0) {
		this._stepCount = 0;
		this._northStepCount = 0;
		this._eastStepCount = 0;

		_interfaceUpdater.setLabelText(Config.LABEL_TOTALSTEPS + _stepCount, InterfaceHandler.UIObject.StepCounter);
		_interfaceUpdater.setLabelText(Config.LABEL_NORTHSTEPS + _northStepCount, InterfaceHandler.UIObject.NorthSteps);
		_interfaceUpdater.setLabelText(Config.LABEL_EASTSTEPS + _eastStepCount, InterfaceHandler.UIObject.EastSteps);

		this._mapEngine.resetPoints();
	}

	@Override
	public void onSensorChanged(SensorEvent inSe_) {
		if (inSe_.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION)
			return;

		this.Process(inSe_.values);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
