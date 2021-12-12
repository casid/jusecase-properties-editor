package org.jusecase.properties.gateways;

import org.jusecase.properties.entities.UndoableRequest;

import java.util.ArrayList;
import java.util.List;

public class UndoableRequestGateway {
    private final List<UndoableRequest> requests = new ArrayList<>();
    private int redoIndex;

    public void add(UndoableRequest request) {
        while (redoIndex < requests.size()) {
            requests.remove(redoIndex);
        }

        requests.add(request);
        redoIndex++;
    }

    public boolean contains(UndoableRequest undoableRequest) {
        return requests.contains(undoableRequest);
    }

   public List<UndoableRequest> getAll() {
      return new ArrayList<>(requests);
   }

   public UndoableRequest getNextRequestToUndo() {
        if (redoIndex > 0) {
            return requests.get(redoIndex - 1);
        }
        return null;
    }

    public UndoableRequest getNextRequestToRedo() {
        if (redoIndex < requests.size()) {
            return requests.get(redoIndex);
        }
        return null;
    }

    public void markNextRequestForUndo() {
        --redoIndex;
    }

    public void markNextRequestForRedo() {
        ++redoIndex;
    }

    public void clear() {
        requests.clear();
        redoIndex = 0;
    }

    public int size() {
        return requests.size();
    }
}
