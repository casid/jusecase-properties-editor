package org.jusecase.properties.gateways;

public class InMemoryPropertiesGateway_SingleThreadTest extends PropertiesGatewayTest {

    @Override
    protected PropertiesGateway createGateway() {
        InMemoryPropertiesGateway inMemoryPropertiesGateway = new InMemoryPropertiesGateway();
        inMemoryPropertiesGateway.setUseMultipleThreads(false);
        return inMemoryPropertiesGateway;
    }
}