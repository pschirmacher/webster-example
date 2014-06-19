package webster.testapp;

import webster.requestresponse.Request;
import webster.resource.Resource;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.joining;

public class PathParamsRes implements Resource {

    @Override
    public CompletableFuture<Object> entity(Request request) {
        return CompletableFuture.completedFuture("{" +
                "\"splats\":\"" + request.splats().value().stream().collect(joining(",")) + "\",\n" +
                "\"foo\":\"" + request.pathParam(":foo").value().get() + "\""
                + "}\n");
    }

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Set<String> supportedMediaTypes() {
        return Collections.singleton("application/json");
    }
}
