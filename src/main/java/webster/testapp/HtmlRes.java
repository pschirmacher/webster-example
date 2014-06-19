package webster.testapp;

import webster.requestresponse.Request;
import webster.resource.HtmlResource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HtmlRes extends HtmlResource {

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public String template() {
        return "hello.ssp";
    }

    @Override
    public CompletableFuture<Map<String, Object>> templateModel(Request request) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", request.param("name").value().orElse("world"));
        return CompletableFuture.completedFuture(model);
    }
}
