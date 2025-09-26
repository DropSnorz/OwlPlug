 
<p align="center">
<img src="doc/owlplug-logo.png">
</p>
<p align="center">
<sup>
<b>OwlPlug is an audio plugin manager. It provides a complete view of your plugin setup and simple tools to manage it.</b>
</sup>
</p>

---

[![Owlplug Main](https://github.com/DropSnorz/OwlPlug/actions/workflows/main.yml/badge.svg)](https://github.com/DropSnorz/OwlPlug/actions/workflows/main.yml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e6b8ee875daa4f74b5bf1cc8fee6df63)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DropSnorz/OwlPlug&amp;utm_campaign=Badge_Grade_Dashboard)
![Last Pre release](https://img.shields.io/github/release-date/dropsnorz/owlplug.svg)
[![Discord](https://img.shields.io/badge/chat-on%20discord-%237289DA.svg)](https://discord.gg/nEdHAMB)
[![Donate](https://img.shields.io/badge/donate-%E2%99%A5-%23253b80)](https://www.paypal.com/donate?hosted_button_id=7MJGDTQXAPJ22)


# Overview

OwlPlug simplifies the way you organize, discover and install audio plugins. No more tedious file management, scattered folders, or missing dependencies — OwlPlug gives you a centralized experience for VST, AU, and LV2 plugins.

- 🔎 **Manage** — Detect and list your existing audio plugins.
- 🔗 **Organize** — Merge plugins from multiple locations into a single root directory.
- 📂 **Analyze Projects** — Scan DAW projects to identify referenced and missing plugins.
- 🌐 **Discover** — Connect to remote sources to browse and install plugins.

![owlplug-demo](https://dropsnorz.com/projects/owlplug/owlplug.gif)

## About

:earth_africa: [OwlPlug Website](https://owlplug.com) | :pushpin: [Roadmap](https://owlplug.com/roadmap) | :page_facing_up: [Documentation](https://github.com/Dropsnorz/OwlPlug/wiki)

OwlPlug is an open-source plugin manager designed to simplify the installation, discovery, and management of audio plugins.
It was born out of frustration with the tedious process of installing and organizing plugins. Inspired by dependency managers and online content stores, OwlPlug offers a seamless way to manage your audio tools while also helping you discover new plugins from the community.
All feedback, contributions, and suggestions are warmly welcomed.

[![Powered by Open Audio Stack](./owlplug-client/src/main/resources/media/powered-by-open-audio-stack.svg)](https://github.com/open-audio-stack)

# Installation

## Direct Download

### Requirements
- Windows 7 (64-bit) or later
- macOS Ventura or later
- Linux with `.deb` package support

### Installation Steps
1. Download the installer for your platform from the [Releases](http://github.com/dropsnorz/owlplug/releases) page.
   - Windows: `.msi` installer
   - macOS: `.dmg` file
   - Linux: `.deb` package
2. Run the installer.
3. Launch OwlPlug.

⚠️ **macOS Users**: On some versions of macOS, you may see a warning that OwlPlug is damaged. This can be fixed by following the instructions on the [Troubleshooting wiki page](https://github.com/DropSnorz/OwlPlug/wiki/Troubleshooting). This issue occurs because the app is not notarized by Apple.

## Package Managers

### Windows

```sh
winget install --exact --id=OwlPlug.OwlPlug
```

# Features

## Plugins discovery

OwlPlug can discover VST2, VST3, AU and LV2 Plugins. OwlPlug is compatible with all previously installed plugins as long as they are all in a specific root directory, for example `C:/AudioPlugins`. Additional directories can be configured if your plugin setup is fragmented on the filesystem.

After downloading Owlplug, you can still organize (add, move, delete, ...) your plugins with a file explorer or with your favorite DAW without breaking anything.  

## Links management

A Link allows you to create and manage symlinks across your filesystem and plugin directories. With Links, you can access directories anywhere on your filesystem (Hard drive, USB keys, custom directories...) through a single root plugin directory. For example, you can configure a link named *usb-drive* in `C:/AudioPlugins` to target your usb hard drive `D:/myPlugins`. All plugins in `D:/myPlugins` will be accessible using `C:/AudioPlugins/usb-drive`. This feature may be useful for DAW that scans plugins from predefined or limited number directories. On some Windows version, symlinks creation may require admin privileges.

## DAW Projects analysis

OwlPlug can scan DAW projects to extract referenced plugins. Plugins references in project files are compared to plugins installed in configured directories. This way, users can quickly identify missing plugins required to open projects.

The list of compatible DAWs is available in the [documentation](https://github.com/DropSnorz/OwlPlug/wiki/Projects-and-DAW-Support)

## Explore and download Plugins

OwlPlug can be connected to several remote sources to download plugins. A Remote Source (or Registry) is a collection of downloadable plugins that can be installed locally. OwlPlug can be configured to use any compatible third-party source in *Explore* Tab > *Sources* > *Add a new source*.

Here are some recommended compatible sources.

#### OwlPlug Registry ⭐ 

OwlPlug official plugin registry for Free or Open Source plugins
* kind: `registry`
* url: `https://registry.owlplug.com/registry.min.json`

#### Open Audio Stack Registry ⭐ 

Open Audio Stack registry plugins maintained by community.
* kind: `registry`
* url: `https://open-audio-stack.github.io/open-audio-stack-registry` 

**Discover more plugin sources in [this wiki page](https://github.com/DropSnorz/OwlPlug/wiki/Remote-plugin-sources).**

### How to distribute plugins on OwlPlug?

* **(Recommended)** Distribute your plugins from the [open-audio-stack-registry](https://github.com/open-audio-stack/open-audio-stack-registry).
* Distribute your plugins from the legacy [owlplug-registry](https://github.com/OwlPlug/owlplug-registry).
* Setup and host a custom remote source to distribute multiple plugins, following the [open-audio-stack-registry](https://github.com/open-audio-stack/open-audio-stack-registry) or [owlplug-registry](https://github.com/OwlPlug/owlplug-registry) specification.

# How to contribute?

There are several ways to support and get involved in the OwlPlug development.
* [Downloading](https://github.com/DropSnorz/OwlPlug/releases) OwlPlug, using it, and talking about it around you.
* [Reporting issues](https://github.com/DropSnorz/OwlPlug/issues) or feature suggestions. Most of the existing features have been inspired by the community feedback.
* [Contributing](https://github.com/DropSnorz/OwlPlug) to the OwlPlug source code.
* [Submitting or requesting](https://github.com/OwlPlug/owlplug-registry) plugins to be distributed with OwlPlug.
* [Donating](https://www.paypal.com/donate?hosted_button_id=7MJGDTQXAPJ22) to help support development and server costs

# Development

## Stack

* Spring boot
* JavaFx
* Hibernate & H2
* Maven
* JUCE


## Development Setup

1. Clone or download project sources
2. Run following commands
```sh
# Install dependencies
mvn clean install
# Move to owlplug client folder
cd owlplug-client
# (Optional) Create the runnable JAR file in /target/ folder
mvn clean install spring-boot:repackage
# Run owlplug
mvn spring-boot:run
```

# License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FDropSnorz%2FOwlPlug.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FDropSnorz%2FOwlPlug?ref=badge_large)

