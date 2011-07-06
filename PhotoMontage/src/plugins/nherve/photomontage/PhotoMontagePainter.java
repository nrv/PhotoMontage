package plugins.nherve.photomontage;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import icy.canvas.IcyCanvas;
import icy.painter.Painter;
import icy.sequence.Sequence;

public class PhotoMontagePainter implements Painter {
	private Sequence sequence;
	private PhotoMontage plugin;

	@Override
	public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
		
	}

	@Override
	public void mousePressed(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseReleased(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseClick(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseMove(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void mouseDrag(MouseEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void keyPressed(KeyEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	@Override
	public void keyReleased(KeyEvent e, Point2D imagePoint, IcyCanvas canvas) {
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	public PhotoMontage getPlugin() {
		return plugin;
	}

	public void setPlugin(PhotoMontage plugin) {
		this.plugin = plugin;
	}

}
