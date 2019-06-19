 
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
![Last Pre release](https://img.shields.io/github/release-date-pre/dropsnorz/owlplug.svg)
![Stage Badge](https://img.shields.io/badge/stage-beta-blue.svg)
[![Discord](https://img.shields.io/badge/chat-on%20discord-%237289DA.svg)](https://discord.gg/nEdHAMB)
[![Patreon](https://img.shields.io/badge/patreon-donate-%23F96854.svg?logo=patreon&logoColor=white&logoWidth=10)](https://www.patreon.com/owlplug)


# Overview

![owlplug-demo](http://dropsnorz.com/projects/owlplug/owlplug.gif)

# Join OwlPlug Beta  :rocket:

**Why i have created OwlPlug ?** 

OwlPlug came from my hate of installing and managing audio plugins. The process is very annoying. I always wanted something simple thats looks like dependency managers or online content store. I've started this just for fun... But because it was so simple to install plugins, i've discovered dozens of cool plugins just by using my own tool. So i realized this is not just a different way to install plugins, it's a real opportunity to share and discover awesome free and open sourced plugins developed by the community. 

I've generated OwlPlug binaries, but I've only tested them in my environment, `Win 10 - Java 9, Java 10`. I'm looking for any kind of feedbacks, even under the same configuration ! And as it's a beta release, it's highly recommended to **backup your files before using OwlPlug**.

**About OwlPlug**
* [OwlPlug Website](https://owlplug.com)
* [Roadmap](https://owlplug.com/roadmap)
* [Documentation](https://github.com/Dropsnorz/OwlPlug/wiki)

## Installation

### Windows

**Requirements**: Windows 7, 8, 10 (64bit)

1. Download binaries [here](http://github.com/dropsnorz/owlplug/releases)
2. Run the MSI Installer.
3. OwlPlug is ready ;)

### Mac

**Requirements**: Java 9+

Things are a bit more complicated for OSX users. I haven't a Mac to generate a packaged app ready to be installed. A compatible Java environement must be set up before directly run the JAR file. Cross platform JAR binary is available [here](http://github.com/dropsnorz/owlplug/releases)


## How to help

[Download and Install](https://github.com/DropSnorz/OwlPlug/releases) OwlPlug, configure it, download plugins, etc... Feel free to report any kind of problems, questions, suggestions by opening an issue in this repository. Here is a non exhaustive list of interesting things to report and fix:

* Major issues, bugs, crashes
* Typo errors and improvements. (also in this README)
* Suggestions and things you wanted to see in OwlPlug: new features, UI/UX improvements, docs needed, ...


## How to contribute

I'm maintaining OwlPlug during my free time, so any help will be really appreciated. Check the [Development Setup](#development-setup) section for technical informations.

# Features

## Plugins

OwlPlug is compatible with your previously installed plugins as they are all in a specific root directory, for example `C:/foo/AudioPlugins`. It means that you can still organize your plugins as you want with a file explorer or with your favorite DAW. 
OwlPlug can discover VST2 and VST3 Plugins. 

## Links

Links allows you to create and manage symlinks accross your filesystem and plugin directories. You can access directories anywhere on your filesystem (Hard drive, USB keys, custom directories...) through your system plugin directory. For example, you can configure a link named *usb-drive* in `C:/VST` to target your usb hard drive `D:/myPlugins`. All plugins in `D:/myPlugins` will be accessible using `C:/VST/usb-drive`. On some Windows version, symlinks creation may require admin privileges.

## Stores 

Owlplug is shipped with a plugin store to automate plugin downloads and installations. A Store is a collection of plugins you can download and install locally. Plugins delivered by OwlPlug Central are available by default. You can configure OwlPlug to use any compatible third party store in *Store* Tab > *Stores* > *Add a new store source...*.


### OwlPlug Central

OwlPlug Central is the default store. It's a secure and trusted source to retrieve plugins. OwlPlug Central is a quick proof of concept for the store feature integration. For now, OwlPlug Central is delivering only free open sourced plugins.

* OwlPlug central: `https://central.owlplug.com/store`

### Third party

Third party stores are maintained by plugin creators, developers or distributors. For security reasons, you should only use stores from trusted sources only.

* Krakli plugins (By [Shane Dunne](http://getdunne.net/wiki/doku.php)): `https://getdunne.net/GyL/owl.php`
* GyL Synths (By [Shane Dunne](http://getdunne.net/wiki/doku.php)): `https://getdunne.net/Krakli/owl.php`


### Third party (Central mirror)

Third party store adapters are maintained by OwlPlug Central but plugins are hosted by original creators. (For testing purposes)

* AmVST (By [Angular Momentum](http://www.amvst.com/)): `https://central.owlplug.com/mirrors/amvst/store`
* [Vst4free](http://vst4free.com): `https://central.owlplug.com/mirrors/vst4free/store`

### How to distriute my plugins on OwlPlug ?

* **OwlPlug Central** - Your plugin will be hosted and distributed by OwlPlug Central. Open an issue or a pull request on the [OwlPlug Central repository](https://github.com/owlplug/central)
* **Setup your store** - If you provide a complete set of plugins, you can create a store endpoint following the [OwlPlug Store Specification](https://github.com/DropSnorz/OwlPlug/blob/master/doc/ThirdParty_Store_Specification.md). Then, provide the endpoint url to your user.

# Development

## Stack

* Spring boot
* JavaFx & JFoenix
* Hibernate & H2
* Maven
* Juce


## Development Setup

1. Clone or download project sources
2. Run following commands
```sh
# Install dependecies
mvn clean install
# Move to owlplug client folder
cd owlplug-client
# (Optional) Create the runnable JAR file
mvn spring-boot:repackage
# Run owlplug
mvn spring-boot:run
```
The first command will generate an executable binary `/target/owlplug-*.jar`. In order to use GoogleDrive repositories, you have to fill OAuth2 client id and secret in `credentials.properties`. Check Prerequisites from [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/OAuth2InstalledApp#prerequisites)

As JavaFx as been decoupled from JDK 11, an (outdated) compatible version is maitained on `java-11` branch.


