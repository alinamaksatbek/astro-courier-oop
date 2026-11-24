import java.util.*;

final class Harita {

    private final Zemin[][] alan;
    private final List<Robot> robotlar;

    Harita(Zemin[][] alan, List<Robot> robotlar) {
        this.alan = alan;
        this.robotlar = List.copyOf(robotlar);
    }
    int genislik() {
        return alan[0].length;
    }
    int yukseklik() {
        return alan.length;
    }
    boolean iceride(int x, int y) {
        return x >= 0 && y >= 0 && x < genislik() && y < yukseklik();
    }

    char simge(int x, int y) {
        return alan[y][x].simge();
    }
    Zemin zemin(int x, int y) {
        return alan[y][x];
    }
    List<Robot> robotlar() {
        return robotlar;
    }
    boolean dolu(int x, int y, Robot haric) {
        for (Robot r : robotlar) {
            if (r != haric &&
                r.durum() != Durum.TESLIM &&
                r.x() == x &&
                r.y() == y) {
                return true;
            }
        }
        return false;
    }
    boolean uygunInis(int x, int y) {

        if (!iceride(x, y) || dolu(x, y, null)) {
            return false;
        }
        return zemin(x, y).inisUygun();
    }
}