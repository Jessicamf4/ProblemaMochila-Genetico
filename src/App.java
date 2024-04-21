import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class App {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
       
        File filePath = new File("..\\ProblemaMochilaGenetico\\Instancias\\KNAPDATA100000.txt");
        
        // Lista para armazenar os itens
        Item[] items = null;

        // Capacidade da mochila
        int capacity = 0;

        // Tamanho da população
        int populationItens = 0;

        //Margem de porcentagem de itens de devem ser inclusos da mochila
        int expectedWeightMargin = 0;
        // Leitura do arquivo
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Lendo capacidade da mochila
            capacity = Integer.parseInt(br.readLine());

            // Lendo tamanho da população
            populationItens = Integer.parseInt(br.readLine());
            
            BufferedReader brItems = new BufferedReader(new FileReader(filePath));
            // Ignorando as duas primeiras linhas
            brItems.readLine();
            brItems.readLine();
            int totalWeight = 0;
            items = new Item[populationItens];
            for (int i = 0; i < populationItens; i++) {
                String line = brItems.readLine();
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter(",");
                scanner.next(); // Ignora o nome do item
                int weight = scanner.nextInt();
                int value = scanner.nextInt();
                items[i] = new Item(weight, value);
                scanner.close();
                totalWeight += weight;
            }
            expectedWeightMargin = Math.round(capacity*populationItens/totalWeight);
            System.out.println(expectedWeightMargin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parâmetros do algoritmo genético
        int populationSize = 50;
        double crossoverRate = 0.8;
        double mutationRate = 0.1;
        int numGenerations = 100;

        // Chamada para a função que implementa o algoritmo genético
        int[] solution = genetic_algorithm_knapsack(items, capacity, populationSize, crossoverRate, mutationRate, numGenerations, expectedWeightMargin);

        // Imprimir a solução encontrada

        int totalValue = 0;
        int totalWeight = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                totalValue += items[i].value;
                totalWeight += items[i].weight;
            } 
        }
        System.out.println("Fitness " + fitness_function(solution, items, capacity)); 
        System.out.println("Valor total " + totalValue); 
        System.out.println("Peso total " + totalWeight); 
        System.out.println("Peso esperado " + capacity);

    }

    // Função para implementar o algoritmo genético para o Problema da Mochila
    static int[] genetic_algorithm_knapsack(Item[] items, int capacity, int populationSize, double crossoverRate, double mutationRate, int numGenerations, int expectedWeightMargin) {
        
        int[][] population = generate_initial_population(items.length, populationSize, expectedWeightMargin);
        
        Random rand = new Random();

        for (int gen = 0; gen < numGenerations; gen++) {
            //System.out.println("GERAÇÃO " + gen);
            int[][] nextGeneration = new int[populationSize][items.length];
            for (int i = 0; i < populationSize; i++) {
                int[] parent1 = roulette_selection(population, items, capacity);
                int[] parent2 = roulette_selection(population, items, capacity);
                int[] offspring = crossover(parent1, parent2, crossoverRate, items, capacity);
               mutate(offspring, mutationRate);
                nextGeneration[i] = offspring;
            }
            population = nextGeneration;
            
        }

        // Encontrar a melhor solução na última geração
        int[] bestSolution = population[0];
        int bestFitness = fitness_function(population[0], items, capacity);
        for (int i = 1; i < populationSize; i++) {
            int fitness = fitness_function(population[i], items, capacity);
            if (fitness > bestFitness) {
                bestSolution = population[i];
                bestFitness = fitness;
            }
        }

        return bestSolution;
    }

    static int[][] generate_initial_population(int size, int populationSize, int expectedWeightMargin) {
        int[][] population = new int[populationSize][size];
        Random rand = new Random();
        int margemInclusaoItens = 100-expectedWeightMargin +2;
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < size; j++) {
                if( rand.nextInt(100) > (margemInclusaoItens)){
                    population[i][j] = rand.nextInt(2); 
                } else{
                    population[i][j] = 0;
                }
                
            }
        }
        return population;
        
        
    }

    // Função de fitness para calcular o valor total da mochila
    static int fitness_function(int[] solution, Item[] items, int capacity) {
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
        //System.out.println(totalValue);
        return totalValue;
    }

    // Função para realizar a seleção por torneio
    static int[] tournament_selection(int[][] population, Item[] items, int capacity) {
        Random rand = new Random();
        int tournamentSize = 5; // Tamanho do torneio
        int[] bestSolution = null;
        int bestFitness = Integer.MIN_VALUE;

        for (int i = 0; i < tournamentSize; i++) {
            int[] solution = population[rand.nextInt(population.length)];
            int fitness = fitness_function(solution, items, capacity);
            if (fitness > bestFitness) {
                bestSolution = solution;
                bestFitness = fitness;
            }
        }

        return bestSolution;
    }

    static Random rand = new Random();

    // Função para realizar a seleção por roleta
static int[] roulette_selection(int[][] population, Item[] items, int capacity) {
    Random rand = new Random();
    double totalFitness = 0;
    double[] fitnessProbabilities = new double[population.length];
    
    // Calcula o fitness total da população
    for (int i = 0; i < population.length; i++) {
        int fitness = fitness_function(population[i], items, capacity);
        totalFitness += fitness;
    }

    // Calcula as probabilidades de seleção para cada indivíduo
    for (int i = 0; i < population.length; i++) {
        int fitness = fitness_function(population[i], items, capacity);
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

    
    // Função para realizar o cruzamento (crossover)
    static int[] crossover(int[] parent1, int[] parent2, double crossoverRate, Item[] items, int capacity) {
        Random rand = new Random();
        int[] offspring = new int[parent1.length];
        
        // Verifica se o cruzamento deve ser realizado com base na taxa de cruzamento
        if (rand.nextDouble() < crossoverRate) {
            int crossoverPoint = rand.nextInt(parent1.length);
            
            // Gera os filhos trocando os genes entre os pais nos pontos de corte
            for (int i = 0; i < parent1.length; i++) {
                if (i < crossoverPoint) {
                    offspring[i] = parent1[i];
                } else {
                    offspring[i] = parent2[i];
                }
            }
            int pesoFilho = fitness_function(offspring, items, capacity);
            int pesoPai1 = fitness_function(parent1, items, capacity);
            int pesoPai2 = fitness_function(parent2, items, capacity);

            if(pesoFilho>pesoPai1 && pesoFilho>pesoPai2){
                offspring = parent1;
            }else if(pesoPai1>pesoFilho && pesoPai1>pesoPai2){
                offspring = parent1;
            }else if(pesoPai2>pesoFilho && pesoPai2>pesoPai1){
                offspring = parent2;
            }

        } else {
            // Se não ocorrer cruzamento, o filho é uma cópia de um dos pais aleatoriamente
            offspring = rand.nextBoolean() ? parent1 : parent2;
        }
        
        return offspring;
    }
    

    // Função para realizar a mutação
    static void mutate(int[] solution, double mutationRate) {
        Random rand = new Random();
        
        // Aplica a mutação em cada gene com base na taxa de mutação
        int geneMutado = rand.nextInt(solution.length);
            if (rand.nextDouble() < mutationRate) {
                // Inverte o estado do gene (0 para 1 ou 1 para 0)
                solution[geneMutado] = (solution[geneMutado] == 0) ? 1 : 0;
            }
    }
}
