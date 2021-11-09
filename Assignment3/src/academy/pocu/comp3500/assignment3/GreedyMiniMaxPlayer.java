package academy.pocu.comp3500.assignment3;

import java.util.ArrayList;

public final class GreedyMiniMaxPlayer extends ChessPlayerBase {
    private static final int DEPTH = 4;

    private final ScoreMove bestScratchScoreMove;

    public GreedyMiniMaxPlayer(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds, DEPTH);
        this.bestScratchScoreMove = new ScoreMove(-1, -1, -1, -1, 0, (char) 0);
    }

    @Override
    protected ArrayList<CompactMove> getCanMoveList(final char[][] board, final EColor turn) {
        ArrayList<CompactMove> moves = new ArrayList<CompactMove>(TOTAL_CASE);

        ScoreMove temp;
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
                        temp = pawnMoveOrNull(board, x, y, turn);
                        break;
                    case 'k':
                        temp = kingMoveOrNull(board, x, y, turn);
                        break;
                    case 'q':
                        temp = queenMoveOrNull(board, x, y, turn);
                        break;
                    case 'r':
                        temp = rookMoveOrNull(board, x, y, turn);
                        break;
                    case 'b':
                        temp = bishopMoveOrNull(board, x, y, turn);
                        break;
                    case 'n':
                        temp = knightMoveOrNull(board, x, y, turn);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown piece symbol");
                }

                if (temp == null) {
                    continue;
                }

                assert (isMoveValid(board, turn, temp.fromX(), temp.fromY(), temp.toX(), temp.toY()));

                final CompactMove newMove = this.compactMoveMemoryPool.getNext();
                newMove.init(temp.fromX(), temp.fromY(), temp.toX(), temp.toY());

                if (temp.score() == KING_SCORE) {
                    moves.clear();
                    moves.add(newMove);
                    return moves;
                }

                moves.add(newMove);
            }
        }

        assert (moves.size() <= TOTAL_CASE);

        return moves;
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private ScoreMove pawnMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (final int[] pawnAttackOffset : PAWN_MOVE_OFFSETS) {
            final int toX = fromX + pawnAttackOffset[0];
            final int toY = fromY + pawnAttackOffset[1];

            if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = ChessPlayerBase.getPieceScore(attackedPiece);
                if (bestScore <= score) {
                    bestScore = score;
                    this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                    if (score == KING_SCORE) {
                        break force_break;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        return this.bestScratchScoreMove;
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private ScoreMove kingMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (final int[] kingMoveOffset : KING_MOVE_OFFSETS) {
            final int toX = fromX + kingMoveOffset[0];
            final int toY = fromY + kingMoveOffset[1];

            if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = GreedyMiniMaxPlayer.getPieceScore(attackedPiece);
                if (bestScore < score) {
                    bestScore = score;
                    this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                    if (score == KING_SCORE) {
                        break force_break;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        return this.bestScratchScoreMove;
    }

    private ScoreMove queenMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
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
                    if (bestScore < score) {
                        bestScore = score;
                        this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                        if (score == KING_SCORE) {
                            break force_break;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        return this.bestScratchScoreMove;
    }

    private ScoreMove rookMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
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
                    if (bestScore < score) {
                        bestScore = score;
                        this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                        if (score == KING_SCORE) {
                            break force_break;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        return this.bestScratchScoreMove;
    }

    private ScoreMove bishopMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
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
                    if (score == KING_SCORE)
                        if (bestScore < score) {
                            bestScore = score;
                            this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                            if (score == KING_SCORE) {
                                break force_break;
                            }
                        }
                } else {
                    break;
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        return this.bestScratchScoreMove;
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private ScoreMove knightMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (final int[] knightMoveOffset : KNIGHT_MOVE_OFFSETS) {
            final int toX = fromX + knightMoveOffset[0];
            final int toY = fromY + knightMoveOffset[1];

            if (isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = GreedyMiniMaxPlayer.getPieceScore(Character.toLowerCase(attackedPiece));
                if (bestScore < score) {
                    bestScore = score;
                    this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                    if (score == KING_SCORE) {
                        break force_break;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        return this.bestScratchScoreMove;
    }
}
