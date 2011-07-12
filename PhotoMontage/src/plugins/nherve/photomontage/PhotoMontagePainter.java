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

package plugins.nherve.photomontage;

import icy.canvas.IcyCanvas;
import icy.painter.Painter;
import icy.roi.ROI2D;
import icy.sequence.Sequence;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.util.List;

import plugins.nherve.photomontage.roi.Link;
import plugins.nherve.photomontage.roi.PhotoMontageROI;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;

public class PhotoMontagePainter implements Painter {
	private Sequence internalSequence;

	private Mask mask;
	private List<Link> links;
	private boolean needRedraw;
	private PhotoMontage plugin;

	public PhotoMontagePainter() {
		super();

		links = null;
	}

	@Override
	public void keyPressed(KeyEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void keyReleased(KeyEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseClick(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseDrag(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseMove(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mousePressed(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseReleased(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
		if (needRedraw) {
			mask.fill(true);
			mask.setOpacity(plugin.getCurrentOpacity());
			mask.setColor(plugin.getWallColor());

			for (ROI2D roi : internalSequence.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					try {
						mask.remove(roi);
					} catch (MaskException e) {
						e.printStackTrace();
					}
				}
			}

			links = plugin.createLinks(internalSequence);

			needRedraw = false;
		}

		mask.paint(g);

		try {
			for (ROI2D roi : internalSequence.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					
					if (plugin.showLines()) {
						((PhotoMontageROI)roi).paintDimensions(g, canvas.getScaleFactorX(), plugin.getDPI(), Color.BLACK);
					}
					
					Rectangle2D r = roi.getBounds2D();

					double t1 = plugin.getThickness1();
					if (t1 > 0) {
						g.setColor(plugin.getFrameIntColor());
						g.fill(new Rectangle2D.Double(r.getMinX() - t1, r.getMinY() - t1, r.getWidth() + 2 * t1, t1));
						g.fill(new Rectangle2D.Double(r.getMinX() - t1, r.getMaxY(), r.getWidth() + 2 * t1, t1));
						g.fill(new Rectangle2D.Double(r.getMinX() - t1, r.getMinY() - t1, t1, r.getHeight() + 2 * t1));
						g.fill(new Rectangle2D.Double(r.getMaxX(), r.getMinY() - t1, t1, r.getHeight() + 2 * t1));
					}

					double t2 = plugin.getThickness2();
					if (t2 > 0) {
						g.setColor(plugin.getFrameExtColor());
						g.fill(new Rectangle2D.Double(r.getMinX() - t1 - t2, r.getMinY() - t1 - t2, r.getWidth() + 2 * t1 + 2* t2, t2));
						g.fill(new Rectangle2D.Double(r.getMinX() - t1 - t2, r.getMaxY() + t1, r.getWidth() + 2 * t1 + 2* t2, t2));
						g.fill(new Rectangle2D.Double(r.getMinX() - t1 - t2, r.getMinY() - t1 - t2, t2, r.getHeight() + 2 * t1 + 2 * t2));
						g.fill(new Rectangle2D.Double(r.getMaxX() + t1, r.getMinY() - t1 - t2, t2, r.getHeight() + 2 * t1 + 2 * t2));
					}
				}
			}
		} catch (ParseException e) {
			// ignore
		}

		if (plugin.showLines() && (links != null)) {
			try {
				if (links.size() > 0) {
					for (Link l : links) {
						l.paint(g, canvas.getScaleFactorX(), plugin.getDPI(), Color.BLACK, true);
					}
				}
			} catch (ParseException e) {
				// ignore
			}
		}
	}

	public void setNeedRedraw(boolean needRedraw) {
		this.needRedraw = needRedraw;
	}

	public void setPlugin(PhotoMontage plugin) {
		this.plugin = plugin;
	}

	public void setSequence(Sequence sequence) {
		this.internalSequence = sequence;
		this.mask = new Mask(sequence.getWidth(), sequence.getHeight(), true);
		this.mask.setOpacity(0.5f);
	}
}
