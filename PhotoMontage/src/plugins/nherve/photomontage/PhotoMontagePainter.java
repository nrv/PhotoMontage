package plugins.nherve.photomontage;

import icy.canvas.IcyCanvas;
import icy.painter.Painter;
import icy.roi.ROI2D;
import icy.sequence.Sequence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import plugins.nherve.photomontage.roi.PhotoMontageROI;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.image.mask.MaskException;

public class PhotoMontagePainter implements Painter {
	private Sequence internalSequence;

	private Mask mask;
	private List<Line2D> lines;
	private boolean needRedraw;
	private PhotoMontage plugin;

	public PhotoMontagePainter() {
		super();
		
		lines = new ArrayList<Line2D>();
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

			ArrayList<PhotoMontageROI> rois = new ArrayList<PhotoMontageROI>();

			for (ROI2D roi : internalSequence.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					rois.add((PhotoMontageROI) roi);
					try {
						mask.remove(roi);
					} catch (MaskException e) {
						e.printStackTrace();
					}
				}
			}

			lines.clear();
			
			for (int i = 0; i < rois.size() - 1; i++) {
				for (int j = i + 1; j < rois.size(); j++) {
					Line2D line = rois.get(i).getXLink(rois.get(j));
					if (line == null) {
						line = rois.get(i).getYLink(rois.get(j));
					}
					if (line != null) {
						boolean add = true;
						for (int k = 0; k < rois.size(); k++) {
							if ((k != i) && (k !=j) && (line.intersects(rois.get(k).getRectangle()))) {
								add = false;
								break;
							}
						}
						if (add) {
							lines.add(line);
						}
					}
				}
			}

			needRedraw = false;
		}

		mask.paint(g);

		if (lines.size() > 0) {
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke((float) (1 / canvas.getScaleFactorX())));
			for (Line2D l : lines) {
				g.draw(l);
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
