package com.nexo.nexorouter.microservice.common.util;

/**
 * Created by carlos on 18/04/17.
 */
public class Util {
    public static String event(Class<?> _class) {
        return _class.getPackage().getName()
                .replaceAll("\\.([a-z]+|[A-Z]+)\\Z", "")
                .replaceAll(".*\\.", "")
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase() + "@" +
                _class.getSimpleName()
                        .replaceAll("([a-z])([A-Z])", "$1-$2")
                        .toLowerCase();
    }
}
