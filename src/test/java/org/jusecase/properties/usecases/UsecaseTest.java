package org.jusecase.properties.usecases;

import org.junit.After;
import org.junit.Before;
import org.jusecase.Usecase;
import org.jusecase.util.GenericTypeResolver;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class UsecaseTest<Request, Response> {
    protected Usecase<Request, Response> usecase;
    protected Request request;
    protected Response response;
    private Throwable error;
    private boolean errorAsserted;

    @SuppressWarnings("unchecked")
    @Before
    public void createRequest() {
        try {
            Class<?> requestClass = GenericTypeResolver.resolve(UsecaseTest.class, getClass(), 0);
            request = (Request) requestClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate request. You need to override createRequest() and do it manually.", e);
        }
    }

    @After
    public void checkError() throws Throwable {
        if (error != null && !errorAsserted) {
            throw error;
        }
    }

    protected void whenRequestIsExecuted() {
        try {
            response = usecase.execute(request);
        } catch (Throwable e) {
            error = e;
        }
    }

    protected void thenResponseIs(Response expected) {
        assertThat(response).isEqualTo(expected);
    }

    protected void thenResponseIsNotNull() {
        assertThat(response).isNotNull();
    }

    protected void thenErrorIs(Throwable expected) {
        assertThat(getError()).isEqualTo(expected);
        errorAsserted = true;
    }

    protected void thenErrorIs(Class<? extends Throwable> expected) {
        assertThat(error).isInstanceOf(expected);
        errorAsserted = true;
    }

    protected void thenErrorMessageIs(String expected) {
        assertThat(getError()).describedAs("Expected error with message '" + expected + "', but nothing was thrown.").isNotNull();
        assertThat(getError().getMessage()).isEqualTo(expected);
        errorAsserted = true;
    }

    protected void thenNoErrorIsThrown() {
        if (error != null) {
            error.printStackTrace();
        }
        assertThat(getError()).isNull();
    }

    protected Throwable getError() {
        errorAsserted = true;
        return error;
    }
}
