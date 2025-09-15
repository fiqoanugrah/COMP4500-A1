package assignment1.test;

import org.junit.jupiter.api.Test;

import assignment1.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for the {@link SequenceFinder} implementation class.
 * <p>
 * We will use a much more comprehensive test suite to test your code, so you should add
 * your own tests to this test suite to help you to debug your implementation.
 */
class SequenceFinderTest {

    @Test
    public void handoutTest1() {
        /* Initialise parameters to the test. */

        // the total number of facilities
        // (note that the total number of facilities installed, m, may be less)
        int numFacilities = 12;
        List<Facility> facilities = new ArrayList<>();
        for (int i = 0; i < numFacilities; i++) {
            facilities.add(new Facility(i));
        }
        // the number of sites
        int n = 8;
        int[] siteCapacities = {3, 2, 1, 1, 2, 2, 1, 1};
        Facility[][] approvedFacilities = {
                {facilities.get(1), facilities.get(2), facilities.get(3), facilities.get(9)},
                {facilities.get(4), facilities.get(5), facilities.get(6)},
                {facilities.get(4), facilities.get(5), facilities.get(8)},
                {facilities.get(6), facilities.get(9)},
                {facilities.get(2), facilities.get(3), facilities.get(4), facilities.get(6)},
                {facilities.get(2), facilities.get(4), facilities.get(7), facilities.get(8)},
                {facilities.get(3), facilities.get(10), facilities.get(11)},
                {facilities.get(0), facilities.get(10), facilities.get(11)},};
        Facility[][] installedFacilities = {{facilities.get(1)},
                {facilities.get(4), facilities.get(5)}, {facilities.get(8)},
                {facilities.get(9)}, {facilities.get(2), facilities.get(6)}, {},
                {facilities.get(11)}, {facilities.get(0)}};
        int[][] installCosts = {{3, 25, 30, 5}, {2, 3, 2}, {3, 5, 5}, {1, 2},
                {5, 2, 1, 10}, {14, 10, 2, 2}, {1, 2, 1}, {1, 2, 1}};
        int[][] removalCosts = {{4, 1, 25, 4}, {2, 1, 10}, {10, 2, 1}, {5, 12},
                {4, 1, 1, 3}, {7, 1, 3, 1}, {1, 3, 1}, {1, 1, 1}};
        List<Site> sites = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            HashSet<Facility> approved = new HashSet<>();
            HashMap<Facility, Integer> installCost = new HashMap<>();
            HashMap<Facility, Integer> removalCost = new HashMap<>();
            for (int j = 0; j < approvedFacilities[i].length; j++) {
                approved.add(approvedFacilities[i][j]);
                installCost.put(approvedFacilities[i][j], installCosts[i][j]);
                removalCost.put(approvedFacilities[i][j], removalCosts[i][j]);
            }
            sites.add(new Site(i, siteCapacities[i], approved, installCost, removalCost));
        }

        HashMap<Site, HashSet<Facility>> configuration = new HashMap<>();
        for (int i = 0; i < n; i++) {
            configuration.put(sites.get(i), new HashSet<>(Arrays.asList(installedFacilities[i])));
        }
        Facility newFacility = facilities.get(3);

        /* Run method on inputs and test result. */
        // the expected smallest cost of an installation sequence
        int expectedCost = 15;
        List<Action> actualSequence = SequenceFinder.findSequence(Configuration.copy(configuration),
                newFacility);
        // check that the installation sequence is valid
        assertTrue(Configuration.validInstallationSequence(configuration, newFacility,
                actualSequence));
        // check that the installation sequence has the expected cheapest cost
        int actualCost = Configuration.cost(actualSequence);
        assertEquals(expectedCost, actualCost);
    }

    @Test
    public void handoutTest2() {
        /* Initialise parameters to the test. */

        // the total number of facilities
        // (note that the total number of facilities installed, m, may be less)
        int allFacilities = 12;
        List<Facility> facilities = new ArrayList<>();
        for (int i = 0; i < allFacilities; i++) {
            facilities.add(new Facility(i));
        }
        // the number of sites
        int n = 8;
        int[] siteCapacities = {3, 2, 1, 1, 2, 2, 1, 1};
        Facility[][] approvedFacilities = {
                {facilities.get(1), facilities.get(2), facilities.get(3), facilities.get(9)},
                {facilities.get(4), facilities.get(5), facilities.get(6)},
                {facilities.get(4), facilities.get(5), facilities.get(8)},
                {facilities.get(6), facilities.get(9)},
                {facilities.get(2), facilities.get(3), facilities.get(4), facilities.get(6)},
                {facilities.get(2), facilities.get(4), facilities.get(7), facilities.get(8)},
                {facilities.get(3), facilities.get(10), facilities.get(11)},
                {facilities.get(0), facilities.get(10), facilities.get(11)},};
        Facility[][] installedFacilities = {{facilities.get(1)},
                {facilities.get(4), facilities.get(5)}, {facilities.get(8)},
                {facilities.get(9)}, {facilities.get(2), facilities.get(6)}, {},
                {facilities.get(11)}, {facilities.get(0)}};
        int[][] installCosts = {{3, 25, 30, 5}, {2, 3, 2}, {3, 5, 5}, {1, 2},
                {5, 2, 1, 10}, {14, 10, 2, 2}, {1, 2, 1}, {1, 2, 1}};
        int[][] removalCosts = {{4, 1, 25, 4}, {2, 1, 10}, {10, 2, 1}, {5, 12},
                {4, 1, 1, 3}, {7, 1, 3, 1}, {1, 3, 1}, {1, 1, 1}};
        List<Site> sites = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            HashSet<Facility> approved = new HashSet<>();
            HashMap<Facility, Integer> installCost = new HashMap<>();
            HashMap<Facility, Integer> removalCost = new HashMap<>();
            for (int j = 0; j < approvedFacilities[i].length; j++) {
                approved.add(approvedFacilities[i][j]);
                installCost.put(approvedFacilities[i][j], installCosts[i][j]);
                removalCost.put(approvedFacilities[i][j], removalCosts[i][j]);
            }
            sites.add(new Site(i, siteCapacities[i], approved, installCost, removalCost));
        }

        HashMap<Site, HashSet<Facility>> configuration = new HashMap<>();
        for (int i = 0; i < n; i++) {
            configuration.put(sites.get(i), new HashSet<>(Arrays.asList(installedFacilities[i])));
        }
        Facility newFacility = facilities.get(10);

        /* Run method on inputs and test result. */

        List<Action> actualSequence = SequenceFinder.findSequence(Configuration.copy(configuration),
                newFacility);
        // check that the actual installation sequence is null
        assertNull(actualSequence);
    }

    @Test
    public void handoutTest3() {
        /* Initialise parameters to the test. */

        // the total number of facilities
        // (note that the total number of facilities installed, m, may be less)
        int allFacilities = 12;
        List<Facility> facilities = new ArrayList<>();
        for (int i = 0; i < allFacilities; i++) {
            facilities.add(new Facility(i));
        }
        // the number of sites
        int n = 8;
        int[] siteCapacities = {3, 2, 1, 1, 2, 2, 1, 1};
        Facility[][] approvedFacilities = {
                {facilities.get(1), facilities.get(2), facilities.get(3), facilities.get(9)},
                {facilities.get(4), facilities.get(5), facilities.get(6)},
                {facilities.get(4), facilities.get(5), facilities.get(8)},
                {facilities.get(6), facilities.get(9)},
                {facilities.get(2), facilities.get(3), facilities.get(4), facilities.get(6)},
                {facilities.get(2), facilities.get(4), facilities.get(7), facilities.get(8)},
                {facilities.get(3), facilities.get(10), facilities.get(11)},
                {facilities.get(0), facilities.get(10), facilities.get(11)},};
        Facility[][] installedFacilities = {{facilities.get(1)},
                {facilities.get(4), facilities.get(5)}, {facilities.get(8)},
                {facilities.get(9)}, {facilities.get(2), facilities.get(6)}, {},
                {facilities.get(11)}, {facilities.get(0)}};
        int[][] installCosts = {{3, 25, 30, 5}, {2, 3, 2}, {3, 5, 5}, {1, 2},
                {5, 2, 1, 10}, {14, 10, 2, 2}, {1, 2, 1}, {1, 2, 1}};
        int[][] removalCosts = {{4, 1, 25, 4}, {2, 1, 10}, {10, 2, 1}, {5, 12},
                {4, 1, 1, 3}, {7, 1, 3, 1}, {1, 3, 1}, {1, 1, 1}};
        List<Site> sites = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            HashSet<Facility> approved = new HashSet<>();
            HashMap<Facility, Integer> installCost = new HashMap<>();
            HashMap<Facility, Integer> removalCost = new HashMap<>();
            for (int j = 0; j < approvedFacilities[i].length; j++) {
                approved.add(approvedFacilities[i][j]);
                installCost.put(approvedFacilities[i][j], installCosts[i][j]);
                removalCost.put(approvedFacilities[i][j], removalCosts[i][j]);
            }
            sites.add(new Site(i, siteCapacities[i], approved, installCost, removalCost));
        }

        HashMap<Site, HashSet<Facility>> configuration = new HashMap<>();
        for (int i = 0; i < n; i++) {
            configuration.put(sites.get(i), new HashSet<>(Arrays.asList(installedFacilities[i])));
        }
        Facility newFacility = facilities.get(7);

        /* Run method on inputs and test result. */

        // the expected smallest cost of an installation sequence
        int expectedCost = 2;
        List<Action> actualSequence = SequenceFinder.findSequence(Configuration.copy(configuration),
                newFacility);
        // check that the installation sequence is valid
        assertTrue(Configuration.validInstallationSequence(configuration, newFacility,
                actualSequence));
        // check that the installation sequence has the expected cheapest cost
        int actualCost = Configuration.cost(actualSequence);
        assertEquals(expectedCost, actualCost);
    }

}
