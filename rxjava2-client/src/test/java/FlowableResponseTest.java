import io.reactivex.Flowable;
import org.junit.Test;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class FlowableResponseTest extends RxJerseyTest {

    @Test
    public void shouldHandleEmpty() {
        Resource resource = target(Resource.class);
        Response response = resource.empty().blockingFirst();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldParseEntity() {
        Resource resource = target(Resource.class);
        Response response = resource.json("hello").blockingFirst();

        assertEquals(response.readEntity(Entity.class).message, "hello");
    }

    @Test
    public void shouldHandleError() {
        Resource resource = target(Resource.class);
        Response response = resource.error().blockingFirst();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test(expected = NotSupportedException.class)
    public void shouldThrowSensibleErrorForNonRxType() {
        Resource resource = target(Resource.class);
        Response response = resource.string();

        assertEquals(response.readEntity(String.class), "");
    }

    @Test(expected = NotSupportedException.class)
    public void shouldThrowSensibleErrorForNonRxTypeWithParam() {
        Resource resource = target(Resource.class);
        Response response = resource.echo("message");

        assertEquals(response.readEntity(String.class), "");
    }

    @Path("/endpoint")
    public interface Resource {

        @GET
        @Path("empty")
        Flowable<Response> empty();

        @GET
        @Path("json")
        Flowable<Response> json(@QueryParam("message") String message);

        @GET
        @Path("error")
        Flowable<Response> error();

        @GET
        @Path("string")
        Response string();

        @GET
        @Path("echo")
        Response echo(@QueryParam("message") String message);

    }
}
