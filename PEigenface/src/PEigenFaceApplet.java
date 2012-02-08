

import java.io.File;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PImage;

public class PEigenFaceApplet  extends PApplet {
	PEigenface face;
	
	double[] weights=new double[10];
	public double[] offsets;
	
	public void setup() {
		size(700,840);
		String path = "../cathedrals";
		File[] files = this.getFilesFromFolder(path);
		PImage[] imageSet = new PImage[files.length];
		for (int i = 0; i < files.length; i++) {
			PImage img = this.loadImage(files[i].getAbsolutePath());
			imageSet[i] = img;
		}
		face=new PEigenface(this);
		face.initEigenfaces(imageSet);
		int nEigenfaces=20;
		weights=new double[nEigenfaces];
		offsets=new double[nEigenfaces];
		for (int i=0;i<offsets.length;i++){
			offsets[i]=random(30,50);
			if (random(1)>0.5){
				offsets[i]*=-1;
			}
			weights[i]=face.weights[0][i];
		}
	}
	public void draw(){
		scale(1f);
		for (int i=0;i<weights.length;i++){
			weights[i]+=offsets[i];
			if (weights[i]<=-3000){
				weights[i]=-3000;
				offsets[i]*=-1;
			}
			if (weights[i]>=5000){
				weights[i]=5000;
				offsets[i]*=-1;
			}
		}
		PImage img=face.getImageByWeights(weights,25);
		image(img,0,0,width,height);
	}
	
	public File[] getFilesFromFolder(String folderPath) {
		File dir = new File(folderPath);
		File[] files = dir.listFiles();
		Arrays.sort(files);
		return files;
	}
	
}



