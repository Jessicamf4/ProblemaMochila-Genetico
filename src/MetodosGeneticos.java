import java.util.Random;

public class MetodosGeneticos {
    private Fitness fitness = new Fitness(); 

    public int[] crossover(int[] parent1, int[] parent2, double crossoverRate, Item[] items, int capacity) {
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
            int pesoFilho = fitness.fitness_function(offspring, items, capacity);
            int pesoPai1 = fitness.fitness_function(parent1, items, capacity);
            int pesoPai2 = fitness.fitness_function(parent2, items, capacity);

            if (pesoFilho > pesoPai1 && pesoFilho > pesoPai2) {
                offspring = parent1;
            } else if (pesoPai1 > pesoFilho && pesoPai1 > pesoPai2) {
                offspring = parent1;
            } else if (pesoPai2 > pesoFilho && pesoPai2 > pesoPai1) {
                offspring = parent2;
            }

        } else {
            // Se não ocorrer cruzamento, o filho é uma cópia de um dos pais aleatoriamente
            offspring = rand.nextBoolean() ? parent1 : parent2;
        }

        return offspring;
    }

    public void mutate(int[] solution, double mutationRate) {
        Random rand = new Random();

        // Aplica a mutação em cada gene com base na taxa de mutação
        int geneMutado = rand.nextInt(solution.length);
        if (rand.nextDouble() < mutationRate) {
            // Inverte o estado do gene (0 para 1 ou 1 para 0)
            solution[geneMutado] = (solution[geneMutado] == 0) ? 1 : 0;
        }
    }
}
