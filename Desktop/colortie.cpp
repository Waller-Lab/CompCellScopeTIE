#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/contrib/contrib.hpp"
#include <iostream>
using namespace cv;
using namespace std;
int main(int argc, char ** argv)
{
    const char* filename = argc >=2 ? argv[1] : "lena.jpg";

    Mat I = imread(filename, CV_LOAD_IMAGE_COLOR);
    if( I.empty())
        return -1;
    vector<Mat> rgb;
    split(I, rgb);
    //normalize(rgb[0], rgb[0], rgb[0].rows*rgb[0].cols, 0, NORM_L1);
    Mat red, green, blue;
    rgb[0].convertTo(blue, CV_64F);
    rgb[1].convertTo(green, CV_64F);
    rgb[2].convertTo(red, CV_64F);
    
    double offset = 10;
    double k = 1000;
    double delta_z = 1;
    
    Mat G = (k/(green + offset)).mul((blue - red)*(1.0/delta_z)); 
    
    //cout << "M = " << endl << " " << G << endl << endl;
    //cout << G.type() << endl;
     
    Mat padded;                            //expand input image to optimal size
    int m = getOptimalDFTSize( G.rows );
    int n = getOptimalDFTSize( G.cols ); // on the border add zero values
    copyMakeBorder(G, padded, 0, m - G.rows, 0, n - G.cols, BORDER_CONSTANT, Scalar::all(0));

    Mat planes[] = {Mat_<float>(padded), Mat::zeros(padded.size(), CV_32F)};
    Mat complexG;
    merge(planes, 2, complexG);         // Add to the expanded another plane with zeros

    dft(complexG, complexG);            // this way the result may fit in the source matrix

    // compute the magnitude and switch to logarithmic scale
    // => log(1 + sqrt(Re(DFT(I))^2 + Im(DFT(I))^2))
    split(complexG, planes);                   // planes[0] = Re(DFT(I), planes[1] = Im(DFT(I))
    magnitude(planes[0], planes[1], planes[0]);// planes[0] = magnitude
    Mat magG = planes[0];

    magG += Scalar::all(1);                    // switch to logarithmic scale
    log(magG, magG);

    // crop the spectrum, if it has an odd number of rows or columns
    magG = magG(Rect(0, 0, magG.cols & -2, magG.rows & -2));

    // rearrange the quadrants of Fourier image  so that the origin is at the image center
    int cx = magG.cols/2;
    int cy = magG.rows/2;

    Mat q0(magG, Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
    Mat q1(magG, Rect(cx, 0, cx, cy));  // Top-Right
    Mat q2(magG, Rect(0, cy, cx, cy));  // Bottom-Left
    Mat q3(magG, Rect(cx, cy, cx, cy)); // Bottom-Right

    Mat tmp;                           // swap quadrants (Top-Left with Bottom-Right)
    q0.copyTo(tmp);
    q3.copyTo(q0);
    tmp.copyTo(q3);

    q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
    q2.copyTo(q1);
    tmp.copyTo(q2);

    normalize(magG, magG, 0, 1, CV_MINMAX); // Transform the matrix with float values into a
                                            // viewable image form (float between values 0 and 1).
    
    //normalize(G, G, 0, 1, CV_MINMAX);
    applyColorMap(G, G, 2);
    //imshow("Blue Image"       , G);    // Show the result
    imshow("spectrum magnitude", magG);
    waitKey();

    return 0;
}
