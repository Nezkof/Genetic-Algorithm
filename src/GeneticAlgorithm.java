import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final int sizeOfPopulation;
    private final int numIterations;
    private final double crossoverRatio;
    private final double mutationRatio;
    private final double selectionRatio;
    private final List<String[]> population;
    private final FitnessFunction fitnessFunction;
    private final Random random;
    private long evolutionDuration;
    private double optimalScore;

    public GeneticAlgorithm(int sizeOfPopulation, int numIterations, double crossoverRatio, double mutationRatio, double selectionRatio, double[] bounds, FitnessFunction fitnessFunction) {
        this.random = new Random();
        this.sizeOfPopulation = sizeOfPopulation;
        this.numIterations = numIterations;
        this.crossoverRatio = crossoverRatio;
        this.mutationRatio = mutationRatio;
        this.selectionRatio = selectionRatio;
        this.population = createInitialPopulation(bounds);
        this.fitnessFunction = fitnessFunction;
    }

    private List<String[]> createInitialPopulation(double[] bounds) {
        List<String[]> population = new ArrayList<>();
        for (int i = 0; i < sizeOfPopulation; ++i)
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
        double optimalScore = Double.MAX_VALUE;

        for (int gen = 0; gen < numIterations; ++gen) {
            List<Double> scores = new ArrayList<>();
            for (String[] individual : population) {
                double x = decode(individual[0]);
                double y = decode(individual[1]);
                double score = fitnessFunction.calculate(x, y);
                scores.add(score);

                if (score < optimalScore) {
                    optimalScore = score;
                }
            }

            List<String[]> children = new ArrayList<>();

            for (int i = 0; i < sizeOfPopulation; i += 2) {
                String[][] offspring;
                if (random.nextDouble() < selectionRatio) {
                    offspring = crossover(tournamentSelection(scores), tournamentSelection(scores));
                    mutate(offspring[0]);
                    mutate(offspring[1]);
                } else {
                    int firstIndividIndex = random.nextInt(population.size());
                    int secondIndividIndex = random.nextInt(population.size());

                    while (firstIndividIndex == secondIndividIndex)
                        secondIndividIndex = random.nextInt(population.size());

                    offspring = new String[][]{population.get(firstIndividIndex), population.get(secondIndividIndex)};
                }
                children.add(offspring[0]);
                children.add(offspring[1]);
            }

            population.clear();
            population.addAll(children);

            this.optimalScore = optimalScore;
        }

        evolutionDuration = System.currentTimeMillis() - startTime;
    }

    private String[][] crossover(String[] parent1, String[] parent2) {
        String[][] offspring = new String[2][2];
        if (random.nextDouble() < crossoverRatio) {
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
        //вибір 3 особин, найкращої серед них
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

    private void mutate(String[] individual) {
        for (int i = 0; i < individual.length; i++)
            if (random.nextDouble() < mutationRatio) {
                int index = random.nextInt(individual.length);
                char[] chars = individual[index].toCharArray();
                chars[random.nextInt(chars.length)] = (chars[random.nextInt(chars.length)] == '0') ? '1' : '0';
                individual[index] = String.valueOf(chars);
            }

    }

    public long getEvolutionDuration() { return evolutionDuration;}
    public double getOptimalScore() {return optimalScore;}
}
