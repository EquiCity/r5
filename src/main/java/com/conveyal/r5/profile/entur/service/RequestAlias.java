package com.conveyal.r5.profile.entur.service;

import com.conveyal.r5.profile.entur.api.request.RangeRaptorRequest;

import static com.conveyal.r5.profile.entur.api.request.Optimization.PARALLEL;
import static com.conveyal.r5.profile.entur.api.request.Optimization.PARETO_CHECK_AGAINST_DESTINATION;
import static com.conveyal.r5.profile.entur.api.request.Optimization.TRANSFERS_STOP_FILTER;

public class RequestAlias {
    public static String alias(RangeRaptorRequest<?> request, boolean multithreaded) {
        String alias = request.profile().abbreviation();

        if (request.searchInReverse()) {
            alias += "-Rev";
        }
        if (request.optimizationEnabled(PARALLEL) && multithreaded) {
            // Run search in parallel
            alias += "-Pll";
        }
        if (request.optimizationEnabled(PARETO_CHECK_AGAINST_DESTINATION)) {
            // Heuristic to prune on pareto optimal Destination arrivals
            alias += "-?Dst";
        }
        if (request.optimizationEnabled(TRANSFERS_STOP_FILTER)) {
            // Heuristic used to generate stop filter based on number of transfers
            alias += "-?Stp";
        }
        return alias;
    }
}
