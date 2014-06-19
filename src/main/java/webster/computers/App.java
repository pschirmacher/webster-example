package webster.computers;

import webster.computers.resources.ComputerCreate;
import webster.computers.resources.ComputerEdit;
import webster.computers.resources.Computers;
import webster.computers.resources.Home;
import webster.links.Link;
import webster.netty.Server;
import webster.requestresponse.Request;
import webster.requestresponse.Response;
import webster.resource.AssetsResource;
import webster.routing.Decoratable;
import webster.routing.RoutingTable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static webster.routing.Decorators.logRequestResponse;
import static webster.routing.Decorators.parseHiddenMethodFromForms;
import static webster.routing.RoutingBuilder.from;
import static webster.routing.RoutingBuilder.routingTable;

public class App {

    public static final Link home = link().withPattern("/").build();
    public static final Link computers = link().withPattern("/computers").build();
    public static final Link createComputer = link().withPattern("/computer_new").build();
    public static final Link editComputer = link().withPattern("/computer/:id").build();

    public static void main(String[] args) throws Exception {

        RoutingTable routingTable = routingTable()
                .withRoute(from(home).toResource(Home::new))
                .withRoute(from(computers).toResource(Computers::new))
                .withRoute(from(createComputer).toResource(ComputerCreate::new))
                .withRoute(from(editComputer).toResource(ComputerEdit::new))
                .withRoute(from("/assets/*").toResource(new AssetsResource()))
                .build();

        Function<Request, CompletableFuture<Response>> app = routingTable
                .decoratedWith(parseHiddenMethodFromForms)
                .decoratedWith(logRequestResponse);

        new Server().run(app);
    }

    private static Link.Builder link() {
        return new Link.Builder().withScheme("http").withHost("localhost").withPort(8080);
    }
}
