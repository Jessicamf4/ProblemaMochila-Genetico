import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        File filePath = new File("..\\ProblemaMochilaGenetico\\Instancias\\KNAPDATA40.txt");

        // Lista para armazenar os itens
        Item[] items = null;

        // Capacidade da mochila
        int capacity = 0;

        // Tamanho da população
        int populationItens = 0;

        // Margem de porcentagem de itens de devem ser inclusos da mochila
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
            brItems.close();
            expectedWeightMargin = Math.round(capacity * populationItens / totalWeight);
            System.out.println(expectedWeightMargin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parâmetros do algoritmo genético
        int populationSize = 50;
        double crossoverRate = 0.8;
        double mutationRate = 0.2;
        int numGenerations = 300;

        // Chamada para a função que implementa o algoritmo genético
        GerarPopulacao geneticAlgorithm = new GerarPopulacao();
        int[] solution = geneticAlgorithm.genetic_algorithm_knapsack(items, capacity, populationSize, crossoverRate,
                mutationRate, numGenerations, expectedWeightMargin);

        // Imprimir a solução encontrada
        Fitness fitness = new Fitness();
        int totalValue = 0;
        int totalWeight = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                totalValue += items[i].value;
                totalWeight += items[i].weight;
            }
        }
        System.out.println("Fitness " + fitness.fitness_function(solution, items, capacity));
        System.out.println("Valor total " + totalValue);
        System.out.println("Peso total " + totalWeight);
        System.out.println("Peso esperado " + capacity);

    }
}
