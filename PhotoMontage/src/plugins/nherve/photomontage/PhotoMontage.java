package plugins.nherve.photomontage;

import icy.gui.component.ComponentUtil;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.gui.util.WindowPositionSaver;
import icy.roi.ROIEvent;
import icy.roi.ROIEvent.ROIEventType;
import icy.roi.ROIListener;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceEvent.SequenceEventSourceType;
import icy.sequence.SequenceEvent.SequenceEventType;
import icy.sequence.SequenceListener;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import plugins.nherve.photomontage.roi.ROI2DRectangleAspectRatio;
import plugins.nherve.toolbox.image.mask.Mask;
import plugins.nherve.toolbox.plugin.PainterManagerSingletonPlugin;

public class PhotoMontage extends PainterManagerSingletonPlugin<PhotoMontagePainter> implements ActionListener, DocumentListener, ROIListener, SequenceListener {
	private final static String PLUGIN_NAME = "Photo Montage";
	private final static String PLUGIN_VERSION = "1.0.0";
	private final static String FULL_PLUGIN_NAME = PLUGIN_NAME + " V" + PLUGIN_VERSION;
	private final static String PREFERENCES_NODE = "icy/plugins/nherve/photomontage/PhotoMontage";

	private IcyFrame frame;
	private JLabel lbCurrentImage;
	private JTextField tfROIW;
	private JTextField tfROIH;
	private JLabel lbCurrentRatio;
	private JButton btCreateROI;

	private double currentRatio;

	private DecimalFormat df;

	public PhotoMontage() {
		super();

		df = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.FRANCE));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JButton) {
			JButton b = (JButton) o;

			if (b == btCreateROI) {
				createNewROI();
				return;
			}
		}
	}

	@Override
	public PhotoMontagePainter createNewPainter() {
		PhotoMontagePainter painter = new PhotoMontagePainter();
		painter.setPlugin(this);
		Sequence currentSequence = getCurrentSequence();
		painter.setSequence(currentSequence);
		painter.setNeedRedraw(true);
		return painter;
	}

	private void createNewROI() {
		if (hasCurrentSequence()) {
			Sequence s = getCurrentSequence();

			double x = 0d;
			double y = 0d;
			double w = 100d;

			double h = w / currentRatio;

			Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
			ROI2DRectangleAspectRatio roi = new ROI2DRectangleAspectRatio(r, currentRatio);

			roi.attachTo(s);
			
			roi.addListener(this);
		}
	}
	
	private void updatePainter() {
		if (hasCurrentSequence()) {
			getCurrentSequencePainter().setNeedRedraw(true);
			getCurrentSequence().painterChanged(null);
		}
	}

	@Override
	public String getPainterName() {
		return PhotoMontagePainter.class.getName();
	}

	@Override
	public void sequenceHasChangedAfterSettingPainter() {
		if (hasCurrentSequence()) {
			lbCurrentImage.setText(getCurrentSequence().getName());
			getCurrentSequence().addListener(this);
		} else {
			lbCurrentImage.setText("none");
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

		lbCurrentImage = new JLabel("none");
		JPanel p1 = GuiUtil.createLineBoxPanel(new JLabel("Current image : "), Box.createHorizontalGlue(), lbCurrentImage);
		mainPanel.add(p1);

		btCreateROI = new JButton("Create");
		btCreateROI.addActionListener(this);

		int w = 3;
		int h = 2;

		JLabel lbW = new JLabel("W");
		tfROIW = new JTextField(Integer.toString(w));
		ComponentUtil.setFixedHeight(tfROIW, 25);
		JLabel lbH = new JLabel("H");
		tfROIH = new JTextField(Integer.toString(h));
		ComponentUtil.setFixedHeight(tfROIH, 25);
		JLabel lbR = new JLabel("Ratio : ");
		lbCurrentRatio = new JLabel();

		updateRatio();

		JPanel p2 = GuiUtil.createLineBoxPanel(lbW, tfROIW, Box.createHorizontalGlue(), lbH, tfROIH, Box.createHorizontalGlue(), lbR, lbCurrentRatio, Box.createHorizontalGlue(), btCreateROI);
		p2.setBorder(new TitledBorder("New ROI"));
		mainPanel.add(p2);

		tfROIW.getDocument().addDocumentListener(this);
		tfROIH.getDocument().addDocumentListener(this);

		frame.addFrameListener(this);
		frame.setVisible(true);
		frame.pack();

		frame.requestFocus();
	}

	@Override
	public void stopInterface() {
	}

	private void updateRatio() {
		try {
			double w = Double.parseDouble(tfROIW.getText());
			double h = Double.parseDouble(tfROIH.getText());

			if ((h != 0) && (w != 0)) {
				currentRatio = w / h;
				lbCurrentRatio.setText(df.format(currentRatio));
				btCreateROI.setEnabled(true);
				return;
			}
		} catch (NumberFormatException e) {
			// ignore
		}

		lbCurrentRatio.setText("n.a.");
		btCreateROI.setEnabled(false);
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateRatio();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateRatio();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateRatio();
	}

	@Override
	public void roiChanged(ROIEvent event) {
		if (event.getType() == ROIEventType.ROI_CHANGED) {
			updatePainter();
		}
	}

	@Override
	public void sequenceChanged(SequenceEvent sequenceEvent) {
		if ((sequenceEvent.getSourceType() == SequenceEventSourceType.SEQUENCE_ROI) && (sequenceEvent.getType() == SequenceEventType.REMOVED)) {
			updatePainter();
		}
		if ((sequenceEvent.getSourceType() == SequenceEventSourceType.SEQUENCE_ROI) && (sequenceEvent.getType() == SequenceEventType.ADDED)) {
			updatePainter();
		}
	}

	@Override
	public void sequenceClosed(Sequence sequence) {
		
	}

}
