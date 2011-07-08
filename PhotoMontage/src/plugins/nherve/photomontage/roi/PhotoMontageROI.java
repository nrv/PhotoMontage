package plugins.nherve.photomontage.roi;

import icy.painter.Anchor2D;
import icy.roi.ROI2DRectangle;
import icy.roi.ROIEvent.ROIPointEventType;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class PhotoMontageROI extends ROI2DRectangle {
	
	public PhotoMontageROI(Rectangle2D r) {
		super(r);
	}

	@Override
	public abstract void positionChanged(Anchor2D source);
	
	@Override
	public void translate(double dx, double dy) {
		((Anchor2DNoEvent)bottomRight).translateNoEvent(dx, dy);
		((Anchor2DNoEvent)topLeft).translateNoEvent(dx, dy);
		((Anchor2DNoEvent)topRight).translateNoEvent(dx, dy);
		((Anchor2DNoEvent)bottomLeft).translateNoEvent(dx, dy);
		
		roiChanged(ROIPointEventType.POINT_CHANGED, bottomRight);
	}

	@Override
	protected Anchor2D createAnchor(Point2D pos) {
		return new Anchor2DNoEvent(pos, DEFAULT_SELECTED_COLOR, OVER_COLOR);
	}
	
	public Line2D getXLink(PhotoMontageROI other) {
		Rectangle2D r1 = getBounds2D();
		Rectangle2D r2 = other.getBounds2D();
		
		if (r1.intersects(r2)) {
			return null;
		}
		
		if (r1.getMaxX() < r2.getMinX()) {
			return null;
		}
		
		if (r2.getMaxX() < r1.getMinX()) {
			return null;
		}
		
		double minX = Math.max(r1.getMinX(), r2.getMinX());
		double maxX = Math.min(r1.getMaxX(), r2.getMaxX());
		double x = minX + (maxX - minX) / 2;
		double minY = Math.max(r1.getMinY(), r2.getMinY());
		double maxY = Math.min(r1.getMaxY(), r2.getMaxY());
		
		return new Line2D.Double(x, minY, x, maxY);
	}
	
}
