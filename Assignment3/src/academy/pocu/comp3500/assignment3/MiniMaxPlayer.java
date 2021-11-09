package academy.pocu.comp3500.assignment3;

import java.util.ArrayList;

public class MiniMaxPlayer extends ChessPlayerBase {
    private static final int DEPTH = 3;

    public MiniMaxPlayer(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds, DEPTH);
    }

    @Override
    protected final ArrayList<CompactMove> getCanMoveList(final char[][] board, final EColor turn) {
        ArrayList<CompactMove> outMoves = new ArrayList<CompactMove>(TOTAL_CASE);

        ArrayList<ScoreMove> temps;
        for (int y = 0; y < BOARD_SIZE; ++y) {
            for (int x = 0; x < BOARD_SIZE; ++x) {
                if (board[y][x] == 0) {
                    continue;
                }

                if ((turn == EColor.WHITE) != Character.isLowerCase(board[y][x])) {
                    continue;
                }

                final char symbol = board[y][x];
                switch (Character.toLowerCase(symbol)) {
                    case 'p':
                        temps = pawnMoveOrNull(board, x, y, turn);
                        break;
                    case 'k':
                        temps = kingMoveOrNull(board, x, y, turn);
                        break;
                    case 'q':
                        temps = queenMoveOrNull(board, x, y, turn);
                        break;
                    case 'r':
                        temps = rookMoveOrNull(board, x, y, turn);
                        break;
                    case 'b':
                        temps = bishopMoveOrNull(board, x, y, turn);
                        break;
                    case 'n':
                        temps = knightMoveOrNull(board, x, y, turn);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown piece symbol");
                }

                if (temps.size() == 0) {
                    continue;
                }

                for (ScoreMove temp : temps) {
                    assert (isMoveValid(board, turn, temp.fromX(), temp.fromY(), temp.toX(), temp.toY()));

                    final CompactMove newMove = this.compactMoveMemoryPool.getNext();
                    newMove.init(temp.fromX(), temp.fromY(), temp.toX(), temp.toY());

                    if (temp.score() == KING_SCORE) {
                        outMoves.clear();
                        outMoves.add(newMove);
                        return outMoves;
                    }

                    outMoves.add(newMove);
                }
            }
        }

        assert (outMoves.size() <= TOTAL_CASE);

        return outMoves;
    }

    private ArrayList<ScoreMove> pawnMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(PAWN_CASE);

        for (final int[] pawnAttackOffset : PAWN_MOVE_OFFSETS) {
            final int toX = fromX + pawnAttackOffset[0];
            final int toY = fromY + pawnAttackOffset[1];

            if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = ChessPlayerBase.getPieceScore(attackedPiece);

                final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                if (score == KING_SCORE) {
                    outScoreMoves.clear();
                    outScoreMoves.add(newScoreMove);
                    return outScoreMoves;
                }

                outScoreMoves.add(newScoreMove);
            }
        }

        assert (outScoreMoves.size() <= PAWN_CASE);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> kingMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(KING_CASE);

        for (final int[] kingMoveOffset : KING_MOVE_OFFSETS) {
            final int toX = fromX + kingMoveOffset[0];
            final int toY = fromY + kingMoveOffset[1];

            if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = GreedyMiniMaxPlayer.getPieceScore(attackedPiece);

                final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                if (score == KING_SCORE) {
                    outScoreMoves.clear();
                    outScoreMoves.add(newScoreMove);
                    return outScoreMoves;
                }

                outScoreMoves.add(newScoreMove);
            }
        }

        assert (outScoreMoves.size() <= KING_CASE);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> queenMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(QUEEN_CASE);

        for (int moveType = 0; moveType < 8; ++moveType) {
            final int UP_VERTICAL_MOVE_TYPE = 0;
            final int DOWN_VERTICAL_MOVE_TYPE = 1;
            final int RIGHT_HORIZONTAL_MOVE_TYPE = 2;
            final int LEFT_HORIZONTAL_MOVE_TYPE = 3;
            final int UP_RIGHT_CROSS_MOVE_TYPE = 4;
            final int UP_LEFT_CROSS_MOVE_TYPE = 5;
            final int DOWN_RIGHT_CROSS_MOVE_TYPE = 6;
            final int DOWN_LEFT_CROSS_MOVE_TYPE = 7;

            for (int moveSize = 1; moveSize < BOARD_SIZE; ++moveSize) {
                final int queenMoveOffsetX;
                final int queenMoveOffsetY;
                switch (moveType) {
                    case UP_VERTICAL_MOVE_TYPE:
                        queenMoveOffsetX = 0;
                        queenMoveOffsetY = moveSize;
                        break;
                    case DOWN_VERTICAL_MOVE_TYPE:
                        queenMoveOffsetX = 0;
                        queenMoveOffsetY = -moveSize;
                        break;
                    case RIGHT_HORIZONTAL_MOVE_TYPE:
                        queenMoveOffsetX = moveSize;
                        queenMoveOffsetY = 0;
                        break;
                    case LEFT_HORIZONTAL_MOVE_TYPE:
                        queenMoveOffsetX = -moveSize;
                        queenMoveOffsetY = 0;
                        break;
                    case UP_RIGHT_CROSS_MOVE_TYPE:
                        queenMoveOffsetX = moveSize;
                        queenMoveOffsetY = moveSize;
                        break;
                    case UP_LEFT_CROSS_MOVE_TYPE:
                        queenMoveOffsetX = -moveSize;
                        queenMoveOffsetY = moveSize;
                        break;
                    case DOWN_RIGHT_CROSS_MOVE_TYPE:
                        queenMoveOffsetX = moveSize;
                        queenMoveOffsetY = -moveSize;
                        break;
                    case DOWN_LEFT_CROSS_MOVE_TYPE:
                        queenMoveOffsetX = -moveSize;
                        queenMoveOffsetY = -moveSize;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown queen move type");
                }

                final int toX = fromX + queenMoveOffsetX;
                final int toY = fromY + queenMoveOffsetY;
                if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = GreedyMiniMaxPlayer.getPieceScore(Character.toLowerCase(attackedPiece));

                    final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                    newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                    if (score == KING_SCORE) {
                        outScoreMoves.clear();
                        outScoreMoves.add(newScoreMove);
                        return outScoreMoves;
                    }

                    outScoreMoves.add(newScoreMove);
                } else {
                    break;
                }
            }
        }

        assert (outScoreMoves.size() <= QUEEN_CASE);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> rookMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(ROOK_CASE);

        for (int moveType = 0; moveType < 4; ++moveType) {
            final int UP_VERTICAL_MOVE_TYPE = 0;
            final int DOWN_VERTICAL_MOVE_TYPE = 1;
            final int RIGHT_HORIZONTAL_MOVE_TYPE = 2;
            final int LEFT_HORIZONTAL_MOVE_TYPE = 3;

            for (int moveSize = 1; moveSize < BOARD_SIZE; ++moveSize) {
                final int rookMoveOffsetX;
                final int rookMoveOffsetY;
                switch (moveType) {
                    case UP_VERTICAL_MOVE_TYPE:
                        rookMoveOffsetX = 0;
                        rookMoveOffsetY = moveSize;
                        break;
                    case DOWN_VERTICAL_MOVE_TYPE:
                        rookMoveOffsetX = 0;
                        rookMoveOffsetY = -moveSize;
                        break;
                    case RIGHT_HORIZONTAL_MOVE_TYPE:
                        rookMoveOffsetX = moveSize;
                        rookMoveOffsetY = 0;
                        break;
                    case LEFT_HORIZONTAL_MOVE_TYPE:
                        rookMoveOffsetX = -moveSize;
                        rookMoveOffsetY = 0;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown queen move type");
                }

                final int toX = fromX + rookMoveOffsetX;
                final int toY = fromY + rookMoveOffsetY;

                if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = GreedyMiniMaxPlayer.getPieceScore(Character.toLowerCase(attackedPiece));

                    final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                    newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                    if (score == KING_SCORE) {
                        outScoreMoves.clear();
                        outScoreMoves.add(newScoreMove);
                        return outScoreMoves;
                    }

                    outScoreMoves.add(newScoreMove);
                } else {
                    break;
                }
            }
        }

        assert (outScoreMoves.size() <= ROOK_CASE);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> bishopMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(BISHOP_CASE);

        for (int moveType = 0; moveType < 4; ++moveType) {
            final int UP_RIGHT_CROSS_MOVE_TYPE = 0;
            final int UP_LEFT_CROSS_MOVE_TYPE = 1;
            final int DOWN_RIGHT_CROSS_MOVE_TYPE = 2;
            final int DOWN_LEFT_CROSS_MOVE_TYPE = 3;

            for (int moveSize = 1; moveSize < BOARD_SIZE; ++moveSize) {
                final int bishopMoveOffsetX;
                final int bishopMoveOffsetY;
                switch (moveType) {
                    case UP_RIGHT_CROSS_MOVE_TYPE:
                        bishopMoveOffsetX = moveSize;
                        bishopMoveOffsetY = moveSize;
                        break;
                    case UP_LEFT_CROSS_MOVE_TYPE:
                        bishopMoveOffsetX = -moveSize;
                        bishopMoveOffsetY = moveSize;
                        break;
                    case DOWN_RIGHT_CROSS_MOVE_TYPE:
                        bishopMoveOffsetX = moveSize;
                        bishopMoveOffsetY = -moveSize;
                        break;
                    case DOWN_LEFT_CROSS_MOVE_TYPE:
                        bishopMoveOffsetX = -moveSize;
                        bishopMoveOffsetY = -moveSize;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown queen move type");
                }

                final int toX = fromX + bishopMoveOffsetX;
                final int toY = fromY + bishopMoveOffsetY;

                if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = GreedyMiniMaxPlayer.getPieceScore(Character.toLowerCase(attackedPiece));

                    final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                    newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                    if (score == KING_SCORE) {
                        outScoreMoves.clear();
                        outScoreMoves.add(newScoreMove);
                        return outScoreMoves;
                    }

                    outScoreMoves.add(newScoreMove);
                } else {
                    break;
                }
            }
        }

        assert (outScoreMoves.size() <= BISHOP_CASE);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> knightMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(KNIGHT_CASE);

        for (final int[] knightMoveOffset : KNIGHT_MOVE_OFFSETS) {
            final int toX = fromX + knightMoveOffset[0];
            final int toY = fromY + knightMoveOffset[1];

            if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = GreedyMiniMaxPlayer.getPieceScore(Character.toLowerCase(attackedPiece));

                final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                if (score == KING_SCORE) {
                    outScoreMoves.clear();
                    outScoreMoves.add(newScoreMove);
                    return outScoreMoves;
                }

                outScoreMoves.add(newScoreMove);
            }
        }

        assert (outScoreMoves.size() <= KNIGHT_CASE);

        return outScoreMoves;
    }
}
