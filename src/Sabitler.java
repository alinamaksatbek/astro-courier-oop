/** Tum enerji maliyetleri tek yerde: degisiklik gerekirse sadece buradan yapilir. */
final class Sabitler {
    private Sabitler() {}

    static final int DUZ = 1;        // duz zemin (tum govdeler)
    static final int TOZ = 2;        // toz firtinasi (havali haric)
    static final int KAYA = 3;       // kraterli kaya (sicrayan haric)
    static final int BUZ = 2;        // buz kabugu (kizakli ve havali haric)
    static final int HAVALI = 1;     // havali robotun toz/kaya/buz maliyeti
    static final int KIZAK_BUZ = 1;  // kizakli robotun buz maliyeti
    static final int SICRAMA = 2;    // sicrayan robotun kaya ustunden atlama maliyeti
    static final int METAN = 1;      // metan golunden gecis (havali / yuzen)
    static final int TESLIM = 1;     // teslim rampasina giris
    static final int LAV = 4;        // lav gecis maliyeti (ozgun zemin)
}
