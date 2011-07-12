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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import plugins.nherve.photomontage.PhotoMontage;

public class Link extends Line2D.Double {
	private static final long serialVersionUID = -6046364998228141783L;
	private boolean vertical;
	
	public Link(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		
		vertical = (x2 - x1 == 0);
	}
	
	public double length() {
		Rectangle2D r = getBounds2D();
		return vertical ? r.getHeight() : r.getWidth();
	}
	
	public double length(double dpi) {
		return PhotoMontage.convertPixToCm(length(), dpi);
	}
	
	public void paint(Graphics2D g, double zoom, double dpi, Color color, boolean text) {
		g.setColor(color);
		float stroke = (float) (1 / zoom);
		g.setStroke(new BasicStroke(stroke));
		g.draw(this);
		
		if (text) {
			String str = PhotoMontage.format(length(dpi)) + PhotoMontage.CM;
			Font font = new Font("Arial", Font.BOLD, (int)(10 / zoom));
			g.setFont(font);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Rectangle2D r = getBounds2D();
			float tx = (float)r.getCenterX();
			float ty = (float)r.getCenterY();
			Rectangle2D tr = font.getStringBounds(str, g.getFontRenderContext());
			if (vertical) {
				tx += 20;
				ty += tr.getHeight() / 2;
			} else {
				ty -= (20 + tr.getHeight() / 2);
				tx -= tr.getWidth() / 2;
			}
			g.drawString(str, tx, ty);
		}
	}
}
