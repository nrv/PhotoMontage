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

import java.awt.geom.Rectangle2D;

public class StandardROI extends PhotoMontageROI {

	public StandardROI(Rectangle2D r) {
		super(r);
		setName("StandardROI");
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		StandardROI roi = new StandardROI(getBounds2D());
		return roi;
	}

}
