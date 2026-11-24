import java.util.*;

final class Simulasyon {

    private final Harita harita;
    private final List<Komut> komutlar;

    Simulasyon(Harita harita, List<Komut> komutlar) {
        this.harita = harita;
        this.komutlar = List.copyOf(komutlar);
    }
    void calistir() {
        for (Komut k : komutlar) {
            for (Robot r : harita.robotlar()) {
                if (r.durum() == Durum.AKTIF) {
                    k.uygula(r, harita);
                }
            }
        }
    }
    String sonuc() {
        StringBuilder b = new StringBuilder();
        int teslim = 0;
        int enerji = 0;

        for (Robot r : harita.robotlar()) {
            b.append(r.sonuc()).append('\n');
            if (r.durum() == Durum.TESLIM) {
                teslim++;
            }

            enerji += r.enerji();
        }
        return b.append("TESLIM ")
                .append(teslim)
                .append('\n')
                .append("KALAN_ENERJI ")
                .append(enerji)
                .toString();
    }
    Harita harita() {

        return harita;
    }
}