/**
 * ADD LICENSE
 */
package com.salesforce.omakase.error;

import com.google.common.base.Optional;

/**
 * Base class for {@link ErrorManager}s.
 * 
 * @author nmcwilliams
 */
public abstract class AbstractErrorManager implements ErrorManager {
    private Optional<Reporting> reporting = Optional.absent();

    @Override
    public void reporting(Reporting reporting) {
        this.reporting = Optional.of(reporting);

    }

    /**
     * Gets the real error level, taking into account any specified overrides.
     * 
     * @param id
     *            The error id.
     * @param defaultLevel
     *            The default error level to use if no overrides are specified.
     * @return The error level.
     */
    protected ErrorLevel getLevel(String id, ErrorLevel defaultLevel) {
        if (reporting.isPresent()) {
            Optional<ErrorLevel> override = reporting.get().getErrorLevelOverride(id);
            if (override.isPresent()) return override.get();
        }
        return defaultLevel;
    }
}
