import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--gui")) {
            Arayuz.baslat();
            return;
        }
        if (args.length != 2) {
            System.err.println("Kullanim: java -cp out Main harita.txt komutlar.txt  VEYA  java -cp out Main --gui");
            System.exit(1);
        }
        try {
            Harita h = DosyaOkuyucu.harita(Path.of(args[0]));
            var komutlar = DosyaOkuyucu.komutlar(Path.of(args[1]));
            Simulasyon s = new Simulasyon(h, komutlar);
            s.calistir();
            System.out.println(s.sonuc());
        } catch (Exception ex) {
            System.err.println("HATA: " + ex.getMessage());
            System.exit(1);
        }
    }
}
