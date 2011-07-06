package plugins.nherve.photomontage;

import icy.plugin.abstract_.Plugin;
import icy.plugin.interface_.PluginROI;
import icy.roi.ROI;

import java.awt.geom.Point2D;

public class AspectRatioRectangle extends Plugin implements PluginROI {
	
	@Override
	public ROI createROI(Point2D pt, boolean cm) {
		return new ROI2DRectangleAspectRatio(pt, cm);
	}
}
