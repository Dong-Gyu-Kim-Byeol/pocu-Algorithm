package academy.pocu.comp3500.assignment3;

import java.util.ArrayList;

public final class Chess {
    public static final int BOARD_SIZE = 8;

    public static final int PAWN_CASE = 4;
    public static final int KING_CASE = 8;
    public static final int QUEEN_CASE = 32;
    public static final int ROOK_CASE = 16;
    public static final int BISHOP_CASE = 16;
    public static final int KNIGHT_CASE = 8;
    public static final int TOTAL_CASE = PAWN_CASE * 8 + KING_CASE + QUEEN_CASE + ROOK_CASE * 2 + BISHOP_CASE * 2 + KNIGHT_CASE * 2;

    public static final int[][] KING_MOVE_OFFSETS = {
            {-1, 1},
            {-1, 0},
            {-1, -1},
            {0, 1},
            {0, -1},
            {1, 1},
            {1, 0},
            {1, -1}
    };

    public static final int[][] KNIGHT_MOVE_OFFSETS = {
            {-2, -1},
            {-2, 1},
            {-1, -2},
            {-1, 2},
            {1, -2},
            {1, 2},
            {2, -1},
            {2, 1}
    };

    public static final int[][] PAWN_MOVE_OFFSETS = {
            {0, -1},
            {0, -2},
            {0, 1},
            {0, 2},
            {-1, -1},
            {1, -1},
            {-1, 1},
            {1, 1}
    };

    public static final int KING_SCORE = 100;
    public static final int QUEEN_SCORE = 20;
    public static final int ROOK_SCORE = 15;
    public static final int BISHOP_SCORE = 10;
    public static final int KNIGHT_SCORE = 5;
    public static final int PAWN_SCORE = 2;

    public static int getPieceScore(final char attackedPiece) {
        switch (Character.toLowerCase(attackedPiece)) {
            case 'k':
                return KING_SCORE;
            case 'q':
                return QUEEN_SCORE;
            case 'r':
                return ROOK_SCORE;
            case 'b':
                return BISHOP_SCORE;
            case 'n':
                return KNIGHT_SCORE;
            case 'p':
                return PAWN_SCORE;
            default:
                return 0;
        }
    }

    public static ScoreMove getMaxScoreMove(final ArrayList<ScoreMove> moves) {
        assert (!moves.isEmpty());

        ScoreMove bestMove = moves.get(0);
        for (final ScoreMove scoreMove : moves) {
            if ((scoreMove.score() == bestMove.score() && (Character.toLowerCase(scoreMove.piece()) == 'p' || Character.toLowerCase(bestMove.piece()) == 'k'))
                    || (scoreMove.score() > bestMove.score())) {
                bestMove = scoreMove;
            }
        }

        return bestMove;
    }

    public static ScoreMove getMinScoreMove(final ArrayList<ScoreMove> moves) {
        assert (!moves.isEmpty());

        ScoreMove bestMove = moves.get(0);
        for (final ScoreMove move : moves) {
            if ((move.score() == bestMove.score() && Character.toLowerCase(move.piece()) == 'p')
                    || move.score() < bestMove.score()) {
                bestMove = move;
            }
        }

        return bestMove;
    }

    public static int calculateBoardPoint(final char[][] board, final EColor player) {
        int score = 0;
        for (int y = 0; y < BOARD_SIZE; ++y) {
            for (int x = 0; x < BOARD_SIZE; ++x) {
                if ((player == EColor.WHITE) == Character.isLowerCase(board[y][x])) {
                    // my piece
                    score += getPieceScore(Character.toLowerCase(board[y][x]));
                } else {
                    // opponent piece
                    score -= getPieceScore(Character.toLowerCase(board[y][x]));
                }
            }
        }

        return score;
    }

    public static boolean hasWon(final char[][] board, final EColor player) {
        for (int y = 0; y < BOARD_SIZE; ++y) {
            for (int x = 0; x < BOARD_SIZE; ++x) {
                if ((player == EColor.WHITE) == Character.isUpperCase(board[y][x])) {
                    // opponent piece
                    if (Character.toLowerCase(board[y][x]) == 'k') {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static char[][] createCopy(final char[][] board) {
        assert (board.length == BOARD_SIZE);
        assert (board[0].length == BOARD_SIZE);

        final char[][] copy = new char[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                copy[i][j] = board[i][j];
            }
        }

        return copy;
    }

    public static boolean isMoveValid(char[][] board, EColor player, final int fromX, final int fromY, final int toX, final int toY) {
        if (fromX >= BOARD_SIZE || fromX < 0
                || fromY >= BOARD_SIZE || fromY < 0) {
            return false;
        }

        final char symbol = board[fromY][fromX];

        if (symbol == 0) {
            assert (false);
            return false;
        }

        if ((player == EColor.WHITE && !Character.isLowerCase(symbol))
                || player != EColor.WHITE && Character.isLowerCase(symbol)) {
            assert (false);
            return false;
        }

        if (toX >= BOARD_SIZE || toX < 0
                || toY >= BOARD_SIZE || toY < 0) {
            return false;
        }

        if (fromX == toX && fromY == toY) {
            assert (false);
            return false;
        }

        char symbolInvariant = Character.toLowerCase(symbol);

        switch (symbolInvariant) {
            case 'p':
                return isPawnMoveValid(board, fromX, fromY, toX, toY);
            case 'n':
                return isKnightMoveValid(board, fromX, fromY, toX, toY);
            case 'b':
                return isBishopMoveValid(board, fromX, fromY, toX, toY);
            case 'r':
                return isRookMoveValid(board, fromX, fromY, toX, toY);
            case 'q':
                return isQueenMoveValid(board, fromX, fromY, toX, toY);
            case 'k':
                return isKingMoveValid(board, fromX, fromY, toX, toY);
            default:
                throw new IllegalArgumentException("Unknown piece symbol");
        }
    }

    public static boolean isBishopMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = board[fromY][fromX];
        char toPiece = board[toY][toX];

        if (toPiece != 0 && Character.isLowerCase(fromPiece) == Character.isLowerCase(toPiece)) {
            return false;
        }

        if (Math.abs(fromX - toX) != Math.abs(fromY - toY)) {
            return false;
        }

        int xIncrement = fromX < toX ? 1 : -1;
        int yIncrement = fromY < toY ? 1 : -1;

        int x = fromX + xIncrement;
        int y = fromY + yIncrement;

        while (x != toX && y != toY) {
            if (board[y][x] != 0 && x != toX && y != toY) {
                return false;
            }

            x += xIncrement;
            y += yIncrement;
        }

        return true;
    }

    public static boolean isRookMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = board[fromY][fromX];
        char toPiece = board[toY][toX];

        if (toPiece != 0 && Character.isLowerCase(fromPiece) == Character.isLowerCase(toPiece)) {
            return false;
        }

        if (fromX == toX) {
            int yIncrement = fromY < toY ? 1 : -1;

            int y = fromY + yIncrement;

            while (y != toY) {
                if (board[y][fromX] != 0) {
                    return false;
                }

                y += yIncrement;
            }

            return true;

        } else if (fromY == toY) {
            int xIncrement = fromX < toX ? 1 : -1;

            int x = fromX + xIncrement;

            while (x != toX) {
                if (board[fromY][x] != 0) {
                    return false;
                }

                x += xIncrement;
            }

            return true;
        }

        return false;
    }

    public static boolean isKnightMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = board[fromY][fromX];
        char toPiece = board[toY][toX];

        if (toPiece != 0 && Character.isLowerCase(fromPiece) == Character.isLowerCase(toPiece)) {
            return false;
        }

        for (int i = 0; i < KNIGHT_MOVE_OFFSETS.length; ++i) {
            if (fromX + KNIGHT_MOVE_OFFSETS[i][0] == toX && fromY + KNIGHT_MOVE_OFFSETS[i][1] == toY) {
                return true;
            }
        }

        return false;
    }

    public static boolean isQueenMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        return isBishopMoveValid(board, fromX, fromY, toX, toY) || isRookMoveValid(board, fromX, fromY, toX, toY);
    }

    public static boolean isKingMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = board[fromY][fromX];
        char toPiece = board[toY][toX];

        if (toPiece != 0 && Character.isLowerCase(fromPiece) == Character.isLowerCase(toPiece)) {
            return false;
        }

        for (int i = 0; i < KING_MOVE_OFFSETS.length; ++i) {
            if (fromX + KING_MOVE_OFFSETS[i][0] == toX && fromY + KING_MOVE_OFFSETS[i][1] == toY) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPawnMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = board[fromY][fromX];
        char toPiece = board[toY][toX];

        boolean isFromPieceWhite = Character.isLowerCase(fromPiece);
        boolean isToPieceWhite = Character.isLowerCase(toPiece);

        if (toPiece != 0 && isFromPieceWhite == isToPieceWhite) {
            return false;
        }

        if (toPiece != 0 && fromX == toX) {
            return false;
        }

        boolean hasMoved = isFromPieceWhite ? fromY != 6 : fromY != 1;

        if (!hasMoved && fromX == toX && Math.abs(toY - fromY) == 2) {
            if (toY > fromY && !isFromPieceWhite && board[toY - 1][toX] == 0) {
                return true;
            }

            return toY < fromY && isFromPieceWhite && board[toY + 1][toX] == 0;
        } else if (fromX == toX && Math.abs(toY - fromY) == 1) {
            if (toY > fromY && !isFromPieceWhite) {
                return true;
            }

            return toY < fromY && isFromPieceWhite;
        } else if (toX == fromX - 1 || toX == fromX + 1) {
            if (toPiece != 0 && isToPieceWhite != isFromPieceWhite) {
                return isFromPieceWhite ? toY == fromY - 1 : toY == fromY + 1;
            }
        }

        return false;
    }
}
