/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.owlplug.core.model.Plugin;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class OsxPlistFile {
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private File plist;

  public OsxPlistFile(File plist) {
    super();
    this.plist = plist;
  }


  public void bindProperties(Plugin plugin){
    
    try {
      NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(plist);
      NSObject nsBundleId = rootDict.objectForKey("CFBundleIdentifier");
      NSObject nsBundleVersion = rootDict.objectForKey("CFBun" + "dleShortVersionString");

      if (nsBundleVersion == null) {
        nsBundleVersion = rootDict.objectForKey("CFBundleVersion");
      }
      if (nsBundleId != null) {
        plugin.setBundleId(nsBundleId.toString());
      }
      if (nsBundleVersion != null) {
        plugin.setVersion(nsBundleVersion.toString());
      }
    } catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
        | SAXException e) {
      log.error("Error while binding plugin  properties from Plist file", e);
    }
  }
}
