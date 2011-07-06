package plugins.nherve.photomontage;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;

import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.gui.util.WindowPositionSaver;
import icy.sequence.Sequence;
import plugins.nherve.toolbox.plugin.PainterManagerSingletonPlugin;

public class PhotoMontage extends PainterManagerSingletonPlugin<PhotoMontagePainter> {
	private final static String PLUGIN_NAME = "Photo Montage";
	private final static String PLUGIN_VERSION = "1.0.0";
	private final static String FULL_PLUGIN_NAME = PLUGIN_NAME + " V" + PLUGIN_VERSION;
	private final static String PREFERENCES_NODE = "icy/plugins/nherve/photomontage/PhotoMontage";

	private IcyFrame frame;

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
		frame = GuiUtil.generateTitleFrame(FULL_PLUGIN_NAME, mainPanel, new Dimension(580, 100), true, true, true, true);
		addIcyFrame(frame);
		new WindowPositionSaver(frame, PREFERENCES_NODE, new Point(0, 0), new Dimension(580, 800));

	}

	@Override
	public void stopInterface() {
	}

}
