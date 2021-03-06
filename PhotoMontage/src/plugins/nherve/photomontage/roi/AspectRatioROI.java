/*
 * Copyright 2011 Nicolas Herv�.
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

public class AspectRatioROI extends PhotoMontageROI {
	private double ratio;
	
	public AspectRatioROI(Rectangle2D r, double ratio) {
		super(r);
		setName("AspectRatioROI");
		this.ratio = ratio;
	}
	
	@Override
	public void positionChanged(Anchor2D source) {
		double w = 0;

		if (source == bottomRight) {
			w = bottomRight.getX() - bottomLeft.getX();
		} else if (source == bottomLeft) {
			w = bottomRight.getX() - bottomLeft.getX();
		} else if (source == topRight) {
			w = topRight.getX() - topLeft.getX();
		} else if (source == topLeft) {
			w = topRight.getX() - topLeft.getX();
		}

		double h = w / ratio;
		
		if (source == bottomRight) {
			((Anchor2DNoEvent)bottomRight).setPositionNoEvent(topLeft.getX() + w, topLeft.getY() + h);
			((Anchor2DNoEvent)topRight).setXNoEvent(bottomRight.getX());
			((Anchor2DNoEvent)bottomLeft).setYNoEvent(bottomRight.getY());
		} else if (source == bottomLeft) {
			((Anchor2DNoEvent)bottomLeft).setPositionNoEvent(topRight.getX() - w, topRight.getY() + h);
			((Anchor2DNoEvent)topLeft).setXNoEvent(bottomLeft.getX());
			((Anchor2DNoEvent)bottomRight).setYNoEvent(bottomLeft.getY());
		} else if (source == topRight) {
			((Anchor2DNoEvent)topRight).setPositionNoEvent(bottomLeft.getX() + w, bottomLeft.getY() - h);
			((Anchor2DNoEvent)bottomRight).setXNoEvent(topRight.getX());
			((Anchor2DNoEvent)topLeft).setYNoEvent(topRight.getY());
		} else if (source == topLeft) {
			((Anchor2DNoEvent)topLeft).setPositionNoEvent(bottomRight.getX() - w, bottomRight.getY() - h);
			((Anchor2DNoEvent)bottomLeft).setXNoEvent(topLeft.getX());
			((Anchor2DNoEvent)topRight).setYNoEvent(topLeft.getY());
		}

		roiChanged(ROIPointEventType.POINT_CHANGED, source);
	}
	
	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
		
		positionChanged(topLeft);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AspectRatioROI roi = new AspectRatioROI(getBounds2D(), ratio);
		return roi;
	}
}
