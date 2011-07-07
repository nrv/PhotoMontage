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
