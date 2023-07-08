package se.edinjakupovic.routing;

import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PrefixTreePathNode {

    private PrefixTreePathNode wildcardNode;
    private final Map<String, PrefixTreePathNode> children;
    private RequestHandler<?, ?> handler;

    private PrefixTreePathNode(Map<String, PrefixTreePathNode> children, RequestHandler<?, ?> handler) {
        this.wildcardNode = null;
        this.children = children;
        this.handler = handler;
    }


    public static PrefixTreePathNode segment() {
        return new PrefixTreePathNode(childMap(), null);
    }

    public static PrefixTreePathNode variable() {
        return new PrefixTreePathNode(childMap(), null);
    }

    public static PrefixTreePathNode rootNode() {
        return new PrefixTreePathNode(
                new HashMap<>(),
                new RequestHandler<String, Void>() {
                    @Override
                    public Response<Void> handle(Request<String> request) {
                        return Response.EMPTY_RESPONSE;
                    }

                    @Override
                    public Class<String> requestType() {
                        return String.class;
                    }
                }
        );
    }

    @Nullable
    public PrefixTreePathNode getWildcardNode() {
        return wildcardNode;
    }


    public void setWildcardNode(PrefixTreePathNode wildcardNode) {
        this.wildcardNode = wildcardNode;
    }

    public void addChild(String segmentName, PrefixTreePathNode node) {
        children.put(segmentName, node);
    }

    @Nullable
    public PrefixTreePathNode getChild(String name) {
        return children.get(name);
    }

    private static Map<String, PrefixTreePathNode> childMap() {
        return new HashMap<>(1 << 2);
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T, R> RequestHandler<T, R> getHandler() {
        return (RequestHandler<T, R>) handler;
    }


    public void setHandler(RequestHandler<?, ?> handler) {
        this.handler = handler;
    }
}
