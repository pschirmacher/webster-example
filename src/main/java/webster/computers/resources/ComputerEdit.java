package webster.computers.resources;

import webster.computers.App;
import webster.computers.core.Computer;
import webster.requestresponse.Form;
import webster.requestresponse.Request;
import webster.requestresponse.Response;
import webster.requestresponse.parsing.Parsers;
import webster.resource.HtmlResource;
import webster.util.Maps;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static webster.requestresponse.parsing.Parsers.*;

public class ComputerEdit extends HtmlResource {

    @Override
    public Set<String> allowedMethods() {
        return new HashSet<>(Arrays.asList("GET", "PUT", "DELETE"));
    }

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        Optional<Long> computerId = request.pathParam(":id").parse(Parsers.asLong);
        return CompletableFuture.supplyAsync(() -> {
            Optional<Computer> computerOptional = computerId.flatMap(Computer::byId);
            computerOptional.ifPresent(c -> request.context().put("computer", c));
            return computerOptional.isPresent();
        });
    }

    @Override
    public String template() {
        return "/computers/computer_edit.ssp";
    }

    @Override
    public CompletableFuture<Map<String, Object>> templateModel(Request request) {
        Computer computer = request.context().getExisting("computer");
        return completedFuture(Maps.<String, Object>newMap()
                .with("form", ComputerCreate.form.empty()
                        .with("name", computer.name())
                        .with("company", computer.company())
                        .with("introduced", computer.introduced().toInstant().toString())
                        .with("discontinued", computer.discontinued().map(d -> d.toInstant().toString()).orElse("")))
                .with("updated", request.context().get("updated").isPresent())
                .build());
    }

    @Override
    public CompletableFuture<Boolean> isPutValid(Request request) {
        Form form = ComputerCreate.form.parse(request);
        boolean introducedValid = form.field("introduced").parse(asInstant).isPresent();
        return completedFuture(!form.hasError() && introducedValid);
    }

    @Override
    public CompletableFuture<String> badRequestEntity(Request request) {
        return completedFuture(
                renderHtml(
                        template(),
                        Maps.<String, Object>newMap().with("form", ComputerCreate.form.parse(request)).build(),
                        request)
        );
    }

    @Override
    public CompletableFuture<Void> onPut(Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Form form = ComputerCreate.form.parse(request);
            Computer computer = new Computer(
                    request.pathParam(":id").parse(asLong).get(),
                    form.field("name").parse(asTrimmed).get(),
                    form.field("introduced").parse(asInstant).map(Date::from).get(),
                    form.field("discontinued").parse(asInstant).map(Date::from),
                    form.field("company").parse(asTrimmed).get());
            Computer.store(computer);
            request.context().put("computer", computer);
            request.context().put("updated", true);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> onDelete(Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Computer.delete(request.pathParam(":id").parse(asLong).get());
            request.context().put("deleted", true);
            return null;
        });
    }

    @Override
    public CompletableFuture<Optional<String>> etag(Request request) {
        return completedFuture(
                request.context().get("computer").map(c -> String.valueOf(c.hashCode())));
    }

    @Override
    public CompletableFuture<Optional<Instant>> lastModified(Request request) {
        return completedFuture(
                request.context().<Computer>get("computer").map(c -> c.lastChangeDate().toInstant()));
    }

    @Override
    public CompletableFuture<Response> override(Request request, Response response) {
        return completedFuture(
                request.context().get("deleted").isPresent()
                        ? new Response(301, Maps.newStringMap().with("Location", App.computers.absoluteUrl()).build())
                        : response
        );
    }
}
