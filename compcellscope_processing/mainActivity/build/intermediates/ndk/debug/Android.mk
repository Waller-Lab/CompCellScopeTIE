LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := nativeProcessing
LOCAL_SRC_FILES := \
	/Users/nsadras/Development/compcellscope_processing/mainActivity/src/main/jni/Android.mk \
	/Users/nsadras/Development/compcellscope_processing/mainActivity/src/main/jni/Application.mk \
	/Users/nsadras/Development/compcellscope_processing/mainActivity/src/main/jni/native.cpp \

LOCAL_C_INCLUDES += /Users/nsadras/Development/compcellscope_processing/mainActivity/src/main/jni
LOCAL_C_INCLUDES += /Users/nsadras/Development/compcellscope_processing/mainActivity/src/debug/jni

include $(BUILD_SHARED_LIBRARY)
