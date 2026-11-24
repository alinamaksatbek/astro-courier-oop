final class PaletliGovde extends RobotGovdesi {

    String ad() {
        return "PALETLI";
    }
}

final class SicrayanGovde extends RobotGovdesi {

    String ad() {
        return "SICRAYAN";
    }
    public boolean atlayabilir() {
        return true;
    }
}

final class HavaliGovde extends RobotGovdesi {

    String ad() {
        return "HAVALI";
    }
    public boolean ucabilir() {
        return true;
    }
    int tozMaliyet() {
        return Sabitler.HAVALI;
    }
    int kayaMaliyet() {
        return Sabitler.HAVALI;
    }
    int buzMaliyet() {
        return Sabitler.HAVALI;
    }
}

final class KizakliGovde extends RobotGovdesi {
    String ad() {
        return "KIZAKLI";
    }
    public boolean kayabilir() {
        return true;
    }
    int buzMaliyet() {
        return Sabitler.KIZAK_BUZ;
    }
}
// Ozgun govde: golu yuzerek asar;
// diger zeminlerde temel maliyetleri kullanir.
final class YuzenGovde extends RobotGovdesi {

    String ad() {
        return "YUZEN";
    }

    public boolean yuzebilir() {
        return true;
    }
}