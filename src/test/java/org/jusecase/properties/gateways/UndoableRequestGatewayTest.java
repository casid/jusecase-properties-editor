package org.jusecase.properties.gateways;

import org.junit.Test;
import org.jusecase.properties.usecases.UndoableRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class UndoableRequestGatewayTest {
    private UndoableRequestGateway gateway = new UndoableRequestGateway();
    @Test
    public void add() {
        UndoableRequest request1 = new UndoableRequest();
        UndoableRequest request2 = new UndoableRequest();
        gateway.add(request1);
        gateway.add(request2);

        gateway.markNextRequestForUndo();

        UndoableRequest request3 = new UndoableRequest();
        gateway.add(request3);

        assertThat(gateway.contains(request1)).isTrue();
        assertThat(gateway.contains(request2)).isFalse();
        assertThat(gateway.contains(request3)).isTrue();
        assertThat(gateway.getNextRequestToUndo()).isSameAs(request3);
        assertThat(gateway.getNextRequestToRedo()).isNull();
    }
}