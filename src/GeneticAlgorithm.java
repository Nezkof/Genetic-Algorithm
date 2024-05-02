import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final int populationSize;
    private final int iterationNumber;
    private final double crossoverRate;
    private final double mutationRate;
    private final double selectionRate;
    private final List<String[]> population;
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

    private List<String[]> generatePopulation(double[] bounds){
        List<String[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; ++i)
            population.add(new String[]{
                    encode(bounds[0] + random.nextDouble() * (bounds[1] - bounds[0])),
                    encode(bounds[0] + random.nextDouble() * (bounds[1] - bounds[0]))
            });

        return population;
    }

    private String encode(double value) {
        long longBits = Double.doubleToLongBits(value);
        return Long.toBinaryString(longBits);
    }

    private double decode(String value) {
        long longBits = Long.parseUnsignedLong(value, 2);
        return Double.longBitsToDouble(longBits);
    }

    public void startEvolution() {
        long startTime = System.currentTimeMillis();
        double bestScore = Double.MAX_VALUE;

        for (int gen = 0; gen < iterationNumber; ++gen) {
            List<Double> scores = new ArrayList<>();
            for (String[] individual : population) {
                double x = decode(individual[0]);
                double y = decode(individual[1]);
                double score = fitnessFunction.calculate(x, y);
                scores.add(score);

                if (score < bestScore) {
                    bestScore = score;
                }
            }

            List<String[]> children = new ArrayList<>();

            for (int i = 0; i < populationSize; i += 2) {
                String[][] offspring;
                if (random.nextDouble() < selectionRate) {
                    offspring = crossover(tournamentSelection(scores), tournamentSelection(scores));
                    mutate(offspring[0]);
                    mutate(offspring[1]);
                } else {
                    int randomIndex1 = random.nextInt(population.size());
                    int randomIndex2 = random.nextInt(population.size());

                    while (randomIndex1 == randomIndex2)
                        randomIndex2 = random.nextInt(population.size());

                    offspring = new String[][]{population.get(randomIndex1), population.get(randomIndex2)};
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

    private String[][] crossover(String[] parent1, String[] parent2) {
        String[][] offspring = new String[2][2];
        if (random.nextDouble() < crossoverRate) {
            int crossoverPoint = random.nextInt(parent1.length);
            offspring[0][0] = parent1[0].substring(0, crossoverPoint) + parent2[0].substring(crossoverPoint);
            offspring[1][0] = parent2[0].substring(0, crossoverPoint) + parent1[0].substring(crossoverPoint);
            offspring[0][1] = parent1[1].substring(0, crossoverPoint) + parent2[1].substring(crossoverPoint);
            offspring[1][1] = parent2[1].substring(0, crossoverPoint) + parent1[1].substring(crossoverPoint);
        } else {
            offspring[0] = parent1;
            offspring[1] = parent2;
        }
        return offspring;
    }

    private String[] tournamentSelection(List<Double> scores) {
        //обираємо 3 особи, серед них найкращу
        String[] bestIndividual = population.get(0);
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

    private String[] randomSelection() {
        return population.get(random.nextInt(population.size()));
    }

    private String[] proportionalSelection(List<Double> scores) {
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

    private void mutate(String[] individual) {
        for (int i = 0; i < individual.length; i++)
            if (random.nextDouble() < mutationRate) {
                int index = random.nextInt(individual.length);
                char[] chars = individual[index].toCharArray();
                chars[random.nextInt(chars.length)] = (chars[random.nextInt(chars.length)] == '0') ? '1' : '0';
                individual[index] = String.valueOf(chars);
            }

    }

    public long getEvolutionDuration() { return evolutionDuration;}
    public double getBestScore() {return bestScore;}
}