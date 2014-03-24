package ca.uwaterloo.Lab4_201_20.lib;

public class Functions {

	public static float[] lowPassFilter(float[] in, float dt, float RC) {
		float[] out = new float[in.length];

		float a = dt / (dt + RC);
		out[0] = 0;

		for (int i = 1; i < in.length; i++)
			out[i] = a * in[i] + (1 - a) * out[i - 1];

		return out;
	}

	public static float getMagnitude(float[] inFloat_) {
		double sum = 0;

		for (int i = 0; i < inFloat_.length; i++)
			sum += Math.pow(inFloat_[i], 2);

		return (float) Math.sqrt(sum);
	}

	public static double compassRawFilter(double prevVal, double currVal) {
		double magn = Math.abs(currVal - prevVal);
		return magn < Functions.Config.COMPASS_DEG_SMOOTH_FACTOR ? prevVal : currVal;
	}

	public class Config {
		public static final double DISCREPENCY_VALUE_MAIN = 0.30;
		public static final double DISCREPENCY_VALUE_MAGN = 0.44;

		public static final String LABEL_TOTALSTEPS = "Total Steps: ";
		public static final String LABEL_EASTSTEPS = "East Steps: ";
		public static final String LABEL_NORTHSTEPS = "North Steps: ";

		public static final float FILTER_DT = 0.21f;
		public static final float FILTER_RC = 0.93f;

		public static final int IDX_CHOSEN_AXIS = 2;

		public static final int QUANT_SENSOR = 3;
		public static final int QUANT_MATRIX = 9;

		public static final int IDX_AZIMUTH = 0;

		public static final double COMPASS_DEG_SMOOTH_FACTOR = 1.2;
		public static final double COMPASS_DEG_THRESHOLD = 90;

		public static final int MAP_X_WIDTH = 181;
		public static final int MAP_Y_HEIGHT = 120;
		public static final int MAP_SCALE = 10;
		public static final float MAP_PATH_SCAN_VALUE = 0.1f;
		public static final float MAP_PATH_MAX_SCAN = 10.6f;
		public static final float MAP_WALK_SCALE = 2.4f;
		public static final float MAP_DEST_REACH_DISCREPENCY = 0.5f;
	}
}
