package assignment1;

import java.util.*;

public class SequenceFinder {

    /**
     * COMP4500 Assignment 1 – Part B
     * Muhammad Fiqo Anugrah – 48298975
     *
     * Note:
     * - The plan must always finish with a DirectInstall of the new facility.
     * - If there’s already some site that approves the facility and has space,
     *   then we can just install it straight away.
     * - Otherwise I need to “make space” by relocating other facilities.
     *   I picture this as the empty slot moving around between sites whenever I relocate.
     * - Since every move has a positive cost, Dijkstra is a natural fit to find
     *   the cheapest way of creating space at a site that approves the new facility.
     */
    public static List<Action> findSequence(HashMap<Site, HashSet<Facility>> configuration,
                                            Facility newFacility) {

        // Step 1: collect the list of sites and give them indices for arrays
        List<Site> sites = new ArrayList<>(configuration.keySet());
        int n = sites.size();
        Map<Site, Integer> siteIndex = new HashMap<>();
        for (int i = 0; i < n; i++) siteIndex.put(sites.get(i), i);

        // Step 2: Dijkstra setup
        // dist[i] = min cost to make site i “empty” (has at least one spare slot)
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);

        // For reconstructing the path later:
        int[] parent = new int[n];              // which site passed the empty slot here
        Facility[] movedFacility = new Facility[n]; // which facility was moved along that step
        Arrays.fill(parent, -1);

        // Priority queue holds (cost so far, siteIndex)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        // Step 3: initialise base cases
        // Any site that already has spare capacity starts at cost 0
        for (int i = 0; i < n; i++) {
            if (configuration.get(sites.get(i)).size() < sites.get(i).capacity()) {
                dist[i] = 0;
                pq.offer(new int[]{0, i});
            }
        }

        // Step 4: main Dijkstra loop
        // Pop the cheapest site that currently has the empty slot
        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int cost = curr[0];
            int emptyIdx = curr[1];

            // Skip if we already found a cheaper way for this site
            if (cost > dist[emptyIdx]) continue;

            Site emptySite = sites.get(emptyIdx);

            // Try to pull facilities from other sites into this empty site
            for (int fullIdx = 0; fullIdx < n; fullIdx++) {
                if (fullIdx == emptyIdx) continue;

                Site fullSite = sites.get(fullIdx);

                for (Facility f : configuration.get(fullSite)) {
                    // Only possible if emptySite approves the facility
                    if (!emptySite.approved(f)) continue;

                    int moveCost = fullSite.removalCost(f) + emptySite.installationCost(f);
                    int newCost = cost + moveCost;

                    // If this is cheaper, update and push new state
                    if (newCost < dist[fullIdx]) {
                        dist[fullIdx] = newCost;
                        parent[fullIdx] = emptyIdx;
                        movedFacility[fullIdx] = f;
                        pq.offer(new int[]{newCost, fullIdx});
                    }
                }
            }
        }

        // Step 5: pick the best target site to install newFacility
        int bestTarget = -1, bestTotal = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            Site site = sites.get(i);
            if (!site.approved(newFacility)) continue;
            if (dist[i] == Integer.MAX_VALUE) continue;
            int total = dist[i] + site.installationCost(newFacility);
            if (total < bestTotal) {
                bestTotal = total;
                bestTarget = i;
            }
        }

        // If nothing works, return null
        if (bestTarget == -1) return null;

        // Step 6: reconstruct the sequence of actions
        return reconstructActionSequence(sites, parent, movedFacility, bestTarget, newFacility);
    }

    /**
     * Reconstruct the list of actions from the arrays built during Dijkstra.
     * I trace the path backwards from the target, then reverse it,
     * so I know exactly which relocations happened and in what order.
     * Finally I add the DirectInstall of the new facility.
     */
    private static List<Action> reconstructActionSequence(List<Site> sites,
                                                          int[] parent,
                                                          Facility[] movedFacility,
                                                          int targetIndex,
                                                          Facility newFacility) {
        List<Integer> pathIndices = new ArrayList<>();
        List<Facility> pathFacilities = new ArrayList<>();

        int curr = targetIndex;
        while (parent[curr] != -1) {
            pathIndices.add(curr);
            pathFacilities.add(movedFacility[curr]);
            curr = parent[curr];
        }
        pathIndices.add(curr); // add the initially empty site

        Collections.reverse(pathIndices);
        Collections.reverse(pathFacilities);

        List<Action> actions = new ArrayList<>();

        // Add relocations along the path
        for (int i = 1; i < pathIndices.size(); i++) {
            Site from = sites.get(pathIndices.get(i));
            Site to = sites.get(pathIndices.get(i - 1));
            Facility f = pathFacilities.get(i - 1);
            actions.add(new Action.RelocateAction(f, from, to));
        }

        // Finally, install the new facility
        Site finalSite = sites.get(targetIndex);
        actions.add(new Action.DirectInstallAction(newFacility, finalSite));

        return actions;
    }
}