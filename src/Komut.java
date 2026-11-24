interface Komut {

    void uygula(Robot robot, Harita harita);

    String aciklama();
}
final class SagKomutu implements Komut {
    public void uygula(Robot r, Harita h) {

        r.sagaDon();
    }

    public String aciklama() {

        return "SAG";
    }
}
final class SolKomutu implements Komut {

    public void uygula(Robot r, Harita h) {

        r.solaDon();
    }
    public String aciklama() {

        return "SOL";
    }
}
final class IleriKomutu implements Komut {

    private final int adim;

    IleriKomutu(int adim) {
        if (adim < 1 || adim > 99) {
            throw new IllegalArgumentException("ILERI 1-99 olmali");
        }
        this.adim = adim;
    }
    public String aciklama() {

        return "ILERI " + adim;
    }
    public void uygula(Robot r, Harita h) {

        for (int i = 0; i < adim && r.durum() == Durum.AKTIF; i++) {

            int nx = r.x() + r.yon().dx;
            int ny = r.y() + r.yon().dy;

            if (!h.iceride(nx, ny) || h.dolu(nx, ny, r)) {
                break;
            }
            Hareket plan = h.zemin(nx, ny).gir(r, h, nx, ny);
            if (!plan.hareket && plan.maliyet == 0) {
                break;
            }
            if (!r.enerjiHarca(plan.maliyet)) {
                break;
            }
            if (plan.hareket) {
                r.konumDegistir(plan.x, plan.y);
                if (plan.teslim) {
                    r.teslimEt();
                }
            }
            if (!plan.devam) {
                break;
            }
        }
    }
}