package com.cyzapps.mathrecog;

import com.cyzapps.VisualMFP.Position3D;
import com.cyzapps.imgmatrixproc.ImgMatrixOutput;
import java.util.Arrays;
import java.util.LinkedList;

public class CharUnit {
	private byte[][] mbarrayBiValues = new byte[0][0];	// bivalue matrix (true means colored, false means not colored)
    // do not check validiblity of left, top, width and height to save time.
    private int mnLeft = 0;
	private int mnTop = 0;
    private int mnWidth = 0;
    private int mnHeight = 0;
    // from here, all the values and positions are not oriented from (left, top) but from (0, 0)
    private Position3D[] mlistAllPnts = new Position3D[0];	// all the points.
    private Position3D[] mlistNormPnts = new Position3D[0];    // all the normalized points.
    
    private int[] mnarrayJointPnts = new int[12];    // joint points in lines from central to right, right top, top, left top,
                                                    // left, left bottom, bottom, right bottom and two other vertical lines and
                                                    // two other horizontal lines.
	private double mdMaxX = Double.NEGATIVE_INFINITY, mdMaxY = Double.NEGATIVE_INFINITY,
			mdMinX = Double.POSITIVE_INFINITY, mdMinY = Double.POSITIVE_INFINITY;
	private int mnPntCount = 0;
    private int mnEdgePntCnt = 0;
    private double mdAvgX = 0;
    private double mdStdevX = 0;
	private double mdAvgY = 0;
	private double mdStdevY = 0;
	public byte[][] getBiValueMatrix()	{
		return mbarrayBiValues;
	}
    public int getLeft()    {
        return mnLeft;
    }
    public int getTop() {
        return mnTop;
    }
    public int getWidth()   {
        return mnWidth;
    }
    public int getHeight()  {
        return mnHeight;
    }
	public double getMaxX()	{
		return mdMaxX;
	}
	public double getMinX()	{
		return mdMinX;
	}
	public double getMaxY()	{
		return mdMaxY;
	}
	public double getMinY()	{
		return mdMinY;
	}
	public Position3D[] getAllPnts()	{
		return mlistAllPnts;
	}
	public Position3D[] getNormPnts()	{
		return mlistNormPnts;
	}
	public int getPntCount()	{
		return mnPntCount;
	}
    public int getEdgePntCnt()  {
        return mnEdgePntCnt;
    }
    public double getAvgStrokeWidth()  {
        return (mnPntCount * 2.0)/mnEdgePntCnt;
    }
    public double getAvgX() {
        return mdAvgX;
    }
    public double getStdevX() {
        return mdStdevX;
    }
	public double getAvgY()	{
		return mdAvgY;
	}
	public double getStdevY()	{
		return mdStdevY;
	}
	
	public CharUnit()	{
		initialize();
	}
	
	public CharUnit(byte[][] arrayBiValues)	{
        if (arrayBiValues == null)  {
            setBiValueMatrix(arrayBiValues, 0, 0, 0, 0);
        } else if (arrayBiValues.length == 0) {
            setBiValueMatrix(arrayBiValues, 0, 0, arrayBiValues.length, 0);
        } else  {
            setBiValueMatrix(arrayBiValues, 0, 0, arrayBiValues.length, arrayBiValues[0].length);
        }
	}
    
    public CharUnit(byte[][] arrayBiValues, int nLeft, int nTop, int nWidth, int nHeight)	{
        setBiValueMatrix(arrayBiValues, nLeft, nTop, nWidth, nHeight);
    }
    
    public CharUnit(ImageChop imgChop)  {
        setBiValueMatrix(imgChop.mbarrayImg, imgChop.mnLeft, imgChop.mnTop, imgChop.mnWidth, imgChop.mnHeight);
    }
	
	public void initialize()	{
		mbarrayBiValues = new byte[0][0];	// bivalue matrix (true means colored, false means not colored)
        mnLeft = 0;
        mnTop = 0;
        mnWidth = 0;
        mnHeight = 0;
		mdMaxX = -Double.MAX_VALUE;
		mdMaxY = -Double.MAX_VALUE;
		mdMinX = Double.MAX_VALUE;
		mdMinY = Double.MAX_VALUE;
		mlistAllPnts = new Position3D[0];
        mnarrayJointPnts = new int[12];
		mnPntCount = 0;
        mdAvgX = 0;
        mdStdevX = 0;
		mdAvgY = 0;
		mdStdevY = 0;
	}
	
    public void setBiValueMatrix(ImageChop imgChop)  {
        setBiValueMatrix(imgChop.mbarrayImg, imgChop.mnLeft, imgChop.mnTop, imgChop.mnWidth, imgChop.mnHeight);
    }
    
    public void setBiValueMatrix(byte[][] barrayMatrix) {
        int nWidth = barrayMatrix.length;
        int nHeight = barrayMatrix[0].length;
        setBiValueMatrix(barrayMatrix, 0, 0, nWidth, nHeight);
    }
    
	public void setBiValueMatrix(byte[][] barrayMatrix, int nLeft, int nTop, int nWidth, int nHeight)	{
		if (barrayMatrix == null)	{
			initialize();
		} else	{
			mnPntCount = 0;
			mbarrayBiValues = barrayMatrix;
            mnLeft = nLeft;
            mnTop = nTop;
            mnWidth = nWidth;
            mnHeight = nHeight;
			double dSumY = 0, dSumYSq = 0, dSumX = 0, dSumXSq = 0; 
			for (int row = 0; row < nHeight; row ++)	{
				for (int col = 0; col < nWidth; col ++)	{
					if (mbarrayBiValues[col + nLeft][row + nTop] == 1)	{
                        double dCvtedX = (double)col, dCvtedY = (double)row;
						if (dCvtedX > mdMaxX)	{
							mdMaxX = dCvtedX;
						}
						if (dCvtedX < mdMinX)	{
							mdMinX = dCvtedX;
						}
						if (dCvtedY > mdMaxY)	{
							mdMaxY = dCvtedY;
						}
						if (dCvtedY < mdMinY)	{
							mdMinY = dCvtedY;
						}
						mnPntCount ++;
                        if (row == 0 || row == nHeight - 1 || col == 0 || col == nWidth - 1)    {
                            mnEdgePntCnt ++;
                        } else if (mbarrayBiValues[col + nLeft - 1][row + nTop] == 0
                                || mbarrayBiValues[col + nLeft - 1][row + nTop + 1] == 0
                                || mbarrayBiValues[col + nLeft][row + nTop + 1] == 0
                                || mbarrayBiValues[col + nLeft + 1][row + nTop + 1] == 0
                                || mbarrayBiValues[col + nLeft + 1][row + nTop] == 0
                                || mbarrayBiValues[col + nLeft + 1][row + nTop - 1] == 0
                                || mbarrayBiValues[col + nLeft][row + nTop - 1] == 0
                                || mbarrayBiValues[col + nLeft - 1][row + nTop - 1] == 0) {
                            mnEdgePntCnt ++;
                        }
						dSumY += dCvtedY;
						dSumYSq += dCvtedY * dCvtedY;
						dSumX += dCvtedX;
                        dSumXSq += dCvtedX * dCvtedX;
					}
				}
			}
			if (mnPntCount == 0)	{
				// no point at all.
				mlistAllPnts = new Position3D[0];
				mlistNormPnts = new Position3D[0];
                mdAvgX = 0;
                mdStdevX = 0;
				mdAvgY = 0;
				mdStdevY = 0;
			} else  {
                if (mnPntCount * dSumYSq - dSumY * dSumY == 0)	{
                    // all the ys are equal
                    mlistAllPnts = new Position3D[mnPntCount];
                    mlistNormPnts = new Position3D[mnPntCount];
                    mdAvgX = dSumX/mnPntCount;
                    mdAvgY = dSumY/mnPntCount;
                    mdStdevY = 0;
                } else  {
                    mlistAllPnts = new Position3D[mnPntCount];
                    mlistNormPnts = new Position3D[mnPntCount];
                    mdAvgX = dSumX/mnPntCount;
                    mdAvgY = dSumY/mnPntCount;
                    mdStdevY = Math.sqrt(dSumYSq/mnPntCount - mdAvgY * mdAvgY);
                }
                mdStdevX = Math.sqrt(dSumXSq/mnPntCount - mdAvgX * mdAvgX);
				int idxPnt = 0;
				for (int row = 0; row < nHeight; row ++)	{
					for (int col = 0; col < nWidth; col ++)	{
						if (mbarrayBiValues[col + nLeft][row + nTop] == 1)	{
							mlistAllPnts[idxPnt] = new Position3D(col, row);
							idxPnt ++;
						}
					}
				}
                for (int idx = 0; idx < mnPntCount; idx ++) {
                    mlistNormPnts[idx] = new Position3D((mdStdevX==0)?0:((mlistAllPnts[idx].getX() - mdAvgX)/mdStdevX),
                            (mdStdevY==0)?0:((mlistAllPnts[idx].getY() - mdAvgY)/mdStdevY));
                }
			}
            
            calcJointPoints();  // calculate joint points.
		}
	}
	
    // (dX - dXInit)/(dY - dYInit) = dLnSlope. dLnSlope can be 0, can be positive inf (to right) or negative inf (to left).
    public int countLnJointPnts(double dXInit, double dYInit, double dLnSlope, boolean bUpWards)  {
		int nNumOfJntPnts = 0;
		double dX = dXInit, dY = dYInit;
		LinkedList<Position3D> listJnts = new LinkedList<Position3D>();
        // if not vertical or horizontal cut line, then we have to add the first point 
        if (dLnSlope != 0 && Double.isInfinite(dLnSlope) == false && Double.isNaN(dLnSlope) == false)   {
            if ((int)dX >= 0 && (int)dX < mnWidth && (int)dY >= 0 && (int)dY < mnHeight
                        && mbarrayBiValues[(int)dX + mnLeft][(int)dY + mnTop] == 1)   {
                nNumOfJntPnts ++;
                listJnts.add(new Position3D((int)dX, (int)dY));
            }
        }
        
		if (dLnSlope > 0 && dLnSlope != Double.POSITIVE_INFINITY && !bUpWards)	{	// if slope is positive and line is going down. right bottom
            while (dX <= (int)mdMaxX + 1 && dY <= (int)mdMaxY + 1)    {
                double dNewX = dLnSlope * ((Math.floor(dY) + 1) - dYInit) + dXInit;
                double dNewY = 1/dLnSlope * ((Math.floor(dX) + 1) - dXInit) + dYInit;
                int nNextX = (int)dX, nNextY = (int)dY;
                if ((dNewX - dXInit) < (dNewY - dYInit) * dLnSlope)    {
                    nNextX = (int)dNewX;
                    nNextY = (int)(Math.floor(dY) + 1);
                    dX = dNewX;
                    dY = Math.floor(dY) + 1;
                } else  {
                    nNextX = (int)(Math.floor(dX) + 1);
                    nNextY = (int)dNewY;
                    dX = Math.floor(dX) + 1;
                    dY = dNewY;
                }
                if (nNextX >= 0 && nNextX < mnWidth && nNextY >= 0 && nNextY < mnHeight
                        && mbarrayBiValues[nNextX + mnLeft][nNextY + mnTop] == 1
                        && (listJnts.size() == 0 || (nNextX != listJnts.getLast().getX() || nNextY != listJnts.getLast().getY())))   {
                    nNumOfJntPnts ++;
                    for (int idx1 = listJnts.size() - 1, nPntCnt = 0; idx1 >= 0 && nPntCnt < 2; idx1 --, nPntCnt++)   {
                        Position3D p3 = listJnts.get(idx1);
                        if (Math.abs((int)p3.getX() - nNextX) <= 1 && Math.abs((int)p3.getY() - nNextY) <= 1)   {
                            nNumOfJntPnts --;
                            break;
                        }
                    }
                    listJnts.add(new Position3D(nNextX, nNextY));
                }
            }
		} else if (dLnSlope > 0 && dLnSlope != Double.POSITIVE_INFINITY && bUpWards)	{	// if slope is positive and line is going up. left top
            while (dX >= (int)mdMinX && dY >= (int)mdMinY)    {
                double dNewX = dXInit - dLnSlope * (dYInit - (Math.ceil(dY) - 1));
                double dNewY = dYInit - 1/dLnSlope * (dXInit - (Math.ceil(dX) - 1));
                int nNextX = (int)dX, nNextY = (int)dY;
                if ((dXInit - dNewX) < (dYInit - dNewY) * dLnSlope)    {
                    nNextX = (int)dNewX;
                    nNextY = (int)(Math.ceil(dY) - 1);
                    dX = dNewX;
                    dY = Math.ceil(dY) - 1;
                } else  {
                    nNextX = (int)(Math.ceil(dX) - 1);
                    nNextY = (int)dNewY;
                    dX = Math.ceil(dX) - 1;
                    dY = dNewY;
                }
                if (nNextX >= 0 && nNextX < mnWidth && nNextY >= 0 && nNextY < mnHeight
                        && mbarrayBiValues[nNextX + mnLeft][nNextY + mnTop] == 1
                        && (listJnts.size() == 0 || (nNextX != listJnts.getLast().getX() || nNextY != listJnts.getLast().getY())))   {
                    nNumOfJntPnts ++;
                    for (int idx1 = listJnts.size() - 1, nPntCnt = 0; idx1 >= 0 && nPntCnt < 2; idx1 --, nPntCnt++)   {
                        Position3D p3 = listJnts.get(idx1);
                        if (Math.abs((int)p3.getX() - nNextX) <= 1 && Math.abs((int)p3.getY() - nNextY) <= 1)   {
                            nNumOfJntPnts --;
                            break;
                        }
                    }
                    listJnts.add(new Position3D(nNextX, nNextY));
                }
            }
        } else if (dLnSlope < 0 && dLnSlope != Double.NEGATIVE_INFINITY && !bUpWards)    {  // left bottom.
            while (dX >= (int)mdMinX && dY <= (int)mdMaxY + 1)    {
                double dNewX = dXInit + dLnSlope * ((Math.floor(dY) + 1) - dYInit);
                double dNewY = -1/dLnSlope * (dXInit - (Math.ceil(dX) - 1)) + dYInit;
                int nLastX = (int)dX, nLastY = (int)dY;
                int nNextX = (int)dX, nNextY = (int)dY;
                if ((dXInit - dNewX) < (dYInit - dNewY) * dLnSlope)    {
                    nNextX = (int)dNewX;
                    nNextY = (int)(Math.floor(dY) + 1);
                    dX = dNewX;
                    dY = Math.floor(dY) + 1;
                } else  {
                    nNextX = (int)(Math.ceil(dX) - 1);
                    nNextY = (int)dNewY;
                    dX = Math.ceil(dX) - 1;
                    dY = dNewY;
                }
                if ((nLastX - 1) == nNextX && (nLastY + 1) == nNextY)   {
                    int nMidX = nNextX, nMidY = nLastY;
                    if (nMidX >= 0 && nMidX < mnWidth && nMidY >= 0 && nMidY < mnHeight
                            && mbarrayBiValues[nMidX + mnLeft][nMidY + mnTop] == 1
                            && (listJnts.size() == 0 || (nMidX != listJnts.getLast().getX() || nMidY != listJnts.getLast().getY())))   {
                        nNumOfJntPnts ++;
                        for (int idx1 = listJnts.size() - 1, nPntCnt = 0; idx1 >= 0 && nPntCnt < 2; idx1 --, nPntCnt++)   {
                            Position3D p3 = listJnts.get(idx1);
                            if (Math.abs((int)p3.getX() - nMidX) <= 1 && Math.abs((int)p3.getY() - nMidY) <= 1)   {
                                nNumOfJntPnts --;
                                break;
                            }
                        }
                        listJnts.add(new Position3D(nMidX, nMidY));
                    }
                }
                if (nNextX >= 0 && nNextX < mnWidth && nNextY >= 0 && nNextY < mnHeight
                        && mbarrayBiValues[nNextX + mnLeft][nNextY + mnTop] == 1
                        && (listJnts.size() == 0 || (nNextX != listJnts.getLast().getX() || nNextY != listJnts.getLast().getY())))   {
                    nNumOfJntPnts ++;
                    for (int idx1 = listJnts.size() - 1, nPntCnt = 0; idx1 >= 0 && nPntCnt < 2; idx1 --, nPntCnt++)   {
                        Position3D p3 = listJnts.get(idx1);
                        if (Math.abs((int)p3.getX() - nNextX) <= 1 && Math.abs((int)p3.getY() - nNextY) <= 1)   {
                            nNumOfJntPnts --;
                            break;
                        }
                    }
                    listJnts.add(new Position3D(nNextX, nNextY));
                }
            }
        } else if (dLnSlope < 0 && dLnSlope != Double.NEGATIVE_INFINITY && bUpWards)    {   // right top.
            while (dX <= (int)mdMaxX + 1 && dY >= (int)mdMinY)    {
                double dNewX = -dLnSlope * (dYInit - (Math.ceil(dY) - 1)) + dXInit;
                double dNewY = dYInit + 1/dLnSlope * ((Math.floor(dX) + 1) - dXInit);
                int nLastX = (int)dX, nLastY = (int)dY;
                int nNextX = (int)dX, nNextY = (int)dY;
                if ((dNewX - dXInit) < (dNewY - dYInit) * dLnSlope)    {
                    nNextX = (int)dNewX;
                    nNextY = (int)(Math.ceil(dY) - 1);
                    dX = dNewX;
                    dY = Math.ceil(dY) - 1;
                } else  {
                    nNextX = (int)Math.floor(dX) + 1;
                    nNextY = (int)dNewY;
                    dX = Math.floor(dX) + 1;
                    dY = dNewY;
                }
                if ((nLastX + 1) == nNextX && (nLastY - 1) == nNextY)   {
                    int nMidX = nLastX, nMidY = nNextY;
                    if (nMidX >= 0 && nMidX < mnWidth && nMidY >= 0 && nMidY < mnHeight
                            && mbarrayBiValues[nMidX + mnLeft][nMidY + mnTop] == 1
                            && (listJnts.size() == 0 || (nMidX != listJnts.getLast().getX() || nMidY != listJnts.getLast().getY())))   {
                        nNumOfJntPnts ++;
                        for (int idx1 = listJnts.size() - 1, nPntCnt = 0; idx1 >= 0 && nPntCnt < 2; idx1 --, nPntCnt++)   {
                            Position3D p3 = listJnts.get(idx1);
                            if (Math.abs((int)p3.getX() - nMidX) <= 1 && Math.abs((int)p3.getY() - nMidY) <= 1)   {
                                nNumOfJntPnts --;
                                break;
                            }
                        }
                        listJnts.add(new Position3D(nMidX, nMidY));
                    }
                }
                if (nNextX >= 0 && nNextX < mnWidth && nNextY >= 0 && nNextY < mnHeight
                        && mbarrayBiValues[nNextX + mnLeft][nNextY + mnTop] == 1
                        && (listJnts.size() == 0 || (nNextX != listJnts.getLast().getX() || nNextY != listJnts.getLast().getY())))   {
                    nNumOfJntPnts ++;
                    for (int idx1 = listJnts.size() - 1, nPntCnt = 0; idx1 >= 0 && nPntCnt < 2; idx1 --, nPntCnt++)   {
                        Position3D p3 = listJnts.get(idx1);
                        if (Math.abs((int)p3.getX() - nNextX) <= 1 && Math.abs((int)p3.getY() - nNextY) <= 1)   {
                            nNumOfJntPnts --;
                            break;
                        }
                    }
                    listJnts.add(new Position3D(nNextX, nNextY));
                }
            }
        } else if (dLnSlope == 0 && !bUpWards)  {   // to bottom
            if (dXInit >= 0 & dXInit < mnWidth + 1)  {
                for (int idx = (int)Math.max(mdMinY, dYInit); idx <= (int)mdMaxY; idx ++)   {
                    if (mbarrayBiValues[(int)dXInit + mnLeft][idx + mnTop] == 1)   {
                        if (idx == (int)Math.max(mdMinY, dYInit))   {
                            nNumOfJntPnts ++;
                        } else if (mbarrayBiValues[(int)dXInit + mnLeft][idx - 1 + mnTop] == 0)    {
                            nNumOfJntPnts ++;
                        }
                    }
                }
            }
        } else if (dLnSlope == 0 && bUpWards)   {   // to top
            if (dXInit >= 0 & dXInit < mnWidth + 1)  {
                for (int idx = (int)Math.min(mdMaxY, dYInit); idx >= (int)mdMinY; idx --)   {
                    if (mbarrayBiValues[(int)dXInit + mnLeft][idx + mnTop] == 1)   {
                        if (idx == (int)Math.min(mdMaxY, dYInit))   {
                            nNumOfJntPnts ++;
                        } else if (mbarrayBiValues[(int)dXInit + mnLeft][idx + 1 + mnTop] == 0)    {
                            nNumOfJntPnts ++;
                        }
                    }
                }
            }
        } else if (dLnSlope == Double.NEGATIVE_INFINITY)    {   // to left.
            if (dYInit >= 0 && dYInit < mnHeight + 1)   {
                for (int idx = (int)Math.min(mdMaxX, dXInit); idx >= (int)mdMinX; idx --)   {
                    if (mbarrayBiValues[idx + mnLeft][(int)dYInit + mnTop] == 1)   {
                        if (idx == (int)Math.min(mdMaxX, dXInit))   {
                            nNumOfJntPnts ++;
                        } else if (mbarrayBiValues[idx + 1 + mnLeft][(int)dYInit + mnTop] == 0)    {
                            nNumOfJntPnts ++;
                        }
                    }
                }
            }
        } else if (dLnSlope == Double.POSITIVE_INFINITY)    {   // to right
            if (dYInit >= 0 && dYInit < mnHeight + 1)   {
                for (int idx = (int)Math.max(mdMinX, dXInit); idx <= (int)mdMaxX; idx ++)   {
                    if (mbarrayBiValues[idx + mnLeft][(int)dYInit + mnTop] == 1)   {
                        if (idx == (int)Math.max(mdMinX, dXInit))   {
                            nNumOfJntPnts ++;
                        } else if (mbarrayBiValues[idx - 1 + mnLeft][(int)dYInit + mnTop] == 0)    {
                            nNumOfJntPnts ++;
                        }
                    }
                }
            }
        }
        return nNumOfJntPnts;
    }
    
    public void calcJointPoints()   {
        mnarrayJointPnts = new int[12];
        boolean bHasPnt = false;
        for (int idx = 0; idx < mnWidth; idx ++)    {
            for (int idx1 = 0; idx1 < mnHeight; idx1 ++)    {
                if (mbarrayBiValues[idx + mnLeft][idx1 + mnTop] == 1)   {
                    bHasPnt = true;
                    break;
                }
            }
        }
        
        if (!bHasPnt)   {
            return;    // no point.
        }
        
        double dSlope = 0, dXVariationRange = mdStdevX;

        // step 1, calculate number of joint points
        // 1. from central to right.
        int nNumOfJntsToRight = countLnJointPnts(mdAvgX, mdAvgY, Double.POSITIVE_INFINITY, true);
        
        // 2. from central to top.
        int nNumOfJntsToTop = countLnJointPnts(mdAvgX, mdAvgY, dSlope, true);
        
        // 3. from central to left.
        int nNumOfJntsToLeft = countLnJointPnts(mdAvgX, mdAvgY, Double.NEGATIVE_INFINITY, true);
        
        // 4. from central to bottom.
        int nNumOfJntsToBottom = countLnJointPnts(mdAvgX, mdAvgY, dSlope, false);
        
        // 5. from central to right top. if mdStdev == 0, it degrades to to right.
        int nNumOfJnts2RT = (mdStdevY == 0)?countLnJointPnts(mdAvgX, mdAvgY, Double.POSITIVE_INFINITY, true)
                                            :countLnJointPnts(mdAvgX, mdAvgY, dSlope - dXVariationRange/mdStdevY, true);
        
        // 6. from central to left top. if mdStdev == 0, it degrades to to left.
        int nNumOfJnts2LT = (mdStdevY == 0)?countLnJointPnts(mdAvgX, mdAvgY, Double.NEGATIVE_INFINITY, true)
                                            :countLnJointPnts(mdAvgX, mdAvgY, dSlope + dXVariationRange/mdStdevY, true);
        
        // 7. from central to left bottom. if mdStdev == 0, it degrades to to left.
        int nNumOfJnts2LB = (mdStdevY == 0)?countLnJointPnts(mdAvgX, mdAvgY, Double.NEGATIVE_INFINITY, true)
                                            :countLnJointPnts(mdAvgX, mdAvgY, dSlope - dXVariationRange/mdStdevY, false);
        
        // 8. from central to right bottom. if mdStdev == 0, it degrades to to right.
        int nNumOfJnts2RB = (mdStdevY == 0)?countLnJointPnts(mdAvgX, mdAvgY, Double.POSITIVE_INFINITY, true)
                                            :countLnJointPnts(mdAvgX, mdAvgY, dSlope + dXVariationRange/mdStdevY, false);
        
        // 9. left 1 stdev vertical line.
        double dLeft1StdevXInit = mdAvgX - (mdAvgY - mdMinY) * dSlope - dXVariationRange * 1.0;
        int nNumOfJntsLeftVLn = 0;
        if (dLeft1StdevXInit >= mdMinX) {
            nNumOfJntsLeftVLn = countLnJointPnts(dLeft1StdevXInit, mdMinY, dSlope, false);
        }
        
        // 10. right 1 stdev vertical line.
        double dRight1StdevXInit = mdAvgX - (mdAvgY - mdMinY) * dSlope + dXVariationRange * 1.0;
        int nNumOfJntsRightVLn = 0;
        if (dRight1StdevXInit <= mdMaxX)    {
            nNumOfJntsRightVLn = countLnJointPnts(dRight1StdevXInit, mdMinY, dSlope, false);
        }
        
        // 11. top 1 stdev horizontal line.
        double dYTopHLn = mdAvgY - mdStdevY * 1.0;
        int nNumOfJntsTopHLn = 0;
        if (dYTopHLn >= mdMinY) {
            nNumOfJntsTopHLn = countLnJointPnts(mdMinX, dYTopHLn, Double.POSITIVE_INFINITY, true);
        }
        
        // 12. bottom 1 stdev horizontal line.
        double dYBottomHLn = mdAvgY + mdStdevY * 1.0;
        int nNumOfJntsBottomHLn = 0;
        if (dYBottomHLn <= mdMaxY)  {
            nNumOfJntsBottomHLn = countLnJointPnts(mdMinX, dYBottomHLn, Double.POSITIVE_INFINITY, true);
        }
        
        mnarrayJointPnts[0] = nNumOfJntsToRight;
        mnarrayJointPnts[1] = nNumOfJnts2RT;
        mnarrayJointPnts[2] = nNumOfJntsToTop;
        mnarrayJointPnts[3] = nNumOfJnts2LT;
        mnarrayJointPnts[4] = nNumOfJntsToLeft;
        mnarrayJointPnts[5] = nNumOfJnts2LB;
        mnarrayJointPnts[6] = nNumOfJntsToBottom;
        mnarrayJointPnts[7] = nNumOfJnts2RB;
        mnarrayJointPnts[8] = nNumOfJntsLeftVLn;
        mnarrayJointPnts[9] = nNumOfJntsRightVLn;
        mnarrayJointPnts[10] = nNumOfJntsTopHLn;
        mnarrayJointPnts[11] = nNumOfJntsBottomHLn;
    }
    
    // using count number of joint points method to compare, return from 0 to 1, smaller, better.
    public double compareToCountJntPnts(CharUnit charUnit)  {
        double dLikeness = 0;
        for (int idx = 0; idx < mnarrayJointPnts.length; idx ++)    {
            dLikeness += Math.min(2, Math.abs(mnarrayJointPnts[idx] - charUnit.mnarrayJointPnts[idx]));
        }
        return dLikeness / mnarrayJointPnts.length / 2.0;
    }
    
    // using lattice density method to compare 
	public double compareToLatticeDensity(CharUnit charUnit, int nMainCutTimes, int nDivShiftTimes, int nReturnType)	{
        // avoid infinite or NaN value of dXScalingRatio and dYScalingRatio.
        if (charUnit.getStdevX() == 0 && mdStdevX == 0)    {
            return 0;
        } else if (charUnit.getStdevX() * mdStdevX == 0)  {
            return 1;
        }
        if (charUnit.getStdevY() == 0 && mdStdevY == 0)    {
            return 0;
        } else if (charUnit.getStdevY() * mdStdevY == 0)  {
            return 1;
        }        
        
		double dXScalingRatio = charUnit.getStdevX() / mdStdevX;
		double dYScalingRatio = charUnit.getStdevY() / mdStdevY;
        // the font cannot be streched too much.
        if (dXScalingRatio / dYScalingRatio > 2.0)    {
            dXScalingRatio = 2 * dYScalingRatio;
        } else if (dXScalingRatio / dYScalingRatio < 0.5)    {
            dXScalingRatio = 0.5 * dYScalingRatio;
        }
		Position3D[] listCvtedPnts = new Position3D[mnPntCount];
		double dMaxX = charUnit.getMaxX();
		double dMinX = charUnit.getMinX();
		double dMaxY = charUnit.getMaxY();
		double dMinY = charUnit.getMinY();
		for (int idx = 0; idx < mnPntCount; idx ++)	{
			double x = mlistAllPnts[idx].getX();
			double y = mlistAllPnts[idx].getY();
			double newY = (y - mdAvgY) * dYScalingRatio + charUnit.getAvgY();
            double newX;
            newX = (x - mdAvgX) * dXScalingRatio + charUnit.getAvgX();
			if (newX > dMaxX)	{
				dMaxX = newX;
			}
			if (newX < dMinX)	{
				dMinX = newX;
			}
			if (newY > dMaxY)	{
				dMaxY = newY;
			}
			if (newY < dMinY)	{
				dMinY = newY;
			}
			listCvtedPnts[idx] = new Position3D(newX, newY);
		}
		/*
		 * Now we get the range. Assume the width of a stroke cannot be wider than long edge (can be height or width)/5 or
         * short edge (can be height or width)/2. Generally suggest to choose nMainCutTimes = 6 or 8 and nDivShiftTimes = 2
         * or 3. Average number of cuts in one side cannot too big or too small, too big means one cut is much narrower than
         * stroke width (before thinning) so that after thinning, cannot see match, too small means all the characters are
         * similar.
		 */
        double dDivUnit = Math.sqrt((dMaxX + 1 - dMinX)*(dMaxY + 1 - dMinY))/nMainCutTimes;   // not use charUnit.getAvgStrokeWidth();
        if (dDivUnit > Math.min((dMaxX + 1 - dMinX), (dMaxY + 1 - dMinY))/2)    {
            dDivUnit = Math.min((dMaxX + 1 - dMinX), (dMaxY + 1 - dMinY))/2;    // ensure that shorter edge is cut to at least 2 pieces.
        }
		int nHeightDiv = (int)Math.ceil((dMaxY + 1 - dMinY)/dDivUnit) * nDivShiftTimes;
		int nWidthDiv = (int)Math.ceil((dMaxX + 1 - dMinX)/dDivUnit) * nDivShiftTimes;
		int[][] narrayPntCounts = new int[nWidthDiv][nHeightDiv];
		int[][] narrayCompPntCounts = new int[nWidthDiv][nHeightDiv];
		double dHeightUnit = (dMaxY + 1 - dMinY)/nHeightDiv;
		double dWidthUnit = (dMaxX + 1 - dMinX)/nWidthDiv;
		for (int idx = 0; idx < charUnit.getAllPnts().length; idx ++)	{
			double x = charUnit.getAllPnts()[idx].getX();
			double y = charUnit.getAllPnts()[idx].getY();
			narrayCompPntCounts[(int)((x - dMinX)/(dWidthUnit))][(int)((y - dMinY)/(dHeightUnit))] ++;
		}
        //ImgMatrixOutput.printMatrix(narrayCompPntCounts);
		for (int idx = 0; idx < listCvtedPnts.length; idx ++)	{
			double x = listCvtedPnts[idx].getX();
			double y = listCvtedPnts[idx].getY();
			narrayPntCounts[(int)((x - dMinX)/(dWidthUnit))][(int)((y - dMinY)/(dHeightUnit))] ++;
		}
        //ImgMatrixOutput.printMatrix(narrayPntCounts);
		
		// now calculate stdev of point densities
		double[][] darraySumDensityErrSqs = new double[nDivShiftTimes][nDivShiftTimes];
		double dMaxSumDensErrSq = Double.NEGATIVE_INFINITY;
		double dMinSumDensErrSq = Double.POSITIVE_INFINITY;
		double dAvgSumDensErrSq = 0;
        
        if (mnPntCount == 0 || charUnit.getPntCount() == 0) {   // to avoid infinit case.
            if (mnPntCount == charUnit.getPntCount())   {
                dMaxSumDensErrSq = dMinSumDensErrSq = dAvgSumDensErrSq = 0;
            } else {
                dMaxSumDensErrSq = dMinSumDensErrSq = dAvgSumDensErrSq = 2;
            }
        } else  {
            for (int idxSftInit1 = 0; idxSftInit1 < nDivShiftTimes; idxSftInit1 ++)	{
                for (int idxSftInit2 = 0; idxSftInit2 < nDivShiftTimes; idxSftInit2 ++)	{
                    for (int idx1 = 0; idx1 < nHeightDiv/nDivShiftTimes; idx1 ++)	{
                        for (int idx2 = 0; idx2 < nWidthDiv/nDivShiftTimes; idx2 ++)	{
                            double dDensity = 0, dCompDensity = 0;
                            for (int idx3 = 0; idx3 < nDivShiftTimes; idx3 ++)	{
                                for (int idx4 = 0; idx4 < nDivShiftTimes; idx4 ++)	{
                                    int nXIdx = (idx2 * nDivShiftTimes + idx4 + idxSftInit2) % nWidthDiv;
                                    int nYIdx = (idx1 * nDivShiftTimes + idx3 + idxSftInit1) % nHeightDiv;
                                    dDensity += (double)(narrayPntCounts[nXIdx][nYIdx]) / (double)(mnPntCount);
                                    dCompDensity += (double)(narrayCompPntCounts[nXIdx][nYIdx])/(double)(charUnit.getPntCount());
                                }
                            }
                            darraySumDensityErrSqs[idxSftInit2][idxSftInit1] += (dDensity - dCompDensity)*(dDensity - dCompDensity);
                        }
                    }
                    // darraySumDensityErrSqs[idxSftInit2][idxSftInit1] is always from 0 to 2. so need not to normalize
                    // darraySumDensityErrSqs[idxSftInit2][idxSftInit1] *= (nHeightDiv * nWidthDiv) / nDivShiftTimes / nDivShiftTimes;	// normalize
                    if (darraySumDensityErrSqs[idxSftInit2][idxSftInit1] > dMaxSumDensErrSq)	{
                        dMaxSumDensErrSq = darraySumDensityErrSqs[idxSftInit2][idxSftInit1];
                    }
                    if (darraySumDensityErrSqs[idxSftInit2][idxSftInit1] < dMinSumDensErrSq)	{
                        dMinSumDensErrSq = darraySumDensityErrSqs[idxSftInit2][idxSftInit1];
                    }
                    dAvgSumDensErrSq += darraySumDensityErrSqs[idxSftInit2][idxSftInit1];
                }
            }
            dAvgSumDensErrSq /= nDivShiftTimes * nDivShiftTimes;
        }
        double dReturnValue;
		if (nReturnType == 2)	{
			dReturnValue = dMaxSumDensErrSq;
		} else if (nReturnType == 1)	{
			dReturnValue = dMinSumDensErrSq;
		} else	{
			dReturnValue = dAvgSumDensErrSq;
		}
        return Math.min(dReturnValue, 1.0);
	}
    
    // return value from 0 to 1
    public double compareToMinDistance(CharUnit charUnit)  {
        if (mlistNormPnts.length == 0 && charUnit.mlistNormPnts.length == 0)    {
            return 0;
        } else if ((mlistNormPnts.length * charUnit.mlistNormPnts.length) == 0)    {
            return 1;   //one has more than 0 points the other has 0 points.
        }
        double dSumDisParam2This = 0, dSumDisThis2Param = 0;
        double dSumDisSqrParam2This = 0, dSumDisSqrThis2Param = 0;
        double[] darrayMinDisSqrThis2Param = new double[mlistNormPnts.length];
        Arrays.fill(darrayMinDisSqrThis2Param, Double.MAX_VALUE);
        for (int idx = 0; idx < charUnit.mlistNormPnts.length; idx ++)  {
            double dMinDisSqrParam2This = Double.MAX_VALUE;
            for (int idx1 = 0; idx1 < mlistNormPnts.length; idx1 ++)  {
                double distanceSqr = (charUnit.mlistNormPnts[idx].getX() - mlistNormPnts[idx1].getX())
                                    * (charUnit.mlistNormPnts[idx].getX() - mlistNormPnts[idx1].getX())
                                + (charUnit.mlistNormPnts[idx].getY() - mlistNormPnts[idx1].getY())
                                    * (charUnit.mlistNormPnts[idx].getY() - mlistNormPnts[idx1].getY());
                if (dMinDisSqrParam2This > distanceSqr)   {
                    dMinDisSqrParam2This = distanceSqr;
                }
                if (darrayMinDisSqrThis2Param[idx1] > distanceSqr)   {
                    darrayMinDisSqrThis2Param[idx1] = distanceSqr;
                }
            }
            dSumDisSqrParam2This += dMinDisSqrParam2This;
            dSumDisParam2This += Math.sqrt(dMinDisSqrParam2This);
        }
        double dAvgDisParam2This = dSumDisParam2This/charUnit.mlistNormPnts.length;
        double dAvgDisSqrParam2This = dSumDisSqrParam2This/charUnit.mlistNormPnts.length;
        double dAvgPlusStdDisParam2This = dAvgDisParam2This + Math.sqrt(dAvgDisSqrParam2This - dAvgDisParam2This * dAvgDisParam2This);
        for (int idx1 = 0; idx1 < mlistNormPnts.length; idx1 ++)  {
            dSumDisSqrThis2Param += darrayMinDisSqrThis2Param[idx1];
            dSumDisThis2Param += Math.sqrt(darrayMinDisSqrThis2Param[idx1]);
        }
        double dAvgDisThis2Param = dSumDisThis2Param/mlistNormPnts.length;
        double dAvgDisSqrThis2Param = dSumDisSqrThis2Param/mlistNormPnts.length;
        double dAvgPlusStdDisThis2Param = dAvgDisThis2Param + Math.sqrt(dAvgDisSqrThis2Param - dAvgDisThis2Param * dAvgDisThis2Param);
        return Math.max(dAvgPlusStdDisParam2This, dAvgPlusStdDisThis2Param)/2.0;
    }
    
    public double compareToShape(CharUnit charUnit)  {
        double dCharUnitWOverH = charUnit.mnWidth/(double)charUnit.mnHeight;
        double dUCWOverH = mnWidth/(double)mnHeight;
        if (dCharUnitWOverH < ConstantsMgr.msdExtendableCharWOverHThresh && dCharUnitWOverH > 1.0/ConstantsMgr.msdExtendableCharWOverHThresh
                 && (dCharUnitWOverH > ConstantsMgr.msdCharWOverHMaxSkewRatio * dUCWOverH
                    || dCharUnitWOverH < 1.0/ConstantsMgr.msdCharWOverHMaxSkewRatio * dUCWOverH))   {
            // for normal char, width/height ratio is too different.
            return 1.0;
        }else if (dCharUnitWOverH >= ConstantsMgr.msdExtendableCharWOverHThresh
                && dUCWOverH < ConstantsMgr.msdExtendableCharWOverHThresh / ConstantsMgr.msdCharWOverHMaxSkewRatio)   {
            // charUnit is very long while uc is not.
            return 1.0;
        } else if (dCharUnitWOverH <= 1.0/ConstantsMgr.msdExtendableCharWOverHThresh
                && dUCWOverH > ConstantsMgr.msdCharWOverHMaxSkewRatio/ConstantsMgr.msdExtendableCharWOverHThresh)    {
            // charUnit is very thin and tall while uc is not.
            return 1.0;
        } else  {
            return 0.0;
        }
    }

    public void printMatrix()   {
        if (mbarrayBiValues == null || mbarrayBiValues.length == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx = 0; idx < mbarrayBiValues[0].length; idx ++)   {
                for (int idx1 = 0; idx1 < mbarrayBiValues.length; idx1 ++)   {
                    if (idx1 >= mnLeft && idx1 < mnLeft + mnWidth && idx >= mnTop && idx < mnTop + mnHeight)    {
                        System.out.print(mbarrayBiValues[idx1][idx] + "\t");
                    } else  {
                        System.out.print("0\t");
                    }
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }

    public void printMinContainerMatrix()   {
        if (mbarrayBiValues == null || mnWidth == 0)   {
            System.out.println("[]");
        } else {
            System.out.println("[");
            for (int idx1 = mnTop; idx1 < mnTop + mnHeight; idx1 ++)   {
                for (int idx = mnLeft; idx < mnLeft + mnWidth; idx ++)   {
                    System.out.print(mbarrayBiValues[idx][idx1] + "\t");
                }
                System.out.print("\n");
            }
            System.out.println("]");
        }
    }
}
