package webster.computers.resources;

import webster.computers.App;
import webster.requestresponse.Request;
import webster.resource.RedirectingResource;

import java.util.concurrent.CompletableFuture;

public class Home implements RedirectingResource {

    @Override
    public CompletableFuture<String> locationHeader(Request request) {
        return CompletableFuture.completedFuture(App.computers.absoluteUrl());
    }
}
