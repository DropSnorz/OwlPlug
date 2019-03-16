/*
  ==============================================================================

    com_owlplug_host_NativeHostJNI.cpp
    Created: 16 Mar 2019 12:08:36am
    Author:  Arthur

  ==============================================================================
*/
#include <iostream>

#include "com_owlplug_host_NativeHostJNI.h"
JNIEXPORT void JNICALL Java_com_owlplug_host_NativeHostJNI_sayHello
  (JNIEnv* env, jobject thisObject) {
    std::cout << "Hello from C++ !!" << std::endl;
}