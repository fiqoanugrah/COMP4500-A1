package assignment1;

import java.util.*;

/**
 * An abstract class to represent actions that can be taken to modify the government-owned
 * sites.
 * <p>
 * DO NOT MODIFY THIS FILE IN ANY WAY.
 */
public abstract class Action {

    /**
     * @ensure Returns the cost of performing this action.
     */
    public abstract int cost();

    /**
     * @require Configuration.isValid(configuration)
     * @ensure Returns whether or not this action can be applied to the given
     * configuration.
     */
    public abstract boolean canApply(HashMap<Site, HashSet<Facility>> configuration);

    /**
     * @require Configuration.isValid(configuration)and canApply(configuration)
     * @ensure The given configuration is updated by performing this action.
     */
    public abstract void apply(HashMap<Site, HashSet<Facility>> configuration);

    /**
     * Represents an action to directly install a facility at a site.
     */
    public final static class DirectInstallAction extends Action {
        // the facility to be directly installed
        private final Facility facility;
        // the site of the installation
        private final Site site;

        /**
         * @require facility != null && site != null && site.approved(facility)
         * @ensure Creates a new action to directly install the given facility at the
         * given site.
         */
        public DirectInstallAction(Facility facility, Site site) {
            if (facility == null || site == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
            }
            if (!site.approved(facility)) {
                throw new IllegalArgumentException(
                        "The facility must have approval at the installation site.");
            }
            this.facility = facility;
            this.site = site;
        }

        /**
         * @ensure Returns the facility to be installed.
         */
        public Facility facility() {
            return facility;
        }

        /**
         * @ensure Returns the site where the facility will be installed.
         */
        public Site site() {
            return site;
        }

        @Override
        public int cost() {
            return site.installationCost(facility);
        }

        @Override
        public boolean canApply(HashMap<Site, HashSet<Facility>> configuration) {
            // the installation site must be part of the configuration
            if (!configuration.containsKey(site)) {
                return false;
            }
            // the facility must not currently be installed at any site
            for (Site site : configuration.keySet()) {
                if (configuration.get(site).contains(facility)) {
                    return false;
                }
            }
            // there must be room at the site to install the facility
            return configuration.get(site).size() != site.capacity();
        }

        @Override
        public void apply(HashMap<Site, HashSet<Facility>> configuration) {
            configuration.get(site).add(facility);
        }

        @Override
        public String toString() {
            return "directly install " + facility + " at " + site + " at cost " + cost();
        }
    }

    /**
     * Represents an action to relocate a facility from one site to another.
     */
    public final static class RelocateAction extends Action {
        // the facility to be relocated
        private final Facility facility;
        // the site the facility is to be moved from
        private final Site source;
        // the site that the facility is to be moved to
        private final Site destination;

        /**
         * @require facility != null && source != null && destination !=null &&
         * !source.equals(destination) && source.approved(facility) &&
         * destination.approved(facility) &&
         * @ensure Creates a new action to move the given facility from the source site to
         * the destination site.
         */
        public RelocateAction(Facility facility, Site source, Site destination) {
            if (facility == null || source == null || destination == null) {
                throw new IllegalArgumentException("The arguments cannot be null");
            }
            if (source.equals(destination)) {
                throw new IllegalArgumentException(
                        "Cannot relocate a facility from a site to itself.");
            }
            if (!source.approved(facility) || !destination.approved(facility)) {
                throw new IllegalArgumentException("The facility must have approval at both "
                        + "the source and destination sites.");
            }
            this.facility = facility;
            this.source = source;
            this.destination = destination;
        }

        /**
         * @ensure Returns the facility to be relocated.
         */
        public Facility facility() {
            return facility;
        }

        /**
         * @ensure Returns the site that the facility will be moved from.
         */
        public Site source() {
            return source;
        }

        /**
         * @ensure Returns the site that the facility will be moved to.
         */
        public Site destination() {
            return destination;

        }

        @Override
        public int cost() {
            return source.removalCost(facility) + destination.installationCost(facility);
        }

        @Override
        public boolean canApply(HashMap<Site, HashSet<Facility>> configuration) {
            // the source and destination sites are part of the configuration
            if (!configuration.containsKey(source)
                    || !configuration.containsKey(destination)) {
                return false;
            }
            // the facility must be installed at the source site
            if (!configuration.get(source).contains(facility)) {
                return false;
            }
            // there must be room at the destination site to install the facility
            return configuration.get(destination).size() != destination.capacity();
        }

        @Override
        public void apply(HashMap<Site, HashSet<Facility>> configuration) {
            configuration.get(source).remove(facility);
            configuration.get(destination).add(facility);
        }

        @Override
        public String toString() {
            return "relocate " + facility + " from " + source + " to " + destination + " at cost "
                    + cost();
        }

    }

}
