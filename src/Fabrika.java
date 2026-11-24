import java.util.*;

final class Fabrika {

    private static final Map<String, java.util.function.Supplier<RobotGovdesi>> GOVDE =
            new HashMap<>();

    private static final Map<Character, java.util.function.Supplier<Zemin>> ZEMIN =
            new HashMap<>();

    static {
        GOVDE.put("PALETLI", PaletliGovde::new);
        GOVDE.put("SICRAYAN", SicrayanGovde::new);
        GOVDE.put("HAVALI", HavaliGovde::new);
        GOVDE.put("KIZAKLI", KizakliGovde::new);
        GOVDE.put("YUZEN", YuzenGovde::new);

        ZEMIN.put('.', DuzZemin::new);
        ZEMIN.put('T', TozFirtinasi::new);
        ZEMIN.put('K', KraterliKaya::new);
        ZEMIN.put('~', MetanGolu::new);
        ZEMIN.put('I', BuzKabugu::new);
        ZEMIN.put('X', Engel::new);
        ZEMIN.put('H', Rampa::new);
        ZEMIN.put('L', Lav::new);
    }
    static RobotGovdesi govde(String ad) {
        var s = GOVDE.get(ad);
        return s == null ? null : s.get();
    }
    static Zemin zemin(char c) {
        var s = ZEMIN.get(c);
        return s == null ? null : s.get();
    }
}