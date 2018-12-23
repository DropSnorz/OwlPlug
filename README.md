 
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

# Join OwlPlug Beta  :rocket:


**Why i have created OwlPlug ?** 

OwlPlug came from my hate of installing and managing audio plugins. The process is very annoying. I always wanted something simple thats looks like dependency managers or online content store. I've started this just for fun... But because it was so simple to install plugins, i've discovered dozens of cool plugins just by using my own tool. So i realized this is not just a different way to install plugins, it's a real opportunity to share and discover awesome free and open sourced plugins developed by the community. 

I've generated OwlPlug binaries, but I've only tested them in my environment, `Win 10 - Java 9, Java 10`. I'm looking for any kind of feedbacks, even under the same configuration ! And as it's a beta phase, it's highly recommended to **backup your files before using OwlPlug**.

## Installation

**Requirements**: Java 9+ or Java 10+

1. Download binaries [here](http://github.com/dropsnorz/owlplug/releases)
2. Extract archive
3. Run application from `owplug.jar` or `owlplug.exe`


## How to help

[Download and Install](https://github.com/DropSnorz/OwlPlug/releases) OwlPlug, try to configure it, download plugins, etc... Feel free to report any kind of problems, questions, suggestions by opening an issue in this repository. Here is a non exhaustive list of interesting things to report and fix:

* Major issues, bugs, crashes
* Typo errors and improvements. (also in this README)
* Suggestions and things you wanted to see in OwlPlug: new features, UI/UX improvements, docs needed, ...


## How to contribute

I'm maintaining OwlPlug during my free time, so any help will be really appreciated. Check the [Development Setup](#development-setup) section for technical informations.

# Features

## Plugins

OwlPlug is compatible with your installed plugins as they are all in a specific root directory. For example `C:/foo/AudioPlugins`. It means that you can still organize your plugins with a file explorer or with your favorite DAW. 
OwlPlug can discover VST2 and VST3 Plugins.


## Repositories

You can set up repositories to keep your local plugin collection synced with a master source (local network, Google Drive, ...). This is basically a file-pulling system from a remote folder to your local plugin folder. It's really useful when you have to collaborate and share effects or even if you manage different plugin stages (live, studio, and test plugin sets). All repository's files are stored in your plugin directory in a specific folder called `.repositories`. Some plugins protected by DRMs can't be easily included in repositories as they often require extra authorization flow to work. 

Multiple repositories types are supported:
* Local network repositories
* Google drive repositories
* HTTP repositories (Coming soon)


### Local network repositories

Create a local or remote folder and mount it on your filesystem. This folder will act as a remote source that you can pull into your local plugin directory.


### Google drive repositories

Create a folder in your Google Drive space. Add a new Google Drive repository in OwlPlug using the folder fileId. You can retrieve fileId from the folder URL. You can use any Google drive folders as long as your account have authorization to read it. OwlPlug will pull the entire folder content in your local plugin directory.


## Stores 

Owlplug is shipped with a plugin store to automate plugin downloads and installations. A Store is a collection of available plugins you can install locally. Plugins delivered by OwlPlug Central are included by default. You can configure OwlPlug to use any compatible third party store in *Store* Tab > *Stores* > *Add a new store source...*.


### OwlPlug central

OwlPlug central is the default Store source. It's a secure and trusted source to retrieve plugins. OwlPlug Central is a quick proof of concept for the Store feature integration in OwlPlug.

* OwlPlug central: `http://owlplug.dropsnorz.com/store `

### Third party

Third party stores are maintained by creators, developers and distributors. For security reasons, you should only explore stores from trusted sources only.

* Krakli plugins (By [Shane Dunne](http://getdunne.net/wiki/doku.php)): `http://getdunne.net/GyL/owl.php`
* GyL Synths (By [Shane Dunne](http://getdunne.net/wiki/doku.php)): `http://getdunne.net/Krakli/owl.php`


### Third party (Central mirror)

Third party store adapter maintained by OwlPlug Central but hosted by original creators. (For testing purposes)

* Krakli plugins (By [Shane Dunne](http://getdunne.net/wiki/doku.php)): `http://owlplug.dropsnorz.com/mirrors/krakli`
* GyL Synths (By [Shane Dunne](http://getdunne.net/wiki/doku.php)): `http://owlplug.dropsnorz.com/mirrors/gyl`
* AmVST (By [Angular Momentum](http://www.amvst.com/)): `http://owlplug.dropsnorz.com/mirrors/amvst`

### How to distriute my plugins on OwlPlug ?

* **OwlPlug Central** - Your plugin will be hosted and distributed by OwlPlug Central. Send me a mail or create an issue in the [OwlPlug central github repo](http://github.com/dropsnorz/owlplug-central-static)
* **Setup your store** - If you provide a complete set of plugins, you can create a store endpoint following the [OwlPlug Store Specification](https://github.com/droppsnorz/owlplug). Then, provide the endpoint url to your user.

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

As JavaFx as been decoupled from JDK 11, a compatible version is maitained on `java-11` branch.


# Roadmap

- February 2019 - Plugin Discovery Update
- Spring 2019 - Better UI / UX
- Last half of 2019 - Auto-updates, URI Schemes, OwlPlug central improvements


# Possible incoming major features

- Plugins Organization (Move plugins between folders)
- Better plugin discovery
- HTTP Repositories
- Plugins update from store
- Plugins backup
