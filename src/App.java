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
        //Item[] items = {
           // new Item(2, 10),
           // new Item(3, 7),
           // new Item(4, 14),
           // new Item(5, 5),
           // new Item(6, 3)
       // };
        //int capacity = 10;

        File filePath = new File("..\\ProblemaMochilaGenetico\\Instancias\\KNAPDATA40.txt");
        
        // Lista para armazenar os itens
        Item[] items = null;

        // Capacidade da mochila
        int capacity = 0;

        // Tamanho da população
        int populationSize = 0;

        // Leitura do arquivo
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Lendo capacidade da mochila
            capacity = Integer.parseInt(br.readLine());

            // Lendo tamanho da população
            populationSize = Integer.parseInt(br.readLine());

            BufferedReader brItems = new BufferedReader(new FileReader(filePath));
            // Ignorando as duas primeiras linhas
            brItems.readLine();
            brItems.readLine();

            items = new Item[populationSize];
            for (int i = 0; i < populationSize; i++) {
                String line = brItems.readLine();
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter(",");
                scanner.next(); // Ignora o nome do item
                int weight = scanner.nextInt();
                int value = scanner.nextInt();
                items[i] = new Item(weight, value);
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parâmetros do algoritmo genético
       // int populationSize = 50;
        double crossoverRate = 0.8;
        double mutationRate = 0.1;
        int numGenerations = 500;

        // Chamada para a função que implementa o algoritmo genético
        int[] solution = genetic_algorithm_knapsack(items, capacity, populationSize, crossoverRate, mutationRate, numGenerations);

        // Imprimir a solução encontrada
        for(int i =0; i < solution.length; i++){
            System.out.print(solution[i] + ", ");
        }
        
       
    }

    // Função para implementar o algoritmo genético para o Problema da Mochila
    static int[] genetic_algorithm_knapsack(Item[] items, int capacity, int populationSize, double crossoverRate, double mutationRate, int numGenerations) {
        int[][] population = generate_initial_population(items.length, populationSize);
        
        Random rand = new Random();

        for (int gen = 0; gen < numGenerations; gen++) {
            int[][] nextGeneration = new int[populationSize][items.length];
            for (int i = 0; i < populationSize; i++) {
                int[] parent1 = tournament_selection(population, items, capacity);
                int[] parent2 = roulette_selection(population, items, capacity);
                int[] offspring = crossover(parent1, parent2, crossoverRate);
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

    // Função para gerar uma população inicial aleatória
    static int[][] generate_initial_population(int size, int populationSize) {
        int[][] population = new int[populationSize][size];
        Random rand = new Random();
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < size; j++) {
                population[i][j] = rand.nextInt(2); // 0 ou 1 (selecionado ou não selecionado)
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
        // Penalize soluções que excedam a capacidade da mochila
		// Implemente o método de penalização que achar mais adequado
        if (totalWeight > capacity) {
            totalValue = 0;
        }
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

    // Função para realizar a seleção por roleta
    static int[] roulette_selection(int[][] population, Item[] items, int capacity) {
        Random rand = new Random();
        int totalFitness = 0;
        int[] cumulativeFitness = new int[population.length];
        int selectedIdx = -1;

        // Calcula o fitness total e o fitness cumulativo
        for (int i = 0; i < population.length; i++) {
            int fitness = fitness_function(population[i], items, capacity);
            totalFitness += fitness;
            cumulativeFitness[i] = totalFitness;
        }

        // Gera um número aleatório dentro do fitness total
        int randomFitness = rand.nextInt(totalFitness);

        // Encontra o índice do indivíduo selecionado
        for (int i = 0; i < population.length; i++) {
            if (randomFitness < cumulativeFitness[i]) {
                selectedIdx = i;
                break;
            }
        }

        return population[selectedIdx];
    }

    // Função para realizar o cruzamento (crossover)
    static int[] crossover(int[] parent1, int[] parent2, double crossoverRate) {
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
        for (int i = 0; i < solution.length; i++) {
            if (rand.nextDouble() < mutationRate) {
                // Inverte o estado do gene (0 para 1 ou 1 para 0)
                solution[i] = (solution[i] == 0) ? 1 : 0;
            }
        }
    }
}
