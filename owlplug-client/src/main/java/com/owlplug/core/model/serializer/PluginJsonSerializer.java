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

package com.owlplug.core.model.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.owlplug.core.model.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginJsonSerializer {

  private static final Logger log = LoggerFactory.getLogger(PluginJsonSerializer.class);

  public String serialize(Iterable<Plugin> plugins) {

    ObjectMapper objectMapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    try {
      return objectMapper.writeValueAsString(plugins);
    } catch (JsonProcessingException e) {
      log.error("Error while creating json object",e);
      return null;
    }

  }
}
