package plugins.nherve.photomontage;

import icy.painter.Anchor2D;
import icy.roi.ROI2DRectangle;
import icy.roi.ROIEvent.ROIPointEventType;

import java.awt.geom.Point2D;

public class ROI2DRectangleAspectRatio extends ROI2DRectangle {

	private Point2D bkTopLeft;
	private Point2D bkTopRight;
	private Point2D bkBottomLeft;
	private Point2D bkBottomRight;
	
	public ROI2DRectangleAspectRatio(Point2D pt, boolean cm) {
		super(pt, cm);
		setName("RectangleAR2D");
		
		bkTopLeft = topLeft.getPosition();
		bkTopRight = topRight.getPosition();
		bkBottomLeft = bottomLeft.getPosition();
		bkBottomRight = bottomRight.getPosition();
	}

	@Override
	public void positionChanged(Anchor2D source) {
		if (source == bottomRight) {
			topRight.setX(bottomRight.getX());
			bottomLeft.setY(bottomRight.getY());
			bkBottomRight = bottomRight.getPosition();
		}

		roiChanged(ROIPointEventType.POINT_CHANGED, source);
	}

}
