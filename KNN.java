import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class KNN {
    private List<Obserwacja> zbiorTreningowy;
    private int liczbaCech;

    public KNN(String sciezkaTreningowa) {
        zbiorTreningowy = new ArrayList<>();
        wczytajDaneTreningowe(sciezkaTreningowa);
    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        System.out.print("Podaj ścieżkę do pliku treningowego: ");
        String sciezkaTreningowa = scanner.nextLine();
        KNN klasyfikator = new KNN(sciezkaTreningowa);

        System.out.print("Podaj liczbę k: ");
        int k = Integer.parseInt(scanner.nextLine());


        label:

        //Petla - 4 opcje
        while (true) {

            System.out.println("\nWybierz jedną z opcji:");
            System.out.println("a) Klasyfikacja wszystkich obserwacji ze zbioru testowego");
            System.out.println("b) Klasyfikacja obserwacji podanej przez użytkownika");
            System.out.println("c) Zmień K");
            System.out.println("d) Wyjście z programu");

            String opcja = scanner.nextLine();


            switch (opcja) {

                // Klasyfikacja wszystkich obserwacji ze zbioru testowego podanego w oddzielnym pliku
                case "a":
                    System.out.print("Podaj ścieżkę do pliku ze zbiorem testowym: ");
                    String sciezkaTestowa = scanner.nextLine();
                    List<Obserwacja> zbiorTestowy = new ArrayList<>();
                    klasyfikator.wczytajDaneTestowe(sciezkaTestowa, zbiorTestowy);
                    List<String> przewidywania = new ArrayList<>();

                    for (Obserwacja obserwacjaTestowa : zbiorTestowy) {
                        List<Obserwacja> sasiedzi = klasyfikator.znajdzSasiadow(obserwacjaTestowa.getCechy(), k);
                        String wynik = klasyfikator.przewidywanieKlasyfikacjiKwiatow(sasiedzi);
                        przewidywania.add(wynik);
                        System.out.println("Przewidziana etykieta: " + wynik);
                    }
                    double dokladnosc = klasyfikator.obliczDokladnosc(zbiorTestowy, przewidywania);
                    System.out.printf("Dokładność klasyfikacji: %.2f%%\n", dokladnosc);
                    break;

                case "b":
                    System.out.println("Podaj cechy obserwacji oddzielone przecinkami:");
                    String[] userInput = scanner.nextLine().split(",");
                    List<Double> userInstance = new ArrayList<>();

                    for (String input : userInput) {
                        userInstance.add(Double.parseDouble(input));
                    }

                    List<Obserwacja> sasiedzi = klasyfikator.znajdzSasiadow(userInstance, k);
                    String wynik = klasyfikator.przewidywanieKlasyfikacjiKwiatow(sasiedzi);
                    System.out.println("Przewidziana etykieta: " + wynik);
                    break;

                // Zmiana liczby k
                case "c":
                    System.out.print("Podaj nową liczbę K: ");
                    k = Integer.parseInt(scanner.nextLine());
                    break;

                //Przerwanie etykiety - operacji
                case "d":
                    break label;

                //Inna nieprzewidziana operacja - przerwanie
                default:
                    System.out.println("Niepoprawna opcja. Wybierz ponownie.");
                    break;
            }

        }
    }

    // Metoda wczytująca dane treningowe z pliku
    private void wczytajDaneTreningowe(String sciezkaTreningowa) {
        try (BufferedReader br = new BufferedReader(new FileReader(sciezkaTreningowa))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                List<Double> cechy = new ArrayList<>();
                for (int i = 0; i < parts.length - 1; i++) {
                    cechy.add(Double.parseDouble(parts[i]));
                }
                String etykieta = parts[parts.length - 1];
                zbiorTreningowy.add(new Obserwacja(cechy, etykieta));
            }
            if (!zbiorTreningowy.isEmpty()) {
                liczbaCech = zbiorTreningowy.get(0).getCechy().size();
            }
        } catch (IOException e) {
            System.out.println("Wystąpił błąd + " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metoda wczytująca dane testowe z pliku
    private void wczytajDaneTestowe(String sciezkaTestowa, List<Obserwacja> zbiorTestowy) {
        try (BufferedReader br = new BufferedReader(new FileReader(sciezkaTestowa))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                List<Double> cechy = new ArrayList<>();
                for (int i = 0; i < parts.length - 1; i++) {
                    cechy.add(Double.parseDouble(parts[i]));
                }
                zbiorTestowy.add(new Obserwacja(cechy, parts[parts.length - 1]));
            }
        } catch (IOException e) {
            System.out.println("Wystąpił błąd + " + e.getMessage());
            e.printStackTrace();
        }


    // Metoda zwracająca k najbliższych sąsiadów dla danej obserwacji
    public List<Obserwacja> znajdzSasiadow(List<Double> obserwacjaTestowa, int k) {
        for (Obserwacja obserwacjaTreningowa : zbiorTreningowy) {
            double odleglosc = odlegloscEuklidesowa(obserwacjaTestowa, obserwacjaTreningowa.getCechy());
            obserwacjaTreningowa.setOdleglosc(odleglosc);
        }
        zbiorTreningowy.sort(Comparator.comparingDouble(Obserwacja::getOdleglosc));
        return zbiorTreningowy.subList(0, k);
    }

    // Metoda dokonująca klasyfikacji na podstawie k najbliższych sąsiadów
    private String przewidywanieKlasyfikacjiKwiatow(List<Obserwacja> sasiedzi) {
        Map<String, Integer> glosyKlasy = new HashMap<>();

        for (Obserwacja sasiad : sasiedzi) {
            String etykieta = sasiad.getEtykieta();
            glosyKlasy.put(etykieta, glosyKlasy.getOrDefault(etykieta, 0) + 1);
        }
        return glosyKlasy.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    //Liczenie odleglosci euklidesowej
    private double odlegloscEuklidesowa(List<Double> obserwacja1, List<Double> obserwacja2) {
        double odleglosc = 0;
        for (int i = 0; i < liczbaCech; i++) {
            odleglosc += Math.pow(obserwacja1.get(i) - obserwacja2.get(i), 2);
        }
        return Math.sqrt(odleglosc);
    }
//    Metoda obliczająca odległość euklidesową między dwiema obserwacjami na podstawie ich cech.

    //Liczenie dokładności - użyte w case a
    public double obliczDokladnosc(List<Obserwacja> zbiorTestowy, List<String> przewidywania) {
        int poprawne = 0;
        for (int i = 0; i < zbiorTestowy.size(); i++) {
            if (zbiorTestowy.get(i).getEtykieta().equals(przewidywania.get(i))) {
                poprawne++;
            }
        }
        if (zbiorTestowy.size() == 0) {
            return 0.0;
        }
        return (double) poprawne / zbiorTestowy.size() * 100.0;
    }
}