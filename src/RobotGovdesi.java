abstract class RobotGovdesi implements Ucabilir, Atlayabilir, Kayabilir, Yuzebilir {
    abstract String ad();
    public boolean ucabilir() {
        return false;
    }
    public boolean atlayabilir() {
        return false;
    }
    public boolean kayabilir() {
        return false;
    }
    public boolean yuzebilir() {
        return false;
    }
    int duzMaliyet() {
        return Sabitler.DUZ;
    }
    int tozMaliyet() {
        return Sabitler.TOZ;
    }
    int kayaMaliyet() {
        return Sabitler.KAYA;
    }
    int buzMaliyet() {
        return Sabitler.BUZ;
    }
    int lavMaliyet() {
        return Sabitler.LAV;
    }
}