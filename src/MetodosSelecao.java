import java.util.Random;

public class MetodosSelecao {

    private Fitness fitnessf = new Fitness(); 

    public int[] roulette_selection(int[][] population, Item[] items, int capacity) {
        Random rand = new Random();
        double totalFitness = 0;
        double[] fitnessProbabilities = new double[population.length];

        // Calcula o fitness total da população
        for (int i = 0; i < population.length; i++) {
            int fitness = fitnessf.fitness_function(population[i], items, capacity);
            totalFitness += fitness;
        }

        // Calcula as probabilidades de seleção para cada indivíduo
        for (int i = 0; i < population.length; i++) {
            int fitness = fitnessf.fitness_function(population[i], items, capacity);
            fitnessProbabilities[i] = (double) fitness / totalFitness;
        }

        // Seleciona um pai com base nas probabilidades de fitness
        double randomNum = rand.nextDouble();
        double cumulativeProbability = 0;
        for (int i = 0; i < population.length; i++) {
            cumulativeProbability += fitnessProbabilities[i];
            if (randomNum <= cumulativeProbability) {
                return population[i];
            }
        }

        // Caso algo dê errado, retorna um indivíduo aleatório
        return population[rand.nextInt(population.length)];
    }

    public int[] tournament_selection(int[][] population, Item[] items, int capacity) {
        Random rand = new Random();
        int tournamentSize = 5; // Tamanho do torneio
        int[] bestSolution = null;
        int bestFitness = Integer.MIN_VALUE;

        for (int i = 0; i < tournamentSize; i++) {
            int[] solution = population[rand.nextInt(population.length)];
            int fitness = fitnessf.fitness_function(solution, items, capacity);
            if (fitness > bestFitness) {
                bestSolution = solution;
                bestFitness = fitness;
            }
        }

        return bestSolution;
    }
}
