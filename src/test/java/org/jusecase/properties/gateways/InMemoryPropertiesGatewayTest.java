package org.jusecase.properties.gateways;

public class InMemoryPropertiesGatewayTest extends PropertiesGatewayTest {

    @Override
    protected PropertiesGateway createGateway() {
        return new InMemoryPropertiesGateway();
    }
}