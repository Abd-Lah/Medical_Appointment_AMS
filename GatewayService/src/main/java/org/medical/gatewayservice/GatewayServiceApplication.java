package org.medical.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient // Enable Eureka Client for service discovery
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	@Bean
	public DiscoveryClientRouteDefinitionLocator
	discoveryClientRouteLocator(DiscoveryClient discoveryClient) {

		return new DiscoveryClientRouteDefinitionLocator(discoveryClient);
	}
}
