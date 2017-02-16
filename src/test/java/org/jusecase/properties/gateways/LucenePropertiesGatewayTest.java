package org.jusecase.properties.gateways;

import org.junit.Before;

public class LucenePropertiesGatewayTest extends PropertiesGatewayTest {
    @Before
    public void setUp() throws Exception {
        gateway = new LucenePropertiesGateway();
    }
}