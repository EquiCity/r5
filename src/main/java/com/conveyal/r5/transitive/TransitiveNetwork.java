package com.conveyal.r5.transitive;

import com.conveyal.gtfs.model.Route;
import com.conveyal.gtfs.model.Stop;
import com.conveyal.r5.streets.VertexStore;
import com.conveyal.r5.transit.RouteInfo;
import com.conveyal.r5.transit.TransitLayer;
import com.conveyal.r5.transit.TripPattern;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A representation of a TransitLayer as a Transitive network.
 * See https://github.com/conveyal/transitive.js/wiki/Transitive-Conceptual-Overview
 * @author mattwigway
 */
public class TransitiveNetwork {
    public List<TransitiveRoute> routes = new ArrayList<>();
    public List<TransitiveStop> stops = new ArrayList<>();
    public List<TransitivePattern> patterns = new ArrayList<>();
    // places, journeys not currently supported - these are added by the client.

    public TransitiveNetwork (TransitLayer layer) {
        // first write patterns, accumulating routes along the way
        TIntObjectMap<TransitiveRoute> routes = new TIntObjectHashMap<>();

        for (int pattIdx = 0; pattIdx < layer.tripPatterns.size(); pattIdx++) {
            TripPattern patt = layer.tripPatterns.get(pattIdx);

            if (!routes.containsKey(patt.routeIndex)) {
                // create the route
                // TODO save enough information to get rid of all of this boilerplate
                TransitiveRoute route = new TransitiveRoute();
                RouteInfo ri = layer.routes.get(patt.routeIndex);
                route.agency_id = ri.agency_id;
                route.route_short_name = ri.route_short_name;
                route.route_long_name = ri.route_long_name;
                route.route_id = patt.routeIndex + "";
                route.route_type = ri.route_type;
                route.color = ri.color;
                routes.put(patt.routeIndex, route);
            }

            TransitivePattern tr = new TransitivePattern();
            // TODO boilerplate
            tr.pattern_id = pattIdx + "";
            tr.pattern_name = routes.get(patt.routeIndex).route_short_name;
            tr.route_id = patt.routeIndex + "";
            tr.stops = IntStream.of(patt.stops).mapToObj(s -> new TransitivePattern.StopIdRef(s + "")).collect(Collectors.toList());
            patterns.add(tr);
        }

        this.routes.addAll(routes.valueCollection());

        VertexStore.Vertex v = layer.linkedStreetLayer.vertexStore.getCursor();

        // write stops
        for (int sidx = 0; sidx < layer.getStopCount(); sidx++) {
            v.seek(layer.streetVertexForStop.get(sidx));
            TransitiveStop ts = new TransitiveStop();
            ts.stop_id = sidx + "";
            ts.stop_lat = v.getLat();
            ts.stop_lon = v.getLon();
            ts.stop_name = layer.stopNames.get(sidx);
            stops.add(ts);
        }
    }
}