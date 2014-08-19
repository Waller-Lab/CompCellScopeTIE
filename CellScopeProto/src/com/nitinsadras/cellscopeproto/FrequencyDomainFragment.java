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
		getPhaseImage();
		//filter();
	}
	
	public void getPhaseImage(){
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.box60v);
		image= image.copy(Bitmap.Config.ARGB_8888, true);  
		iv = (ImageView) getView().findViewById(R.id.imageView2);
		
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
		double delta_z = .000001;
		double offset = .001; //avoid division by zero errors
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
				//Log.d("frequency", Double.toString(g[curr_real_index]));
				g[curr_real_index] = g[curr_real_index]/denom;
				g[curr_im_index] = g[curr_im_index]/denom;
				//Log.d("frequency", Double.toString(g[curr_real_index]));
				
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
		writeArrayToFile(phase_raw);
		
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < phase_raw.length; i++){
			if(phase_raw[i] > max){
				max = phase_raw[i];
			} else if(phase_raw[i] < min){
				min = phase_raw[i];
			}
		}
		
		int[] colorArray = {0xfffafa,0xf8f8ff,0xf8f8ff,0xf5f5f5,0xf5f5f5,0xdcdcdc,0xfffaf0,0xfffaf0,0xfdf5e6,0xfdf5e6,0xfaf0e6,0xfaebd7,0xfaebd7,0xffefd5,0xffefd5,0xffebcd,0xffebcd,0xffe4c4,0xffdab9,0xffdab9,0xffdead,0xffdead,0xffe4b5,0xfff8dc,0xfffff0,0xfffacd,0xfffacd,0xfff5ee,0xf0fff0,0xf5fffa,0xf5fffa,0xf0ffff,0xf0f8ff,0xf0f8ff,0xe6e6fa,0xfff0f5,0xfff0f5,0xffe4e1,0xffe4e1,0xffffff,0x000000,0x2f4f4f,0x2f4f4f,0x2f4f4f,0x2f4f4f,0x696969,0x696969,0x696969,0x696969,0x708090,0x708090,0x708090,0x708090,0x778899,0x778899,0x778899,0x778899,0xbebebe,0xbebebe,0xd3d3d3,0xd3d3d3,0xd3d3d3,0xd3d3d3,0x191970,0x191970,0x000080,0x000080,0x000080,0x6495ed,0x6495ed,0x483d8b,0x483d8b,0x6a5acd,0x6a5acd,0x7b68ee,0x7b68ee,0x8470ff,0x8470ff,0x0000cd,0x0000cd,0x4169e1,0x4169e1,0x0000ff,0x1e90ff,0x1e90ff,0x00bfff,0x00bfff,0x87ceeb,0x87ceeb,0x87cefa,0x87cefa,0x4682b4,0x4682b4,0xb0c4de,0xb0c4de,0xadd8e6,0xadd8e6,0xb0e0e6,0xb0e0e6,0xafeeee,0xafeeee,0x00ced1,0x00ced1,0x48d1cc,0x48d1cc,0x40e0d0,0x00ffff,0xe0ffff,0xe0ffff,0x5f9ea0,0x5f9ea0,0x66cdaa,0x66cdaa,0x7fffd4,0x006400,0x006400,0x556b2f,0x556b2f,0x8fbc8f,0x8fbc8f,0x2e8b57,0x2e8b57,0x3cb371,0x3cb371,0x20b2aa,0x20b2aa,0x98fb98,0x98fb98,0x00ff7f,0x00ff7f,0x7cfc00,0x7cfc00,0x00ff00,0x7fff00,0x00fa9a,0x00fa9a,0xadff2f,0xadff2f,0x32cd32,0x32cd32,0x9acd32,0x9acd32,0x228b22,0x228b22,0x6b8e23,0x6b8e23,0xbdb76b,0xbdb76b,0xf0e68c,0xeee8aa,0xeee8aa,0xfafad2,0xfafad2,0xffffe0,0xffffe0,0xffff00,0xffd700,0xeedd82,0xeedd82,0xdaa520,0xb8860b,0xb8860b,0xbc8f8f,0xbc8f8f,0xcd5c5c,0xcd5c5c,0x8b4513,0x8b4513,0xa0522d,0xcd853f,0xdeb887,0xf5f5dc,0xf5deb3,0xf4a460,0xf4a460,0xd2b48c,0xd2691e,0xb22222,0xa52a2a,0xe9967a,0xe9967a,0xfa8072,0xffa07a,0xffa07a,0xffa500,0xff8c00,0xff8c00,0xff7f50,0xf08080,0xf08080,0xff6347,0xff4500,0xff4500,0xff0000,0xff69b4,0xff69b4,0xff1493,0xff1493,0xffc0cb,0xffb6c1,0xffb6c1,0xdb7093,0xdb7093,0xb03060,0xc71585,0xc71585,0xd02090,0xd02090,0xff00ff,0xee82ee,0xdda0dd,0xda70d6,0xba55d3,0xba55d3,0x9932cc,0x9932cc,0x9400d3,0x9400d3,0x8a2be2,0x8a2be2,0xa020f0,0x9370db,0x9370db,0xd8bfd8,0xfffafa,0xeee9e9,0xcdc9c9,0x8b8989,0xfff5ee,0xeee5de,0xcdc5bf,0x8b8682,0xffefdb,0xeedfcc,0xcdc0b0,0x8b8378,0xffe4c4,0xeed5b7,0xcdb79e,0x8b7d6b,0xffdab9,0xeecbad,0xcdaf95,0x8b7765,0xffdead,0xeecfa1,0xcdb38b,0x8b7900,0xfffacd,0xeee9bf,0xcdc9a5,0x8b8970,0xfff8dc,0xeee8cd,0xcdc8b1,0x8b8878,0xfffff0,0xeeeee0,0xcdcdc1,0x8b8b83,0xf0fff0,0xe0eee0,0xc1cdc1,0x838b83,0xfff0f5,0xeee0e5,0xcdc1c5,0x8b8386,0xffe4e1,0xeed5d2,0xcdb7b5,0x8b7d7b,0xf0ffff,0xe0eeee,0xc1cdcd,0x838b8b,0x836fff,0x7a67ee,0x6959cd,0x473c8b,0x4876ff,0x436eee,0x3a5fcd,0x27408b,0x0000ff,0x0000ee,0x0000cd,0x00008b,0x1e90ff,0x1c86ee,0x1874cd,0x104e8b,0x63b8ff,0x5cacee,0x4f94cd,0x36648b,0x00bfff,0x00b2ee,0x009acd,0x00688b,0x87ceff,0x7ec0ee,0x6ca6cd,0x4a708b,0xb0e2ff,0xa4d3ee,0x8db6cd,0x607b8b,0xc6e2ff,0xb9d3ee,0x9fb6cd,0x6c7b8b,0xcae1ff,0xbcd2ee,0xa2b5cd,0x6e7b8b,0xbfefff,0xb2dfee,0x9ac0cd,0x68838b,0xe0ffff,0xd1eeee,0xb4cdcd,0x7a8b8b,0xbbffff,0xaeeeee,0x96cdcd,0x668b8b,0x98f5ff,0x8ee5ee,0x7ac5cd,0x53868b,0x00f5ff,0x00e5ee,0x00c5cd,0x00868b,0x00ffff,0x00eeee,0x00cdcd,0x008b8b,0x97ffff,0x8deeee,0x79cdcd,0x528b8b,0x7fffd4,0x76eec6,0x66cdaa,0x458b74,0xc1ffc1,0xb4eeb4,0x9bcd9b,0x698b69,0x54ff9f,0x4eee94,0x43cd80,0x2e8b00,0x9aff9a,0x90ee90,0x7ccd7c,0x548b00,0x00ff7f,0x00ee76,0x00cd66,0x008b00,0x00ff00,0x00ee00,0x00cd00,0x008b00,0x7fff00,0x76ee00,0x66cd00,0x458b00,0xc0ff00,0xb3ee00,0x9acd00,0x698b00,0xcaff70,0xbcee68,0xa2cd00,0x6e8b00,0xfff68f,0xeee685,0xcdc673,0x8b8600,0xffec8b,0xeedc82,0xcdbe70,0x8b8100,0xffffe0,0xeeeed1,0xcdcdb4,0x8b8b7a,0xffff00,0xeeee00,0xcdcd00,0x8b8b00,0xffd700,0xeec900,0xcdad00,0x8b7500,0xffc100,0xeeb400,0xcd9b00,0x8b6900,0xffb900,0xeead00,0xcd9500,0x8b6500,0xffc1c1,0xeeb4b4,0xcd9b9b,0x8b6969,0xff6a6a,0xee6300,0xcd5500,0x8b3a00,0xff8200,0xee7900,0xcd6800,0x8b4700,0xffd39b,0xeec591,0xcdaa7d,0x8b7300,0xffe7ba,0xeed8ae,0xcdba96,0x8b7e66,0xffa500,0xee9a00,0xcd8500,0x8b5a00,0xff7f00,0xee7600,0xcd6600,0x8b4500,0xff3000,0xee2c00,0xcd2600,0x8b1a00,0xff4000,0xee3b00,0xcd3300,0x8b2300,0xff8c69,0xee8200,0xcd7000,0x8b4c00,0xffa07a,0xee9572,0xcd8100,0x8b5700,0xffa500,0xee9a00,0xcd8500,0x8b5a00,0xff7f00,0xee7600,0xcd6600,0x8b4500,0xff7200,0xee6a00,0xcd5b00,0x8b3e00,0xff6300,0xee5c00,0xcd4f00,0x8b3600,0xff4500,0xee4000,0xcd3700,0x8b2500,0xff0000,0xee0000,0xcd0000,0x8b0000,0xff1493,0xee1289,0xcd1076,0x8b0a00,0xff6eb4,0xee6aa7,0xcd6090,0x8b3a62,0xffb5c5,0xeea9b8,0xcd919e,0x8b636c,0xffaeb9,0xeea2ad,0xcd8c95,0x8b5f65,0xff82ab,0xee799f,0xcd6889,0x8b4700,0xff34b3,0xee30a7,0xcd2990,0x8b1c00,0xff3e96,0xee3a8c,0xcd3278,0x8b2200,0xff00ff,0xee00ee,0xcd00cd,0x8b008b,0xff83fa,0xee7ae9,0xcd69c9,0x8b4789,0xffbbff,0xeeaeee,0xcd96cd,0x8b668b,0xe066ff,0xd15fee,0xb452cd,0x7a378b,0xbf3eff,0xb23aee,0x9a32cd,0x68228b,0x9b30ff,0x912cee,0x7d26cd,0x551a8b,0xab82ff,0x9f79ee,0x8968cd,0x5d478b,0xffe1ff,0xeed2ee,0xcdb5cd,0x8b7b8b,0x000000,0x000000,0x030303,0x030303,0x050505,0x050505,0x080808,0x080808,0x0a0a0a,0x0a0a0a,0x0d0d0d,0x0d0d0d,0x0f0f0f,0x0f0f0f,0x121212,0x121212,0x141414,0x141414,0x171717,0x171717,0x1a1a1a,0x1a1a1a,0x1c1c1c,0x1c1c1c,0x1f1f1f,0x1f1f1f,0x212121,0x212121,0x242424,0x242424,0x262626,0x262626,0x292929,0x292929,0x2b2b2b,0x2b2b2b,0x2e2e2e,0x2e2e2e,0x303030,0x303030,0x333333,0x333333,0x363636,0x363636,0x383838,0x383838,0x3b3b3b,0x3b3b3b,0x3d3d3d,0x3d3d3d,0x404040,0x404040,0x424242,0x424242,0x454545,0x454545,0x474747,0x474747,0x4a4a4a,0x4a4a4a,0x4d4d4d,0x4d4d4d,0x4f4f4f,0x4f4f4f,0x525252,0x525252,0x545454,0x545454,0x575757,0x575757,0x595959,0x595959,0x5c5c5c,0x5c5c5c,0x5e5e5e,0x5e5e5e,0x616161,0x616161,0x636363,0x636363,0x666666,0x666666,0x696969,0x696969,0x6b6b6b,0x6b6b6b,0x6e6e6e,0x6e6e6e,0x707070,0x707070,0x737373,0x737373,0x757575,0x757575,0x787878,0x787878,0x7a7a7a,0x7a7a7a,0x7d7d7d,0x7d7d7d,0x7f7f7f,0x7f7f7f,0x828282,0x828282,0x858585,0x858585,0x878787,0x878787,0x8a8a8a,0x8a8a8a,0x8c8c8c,0x8c8c8c,0x8f8f8f,0x8f8f8f,0x919191,0x919191,0x949494,0x949494,0x969696,0x969696,0x999999,0x999999,0x9c9c9c,0x9c9c9c,0x9e9e9e,0x9e9e9e,0xa1a1a1,0xa1a1a1,0xa3a3a3,0xa3a3a3,0xa6a6a6,0xa6a6a6,0xa8a8a8,0xa8a8a8,0xababab,0xababab,0xadadad,0xadadad,0xb0b0b0,0xb0b0b0,0xb3b3b3,0xb3b3b3,0xb5b5b5,0xb5b5b5,0xb8b8b8,0xb8b8b8,0xbababa,0xbababa,0xbdbdbd,0xbdbdbd,0xbfbfbf,0xbfbfbf,0xc2c2c2,0xc2c2c2,0xc4c4c4,0xc4c4c4,0xc7c7c7,0xc7c7c7,0xc9c9c9,0xc9c9c9,0xcccccc,0xcccccc,0xcfcfcf,0xcfcfcf,0xd1d1d1,0xd1d1d1,0xd4d4d4,0xd4d4d4,0xd6d6d6,0xd6d6d6,0xd9d9d9,0xd9d9d9,0xdbdbdb,0xdbdbdb,0xdedede,0xdedede,0xe0e0e0,0xe0e0e0,0xe3e3e3,0xe3e3e3,0xe5e5e5,0xe5e5e5,0xe8e8e8,0xe8e8e8,0xebebeb,0xebebeb,0xededed,0xededed,0xf0f0f0,0xf0f0f0,0xf2f2f2,0xf2f2f2,0xf5f5f5,0xf5f5f5,0xf7f7f7,0xf7f7f7,0xfafafa,0xfafafa,0xfcfcfc,0xfcfcfc,0xffffff,0xffffff,0xa9a9a9,0xa9a9a9,0xa9a9a9,0xa9a9a9,0x00008b,0x00008b,0x008b8b,0x008b8b,0x8b008b,0x8b008b,0x8b0000,0x8b0000,0x90ee90,0x90ee90};
		
		ColorMap map = new ColorMap(colorArray, min,max);
		
		int[] phase_colored = new int[image.getWidth()*image.getHeight()];
		for(int i = 0; i < phase_colored.length; i++ ){
			phase_colored[i] = (0xFF << 24) + map.getColor(phase_raw[i]);
		}
		Bitmap phase_bmp = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		phase_bmp.setPixels(phase_colored, 0, image.getWidth(), 0,0, image.getWidth(), image.getHeight() );
		iv.setImageBitmap(phase_bmp);   
		
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
