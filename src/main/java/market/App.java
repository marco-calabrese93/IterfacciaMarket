package market;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Menu Principale ===");
            System.out.println("1 - Inserisci nuova richiesta");
            System.out.println("2 - Tempo medio di evasione per tecnici");
            System.out.println("3 - Visualizza richieste non assegnate");
            System.out.println("0 - Esci");
            System.out.print("Scelta: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Riprova.");
                continue;
            }

            switch (scelta) {
                case 1:
                    InserimentoRichiesta.esegui();
                    break;
                case 2:
                    TempoMedioTecnici.execute();
                    break;
                case 3:
                    RichiesteNonAssegnate.execute();
                    break;
                case 0:
                    System.out.println("Uscita dal programma. Arrivederci!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Scelta non valida. Riprova.");
            }
        }
    }
}
