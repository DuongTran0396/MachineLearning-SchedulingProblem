public class Main {
    public static void main(String[] args) {
        ReadInput readinput = new ReadInput(args[0]);
        Validation validation = new Validation(readinput.getdArr());
        Evaluation eval = new Evaluation(readinput.getdArr(), Integer.valueOf(args[1]),Integer.valueOf(args[2]),
                Integer.valueOf(args[3]),Integer.valueOf(args[4]),Integer.valueOf(args[5]),
                Integer.valueOf(args[6]),Integer.valueOf(args[7]),Integer.valueOf(args[8]));
        GeneticAlgorithm ga = new GeneticAlgorithm(readinput.getdArr(), validation, eval);

    }
}