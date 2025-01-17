package com.ksoot.problem.core;

import com.ksoot.problem.spring.config.ProblemBeanRegistry;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ksoot.problem.core.ProblemConstant.CAUSE_KEY;
import static com.ksoot.problem.core.ProblemConstant.CODE_KEY;
import static com.ksoot.problem.core.ProblemConstant.METHOD_KEY;
import static com.ksoot.problem.core.ProblemConstant.TIMESTAMP_KEY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public interface ErrorResponseBuilder<T, R> {

  ContentNegotiationStrategy DEFAULT_CONTENT_NEGOTIATION_STRATEGY =
      new FallbackContentNegotiationStrategy(new HeaderContentNegotiationStrategy());

  static Optional<MediaType> getProblemMediaType(final List<MediaType> mediaTypes) {
    for (final MediaType mediaType : mediaTypes) {
      if (mediaType.includes(APPLICATION_JSON) || mediaType.includes(MediaTypes.PROBLEM)) {
        return Optional.of(MediaTypes.PROBLEM);
      } else if (mediaType.includes(MediaTypes.X_PROBLEM)) {
        return Optional.of(MediaTypes.X_PROBLEM);
      }
    }

    return Optional.empty();
  }

  R buildResponse(final Throwable throwable, final T request, final HttpStatus status,
                  final HttpHeaders headers, final Problem problem);

  default ProblemDetail createProblemDetail(final T request, final HttpStatus status, final Problem problem) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, problem.getDetail());
    problemDetail.setTitle(problem.getTitle());
    problemDetail.setInstance(requestUri(request));
    if (StringUtils.isNotBlank(ProblemBeanRegistry.problemProperties().getTypeUrl())) {
      URI type = URI.create(ProblemBeanRegistry.problemProperties().getTypeUrl() + "#" + problem.getCode());
      problemDetail.setType(type);
    }
    problemDetail.setProperty(METHOD_KEY, requestMethod(request));
    problemDetail.setProperty(TIMESTAMP_KEY, OffsetDateTime.now());
    problemDetail.setProperty(CODE_KEY, problem.getCode());

    if (MapUtils.isNotEmpty(problem.getParameters())) {
      problem.getParameters().forEach((k, v) -> problemDetail.setProperty(k, v));
    }
    if (Objects.nonNull(problem.getCause())) {
      problemDetail.setProperty(CAUSE_KEY, problem.getCause());
    }
    return problemDetail;
  }

  default URI requestUri(final T request) {
    throw new UnsupportedOperationException("Method need to be implemented in implementation class");
  }

  default HttpMethod requestMethod(final T request) {
    throw new UnsupportedOperationException("Method need to be implemented in implementation class");
  }
}
