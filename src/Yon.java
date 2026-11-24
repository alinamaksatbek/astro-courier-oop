enum Yon {
    E(1, 0),
    S(0, 1),
    W(-1, 0),
    N(0, -1);
    final int dx, dy;
    Yon(int x, int y) {
        dx = x;
        dy = y;
    }
    Yon sag() {
        return values()[(ordinal() + 1) % 4];
    }
    Yon sol() {
        return values()[(ordinal() + 3) % 4];
    }
}