package plugins.nherve.photomontage.roi;

import icy.painter.Anchor2D;
import icy.roi.ROI2DRectangle;
import icy.roi.ROIEvent.ROIPointEventType;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class PhotoMontageROI extends ROI2DRectangle implements Cloneable {
	
	public PhotoMontageROI(Rectangle2D r) {
		super(r);
		setName("PhotoMontageROI");
	}

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
	
	public Link getLink(PhotoMontageROI other, double frameThickness) {
		Link l = getXLink(other, frameThickness);
		if (l == null) {
			l = getYLink(other, frameThickness);
		}
		return l;
	}
	
	private Link getXLink(PhotoMontageROI other, double frameThickness) {
		Rectangle2D r1 = getBounds2D();
		Rectangle2D r2 = other.getBounds2D();
		
		if (r1.intersects(r2)) {
			return null;
		}
		
		if (r1.getMaxX() + frameThickness < r2.getMinX() - frameThickness) {
			return null;
		}
		
		if (r2.getMaxX() + frameThickness < r1.getMinX() - frameThickness) {
			return null;
		}
		
		double minX = Math.max(r1.getMinX(), r2.getMinX());
		double maxX = Math.min(r1.getMaxX(), r2.getMaxX());
		double x = minX + (maxX - minX) / 2;
		double minY = Math.max(r1.getMinY(), r2.getMinY());
		double maxY = Math.min(r1.getMaxY(), r2.getMaxY());
		
		return new Link(x, minY - frameThickness, x, maxY + frameThickness);
	}
	
	private Link getYLink(PhotoMontageROI other, double frameThickness) {
		Rectangle2D r1 = getBounds2D();
		Rectangle2D r2 = other.getBounds2D();
		
		if (r1.intersects(r2)) {
			return null;
		}
		
		if (r1.getMaxY() + frameThickness < r2.getMinY() - frameThickness) {
			return null;
		}
		
		if (r2.getMaxY() + frameThickness < r1.getMinY() - frameThickness) {
			return null;
		}
		
		double minY = Math.max(r1.getMinY(), r2.getMinY());
		double maxY = Math.min(r1.getMaxY(), r2.getMaxY());
		double y = minY + (maxY - minY) / 2;
		double minX = Math.max(r1.getMinX(), r2.getMinX());
		double maxX = Math.min(r1.getMaxX(), r2.getMaxX());
		
		return new Link(minX - frameThickness, y, maxX + frameThickness, y);
	}

	@Override
	public String toString() {
		Rectangle2D r1 = getBounds2D();
		return getName() + " - ("+r1.getMinX()+", "+r1.getMinY()+" -> "+r1.getMaxX()+", "+r1.getMaxY()+")";
	}

	
	@Override
	public abstract Object clone() throws CloneNotSupportedException;

}
