package com.cyzapps.VisualMFP;

import java.util.LinkedList;

public class DataSeriesCurve extends DataSeries {
	public LineStyle mlineStyle = new LineStyle();
	public PointStyle mpointStyle = new PointStyle();

	public DataSeriesCurve()	{
		menumDataSeriesType = DATASERIESTYPES.DATASERIES_CURVE;
	}

	@Override
	protected void connect(MultiLinkedPoint mlp) {
		if (mlistData.size() > 0)	{
			mlistData.getLast().msetConnects.add(mlp);
			mlp.msetConnects.add(mlistData.getLast());
		}
	}
	
}
