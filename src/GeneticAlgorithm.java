import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final int populationSize;
    private final int iterationNumber;
    private final double crossoverRate;
    private final double mutationRate;
    private final double selectionRate;
    private final List<double[]> population;
    private final FitnessFunction fitnessFunction;
    private final Random random;
    private long evolutionDuration;
    private double bestScore;

    public GeneticAlgorithm(int populationSize, int iterationNumber,  double crossoverRate, double mutationRate, double selectionRate, double[] bounds, FitnessFunction fitnessFunction) {
        this.random = new Random();
        this.populationSize = populationSize;
        this.iterationNumber = iterationNumber;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.selectionRate = selectionRate;
        this.population = generatePopulation(bounds);
        this.fitnessFunction = fitnessFunction;
    }

    private List<double[]> generatePopulation(double[] bounds){
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; ++i)
            population.add(new double[] {bounds[0] + random.nextDouble() * (bounds[1] - bounds[0]), bounds[0] + random.nextDouble() * (bounds[1] - bounds[0])});

        return population;
    }

    public void startEvolution() {
        long startTime = System.currentTimeMillis();
        double bestScore = Double.MAX_VALUE;

        for (int gen = 0; gen < iterationNumber; ++gen) {
            List<Double> scores = new ArrayList<>();
            for (double[] individual : population) {
                double score = fitnessFunction.calculate(individual[0], individual[1]);
                scores.add(score);

                if (score < bestScore) {
                    bestScore = score;
                }
            }

            // Обрання батьків і виконання кросоверу та мутації
            List<double[]> children = new ArrayList<>();

            for (int i = 0; i < populationSize; i += 2) {
                double[][] offspring;
                if (random.nextDouble() < selectionRate) { // Ймовірність відбору нащадків
                    //offspring = crossover(tournamentSelection(population, scores), tournamentSelection(population, scores));
                    //offspring = crossover(randomSelection(), randomSelection());
                    offspring = crossover(tournamentSelection(population, scores), tournamentSelection(population, scores));
                    mutate(offspring[0]);
                    mutate(offspring[1]);
                } else {
                    int randomIndex1 = random.nextInt(population.size());
                    int randomIndex2 = random.nextInt(population.size());

                    while (randomIndex1 == randomIndex2)
                        randomIndex2 = random.nextInt(population.size());

                    offspring = new double[][]{population.get(randomIndex1), population.get(randomIndex2)};
                }
                children.add(offspring[0]);
                children.add(offspring[1]);
            }

            population.clear();
            population.addAll(children);

            this.bestScore = bestScore;
        }

        evolutionDuration = System.currentTimeMillis() - startTime;
    }


    private double[][] crossover(double[] parent1, double[] parent2) {
        double[][] offspring = new double[2][2];
        if (random.nextDouble() < crossoverRate) {
            int crossoverPoint = random.nextInt(parent1.length);
            offspring[0] = concatenateArrays(Arrays.copyOfRange(parent1, 0, crossoverPoint), Arrays.copyOfRange(parent2, crossoverPoint, parent2.length));
            offspring[1] = concatenateArrays(Arrays.copyOfRange(parent2, 0, crossoverPoint), Arrays.copyOfRange(parent1, crossoverPoint, parent1.length));
        } else {
            offspring[0] = parent1;
            offspring[1] = parent2;
        }
        return offspring;
    }

    private double[] concatenateArrays(double[] a, double[] b) {
        double[] result = new double[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private double[] tournamentSelection(List<double[]> population, List<Double> scores) { //обираємо 3 особи, серед них найкращу
        double[] bestIndividual = null;
        double bestScore = Double.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(population.size());
            double score = scores.get(randomIndex);

            if (score < bestScore) {
                bestIndividual = population.get(randomIndex);
                bestScore = score;
            }
        }
        return bestIndividual;
    }

    private double[] randomSelection() {
        return population.get(random.nextInt(population.size()));
    }

    private double[] proportionalSelection(List<double[]> population, List<Double> scores) {
        // Сума всіх оцінок у популяції
        double totalScore = 0;
        for (double score : scores) {
            totalScore += score;
        }

        // Обираємо випадкове значення для визначення вибору особини
        double randomValue = random.nextDouble() * totalScore;

        // Ідемо по популяції і шукаємо особину, сума оцінок якої перевищує випадкове значення
        double cumulativeScore = 0;
        for (int i = 0; i < population.size(); i++) {
            cumulativeScore += scores.get(i);
            if (cumulativeScore >= randomValue) {
                return population.get(i);
            }
        }

        // Якщо не вдалося знайти, повертаємо останню особину в популяції
        return population.get(population.size() - 1);
    }

    private void mutate(double[] individual) {
        for (int i = 0; i < individual.length; i++)
            if (random.nextDouble() < mutationRate)
                individual[i] = individual[i] + random.nextGaussian();
    }

    public long getEvolutionDuration() { return evolutionDuration;}
    public double getBestScore() {return bestScore;}
}
