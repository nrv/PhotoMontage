package plugins.nherve.photomontage.roi;

import icy.painter.Anchor2D;
import icy.roi.ROIEvent.ROIPointEventType;

import java.awt.geom.Rectangle2D;

public class FixedSizePhotoMontageROI extends PhotoMontageROI {
	private final double originalDPI;
	private final double originalWidth;
	private final double originalHeight;
	
	public FixedSizePhotoMontageROI(Rectangle2D r, double originalDPI) {
		super(r);
		
		this.originalWidth = r.getWidth();
		this.originalHeight = r.getHeight();
		this.originalDPI = originalDPI;
	}

	@Override
	public void positionChanged(Anchor2D source) {
		if (source == bottomRight) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(bottomRight.getX() - originalWidth, bottomRight.getY() - originalHeight);
			((Anchor2DNoEvent)topRight).setPositionNoEvent(bottomRight.getX(), bottomRight.getY() - originalHeight);
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(bottomRight.getX() - originalWidth, bottomRight.getY());
		} else if (source == bottomLeft) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(bottomLeft.getX(), bottomLeft.getY() - originalHeight);
			((Anchor2DNoEvent)topRight).setPositionNoEvent(bottomLeft.getX() + originalWidth, bottomLeft.getY() - originalHeight);
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(bottomLeft.getX() + originalWidth, bottomLeft.getY());
		} else if (source == topRight) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(topRight.getX() - originalWidth, topRight.getY());
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(topRight.getX(), topRight.getY() + originalHeight);
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(topRight.getX() - originalWidth, topRight.getY() + originalHeight);
		} else if (source == topLeft) {
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(topLeft.getX() + originalWidth, topLeft.getY() + originalHeight);
			((Anchor2DNoEvent)topRight).setPositionNoEvent(topLeft.getX() + originalWidth, topLeft.getY());
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(topLeft.getX(), topLeft.getY() + originalHeight);
		}

		roiChanged(ROIPointEventType.POINT_CHANGED, source);
	}

}
