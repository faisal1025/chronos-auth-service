package com.chronos.AuthService.gateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;


@Configuration
public class GatewayRoutesConfig {

//    @Bean
//    RouterFunction<ServerResponse> gatewayRoutes(
//            @Value("${services.user.base-url:http://localhost:8081}") String userServiceBaseUrl,
//            @Value("${services.job.base-url:http://localhost:8082}") String jobServiceBaseUrl
//    ) {
//
//        return GatewayRouterFunctions.route()
//
//                // User service
//                .route(route("user-service")
//                        .path("/api/users/**")
//                        .uri(userServiceBaseUrl)
//                )
//
//                // Job service
//                .route(route("job-service")
//                        .path("/api/jobs/**")
//                        .uri(jobServiceBaseUrl)
//                )
//
//                .build();
//    }
}

