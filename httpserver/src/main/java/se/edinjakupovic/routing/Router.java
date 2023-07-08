package se.edinjakupovic.routing;

import com.sun.net.httpserver.HttpExchange;
import se.edinjakupovic.request.RequestFactory;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;
import se.edinjakupovic.request.RoutingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Router {

    private static final PrefixTreePathNode ROOT = PrefixTreePathNode.rootNode();
    private static final RequestHandler<String, String> ROOT_HANDLER = requireNonNull(ROOT.getHandler());
    private final RequestFactory requestFactory;

    public Router(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public Response<?> handleRequest(String[] uriSegments, HttpExchange exchange) {
        if (uriSegments.length == 0)
            return ROOT_HANDLER.handle(requestFactory.from(exchange, String.class));

        PrefixTreePathNode current = ROOT;
        List<String> pathVariables = null;
        Map<String, String> requestParameters = null;

        for (int i = 1; i < uriSegments.length; i++) {
            var segment = uriSegments[i];

            var lastSegment = i == uriSegments.length - 1;
            if (lastSegment) {
                int index = segment.indexOf("?");
                var hasRequestParameters = index != -1;
                if (hasRequestParameters) {
                    requestParameters = requestFactory.parseRequestParams(segment.substring(index + 1));
                    segment = segment.substring(0, index);
                }
            }

            if (current.hasChildren()) {
                PrefixTreePathNode pathTreeNode = current.getChild(segment);
                if (pathTreeNode != null) {
                    current = pathTreeNode;
                    continue;
                }
            }

            if (current.getWildcardNode() == null)
                throw new RoutingException("No handler for provider wildcard value " + segment);

            if (pathVariables == null)
                pathVariables = new ArrayList<>(1 << 2);
            pathVariables.add(segment);
            current = current.getWildcardNode();
        }
        if (current.getHandler() == null)
            throw new RoutingException("No handler for provided path");

        var handler = current.getHandler();
        return handler.handle(requestFactory.from(
                exchange,
                pathVariables,
                requestParameters,
                handler.requestType()
        ));
    }

    public void registerRoute(List<PathSection> pathNodeList, RequestHandler<?, ?> handler) {

        if (pathNodeList.isEmpty())
            throw new IllegalArgumentException("Invalid route " + pathNodeList);

        PrefixTreePathNode current = ROOT;
        PathSection sectionNode = null;

        for (PathSection pathSection : pathNodeList) {
            sectionNode = pathSection;
            if (sectionNode.type() == PathType.DELIMITER)
                continue;

            if (sectionNode.type() == PathType.SEGMENT) {
                PrefixTreePathNode nextSection = current.getChild(sectionNode.name());
                if (nextSection == null) {
                    nextSection = PrefixTreePathNode.segment();
                    current.addChild(sectionNode.name(), nextSection);
                }
                current = nextSection;
            }

            if (sectionNode.type() == PathType.VARIABLE) {
                if (current.getWildcardNode() == null)
                    current.setWildcardNode(PrefixTreePathNode.variable());
                current = current.getWildcardNode();
            }
        }

        if (!sectionNode.endsPath())
            throw new IllegalStateException("Last node had no handler");
        current.setHandler(handler);
    }


}
