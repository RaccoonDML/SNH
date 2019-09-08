package com.cyzapps.VisualMFP;


import java.util.LinkedList;

public class DataSeriesGridSurface extends DataSeries {
	public SurfaceStyle msurfaceStyle = new SurfaceStyle();
	
	private int mnMode = 0;	// 0 or any other non-1 and non-2 value means xy gird, 1 means yz grid, 2 means zx grid,
							// note that mode here is only applied to point, not point converted. 

	public int getMode()	{return mnMode;}
	
	private int mnModeCvted = 0;	// 0 or any other non-1 and non-2 value means xy gird, 1 means yz grid, 2 means zx grid,
	// note that mode here is only applied to point converted, not point. 

	public int getModeCvted()	{return mnModeCvted;}

	public class LinkedDataCurve	{
		public double mdBaseValue = 0;
		public LinkedList<MultiLinkedPoint> mlistPnts = new LinkedList<MultiLinkedPoint>();

		LinkedDataCurve()	{
		}

		LinkedDataCurve(double dBaseValue)	{
			mdBaseValue = dBaseValue;
		}

		LinkedDataCurve(double dBaseValue, MultiLinkedPoint mlp)	{
			mdBaseValue = dBaseValue;
			mlistPnts.add(mlp);
		}

		LinkedDataCurve(double dBaseValue, LinkedList<MultiLinkedPoint> listPnts)	{
			mdBaseValue = dBaseValue;
			mlistPnts.addAll(listPnts);
		}
	}

	private LinkedList<LinkedDataCurve> mlistD1DataCurves = new LinkedList<LinkedDataCurve>();
	private LinkedList<LinkedDataCurve> mlistD2DataCurves = new LinkedList<LinkedDataCurve>();
    public LinkedList<LinkedList<MultiLinkedPoint>> mlistSurfaceElements = new LinkedList<LinkedList<MultiLinkedPoint>>();

	public DataSeriesGridSurface(String strMode)	{
		menumDataSeriesType = DATASERIESTYPES.DATASERIES_XYSURFACE;
		if (strMode == null)	{
			mnModeCvted = mnMode = 0;
		} else if (strMode.trim().compareToIgnoreCase("yz") == 0 || strMode.trim().compareToIgnoreCase("zy") == 0)	{
			mnModeCvted = mnMode = 1;
		} else if (strMode.trim().compareToIgnoreCase("zx") == 0 || strMode.trim().compareToIgnoreCase("xz") == 0)	{
			mnModeCvted = mnMode = 2;
		} else	{
			mnModeCvted = mnMode = 0;
		}
	}

	public DataSeriesGridSurface(String strMode, String strModeCvted)	{
		menumDataSeriesType = DATASERIESTYPES.DATASERIES_XYSURFACE;
		if (strMode == null)	{
			mnMode = 0;
		} else if (strMode.trim().compareToIgnoreCase("yz") == 0 || strMode.trim().compareToIgnoreCase("zy") == 0)	{
			mnMode = 1;
		} else if (strMode.trim().compareToIgnoreCase("zx") == 0 || strMode.trim().compareToIgnoreCase("xz") == 0)	{
			mnMode = 2;
		} else	{
			mnMode = 0;
		}
		if (strModeCvted == null)	{
			mnModeCvted = 0;
		} else if (strModeCvted.trim().compareToIgnoreCase("yz") == 0 || strModeCvted.trim().compareToIgnoreCase("zy") == 0)	{
			mnModeCvted = 1;
		} else if (strModeCvted.trim().compareToIgnoreCase("zx") == 0 || strModeCvted.trim().compareToIgnoreCase("xz") == 0)	{
			mnModeCvted = 2;
		} else	{
			mnModeCvted = 0;
		}
	}

	public void insertIntoDataCurves(LinkedList<LinkedDataCurve> listDataCurves, MultiLinkedPoint mlp, boolean bConnect)	{
		// assume dD1Value and dD2Value are not Nan nor Inf.
		double dD1Value = mlp.mpnt.getDim(mnMode);
		int nD2Dim = (mnMode == 2)?0:(mnMode == 1)?2:1;

		if (listDataCurves.size() == 0)	{
			// add a brand new linked data curve
			listDataCurves.add(new LinkedDataCurve(dD1Value, mlp));
		} else if (dD1Value < listDataCurves.get(0).mdBaseValue)	{
			listDataCurves.addFirst(new LinkedDataCurve(dD1Value, mlp));
		} else if (dD1Value > listDataCurves.getLast().mdBaseValue)	{
			listDataCurves.addLast(new LinkedDataCurve(dD1Value, mlp));
		} else	{
			int idxLeft = 0, idx = (listDataCurves.size() - 1)/2, idxRight = listDataCurves.size() - 1;
			while (idx != idxLeft)	{
				if (dD1Value < listDataCurves.get(idx).mdBaseValue)	{
					idxRight = idx;
					idx = (idxRight + idxLeft)/2;
				} else if (dD1Value > listDataCurves.get(idx).mdBaseValue)	{
					idxLeft = idx;
					idx = (idxRight + idxLeft)/2;
				} else	{	// find it.
					int index = insertIntoSortedList(listDataCurves.get(idx).mlistPnts, mlp, false, nD2Dim);
					if (index > 0 && bConnect)	{
						listDataCurves.get(idx).mlistPnts.get(index - 1).msetConnects.add(mlp);
						mlp.msetConnects.add(listDataCurves.get(idx).mlistPnts.get(index - 1));
					}
					if (index < listDataCurves.size() - 1 && bConnect)	{
						listDataCurves.get(idx).mlistPnts.get(index + 1).msetConnects.add(mlp);
						mlp.msetConnects.add(listDataCurves.get(idx).mlistPnts.get(index + 1));
					}
				}					
			}
			listDataCurves.add(idxLeft + 1, new LinkedDataCurve(dD1Value, mlp));
		}
	}

	@Override
	protected void connect(MultiLinkedPoint mlp) {
		double dD1Value = (mnMode == 2)?mlp.mpnt.getZ():(mnMode == 1)?mlp.mpnt.getY():mlp.mpnt.getX();
		double dD2Value = (mnMode == 2)?mlp.mpnt.getX():(mnMode == 1)?mlp.mpnt.getZ():mlp.mpnt.getY();

		if (MathLib.isValidReal(dD1Value) == false || MathLib.isValidReal(dD2Value) == false)	{
			return;	// this is not a valid position3d
		}
		
		// insert and connect
		insertIntoDataCurves(mlistD1DataCurves, mlp, true);
		insertIntoDataCurves(mlistD2DataCurves, mlp, true);
	}
		
	@Override
	protected void disconnect(MultiLinkedPoint mlp) {
		for (int idx = 0; idx < mlistD1DataCurves.size(); idx ++)	{
			boolean bRemoved = false;
			for (int idx1 = 0; idx1 < mlistD1DataCurves.get(idx).mlistPnts.size(); idx1 ++)	{
				if (mlistD1DataCurves.get(idx).mlistPnts.get(idx1) == mlp)	{
					mlistD1DataCurves.get(idx).mlistPnts.remove(idx1);
					if (mlistD1DataCurves.get(idx).mlistPnts.size() == 0)	{
						mlistD1DataCurves.remove(idx);
					} else	{
						if (idx1 > 0)	{
							mlistD1DataCurves.get(idx).mlistPnts.get(idx1 - 1).msetConnects.remove(mlp);
						}
						if (idx1 < mlistD1DataCurves.get(idx).mlistPnts.size())	{
							mlistD1DataCurves.get(idx).mlistPnts.get(idx1).msetConnects.remove(mlp);
						}
						if (idx1 > 0 && idx1 < mlistD1DataCurves.get(idx).mlistPnts.size())	{
							mlistD1DataCurves.get(idx).mlistPnts.get(idx1 - 1).msetConnects
								.add(mlistD1DataCurves.get(idx).mlistPnts.get(idx1));
							mlistD1DataCurves.get(idx).mlistPnts.get(idx1).msetConnects
								.add(mlistD1DataCurves.get(idx).mlistPnts.get(idx1 - 1));
						}
					}
					bRemoved = true;
					break;
				}
			}
			if (bRemoved)	{
				break;
			}
		}
		for (int idx = 0; idx < mlistD2DataCurves.size(); idx ++)	{
			boolean bRemoved = false;
			for (int idx1 = 0; idx1 < mlistD2DataCurves.get(idx).mlistPnts.size(); idx1 ++)	{
				if (mlistD2DataCurves.get(idx).mlistPnts.get(idx1) == mlp)	{
					mlistD2DataCurves.get(idx).mlistPnts.remove(idx1);
					if (mlistD2DataCurves.get(idx).mlistPnts.size() == 0)	{
						mlistD2DataCurves.remove(idx);
					} else	{
						if (idx1 > 0)	{
							mlistD2DataCurves.get(idx).mlistPnts.get(idx1 - 1).msetConnects.remove(mlp);
						}
						if (idx1 < mlistD2DataCurves.get(idx).mlistPnts.size())	{
							mlistD2DataCurves.get(idx).mlistPnts.get(idx1).msetConnects.remove(mlp);
						}
						if (idx1 > 0 && idx1 < mlistD2DataCurves.get(idx).mlistPnts.size())	{
							mlistD2DataCurves.get(idx).mlistPnts.get(idx1 - 1).msetConnects
								.add(mlistD2DataCurves.get(idx).mlistPnts.get(idx1));
							mlistD2DataCurves.get(idx).mlistPnts.get(idx1).msetConnects
								.add(mlistD2DataCurves.get(idx).mlistPnts.get(idx1 - 1));
						}
					}
					
					bRemoved = true;
					break;
				}
			}
			if (bRemoved)	{
				break;
			}
		}
		for (MultiLinkedPoint itr : mlp.msetConnects)	{
			itr.msetConnects.remove(mlp);
		}
	}

    // set up data series grid surface in a quick way, set the converted points.
    // assume arrayCvtedPnts is a 2D matrix
	public void setByMatrix(Position3D[][] arrayCvtedPnts)	{
        int nDim1 = arrayCvtedPnts.length;
        int nDim2 = arrayCvtedPnts[0].length;
        double[] arrayD1Values = new double[nDim1];
        for (int idx = 0; idx < nDim1; idx ++)  {
            arrayD1Values[idx] = idx;
        }
        double[] arrayD2Values = new double[nDim2];
        for (int idx = 0; idx < nDim2; idx ++)  {
            arrayD2Values[idx] = idx;
        }
        
        double[][] arrayD3Values = new double[nDim1][nDim2];
        for (int idx1 = 0; idx1 < nDim1; idx1 ++)   {
            for (int idx2 = 0; idx2 < nDim2; idx2 ++)   {
                arrayD3Values[idx1][idx2] = 0;
            }
        }
        setByMatrix(arrayD1Values, arrayD2Values, arrayD3Values, arrayCvtedPnts);
    }

	// set up data series grid surface in a quick way, set the converted points.
	public void setByMatrix(double[] arrayD1Values, double[] arrayD2Values, double[][] arrayD3Values, Position3D[][] arrayCvtedPnts)	{
		mlistD1DataCurves.clear();
		mlistD2DataCurves.clear();
		mlistData.clear();
		mlistDataSortedCvtedX.clear();
		mlistDataSortedCvtedY.clear();
		mlistDataSortedCvtedZ.clear();
		mlistSurfaceElements.clear();
		
		MultiLinkedPoint[][] arrayMlps = new MultiLinkedPoint[arrayD1Values.length][arrayD2Values.length];
		for (int idx1 = 0; idx1 < arrayD1Values.length; idx1 ++)	{
			for (int idx2 = 0; idx2 < arrayD2Values.length; idx2 ++)	{
				switch (mnMode)	{
				case 2:	//z - x grid
					arrayMlps[idx1][idx2] = new MultiLinkedPoint(
							new Position3D(arrayD2Values[idx2], arrayD3Values[idx1][idx2], arrayD1Values[idx1]),
							arrayCvtedPnts[idx1][idx2]
							);
					break;
				case 1: //y - z grid
					arrayMlps[idx1][idx2] = new MultiLinkedPoint(
							new Position3D(arrayD3Values[idx1][idx2], arrayD1Values[idx1], arrayD2Values[idx2]),
							arrayCvtedPnts[idx1][idx2]
							);
					break;
				default:	// x - y grid
					arrayMlps[idx1][idx2] = new MultiLinkedPoint(
							new Position3D(arrayD1Values[idx1], arrayD2Values[idx2], arrayD3Values[idx1][idx2]),
							arrayCvtedPnts[idx1][idx2]
							);
					break;
				}
				mlistData.add(arrayMlps[idx1][idx2]);
				if (MathLib.isValidReal(arrayCvtedPnts[idx1][idx2].getX())
						&& MathLib.isValidReal(arrayCvtedPnts[idx1][idx2].getY())
						&& MathLib.isValidReal(arrayCvtedPnts[idx1][idx2].getZ()))	{
					insertIntoSortedList(mlistDataSortedCvtedX, arrayMlps[idx1][idx2], true, 0);
					insertIntoSortedList(mlistDataSortedCvtedY, arrayMlps[idx1][idx2], true, 1);
					insertIntoSortedList(mlistDataSortedCvtedZ, arrayMlps[idx1][idx2], true, 2);
				}
			}
		}
		
		
		for (int idx1 = 0; idx1 < arrayD1Values.length; idx1 ++)	{
			LinkedDataCurve linkedDataCurve = new LinkedDataCurve(arrayD1Values[idx1]);
			for (int idx2 = 0; idx2 < arrayD2Values.length; idx2 ++)	{
				linkedDataCurve.mlistPnts.add(arrayMlps[idx1][idx2]);
				if (idx2 > 0)	{
					arrayMlps[idx1][idx2].linkTo(arrayMlps[idx1][idx2 - 1]);
					arrayMlps[idx1][idx2 - 1].linkTo(arrayMlps[idx1][idx2]);
				}
			}
			mlistD1DataCurves.add(linkedDataCurve);
		}
		
		for (int idx2 = 0; idx2 < arrayD2Values.length; idx2 ++)	{
			LinkedDataCurve linkedDataCurve = new LinkedDataCurve(arrayD2Values[idx2]);
			for (int idx1 = 0; idx1 < arrayD1Values.length; idx1 ++)	{
				linkedDataCurve.mlistPnts.add(arrayMlps[idx1][idx2]);
				if (idx1 > 0)	{
					arrayMlps[idx1][idx2].linkTo(arrayMlps[idx1 - 1][idx2]);
					arrayMlps[idx1 - 1][idx2].linkTo(arrayMlps[idx1][idx2]);
				}
			}
			mlistD2DataCurves.add(linkedDataCurve);
		}
        
		for (int idx2 = 0; idx2 < arrayD2Values.length - 1; idx2 ++)	{
			for (int idx1 = 0; idx1 < arrayD1Values.length - 1; idx1 ++)	{
                LinkedList<MultiLinkedPoint> listMLP = new LinkedList<MultiLinkedPoint>();
                listMLP.add(arrayMlps[idx1 + 1][idx2]);
                listMLP.add(arrayMlps[idx1 + 1][idx2 + 1]);
                listMLP.add(arrayMlps[idx1][idx2]);
                listMLP.add(arrayMlps[idx1][idx2 + 1]);
                mlistSurfaceElements.add(listMLP);
                /*LinkedList<MultiLinkedPoint> listMLP1 = new LinkedList<MultiLinkedPoint>();
                listMLP1.add(arrayMlps[idx1][idx2 + 1]);
                listMLP1.add(arrayMlps[idx1][idx2]);
                listMLP1.add(arrayMlps[idx1 + 1][idx2 + 1]);
                mlistSurfaceElements.add(listMLP1);*/
            }
        }
	}
	
	// set up data series grid surface in a quick way
	public void setByMatrix(double[] arrayD1Values, double[] arrayD2Values, double[][] arrayD3Values)	{
		Position3D[][] arrayCvtedPnts = new Position3D[arrayD1Values.length][arrayD2Values.length];
		for (int idx1 = 0; idx1 < arrayD1Values.length; idx1 ++)	{
			for (int idx2 = 0; idx2 < arrayD2Values.length; idx2 ++)	{
				switch (mnMode)	{
				case 2:	//z - x grid
					arrayCvtedPnts[idx1][idx2] = new Position3D(arrayD2Values[idx2], arrayD3Values[idx1][idx2], arrayD1Values[idx1]);
					break;
				case 1: //y - z grid
					arrayCvtedPnts[idx1][idx2] = new Position3D(arrayD3Values[idx1][idx2], arrayD1Values[idx1], arrayD2Values[idx2]);
					break;
				default:	// x - y grid
					arrayCvtedPnts[idx1][idx2] = new Position3D(arrayD1Values[idx1], arrayD2Values[idx2], arrayD3Values[idx1][idx2]);
					break;
				}
			}
		}
		setByMatrix(arrayD1Values, arrayD2Values, arrayD3Values, arrayCvtedPnts);
	}
}
