package assignment1;

import java.util.*;

public class SequenceFinder {

    /**
     *  COMP4500 Assignment 1 - Part B
     *  Muhammad Fiqo Anugrah - 48298975
     */

    public static List<Action> findSequence(HashMap<Site, HashSet<Facility>> configuration,
                                            Facility newFacility) {

        // - The sequence must always end with a DirectInstall of the new facility.
        // - If there is already a site that approves the facility and has free space,
        //   then that’s the answer straight away
        // - If not, then I have to make space at some approved site.
        //   I think of the empty slot like it “moves” between sites every time
        //   I relocate a facility
        // - Since all costs are positive, Dijkstra is the natural way to find
        //   the cheapest path that brings the empty slot to a valid target.

        List<Site> sites = new ArrayList<>(configuration.keySet());
        int n = sites.size();
        Map<Site, Integer> indexOf = new HashMap<>();
        for (int i = 0; i < n; i++) indexOf.put(sites.get(i), i);

        // First thing I do: mark which sites already have spare capacity
        boolean[] hasSpare = new boolean[n];
        for (int i = 0; i < n; i++) {
            Site s = sites.get(i);
            hasSpare[i] = configuration.get(s).size() < s.capacity();
        }

        // Case 1: simple case
        // If some approved site has space already, just install there.
        Site bestDirectSite = null;
        int bestDirectCost = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            Site s = sites.get(i);
            if (s.approved(newFacility) && hasSpare[i]) {
                int c = s.installationCost(newFacility);
                if (c < bestDirectCost) {
                    bestDirectCost = c;
                    bestDirectSite = s;
                }
            }
        }
        if (bestDirectSite != null) {
            return List.of(new Action.DirectInstallAction(newFacility, bestDirectSite));
        }

        // Case 2: harder case, every approved site is full.
        // I model this as a graph:
        // - Each site is a node.
        // - An edge u->v means: I can move some facility y from v into u
        //   (only if u approves y).
        //   The cost is removalCost(v,y) + installationCost(u,y).
        //   After this move, site v becomes the new empty site
        //   (so the empty slot travels from u to v).
        // By running Dijkstra from all initially empty sites,
        // I get the minimum cost to make any other site empty.

        final int INF = Integer.MAX_VALUE / 4;
        int[] minCost = new int[n];
        Arrays.fill(minCost, INF);

        Site[] parentSite = new Site[n];            // how the empty slot reached this site
        Facility[] movedFromHere = new Facility[n]; // which facility was moved out

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        for (int i = 0; i < n; i++) {
            if (hasSpare[i]) {
                minCost[i] = 0;
                pq.add(new int[]{0, i});
            }
        }

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], ui = cur[1];
            if (d != minCost[ui]) continue;
            Site u = sites.get(ui);

            // Try pulling a facility from each other site v into u
            for (int vi = 0; vi < n; vi++) {
                if (vi == ui) continue;
                Site v = sites.get(vi);

                // I look for the cheapest facility y in v that u can accept
                Facility bestY = null;
                int bestW = INF;
                for (Facility y : configuration.get(v)) {
                    if (u.approved(y)) {
                        int w = v.removalCost(y) + u.installationCost(y);
                        if (w < bestW) {
                            bestW = w;
                            bestY = y;
                        }
                    }
                }
                if (bestY == null) continue;

                int nd = d + bestW;
                if (nd < minCost[vi]) {
                    minCost[vi] = nd;
                    parentSite[vi] = u;          // v becomes empty after moving y into u
                    movedFromHere[vi] = bestY;   // remember which facility I moved
                    pq.add(new int[]{nd, vi});
                }
            }
        }

        // Now I choose the target site:
        // Among sites that approve the new facility,
        // I take the one with the smallest (cost to empty it + install cost).
        int bestTargetIdx = -1;
        int bestTotal = INF;
        for (int ti = 0; ti < n; ti++) {
            Site t = sites.get(ti);
            if (!t.approved(newFacility)) continue;
            if (minCost[ti] == INF) continue;
            int total = minCost[ti] + t.installationCost(newFacility);
            if (total < bestTotal) {
                bestTotal = total;
                bestTargetIdx = ti;
            }
        }
        if (bestTargetIdx == -1) {
            return null; // I cannot create a slot at any valid site
        }

        // Reconstruct how the empty slot traveled
        List<Site> path = new ArrayList<>();
        List<Facility> moved = new ArrayList<>();
        int curIdx = bestTargetIdx;
        while (true) {
            Site curSite = sites.get(curIdx);
            path.add(curSite);
            Site p = parentSite[curIdx];
            if (p == null) break;  // reached an initial empty site
            moved.add(movedFromHere[curIdx]);
            curIdx = indexOf.get(p);
        }
        Collections.reverse(path);
        Collections.reverse(moved);

        // Translate that path into actions:
        // For each hop, relocate the recorded facility,
        // and at the end directly install the new facility.
        List<Action> sequence = new ArrayList<>();
        for (int i = 1; i < path.size(); i++) {
            Site src = path.get(i);
            Site dst = path.get(i - 1);
            Facility y = moved.get(i - 1);
            sequence.add(new Action.RelocateAction(y, src, dst));
        }
        Site target = path.get(path.size() - 1);
        sequence.add(new Action.DirectInstallAction(newFacility, target));
        return sequence;
    }
}