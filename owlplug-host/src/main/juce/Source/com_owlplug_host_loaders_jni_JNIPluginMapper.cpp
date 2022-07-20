/* OwlPlug
 * Copyright (C) 2018 Arthur <dropsnorz@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
  ==============================================================================

    com_owlplug_host_loaders_jni_JNIPluginMapper.cpp
    Created: 16 Mar 2019 12:08:36am
    Author:  Arthur

  ==============================================================================
*/
#include <iostream>
#include "../JuceLibraryCode/JuceHeader.h"
#include "com_owlplug_host_loaders_jni_JNIPluginMapper.h"

/**
* Returns a NativePlugin jobject instance from a PluginDescription
*
*/
jobject buildJNativePluginInstance(JNIEnv* env, PluginDescription* pluginDescription) {

	// Convert native types to JVM objects
	jstring name = env->NewStringUTF(pluginDescription->name.getCharPointer());
	jstring descriptiveName = env->NewStringUTF(pluginDescription->descriptiveName.getCharPointer());
	jstring pluginFormatName = env->NewStringUTF(pluginDescription->pluginFormatName.getCharPointer());
	jstring category = env->NewStringUTF(pluginDescription->category.getCharPointer());
	jstring manufacturerName = env->NewStringUTF(pluginDescription->manufacturerName.getCharPointer());
	jstring version = env->NewStringUTF(pluginDescription->version.getCharPointer());
	jstring fileOrIdentifier = env->NewStringUTF(pluginDescription->fileOrIdentifier.getCharPointer());
	jint uid = pluginDescription->uniqueId;
	jboolean isInstrument = pluginDescription->isInstrument;
	jint numInputChannels = pluginDescription->numInputChannels;
	jint numOutputChannels = pluginDescription->numOutputChannels;
	jboolean hasSharedContainer = pluginDescription->hasSharedContainer;


	// Create NativePlugin class instance
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
	
	// Apply values to fields
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


/**
 * JNI mapPlugin implementation.
 * Returns an Array List of com.owlplug.host.NativePlugin instances based on the given path.
 * NativePlugins fields are filled with plugin description and metadata properties
 */
JNIEXPORT jobject JNICALL Java_com_owlplug_host_loaders_jni_JNIPluginMapper_mapPlugin
  (JNIEnv* env, jobject thisObject, jstring pluginPath) {

	// Used by Console / Library app to take care of Juce components lifecycle
	// MessageManager is automatically released at the end of function scope
	ScopedJuceInitialiser_GUI initGui;

	// Retrieve plugin path from JVM env
	const char* pathCharPointer = env->GetStringUTFChars(pluginPath, NULL);

	AudioPluginFormatManager pluginFormatManager;
	pluginFormatManager.addDefaultFormats();
	KnownPluginList plugList;
	
	// Array of plugin description
	OwnedArray<juce::PluginDescription> pluginDescriptions;

	// For each managed format, we try to fill pluginDescriptions array.
	for (int i = 0; i < pluginFormatManager.getNumFormats(); ++i) {
		plugList.scanAndAddFile(pathCharPointer, false, pluginDescriptions,
			*pluginFormatManager.getFormat(i));
	}

	if (pluginDescriptions.size() == 0) {
		return NULL;
	}

    // Create a Java Array List to hold plugin components
    jclass java_util_ArrayList = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));;
    jmethodID java_util_ArrayList_ = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
	jobject pluginList = env->NewObject(java_util_ArrayList, java_util_ArrayList_, pluginDescriptions.size());

    for (int i = 0; i < pluginDescriptions.size(); i++ ) {
        jobject nativePlugin = buildJNativePluginInstance(env, pluginDescriptions[i]);

        jmethodID java_util_ArrayList_add = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");
        env->CallBooleanMethod(pluginList, java_util_ArrayList_add, nativePlugin);

        env->DeleteLocalRef(nativePlugin);
    }

	return pluginList;
}
