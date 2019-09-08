package com.cyzapps.VisualMFP;

public class MathLib {
	
	public static final double THE_MAX_ABSOLUTE_ERROR_OF_DOUBLE = 5.0e-16;
	public static final double THE_MAX_RELATIVE_ERROR_OF_DOUBLE = 5.0e-32;
	
	
	public static boolean isEqual(double a, double b)
	{
		if (isValidReal(a) == false || isValidReal(b) == false)	{
			// cannot compare NaN or Inf
			return false;
		}
		if (Math.abs(a - b) < THE_MAX_ABSOLUTE_ERROR_OF_DOUBLE
				&& Math.abs(a) < THE_MAX_ABSOLUTE_ERROR_OF_DOUBLE
				&& Math.abs(b) < THE_MAX_ABSOLUTE_ERROR_OF_DOUBLE)	{
            // this is to compare numbers very close to zero
			return true;
		} else	{
			double dDivBy;
			if (Math.abs(a) > Math.abs(b))	{
				dDivBy = Math.abs(a);
			} else	{
				dDivBy = Math.abs(b);
			}
            if (dDivBy == 0)	{
                return true;
            }
            if (Math.abs(a - b)/dDivBy <= THE_MAX_RELATIVE_ERROR_OF_DOUBLE)	{
                return true;
            }
            return false;
        }
	}
	
	public static boolean isValidReal(double a)	{
		if (Double.isInfinite(a) || Double.isNaN(a))	{
			return false;
		} else	{
			return true;
		}
	}
}
