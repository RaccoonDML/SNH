package com.cyzapps.VisualMFP;

public class CoordAxis extends Line {

	public String mstrAxisName = "";
	
	public double mdValueFrom = 0;
	
	public double mdValueTo = 0;
	
    public double mdMarkInterval = 0;
    
	public double[] marraydScaleMarks = new double[0];
	
	public String[] marraystrMarkTxts = new String[0];
	
	// size and shape of arrow and market should be determined by chart
	
	public CoordAxis()	{
	}
	
	public CoordAxis(CoordAxis axis)	{
		super((CoordAxis)axis);
		mstrAxisName = axis.mstrAxisName;
		mdValueFrom = axis.mdValueFrom;
		mdValueTo = axis.mdValueTo;
		mdMarkInterval = axis.mdMarkInterval;
		marraydScaleMarks = new double[axis.marraydScaleMarks.length];
		for (int idx = 0; idx < axis.marraydScaleMarks.length; idx ++)	{
			marraydScaleMarks[idx] = axis.marraydScaleMarks[idx];
		}
		marraystrMarkTxts = new String[axis.marraystrMarkTxts.length];
		for (int idx = 0; idx < axis.marraystrMarkTxts.length; idx ++)	{
			marraystrMarkTxts[idx] = axis.marraystrMarkTxts[idx];
		}
	}
}
