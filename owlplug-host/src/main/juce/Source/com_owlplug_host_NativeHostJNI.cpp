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

	// Converts native types to JVM objects
	jstring name = env->NewStringUTF(pluginDescription->name.getCharPointer());
	jstring descriptiveName = env->NewStringUTF(pluginDescription->descriptiveName.getCharPointer());
	jstring pluginFormatName = env->NewStringUTF(pluginDescription->pluginFormatName.getCharPointer());
	jstring category = env->NewStringUTF(pluginDescription->category.getCharPointer());
	jstring manufacturerName = env->NewStringUTF(pluginDescription->manufacturerName.getCharPointer());
	jstring version = env->NewStringUTF(pluginDescription->version.getCharPointer());
	jstring fileOrIdentifier = env->NewStringUTF(pluginDescription->fileOrIdentifier.getCharPointer());
	jint uid = pluginDescription->uid;
	jboolean isInstrument = pluginDescription->isInstrument;
	jint numInputChannels = pluginDescription->numInputChannels;
	jint numOutputChannels = pluginDescription->numOutputChannels;
	jboolean hasSharedContainer = pluginDescription->hasSharedContainer;


	// Create the object of the class UserData
	jclass nativePluginClass = env->FindClass("com/owlplug/host/NativePlugin");
	jobject nativePlugin = env->AllocObject(nativePluginClass);

	// Get the UserData fields to be set
	jfieldID nameField = env->GetFieldID(nativePluginClass, "name", "Ljava/lang/String;");
	jfieldID descriptiveNameField = env->GetFieldID(nativePluginClass, "descriptiveName", "Ljava/lang/String;");
	jfieldID pluginFormatNameField = env->GetFieldID(nativePluginClass, "pluginFormatName", "Ljava/lang/String;");
	jfieldID manufacturerNameField = env->GetFieldID(nativePluginClass, "manufacturerName", "Ljava/lang/String;");
	jfieldID categoryField = env->GetFieldID(nativePluginClass, "category", "Ljava/lang/String;");
	jfieldID versionField = env->GetFieldID(nativePluginClass, "version", "Ljava/lang/String;");
	jfieldID fileOrIdentifierField = env->GetFieldID(nativePluginClass, "fileOrIdentifier", "Ljava/lang/String;");
	jfieldID uidField = env->GetFieldID(nativePluginClass, "uid", "I");
	jfieldID isInstrumentField = env->GetFieldID(nativePluginClass, "isInstrument", "Z");
	jfieldID numInputChannelsField = env->GetFieldID(nativePluginClass, "numInputChannels", "I");
	jfieldID numOutputChannelsField = env->GetFieldID(nativePluginClass, "numOutputChannels", "I");
	jfieldID hasSharedContainerField = env->GetFieldID(nativePluginClass, "hasSharedContainer", "Z");


	env->SetObjectField(nativePlugin, nameField, name);
	env->SetObjectField(nativePlugin, descriptiveNameField, descriptiveName);
	env->SetObjectField(nativePlugin, pluginFormatNameField, pluginFormatName);
	env->SetObjectField(nativePlugin, manufacturerNameField, manufacturerName);
	env->SetObjectField(nativePlugin, categoryField, category);
	env->SetObjectField(nativePlugin, versionField, version);
	env->SetObjectField(nativePlugin, fileOrIdentifierField, fileOrIdentifier);
	env->SetIntField(nativePlugin, uidField, uid);
	env->SetBooleanField(nativePlugin, isInstrumentField, isInstrument);
	env->SetIntField(nativePlugin, numInputChannelsField, numInputChannels);
	env->SetIntField(nativePlugin, numOutputChannelsField, numOutputChannels);
	env->SetBooleanField(nativePlugin, hasSharedContainerField, hasSharedContainer);


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
