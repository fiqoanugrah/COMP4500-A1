package assignment1;

import java.util.*;

/**
 * This class contains some helper functions related to configurations.
 * <p>
 * A configuration is a mapping from each government-owned site to the set of facilities
 * that are currently installed at that site.
 * <p>
 * DO NOT MODIFY THIS FILE IN ANY WAY.
 */
public class Configuration {

    /**
     * @ensure Returns true when the given configuration is valid, i.e. if it is not null;
     * the sites that are part of the configuration are not null; if each site in
     * the configuration only has facilities installed that are approved for that
     * site, and that the total number of facilities installed at each site
     * does not exceed the capacity of the site; and if each facility that is
     * installed, is installed at only one site.
     */
    public static boolean isValid(HashMap<Site, HashSet<Facility>> configuration) {
        if (configuration == null || configuration.containsKey(null)) {
            return false;
        }
        Set<Facility> allInstalledFacilities = new HashSet<>();
        for (Site site : configuration.keySet()) {
            HashSet<Facility> siteFacilities = configuration.get(site);
            /*
             * The number of facilities installed at a site must not exceed the site's
             * capacity
             */
            if (siteFacilities.size() > site.capacity()) {
                return false;
            }
            /*
             * Each installed facility must be approved for installation at its
             * installation site.
             */
            for (Facility f : siteFacilities) {
                if (!site.approved(f)) {
                    return false;
                }
                if (!allInstalledFacilities.add(f)) {
                    /* No facility can be installed at more than one site. */
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @require isValid(configuration)
     * @ensure Returns the set of facilities that are installed in the given
     * configuration.
     */
    public static HashSet<Facility> installedFacilities(
            HashMap<Site, HashSet<Facility>> configuration) {
        HashSet<Facility> installedFacilities = new HashSet<>();
        for (Site site : configuration.keySet()) {
            installedFacilities.addAll(configuration.get(site));
        }
        return installedFacilities;
    }

    /**
     * @require isValid(configuration) && configuration.keySet().contains(site)
     * @ensure Returns true if the given site has the maximum number of facilities that
     * can be installed at the site in the given configuration, and false
     * otherwise.
     */
    public static boolean atCapacity(Site site, HashMap<Site, HashSet<Facility>> configuration) {
        return configuration.get(site).size() == site.capacity();
    }

    /**
     * @require isValid(initialConfiguration)
     * @ensure Returns true if the given sequence is an installation sequence for the new
     * facility from the given initial configuration, and false otherwise.
     */
    public static boolean validInstallationSequence(
            HashMap<Site, HashSet<Facility>> initialConfiguration, Facility newFacility,
            List<Action> sequence) {
        HashMap<Site, HashSet<Facility>> currentConfiguration = copy(initialConfiguration);
        /* The sequence cannot be null, and must contain at least one action */
        if (sequence == null || sequence.isEmpty()) {
            return false;
        }
        for (int i = 0; i < sequence.size() - 1; i++) {
            /*
             * Each action but the last must be a relocate action that can be applied to
             * the current state of the configuration, after earlier actions from the
             * sequence have been applied, in sequence order.
             */
            Action action = sequence.get(i);
            if (!(action instanceof Action.RelocateAction)
                    || !action.canApply(currentConfiguration)) {
                return false;
            }
            action.apply(currentConfiguration);
        }
        /*
         * The final action must directly install the new facility, and be able to be
         * applied to the current state of the configuration, after earlier actions from
         * the sequence have been applied, in sequence order.
         */
        Action finalAction = sequence.get(sequence.size() - 1);
        if (!(finalAction instanceof Action.DirectInstallAction)
                || !((Action.DirectInstallAction) finalAction).facility().equals(newFacility)
                || !finalAction.canApply(currentConfiguration)) {
            return false;
        }
        finalAction.apply(currentConfiguration);
        return true;
    }

    /**
     * @require isValid(configuration)
     * @ensure returns a copy of the given configuration.
     */
    public static HashMap<Site, HashSet<Facility>> copy(
            HashMap<Site, HashSet<Facility>> configuration) {
        HashMap<Site, HashSet<Facility>> copy = new HashMap<>();
        for (Site site : configuration.keySet()) {
            copy.put(site, new HashSet<>(configuration.get(site)));
        }
        return copy;
    }

    /**
     * @require sequence != null && !sequence.contains(null)
     * @ensure returns the cost of the installation sequence.
     */
    public static int cost(List<Action> sequence) {
        int cost = 0;
        for (Action action : sequence) {
            cost += action.cost();
        }
        return cost;
    }

}
