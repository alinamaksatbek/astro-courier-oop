final class Robot {

    static Runnable hareketDinleyicisi;

    private void bildir() {

        if (hareketDinleyicisi != null) {
            hareketDinleyicisi.run();
        }
    }

    private final int no;
    private final RobotGovdesi govde;
    private int x, y, enerji;
    private Yon yon = Yon.E;
    private Durum durum = Durum.AKTIF;

    Robot(int no, int x, int y, int enerji, RobotGovdesi govde) {

        if (no < 1 || no > 9 || enerji < 0) {
            throw new IllegalArgumentException("Gecersiz robot");
        }
        this.no = no;
        this.x = x;
        this.y = y;
        this.enerji = enerji;
        this.govde = govde;
    }
    int no() {
        return no;
    }
    int x() {
        return x;
    }
    int y() {
        return y;
    }
    int enerji() {
        return enerji;
    }
    Yon yon() {
        return yon;
    }
    Durum durum() {
        return durum;
    }
    RobotGovdesi govde() {
        return govde;
    }
    void sagaDon() {
        if (durum == Durum.AKTIF) {
            yon = yon.sag();
            bildir();
        }
    }
    void solaDon() {
        if (durum == Durum.AKTIF) {
            yon = yon.sol();
            bildir();
        }
    }
    boolean enerjiHarca(int miktar) {

        if (durum != Durum.AKTIF) {
            return false;
        }
        if (miktar < 0) {
            throw new IllegalArgumentException("Negatif maliyet");
        }
        if (enerji < miktar) {
            durum = Durum.ENERJISIZ;
            bildir();
            return false;
        }
        enerji -= miktar;
        return true;
    }
    void konumDegistir(int nx, int ny) {

        if (durum != Durum.AKTIF) {
            throw new IllegalStateException("Robot aktif degil");
        }

        x = nx;
        y = ny;

        bildir();
    }
    void teslimEt() {
        if (durum == Durum.AKTIF) {
            durum = Durum.TESLIM;
            bildir();
        }
    }
    String sonuc() {
        return "ROBOT " + no + " " + govde.ad()
                + " KONUM " + x + " " + y
                + " YON " + yon
                + " ENERJI " + enerji
                + " DURUM " + durum;
    }
}