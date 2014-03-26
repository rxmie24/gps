package ca.uwaterloo.Lab4_201_20.lib;

import java.util.HashMap;

import android.widget.TextView;

public class InterfaceHandler {

	private static InterfaceHandler _instance = null;
	private static Object _lockSync = new Object();

	public enum UIObject {
		Graph, StepCounter, NorthSteps, EastSteps, Orientation, DestinationReached
	}

	private HashMap<UIObject, TextView> _tvHash;

	private InterfaceHandler() {
		_tvHash = new HashMap<UIObject, TextView>();
	}

	public void setLabel(TextView inTextView_, UIObject inLabel_) {
		if (this._tvHash.containsKey(inLabel_) == false)
			this._tvHash.put(inLabel_, inTextView_);
	}

	public void setLabelText(String inText_, UIObject inLabel_) {
		if (this._tvHash.containsKey(inLabel_) == true) {
			TextView tmp = this._tvHash.get(inLabel_);
			if (tmp != null)
				tmp.setText(inText_);
		}
	}

	public static InterfaceHandler getInstance() {
		if (_instance == null) {
			synchronized (_lockSync) {
				if (_instance == null)
					_instance = new InterfaceHandler();
			}
		}
		return _instance;
	}
}
