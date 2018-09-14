![owlplug](doc/owlplug-logo.png)

<br />

[ ![Codeship Status for DropSnorz/OwlPlug](https://app.codeship.com/projects/29447280-727d-0136-a8a6-3675cf281030/status?branch=master)](https://app.codeship.com/projects/299436)

## Overview

OwlPlug is a small audio plugin manager. It provides a complete view of your plugin setup and simple tools to manage them.

![owlplug-demo](http://dropsnorz.com/projects/owlplug/owlplug.gif)

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

Create a local or remote folder and mount it on your filesystem. This folder will act as a remote source that you can pull into your local plugin directory.


### Google drive repositories

Create a folder in your Google Drive space. Add a new Google Drive repository in OwlPlug using the folder fileId. You can retrieve fileId from the folder URL. You can use any Google drive folders as long as your account have authorization to read it. OwlPlug will pull the entire folder content in your local plugin directory.


## Stores 

Owlplug is shipped with a plugin store to automate plugin downloads and installations.
For now, as it's **a fully WIP feature**, only plugins delivered by OwlPlug central are included. 

In the next realeases, Owlplug may support multiple store sources. Everybody will be able to setup and host a store and let OwlPlug deals with it.


### OwlPlug central

OwlPlug central is the default Store source. If you wan't to include your plugin in OwlPlug Central, create an issue in this repository with a link to download your plugin.
Owlplug Central backend is a quick prototype/POC for the Store feature integration in Owlplug.
- Mirror: Binairies are stored in OwlPlug central server.
- Bridge: You keep plugin binairies on your server by providing only a file access URL to OwlPlug central. I don't like this option for security reasons, but it still usefull for testing purpose.



## Possible next Major features

- Plugins Organization (Move plugins between folders)
- Better plugin discovery
- HTTP Repositories
- Plugins update from store
- Plugins backup
