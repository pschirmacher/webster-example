package webster.testapp;

import webster.requestresponse.Request;
import webster.requestresponse.ResponseBody;
import webster.requestresponse.Responses;
import webster.resource.ContentNegotiationResource;
import webster.resource.Html;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConNegRes extends ContentNegotiationResource implements Html {

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public ContentNegotiator contentNegotation() {
        return supportFor()
                .mediaType("application/json", this::jsonEntity)
                .mediaType("application/xml", this::xmlEntity)
                .mediaType("text/html", htmlProducer("conneg.jade", this::templateModel));
    }

    private CompletableFuture<ResponseBody> jsonEntity(Request request) {
        return loadEntity(request).thenApply(e -> Responses.bodyFrom("{\"json\":\"" + e + "\"}"));
    }

    private CompletableFuture<ResponseBody> xmlEntity(Request request) {
        return loadEntity(request).thenApply(e -> Responses.bodyFrom("<xml>" + e + "</xml>"));
    }

    private CompletableFuture<String> loadEntity(Request request) {
        return CompletableFuture.completedFuture("entity for media type " + request.header("Accept").value().get());
    }

    public CompletableFuture<Map<String, Object>> templateModel(Request request) {
        Map<String, Object> model = new HashMap<>();
        model.put("mediatype", request.header("Accept").value().get());
        return CompletableFuture.completedFuture(model);
    }
}
