/*
 * Copyright 2011 Nicolas Hervé.
 * 
 * This file is part of PhotoMontage, which is an ICY plugin.
 * 
 * PhotoMontage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PhotoMontage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PhotoMontage. If not, see <http://www.gnu.org/licenses/>.
 */

package plugins.nherve.photomontage.roi;

import icy.painter.Anchor2D;
import icy.roi.ROIEvent.ROIPointEventType;

import java.awt.geom.Rectangle2D;

public class FixedSizeROI extends PhotoMontageROI {
	private final double originalWidth;
	private final double originalHeight;
	
	public FixedSizeROI(Rectangle2D r) {
		super(r);
		setName("FixedSizeROI");
		this.originalWidth = r.getWidth();
		this.originalHeight = r.getHeight();
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

	@Override
	public Object clone() throws CloneNotSupportedException {
		FixedSizeROI roi = new FixedSizeROI(getBounds2D());
		return roi;
	}

}
