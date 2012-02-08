
import processing.core.PApplet;
import processing.core.PImage;


public class PEigenface extends EigenFace {
	PApplet parent;
	int imageWidth=125;
	int imageHeight=150;
	PImage[] imagesSet = new PImage[25];
	PImage[] reconstrucetedIamges ;
	PImage[] imagesEigen;
	PImage[] imagesDistance;
	final static int RGB=2;
	final static int HSB=1;
	final static int GRAY=0;
	public PEigenface(PApplet parent) {
		this.parent=parent;
		// TODO Auto-generated constructor stub
	}

	public void initEigenfaces(PImage[] imageSet) {
		this.imagesSet = imageSet;
		double[][] dataSet = new double[imageSet.length][];
		for (int i = 0; i < imageSet.length; i++) {
			PImage img = imageSet[i];
			this.imageHeight=img.height;
			this.imageWidth=img.width;
			dataSet[i] = getBrightnessArray(img);
			imageSet[i] = img;
		}
		initEigenfaces(dataSet);
		
		// calculate Images
		imagesEigen = new PImage[eigenFaces.length];
		for (int i = 0; i < eigenFaces.length; i++) {
			PImage eigen = getNormalizedImage(imageWidth, imageHeight,0,255, eigenFaces[i]);
			imagesEigen[i] = eigen;
		}
		reconstrucetedIamges=this.reconstructFaces(selectedNumOfEigenFaces);
	}
	
	public double[] getWeights(PImage img , int selectedeigenfaces){
		double[] data = getBrightnessArray(img);
		return getWeights(data,selectedeigenfaces);
	}
	
	public int findMatchResult(PImage img, int selectedeigenfaces) {
		double[] inputWts=getWeights(img,selectedeigenfaces);
		return findMatchResult(inputWts,selectedeigenfaces);
	}
	
	public PImage[] reconstructFaces(int selectedeigenfaces) {			
		PImage[] images=new PImage[imagesSet.length];
		for (int i=0;i<imagesSet.length;i++){
			images[i]=getImageByWeights(weights[i],selectedeigenfaces);
		}
		return images;
	}
	
	public double[] getBrightnessArray(PImage img) {
		double[] data = new double[img.pixels.length];
		for (int ii = 0; ii < img.pixels.length; ii++) {
			float b = parent.brightness(img.pixels[ii]);
			data[ii] = b;
		}
		return data;
	}

	public PImage getImageByWeights(double[] weights,int selectedeigenfaces) {
		double[][] egnfacesSubMatrix = MatrixMath.getSubMatrix(eigenFaces, 0,
				selectedeigenfaces);
		double[] imageData=MatrixMath.multiply(weights, egnfacesSubMatrix);
		imageData = MatrixMath.add(imageData, dataAverage);
		return getImage(imageWidth, imageHeight, imageData);
	}
	public PImage getImageBW(int nX, int nY,float limit, double[] brightness) {
		PImage img = parent.createImage(nX, nY, PApplet.RGB);
		for (int i = 0; i < brightness.length; i++) {
			float b=Math.abs((int) brightness[i]);
			//if (b<0)b=0;
		if (b<limit)b=0;
			else{
				b=255;
			}
			img.pixels[i] = parent.color(b);
		}
		img.loadPixels();
		return img;
	}
	public PImage getImage(int nX, int nY, double[] brightness) {
		PImage img = parent.createImage(nX, nY, PApplet.RGB);
		for (int i = 0; i < brightness.length; i++) {
			float b=Math.abs((int) brightness[i]);
			if (b<0)b=0;
			img.pixels[i] = parent.color(b);
		}
		img.loadPixels();
		return img;
	}

	public PImage getNormalizedImage(int nX, int nY,float goalMin,float goalMax, double[] brightness) {
		PImage img = parent.createImage(nX, nY, PApplet.RGB);
		double min = MatrixMath.min(brightness);
		double max = MatrixMath.max(brightness);
		for (int i = 0; i < brightness.length; i++) {
			float cB = PApplet.map((float) brightness[i], (float) min,
					(float) max, goalMin, goalMax);
			img.pixels[i] = parent.color(cB);
		}
		img.loadPixels();
		return img;
	}

}
