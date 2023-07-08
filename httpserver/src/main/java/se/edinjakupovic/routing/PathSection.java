package se.edinjakupovic.routing;


import static se.edinjakupovic.routing.PathType.DELIMITER;
import static se.edinjakupovic.routing.PathType.SEGMENT;
import static se.edinjakupovic.routing.PathType.VARIABLE;

public record PathSection(String name, PathType type, boolean endsPath) {
    private static final PathSection DELIMITER_NODE = new PathSection("/", DELIMITER, false);

    public static PathSection segment(String name) {
        return new PathSection(name, SEGMENT, false);
    }

    public static PathSection variable(String name) {
        return new PathSection(name, VARIABLE, false);
    }

    public static PathSection delimiter() {
        return DELIMITER_NODE;
    }

    public static PathSection lastSegment(String name, PathType type) {
        return new PathSection(name, type, true);
    }

}
