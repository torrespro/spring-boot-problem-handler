package com.ksoot.problem.spring.advice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksoot.problem.spring.advice.security.SecurityAdviceTraits;
import com.ksoot.problem.spring.advice.webflux.SpringWebfluxProblemResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProblemServerAccessDeniedHandler implements ServerAccessDeniedHandler {

  private final SecurityAdviceTraits<ServerWebExchange, Mono<ResponseEntity<ProblemDetail>>> advice;

  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> handle(final ServerWebExchange exchange, final AccessDeniedException exception) {
    return this.advice.handleAccessDeniedException(exception, exchange).flatMap(
        entity -> SpringWebfluxProblemResponseUtils.writeResponse(entity, exchange, this.objectMapper));
  }
}
