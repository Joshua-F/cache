package org.runestar.cache.tools;

import org.runestar.cache.content.io.Packet;
import org.runestar.cache.format.MemCache;

import java.util.SortedMap;
import java.util.TreeMap;

public final class WorldMapArea {

    private static final int WORLDMAPDATA = 23;

    private WorldMapArea() {}

    public static SortedMap<Integer, String> get(MemCache cache) {
        var names = new TreeMap<Integer, String>();
        var details = cache.archive(WORLDMAPDATA).group("details");
        if (details == null) {
            return names;
        }

        for (var file : details.files()) {
            var packet = new Packet(file.data());
            names.put(file.id(), packet.gjstr());
        }

        return names;
    }

}
