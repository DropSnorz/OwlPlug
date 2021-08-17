# OwlPlug Store Endpoint Specification
**Version 1.2.0**

## Introduction

An OwlPlug Store is a single remote resource providing a list of available Products. 

## Specification

### Version

The Owlplug Store Specification is versioned using [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) semver.

The `major`.`minor` portion of the semver (for example `3.0`) designate the Store Specification feature set. Typically, *`.patch`* versions address errors in this document, not the feature set. Tooling which supports Store Specification 3.0 should be compatible with all 3.0.* versions. The patch version should not be considered by tooling, making no distinction between `3.0.0` and `3.0.1` for example.

Subsequent minor version releases of the Specification should not interfere with tooling developed to a lower minor version and same major version. Thus a hypothetical `3.1.0` specification should be usable with tooling designed for `3.0.0`.

## Format

A Store endpoint serve a JSON document. All field names in the specification are **case-sensitive**.
The root object is a [Store](#Store) object.

### Example

```json
{
  "name": "My Custom store",
  "url": "https://example.com",
  "schemaVersion": "1.0.0",
  "products": [
    {
      "name": "Wobbleizer",
      "slug": "wobbleizer",
      "creator": "Dropsnorz",
      "license": "mit",
      "screenshotUrl": "https://owlplug.dropsnorz.com/products/com.dropsnorz.wobbleizer/wobbleizer.png",
      "description": "A frequency filter with LFO modulation",
      "pageUrl": "https://github.com/dropsnorz/wobbleizer",
      "type": "effect",
      "stage": "beta",
      "tags": ["Filter", "LFO"],
      "bundles" : [
      	{"name": "Win - Osx x64", "targets": ["win64", "osx"], "format":"vst", "downloadUrl":"https://owlplug.dropsnorz.com/products/com.dropsnorz.wobbleizer/wobbleizer.zip", "fileSize":5900000},
        {"name": "Win32", "targets": ["win32"], "format":"vst", "downloadUrl":"https://owlplug.dropsnorz.com/products/com.dropsnorz.wobbleizer/wobbleizer.zip", "fileSize":5900000}
      ]
    },
    {
      "name": "Foo Plugin",
      "creator": "Foo name",
      "license": "mit",
      "screenshotUrl": "https://via.placeholder.com/350x150.png",
      "description": "An awesome foo plugin....",
      "pageUrl": "http://example.com",
      "type": "instrument",
      "stage": "release",
      "tags": ["Synth", "Analog"],
      "bundles" : [
        {"name": "Full Release", "targets": ["win32", "win64", "osx"], "format": "vst3", "downloadUrl":"https://owlplug.dropsnorz.com/products/com.dropsnorz.wobbleizer/wobbleizer.zip", "fileSize":5900000}
      ]
    }
  ]
}
```

## Schema

### Store

Document root object. Provides general information about your store and carries a list of products.

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the store. 255 characters max.
url | `string` | **REQUIRED**. An url to access your store or developer information. This is not necessarily the endpoint URL. 255 characters max.
schemaVersion | `string` | **REQUIRED**. This string must be the [semantic version number](https://semver.org/spec/v2.0.0.html) of the [OwlPlug Store Specification](#versions) that the document uses. 255 characters max.
version | `string` | *Deprecated*
products | `array` | **REQUIRED**. An array of [Product](#Product). Can be empty.

### Product

A Product is basically an abstract concept for a Plugin. OwlPlug only supports Steinberg VST plugins. 

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the product/plugin. 255 characters max.
slug | `string` | **REQUIRED**. Unique lower case and hyphen-separated identifier. 255 characters max and must match `^[a-z0-9]+(?:[a-z0-9]+)*$`. Examples: `wobbleizer`, `my-plugin`, `an-other-slug`.  
creator | `string` | **REQUIRED**. Product creator / manufacturer name. 255 characters max.
license | `string` | Distribution license. Free field but keep it short. Use the [Github keywords](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/creating-a-repository-on-github/licensing-a-repository) like `gpl-3.0`, `mit`, `...` as most as possible for consistency. 255 character max.
type | `string` | **REQUIRED**. Must be `instrument` for VSTi or `effect` for VST/VSTfx or `unknown`. 255 characters max.
stage | `string` | Can be `beta` for an early release, `demo` for a limited release or `release` for stable build. 255 characters max.
pageUrl | `string` | **REQUIRED**. The plugin page url. 255 characters max.
donateUrl | `string` | Creator support/donation URL. 255 characters max.
screenshotUrl | `string` | **REQUIRED**. Plugin screenshot url. Must be a png file. 255 characters max.
description | `string` | **REQUIRED**. A short plugin description. 1000 characters max.
version | `string`| Plugin version. Version will be applied to all bundles if not overloaded.
technicalUid | `string` | Plugin unique id. Uid will be applied to all bundles if not overloaded.
tags | `array` | An array of [Tag](#Tag). Can be empty.
bundles | `array` | **REQUIRED**. An array of [Bundle](#Bundle). Can be empty.

### Bundle

A bundle is a package containing a plugin in a specific `format` (vst or vst3) for one or multiple `targets` environments (Windows, OSX...).
For example, a bundle can contains a vst3 release compatible in Win64 and OSX environment. 

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the bundle. 255 characters max.
targets | `array` | **REQUIRED**. Array of plugin [Target](#Target). Supported environements by the bundle.
format | `string` | **REQUIRED**. Plugin format. Must be `vst`, `vst3`, `au`, `various` or `unknown`.
version | `string`| Bundle/Plugin version. Overload parent *store.product.version* property.
technicalUid | `string` | Bundle/Plugin unique id. Overload parent *store.product.technicalUid* property.
downloadUrl | `string` | **REQUIRED**. Bundle download url. 255 characters max. Check [Bundle file structure](#Bundle_file_structure)
fileSize | `long` | Size of bundle file in bytes.


### Tag

A Tag is just a simple string describing main features of a Plugin. 255 characters max are allowed for each tag. You can specify any keywords that describes the plugin. OwlPlug will suggest the following list of tags:
```
Ambient,Amp,Analog,Bass,Brass,Compressor,Delay,Distortion,Drum,Equalizer,Filter,Flanger,Gate,Guitar,LFO,Limiter,Maximizer,Monophonic,Orchestral,Organ,Panner,Phaser,Piano,Reverb,Synth,Tremolo,Tube,Vintage
```

### Target

A Target is just a simple string describing main target environments of a plugin. You must use following identifiers:
* Windows 32 bit: `win32`
* Windows 64 bit: `win64`
* OSX: `osx`
* Linux: `linux`

For example, in case of a win 64 bit release, you should specify `win64`. If a bundle contains both x64 and x86 windows distributions, you can have an array of target like this `["win64", "win32"]`. This is **not** a compatibility flag so if you are distributing Win 32 release only, you should **not** specify `win64`.

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

## Json schema Draft-07
```json
{
  "definitions": {},
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "http://example.com/root.json",
  "type": "object",
  "title": "Store Object",
  "required": [
    "name",
    "url",
    "schemaVersion",
    "products"
  ],
  "properties": {
    "name": {
      "$id": "#/properties/name",
      "type": "string",
      "title": "Store Name",
      "default": "",
      "examples": [
        "My Custom store"
      ],
      "pattern": "^(.*)$"
    },
    "url": {
      "$id": "#/properties/url",
      "type": "string",
      "title": "Store Url",
      "default": "",
      "examples": [
        "https://example.com"
      ],
      "pattern": "^(.*)$"
    },
    "schemaVersion": {
      "$id": "#/properties/version",
      "type": "string",
      "title": "Store Version",
      "default": "",
      "examples": [
        "1.0.0"
      ],
      "pattern": "^(.*)$"
    },
    "version": {
      "$id": "#/properties/version",
      "type": "string",
      "title": "Store Version",
      "default": "",
      "examples": [
        "1.0.0"
      ],
      "pattern": "^(.*)$"
    },
    "products": {
      "$id": "#/properties/products",
      "type": "array",
      "title": "Store Products",
      "items": {
        "$id": "#/properties/products/items",
        "type": "object",
        "title": "Product Item",
        "required": [
          "name",
          "creator",
          "screenshotUrl",
          "description",
          "pageUrl",
          "type",
          "bundles"
        ],
        "properties": {
          "name": {
            "$id": "#/properties/products/items/properties/name",
            "type": "string",
            "title": "Product Name",
            "default": "",
            "examples": [
              "Wobbleizer"
            ],
            "pattern": "^(.*)$"
          },
          "slug": {
            "$id": "#/properties/products/items/properties/slug",
            "type": "string",
            "title": "Product Slug",
            "default": "",
            "examples": [
              "wobbleizer", "my-plugin", "an-other-slug"
            ],
            "pattern": "^[a-z0-9]+(?:[a-z0-9]+)*$"
          },
          "creator": {
            "$id": "#/properties/products/items/properties/creator",
            "type": "string",
            "title": "Product Creator",
            "default": "",
            "examples": [
              "Dropsnorz"
            ],
            "pattern": "^(.*)$"
          },
          "license": {
            "$id": "#/properties/products/items/properties/license",
            "type": "string",
            "title": "Product License",
            "default": "",
            "examples": [
              "mit"
            ],
            "pattern": "^(.*)$"
          },
          "screenshotUrl": {
            "$id": "#/properties/products/items/properties/screenshotUrl",
            "type": "string",
            "title": "Product Screenshot Url",
            "default": "",
            "examples": [
              "https://via.placeholder.com/350x150.png"
            ],
            "pattern": "^(.*)$"
          },
          "description": {
            "$id": "#/properties/products/items/properties/description",
            "type": "string",
            "title": "Product Description",
            "default": "",
            "examples": [
              "A frequency filter with LFO modulation"
            ],
            "pattern": "^(\n|.)*$"
          },
	  "version": {
            "$id": "#/properties/products/items/properties/version",
            "type": "string",
            "title": "Product version",
            "default": "",
            "examples": [
              "1.0.0"
            ],
            "pattern": "^(.*)$"
          },
	  "technicalUid": {
            "$id": "#/properties/products/items/properties/technicalUid",
            "type": "string",
            "title": "Product unique Id",
            "default": "",
            "examples": [
              "1237986614"
            ],
            "pattern": "^(.*)$"
          },
          "pageUrl": {
            "$id": "#/properties/products/items/properties/pageUrl",
            "type": "string",
            "title": "Product Page Url",
            "default": "",
            "examples": [
              "https://example.com"
            ],
            "pattern": "^(.*)$"
          },
	  "donateUrl": {
            "$id": "#/properties/products/items/properties/donateUrl",
            "type": "string",
            "title": "Creator donate Url",
            "default": "",
            "examples": [
              "https://example.com"
            ],
            "pattern": "^(.*)$"
          },
          "type": {
            "$id": "#/properties/products/items/properties/type",
            "type": "string",
            "title": "Product Type",
            "default": "",
            "examples": [
              "effect"
            ],
            "pattern": "^(.*)$"
          },
          "stage": {
            "$id": "#/properties/products/items/properties/stage",
            "type": "string",
            "title": "Product Stage",
            "default": "",
            "examples": [
              "release"
            ],
            "pattern": "^(.*)$"
          },
          "tags": {
            "$id": "#/properties/products/items/properties/tags",
            "type": "array",
            "title": "Product Tags",
            "items": {
              "$id": "#/properties/products/items/properties/tags/items",
              "type": "string",
              "title": "Tag Item",
              "default": "",
              "examples": [
                "Filter",
                "LFO"
              ],
              "pattern": "^(.*)$"
            }
          },
          "bundles": {
            "$id": "#/properties/products/items/properties/bundles",
            "type": "array",
            "title": "Product Bundles",
            "items": {
              "$id": "#/properties/products/items/properties/bundles/items",
              "type": "object",
              "title": "Bundle Item",
              "required": [
                "name",
                "targets",
                "format",
                "downloadUrl"
              ],
              "properties": {
                "name": {
                  "$id": "#/properties/products/items/properties/bundles/items/properties/name",
                  "type": "string",
                  "title": "Bundle Name",
                  "default": "",
                  "examples": [
                    "Win x64"
                  ],
                  "pattern": "^(.*)$"
                },
                "targets": {
                  "$id": "#/properties/products/items/properties/bundles/items/properties/targets",
                  "type": "array",
                  "title": "Bundle Targets",
                  "items": {
                    "$id": "#/properties/products/items/properties/bundles/items/properties/targets/items",
                    "type": "string",
                    "title": "Target Item",
                    "default": "",
                    "examples": [
                      "win64",
                      "osx"
                    ],
                    "pattern": "^(.*)$"
                  }
                },
                "format": {
                  "$id": "#/properties/products/items/properties/bundles/items/properties/format",
                  "type": "string",
                  "title": "Bundle format",
                  "default": "",
                  "examples": [
                    "vst"
                  ],
                  "pattern": "^(.*)$"
                },
	       "version": {
	         "$id": "#/properties/products/items/properties/version",
	          "type": "string",
	          "title": "Product version",
                  "default": "",
                  "examples": [
	            "1.0.0"
	          ],
	          "pattern": "^(.*)$"
	        },
                "technicalUid": {
                  "$id": "#/properties/products/items/properties/technicalUid",
                   "type": "string",
                   "title": "Product unique Id",
                   "default": "",
                   "examples": [
                     "1237986614"
                   ],
                  "pattern": "^(.*)$"
                },
                "downloadUrl": {
                  "$id": "#/properties/products/items/properties/bundles/items/properties/downloadUrl",
                  "type": "string",
                  "title": "Bundle Download Url",
                  "default": "",
                  "examples": [
                    "https://example.com/plugin.zip"
                  ],
                  "pattern": "^(.*)$"
                },
                "fileSize": {
                  "$id": "#/properties/products/items/properties/bundles/items/properties/fileSize",
                  "type": "integer",
                  "title": "Bundle FileSize",
                  "default": 0,
                  "examples": [
                    5900000
                  ]
                }
              }
            }
          }
        }
      }
    }
  }
}

```
