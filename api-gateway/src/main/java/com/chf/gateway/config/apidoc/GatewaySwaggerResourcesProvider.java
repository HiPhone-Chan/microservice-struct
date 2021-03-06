package com.chf.gateway.config.apidoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.chf.core.constants.SystemConstants;

import reactor.core.scheduler.Schedulers;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * Retrieves all registered microservices Swagger resources.
 */
@Component
@Primary
@Configuration
@Profile(SystemConstants.PROFILE_API_DOCS)
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {

    private String gatewayName;

    private final RouteLocator routeLocator;

    @Qualifier("swaggerResources")
    private final SwaggerResourcesProvider swaggerResourcesProvider;

    public GatewaySwaggerResourcesProvider(RouteLocator routeLocator,
            SwaggerResourcesProvider swaggerResourcesProvider) {
        this.routeLocator = routeLocator;
        this.swaggerResourcesProvider = swaggerResourcesProvider;
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> swaggerResources = new ArrayList<>();

        swaggerResources.add(swaggerResource("(default)", "/v3/api-docs"));
        swaggerResources.add(swaggerResource("(management)", "/v3/api-docs?group=management"));

        List<String> microservices = routeLocator.getRoutes().map(this::getMicroserviceName).collectList()
                .defaultIfEmpty(Collections.emptyList()).subscribeOn(Schedulers.boundedElastic()).toFuture()
                .orTimeout(10, TimeUnit.SECONDS)
                .join();
        microservices.stream().filter(this::isNotGateway).filter(this::isNotConsul)
                .forEach(microservice -> swaggerResources
                        .add(swaggerResource(microservice, getMicroserviceApiDocs(microservice))));
        return swaggerResources;
    }

    public static SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("3.0");
        return swaggerResource;
    }

    private boolean isNotGateway(String name) {
        return !name.equalsIgnoreCase(gatewayName);
    }

    private boolean isNotConsul(String name) {
        return !name.equalsIgnoreCase("consul");
    }

    private String getMicroserviceApiDocs(String name) {
        return "/services/".concat(name).concat("/v3/api-docs");
    }

    private String getMicroserviceName(Route route) {
        return route.getUri().toString().replace("lb://", "").toLowerCase();
    }
}
