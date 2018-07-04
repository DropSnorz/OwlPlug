# OwlPlug


## Overview

OwlPlug is a small audio plugin manager. It provides a complete view of your plugin setup and simple tools to manage them.


**Owlplug is currently in alpha stage. It's higly recommended to backup your files before using it.**

## Plugins

OwlPlug is compatible with your plugin setup as they are all in a specific root directory. For example `C:/AudioPlugins`. It means that you can still organize your plugins with a file explorer.
OwlPlug can discover VST2 and VST3 Plugins.


## Repositories

You can setup repositories to keep your local plugin collection synced with a master source (Local network, google drive, ...). This is basically a file-pulling system from a remote folder to your local plugin folder. It's really usefull when you have to collaborate and share effects or manage different plugin stages (live, studio, and test plugin sets). Some plugins protected by DRMs can't be easily included in repositories as it requires extra authorization flow to work. 

Multiple repositories types are supported:
* Local network repositories
* Google drive repositoies
* HTTP repositories (Coming soon)


### Local network repositories

Create a local or remote folder by mounting it on your current filesystem. This folder will act as a remote source that you can pull into your local plugin folder.


### Google drive repositories

Create a folder in your Google Drive space. Create a new Google Drive repository in OwlPlug using the fileId of the folder. OwlPlug will pull the folder content in your local plugin folder.


## Stores 

In the next releases, OwlPlug will be shipped with a plugin store to automate plugin downloads and installations.

