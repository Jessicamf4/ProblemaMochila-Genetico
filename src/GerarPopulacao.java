import java.util.Random;

import javax.swing.JFrame;

public class GerarPopulacao {

    private MetodosGeneticos metodosG = new MetodosGeneticos();
    public MetodosSelecao metodosS = new MetodosSelecao();
    private Fitness fitnessf = new Fitness();
    // private GraficoEvolucao Gafico = new GraficoEvolucao();

    public int[] genetic_algorithm_knapsack(Item[] items, int capacity, int populationSize, double crossoverRate,
            double mutationRate, int numGenerations, int expectedWeightMargin) {

        int[][] population = generate_initial_population(items.length, populationSize, expectedWeightMargin);

        int[][] bestSolutionGeneration = new int[numGenerations][items.length];
        int[] bestFitnessGeneration = new int[numGenerations];
        int[] geracao = new int[numGenerations];

        for (int gen = 0; gen < numGenerations; gen++) {

            int[][] nextGeneration = new int[populationSize][items.length];

            for (int i = 0; i < populationSize; i++) {
                int[] parent1 = metodosS.roulette_selection(population, items, capacity);
                int[] parent2 = metodosS.roulette_selection(population, items, capacity);
                int[] offspring = metodosG.crossover(parent1, parent2, crossoverRate, items, capacity);
                metodosG.mutate(offspring, mutationRate);
                nextGeneration[i] = offspring;
            }
            population = nextGeneration;

            bestSolutionGeneration[gen] = findBestSolution(population, items, capacity, populationSize);
            int bestFitness = fitnessf.fitness_function(bestSolutionGeneration[gen], items, capacity);

            bestFitnessGeneration[gen] = bestFitness;
            System.out.println("Geração " + gen + ", Melhor Fitness: " + bestFitness);
            geracao[gen] = gen;
        }

        GraficoEvolucao grafico = new GraficoEvolucao("Evolução da Melhor Fitness");
        grafico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        grafico.pack();
        grafico.setVisible(true);
        for (int gen = 0; gen < numGenerations; gen++) {
            grafico.addDataPoint(geracao[gen], bestFitnessGeneration[gen]);
        }
        grafico.repaint();

        return findBestSolution(population, items, capacity, populationSize);

    }

    public int[] findBestSolution(int[][] population, Item[] items, int capacity, int populationSize) {
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
