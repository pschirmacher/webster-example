package webster.computers.resources;

import webster.requestresponse.Form;
import webster.requestresponse.Request;
import webster.resource.HtmlResource;
import webster.util.Maps;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ComputerCreate extends HtmlResource {

    public static final String template = "/computers/computer_new.ssp";

    public static final Form.Description form = new Form.DescriptionBuilder()
            .withField("name", ".+")
            .withField("company", ".+")
            .withField("introduced", ".+")
            .withField("discontinued", ".*")
            .build();

    @Override
    public String template() {
        return template;
    }

    @Override
    public CompletableFuture<Map<String, Object>> templateModel(Request request) {
        return CompletableFuture.completedFuture(Maps.<String, Object>newMap()
                .with("form", form.empty().with("introduced", Instant.now().toString()))
                .build());
    }

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }
}
