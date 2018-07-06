# OwlPlug


## Overview

OwlPlug is a small audio plugin manager. It provides a complete view of your plugin setup and simple tools to manage them.

**Owlplug is currently in alpha stage. It's higly recommended to backup your files before using it.**

## Plugins

OwlPlug is compatible with your plugin setup as they are all in a specific root directory. For example `C:/AudioPlugins`. It means that you can still organize your plugins with a file explorer or with your favorite DAW.
OwlPlug can discover VST2 and VST3 Plugins.


## Repositories

You can setup repositories to keep your local plugin collection synced with a master source (Local network, google drive, ...). This is basically a file-pulling system from a remote folder to your local plugin folder. It's really usefull when you have to collaborate and share effects or even if you manage different plugin stages (live, studio, and test plugin sets). All repositories files are stored in your plugin directory in a specific folder called `.repositories`. Some plugins protected by DRMs can't be easily included in repositories as they often require extra authorization flow to work. 

Multiple repositories types are supported:
* Local network repositories
* Google drive repositories
* HTTP repositories (Coming soon)


### Local network repositories

Create a local or remote folder and mount it on your current filesystem. This folder will act as a remote source that you can pull into your local plugin directory.


### Google drive repositories

Create a folder in your Google Drive space. Create a new Google Drive repository in OwlPlug using the fileId of the folder. You can retrieve fileId from your folder URL. You can use every folder in google drive as your account have authorization to read it. OwlPlug will pull the folder content in your local plugin directory.


## Stores 

Owlplug is shipped with a plugin store to automate plugin downloads and installations.
For now, as it's **a fully WIP feature**, only plugins delivered by OwlPlug central are included. 

In the next realeases, owlplug will support multiple store sources. Everybody will be able to setup and host a store and let OwlPlug deals with it.


### OwlPlug central

OwlPlug central is the default Store source. If you wan't to include your plugin in OwlPlug Central, create an issue in this repository and chose between the two plugin delivery mode:
- Mirror: Binairies are stored in OwlPlug central server.
- Bridge: You keep plugin binairies on your server by providing only a file access URL to OwlPlug central.
