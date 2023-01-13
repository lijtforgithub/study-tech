package com.ljt.study.dingtalk.service.impl;

import com.ljt.study.dingtalk.properties.DingTalkProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2022-07-22 9:29
 */
@Slf4j
@Getter
public class DingTalkWebClient {

    private final WebClient webClient;

    public DingTalkWebClient(DingTalkProperties properties) {
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> {
                    if (Objects.isNull(properties.getOpen()) || Boolean.TRUE.equals(properties.getOpen())) {
                        return next.exchange(request);
                    }

                    log.info("----------钉钉开关关闭----------");

                    return Mono.just(new ClientResponse() {
                        @Override
                        public HttpStatus statusCode() {
                            return HttpStatus.FORBIDDEN;
                        }

                        @Override
                        public int rawStatusCode() {
                            return HttpStatus.FORBIDDEN.value();
                        }

                        @Override
                        public Headers headers() {
                            return null;
                        }

                        @Override
                        public MultiValueMap<String, ResponseCookie> cookies() {
                            return null;
                        }

                        @Override
                        public ExchangeStrategies strategies() {
                            return null;
                        }

                        @Override
                        public <T> T body(BodyExtractor<T, ? super ClientHttpResponse> extractor) {
                            return null;
                        }

                        @Override
                        public <T> Mono<T> bodyToMono(Class<? extends T> elementClass) {
                            return null;
                        }

                        @Override
                        public <T> Mono<T> bodyToMono(ParameterizedTypeReference<T> elementTypeRef) {
                            return null;
                        }

                        @Override
                        public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
                            return null;
                        }

                        @Override
                        public <T> Flux<T> bodyToFlux(ParameterizedTypeReference<T> elementTypeRef) {
                            return null;
                        }

                        @Override
                        public Mono<Void> releaseBody() {
                            return null;
                        }

                        @Override
                        public <T> Mono<ResponseEntity<T>> toEntity(Class<T> bodyClass) {
                            return null;
                        }

                        @Override
                        public <T> Mono<ResponseEntity<T>> toEntity(ParameterizedTypeReference<T> bodyTypeReference) {
                            return null;
                        }

                        @Override
                        public <T> Mono<ResponseEntity<List<T>>> toEntityList(Class<T> elementClass) {
                            return null;
                        }

                        @Override
                        public <T> Mono<ResponseEntity<List<T>>> toEntityList(ParameterizedTypeReference<T> elementTypeRef) {
                            return null;
                        }

                        @Override
                        public Mono<ResponseEntity<Void>> toBodilessEntity() {
                            return null;
                        }

                        @Override
                        public Mono<WebClientResponseException> createException() {
                            return null;
                        }

                        @Override
                        public String logPrefix() {
                            return null;
                        }
                    });

                }).build();
    }
}
