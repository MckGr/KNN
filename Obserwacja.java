import java.util.List;

class Obserwacja {

    //Lista cech
    private final List<Double> cechy;

    //Etykieta
    private final String etykieta;

    //Odległość od innej instancji (wykorzystywana w kNN)
    private double odleglosc;

    //  pierwsze N-1 kolumn to cechy obserwacji (cechy) a ostatnia kolumna to poprawna etykieta (etykieta)
    public Obserwacja(List<Double> cechy, String etykieta) {
        this.cechy = cechy;
        this.etykieta = etykieta;
    }


    //Gettery
    public List<Double> getCechy() {
        return cechy;
    }

    public String getEtykieta() {
        return etykieta;
    }

    public double getOdleglosc() {
        return odleglosc;
    }

    //Settery
    public void setOdleglosc(double odleglosc) {
        this.odleglosc = odleglosc;
    }
}