/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.plugin.tasks.discovery.fileformats;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.owlplug.plugin.model.Plugin;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class OsxPlistFile {
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final File plist;

  public OsxPlistFile(File plist) {
    super();
    this.plist = plist;
  }


  public void bindProperties(Plugin plugin) {
    try {
      // Ensure plist class constructor input is not null
      if (plist == null) {
        log.error("Plist file/input is null, cannot bind properties.");
        return;
      }

      NSObject rootObject = PropertyListParser.parse(plist);

      // Ensure parsed result is a NSDictionary
      // Some execution shown that it could be null
      if (!(rootObject instanceof NSDictionary)) {
        log.error("Plist root is not a NSDictionary, actual type: {}",
                rootObject == null ? "null" : rootObject.getClass().getName());
        return;
      }

      NSDictionary rootDict = (NSDictionary) rootObject;

      NSObject nsBundleId = rootDict.objectForKey("CFBundleIdentifier");
      NSObject nsBundleVersion = rootDict.objectForKey("CFBundleShortVersionString");

      if (nsBundleVersion == null) {
        nsBundleVersion = rootDict.objectForKey("CFBundleVersion");
      }
      if (nsBundleId != null) {
        plugin.setBundleId(nsBundleId.toString());
      }
      if (nsBundleVersion != null) {
        plugin.setVersion(nsBundleVersion.toString());
      }

    } catch (IOException | PropertyListFormatException | ParseException
             | ParserConfigurationException | SAXException e) {
      log.error("Error while binding plugin properties from Plist file", e);
    }
  }
}
