package plugins.nherve.photomontage.roi;

import java.awt.geom.Rectangle2D;

public class StandardROI extends PhotoMontageROI {

	public StandardROI(Rectangle2D r) {
		super(r);
		setName("StandardROI");
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		StandardROI roi = new StandardROI(getBounds2D());
		return roi;
	}

}
