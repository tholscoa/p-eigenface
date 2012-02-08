/*Packages cern.colt* , cern.jet*, cern.clhep

Copyright (c) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose is hereby granted without fee, provided that the above copyright notice appear in all copies and that both that copyright notice and this permission notice appear in supporting documentation. CERN makes no representations about the suitability of this software for any purpose. It is provided "as is" without expressed or implied warranty.*/

import java.util.Collections;
import java.util.Vector;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;


public class EigenFace {
	double[][] eigenFaces;
	double[] dataAverage;
	double[][] dataDifference;
	int selectedNumOfEigenFaces = 25;
	double[][] weights;

	public void initEigenfaces(double[][] dataSet) {
		// m.normalise();
		// difference from average
		dataAverage = MatrixMath.getAverage(dataSet);
		dataDifference = MatrixMath.subtractFromRows(dataSet, dataAverage);
		
		// covarianzmatrix
		double[][] mN = MatrixMath.multiply(dataDifference,
				MatrixMath.transpose(dataDifference));
		
		// eigenvaluedecomosition from cern
		DenseDoubleMatrix2D d = new DenseDoubleMatrix2D(mN);
		EigenvalueDecomposition e = new EigenvalueDecomposition(d);
		double[] eigenValues = MatrixMath.diag(e.getD().toArray());
		double[][] eigenVectors = e.getV().viewDice().toArray();
		
		// sorting
		Vector<EigenPair> eigenPairs = new Vector<EigenPair>();
		for (int i = 0; i < eigenValues.length; i++) {
			EigenPair ep = new EigenPair(eigenValues[i], eigenVectors[i]);
			eigenPairs.add(ep);
		}
		Collections.sort(eigenPairs);
		for (int i = 0; i < eigenPairs.size(); i++) {
			EigenPair ep = eigenPairs.get(i);
			eigenValues[i] = ep.eigenValue;
			eigenVectors[i] = ep.eigenVector;
		}
		eigenFaces = MatrixMath.multiply(eigenVectors, dataDifference);
		eigenFaces = MatrixMath.normalizeRows(eigenFaces);
		
		// calculate Weights
		double[][] eigenFacesPart = MatrixMath.getSubMatrix(eigenFaces, 0,
				selectedNumOfEigenFaces);
		weights = MatrixMath.multiply(dataDifference,
				MatrixMath.transpose(eigenFacesPart));
	}

	public double[] getWeights(double[] data, int selectedeigenfaces) {
		data = MatrixMath.subtract(data, dataAverage);
		// finding weights..
		double[][] eigenFacesMatrixPart = MatrixMath.getSubMatrix(eigenFaces,
				0, selectedeigenfaces);
		double[][] eigenFacesMatrixPartTranspose = MatrixMath
				.transpose(eigenFacesMatrixPart);
		double[] inputWtsArray = MatrixMath.multiply(data,
				eigenFacesMatrixPartTranspose);// single array
		return inputWtsArray;
	}

	public int findMatchResult(double[] inputWts, int selectedeigenfaces) {
		// calculating euclidean distance to wts from inputImages.
		double[] distances = new double[weights.length];
		for (int i = 0; i < weights.length; i++) {
			distances[i] = MatrixMath.getDistance(weights[i], inputWts);
		}
		// finding closest Face
		double minimumDistance = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] < minimumDistance) {
				minimumDistance = distances[i];
				index = i;
			}
		}
		return index;
	}

	public class EigenPair implements Comparable<EigenPair> {
		double eigenValue;
		double[] eigenVector;
		public EigenPair(double eigenValue, double[] eigenVector) {
			this.eigenValue = eigenValue;
			this.eigenVector = eigenVector;
		}
		public int compareTo(EigenPair other) {
			if (eigenValue > other.eigenValue)
				return -1;
			return 1;
		}
	}

}
