package com.medilabo.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Configuration class to route the request to the matching microservices.
 * Automatically injects a basic internal auth if not present.
 */
@Configuration
public class GatewayRoutesConfig {
    private static final Logger log = LoggerFactory.getLogger(GatewayRoutesConfig.class);

    @Value("${gateway.services.patient.url}")
    private String patientServiceUrl;

    @Value("${gateway.services.note.url}")
    private String noteServiceUrl;

    @Value("${gateway.services.assessment.url}")
    private String assessmentServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> diabetesDetectorRoutes() {
        log.debug("Initializing Gateway routes configuration");

        return route("patient-service")
                .route(path("/patients/**"), http())
                .before(addGatewayAuth())
                .before(uri(patientServiceUrl))
                .build()

                .and(route("note-service")
                        .route(path("/notes/**"), http())
                        .before(addGatewayAuth())
                        .before(uri(noteServiceUrl))
                        .build()
                )

                .and(route("assessment-service")
                        .route(path("/assessment/**"), http())
                        .before(addGatewayAuth())
                        .before(uri(assessmentServiceUrl))
                        .build()
                );
    }

    /**
     * Add the Basic internal authentication if no Authorization headers is present.
     * @return the request with the Authorization headers added (if needed).
     */
    private static Function<ServerRequest, ServerRequest> addGatewayAuth() {
        return request -> {
            if (!request.headers().header("Authorization").isEmpty()) {
                log.debug("Authorization header already present for request {}", request.path());
                return request;
            }

            log.debug("Injecting internal Gateway authentication for request {}", request.path());

            String auth = "gateway:gateway-secret";
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            return ServerRequest.from(request)
                    .header("Authorization", "Basic " + encodedAuth)
                    .build();
        };
    }
}