package academy.pocu.comp3500.assignment3;

import academy.pocu.comp3500.assignment3.chess.Move;
import academy.pocu.comp3500.assignment3.chess.PlayerBase;

import java.util.ArrayList;

public final class GreedyMiniMaxPlayer extends PlayerBase {
    private static final int MINI_MAX_DEPTH = 5;

    // DEPTH = 5;
    private static final int SCORE_MOVE_MEMORY_POOL_DEFAULT_SIZE = 315273;
    private static final int BOARD_MEMORY_POOL_DEFAULT_SIZE = 25473;

    private static int outMovesMaxSizeInGetCanMoves = 14;

    // --

    private final EColor color;

    private final ScoreMove bestScratchScoreMove;
    private final MemoryPool<ScoreMove> scoreMoveMemoryPool;
    private final ManualMemoryPool<char[][]> boardMemoryPool;

    // --

    public GreedyMiniMaxPlayer(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds);
        this.bestScratchScoreMove = new ScoreMove(-1, -1, -1, -1, 0, (char) 0);

        this.color = isWhite ? EColor.WHITE : EColor.BLACK;

        try {
            this.scoreMoveMemoryPool = new MemoryPool<ScoreMove>(ScoreMove.class.getDeclaredConstructor(), SCORE_MOVE_MEMORY_POOL_DEFAULT_SIZE);

            this.boardMemoryPool = new ManualMemoryPool<char[][]>();
            this.initBoardMemoryPool(BOARD_MEMORY_POOL_DEFAULT_SIZE);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("can not getDeclaredConstructor");
        }
    }

    // --

    public void printMemoryPoolSize() {
        System.out.println("GreedyMiniMaxPlayer");
        System.out.println("scoreMoveMemoryPool.poolSize() : " + scoreMoveMemoryPool.poolSize());
        System.out.println("boardMemoryPool.poolSize() : " + boardMemoryPool.poolSize());

        System.out.println("outMovesMaxSizeInGetCanMoves : " + outMovesMaxSizeInGetCanMoves);

        System.out.println();
    }

    public Move getNextMove(final char[][] board) {
        return getNextMove(board, null);
    }

    public Move getNextMove(final char[][] board, final Move opponentMove) {
        assert (board.length == Chess.BOARD_SIZE);
        assert (board[0].length == Chess.BOARD_SIZE);
        assert (this.scoreMoveMemoryPool.getNextIndex() == 0);
        assert (this.boardMemoryPool.getNextIndex() == 0);

        final EColor opponent = this.color == EColor.WHITE ? EColor.BLACK : EColor.WHITE;

        ScoreMove move = getBestMoveRecursive(board,
                this.color,
                opponent,
                this.color,
                1,
                MINI_MAX_DEPTH);

        this.scoreMoveMemoryPool.resetNextIndex();
        this.boardMemoryPool.resetNextIndex();

        return new Move(move.fromX(), move.fromY(), move.toX(), move.toY());
    }

    // --

    private ScoreMove getBestMoveRecursive(final char[][] board, final EColor player, final EColor opponent, final EColor turn, final int turnCount, final int maxTurnCount) {
        assert (board.length == Chess.BOARD_SIZE);
        assert (board[0].length == Chess.BOARD_SIZE);
        assert (turnCount >= 1);
        assert (turnCount <= maxTurnCount);

        if (Chess.hasWon(board, opponent)) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, -Chess.KING_SCORE * 2);
            return newScoreMove;
        }

        if (Chess.hasWon(board, player)) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, Chess.KING_SCORE * 2);
            return newScoreMove;
        }

        final ArrayList<ScoreMove> canMoveList = getBestMoves(board, turn);
        if (canMoveList.isEmpty()) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, 0);
            return newScoreMove;
        }

        for (final ScoreMove canMove : canMoveList) {
            if (turnCount == maxTurnCount) {
                final char tempToPiece = board[canMove.toY()][canMove.toX()];
                board[canMove.toY()][canMove.toX()] = board[canMove.fromY()][canMove.fromX()];
                board[canMove.fromY()][canMove.fromX()] = 0;

                canMove.init(canMove.fromX(), canMove.fromY(), canMove.toX(), canMove.toY(), Chess.calculateBoardPoint(board, player));

                board[canMove.fromY()][canMove.fromX()] = board[canMove.toY()][canMove.toX()];
                board[canMove.toY()][canMove.toX()] = tempToPiece;

                continue;
            }

            final char[][] newBoard = this.getNextBoard();
            Chess.copyBoard(board, newBoard);

            newBoard[canMove.toY()][canMove.toX()] = newBoard[canMove.fromY()][canMove.fromX()];
            newBoard[canMove.fromY()][canMove.fromX()] = 0;

            final EColor nextPlayer = turn == player
                    ? opponent : player;

            final int score = getBestMoveRecursive(newBoard,
                    player,
                    opponent,
                    nextPlayer,
                    turnCount + 1,
                    maxTurnCount).score();

            canMove.init(canMove.fromX(), canMove.fromY(), canMove.toX(), canMove.toY(), score, canMove.piece());
        }

        if (turn == player) {
            return Chess.getMaxScoreMove(canMoveList);
        }

        return Chess.getMinScoreMove(canMoveList);
    }

    private ArrayList<ScoreMove> getBestMoves(final char[][] board, final EColor turn) {
        final ArrayList<ScoreMove> outMoves = new ArrayList<ScoreMove>(GreedyMiniMaxPlayer.outMovesMaxSizeInGetCanMoves);

        ScoreMove pieceMove;
        for (int y = 0; y < Chess.BOARD_SIZE; ++y) {
            for (int x = 0; x < Chess.BOARD_SIZE; ++x) {
                if (board[y][x] == 0) {
                    continue;
                }

                if ((turn == EColor.WHITE) != Character.isLowerCase(board[y][x])) {
                    continue;
                }

                final char symbol = board[y][x];
                switch (Character.toLowerCase(symbol)) {
                    case 'p':
                        pieceMove = getPawnBestMoveOrNull(board, x, y, turn);
                        break;
                    case 'k':
                        pieceMove = getKingCanMoveOrNull(board, x, y, turn);
                        break;
                    case 'q':
                        pieceMove = getQueenCanMoveOrNull(board, x, y, turn);
                        break;
                    case 'r':
                        pieceMove = getRookCanMoveOrNull(board, x, y, turn);
                        break;
                    case 'b':
                        pieceMove = getBishopCanMoveOrNull(board, x, y, turn);
                        break;
                    case 'n':
                        pieceMove = getKnightCanMoveOrNull(board, x, y, turn);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown piece symbol");
                }

                if (pieceMove == null) {
                    continue;
                }

                assert (Chess.isMoveValid(board, turn, pieceMove.fromX(), pieceMove.fromY(), pieceMove.toX(), pieceMove.toY()));

                if (pieceMove.score() == Chess.KING_SCORE) {
                    outMoves.clear();
                    outMoves.add(pieceMove);
                    return outMoves;
                }

                outMoves.add(pieceMove);
            }
        }

        assert (outMoves.isEmpty() == false);

        if (GreedyMiniMaxPlayer.outMovesMaxSizeInGetCanMoves < outMoves.size()) {
            GreedyMiniMaxPlayer.outMovesMaxSizeInGetCanMoves = outMoves.size();
        }

        return outMoves;
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private ScoreMove getPawnBestMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (final int[] pawnAttackOffset : Chess.PAWN_MOVE_OFFSETS) {
            final int toX = fromX + pawnAttackOffset[0];
            final int toY = fromY + pawnAttackOffset[1];

            if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = Chess.getPieceScore(attackedPiece);
                if (bestScore <= score) {
                    bestScore = score;
                    this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                    if (score == Chess.KING_SCORE) {
                        break force_break;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        final ScoreMove out = this.scoreMoveMemoryPool.getNext();
        out.init(this.bestScratchScoreMove);
        return out;
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private ScoreMove getKingCanMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (final int[] kingMoveOffset : Chess.KING_MOVE_OFFSETS) {
            final int toX = fromX + kingMoveOffset[0];
            final int toY = fromY + kingMoveOffset[1];

            if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = Chess.getPieceScore(attackedPiece);
                if (bestScore < score) {
                    bestScore = score;
                    this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                    if (score == Chess.KING_SCORE) {
                        break force_break;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        final ScoreMove out = this.scoreMoveMemoryPool.getNext();
        out.init(this.bestScratchScoreMove);
        return out;
    }

    private ScoreMove getQueenCanMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
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

            for (int moveSize = 1; moveSize < Chess.BOARD_SIZE; ++moveSize) {
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
                if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));
                    if (bestScore < score) {
                        bestScore = score;
                        this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                        if (score == Chess.KING_SCORE) {
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

        final ScoreMove out = this.scoreMoveMemoryPool.getNext();
        out.init(this.bestScratchScoreMove);
        return out;
    }

    private ScoreMove getRookCanMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (int moveType = 0; moveType < 4; ++moveType) {
            final int UP_VERTICAL_MOVE_TYPE = 0;
            final int DOWN_VERTICAL_MOVE_TYPE = 1;
            final int RIGHT_HORIZONTAL_MOVE_TYPE = 2;
            final int LEFT_HORIZONTAL_MOVE_TYPE = 3;

            for (int moveSize = 1; moveSize < Chess.BOARD_SIZE; ++moveSize) {
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
                        throw new IllegalArgumentException("Unknown rook move type");
                }

                final int toX = fromX + rookMoveOffsetX;
                final int toY = fromY + rookMoveOffsetY;

                if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));
                    if (bestScore < score) {
                        bestScore = score;
                        this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                        if (score == Chess.KING_SCORE) {
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

        final ScoreMove out = this.scoreMoveMemoryPool.getNext();
        out.init(this.bestScratchScoreMove);
        return out;
    }

    private ScoreMove getBishopCanMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (int moveType = 0; moveType < 4; ++moveType) {
            final int UP_RIGHT_CROSS_MOVE_TYPE = 0;
            final int UP_LEFT_CROSS_MOVE_TYPE = 1;
            final int DOWN_RIGHT_CROSS_MOVE_TYPE = 2;
            final int DOWN_LEFT_CROSS_MOVE_TYPE = 3;

            for (int moveSize = 1; moveSize < Chess.BOARD_SIZE; ++moveSize) {
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
                        throw new IllegalArgumentException("Unknown bishop move type");
                }

                final int toX = fromX + bishopMoveOffsetX;
                final int toY = fromY + bishopMoveOffsetY;

                if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));
                    if (score == Chess.KING_SCORE)
                        if (bestScore < score) {
                            bestScore = score;
                            this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                            if (score == Chess.KING_SCORE) {
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

        final ScoreMove out = this.scoreMoveMemoryPool.getNext();
        out.init(this.bestScratchScoreMove);
        return out;
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private ScoreMove getKnightCanMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        int bestScore = -1;
        force_break:
        for (final int[] knightMoveOffset : Chess.KNIGHT_MOVE_OFFSETS) {
            final int toX = fromX + knightMoveOffset[0];
            final int toY = fromY + knightMoveOffset[1];

            if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));
                if (bestScore < score) {
                    bestScore = score;
                    this.bestScratchScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);
                    if (score == Chess.KING_SCORE) {
                        break force_break;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        final ScoreMove out = this.scoreMoveMemoryPool.getNext();
        out.init(this.bestScratchScoreMove);
        return out;
    }

    // init ManualMemoryPool<char[][]>
    private void initBoardMemoryPool(final int size) {
        for (int i = 0; i < size; ++i) {
            this.boardMemoryPool.addPool(new char[Chess.BOARD_SIZE][Chess.BOARD_SIZE]);
        }
    }

    // getNext ManualMemoryPool<char[][]>
    private char[][] getNextBoard() {
        char[][] temp = this.boardMemoryPool.getNextOrNull();
        if (temp == null) {
            this.boardMemoryPool.addPool(new char[Chess.BOARD_SIZE][Chess.BOARD_SIZE]);
            temp = this.boardMemoryPool.getNextOrNull();
        }

        assert (temp != null);
        return temp;
    }
}
