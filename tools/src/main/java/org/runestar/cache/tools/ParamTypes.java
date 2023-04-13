package org.runestar.cache.tools;

import org.runestar.cache.content.Type;
import org.runestar.cache.content.config.ConfigType;
import org.runestar.cache.content.config.ParamType;
import org.runestar.cache.format.MemCache;

import java.util.SortedMap;
import java.util.TreeMap;

public final class ParamTypes {

    private ParamTypes() {}

    public static SortedMap<Integer, String> get(MemCache cache) {
        var paramTypes = new TreeMap<Integer, String>();
        for (var file : cache.archive(ConfigType.ARCHIVE).group(ParamType.GROUP).files()) {
            var param = new ParamType();
            param.decode(file.data());
            if (param.type != -1) {
                paramTypes.put(file.id(), Type.ofAuto(param.type).getLiteral());
            }
        }
        return paramTypes;
    }
}
