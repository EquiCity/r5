package com.conveyal.r5.profile.entur.rangeraptor.standard.configure;

import com.conveyal.r5.profile.entur.api.request.RangeRaptorProfile;
import com.conveyal.r5.profile.entur.rangeraptor.transit.SearchContext;

import static com.conveyal.r5.profile.entur.api.request.RangeRaptorProfile.NO_WAIT_BEST_TIME;
import static com.conveyal.r5.profile.entur.api.request.RangeRaptorProfile.NO_WAIT_STD;


/**
 * This class verify that the request is valid in the context of Standard Range Raptor.
 */
class VerifyRequestIsValid  {
    private final SearchContext<?> context;


    VerifyRequestIsValid(SearchContext<?> context) {
        this.context = context;
    }

    void verify() {
        verifyNoWaitWorkersIsOneIterationOnly();
    }


    /* private methods */

    private void verifyNoWaitWorkersIsOneIterationOnly() {
        verify(noWaitWorker() && !oneIteration(), "The profile %s is only defined for one iteration.", profile());
    }

    private void verify(boolean condition, String format, Object ... args) {
        if(condition) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    private RangeRaptorProfile profile() {
        return context.request().profile();
    }

    private boolean noWaitWorker() {
        return context.request().profile().isOneOf(NO_WAIT_STD, NO_WAIT_BEST_TIME);
    }

    private boolean oneIteration() {
        return context.calculator().oneIterationOnly();
    }

}
