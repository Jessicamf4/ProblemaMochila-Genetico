import java.util.Random;

public class GerarPopulacao {

    private MetodosGeneticos metodosG = new MetodosGeneticos();
    public MetodosSelecao metodosS = new MetodosSelecao();
    private Fitness fitnessf = new Fitness();

    public int[] genetic_algorithm_knapsack(Item[] items, int capacity, int populationSize, double crossoverRate,
            double mutationRate, int numGenerations, int expectedWeightMargin) {

        int[][] population = generate_initial_population(items.length, populationSize, expectedWeightMargin);

        Random rand = new Random();

        for (int gen = 0; gen < numGenerations; gen++) {
            // System.out.println("GERAÇÃO " + gen);
            int[][] nextGeneration = new int[populationSize][items.length];
            for (int i = 0; i < populationSize; i++) {
                int[] parent1 = metodosS.tournament_selection(population, items, capacity);
                int[] parent2 = metodosS.tournament_selection(population, items, capacity);
                int[] offspring = metodosG.crossover(parent1, parent2, crossoverRate, items, capacity);
                metodosG.mutate(offspring, mutationRate);
                nextGeneration[i] = offspring;
            }
            population = nextGeneration;
        }
            // Encontrar a melhor solução na última geração
            int[] bestSolution = population[0];
            int bestFitness = fitnessf.fitness_function(population[0], items, capacity);

            for (int i = 1; i < populationSize; i++) {
                int fitness = fitnessf.fitness_function(population[i], items, capacity);
                if (fitness > bestFitness) {
                    bestSolution = population[i];
                    bestFitness = fitness;
                }
            }

            return bestSolution;
        
    }

    public int[][] generate_initial_population(int size, int populationSize, int expectedWeightMargin) {
        int[][] population = new int[populationSize][size];
        Random rand = new Random();
        int margemInclusaoItens = 100 - expectedWeightMargin + 2;
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < size; j++) {
                if (rand.nextInt(100) > (margemInclusaoItens)) {
                    population[i][j] = rand.nextInt(2);
                } else {
                    population[i][j] = 0;
                }
            }
        }
        return population;
    }
}
