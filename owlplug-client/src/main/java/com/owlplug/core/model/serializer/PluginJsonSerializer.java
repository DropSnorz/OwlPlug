package com.owlplug.core.model.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.owlplug.core.model.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginJsonSerializer {

  private static final Logger log = LoggerFactory.getLogger(PluginJsonSerializer.class);

  public static String serialize(Iterable<Plugin> plugins) {

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
