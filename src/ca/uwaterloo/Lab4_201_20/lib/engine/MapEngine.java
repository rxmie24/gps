package ca.uwaterloo.Lab4_201_20.lib.engine;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.graphics.PointF;
import ca.uwaterloo.Lab4_201_20.lib.Functions;
import ca.uwaterloo.Lab4_201_20.lib.InterfaceHandler;
import ca.uwaterloo.Lab4_201_20.lib.InterfaceHandler.UIObject;
import ca.uwaterloo.Lab4_201_20.mapper.*;

public class MapEngine implements PositionListener {

	NavigationalMap _navMap;
	MapView _mapView;
	InterfaceHandler _interfaceUpdater;

	public MapEngine(NavigationalMap inNavMap_, MapView inMapView_, int inWidth_, int inHeight_) {
		this._mapView = inMapView_;
		this._navMap = inNavMap_;
		this._interfaceUpdater = InterfaceHandler.getInstance();
	}

	@Override
	public void originChanged(MapView source, PointF loc) {
		source.setUserPoint(loc);
		source.setOriginPoint(loc);
		this.updateUserPath();
	}

	@Override
	public void destinationChanged(MapView source, PointF dest) {
		source.setDestinationPoint(dest);
		this.updateUserPath();
	}

	public void updatePosition(double currBearing) {
		float deltaY = (-1) * (float) Math.cos(Math.toRadians(currBearing));
		float deltaX = (float) Math.sin(Math.toRadians(currBearing));

		PointF tmp = new PointF(_mapView.getUserPoint().x + (deltaX / Functions.Config.MAP_WALK_SCALE),
				_mapView.getUserPoint().y + (deltaY / Functions.Config.MAP_WALK_SCALE));

		if (this._navMap.calculateIntersections(_mapView.getUserPoint(), tmp).size() == 0) {
			this._mapView.setUserPoint(tmp);
			this.updateUserPath();
		}
	}

	public void resetPoints() {
		this._mapView.setDestinationPoint(new PointF(0, 0));
		this._mapView.setOriginPoint(new PointF(0, 0));
		this._mapView.setUserPoint(new PointF(0, 0));
		this._mapView.setUserPath(Arrays.asList(new PointF(0, 0)));
	}

	private void updateUserPath() {
		List<InterceptPoint> tmp = _navMap.calculateIntersections(this._mapView.getUserPoint(),
				this._mapView.getDestinationPoint());

		if (tmp.size() == 0)
			_mapView.setUserPath(Arrays.asList(this._mapView.getUserPoint(), this._mapView.getDestinationPoint()));
		else
			_mapView.setUserPath(pathFinder(this._mapView.getUserPoint(), this._mapView.getDestinationPoint()));

		this.userArrivalCheck();
	}

	private void userArrivalCheck() {
		PointF curr = this._mapView.getUserPoint();
		PointF dest = this._mapView.getDestinationPoint();
		List<PointF> nextPt = this._mapView.getUpdatedUserPath();

		if (nextPt.size() > 1) {
			PointF tmpSpot = nextPt.get(1);

			float deltaX = tmpSpot.x - curr.x;
			float deltaY = tmpSpot.y - curr.y;

			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);

			this._interfaceUpdater.setLabelText("Walk: " + nf.format(Math.abs(deltaX * Functions.Config.MAP_WALK_SCALE))
					+ " Steps " + (deltaX <= 0 ? "West" : "East"), UIObject.EastSteps);

			this._interfaceUpdater.setLabelText("Walk: " + nf.format(Math.abs(deltaY * Functions.Config.MAP_WALK_SCALE))
					+ " Steps " + (deltaX <= 0 ? "South" : "North"), UIObject.NorthSteps);
		}

		float deltaX = dest.x - curr.x;
		float deltaY = dest.y - curr.y;

		this._interfaceUpdater
				.setLabelText(
						(Math.abs(deltaX) < Functions.Config.MAP_DEST_REACH_DISCREPENCY && Math.abs(deltaY) < Functions.Config.MAP_DEST_REACH_DISCREPENCY) ? "You are at your destination!"
								: "Not arrived yet", UIObject.DestinationReached);
	}

	private List<PointF> pathFinder(PointF inOrigin_, PointF inDest_) {
		ArrayList<PointF> rtnPath = new ArrayList<PointF>();
		rtnPath.add(inOrigin_);

		List<PointF> simplePath = pathIterator(inDest_.y > inOrigin_.y ? inOrigin_.y : inDest_.y,
				inDest_.y > inOrigin_.y ? inDest_.y : inOrigin_.y, true, inOrigin_, inDest_);
		if (simplePath != null) {
			rtnPath.addAll(simplePath);
			return rtnPath;
		}

		List<PointF> moveUp = pathIterator(inOrigin_.y, inOrigin_.y + Functions.Config.MAP_PATH_MAX_SCAN, true, inOrigin_,
				inDest_);
		if (moveUp != null) {
			rtnPath.addAll(moveUp);
			return rtnPath;
		}

		List<PointF> moveDown = pathIterator(inOrigin_.y, inOrigin_.y - Functions.Config.MAP_PATH_MAX_SCAN, false, inOrigin_,
				inDest_);
		if (moveDown != null) {
			rtnPath.addAll(moveDown);
			return rtnPath;
		}

		return rtnPath;
	}

	private List<PointF> pathIterator(float inFrom_, float inTo_, boolean inPositiveDecrement_, PointF inOrigin_, PointF inDest_) {
		if (inPositiveDecrement_) {
			for (float i = inFrom_; i < inTo_; i += Functions.Config.MAP_PATH_SCAN_VALUE) {
				List<PointF> subPath = subPathFinder(new PointF(inOrigin_.x, i), new PointF(inDest_.x, i), inDest_);
				if (subPath != null)
					return subPath;
			}
		} else {
			for (float i = inFrom_; i > inTo_; i -= Functions.Config.MAP_PATH_SCAN_VALUE) {
				List<PointF> subPath = subPathFinder(new PointF(inOrigin_.x, i), new PointF(inDest_.x, i), inDest_);
				if (subPath != null)
					return subPath;
			}
		}
		return null;
	}

	private List<PointF> subPathFinder(PointF inSrc_, PointF inTmpDest_, PointF inRealDest_) {
		if (_navMap.calculateIntersections(inSrc_, inTmpDest_).size() == 0)
			if (_navMap.calculateIntersections(inTmpDest_, inRealDest_).size() == 0)
				return Arrays.asList(inSrc_, inTmpDest_, inRealDest_);

		return null;
	}
}
