# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.0

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list

# Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/Cellar/cmake/3.0.0/bin/cmake

# The command to remove a file.
RM = /usr/local/Cellar/cmake/3.0.0/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/nsadras/Development/cellscope/Desktop

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/nsadras/Development/cellscope/Desktop

# Include any dependencies generated for this target.
include CMakeFiles/ColorTIE.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/ColorTIE.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/ColorTIE.dir/flags.make

CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o: CMakeFiles/ColorTIE.dir/flags.make
CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o: ColorTIE.cpp
	$(CMAKE_COMMAND) -E cmake_progress_report /Users/nsadras/Development/cellscope/Desktop/CMakeFiles $(CMAKE_PROGRESS_1)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building CXX object CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_FLAGS) -o CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o -c /Users/nsadras/Development/cellscope/Desktop/ColorTIE.cpp

CMakeFiles/ColorTIE.dir/ColorTIE.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/ColorTIE.dir/ColorTIE.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -E /Users/nsadras/Development/cellscope/Desktop/ColorTIE.cpp > CMakeFiles/ColorTIE.dir/ColorTIE.cpp.i

CMakeFiles/ColorTIE.dir/ColorTIE.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/ColorTIE.dir/ColorTIE.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -S /Users/nsadras/Development/cellscope/Desktop/ColorTIE.cpp -o CMakeFiles/ColorTIE.dir/ColorTIE.cpp.s

CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.requires:
.PHONY : CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.requires

CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.provides: CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.requires
	$(MAKE) -f CMakeFiles/ColorTIE.dir/build.make CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.provides.build
.PHONY : CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.provides

CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.provides.build: CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o

# Object files for target ColorTIE
ColorTIE_OBJECTS = \
"CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o"

# External object files for target ColorTIE
ColorTIE_EXTERNAL_OBJECTS =

ColorTIE: CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o
ColorTIE: CMakeFiles/ColorTIE.dir/build.make
ColorTIE: /usr/local/lib/libopencv_videostab.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_video.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_ts.a
ColorTIE: /usr/local/lib/libopencv_superres.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_stitching.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_photo.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_ocl.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_objdetect.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_nonfree.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_ml.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_legacy.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_imgproc.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_highgui.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_gpu.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_flann.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_features2d.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_core.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_contrib.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_calib3d.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_nonfree.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_ocl.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_gpu.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_photo.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_objdetect.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_legacy.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_video.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_ml.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_calib3d.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_features2d.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_highgui.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_imgproc.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_flann.2.4.9.dylib
ColorTIE: /usr/local/lib/libopencv_core.2.4.9.dylib
ColorTIE: CMakeFiles/ColorTIE.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --red --bold "Linking CXX executable ColorTIE"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/ColorTIE.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/ColorTIE.dir/build: ColorTIE
.PHONY : CMakeFiles/ColorTIE.dir/build

CMakeFiles/ColorTIE.dir/requires: CMakeFiles/ColorTIE.dir/ColorTIE.cpp.o.requires
.PHONY : CMakeFiles/ColorTIE.dir/requires

CMakeFiles/ColorTIE.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/ColorTIE.dir/cmake_clean.cmake
.PHONY : CMakeFiles/ColorTIE.dir/clean

CMakeFiles/ColorTIE.dir/depend:
	cd /Users/nsadras/Development/cellscope/Desktop && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/nsadras/Development/cellscope/Desktop /Users/nsadras/Development/cellscope/Desktop /Users/nsadras/Development/cellscope/Desktop /Users/nsadras/Development/cellscope/Desktop /Users/nsadras/Development/cellscope/Desktop/CMakeFiles/ColorTIE.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/ColorTIE.dir/depend
