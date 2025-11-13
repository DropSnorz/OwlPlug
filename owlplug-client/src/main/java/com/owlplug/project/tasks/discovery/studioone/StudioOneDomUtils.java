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
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element childElem && "Attributes".equals(childElem.getTagName())) {
        String xid = childElem.getAttribute("x:id");
        if (xidValue.equals(xid)) {
          return childElem;
        }
      }
    }
    return null;
  }

  /**
   * Finds deviceData and ghostData elements within a parent element.
   *
   * @param element the parent element to search within
   * @return a record containing deviceData and ghostData elements (either may be null)
   */
  public static DeviceDataAndGhostData findDeviceDataAndGhostData(Element element) {
    NodeList children = element.getChildNodes();
    Element deviceData = null;
    Element ghostData = null;

    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element childElem && "Attributes".equals(childElem.getTagName())) {
        String xid = childElem.getAttribute("x:id");
        if ("deviceData".equals(xid)) {
          deviceData = childElem;
        } else if ("ghostData".equals(xid)) {
          ghostData = childElem;
        }
      }
    }

    return new DeviceDataAndGhostData(deviceData, ghostData);
  }

  /**
   * Finds the classInfo element within a ghostData element.
   *
   * @param ghostData the ghostData element to search within
   * @return the classInfo element, or null if not found
   */
  public static Element findClassInfo(Element ghostData) {
    if (ghostData == null) {
      return null;
    }

    NodeList ghostChildren = ghostData.getChildNodes();
    for (int i = 0; i < ghostChildren.getLength(); i++) {
      Node ghostChild = ghostChildren.item(i);
      if (ghostChild instanceof Element ghostChildElem && "Attributes".equals(ghostChildElem.getTagName())) {
        String xid = ghostChildElem.getAttribute("x:id");
        if ("classInfo".equals(xid)) {
          return ghostChildElem;
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

