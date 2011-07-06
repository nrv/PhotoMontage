package plugins.nherve.photomontage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.gui.util.WindowPositionSaver;
import icy.plugin.interface_.PluginROI;
import icy.roi.ROI;
import icy.sequence.Sequence;
import plugins.nherve.toolbox.plugin.PainterManagerSingletonPlugin;

public class PhotoMontage extends PainterManagerSingletonPlugin<PhotoMontagePainter>  {
	private final static String PLUGIN_NAME = "Photo Montage";
	private final static String PLUGIN_VERSION = "1.0.0";
	private final static String FULL_PLUGIN_NAME = PLUGIN_NAME + " V" + PLUGIN_VERSION;
	private final static String PREFERENCES_NODE = "icy/plugins/nherve/photomontage/PhotoMontage";

	private IcyFrame frame;
	
	private JLabel currentImage;

	@Override
	public PhotoMontagePainter createNewPainter() {
		PhotoMontagePainter painter = new PhotoMontagePainter();
		painter.setPlugin(this);
		Sequence currentSequence = getCurrentSequence();
		painter.setSequence(currentSequence);
		return painter;
	}

	@Override
	public String getPainterName() {
		return PhotoMontagePainter.class.getName();
	}

	@Override
	public void sequenceHasChangedAfterSettingPainter() {
		if (hasCurrentSequence()) {
			currentImage.setText(getCurrentSequence().getName());
		} else {
			currentImage.setText("none");
		}
	}

	@Override
	public void sequenceHasChangedBeforeSettingPainter() {
	}

	@Override
	public void sequenceWillChange() {
	}

	@Override
	public void startInterface() {
		JPanel mainPanel = GuiUtil.generatePanel();
		frame = GuiUtil.generateTitleFrame(FULL_PLUGIN_NAME, mainPanel, new Dimension(100, 100), true, true, true, true);
		addIcyFrame(frame);
		new WindowPositionSaver(frame, PREFERENCES_NODE, new Point(0, 0), new Dimension(400, 400));
		
		currentImage = new JLabel("none");
		JPanel p1 = GuiUtil.createLineBoxPanel(new JLabel("Current image : "), Box.createHorizontalGlue(), currentImage);
		mainPanel.add(p1);
		
		frame.addFrameListener(this);
		frame.setVisible(true);
		frame.pack();

		frame.requestFocus();
	}

	@Override
	public void stopInterface() {
	}



}
