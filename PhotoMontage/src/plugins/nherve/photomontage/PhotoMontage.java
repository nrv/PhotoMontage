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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import plugins.nherve.photomontage.roi.AspectRatioROI;
import plugins.nherve.photomontage.roi.FixedSizeROI;
import plugins.nherve.photomontage.roi.Link;
import plugins.nherve.photomontage.roi.PhotoMontageROI;
import plugins.nherve.photomontage.roi.StandardROI;
import plugins.nherve.toolbox.NherveToolbox;
import plugins.nherve.toolbox.plugin.PainterManagerSingletonPlugin;

public class PhotoMontage extends PainterManagerSingletonPlugin<PhotoMontagePainter> implements ActionListener, DocumentListener, ROIListener, SequenceListener, ChangeListener, ItemListener {
	private final static String PLUGIN_NAME = "Photo Montage";
	private final static String PLUGIN_VERSION = "1.0.0";
	private final static String FULL_PLUGIN_NAME = PLUGIN_NAME + " V" + PLUGIN_VERSION;
	private final static String PREFERENCES_NODE = "icy/plugins/nherve/photomontage/PhotoMontage";

	private final static String NONE = "none";
	private final static String NA = "n.a.";
	public final static String CM = " cm";
	private final static double CM_PER_INCH = 2.54d;

	private static DecimalFormat df;

	static {
		df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance());
	}

	public static double convertCmToPix(double cm, double dpi) {
		return dpi * cm / CM_PER_INCH;
	}

	public static double convertPixToCm(double pix, double dpi) {
		if (dpi != 0) {
			return pix * CM_PER_INCH / dpi;
		}
		return 0;
	}

	public static double convertToDPI(double pix, double cm) {
		if (cm != 0) {
			return pix * CM_PER_INCH / cm;
		}
		return 0;
	}

	public static String format(double arg0) {
		return df.format(arg0);
	}

	private IcyFrame frame;
	private JTextField tfDPI;
	private JLabel lbImageW;
	private JLabel lbImageH;
	private JLabel lbCurrentImage;
	private JLabel lbCurrentROI;
	private JTextField tfARROIW;
	private JTextField tfARROIH;
	private JTextField tfFSROIW;
	private JTextField tfFSROIH;
	private JLabel lbCurrentRatio;
	private JButton btCreateARROI;
	private JButton btCreateFSROI;
	private JButton btCreateStdROI;
	private JButton btSwitchARROI;
	private JButton btSwitchFSROI;
	private JSlider slOpacity;
	private JButton btWallColor;
	private JButton btFrameIntColor;
	private JButton btFrameExtColor;

	private JButton btDuplicateROI;

	
	private JButton btLoad;
	private JButton btSave;
	private JButton btProcess;
	
	private JTextField tfFrameThick1;
	private JTextField tfFrameThick2;

	private JCheckBox cbShowLines;

	private double currentRatio;

	private boolean stopping;

	public PhotoMontage() {
		super();

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

			if (b == btCreateStdROI) {
				createStandardROI();
				return;
			}

			if (b == btDuplicateROI) {
				duplicateROI();
				return;
			}
			
			if (b == btLoad) {
				return;
			}
			
			if (b == btSave) {
				return;
			}
			
			if (b == btProcess) {
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
				updatePainter(true);
				return;
			}

			if (b == btFrameIntColor) {
				btFrameIntColor.setBackground(JColorChooser.showDialog(frame.getFrame(), "Choose current frame interior color", btFrameIntColor.getBackground()));
				updateROIsColor();
				updatePainter(true);
				return;
			}

			if (b == btFrameExtColor) {
				btFrameExtColor.setBackground(JColorChooser.showDialog(frame.getFrame(), "Choose current frame exterior color", btFrameExtColor.getBackground()));
				updatePainter(true);
				return;
			}

		}
	}

	private void addToCurrentSequence(PhotoMontageROI roi) {
		if (hasCurrentSequence()) {
			roi.setColor(btFrameIntColor.getBackground());
			roi.attachTo(getCurrentSequence());
			roi.addListener(this);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		JTextField tf = whichTextField(e);
		if ((tf == tfARROIH) || (tf == tfARROIW)) {
			updateRatio();
		} else if (tf == tfDPI) {
			updateDPI();
		} else if ((tf == tfFrameThick1) || (tf == tfFrameThick2)) {
			updatePainter(true);
		}
	}

	private void createAspectRatioROI() {
		if (hasCurrentSequence()) {
			Sequence s = getCurrentSequence();

			double x = 0d;
			double y = 0d;

			double w = s.getWidth() / 5d;

			double h = w / currentRatio;

			Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
			PhotoMontageROI roi = new AspectRatioROI(r, currentRatio);

			addToCurrentSequence(roi);
		}
	}

	private void createFixedSizeROI() {
		if (hasCurrentSequence()) {
			double x = 0d;
			double y = 0d;
			double w = 0d;
			double h = 0d;
			double dpi = 0d;

			try {
				dpi = getDPI();
				w = df.parse(tfFSROIW.getText()).doubleValue();
				w = convertCmToPix(w, dpi);
				h = df.parse(tfFSROIH.getText()).doubleValue();
				h = convertCmToPix(h, dpi);
			} catch (ParseException e) {
				// ignore
			}

			if ((w != 0) && (h != 0) && (dpi != 0)) {
				Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
				PhotoMontageROI roi = new FixedSizeROI(r);

				addToCurrentSequence(roi);
			}
		}
	}

	List<Link> createLinks(Sequence s) {
		ArrayList<PhotoMontageROI> rois = new ArrayList<PhotoMontageROI>();

		for (ROI2D roi : s.getROI2Ds()) {
			if (roi instanceof PhotoMontageROI) {
				rois.add((PhotoMontageROI) roi);
			}
		}

		List<Link> links = new ArrayList<Link>();

		try {
			double thick = getThickness1() + getThickness2();

			for (int i = 0; i < rois.size() - 1; i++) {
				for (int j = i + 1; j < rois.size(); j++) {
					Link link = rois.get(i).getLink(rois.get(j), thick);
					if (link != null) {
						boolean add = true;
						for (int k = 0; k < rois.size(); k++) {
							if ((k != i) && (k != j) && (link.intersects(rois.get(k).getRectangle()))) {
								add = false;
								break;
							}
						}
						if (add) {
							links.add(link);
						}
					}
				}
			}
		} catch (ParseException e) {
			// ignore
		}

		return links;
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

	private void createStandardROI() {
		if (hasCurrentSequence()) {
			double x = 0d;
			double y = 0d;

			Sequence s = getCurrentSequence();
			double w = s.getWidth() / 5d;
			double h = s.getHeight() / 5d;

			if ((w != 0) && (h != 0)) {
				Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
				StandardROI roi = new StandardROI(r);

				addToCurrentSequence(roi);
			}
		}
	}

	private void duplicateROI() {
		try {
			if (hasCurrentSequence()) {
				Sequence s = getCurrentSequence();
				PhotoMontageROI roi = getSelectedROI();
				if (roi != null) {
					roi = (PhotoMontageROI) roi.clone();
					double dx = s.getWidth() / 10d;
					double dy = s.getHeight() / 10d;
					roi.translate(dx, dy);
					addToCurrentSequence(roi);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public float getCurrentOpacity() {
		return slOpacity.getValue() / 100f;
	}

	public double getDPI() throws ParseException {
		return df.parse(tfDPI.getText()).doubleValue();
	}

	public Color getFrameExtColor() {
		return btFrameExtColor.getBackground();
	}

	public Color getFrameIntColor() {
		return btFrameIntColor.getBackground();
	}

	@Override
	public String getPainterName() {
		return PhotoMontagePainter.class.getName();
	}

	private PhotoMontageROI getSelectedROI() {
		if (hasCurrentSequence()) {
			for (ROI2D roi : getCurrentSequence().getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					if (roi.isSelected()) {
						return (PhotoMontageROI) roi;
					}
				}
			}
		}
		return null;
	}

	public double getThickness1() throws ParseException {
		return convertCmToPix(df.parse(tfFrameThick1.getText()).doubleValue(), getDPI());
	}

	public double getThickness2() throws ParseException {
		return convertCmToPix(df.parse(tfFrameThick2.getText()).doubleValue(), getDPI());
	}

	public Color getWallColor() {
		return btWallColor.getBackground();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object o = e.getSource();

		if (o == null) {
			return;
		}

		if (o instanceof JCheckBox) {
			JCheckBox c = (JCheckBox) e.getSource();

			if (c == cbShowLines) {
				updatePainter(false);
			}
		}
	}

	private void removeROIsFromAllSequences() {
		for (Sequence s : getSequences()) {
			for (ROI2D roi : s.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					s.removeROI(roi);
				}
			}
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void roiChanged(ROIEvent event) {
		if (event.getType() == ROIEventType.ROI_CHANGED) {
			updatePainter(true);
		}
	}

	@Override
	public void sequenceChanged(SequenceEvent sequenceEvent) {
		if (sequenceEvent.getSourceType() == SequenceEventSourceType.SEQUENCE_ROI) {
			if (!stopping && (sequenceEvent.getType() == SequenceEventType.REMOVED)) {
				updatePainter(true);
			}
			if (sequenceEvent.getType() == SequenceEventType.ADDED) {
				updatePainter(true);
			}
			updateSelectedROI();
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

		updateSelectedROI();
		updateDPI();
	}

	@Override
	public void sequenceHasChangedBeforeSettingPainter() {
	}

	@Override
	public void sequenceWillChange() {
	}

	public boolean showLines() {
		return cbShowLines.isSelected();
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

		btCreateStdROI = new JButton("Create");
		btCreateStdROI.addActionListener(this);

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

		btFrameIntColor = new JButton();
		ComponentUtil.setFixedSize(btFrameIntColor, new Dimension(50, 22));
		btFrameIntColor.setToolTipText("Change frame interior color");
		btFrameIntColor.setBackground(Color.WHITE);
		btFrameIntColor.addActionListener(this);

		btFrameExtColor = new JButton();
		ComponentUtil.setFixedSize(btFrameExtColor, new Dimension(50, 22));
		btFrameExtColor.setToolTipText("Change frame exterior color");
		btFrameExtColor.setBackground(Color.BLACK);
		btFrameExtColor.addActionListener(this);

		cbShowLines = new JCheckBox("Show lines");
		cbShowLines.setSelected(true);
		cbShowLines.addItemListener(this);

		tfFrameThick1 = new JTextField(format(0.25));
		ComponentUtil.setFixedHeight(tfFrameThick1, 25);
		tfFrameThick2 = new JTextField(format(1));
		ComponentUtil.setFixedHeight(tfFrameThick2, 25);

		JPanel p3a = GuiUtil.createLineBoxPanel(slOpacity, Box.createHorizontalGlue(), cbShowLines, Box.createHorizontalGlue(), btWallColor, Box.createHorizontalGlue(), btFrameIntColor, Box.createHorizontalGlue(), btFrameExtColor, Box.createHorizontalGlue());
		JPanel p3b = GuiUtil.createLineBoxPanel(new JLabel("Thick 1"), tfFrameThick1, new JLabel(CM), Box.createHorizontalGlue(), new JLabel("Thick 2"), tfFrameThick2, new JLabel(CM));
		JPanel p3 = GuiUtil.createPageBoxPanel(p3a, p3b);
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

		tfFSROIW = new JTextField(format(15));
		ComponentUtil.setFixedHeight(tfFSROIW, 25);
		tfFSROIH = new JTextField(format(10));
		ComponentUtil.setFixedHeight(tfFSROIH, 25);

		JPanel p4 = GuiUtil.createLineBoxPanel(new JLabel("W"), tfFSROIW, new JLabel(CM), Box.createHorizontalGlue(), btSwitchFSROI, Box.createHorizontalGlue(), new JLabel("H"), tfFSROIH, new JLabel(CM), Box.createHorizontalGlue(), btCreateFSROI);
		p4.setBorder(new TitledBorder("New Fixed Size ROI"));
		mainPanel.add(p4);

		JPanel p6 = GuiUtil.createLineBoxPanel(Box.createHorizontalGlue(), btCreateStdROI);
		p6.setBorder(new TitledBorder("New Standard ROI"));
		mainPanel.add(p6);

		lbCurrentROI = new JLabel(NONE);
		btDuplicateROI = new JButton("Duplicate");
		btDuplicateROI.addActionListener(this);
		JPanel p5 = GuiUtil.createLineBoxPanel(lbCurrentROI, Box.createHorizontalGlue(), btDuplicateROI);
		p5.setBorder(new TitledBorder("Current ROI"));
		mainPanel.add(p5);
		
		
		btLoad = new JButton("Load");
		btLoad.addActionListener(this);
		btLoad.setEnabled(false);
		
		btSave = new JButton("Save");
		btSave.addActionListener(this);
		btSave.setEnabled(false);
		
		btProcess = new JButton("Process");
		btProcess.addActionListener(this);
		
		JPanel p7 = GuiUtil.createLineBoxPanel(btLoad, Box.createHorizontalGlue(), btSave, Box.createHorizontalGlue(), btProcess);
		p7.setBorder(new TitledBorder("Global actions"));
		mainPanel.add(p7);

		tfARROIW.getDocument().addDocumentListener(this);
		tfARROIH.getDocument().addDocumentListener(this);
		tfDPI.getDocument().addDocumentListener(this);
		tfFrameThick1.getDocument().addDocumentListener(this);
		tfFrameThick2.getDocument().addDocumentListener(this);

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
				updatePainter(true);
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
				double dpi = getDPI();
				if (Math.abs(dpi) > 0.1d) {
					lbImageW.setText(format(convertPixToCm(w, dpi)) + CM);
					lbImageH.setText(format(convertPixToCm(h, dpi)) + CM);
					return;
				}
			} catch (ParseException e) {
				// ignore
			}
		}

		lbImageW.setText(NA);
		lbImageH.setText(NA);
	}

	private void updatePainter(boolean redraw) {
		if (hasCurrentSequence()) {
			if (redraw) {
				getCurrentSequencePainter().setNeedRedraw(true);
			}
			getCurrentSequence().painterChanged(null);
		}
	}

	private void updateRatio() {
		try {
			double w = df.parse(tfARROIW.getText()).doubleValue();
			double h = df.parse(tfARROIH.getText()).doubleValue();

			if ((h != 0) && (w != 0)) {
				currentRatio = w / h;
				lbCurrentRatio.setText(format(currentRatio));
				btCreateARROI.setEnabled(true);
				btCreateFSROI.setEnabled(true);
				return;
			}
		} catch (ParseException e) {
			// ignore
		}

		lbCurrentRatio.setText(NA);
		btCreateARROI.setEnabled(false);
		btCreateFSROI.setEnabled(false);
	}

	private void updateROIsColor() {
		for (Sequence s : getSequences()) {
			for (ROI2D roi : s.getROI2Ds()) {
				if (roi instanceof PhotoMontageROI) {
					roi.setColor(btFrameIntColor.getBackground());
				}
			}
		}
	}

	private void updateSelectedROI() {
		try {
			PhotoMontageROI roi = getSelectedROI();
			if (roi != null) {
				double dpi = getDPI();
				Rectangle2D r = roi.getBounds2D();
				String w = format(convertPixToCm(r.getWidth(), dpi));
				String h = format(convertPixToCm(r.getHeight(), dpi));
				String dpix = format(convertToDPI(r.getWidth(), df.parse(tfFSROIW.getText()).doubleValue()));
				String dpiy = format(convertToDPI(r.getHeight(), df.parse(tfFSROIH.getText()).doubleValue()));
				lbCurrentROI.setText(roi.getName() + " | " + w + CM + " x " + h + CM + " | DPI (" + dpix + " - " + dpiy + ")");
				return;
			}
		} catch (ParseException e) {
			// ignore
		}

		lbCurrentROI.setText(NONE);
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
		if (e.getDocument() == tfFSROIH.getDocument()) {
			return tfFSROIH;
		}
		if (e.getDocument() == tfFSROIW.getDocument()) {
			return tfFSROIW;
		}
		if (e.getDocument() == tfFrameThick1.getDocument()) {
			return tfFrameThick1;
		}
		if (e.getDocument() == tfFrameThick2.getDocument()) {
			return tfFrameThick2;
		}
		return null;
	}

}
