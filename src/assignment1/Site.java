package assignment1;

import java.util.*;

/**
 * A representation of a site on which facilities can be installed.
 * <p>
 * DO NOT MODIFY THIS FILE IN ANY WAY.
 */
public final class Site {

    // the identifier of the site
    private final int identifier;
    // the maximum anumber of facilities that can be installed at the site at any one time
    private final int capacity;
    // the facilities that have approval to be installed at the site
    private final HashSet<Facility> approved;
    // a mapping from each approved facility to the cost of installing it at the site
    private final HashMap<Facility, Integer> installationCost;
    // a mapping from each approved facility to the cost of removing it from the site
    private final HashMap<Facility, Integer> removalCost;

    /*
     * class invariant: capacity > 0 && approved != null && approved.size() > 0 &&
     * !approved.contains(null) && installationCost != null && removalCost != null &&
     * installationCost.keySet().equals(approved) && removalCost.keySet().equals(approved)
     * && for each approved facility, installationCost.get(f) > 0 and removalCost.get(f) >
     * 0
     */

    /**
     * @require capacity > 0 && approved != null && approved.size() > 0 &&
     * !approved.contains(null) && installationCost != null && removalCost !=
     * null && installationCost.keySet().equals(approved) &&
     * removalCost.keySet().equals(approved) && for each approved facility,
     * installCost.get(f) > 0 and removalCost.get(f) > 0
     * @ensure Creates a new site with the given identifier, maximum number of facilities
     * that can be installed at the site at any one time, set of facilities that
     * have approval to the installed at the site, and installation and removal
     * costs for each of the approved facilities.
     */
    public Site(int identifier, int capacity, Set<Facility> approved,
                Map<Facility, Integer> installationCost, Map<Facility, Integer> removalCost) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "The capacity of the site must be greater than zero.");
        }
        if (approved == null || approved.isEmpty() || approved.contains(null)) {
            throw new IllegalArgumentException(
                    "The approved facilities must be non-null and non-empty");
        }
        if (installationCost == null || removalCost == null
                || !installationCost.keySet().equals(approved)
                || !removalCost.keySet().equals(approved)) {
            throw new IllegalArgumentException(
                    "There must (only) be an installation and removal cost "
                            + "for each approved facility.");
        }
        for (Facility f : approved) {
            if (installationCost.get(f) == null || installationCost.get(f) <= 0) {
                throw new IllegalArgumentException(
                        "The installation cost for each approved facility must be "
                                + "greater than 0.");
            }
            if (removalCost.get(f) == null || removalCost.get(f) <= 0) {
                throw new IllegalArgumentException(
                        "The removal cost for each approved facility must be greater than 0.");
            }
        }
        this.identifier = identifier;
        this.capacity = capacity;
        this.approved = new HashSet<>(approved);
        this.installationCost = new HashMap<>(installationCost);
        this.removalCost = new HashMap<>(removalCost);
    }

    /**
     * @ensure Returns the identifier of the site.
     */
    public int identifier() {
        return identifier;
    }

    /**
     * @ensure Returns the maximum number of facilities that can be installed at the site
     * at any one time.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * @ensure Returns a set of the facilities that have been approved for installation at
     * this site.
     */
    public HashSet<Facility> approved() {
        return new HashSet<>(approved);
    }

    /**
     * @ensure Returns true if and only if the given facility has been approved for
     * installation at this site.
     */
    public boolean approved(Facility facility) {
        return approved.contains(facility);
    }

    /**
     * @require The given facility has been approved for installation at this site.
     * @ensure Returns the cost of installing the given facility at the site.
     */
    public int installationCost(Facility facility) {
        return installationCost.get(facility);
    }

    /**
     * @require The given facility has been approved for installation at this site.
     * @ensure Returns the cost of removing the given facility from the site.
     */
    public int removalCost(Facility facility) {
        return removalCost.get(facility);
    }

    @Override
    public String toString() {
        return "S" + identifier;
    }

}
