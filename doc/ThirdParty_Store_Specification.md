# Store Endpoint specification
**Version 1.0.0**

## Introduction

An OwlPlug Store is a single remote resource providing a list of available products. 

## Specification

### Version

The Owlplug Store Specification is versioned using [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) semver.

The `major`.`minor` portion of the semver (for example `3.0`) designate the OSS feature set. Typically, *`.patch`* versions address errors in this document, not the feature set. Tooling which supports OSS 3.0 should be compatible with all OSS 3.0.* versions. The patch version should not be considered by tooling, making no distinction between `3.0.0` and `3.0.1` for example.

Subsequent minor version releases of the Specification should not interfere with tooling developed to a lower minor version and same major version.  Thus a hypothetical `3.1.0` specification should be usable with tooling designed for `3.0.0`.

## Format

A Store endpoint serve a JSON document. All field names in the specification are **case sensitive**.
The root object is a [Store](#Store) object.

### Example

```json
{}
```

## Schema

### Store

Document root object. Provides general informations about your store and carries a list of products.

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the store. 255 characters max.
url | `string` | **REQUIRED**. An url to access your store or developer informations. This is not necessarily the endpoint URL. 255 characters max.
version | `string` | **REQUIRED**. This string must be the [semantic version number](https://semver.org/spec/v2.0.0.html) of the [OwlPlug StoreSpecification](#versions) that the document uses. 255 characters max.
products | `array` | **REQUIRED**. An array of [Product](#Product). Can be empty.

### Product

A Product is basically an abstract name for a Plugin. OwlPlug only supports Steinberg VST plugins. 

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the product/plugin. 255 characters max.
creator | `string` | **REQUIRED**. Product creator / manufacturer name. 255 characters max.
type | `string` | **REQUIRED**. Must be `instrument` for VSTi or `effect` for VST/VSTfx or `unknown`. 255 characters max.
stage | `string` | Can be `beta` for early releases or `demo` for uncomplete releases. 255 characters max.
pageUrl | `string` | **REQUIRED**. The plugin page url. 255 characters max.
screenshotUrl | `string` | **REQUIRED**. Plugin screenshot url. Must be a png file. 255 characters max.
description | `string` | **REQUIRED**. A short plugin description. 1000 characters max.
tags | `array` | An array of [Tag](#Tag). Can be empty.
bundles | `array` | **REQUIRED**. An array of [Bundle](#Bundle). Can be empty.

### Bundle

A bundle is a package containing a plugin in different `formats` (vst2, vst3...) for one or multiple `targets` environments (Windows, OSX...).
For example, a bundle can contains a plugin in both vst2 and vst3 format and each of them are compatible in Win64 and OSX environement. 

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the bundle. 255 characters max.
targets | `array` | **REQUIRED**. Array of plugin [Target](#Target). Supported environements by the bundle.
formats | `array` | **REQUIRED**. Array of plugin [Format](#Format). Must contains at least one element.
downloadUrl | `string` | **REQUIRED**. Bundle download url. 255 characters max. Check [Bundle file structure](#Bundle file structure)
fileSize | `long` | Size of bundle file in bytes.


### Tag

A Tag is just a simple string describing main features of a Plugin. 255 characters max are allowed for each a tag. You can specify any keywords that describes the plugin. OwlPlug will suggest the following list of tags:
```
Ambient,Amp,Analog,Bass,Brass,Compressor,Delay,Distortion,Drum,Equalizer,Filter,Flanger,Gate,Guitar,LFO,Limiter,Maximizer,Monophonic,Orchestral,Organ,Panner,Phaser,Piano,Reverb,Synth,Tremolo,Tube,Vintage
```

### Target

A Target is just a simple string describing main target environments of a plugin. You must use predefined. 

For example for a win 64 bit release, you should sepcify `win64`. If a bundle contains both x64 and x86 windows distributions, you can have an array of target like this ["win64", "win32"]. This is **not** a compatibility flag so if you are distributing Win32 release, you should **not** specify `win64`.

### Format

A Format is a string identifier that describe plugin formats. Must be `vst2` or `vst3`.

### Bundle file structure

A bundle file must be a zip file. There is no naming convention for the file. A bundle file can be organized in 4 different way:

* Directly
```
plugin.zip/
    ├── plugin.[dll | vst | vst3]
    └── (other files...)
```
* Environment Specific
```
plugin.zip/
    ├── win64
    │    ├── plugin.dll
    │    └── (other required files...)
    └── osx
         ├── plugin.dll
         └── (other required files...)
```
* Directly nested (in a root subfolder)
```
plugin.zip/
    └── plugin
        ├── plugin.[dll | vst | vst3]
        └── (other required files...)
```
* Environment Specific nested (in a root subfolder)
```
plugin.zip/
    └── plugin
        ├── win64
        │    ├── plugin.dll
        │    └── (other required files...)
        └── osx
            ├── plugin.vst
            └── (other required files...)
```
Environment folders names must follow [Target](#Target) convention. Using one of this structure allow OwlPlug to maintain a clean directory tree. If your archive structure don't match, the bundle will be installed as it.
Bundle file must contains plugin file used by the DAW and all required files. It's a common practice to add some docs in bundles subfolders. All `.exe`, `.msi` are not recognized by OwlPlug and cannot be launched for security reasons. 

It's up to you to decide how to organize your plugin distribution. One bundle with different environment targets, or multiple specific bundles. If you are distributing VST 3.6.10+, it's a best practice to distribute only one packaged bundle.

## Migrate from 0.0.1+

* Remove attribute `Store.type`
* Add attribute `Product.type`
* Remove attribute `Product.platformTag` if exists.
* Rename attribute `Product.author` to `Product.creator`
* Rename attribute `Product.iconUrl` to `Product.screenshotUrl`
* Replace attribute Product `Product.downloadUrl` with a [Bundle](#Bundle) array.
If you are providing only one archive, replace `downloadUrl` with a `bundles` attribute which is a list with only one element.
```json
"bundles": [
	{"name":"fooPlugin", "downloadUrl":"http://foo.com/plugin" ...}
]
```

# Annexes

## Json schema
```
Empty
```
