package com.cyzapps.VisualMFP;

public class LinearMapper extends PointMapper {
	
	// all the following parameters are for transferring from FROM coordinate to TO coordinate
	// transferring order: shift first, then rotate, then scale.
	private double[][] marrayF2TMapMatrix = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };	// Mapping matrix from FROM to TO
	private double[][] marrayT2FMapMatrix = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };	// Mapping matrix from TO to FROM

	public static double[][] multiplyMapMatrix(double[][] array1, double[][] array2)	{
		// assume array1 and array2 are both 4 * 4 matrix.
		int nSize = array1.length;	// 4;
		double[][] arrayReturn = new double[nSize][nSize];
		for (int idxI = 0; idxI < nSize; idxI++)	// idxI: line count of arrayReturn
		{
			for (int idxJ = 0; idxJ < nSize; idxJ++)	// idxJ: column count of arrayReturn
			{
				for (int idxK = 0; idxK < nSize; idxK++)
				{
					arrayReturn[idxI][idxJ] += array1[idxI][idxK] * array2[idxK][idxJ];
				}
			}
		}
		return arrayReturn;
	}

	public static double[] mapVector(double[][] arrayMapping, double[] arrayVector)	{
		// assume arrayMapping is a 4*4 matrix and arrayVector's length is 4.
		int nSize = arrayVector.length;	// 4;
		double[] arrayReturn = new double[nSize];
		for (int idxI = 0; idxI < nSize; idxI++)	// idxI: line count of arrayReturn
		{
			for (int idxK = 0; idxK < nSize; idxK++)
			{
				arrayReturn[idxI] += arrayMapping[idxI][idxK] * arrayVector[idxK];
			}
		}
		return arrayReturn;
	}

	public static double[][] invertSquareMatrix(double[][] arrayOriginal)	{
		// assume arrayInput is a square matrix and it has invert matrix.
		int nSize = arrayOriginal.length;	//4;
		double[][] arrayInput = new double[nSize][nSize];	// make a copy of input parameter.
		for (int idx = 0; idx < nSize; idx++)
		{
			System.arraycopy(arrayOriginal[idx], 0, arrayInput[idx], 0, nSize);
		}
		double[][] arrayOutput = new double[nSize][nSize];

		//initialize arrayOutput as I
		for (int index0 = 0; index0 < nSize; index0++)
		{
			for (int index1 = 0; index1 < nSize; index1++)
			{
				if (index0 == index1)
				{
					arrayOutput[index0][index1] = 1;
				}
				else
				{
					arrayOutput[index0][index1] = 0;
				}
			}
		}

		for (int index0 = 0; index0 < nSize; index0++)
		{
			for (int index1 = 0; index1 < nSize; index1++)
			{
				if (index0 == index1)
				{
					continue;
				}
				else
				{
					double dLargestAbs = Math.abs(arrayInput[index0][index0]);
					int nLargestAbsIdx = index0;
					for (int idx = index0 + 1; idx < nSize; idx++)
					{
						double dThisAbs = Math.abs(arrayInput[idx][index0]);
						if (dLargestAbs < dThisAbs)
						{
							dLargestAbs = dThisAbs;
							nLargestAbsIdx = idx;
						}
					}
					if (arrayInput[nLargestAbsIdx][index0] == 0)
					{
						throw new ArithmeticException("Cannot invert matrix");
					}
					if (nLargestAbsIdx != index0)
					{
						// swap the rows
						double[] arrayTmp = new double[nSize];
						System.arraycopy(arrayInput[index0], 0, arrayTmp, 0, nSize);
						System.arraycopy(arrayInput[nLargestAbsIdx], 0, arrayInput[index0], 0, nSize);
						System.arraycopy(arrayTmp, 0, arrayInput[nLargestAbsIdx], 0, nSize);
						System.arraycopy(arrayOutput[index0], 0, arrayTmp, 0, nSize);
						System.arraycopy(arrayOutput[nLargestAbsIdx], 0, arrayOutput[index0], 0, nSize);
						System.arraycopy(arrayTmp, 0, arrayOutput[nLargestAbsIdx], 0, nSize);
					}

					double dRowEliminateRatio = arrayInput[index1][index0] / arrayInput[index0][index0];
					for (int idx = 0; idx < nSize; idx++)
					{
						arrayInput[index1][idx] -= dRowEliminateRatio * arrayInput[index0][idx];
						arrayOutput[index1][idx] -= dRowEliminateRatio * arrayOutput[index0][idx];
					}
				}
			}
		}
		for (int index0 = 0; index0 < nSize; index0++)
		{
			for (int index1 = 0; index1 < nSize; index1++)
			{
				arrayOutput[index0][index1] /= arrayInput[index0][index0];
			}

		}
		return arrayOutput;

	}

	public LinearMapper()
	{
		super();
	}
	
	public LinearMapper(double d0X, double d0Y, double d0Z,
			double dXRotation, double dYRotation, double dZRotation,
			double dRatioX, double dRatioY, double dRatioZ, boolean bFrom2To)	{
		setLinearMapper(d0X, d0Y, d0Z, dXRotation, dYRotation, dZRotation, dRatioX, dRatioY, dRatioZ, bFrom2To);
	}
	
	public void setLinearMapper(double d0X, double d0Y, double d0Z,
			double dXRotation, double dYRotation, double dZRotation,
			double dRatioX, double dRatioY, double dRatioZ, boolean bFrom2To)	{

        int nSize = marrayF2TMapMatrix.length;
		for (int idxI = 0; idxI < nSize; idxI++)
		{
			for (int idxJ = 0; idxJ < nSize; idxJ++)
			{
				marrayF2TMapMatrix[idxI][idxJ] = marrayT2FMapMatrix[idxI][idxJ]
						= (idxI == idxJ) ? 1 : 0;
			}
		}
		adjustLinearMapper(d0X, d0Y, d0Z, dXRotation, dYRotation, dZRotation,
			dRatioX, dRatioY, dRatioZ, bFrom2To);
	}
	
	public void adjustLinearMapper(double d0X, double d0Y, double d0Z,
			double dXRotation, double dYRotation, double dZRotation,
			double dRatioX, double dRatioY, double dRatioZ, boolean bFrom2To)	{

		int nSize = marrayF2TMapMatrix.length;
        
        double[][] arrayMultiplication = new double[4][4];
		for (int idxI = 0; idxI < nSize; idxI++)
		{
			for (int idxJ = 0; idxJ < nSize; idxJ++)
			{
				arrayMultiplication[idxI][idxJ]
						= (idxI == idxJ) ? 1 : (idxJ < nSize - 1) ? 0 : (idxI == 0) ? -d0X : (idxI == 1) ? -d0Y : -d0Z;
			}
		}
		// rotate about x seems mean that see from 0 to 1, anti-clock-wise rotation means positive angle, clock-wise rotation means negative angle. 
		double[][] arrayXRotation = {{1,0,0,0},
								{0, Math.cos(dXRotation), -Math.sin(dXRotation), 0},
								{0, Math.sin(dXRotation), Math.cos(dXRotation), 0},
								{0,0,0,1}};
		double[][] arrayYRotation = {{Math.cos(dYRotation),0,Math.sin(dYRotation),0},
								{0, 1, 0, 0},
								{-Math.sin(dYRotation), 0, Math.cos(dYRotation), 0},
								{0,0,0,1}};
		double[][] arrayZRotation = {{Math.cos(dZRotation), -Math.sin(dZRotation), 0, 0},
								{Math.sin(dZRotation), Math.cos(dZRotation), 0, 0},
								{0, 0, 1, 0},
								{0,0,0,1}};

		double[][] arrayScale = { { dRatioX, 0, 0, 0 }, { 0, dRatioY, 0, 0 }, { 0, 0, dRatioZ, 0 }, { 0, 0, 0, 1 } };

		if (bFrom2To)
		{
			// the parameters are for transferring from FROM coordinate to TO coordinate
			marrayF2TMapMatrix = multiplyMapMatrix(arrayScale,
								multiplyMapMatrix(arrayZRotation,
								multiplyMapMatrix(arrayYRotation,
								multiplyMapMatrix(arrayXRotation,
                                multiplyMapMatrix(arrayMultiplication, marrayF2TMapMatrix)))));
			try	{
				marrayT2FMapMatrix = invertSquareMatrix(marrayF2TMapMatrix);
			} catch(ArithmeticException e)	{
				marrayT2FMapMatrix = new double[4][4];
			}
		} else	{
			// the parameters are for transferring from TO coordinate to FROM coordinate
			marrayT2FMapMatrix = multiplyMapMatrix(arrayScale,
								multiplyMapMatrix(arrayZRotation,
								multiplyMapMatrix(arrayYRotation,
								multiplyMapMatrix(arrayXRotation,
                                multiplyMapMatrix(arrayMultiplication, marrayT2FMapMatrix)))));
			try {
				marrayF2TMapMatrix = invertSquareMatrix(marrayT2FMapMatrix);
			} catch(ArithmeticException e)	{
				marrayF2TMapMatrix = new double[4][4];
			}
		}
	}
	
	public void setLinearMapper(double dArchXInFROM, double dArchYInFROM, double dArchZInFROM,
			double dXRotationFROM2TO, double dYRotationFROM2TO, double dZRotationFROM2TO,
			double dRatioXTOOverFROM, double dRatioYTOOverFROM, double dRatioZTOOverFROM,
			double dArchXInTO, double dArchYInTO, double dArchZInTO)	{
		marrayF2TMapMatrix = new double[4][4];
		marrayT2FMapMatrix = new double[4][4];
		int nSize = marrayF2TMapMatrix.length;
		for (int idxI = 0; idxI < nSize; idxI++)
		{
			for (int idxJ = 0; idxJ < nSize; idxJ++)
			{
				marrayF2TMapMatrix[idxI][idxJ] = marrayT2FMapMatrix[idxI][idxJ]
						= (idxI == idxJ) ? 1 : 0;
			}
		}
		adjustLinearMapper4TOChange(dArchXInFROM, dArchYInFROM, dArchZInFROM,
			dXRotationFROM2TO, dYRotationFROM2TO, dZRotationFROM2TO,
			dRatioXTOOverFROM, dRatioYTOOverFROM, dRatioZTOOverFROM,
			dArchXInTO, dArchYInTO, dArchZInTO);
	}
	
	public void adjustLinearMapper4TOChange(double dArchXInFROM, double dArchYInFROM, double dArchZInFROM,
			double dXRotationFROM2TO, double dYRotationFROM2TO, double dZRotationFROM2TO,
			double dRatioXTOOverFROM, double dRatioYTOOverFROM, double dRatioZTOOverFROM,
			double dArchXInTO, double dArchYInTO, double dArchZInTO)	{
		
		double[][] arrayShiftOLD2NEWInFROM = {{1, 0, 0, -dArchXInFROM},
				{0, 1, 0, -dArchYInFROM}, {0, 0, 1, -dArchZInFROM},{0,0,0,1}};
		double[][] arrayShiftOLD2NEWInTO = {{1, 0, 0, -dArchXInTO},
				{0, 1, 0, -dArchYInTO}, {0, 0, 1, -dArchZInTO},{0,0,0,1}};
		double[][] arrayShiftNEW2OLDInFROM = {{1, 0, 0, dArchXInFROM},
				{0, 1, 0, dArchYInFROM}, {0, 0, 1, dArchZInFROM},{0,0,0,1}};
		double[][] arrayShiftNEW2OLDInTO = {{1, 0, 0, dArchXInTO},
				{0, 1, 0, dArchYInTO}, {0, 0, 1, dArchZInTO},{0,0,0,1}};

		// rotate about x seems mean that see from 0 to 1, anti-clock-wise rotation means positive angle, clock-wise rotation means negative angle. 
		double[][] arrayXRotationFROM2TO = {{1,0,0,0},
								{0, Math.cos(dXRotationFROM2TO), -Math.sin(dXRotationFROM2TO), 0},
								{0, Math.sin(dXRotationFROM2TO), Math.cos(dXRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayYRotationFROM2TO = {{Math.cos(dYRotationFROM2TO),0,Math.sin(dYRotationFROM2TO),0},
								{0, 1, 0, 0},
								{-Math.sin(dYRotationFROM2TO), 0, Math.cos(dYRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayZRotationFROM2TO = {{Math.cos(dZRotationFROM2TO), -Math.sin(dZRotationFROM2TO), 0, 0},
								{Math.sin(dZRotationFROM2TO), Math.cos(dZRotationFROM2TO), 0, 0},
								{0, 0, 1, 0},
								{0,0,0,1}};
		double[][] arrayXRotationTO2FROM = {{1,0,0,0},
								{0, Math.cos(dXRotationFROM2TO), Math.sin(dXRotationFROM2TO), 0},
								{0, -Math.sin(dXRotationFROM2TO), Math.cos(dXRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayYRotationTO2FROM = {{Math.cos(dYRotationFROM2TO),0,-Math.sin(dYRotationFROM2TO),0},
								{0, 1, 0, 0},
								{Math.sin(dYRotationFROM2TO), 0, Math.cos(dYRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayZRotationTO2FROM = {{Math.cos(dZRotationFROM2TO), Math.sin(dZRotationFROM2TO), 0, 0},
								{-Math.sin(dZRotationFROM2TO), Math.cos(dZRotationFROM2TO), 0, 0},
								{0, 0, 1, 0},
								{0,0,0,1}};

		double[][] arrayScaleFROM2TO = { { dRatioXTOOverFROM, 0, 0, 0 }, { 0, dRatioYTOOverFROM, 0, 0 }, { 0, 0, dRatioZTOOverFROM, 0 }, { 0, 0, 0, 1 } };
		double[][] arrayScaleTO2FROM = { { 1/dRatioXTOOverFROM, 0, 0, 0 }, { 0, 1/dRatioYTOOverFROM, 0, 0 }, { 0, 0, 1/dRatioZTOOverFROM, 0 }, { 0, 0, 0, 1 } };

		marrayF2TMapMatrix = multiplyMapMatrix(arrayShiftNEW2OLDInTO,
				multiplyMapMatrix(arrayZRotationFROM2TO,
				multiplyMapMatrix(arrayYRotationFROM2TO,
				multiplyMapMatrix(arrayXRotationFROM2TO, 
				multiplyMapMatrix(arrayScaleFROM2TO,
				multiplyMapMatrix(arrayShiftOLD2NEWInFROM, 
						marrayF2TMapMatrix))))));
		
		marrayT2FMapMatrix = multiplyMapMatrix(marrayT2FMapMatrix,
                multiplyMapMatrix(arrayShiftNEW2OLDInFROM,
				multiplyMapMatrix(arrayZRotationTO2FROM, //FFROM2TO,
				multiplyMapMatrix(arrayYRotationTO2FROM,
				multiplyMapMatrix(arrayXRotationTO2FROM,
				multiplyMapMatrix(arrayScaleTO2FROM, arrayShiftOLD2NEWInTO))))));
	}
	
	public void adjustLinearMapper4FROMChange(double dArchXInFROM, double dArchYInFROM, double dArchZInFROM,
			double dXRotationFROM2TO, double dYRotationFROM2TO, double dZRotationFROM2TO,
			double dRatioXTOOverFROM, double dRatioYTOOverFROM, double dRatioZTOOverFROM,
			double dArchXInTO, double dArchYInTO, double dArchZInTO)	{
		
		double[][] arrayShiftOLD2NEWInFROM = {{1, 0, 0, -dArchXInFROM},
				{0, 1, 0, -dArchYInFROM}, {0, 0, 1, -dArchZInFROM},{0,0,0,1}};
		double[][] arrayShiftOLD2NEWInTO = {{1, 0, 0, -dArchXInTO},
				{0, 1, 0, -dArchYInTO}, {0, 0, 1, -dArchZInTO},{0,0,0,1}};
		double[][] arrayShiftNEW2OLDInFROM = {{1, 0, 0, dArchXInFROM},
				{0, 1, 0, dArchYInFROM}, {0, 0, 1, dArchZInFROM},{0,0,0,1}};
		double[][] arrayShiftNEW2OLDInTO = {{1, 0, 0, dArchXInTO},
				{0, 1, 0, dArchYInTO}, {0, 0, 1, dArchZInTO},{0,0,0,1}};

		// rotate about x seems mean that see from 0 to 1, anti-clock-wise rotation means positive angle, clock-wise rotation means negative angle. 
		double[][] arrayXRotationFROM2TO = {{1,0,0,0},
								{0, Math.cos(dXRotationFROM2TO), -Math.sin(dXRotationFROM2TO), 0},
								{0, Math.sin(dXRotationFROM2TO), Math.cos(dXRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayYRotationFROM2TO = {{Math.cos(dYRotationFROM2TO),0,Math.sin(dYRotationFROM2TO),0},
								{0, 1, 0, 0},
								{-Math.sin(dYRotationFROM2TO), 0, Math.cos(dYRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayZRotationFROM2TO = {{Math.cos(dZRotationFROM2TO), -Math.sin(dZRotationFROM2TO), 0, 0},
								{Math.sin(dZRotationFROM2TO), Math.cos(dZRotationFROM2TO), 0, 0},
								{0, 0, 1, 0},
								{0,0,0,1}};
		double[][] arrayXRotationTO2FROM = {{1,0,0,0},
								{0, Math.cos(dXRotationFROM2TO), Math.sin(dXRotationFROM2TO), 0},
								{0, -Math.sin(dXRotationFROM2TO), Math.cos(dXRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayYRotationTO2FROM = {{Math.cos(dYRotationFROM2TO),0,-Math.sin(dYRotationFROM2TO),0},
								{0, 1, 0, 0},
								{Math.sin(dYRotationFROM2TO), 0, Math.cos(dYRotationFROM2TO), 0},
								{0,0,0,1}};
		double[][] arrayZRotationTO2FROM = {{Math.cos(dZRotationFROM2TO), Math.sin(dZRotationFROM2TO), 0, 0},
								{-Math.sin(dZRotationFROM2TO), Math.cos(dZRotationFROM2TO), 0, 0},
								{0, 0, 1, 0},
								{0,0,0,1}};

		double[][] arrayScaleFROM2TO = { { dRatioXTOOverFROM, 0, 0, 0 }, { 0, dRatioYTOOverFROM, 0, 0 }, { 0, 0, dRatioZTOOverFROM, 0 }, { 0, 0, 0, 1 } };
		double[][] arrayScaleTO2FROM = { { 1/dRatioXTOOverFROM, 0, 0, 0 }, { 0, 1/dRatioYTOOverFROM, 0, 0 }, { 0, 0, 1/dRatioZTOOverFROM, 0 }, { 0, 0, 0, 1 } };

		marrayF2TMapMatrix = multiplyMapMatrix(marrayF2TMapMatrix,
				multiplyMapMatrix(arrayShiftNEW2OLDInFROM,
				multiplyMapMatrix(arrayZRotationTO2FROM, //FFROM2TO,
				multiplyMapMatrix(arrayYRotationTO2FROM,
				multiplyMapMatrix(arrayXRotationTO2FROM,
				multiplyMapMatrix(arrayScaleTO2FROM, arrayShiftOLD2NEWInTO))))));
 		
		marrayT2FMapMatrix = multiplyMapMatrix(arrayShiftNEW2OLDInTO,
				multiplyMapMatrix(arrayZRotationFROM2TO,
				multiplyMapMatrix(arrayYRotationFROM2TO,
				multiplyMapMatrix(arrayXRotationFROM2TO, 
				multiplyMapMatrix(arrayScaleFROM2TO,
				multiplyMapMatrix(arrayShiftOLD2NEWInFROM, marrayT2FMapMatrix))))));
	}
    
    public LinearMapper multiply(LinearMapper mapperRight)  {
    	LinearMapper mapperReturn = new LinearMapper();
        mapperReturn.marrayF2TMapMatrix = multiplyMapMatrix(marrayF2TMapMatrix, mapperRight.marrayF2TMapMatrix);
        mapperReturn.marrayT2FMapMatrix = multiplyMapMatrix(mapperRight.marrayT2FMapMatrix, marrayT2FMapMatrix);
        return mapperReturn;
    }
	
    @Override
	public Position3D mapFrom2To(Position3D from)	{
		double[] arrayFrom = {from.getX(), from.getY(), from.getZ(), 1};
		double[] arrayTo = mapVector(marrayF2TMapMatrix, arrayFrom);
		return new Position3D(arrayTo[0], arrayTo[1], arrayTo[2]);
	}
	
    @Override
	public Position3D mapTo2From(Position3D to)	{
		double[] arrayTo = { to.getX(), to.getY(), to.getZ(), 1 };
		double[] arrayFrom = mapVector(marrayT2FMapMatrix, arrayTo);
		return new Position3D(arrayFrom[0], arrayFrom[1], arrayFrom[2]);
	}


}
