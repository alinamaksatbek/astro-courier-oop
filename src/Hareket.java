final class Hareket {

    final int x, y, maliyet;
    final boolean hareket, devam, teslim;

    private Hareket(
            int x,
            int y,
            int maliyet,
            boolean hareket,
            boolean devam,
            boolean teslim) {

        this.x = x;
        this.y = y;
        this.maliyet = maliyet;
        this.hareket = hareket;
        this.devam = devam;
        this.teslim = teslim;
    }
    static Hareket dur() {
        return new Hareket(
                -1, -1, 0,
                false, false, false
        );
    }
    static Hareket savrul(int maliyet) {
        return new Hareket(
                -1, -1, maliyet,
                false, false, false
        );
    }
    static Hareket git(int x, int y, int maliyet) {
        return new Hareket(
                x, y, maliyet,
                true, true, false
        );
    }
    static Hareket kay(int x, int y, int maliyet) {
       return new Hareket(
                x, y, maliyet,
                true, true, false
        );
    }
    static Hareket teslim(int x, int y) {
        return teslim(x, y, Sabitler.TESLIM);
    }
    static Hareket teslim(int x, int y, int maliyet) {
        return new Hareket(
                x, y, maliyet,
                true, false, true
        );
    }
}