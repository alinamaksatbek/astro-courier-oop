import java.io.*;
import java.nio.file.*;
import java.util.*;

final class DosyaOkuyucu {

    private record Satir(int no, String metin) {}
    private static List<Satir> oku(Path path) throws IOException {

        List<Satir> sonuc = new ArrayList<>();
        List<String> ham = Files.readAllLines(path);

        for (int i = 0; i < ham.size(); i++) {
            String s = ham.get(i);
            int k = s.indexOf('#');

            if (k >= 0) {
                s = s.substring(0, k);
            }
            s = s.trim();
            if (!s.isEmpty()) {
                sonuc.add(new Satir(i + 1, s));
            }
        }
        return sonuc;
    }

    static Harita harita(Path path) throws IOException, DosyaHatasi {

        List<Satir> sat = oku(path);

        int w = -1;
        int h = -1;
        int e = -1;
        boolean harita = false;
        int index = 0;
        Map<Integer, String> turler = new TreeMap<>();
        List<Satir> satirlar = new ArrayList<>();

        for (Satir s : sat) {
            String[] t = s.metin.split("\\s+");
            if (!harita) {
                if (t[0].equals("BOYUT")) {
                    if (w != -1 || t.length != 3) {
                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "BOYUT hatali veya tekrar"
                        );
                    }
                    try {
                        w = Integer.parseInt(t[1]);
                        h = Integer.parseInt(t[2]);

                    } catch (NumberFormatException ex) {

                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "BOYUT sayi olmali"
                        );
                    }
                    if (w < 1 || h < 1 || w > 1000 || h > 1000) {
                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "BOYUT gecersiz"
                        );
                    }
                }
                else if (t[0].equals("ENERJI")) {
                    if (e != -1 || t.length != 2) {
                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "ENERJI hatali veya tekrar"
                        );
                    }
                    try {
                        e = Integer.parseInt(t[1]);

                    } catch (NumberFormatException ex) {

                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "ENERJI sayi olmali"
                        );
                    }
                    if (e < 0) {
                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "ENERJI negatif"
                        );
                    }
                }
                else if (t[0].equals("ROBOT")) {
                    if (t.length != 3) {
                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "ROBOT formati"
                        );
                    }
                    int n;
                    try {
                        n = Integer.parseInt(t[1]);
                    } catch (NumberFormatException ex) {

                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "Robot numarasi sayi olmali"
                        );
                    }
                    if (n < 1 || n > 9 ||
                        turler.containsKey(n) ||
                        Fabrika.govde(t[2]) == null) {

                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "Gecersiz/tekrar robot veya govde"
                        );
                    }
                    turler.put(n, t[2]);
                }

                else if (s.metin.equals("HARITA")) {

                    if (w < 1 || h < 1 || e < 0 || turler.isEmpty()) {
                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "Eksik baslik"
                        );
                    }
                    harita = true;
                }
                else {

                    throw new DosyaHatasi(
                        path.toString(),
                        s.no,
                        "Bilinmeyen baslik"
                    );
                }
            }
            else {
                satirlar.add(s);
            }
        }
        if (!harita) {

            throw new DosyaHatasi(
                path.toString(),
                sat.isEmpty() ? 1 : sat.get(sat.size() - 1).no,
                "HARITA eksik"
            );
        }
        if (satirlar.size() != h) {

            throw new DosyaHatasi(
                path.toString(),
                satirlar.isEmpty()
                    ? sat.get(sat.size() - 1).no
                    : satirlar.get(satirlar.size() - 1).no,
                "Harita satir sayisi " + h + " olmali"
            );
        }

        Zemin[][] alan = new Zemin[h][w];
        Map<Integer, int[]> konumlar = new HashMap<>();

        for (int y = 0; y < h; y++) {
            Satir s = satirlar.get(y);

            if (s.metin.length() != w) {
                throw new DosyaHatasi(
                    path.toString(),
                    s.no,
                    "Harita satiri " + w + " karakter olmali"
                );
            }

            for (int x = 0; x < w; x++) {
                char c = s.metin.charAt(x);
                if (c >= '1' && c <= '9') {
                    int n = c - '0';

                    if (!turler.containsKey(n) || konumlar.containsKey(n)) {

                        throw new DosyaHatasi(
                            path.toString(),
                            s.no,
                            "Tanimsiz veya ikinci robot " + n
                        );
                    }
                    konumlar.put(n, new int[]{x, y});
                    c = '.';
                }

                Zemin z = Fabrika.zemin(c);
                if (z == null) {

                    throw new DosyaHatasi(
                        path.toString(),
                        s.no,
                        "Bilinmeyen zemin: " + c
                    );
                }
                alan[y][x] = z;
            }
        }
        if (!konumlar.keySet().equals(turler.keySet())) {

            throw new DosyaHatasi(
                path.toString(),
                satirlar.get(0).no,
                "Robot tanimi ile baslangic konumlari uyusmuyor"
            );
        }
        List<Robot> robotlar = new ArrayList<>();
        for (var entry : turler.entrySet()) {
            int[] xy = konumlar.get(entry.getKey());
            robotlar.add(
                new Robot(
                    entry.getKey(),
                    xy[0],
                    xy[1],
                    e,
                    Fabrika.govde(entry.getValue())
                )
            );
        }
        return new Harita(alan, robotlar);
    }

    static List<Komut> komutlar(Path path)
            throws IOException, DosyaHatasi {

        List<Komut> result = new ArrayList<>();

        for (Satir s : oku(path)) {

            String[] t = s.metin.split("\\s+");

            if (t.length == 1 && t[0].equals("SAG")) {

                result.add(new SagKomutu());
            }
            else if (t.length == 1 && t[0].equals("SOL")) {

                result.add(new SolKomutu());
            }
            else if (t.length == 2 && t[0].equals("ILERI")) {
                try {

                    int n = Integer.parseInt(t[1]);

                    if (n < 1 || n > 99) {
                        throw new NumberFormatException();
                    }

                    result.add(new IleriKomutu(n));

                } catch (NumberFormatException ex) {

                    throw new DosyaHatasi(
                        path.toString(),
                        s.no,
                        "ILERI degeri 1-99 olmali"
                    );
                }
            }
            else {

                throw new DosyaHatasi(
                    path.toString(),
                    s.no,
                    "Bilinmeyen komut: " + s.metin
                );
            }
        }

        return result;
    }
}