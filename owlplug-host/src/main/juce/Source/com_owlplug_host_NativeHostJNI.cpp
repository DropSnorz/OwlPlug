/*
  ==============================================================================

    com_owlplug_host_NativeHostJNI.cpp
    Created: 16 Mar 2019 12:08:36am
    Author:  Arthur

  ==============================================================================
*/
#include <iostream>
#include "../JuceLibraryCode/JuceHeader.h"
#include "com_owlplug_host_NativeHostJNI.h"

/**
* Returns a NativePlugin jobject for a given PluginDescription
*
*/
jobject buildJNativePluginInstance(JNIEnv* env, PluginDescription* pluginDescription) {

	jstring name = env->NewStringUTF(pluginDescription->name.getCharPointer());
	jint uid = pluginDescription->uid;
	jstring version = env->NewStringUTF(pluginDescription->version.getCharPointer());
	jstring manufacturer = env->NewStringUTF(pluginDescription->manufacturerName.getCharPointer());

	// Create the object of the class UserData
	jclass nativePluginClass = env->FindClass("com/owlplug/host/NativePlugin");
	jobject nativePlugin = env->AllocObject(nativePluginClass);

	// Get the UserData fields to be set
	jfieldID nameField = env->GetFieldID(nativePluginClass, "name", "Ljava/lang/String;");
	jfieldID uidField = env->GetFieldID(nativePluginClass, "uid", "I");
	jfieldID versionField = env->GetFieldID(nativePluginClass, "version", "Ljava/lang/String;");
	jfieldID manufacturerNameField = env->GetFieldID(nativePluginClass, "manufacturerName", "Ljava/lang/String;");

	env->SetObjectField(nativePlugin, nameField, name);
	env->SetIntField(nativePlugin, uidField, uid);
	env->SetObjectField(nativePlugin, versionField, version);
	env->SetObjectField(nativePlugin, manufacturerNameField, manufacturer);

	return nativePlugin;
}


JNIEXPORT jobject JNICALL Java_com_owlplug_host_NativeHostJNI_loadPlugin
  (JNIEnv* env, jobject thisObject, jstring pluginPath) {

	// Used by Console / Library app to take care of Juce components lifecycle
	// MessageManager is automatically released at the end of function scope
	ScopedJuceInitialiser_GUI initGui;

	const char* pathCharPointer = env->GetStringUTFChars(pluginPath, NULL);

	AudioPluginFormatManager pluginFormatManager;
	pluginFormatManager.addDefaultFormats();
	KnownPluginList plugList;
	OwnedArray<juce::PluginDescription> pluginDescriptions;

	for (int i = 0; i < pluginFormatManager.getNumFormats(); ++i) {
		plugList.scanAndAddFile(pathCharPointer, false, pluginDescriptions,
			*pluginFormatManager.getFormat(i));
	}

	if (pluginDescriptions.size() == 0) {
		return NULL;
	}

	jobject nativePlugin = buildJNativePluginInstance(env, pluginDescriptions[0]);

	return nativePlugin;
}
