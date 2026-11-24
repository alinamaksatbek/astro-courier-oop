abstract class Zemin {

    abstract char simge();
    abstract Hareket gir(Robot r, Harita h, int x, int y);

    boolean inisUygun() {
        return true;
    }
    /** Bir robot bu hucreye sicrayarak INDIGINDE ne olur (varsayilan: normal konum degisimi). */
    Hareket inis(int x, int y, int maliyet) {
        return Hareket.git(x, y, maliyet);
    }
    protected Hareket normal(int x, int y, int maliyet) {
        return Hareket.git(x, y, maliyet);
    }
}
final class DuzZemin extends Zemin {
    char simge() {
        return '.';
    }
    Hareket gir(Robot r, Harita h, int x, int y) {
        return normal(x, y, r.govde().duzMaliyet());
    }
}
final class TozFirtinasi extends Zemin {
    char simge() {

        return 'T';
    }
    Hareket gir(Robot r, Harita h, int x, int y) {

        return r.govde().ucabilir()
                ? Hareket.savrul(r.govde().tozMaliyet())
                : normal(x, y, r.govde().tozMaliyet());
    }
}
final class KraterliKaya extends Zemin {
    char simge() {
        return 'K';
    }
    boolean inisUygun() {
        return false;
    }
    Hareket gir(Robot r, Harita h, int x, int y) {
        if (!r.govde().atlayabilir()) {
            return normal(x, y, r.govde().kayaMaliyet());
        }
        int tx = x + r.yon().dx;
        int ty = y + r.yon().dy;
        if (!h.uygunInis(tx, ty)) {
            return Hareket.dur();
        }
        return h.zemin(tx, ty).inis(tx, ty, Sabitler.SICRAMA);
    }
}
final class MetanGolu extends Zemin {
    char simge() {
        return '~';
    }
    boolean inisUygun() {
        return false;
    }
    Hareket gir(Robot r, Harita h, int x, int y) {
        return (r.govde().ucabilir() || r.govde().yuzebilir())
                ? normal(x, y, Sabitler.METAN)
                : Hareket.dur();
    }
}
final class BuzKabugu extends Zemin {
    char simge() {

        return 'I';
    }
    Hareket gir(Robot r, Harita h, int x, int y) {
        if (!r.govde().kayabilir()) {
            return normal(x, y, r.govde().buzMaliyet());
        }
        int tx = x;
        int ty = y;
        while (h.iceride(
                tx + r.yon().dx,
                ty + r.yon().dy
        ) &&
                h.simge(
                        tx + r.yon().dx,
                        ty + r.yon().dy
                ) == 'I' &&
                !h.dolu(
                        tx + r.yon().dx,
                        ty + r.yon().dy,
                        r
                )) {
            tx += r.yon().dx;
            ty += r.yon().dy;
        }
        return Hareket.kay(tx, ty, r.govde().buzMaliyet());
    }
}
final class Engel extends Zemin {
    char simge() {
        return 'X';
    }
    boolean inisUygun() {
        return false;
    }
    Hareket gir(Robot r, Harita h, int x, int y) {
        return Hareket.dur();
    }
}
final class Rampa extends Zemin {
    char simge() {
        return 'H';
    }
    Hareket gir(Robot r, Harita h, int x, int y) {

        return Hareket.teslim(x, y);
    }
    @Override
    Hareket inis(int x, int y, int maliyet) {
        return Hareket.teslim(x, y, maliyet);
    }
}
// Ozgun zemin: lav; yalnizca ucabilen govde gecis yapabilir, 4 enerji harcar.
final class Lav extends Zemin {
    char simge() {
        return 'L';
    }
    boolean inisUygun() {
        return false;
    }
    Hareket gir(Robot r, Harita h, int x, int y) {
        return r.govde().ucabilir()
                ? normal(x, y, r.govde().lavMaliyet())
                : Hareket.dur();
    }
}