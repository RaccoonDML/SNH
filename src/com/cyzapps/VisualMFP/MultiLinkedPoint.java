package com.cyzapps.VisualMFP;

import java.util.HashSet;
import java.util.Set;

public class MultiLinkedPoint {
	protected Position3D mpnt = new Position3D();	// mpnt is the original position3D, for example, for a ball, it is (r, theta, rho)
	protected Position3D mpntCvted = new Position3D();	// mpntCvted is the converted position3D, which will be drawn, for example, for
														// a ball, it is x = r * cos(rho) * cos(theta); y = r * cos(rho) * sin(theta);
														// z = r * sin(rho)
	
	public Set<MultiLinkedPoint> msetConnects = new HashSet<MultiLinkedPoint>();
	
	public MultiLinkedPoint()	{}
	public MultiLinkedPoint(Position3D pnt)	{
		mpntCvted = mpnt = pnt;
	}
	public MultiLinkedPoint(Position3D pnt, Set<MultiLinkedPoint> setConnects)	{
		mpntCvted = mpnt = pnt;
		msetConnects = setConnects;
	}

	public MultiLinkedPoint(Position3D pnt, Position3D pntCvted)	{
		mpnt = pnt;
		mpntCvted = pntCvted;
	}
	public MultiLinkedPoint(Position3D pnt, Position3D pntCvted, Set<MultiLinkedPoint> setConnects)	{
		mpnt = pnt;
		mpntCvted = pntCvted;
		msetConnects = setConnects;
	}

	public void setPoint(Position3D pnt, boolean bCvtedPnt)	{
		if (bCvtedPnt)	{
			mpntCvted = pnt;
		} else	{
			mpnt = pnt;
		}
	}
	
	public Position3D getPoint(boolean bCvtedPnt)	{
		if (bCvtedPnt)	{
			return mpntCvted;
		} else	{
			return mpnt;
		}
	}
	

	public void setOriginalPoint(Position3D pnt)	{
		mpnt = pnt;
	}
	
	public Position3D getOriginalPoint()	{
		return mpnt;
	}

	public void setConvertedPoint(Position3D pnt)	{
		mpntCvted = pnt;
	}
	
	public Position3D getConvertedPoint()	{
		return mpntCvted;
	}

	public void linkTo(MultiLinkedPoint mlp)	{
		for (MultiLinkedPoint itr : msetConnects)	{
			if (itr.mpnt.isEqual(mlp.mpnt))	{
				return;	// ok, this point has been linked to mlp
			}
		}
		// remember it is double link.
		msetConnects.add(mlp);
		mlp.msetConnects.add(this);
	}

	public int getNumberOfConnects()	{
		return msetConnects.size();
	}

}
