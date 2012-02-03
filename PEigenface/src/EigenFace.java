



	import java.util.Collections;
	import java.util.Vector;

	import cern.colt.matrix.impl.DenseDoubleMatrix2D;
	import cern.colt.matrix.linalg.EigenvalueDecomposition;

	import processing.core.PApplet;
	import processing.core.PImage;

public class EigenFace {
		
		
		
		
		double[][] eigenFaces;
		
		double[] dataAverage;
		
		int selectedNumOfEigenFaces = 25;
		double[][] weights;

	

		
	public void initEigenfaces(double[][] dataSet) {
		
		// m.normalise();
		
		// differenz Bilder vom Durchschnitt
		dataAverage = MatrixMath.getAverage(dataSet);
		
		dataSet=MatrixMath.subtractFromRows(dataSet, dataAverage);
		

		// Kovarianzmatrix
		double[][] mN = MatrixMath.multiply(dataSet, MatrixMath.transpose(dataSet));

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
		
		
		eigenFaces=MatrixMath.multiply(eigenVectors, dataSet);
		eigenFaces=MatrixMath.normalizeRows(eigenFaces);

		// calculate Weights
		double[][] eigenFacesPart = MatrixMath.getSubMatrix(eigenFaces,0,
				selectedNumOfEigenFaces);
		weights=MatrixMath.multiply(dataSet, MatrixMath.transpose(eigenFacesPart));
		
		
	}
		
		public double[] getWeights(double[] data , int selectedeigenfaces){
			
			data=MatrixMath.subtract(data, dataAverage);
			
			// finding weights..
			double[][] eigenFacesMatrixPart =MatrixMath.getSubMatrix(eigenFaces, 0, selectedeigenfaces);
			double[][] eigenFacesMatrixPartTranspose =MatrixMath.transpose(eigenFacesMatrixPart);
			
			double[] inputWtsArray = MatrixMath.multiply(data, eigenFacesMatrixPartTranspose);// single array
			return inputWtsArray;
		}

		public int findMatchResult(double[] inputWts, int selectedeigenfaces) {
			MatrixMath.print(inputWts);
			
			// calculating euclidean distance to wts from inputImages.
			double[] distances = new double[weights.length];
			for (int i=0;i<weights.length;i++){
				distances[i]=MatrixMath.getDistance(weights[i], inputWts);
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
			public double eigenValue;
			public double[] eigenVector;

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
