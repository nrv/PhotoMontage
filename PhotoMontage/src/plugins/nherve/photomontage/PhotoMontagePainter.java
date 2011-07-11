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
import java.awt.geom.Point2D;
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

		if (plugin.showLines() && (links != null)) {
			if (links.size() > 0) {
				for (Link l : links) {
					l.paint(g, canvas.getScaleFactorX(), plugin.getDPI(), Color.BLACK, true);
				}
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
