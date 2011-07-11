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
