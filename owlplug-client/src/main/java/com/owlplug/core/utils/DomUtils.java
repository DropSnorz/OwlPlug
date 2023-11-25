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

package com.owlplug.core.utils;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomUtils {

  public static NodeList getDirectDescendantElementsByTagName(Element element, String name) {

    SimpleNodeList nodeList = new SimpleNodeList();
    for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child instanceof Element && name.equals(child.getNodeName())) {
        nodeList.add(child);
      }
    }
    return nodeList;
  }

  public static class SimpleNodeList implements NodeList {

    private ArrayList<Node> list = new ArrayList<>();

    @Override
    public Node item(int index) {
      return list.get(index);
    }

    @Override
    public int getLength() {
      return list.size();
    }

    protected void add(Node node) {
      list.add(node);
    }
  }
}
