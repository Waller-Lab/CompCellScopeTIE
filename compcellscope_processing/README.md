Compcellscope Processing
========================
Android application for processing image acquired using CellScope


Code Structure
--------------

All the java code is found in `mainActivity/src/main/java/com/choochootrain/refocusing/`.  It is divided into various modules.  Activities are found in `activity`, image processing code is found in `tasks/`, and the data structures use to pass information from activities to background processes is contained in `datasets/`.

The image processing tasks inherit from `AsyncTask`.  These tasks are executed by MainActivity, which passes the information from the UI (filepaths, algorithm parameters) to the tasks using the Dataset class.

The native C++ code and NDK makefiles can be found in `mainActivity/src/main/jni`.  All the native functions are currently defined in `native.cpp`.


NDK
---

Getting the NDK to work in Android Studio requires, as of the time this code was written, requires defining tasks in the gradle build files (`mainActivity/build.gradle`).  Native code can be compiled either automatically be Android Studio (I have yet to get this working successfully), or by using the command line `ndk-build` tool. Simply run `ndk-build` from `mainActivity/source/main/`, and the .cpp files will be compiled into .so (shared object) files.  These .so files can then be imported just like any other library, and the native functions will be available for use.
