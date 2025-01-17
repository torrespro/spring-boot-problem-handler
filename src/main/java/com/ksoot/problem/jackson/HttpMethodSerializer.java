package com.ksoot.problem.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;

import java.io.IOException;

final class HttpMethodSerializer extends JsonSerializer<HttpMethod> {

  @Override
  public void serialize(final HttpMethod method, final JsonGenerator json, final SerializerProvider serializers) throws IOException {
    json.writeString(method.name());
  }
}
