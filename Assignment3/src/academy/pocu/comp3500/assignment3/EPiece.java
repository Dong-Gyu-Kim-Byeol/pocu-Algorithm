package academy.pocu.comp3500.assignment3;

public enum EPiece {
    KING(0),
    QUEEN(1),
    ROOK(2),
    BISHOP(3),
    KNIGHT(4),
    PAWN(5),
    COUNT(6);

    public final int number;

    private EPiece(final int number) {
        this.number = number;
    }
}
