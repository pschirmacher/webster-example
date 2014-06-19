package webster.testapp;

import webster.netty.Server;
import webster.routing.RoutingTable;

import static webster.routing.RoutingBuilder.from;
import static webster.routing.RoutingBuilder.routingTable;

public class App {

    public static void main(String[] args) throws Exception {

        RoutingTable routingTable = routingTable()
                .withRoute(from("/").toResource(TestRes::new))
                .withRoute(from("/conneg").toResource(ConNegRes::new))
                .withRoute(from("/hello").toResource(HtmlRes::new))
                .withRoute(from("/:foo/*").toResource(PathParamsRes::new))
                .build();

        new Server().run(routingTable);
    }
}
