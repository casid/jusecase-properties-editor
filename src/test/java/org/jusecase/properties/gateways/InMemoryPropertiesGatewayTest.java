package org.jusecase.properties.gateways;

import org.junit.Before;

public class InMemoryPropertiesGatewayTest extends PropertiesGatewayTest {
    @Before
    public void setUp() throws Exception {
        gateway = new InMemoryPropertiesGateway();
    }
}