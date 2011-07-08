package plugins.nherve.photomontage;

import icy.gui.component.ComponentUtil;
import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.gui.util.WindowPositionSaver;
import icy.roi.ROI2D;
import icy.roi.ROIEvent;
import icy.roi.ROIEvent.ROIEventType;
import icy.roi.ROIListener;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceEvent.SequenceEventSourceType;
import icy.sequence.SequenceEvent.SequenceEventType;
import icy.sequence.SequenceListener;

import java.awt.Color;
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
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import plugins.nherve.photomontage.roi.AspectRatioPhotoMontageROI;
import plugins.nherve.photomontage.roi.FixedSizePhotoMontageROI;
import plugins.nherve.photomontage.roi.PhotoMontageROI;
import plugins.nherve.toolbox.NherveToolbox;
import plugins.nherve.toolbox.plugin.PainterManagerSingletonPlugin;

public class PhotoMontage extends PainterManagerSingletonPlugin<PhotoMontagePainter> implements ActionListener, DocumentListener, ROIListener, SequenceListener, ChangeListener {
	private final static String PLUGIN_NAME = "Photo Montage";
	private final static String PLUGIN_VERSION = "1.0.0";
	private final static String FULL_PLUGIN_NAME = PLUGIN_NAME + " V" + PLUGIN_VERSION;
	private final static String PREFERENCES_NODE = "icy/plugins/nherve/photomontage/PhotoMontage";

	private final static String NONE = "none";
	private final static String NA = "n.a.";
	private final static String CM = "cm";
	private final static double CM_PER_INCH = 2.54d;

	private IcyFrame frame;
	private JTextField tfDPI;
	private JLabel lbImageW;
	private JLabel lbImageH;
	private JLabel lbCurrentImage;
	private JTextField tfARROIW;
	private JTextField tfARROIH;
	private JTextField tfFSROIW;
	private JTextField tfFSROIH;
	private JLabel lbCurrentRatio;
	private JButton btCreateARROI;
	private JButton btCreateFSROI;
	private JButton btSwitchARROI;
	private JButton btSwitchFSROI;
	private JSlider slOpacity;
	private JButton btWallColor;
	private JButton btFrameColor;

	private double currentRatio;

	private DecimalFormat df;
	
	private boolean stopping;

	public PhotoMontage() {
		super();

		df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.FRANCE));
		stopping = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JButton) {
			JButton b = (JButton) o;

			if (b == btCreateARROI) {
				createAspectRatioROI();
				return;
			}
			
			if (b == btCreateFSROI) {
				createFixedSizeROI();
				return;
			}
			
			if (b == btSwitchARROI) {
				String tmp = tfARROIH.getText();
				tfARROIH.setText(tfARROIW.getText());
				tfARROIW.setText(tmp);
				return;
			}
			
			if (b == btSwitchFSROI) {
				String tmp = tfFSROIH.getText();
				tfFSROIH.setText(tfFSROIW.getText());
				tfFSROIW.setText(tmp);
				return;
			}

			if (b == btWallColor) {
				btWallColor.setBackground(JColorChooser.showDialog(frame.getFrame(), "Choose current wall color", btWallColor.getBackground()));
				updatePainter();
				return;
			}
			
			if (b == btFrameColor) {
				btFrameColor.setBackground(JColorChooser.showDialog(frame.getFrame(), "Choose current frame color", btFrameColor.getBackground()));
				updateROIs();
				return;
			}
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		JTextField tf = whichTextField(e);
		if ((tf == tfARROIH) || (tf == tfARROIW)) {
			updateRatio();
		} else if (tf == tfDPI) {
			updateDPI();
		}
	}

	private double convertCmToPix(double cm, double dpi) {
		return dpi * cm / CM_PER_INCH;
	}

	private double convertPixToCm(int pix, double dpi) {
		return (double) pix * CM_PER_INCH / dpi;
	}
	
	private void createAspectRatioROI() {
		if (hasCurrentSequence()) {
			Sequence s = getCurrentSequence();

			double x = 0d;
			double y = 0d;
			
			double w = s.getWidth() / 5d;

			double h = w / currentRatio;

			Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
			PhotoMontageROI roi = new AspectRatioPhotoMontageROI(r, currentRatio);

			roi.setColor(btFrameColor.getBackground());
			roi.attachTo(s);
			roi.addListener(this);
		}
	}

	private void createFixedSizeROI() {
		if (hasCurrentSequence()) {
			Sequence s = getCurrentSequence();

			double x = 0d;
			double y = 0d;
			double w = 0d;
			double h = 0d;
			double dpi = 0d;
			
			try {
				dpi = Double.parseDouble(tfDPI.getText());
				w = Double.parseDouble(tfFSROIW.getText());
				w = convertCmToPix(w, dpi);
				h = Double.parseDouble(tfFSROIH.getText());
				h = convertCmToPix(h, dpi);
			} catch (NumberFormatException e) {
				// ignore
			}
			
			if ((w != 0) && (h != 0) && (dpi!= 0)) {
				Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
				PhotoMontageROI roi = new FixedSizePhotoMontageROI(r, dpi);

				roi.setColor(btFrameColor.getBackground());
				roi.attachTo(s);
				roi.addListener(this);
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

	public Color getWallColor() {
		return btWallColor.getBackground();
	}
	
	public Color getFrameColor() {
		return btFrameColor.getBackground();
	}

	public float getCurrentOpacity() {
		return slOpacity.getValue() / 100f;
	}

	@Override
	public String getPainterName() {
		return PhotoMontagePainter.class.getName();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void roiChanged(ROIEvent event) {
		if (event.getType() == ROIEventType.ROI_CHANGED) {
			updatePainter();
		}
	}

	@Override
	public void sequenceChanged(SequenceEvent sequenceEvent) {
		if (!stopping && (sequenceEvent.getSourceType() == SequenceEventSourceType.SEQUENCE_ROI) && (sequenceEvent.getType() == SequenceEventType.REMOVED)) {
			updatePainter();
		}
		if ((sequenceEvent.getSourceType() == SequenceEventSourceType.SEQUENCE_ROI) && (sequenceEvent.getType() == SequenceEventType.ADDED)) {
			updatePainter();
		}
	}

	@Override
	public void sequenceClosed(Sequence sequence) {

	}

	@Override
	public void sequenceHasChangedAfterSettingPainter() {
		if (hasCurrentSequence()) {
			lbCurrentImage.setText(getCurrentSequence().getFilename());
			getCurrentSequence().addListener(this);
		} else {
			lbCurrentImage.setText(NONE);
		}

		updateDPI();
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

		lbCurrentImage = new JLabel(NONE);
		lbImageW = new JLabel(NA);
		lbImageH = new JLabel(NA);
		tfDPI = new JTextField(Integer.toString(150));
		ComponentUtil.setFixedHeight(tfDPI, 25);
		JPanel p1a = GuiUtil.createLineBoxPanel(lbCurrentImage, Box.createHorizontalGlue());
		JPanel p1b = GuiUtil.createLineBoxPanel(new JLabel("DPI"), tfDPI, Box.createHorizontalGlue(), new JLabel("W : "), lbImageW, Box.createHorizontalGlue(), new JLabel("H : "), lbImageH, Box.createHorizontalGlue());
		JPanel p1 = GuiUtil.createPageBoxPanel(p1a, p1b);
		p1.setBorder(new TitledBorder("Current image"));
		mainPanel.add(p1);

		btCreateARROI = new JButton("Create");
		btCreateARROI.addActionListener(this);
		
		btCreateFSROI = new JButton("Create");
		btCreateFSROI.addActionListener(this);
		
		btSwitchARROI = new JButton(NherveToolbox.switchIcon);
		btSwitchARROI.setToolTipText("Switch");
		btSwitchARROI.addActionListener(this);
		
		btSwitchFSROI = new JButton(NherveToolbox.switchIcon);
		btSwitchARROI.setToolTipText("Switch");
		btSwitchFSROI.addActionListener(this);

		slOpacity = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		slOpacity.addChangeListener(this);
		slOpacity.setMajorTickSpacing(10);
		slOpacity.setMinorTickSpacing(2);
		slOpacity.setPaintTicks(true);

		btWallColor = new JButton();
		ComponentUtil.setFixedSize(btWallColor, new Dimension(50, 22));
		btWallColor.setToolTipText("Change wall color");
		btWallColor.setBackground(Color.WHITE);
		btWallColor.addActionListener(this);
		
		btFrameColor = new JButton();
		ComponentUtil.setFixedSize(btFrameColor, new Dimension(50, 22));
		btFrameColor.setToolTipText("Change frame color");
		btFrameColor.setBackground(Color.BLACK);
		btFrameColor.addActionListener(this);

		JPanel p3 = GuiUtil.createLineBoxPanel(slOpacity, Box.createHorizontalGlue(), btWallColor, Box.createHorizontalGlue(), btFrameColor, Box.createHorizontalGlue());
		p3.setBorder(new TitledBorder("Visualization options"));
		mainPanel.add(p3);

		tfARROIW = new JTextField(Integer.toString(3));
		ComponentUtil.setFixedHeight(tfARROIW, 25);
		tfARROIH = new JTextField(Integer.toString(2));
		ComponentUtil.setFixedHeight(tfARROIH, 25);
		JLabel lbR = new JLabel("Ratio : ");
		lbCurrentRatio = new JLabel();

		updateRatio();

		JPanel p2 = GuiUtil.createLineBoxPanel(new JLabel("W"), tfARROIW, Box.createHorizontalGlue(), btSwitchARROI, Box.createHorizontalGlue(), new JLabel("H"), tfARROIH, Box.createHorizontalGlue(), lbR, lbCurrentRatio, Box.createHorizontalGlue(), btCreateARROI);
		p2.setBorder(new TitledBorder("New Aspect Ratio ROI"));
		mainPanel.add(p2);
		
		tfFSROIW = new JTextField(Integer.toString(15));
		ComponentUtil.setFixedHeight(tfFSROIW, 25);
		tfFSROIH = new JTextField(Integer.toString(10));
		ComponentUtil.setFixedHeight(tfFSROIH, 25);
		
		JPanel p4 = GuiUtil.createLineBoxPanel(new JLabel("W"), tfFSROIW, new JLabel(CM), Box.createHorizontalGlue(), btSwitchFSROI, Box.createHorizontalGlue(), new JLabel("H"), tfFSROIH, new JLabel(CM), Box.createHorizontalGlue(), btCreateFSROI);
		p4.setBorder(new TitledBorder("New Fixed Size ROI"));
		mainPanel.add(p4);

		tfARROIW.getDocument().addDocumentListener(this);
		tfARROIH.getDocument().addDocumentListener(this);
		tfDPI.getDocument().addDocumentListener(this);

		frame.addFrameListener(this);
		frame.setVisible(true);
		frame.pack();

		frame.requestFocus();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JSlider) {
			JSlider s = (JSlider) e.getSource();

			if (s == slOpacity) {
				updatePainter();
			}
		}
	}

	@Override
	public void stopInterface() {
		stopping = true;
		removeROIsFromAllSequences();
	}
	
	private void updateDPI() {
		if (hasCurrentSequence()) {
			try {
				int w = getCurrentSequence().getWidth();
				int h = getCurrentSequence().getHeight();
				double dpi = Double.parseDouble(tfDPI.getText());
				if (Math.abs(dpi) > 0.1d) {
					lbImageW.setText(df.format(convertPixToCm(w, dpi)) + " " + CM);
					lbImageH.setText(df.format(convertPixToCm(h, dpi)) + " " + CM);
					return;
				}
			} catch (NumberFormatException e) {
				// ignore
			}
		}

		lbImageW.setText(NA);
		lbImageH.setText(NA);
	}

	private void updateROIs() {
		for(Sequence s : getSequences()) {
			for (ROI2D roi : s.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					roi.setColor(btFrameColor.getBackground());
				}
			}
		}
	}
	
	private void updatePainter() {
		if (hasCurrentSequence()) {
			getCurrentSequencePainter().setNeedRedraw(true);
			getCurrentSequence().painterChanged(null);
		}
	}

	private void updateRatio() {
		try {
			double w = Double.parseDouble(tfARROIW.getText());
			double h = Double.parseDouble(tfARROIH.getText());

			if ((h != 0) && (w != 0)) {
				currentRatio = w / h;
				lbCurrentRatio.setText(df.format(currentRatio));
				btCreateARROI.setEnabled(true);
				btCreateFSROI.setEnabled(true);
				return;
			}
		} catch (NumberFormatException e) {
			// ignore
		}

		lbCurrentRatio.setText(NA);
		btCreateARROI.setEnabled(false);
		btCreateFSROI.setEnabled(false);
	}

	private JTextField whichTextField(DocumentEvent e) {
		if (e.getDocument() == tfDPI.getDocument()) {
			return tfDPI;
		}
		if (e.getDocument() == tfARROIH.getDocument()) {
			return tfARROIH;
		}
		if (e.getDocument() == tfARROIW.getDocument()) {
			return tfARROIW;
		}
		return null;
	}
	
	private void removeROIsFromAllSequences() {
		for(Sequence s : getSequences()) {
			for (ROI2D roi : s.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					s.removeROI(roi);
				}
			}
		}
	}

}
