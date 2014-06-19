package webster.computers.resources;

import webster.computers.App;
import webster.computers.core.Computer;
import webster.computers.core.Page;
import webster.requestresponse.Form;
import webster.requestresponse.Request;
import webster.resource.HtmlResource;
import webster.util.Maps;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static webster.requestresponse.parsing.Parsers.*;

public class Computers extends HtmlResource {

    private static final ExecutorService computersLoaderService = Executors.newFixedThreadPool(20);

    @Override
    public Set<String> allowedMethods() {
        return new HashSet<>(Arrays.asList("GET", "POST"));
    }

    @Override
    public Set<String> supportedContentTypes() {
        return new HashSet<>(Arrays.asList("application/x-www-form-urlencoded"));
    }

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public String template() {
        return "/computers/computers.ssp";
    }

    @Override
    public CompletableFuture<Map<String, Object>> templateModel(Request request) {
        int page = request.param("page").parse(asInt).orElse(0);
        Optional<String> sortBy = request.param("sortBy").value();
        boolean sortDesc = request.param("order").parse(asTrueIfEqualTo("desc"));
        Optional<String> filter = request.param("filter").value();

        return loadComputers(page, sortBy, sortDesc, filter)
                .thenApply(this::createTemplateModel);
    }

    private Map<String, Object> createTemplateModel(Page<Computer> computerPage) {
        return Maps.<String, Object>newMap()
                .with("currentPage", computerPage)
                .build();
    }

    private CompletableFuture<Page<Computer>> loadComputers(int page,
                                                            Optional<String> sortBy,
                                                            boolean sortDesc,
                                                            Optional<String> filter) {
        return CompletableFuture.supplyAsync(
                () -> Computer.loadPage(page, sortBy, sortDesc, filter),
                computersLoaderService);
    }

    @Override
    public CompletableFuture<Boolean> isPostValid(Request request) {
        Form form = ComputerCreate.form.parse(request);
        boolean introducedValid = form.field("introduced").parse(asInstant).isPresent();
        return CompletableFuture.completedFuture(!form.hasError() && introducedValid);
    }

    @Override
    public CompletableFuture<String> badRequestEntity(Request request) {
        return CompletableFuture.completedFuture(
                renderHtml(
                        ComputerCreate.template,
                        Maps.<String, Object>newMap().with("form", ComputerCreate.form.parse(request)).build(),
                        request)
        );
    }

    @Override
    public CompletableFuture<Void> onPost(Request request) {
        Form form = ComputerCreate.form.parse(request);
        Computer.store(new Computer(
                System.currentTimeMillis(),
                form.field("name").parse(asTrimmed).get(),
                form.field("introduced").parse(asInstant).map(Date::from).get(),
                form.field("discontinued").parse(asInstant).map(Date::from),
                form.field("company").parse(asTrimmed).get()));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> redirectAfterPost(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<String> locationHeader(Request request) {
        return CompletableFuture.completedFuture(
                App.computers.withQueryParam("filter", request.body().parse(asForm).get("name")).absoluteUrl());
    }
}
