 
<p align="center">
<img src="doc/owlplug-logo.png">
</p>
<p align="center">
<sup>
<b>OwlPlug is a small audio plugin manager. It provides a complete view of your plugin setup and simple tools to manage it.</b>
</sup>
</p>

---

[ ![Codeship Status for DropSnorz/OwlPlug](https://app.codeship.com/projects/29447280-727d-0136-a8a6-3675cf281030/status?branch=master)](https://app.codeship.com/projects/299436)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e6b8ee875daa4f74b5bf1cc8fee6df63)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DropSnorz/OwlPlug&amp;utm_campaign=Badge_Grade_Dashboard)
![Stage Badge](https://img.shields.io/badge/stage-alpha-blue.svg)


# Overview

![owlplug-demo](http://dropsnorz.com/projects/owlplug/owlplug.gif)


# Installation

**Owlplug is currently in alpha stage. It's higly recommended to backup your files before using it.**

For now, no binaries are provided. To run OwlPlug, you should check the [Development Setup](#development-setup) section.


# Features

## Plugins

OwlPlug is compatible with your installed plugins as they are all in a specific root directory. For example `C:/foo/AudioPlugins`. It means that you can still organize your plugins with a file explorer or with your favorite DAW. 
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

Owlplug is shipped with a plugin store to automate plugin downloads and installations. For now, it's **a fully WIP feature**. Plugins delivered by OwlPlug Central are included by default. You can configure OwlPlug to use any compatible third party store.


### OwlPlug central

OwlPlug central is the default Store source. If you wan't to include your plugin in OwlPlug Central, create an issue in the [OwlPlug central repository](http://github.com/dropsnorz/owlplug-central-static). OwlPlug Central is a quick prototype/POC for the Store feature integration in OwlPlug. 

* OwlPlug central: `http://owlplug.dropsnorz.com/store `

### Third party (Central mirror)

Third party store adapter provided by OwlPlug Central. (For testing purposes)

```Empty section```

### Third party

```Empty section```


# Development

## Stack

* Spring boot
* JavaFx & JFoenix
* Hibernate & H2
* Maven


## Development Setup

1. Clone or download project sources
2. Run following commands
```sh
# Install dependecies
mvn clean install spring-boot:repackage
# Run owlplug
mvn spring-boot:run
```
The first command will generate an executable binary `/target/owlplug-*.jar`. In order to use GoogleDrive repositories, you have to fill OAuth2 client id and secret in `credentials.properties`. Check Prerequisites from [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/OAuth2InstalledApp#prerequisites)


# Roadmap

- November 2018 - Open beta & Available binairies
- February 2019 - Store improvements
- Spring 2019 - Plugin discovery update


# Possible incoming major features

- Plugins Organization (Move plugins between folders)
- Better plugin discovery
- HTTP Repositories
- Plugins update from store
- Plugins backup
