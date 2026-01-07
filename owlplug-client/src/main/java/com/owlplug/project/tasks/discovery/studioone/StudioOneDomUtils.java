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

package com.owlplug.project.tasks.discovery.studioone;

import com.owlplug.plugin.model.PluginFormat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StudioOneDomUtils {

  /**
   * Finds a child element with a specific x:id attribute value.
   * Note: x:id is a literal attribute name (colon is part of the name, not a namespace).
   *
   * @param parent the parent element to search within
   * @param xidValue the value of the x:id attribute to find
   * @return the element with matching x:id, or null if not found
   */
  public static Element findElementByXId(Element parent, String xidValue) {
    return findAttributeByXId(parent, xidValue);
  }

  /**
   * Finds deviceData and ghostData elements within a parent element.
   *
   * @param element the parent element to search within
   * @return a record containing deviceData and ghostData elements (either may be null)
   */
  public static DeviceDataAndGhostData findDeviceDataAndGhostData(Element element) {
    return new DeviceDataAndGhostData(
        findAttributeByXId(element, "deviceData"),
        findAttributeByXId(element, "ghostData")
    );
  }

  /**
   * Finds the classInfo element within a ghostData element.
   *
   * @param ghostData the ghostData element to search within
   * @return the classInfo element, or null if not found
   */
  public static Element findClassInfo(Element ghostData) {
    return findAttributeByXId(ghostData, "classInfo");
  }

  /**
   * Private helper method to find an Attributes child element with a specific x:id value.
   *
   * @param parent the parent element to search within
   * @param xidValue the value of the x:id attribute to find
   * @return the element with matching x:id, or null if not found
   */
  private static Element findAttributeByXId(Element parent, String xidValue) {
    if (parent == null || xidValue == null) {
      return null;
    }
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        Element elem = (Element) child;
        if ("Attributes".equals(elem.getTagName()) 
            && xidValue.equals(elem.getAttribute("x:id"))) {
          return elem;
        }
      }
    }
    return null;
  }

  /**
   * Extracts the plugin name from an element, preferring classInfo over deviceData.
   * This avoids instance-specific names (e.g., "Ozone 9 (2)") in favor of the actual plugin name.
   *
   * @param element the element containing plugin information
   * @return the plugin name, or null if not found
   */
  public static String extractPluginName(Element element) {
    DeviceDataAndGhostData data = findDeviceDataAndGhostData(element);
    
    // Try classInfo FIRST (it has the actual plugin name)
    Element classInfo = findClassInfo(data.getGhostData());
    if (classInfo != null) {
      String name = classInfo.getAttribute("name");
      if (name != null && !name.isEmpty()) {
        return name;
      }
    }

    // Fallback to deviceData only if classInfo didn't work
    if (data.getDeviceData() != null) {
      String name = data.getDeviceData().getAttribute("name");
      if (name != null && !name.isEmpty()) {
        return name;
      }
    }

    return null;
  }

  /**
   * Extracts the plugin format from an element for audio effect plugins.
   *
   * @param element the element containing plugin information
   * @return the plugin format, or null if not found or native
   */
  public static PluginFormat extractPluginFormat(Element element) {
    DeviceDataAndGhostData data = findDeviceDataAndGhostData(element);
    Element classInfo = findClassInfo(data.getGhostData());
    
    if (classInfo != null) {
      String subCategory = classInfo.getAttribute("subCategory");
      if (subCategory != null && !subCategory.isEmpty()) {
        if (subCategory.equals("(Native)")) {
          return null;
        }
        if (subCategory.contains("VST2")) {
          return PluginFormat.VST2;
        } else if (subCategory.contains("VST3")) {
          return PluginFormat.VST3;
        }
      }
    }
    return null;
  }

  /**
   * Extracts the plugin format from an element for synth/instrument plugins.
   * This includes an additional check for the AudioSynth category.
   *
   * @param element the element containing plugin information
   * @return the plugin format, or null if not found, native, or not an AudioSynth
   */
  public static PluginFormat extractSynthPluginFormat(Element element) {
    DeviceDataAndGhostData data = findDeviceDataAndGhostData(element);
    Element classInfo = findClassInfo(data.getGhostData());
    
    if (classInfo != null) {
      String subCategory = classInfo.getAttribute("subCategory");
      String category = classInfo.getAttribute("category");

      if (category != null && category.equals("AudioSynth")) {
        if (subCategory != null && !subCategory.isEmpty()) {
          if (subCategory.equals("(Native)")) {
            return null;
          }
          if (subCategory.contains("VST2")) {
            return PluginFormat.VST2;
          } else if (subCategory.contains("VST3")) {
            return PluginFormat.VST3;
          }
        }
      }
    }
    return null;
  }

  /**
   * Record to hold deviceData and ghostData elements.
   */
  public static class DeviceDataAndGhostData {
    private final Element deviceData;
    private final Element ghostData;

    public DeviceDataAndGhostData(Element deviceData, Element ghostData) {
      this.deviceData = deviceData;
      this.ghostData = ghostData;
    }

    public Element getDeviceData() {
      return deviceData;
    }

    public Element getGhostData() {
      return ghostData;
    }
  }

}

