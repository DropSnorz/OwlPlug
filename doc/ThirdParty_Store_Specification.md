
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

Document root object. Provides general informations about your store and carries a list of product/plugins.

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the store.
url | `string` | **REQUIRED**. An url to access your store or developer informations. This is not necessarily the endpoint URL.
version | `string` | **REQUIRED**. This string must be the [semantic version number](https://semver.org/spec/v2.0.0.html) of the [OwlPlug StoreSpecification](#versions) that the document uses.
plugins | `array` | **REQUIRED**. An array of [Product](#Product). Can be empty.

### Product

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the plugin.
creator | `string` | **REQUIRED**. Plugin creator / manufacturer name.
type | `string` | **REQUIRED**. Must be `instrument` for VSTi or `effect` for VST/VSTfx or `unknown`.
stage | `string` | Can be `beta` for early releases or `demo` for uncomplete releases.
pageUrl | `string` | **REQUIRED**. The plugin page url.
screenshotUrl | `string` | **REQUIRED**. Plugin screenshot url. Must be a png file.
description | `string` | **REQUIRED**. A short plugin description.
tags | `array` | An array of `Tag`. An array of [Tag](#Tag). Can be empty.
bundles | `array` | **REQUIRED**. An array of [Bundle](#Bundle). Can be empty.

### Bundle

Field Name | Type | Description
---|:---:|---
name | `string` | **REQUIRED**. The name of the bundle.
targets | `array` | **REQUIRED**. Supported environements by the bundle.
downloadUrl | `string` | **REQUIRED**. Bundle download url.

### Tag

Field Name | Type | Description
---|:---:|---


## Migrate from 0.0.1+

