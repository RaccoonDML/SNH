package com.cyzapps.VisualMFP;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class DataSeries {
	
	static public enum DATASERIESTYPES
	{
		DATASERIES_INVALID(0),
		DATASERIES_CURVE(1),
		DATASERIES_XYSURFACE(2),
		DATASERIES_OTHERS(1000);	// we have type invalid, so type others is no longer needed.
		
		
		private int value; 

		private DATASERIESTYPES(int i) { 
			value = i; 
		} 

		public int getValue() { 
			return value; 
		}
	};

	public DATASERIESTYPES menumDataSeriesType = DATASERIESTYPES.DATASERIES_INVALID;
	
	public String mstrName = "";

	// use list instead of set so that we can access the mulilinkedpoint that was last added.
	public LinkedList<MultiLinkedPoint> mlistData = new LinkedList<MultiLinkedPoint>();
	public LinkedList<MultiLinkedPoint> mlistDataSortedCvtedX = new LinkedList<MultiLinkedPoint>();	// from smallest to largest
	public LinkedList<MultiLinkedPoint> mlistDataSortedCvtedY = new LinkedList<MultiLinkedPoint>();
	public LinkedList<MultiLinkedPoint> mlistDataSortedCvtedZ = new LinkedList<MultiLinkedPoint>();
		
	public DataSeries()	{
	}

	protected abstract void connect(MultiLinkedPoint mlp);	// this function is called in add(Position3D) function and before the position3d is added into mlistData
	
	protected void disconnect(MultiLinkedPoint mlp)	{
		for (MultiLinkedPoint itr : mlp.msetConnects)	{
			itr.msetConnects.remove(mlp);
		}
	}

	public static int insertIntoSortedList(LinkedList<MultiLinkedPoint> listMLPnts, MultiLinkedPoint mlp, boolean bCvtedValue, int nSortBy)	{
		// assume dCompValue is not Nan nor Inf.
		double dCompValue = mlp.getPoint(bCvtedValue).getDim(nSortBy);

		int nOriginalSize = listMLPnts.size();
		if (nOriginalSize == 0)	{
			listMLPnts.add(mlp);
			return 0;
		} else if (dCompValue < listMLPnts.get(0).getPoint(bCvtedValue).getDim(nSortBy))	{
			listMLPnts.addFirst(mlp);
			return 0;
		} else if (dCompValue >= listMLPnts.get(nOriginalSize - 1).getPoint(bCvtedValue).getDim(nSortBy))	{
			listMLPnts.add(mlp);
			return nOriginalSize;
		} else	{
			int idxLeft = 0, idx = (nOriginalSize - 1)/2, idxRight = nOriginalSize - 1;
			while (idx != idxLeft)	{
				if (dCompValue < listMLPnts.get(idx).getPoint(bCvtedValue).getDim(nSortBy))	{
					idxRight = idx;
					idx = (idxRight + idxLeft)/2;
				} else	{	// if (dCompValue >= listMLPnts.get(idx).mpnt.getDim(nSortBy))	{
					idxLeft = idx;
					idx = (idxRight + idxLeft)/2;
				}
			}
			listMLPnts.add(idxLeft + 1, mlp);
			return idxLeft + 1;
		}
	}

	public MultiLinkedPoint add(Position3D pnt, Position3D pntCvted) {
		MultiLinkedPoint mlp = new MultiLinkedPoint(pnt, pntCvted, new HashSet<MultiLinkedPoint>());
		connect(mlp);
		mlistData.add(mlp);		
		
		if (MathLib.isValidReal(pntCvted.getX()) && MathLib.isValidReal(pntCvted.getY()) && MathLib.isValidReal(pntCvted.getZ()))	{
			insertIntoSortedList(mlistDataSortedCvtedX, mlp, true, 0);
			insertIntoSortedList(mlistDataSortedCvtedY, mlp, true, 1);
			insertIntoSortedList(mlistDataSortedCvtedZ, mlp, true, 2);
		}
		return mlp;
	}
	
	public MultiLinkedPoint add(Position3D pnt) {
		return add(pnt, pnt);
	}
	
	public Position3D remove(int idx)	{
		MultiLinkedPoint mlp = mlistData.remove(idx);
		disconnect(mlp);

		if (MathLib.isValidReal(mlp.mpntCvted.getX()) && MathLib.isValidReal(mlp.mpntCvted.getY()) && MathLib.isValidReal(mlp.mpntCvted.getZ()))	{
			for (int index = 0; index < mlistDataSortedCvtedX.size(); index ++)	{
				if (mlistDataSortedCvtedX.get(index) == mlp)	{
					mlistDataSortedCvtedX.remove(index);
					break;
				}
			}
			for (int index = 0; index < mlistDataSortedCvtedY.size(); index ++)	{
				if (mlistDataSortedCvtedY.get(index) == mlp)	{
					mlistDataSortedCvtedY.remove(index);
					break;
				}
			}
			for (int index = 0; index < mlistDataSortedCvtedZ.size(); index ++)	{
				if (mlistDataSortedCvtedZ.get(index) == mlp)	{
					mlistDataSortedCvtedZ.remove(index);
					break;
				}
			}
		}
		return mlp.mpntCvted;
	}
	
	public int getItemCount()	{
		return mlistData.size();
	}
	
	public double getMaxCvtedX()	{
		if (mlistDataSortedCvtedX.size() > 0)	{
			return mlistDataSortedCvtedX.getLast().mpntCvted.getX();
		}
		return Double.NaN;
	}
	
	public double getMinCvtedX()	{
		if (mlistDataSortedCvtedX.size() > 0)	{
			return mlistDataSortedCvtedX.getFirst().mpntCvted.getX();
		}
		return Double.NaN;
	}
	
	public double getMaxCvtedY()	{
		if (mlistDataSortedCvtedY.size() > 0)	{
			return mlistDataSortedCvtedY.getLast().mpntCvted.getY();
		}
		return Double.NaN;
	}
	
	public double getMinCvtedY()	{
		if (mlistDataSortedCvtedY.size() > 0)	{
			return mlistDataSortedCvtedY.getFirst().mpntCvted.getY();
		}
		return Double.NaN;
	}
	
	public double getMaxCvtedZ()	{
		if (mlistDataSortedCvtedZ.size() > 0)	{
			return mlistDataSortedCvtedZ.getLast().mpntCvted.getZ();
		}
		return Double.NaN;
	}
	
	public double getMinCvtedZ()	{
		if (mlistDataSortedCvtedZ.size() > 0)	{
			return mlistDataSortedCvtedZ.getFirst().mpntCvted.getZ();
		}
		return Double.NaN;
	}
}
