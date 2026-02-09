package com.medilabo.gateway.config;

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

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> diabetesDetectorRoutes() {
        return route("patient-service")
                .route(path("/patients/**"), http())
                .before(addGatewayAuth())
                .before(uri("http://localhost:8081"))
                .build()

                .and(route("note-service")
                        .route(path("/notes/**"), http())
                        .before(addGatewayAuth())
                        .before(uri("http://localhost:8083"))
                        .build()
                )

                .and(route("assessment-service")
                        .route(path("/assessment/**"), http())
                        .before(addGatewayAuth())
                        .before(uri("http://localhost:8084"))
                        .build()
                );
    }

    private static Function<ServerRequest, ServerRequest> addGatewayAuth() {
        return request -> {
            if (!request.headers().header("Authorization").isEmpty()) {
                return request; // laisse passer le header existant
            }

            String auth = "gateway:gateway-secret";
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            return ServerRequest.from(request)
                    .header("Authorization", "Basic " + encodedAuth)
                    .build();
        };
    }
}