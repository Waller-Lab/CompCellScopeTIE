package com.nitinsadras.cellscopeproto;

import java.io.FileOutputStream;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import edu.emory.mathcs.jtransforms.fft.RealFFTUtils_2D;
import prefuse.util.*;


public class FrequencyDomainFragment extends Fragment {
	
	public ImageView iv;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_frequencydomain, container, false);
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.box60v_500);
		Bitmap nothingImage = BitmapFactory.decodeResource(getResources(), R.drawable.nothing_500);
		
		double[] heightMapArr = getPhaseImage(image);
		double[] backgroundArr = getPhaseImage(nothingImage);
		
		for(int i = 0; i < heightMapArr.length; i++){
			heightMapArr[i] /= backgroundArr[i];
		}
		
		int[] greyscaleHeightMapArr = rawToGreyscale(heightMapArr);
		
		Bitmap result = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		result.setPixels(greyscaleHeightMapArr, 0, image.getWidth(), 0,0, image.getWidth(), image.getHeight() );
		iv = (ImageView) getView().findViewById(R.id.imageView2);
		iv.setImageBitmap(result);   

	}
	
	public double[] getPhaseImage(Bitmap image){
		image= image.copy(Bitmap.Config.ARGB_8888, true);  
		
		int[] imageArray = new int[image.getWidth()*image.getHeight()];
		image.getPixels(imageArray, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
		
		
		double red[] = new double[imageArray.length];
		double blue[] = new double[imageArray.length];
		double green[] = new double[imageArray.length];
				
		//split color channels
		for(int i=0; i<imageArray.length; i++) {
			int curr_pixel = imageArray[i];
			red[i] = (curr_pixel >> 16) & 0xff;
			green[i] = (curr_pixel >> 8) & 0xff;
			blue[i] = curr_pixel & 0xff;
		}

		//normalize by mean
		double red_mean = mean(red);
		double green_mean = mean(green);
		double blue_mean = mean(blue);
		
		for(int i=0; i < imageArray.length; i++){
			red[i] /= red_mean;
			green[i] /= green_mean;
			blue[i] /= blue_mean;
		}

		double g[] = new double[imageArray.length*2];
		
		double k = 1.0;
		double delta_z = 100;
		double offset = .01; //avoid division by zero errors
		for(int i=0; i<imageArray.length; i++) {
			g[i] = -(k/(green[i]+offset))*((blue[i] - red[i])/delta_z);
		}
		
        /** frequency domain **/
		DoubleFFT_2D dfft = new DoubleFFT_2D(image.getHeight(), image.getWidth());
		dfft.realForwardFull(g); // g now holds fft{g}
		
		//generate meshgrid
		int height = image.getHeight();
		int width = image.getWidth();
		int[] meshX = meshGridX(width);
		int[] meshY = meshGridY(height);
		
		//poisson magic
		double epsilon = .1; // user control
		double pi_squared = Math.pow(Math.PI, 2);
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				int curr_real_index = y*2*width + 2*x;
				int curr_im_index = y*2*width + 2*x+1;
				double denom = (-4*pi_squared)*(Math.pow(meshX[x], 2) + Math.pow(meshY[y], 2)) + epsilon;
				g[curr_real_index] = g[curr_real_index]/denom;
				g[curr_im_index] = g[curr_im_index]/denom;				
			}
		}
		
		dfft.complexInverse(g, true);
		
	
		double[] phase_raw = new double[imageArray.length];
		int cols = width;
		// recover image from complexInverse output
		for(int x=0; x < image.getWidth(); x++){
			for(int y=0; y < image.getHeight(); y++){
				double real = g[(y*2*cols) + 2*x];
				double im = g[(y*2*cols) + 2*x + 1]; // should, by all means, be zero
				int mag = (int) Math.sqrt(Math.pow(real, 2) + Math.pow(im, 2));
				phase_raw[(y*cols)+x] = (real);
			}
		}
		
		Log.d("filter", "Image array length: " + Integer.toString(phase_raw.length));
		Log.d("filter", "Image height: " + Integer.toString(image.getHeight()));
		Log.d("filter", "Image width: " + Integer.toString(image.getWidth()));		
		
		return phase_raw;
				
	}
	
	public int[] rawToGreyscale(double[] in){
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < in.length; i++){
			if(in[i] > max){
				max = in[i];
			} else if(in[i] < min){
				min = in[i];
			}
		}
		
		int[] colorArray = new int[256];
		for(int i = 0; i < 256; i++){
			int color = 255-i;
			colorArray[i] = (color << 16) + (color << 8) + color;
		}
		ColorMap map = new ColorMap(colorArray, min,max);
		
		int[] out = new int[in.length];
		for(int i = 0; i < out.length; i++ ){
			out[i] = (0xFF << 24) + map.getColor(in[i]);
		}
		return out;
	}
	
	/***** utility/helper functions *****/
	
	public void printLong(double[] in){

		String veryLongString = Arrays.toString(in);
		int maxLogSize = 4000;
		for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
		    int start = i * maxLogSize;
		    int end = (i+1) * maxLogSize;
		    end = end > veryLongString.length() ? veryLongString.length() : end;
		    Log.v("output", veryLongString.substring(start, end));
		}
	}
	
	public void writeArrayToFile(double[] in){
		String filename = "POISSON_OUTPUT";
		MainActivity parent = (MainActivity) this.getActivity();
		try{
			FileOutputStream fos = parent.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(Arrays.toString(in).getBytes());
			fos.close();
			Log.d("success", "wrote to file");
		} catch (Exception e){
			e.printStackTrace();
		}
		// move file to computer using adb pull
	}
	
	public int[] meshGridX(int width){
		int[] mesh_x = new int[width];
		for(int i = 0; i < width; i++){
			if(i <= (width-1)/2){
				mesh_x[i] = i;
			} else {
				mesh_x[i] = i - width;
			}
		}
		return mesh_x;
	}
	
	public int[] meshGridY(int height){
		int[] mesh_y = new int[height];
		for(int i = 0; i < height; i++){
			if(i <= (height-1)/2){
				mesh_y[i] = -1*i;
			} else {
				mesh_y[i] = height - i;
			}
		}
		return mesh_y;
	}
	
	public double mean(double[] in){
		double sum = 0;
		for(int i = 0; i < in.length; i++){
			sum += in[i];
		}
		return sum/in.length;
	}
	
	
	public void filter(){
		//Drawable image = getResources().getDrawable(R.drawable.waterfall);
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.box60v);
		image= image.copy(Bitmap.Config.ARGB_8888, true);  
		iv = (ImageView) getView().findViewById(R.id.imageView2);
		
		int[] imageArray = new int[image.getWidth()*image.getHeight()];
		image.getPixels(imageArray, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
		
		

		double[] doubleImageArray = new double[imageArray.length*2];
		
		for(int i=0; i<imageArray.length; i++) {
		    doubleImageArray[i] = imageArray[i];
		}
		

		DoubleFFT_2D dfft = new DoubleFFT_2D(image.getHeight(), image.getWidth());
		RealFFTUtils_2D utils = new RealFFTUtils_2D(image.getHeight(), image.getWidth());
		
		dfft.realForwardFull(doubleImageArray);
		
		int limit = 2;
		int cols = image.getWidth();
		int width = image.getWidth();
		int height = image.getHeight();
		/**Log.d("filter", "Image array length: " + Integer.toString(doubleImageArray.length));
		Log.d("filter", "Image height: " + Integer.toString(image.getHeight()));
		Log.d("filter", "Image width: " + Integer.toString(image.getWidth()));
		
	
		String veryLongString = Arrays.toString(doubleImageArray);
		int maxLogSize = 1000;
		for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
		    int start = i * maxLogSize;
		    int end = (i+1) * maxLogSize;
		    end = end > veryLongString.length() ? veryLongString.length() : end;
		    Log.v("filter", veryLongString.substring(start, end));
		}**/
		
		// frequency domain high pass filter
		// TODO: fix coloring issues
		/**for(int x=0; x < width; x ++){
			for(int y=0; y < height; y++){
				boolean left = x < limit;
				boolean right = x > width - limit;
				boolean top = y < limit;
				boolean bottom = y > width - limit;
				if(left || right || top || bottom){
					//Log.d("filter", "loop-dee-loop");
					try{
						utils.pack(0, y, 2*x, doubleImageArray, 0);
						utils.pack(0, height-y, 2*(width-x), doubleImageArray, 0);
					} catch (Exception e){
						// nothing to see here, move along
						Log.d("filter", "oh noes");
					}
					try{
						utils.pack(0, y, 2*x+1, doubleImageArray, 0);
						utils.pack(0, height-y, 2*(width-x)+1, doubleImageArray, 0);
					} catch (Exception e){
						Log.d("filter", "oh nose");
					}
				}
			}
		}**/
		
		
		dfft.complexInverse(doubleImageArray, true);
		
		
		int[] filteredArray = new int[imageArray.length];
		
		// recover image from complexInverse output
		for(int x=0; x < image.getWidth(); x++){
			for(int y=0; y < image.getHeight(); y++){
				double real = doubleImageArray[(y*2*cols) + 2*x];
				double im = doubleImageArray[(y*2*cols) + 2*x + 1]; // should, by all means, be zero
				int mag = (int) Math.sqrt(Math.pow(real, 2) + Math.pow(im, 2));
				filteredArray[(y*cols)+x] = (int) (mag);
				//Log.d("filter", Double.toString(im));
				//filteredArray[(y*cols)+x] = (int) doubleImageArray[(y*2*cols) + 2*x];
			}
		}
		/**for(int i = 0; i < imageArray.length; i++){
			filteredArray[i] = (int) doubleImageArray[i];
		}**/
		Bitmap fftBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		fftBitmap.setPixels(filteredArray, 0, image.getWidth(), 0,0, image.getWidth(), image.getHeight() );
		//fftBitmap.copyPixelsFromBuffer(makeBuffer(fftIntArray, fftIntArrayhttp://stackoverflow.com/questions/12274170/how-to-convert-2d-int-array-to-bitmap-in-androi.length));
		
		iv.setImageBitmap(fftBitmap);
		
	}
	public void printArray(double[] arr, int width, int height){
		
	}
	

}
