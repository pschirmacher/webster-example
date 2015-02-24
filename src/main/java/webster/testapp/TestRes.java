package webster.testapp;

import webster.requestresponse.Request;
import webster.requestresponse.ResponseBody;
import webster.requestresponse.Responses;
import webster.requestresponse.parsing.FormParser;
import webster.resource.Resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TestRes implements Resource {

    @Override
    public CompletableFuture<Void> onDelete(Request request) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("deleted" + request.body().value().map(s -> ": " + s).orElse(""));
            Map<String, String> form = request.body().parse(new FormParser());
            form.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> onPost(Request request) {
        System.out.println("POST " + request.body().value().orElse(""));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> locationHeader(Request request) {
        return CompletableFuture.completedFuture("http://foo.bar/baz");
    }

    @Override
    public Set<String> allowedMethods() {
        return new HashSet<>(Arrays.asList("GET", "DELETE", "POST"));
    }

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Set<String> supportedMediaTypes() {
        return new HashSet<>(Arrays.<String>asList("text/html", "application/json"));
    }

    @Override
    public CompletableFuture<ResponseBody> entity(Request request) {
        return CompletableFuture.supplyAsync(() -> Responses.bodyFrom("foo"));
    }
}
