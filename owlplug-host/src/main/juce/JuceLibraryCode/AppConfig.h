/*

    IMPORTANT! This file is auto-generated each time you save your
    project - if you alter its contents, your changes may be overwritten!

    There's a section below where you can add your own custom code safely, and the
    Projucer will preserve the contents of that block, but the best way to change
    any of these definitions is by using the Projucer's project settings.

    Any commented-out settings will assume their default values.

*/

#pragma once

//==============================================================================
// [BEGIN_USER_CODE_SECTION]

// (You can add your own code in this section, and the Projucer will not overwrite it)

// [END_USER_CODE_SECTION]

#define JUCE_PROJUCER_VERSION 0x90002

//==============================================================================
#define JUCE_MODULE_AVAILABLE_juce_audio_basics                   1
#define JUCE_MODULE_AVAILABLE_juce_audio_processors_headless      1
#define JUCE_MODULE_AVAILABLE_juce_core                           1
#define JUCE_MODULE_AVAILABLE_juce_events                         1

#define JUCE_GLOBAL_MODULE_SETTINGS_INCLUDED 1

//==============================================================================
// juce_audio_processors_headless flags:

#ifndef    JUCE_PLUGINHOST_VST
 #define   JUCE_PLUGINHOST_VST 1
#endif

#ifndef    JUCE_PLUGINHOST_VST3
 #define   JUCE_PLUGINHOST_VST3 1
#endif

#ifndef    JUCE_PLUGINHOST_AU
 #define   JUCE_PLUGINHOST_AU 1
#endif

#ifndef    JUCE_PLUGINHOST_LADSPA
 //#define JUCE_PLUGINHOST_LADSPA 0
#endif

#ifndef    JUCE_PLUGINHOST_LV2
 #define   JUCE_PLUGINHOST_LV2 1
#endif

#ifndef    JUCE_PLUGINHOST_ARA
 //#define JUCE_PLUGINHOST_ARA 0
#endif

#ifndef    JUCE_CUSTOM_VST3_SDK
 //#define JUCE_CUSTOM_VST3_SDK 0
#endif

//==============================================================================
// juce_core flags:

#ifndef    JUCE_FORCE_DEBUG
 //#define JUCE_FORCE_DEBUG 0
#endif

#ifndef    JUCE_LOG_ASSERTIONS
 //#define JUCE_LOG_ASSERTIONS 0
#endif

#ifndef    JUCE_CHECK_MEMORY_LEAKS
 //#define JUCE_CHECK_MEMORY_LEAKS 1
#endif

#ifndef    JUCE_DONT_AUTOLINK_TO_WIN32_LIBRARIES
 //#define JUCE_DONT_AUTOLINK_TO_WIN32_LIBRARIES 0
#endif

#ifndef    JUCE_INCLUDE_ZLIB_CODE
 //#define JUCE_INCLUDE_ZLIB_CODE 1
#endif

#ifndef    JUCE_USE_CURL
 #define   JUCE_USE_CURL 0
#endif

#ifndef    JUCE_LOAD_CURL_SYMBOLS_LAZILY
 //#define JUCE_LOAD_CURL_SYMBOLS_LAZILY 0
#endif

#ifndef    JUCE_CATCH_UNHANDLED_EXCEPTIONS
 //#define JUCE_CATCH_UNHANDLED_EXCEPTIONS 0
#endif

#ifndef    JUCE_ALLOW_STATIC_NULL_VARIABLES
 //#define JUCE_ALLOW_STATIC_NULL_VARIABLES 0
#endif

#ifndef    JUCE_STRICT_REFCOUNTEDPOINTER
 #define   JUCE_STRICT_REFCOUNTEDPOINTER 1
#endif

#ifndef    JUCE_ENABLE_ALLOCATION_HOOKS
 //#define JUCE_ENABLE_ALLOCATION_HOOKS 0
#endif

//==============================================================================
// juce_events flags:

#ifndef    JUCE_EXECUTE_APP_SUSPEND_ON_BACKGROUND_TASK
 //#define JUCE_EXECUTE_APP_SUSPEND_ON_BACKGROUND_TASK 0
#endif

//==============================================================================
#ifndef    JUCE_STANDALONE_APPLICATION
 #if defined(JucePlugin_Name) && defined(JucePlugin_Build_Standalone)
  #define  JUCE_STANDALONE_APPLICATION JucePlugin_Build_Standalone
 #else
  #define  JUCE_STANDALONE_APPLICATION 0
 #endif
#endif
