package com.svelte.spring.demo.svelte.form;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ActionResultSerializer extends JsonSerializer<ActionResult> {

  @Override
  public void serialize(ActionResult value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    ObjectMapper objectMapper = (ObjectMapper) gen.getCodec();
    gen.writeStartObject();
    gen.writePOJOField("type", value.getType());
    gen.writePOJOField("status", value.getStatus());
    gen.writeFieldName("data");
    if (value.getData() == null || value.getData().isEmpty()) {
      gen.writePOJO("-1");
    } else {
      List<Object> data = new ArrayList<>();
      Map<String, Object> values = new HashMap<>();
      int position = 0;
      for (Entry<String, Object> e : value.getData().entrySet()) {
        values.put(e.getKey(), ++position);
      }
      data.add(values);
      for (Entry<String, Object> e : value.getData().entrySet()) {
        data.add(e.getValue());
      }
      gen.writeString(objectMapper.writeValueAsString(data));
    }
    gen.writeEndObject();
  }
}
