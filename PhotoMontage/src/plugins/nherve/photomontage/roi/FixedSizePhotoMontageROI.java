package plugins.nherve.photomontage.roi;

import java.awt.geom.Rectangle2D;

import icy.painter.Anchor2D;
import icy.roi.ROIEvent.ROIPointEventType;

public class FixedSizePhotoMontageROI extends PhotoMontageROI {
	private final double w;
	private final double h;
	
	public FixedSizePhotoMontageROI(Rectangle2D r) {
		super(r);
		
		w = r.getWidth();
		h = r.getHeight();
	}

	@Override
	public void positionChanged(Anchor2D source) {
		if (source == bottomRight) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(bottomRight.getX() - w, bottomRight.getY() - h);
			((Anchor2DNoEvent)topRight).setPositionNoEvent(bottomRight.getX(), bottomRight.getY() - h);
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(bottomRight.getX() - w, bottomRight.getY());
		} else if (source == bottomLeft) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(bottomLeft.getX(), bottomLeft.getY() - h);
			((Anchor2DNoEvent)topRight).setPositionNoEvent(bottomLeft.getX() + w, bottomLeft.getY() - h);
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(bottomLeft.getX() + w, bottomLeft.getY());
		} else if (source == topRight) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(topRight.getX() - w, topRight.getY());
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(topRight.getX(), topRight.getY() + h);
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(topRight.getX() - w, topRight.getY() + h);
		} else if (source == topLeft) {
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(topLeft.getX() + w, topLeft.getY() + h);
			((Anchor2DNoEvent)topRight).setPositionNoEvent(topLeft.getX() + w, topLeft.getY());
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(topLeft.getX(), topLeft.getY() + h);
		}

		roiChanged(ROIPointEventType.POINT_CHANGED, source);
	}

}
