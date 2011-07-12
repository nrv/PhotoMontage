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

import java.awt.Color;
import java.awt.geom.Point2D;

import icy.painter.Anchor2D;

public class Anchor2DNoEvent extends Anchor2D {

	public Anchor2DNoEvent(Point2D position, Color color, Color selectedColor) {
		super(position, color, selectedColor);
	}

	public void setPositionNoEvent(double x, double y) {
		if ((position.x != x) || (position.y != y)) {
			position.x = x;
			position.y = y;
			
			changed();
		}
	}

	public void setXNoEvent(double x) {
		setPositionNoEvent(x, position.y);
	}
	
	public void setYNoEvent(double y) {
		setPositionNoEvent(position.x, y);
	}
	
	public void translateNoEvent(double dx, double dy) {
		setPositionNoEvent(position.x + dx, position.y + dy);
	}
}
