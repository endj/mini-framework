package se.edinjakupovic.routing;

import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;
import se.edinjakupovic.request.TypedRequestHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PrefixTreePathNode {

    private PrefixTreePathNode wildcardNode;
    private final Map<String, PrefixTreePathNode> children;
    private TypedRequestHandler<?, ?> handler;

    private PrefixTreePathNode(Map<String, PrefixTreePathNode> children, TypedRequestHandler<?, ?> handler) {
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
                new TypedRequestHandler<>(
                        new RequestHandler<String, Void>() {
                            @Override
                            public Response<Void> handle(Request<String> request) {
                                return Response.EMPTY_RESPONSE;
                            }

                        },
                        String.class
                )
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
    public <T, R> TypedRequestHandler<T, R> getHandler() {
        return (TypedRequestHandler<T, R>) handler;
    }


    public void setHandler(TypedRequestHandler<?, ?> handler) {
        this.handler = handler;
    }
}
