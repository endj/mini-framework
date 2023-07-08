package se.edinjakupovic.routing;

import java.util.ArrayList;
import java.util.List;

public class UrlPartsParser {

    private static final char SLASH = '/';
    private static final char URI_PARAM_START = ':';

    public static List<PathSection> parse(String route) {
        if (route == null || route.isBlank() || route.charAt(0) != SLASH)
            throw new RouteConfigurationException("Invalid route " + route);

        StringBuilder pathVariableBuffer = new StringBuilder();
        List<PathSection> sectionNodes = new ArrayList<>();

        ParseState state = ParseState.READING_NEXT;

        for (int i = 0; i < route.length(); i++) {
            char c = route.charAt(i);

            switch (state) {
                case READING_NEXT -> {
                    if (c == URI_PARAM_START)
                        state = ParseState.READING_URI_PARAM;
                    else if (c == SLASH)
                        sectionNodes.add(PathSection.delimiter());
                    else {
                        pathVariableBuffer.append(c);
                        state = ParseState.READING_URI_PART;
                    }
                }


                case READING_URI_PARAM -> {
                    if (c == SLASH) {
                        PathSection variable = PathSection.variable(pathVariableBuffer.toString());
                        sectionNodes.add(variable);
                        pathVariableBuffer.setLength(0);
                        state = ParseState.READING_NEXT;
                    } else {
                        pathVariableBuffer.append(c);
                    }
                }


                case READING_URI_PART -> {
                    if (c == SLASH) {
                        PathSection segment = PathSection.segment(pathVariableBuffer.toString());
                        sectionNodes.add(segment);
                        pathVariableBuffer.setLength(0);
                        state = ParseState.READING_NEXT;
                    } else {
                        pathVariableBuffer.append(c);
                    }
                }
            }
        }

        if (sectionNodes.isEmpty())
            throw new IllegalStateException("Something went wrong");

        if (!pathVariableBuffer.isEmpty()) {
            boolean endsWithSlash = pathVariableBuffer.length() == 1 && pathVariableBuffer.charAt(0) == SLASH;
            if (!endsWithSlash) {

                PathSection endSegment = PathSection.lastSegment(pathVariableBuffer.toString(),
                        state == ParseState.READING_URI_PARAM ? PathType.VARIABLE : PathType.SEGMENT);
                sectionNodes.add(endSegment);
            }
        }
        return sectionNodes;
    }

    private enum ParseState {
        READING_URI_PARAM,
        READING_URI_PART,
        READING_NEXT
    }
}
