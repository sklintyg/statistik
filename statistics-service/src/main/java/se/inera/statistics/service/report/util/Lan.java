package se.inera.statistics.service.report.util;

import org.springframework.stereotype.Component;

import java.util.SortedMap;
import java.util.TreeMap;

public class Lan {

    private SortedMap<String, String> lans = new TreeMap<>();

    public Lan() {
        lans.put("01", "Stockholms Län");
        lans.put("\u00d7vrigt", "\u00d7vrigt");
    }

    public SortedMap<String, String> getLans() {
        return lans;
    }
}
