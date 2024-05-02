public class Main {
    public static void main(String[] args) {
        FitnessFunction[] functions = new FitnessFunction[] {
                (x, y) -> 20 + (x * x - 10 * Math.cos(2 * Math.PI * x)) + (y * y - 10 * Math.cos(2 * Math.PI * y)), // Rastrigin Function
                (x, y) -> Math.pow(Math.sin(3 * Math.PI * x), 2) + Math.pow(x - 1, 2) * (1 + Math.pow(Math.sin(3 * Math.PI * y), 2)) +  Math.pow(y - 1, 2) * (1 + Math.pow(Math.sin(2 * Math.PI * y), 2)), // Levy 13 Function
                (x, y) -> 0.5 * (Math.pow(x, 4) - 16 * x * x + 5 * x + Math.pow(y, 4) - 16 * y * y + 5 * y) // Stibinski-Tanga Function
        };
        double[][] functionsBounds ={ {-5.12, 5.12}, {-10, 10}, {-5, 5} };
        double[][] tableVariables = { {0,0}, {1,1}, {-2.903534,-2.903534}};

        final int TESTS_NUMBER = 3;
        int populationSize = 1000;
        int iterationNumber = 100;
        double crossoverRate = 0.5;
        double mutationRate = 0.25;
        double selectionRate = 0.75;

        System.out.println("=======================================================");
        System.out.println("\t\t\t\t\tINITIAL SETTINGS");
        System.out.println("-------------------------------------------------------");
            System.out.println(" -> Population size: " + populationSize);
            System.out.println(" -> Iterations number: " + iterationNumber);
            System.out.println(" -> Crossover rate: " + crossoverRate);
            System.out.println(" -> Mutation rate: " + mutationRate);
            System.out.println(" -> Selection rate: " + selectionRate);
        for (int i = 0; i < functions.length; ++i) {
            System.out.println("=======================================================");
            System.out.print("\t\t\tTesting for <");
            switch (i) {
                case 0:
                    System.out.print("Rastrigin function");
                    break;
                case 1:
                    System.out.print("Levy 13 function");
                    break;
                case 2:
                    System.out.print("Stibinski-Tanga function");
                    break;
            }
            System.out.println(">");
            System.out.println("-------------------------------------------------------");
            System.out.printf("%3s%17s%18s%17s\n", "№", "Duration", "Result", "Error");
            GeneticAlgorithm algorithm = new GeneticAlgorithm(populationSize, iterationNumber, crossoverRate, mutationRate,selectionRate, functionsBounds[i], functions[i]);
            double avgError = 0;
            double avgDuration = 0;
            for (int j = 1; j <= TESTS_NUMBER; ++j) {
                algorithm.startEvolution();
                System.out.printf("%3s%17.5s%18.7f%17.7f\n", j, algorithm.getEvolutionDuration() + " ms", algorithm.getBestScore(), getError(algorithm.getBestScore(), functions[i].calculate(tableVariables[i][0], tableVariables[i][1])));
                avgError += getError(algorithm.getBestScore(), functions[i].calculate(tableVariables[i][0], tableVariables[i][1]));
                avgDuration += algorithm.getEvolutionDuration();
            }
            System.out.println("-------------------------------------------------------");
            avgError /= TESTS_NUMBER;
            avgDuration /= TESTS_NUMBER;
            System.out.println("Average duration: " + (int)avgDuration + " ms");
            System.out.println("Average error: " + String.format("%.7f", avgError));
        }
    }
    public static double getError(double bestScore, double tableValue){
        return bestScore - tableValue;
    };
}
