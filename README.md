CompCellScope
=============
Android application for image analysis using the Computational Cellscope platform

ColorTIE
--------

The android app for ColorTIE can be found in `CellScopeProto/`.  This is an eclipse project and has no acquisition functionality, it simply performs the TIE poisson solver algorithm

A c++ application that performs the same function (but is much faster) can be found in `Desktop/`

Image Processing
----------------

The rest of the image processing algorithms can be found in `compcellscope_processing`, which is an Android Studio project (it has been migrated from Eclipse).  The functionality of this app includes DPC, Refocusing, and TIE.  It makes use of the android NDK to improve performance.  Further documentation can be found inside this directory.



