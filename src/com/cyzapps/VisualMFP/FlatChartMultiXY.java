package com.cyzapps.VisualMFP;

import java.util.LinkedList;

public class FlatChartMultiXY {
	
	public String mstrTitle = "";
	
	public String mstrAxisXNames = "";
	public String mstrAxisYNames = "";
	
	public CoordAxis mcaX = new CoordAxis();
	public CoordAxis mcaY = new CoordAxis();
	
	LinkedList<DataSeries> mlistDataSeries = new LinkedList<DataSeries>();
	
	public String[] marraystrLegends = new String[2];
	
	public PointMapper mpointMapper = new PointMapper();

}
