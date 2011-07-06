package plugins.nherve.photomontage.roi;

import icy.painter.Anchor2D;
import icy.roi.ROI2DRectangle;
import icy.roi.ROIEvent.ROIPointEventType;

import java.awt.geom.Point2D;

public class ROI2DRectangleAspectRatio extends ROI2DRectangle {

	public ROI2DRectangleAspectRatio(Point2D pt, boolean cm) {
		super(pt, cm);
		setName("RectangleAR2D");
	}

	@Override
	public void positionChanged(Anchor2D source) {
		double w = 0;
		double h = 0;

		if (source == bottomRight) {
			w = bottomRight.getX() - bottomLeft.getX();
			h = topRight.getY() - bottomRight.getY();
		}

		// aspect ratio stuff here
		if (Math.abs(w) > Math.abs(h)) {
			h = w;
		} else {
			w = h;
		}
		
		if (source == bottomRight) {
			topRight.setX(bottomRight.getX());
			topRight.setY(bottomRight.getY() - h);
			bottomLeft.setX(bottomRight.getX() - w);
			bottomLeft.setY(bottomRight.getY());
			topLeft.setX(bottomRight.getX() - w);
			topLeft.setY(bottomRight.getY() - h);
		}

		roiChanged(ROIPointEventType.POINT_CHANGED, source);
	}

}
