public class Fitness {
    public int fitness_function(int[] solution, Item[] items, int capacity) {
        int totalValue = 0;
        int totalWeight = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                totalValue += items[i].value;
                totalWeight += items[i].weight;
            }
        }

        if (totalWeight > capacity) {
            totalValue = 0;
        }
        // System.out.println(totalValue);
        return totalValue;
    }
}
