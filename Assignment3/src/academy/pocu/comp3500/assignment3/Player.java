package academy.pocu.comp3500.assignment3;

import academy.pocu.comp3500.assignment3.chess.Move;
import academy.pocu.comp3500.assignment3.chess.PlayerBase;

import java.util.ArrayList;

public class Player extends PlayerBase {
    private static final int DEPTH = 4;

    // DEPTH = 4
    private static final int COMPACT_MOVE_MEMORY_POOL_DEFAULT_SIZE = 55240;
    private static final int SCORE_MOVE_MEMORY_POOL_DEFAULT_SIZE = 159745;
    private static final int BOARD_MEMORY_POOL_DEFAULT_SIZE = 52528;

    private int outMovesMaxSizeInGetCanMoveList = 52;
    private int outScoreMovesMaxSizeInQueenMoveOrNull = 24;
    private int outScoreMovesMaxSizeInRookMoveOrNull = 14;
    private int outScoreMovesMaxSizeInBishopMoveOrNull = 12;

    protected final EColor color;
    protected final Move resultMove;

    protected final MemoryPool<CompactMove> compactMoveMemoryPool;
    protected final MemoryPool<ScoreMove> scoreMoveMemoryPool;
    private final ManualMemoryPool<char[][]> boardMemoryPool;

    public Player(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds);

        this.color = isWhite ? EColor.WHITE : EColor.BLACK;
        this.resultMove = new Move();

        try {
            this.compactMoveMemoryPool = new MemoryPool<CompactMove>(CompactMove.class.getDeclaredConstructor(), COMPACT_MOVE_MEMORY_POOL_DEFAULT_SIZE);
            this.scoreMoveMemoryPool = new MemoryPool<ScoreMove>(ScoreMove.class.getDeclaredConstructor(), SCORE_MOVE_MEMORY_POOL_DEFAULT_SIZE);

            this.boardMemoryPool = new ManualMemoryPool<char[][]>();
            ManualMemoryPool.init(this.boardMemoryPool, Chess.BOARD_SIZE, Chess.BOARD_SIZE, BOARD_MEMORY_POOL_DEFAULT_SIZE);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("can not getDeclaredConstructor");
        }
    }

    public void printMemoryPoolSize() {
        System.out.println("Player");
        System.out.println("compactMoveMemoryPool.poolSize() : " + compactMoveMemoryPool.poolSize());
        System.out.println("scoreMoveMemoryPool.poolSize() : " + scoreMoveMemoryPool.poolSize());
        System.out.println("boardMemoryPool.poolSize() : " + boardMemoryPool.poolSize());
        System.out.println();
    }

    public final Move getNextMove(final char[][] board) {
        return getNextMove(board, null);
    }

    public final Move getNextMove(final char[][] board, final Move opponentMove) {
        assert (board.length == Chess.BOARD_SIZE);
        assert (board[0].length == Chess.BOARD_SIZE);

        this.compactMoveMemoryPool.resetNextIndex();
        this.scoreMoveMemoryPool.resetNextIndex();
        this.boardMemoryPool.resetNextIndex();

        final EColor opponent = this.color == EColor.WHITE ? EColor.BLACK : EColor.WHITE;

        ScoreMove move = getBestMoveRecursive(board,
                this.color,
                opponent,
                this.color,
                1,
                DEPTH);

        resultMove.fromX = move.fromX();
        resultMove.fromY = move.fromY();
        resultMove.toX = move.toX();
        resultMove.toY = move.toY();

        return resultMove;
    }

    protected final ScoreMove getBestMoveRecursive(final char[][] board, final EColor player, final EColor opponent, final EColor turn, final int turnCount, final int maxTurnCount) {
        assert (board.length == Chess.BOARD_SIZE);
        assert (board[0].length == Chess.BOARD_SIZE);
        assert (turnCount >= 1);

        if (turnCount >= maxTurnCount) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, Chess.calculateBoardPoint(board, player));
            return newScoreMove;
        }

        if (Chess.hasWon(board, opponent)) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, -Chess.KING_SCORE);
            return newScoreMove;
        }

        if (Chess.hasWon(board, player)) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, Chess.KING_SCORE);
            return newScoreMove;
        }

        final ArrayList<CompactMove> canMoveList = getCanMoveList(board, turn);
        if (canMoveList.isEmpty()) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, 0);
            return newScoreMove;
        }

        final ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(canMoveList.size());

        for (final CompactMove canMove : canMoveList) {
            final char[][] newBoard = ManualMemoryPool.getNext(this.boardMemoryPool, Chess.BOARD_SIZE, Chess.BOARD_SIZE);
            Chess.copyBoard(board, newBoard);

            newBoard[canMove.toY()][canMove.toX()] = newBoard[canMove.fromY()][canMove.fromX()];
            newBoard[canMove.fromY()][canMove.fromX()] = 0;

            final EColor nextPlayer = turn == player
                    ? opponent : player;

            int score = getBestMoveRecursive(newBoard,
                    player,
                    opponent,
                    nextPlayer,
                    turnCount + 1,
                    maxTurnCount).score();

            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(canMove.fromX(), canMove.fromY(), canMove.toX(), canMove.toY(), score, newBoard[canMove.toY()][canMove.toX()]);
            outScoreMoves.add(newScoreMove);
        }

        if (turn == player) {
            return Chess.getMaxScoreMove(outScoreMoves);
        }

        return Chess.getMinScoreMove(outScoreMoves);
    }

    protected final ArrayList<CompactMove> getCanMoveList(final char[][] board, final EColor turn) {
        final ArrayList<CompactMove> outMoves = new ArrayList<CompactMove>(this.outMovesMaxSizeInGetCanMoveList);

        ArrayList<ScoreMove> temps;
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
                    assert (Chess.isMoveValid(board, turn, temp.fromX(), temp.fromY(), temp.toX(), temp.toY()));

                    final CompactMove newMove = this.compactMoveMemoryPool.getNext();
                    newMove.init(temp.fromX(), temp.fromY(), temp.toX(), temp.toY());

                    if (temp.score() == Chess.KING_SCORE) {
                        outMoves.clear();
                        outMoves.add(newMove);
                        return outMoves;
                    }

                    outMoves.add(newMove);
                }
            }
        }

        if (this.outMovesMaxSizeInGetCanMoveList < outMoves.size()) {
            this.outMovesMaxSizeInGetCanMoveList = outMoves.size();
        }

        return outMoves;
    }

    private ArrayList<ScoreMove> pawnMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(Chess.PAWN_MOVE_OFFSETS.length);

        for (final int[] pawnAttackOffset : Chess.PAWN_MOVE_OFFSETS) {
            final int toX = fromX + pawnAttackOffset[0];
            final int toY = fromY + pawnAttackOffset[1];

            if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = Chess.getPieceScore(attackedPiece);

                final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                if (score == Chess.KING_SCORE) {
                    outScoreMoves.clear();
                    outScoreMoves.add(newScoreMove);
                    return outScoreMoves;
                }

                outScoreMoves.add(newScoreMove);
            }
        }

        assert (outScoreMoves.size() <= Chess.PAWN_MOVE_OFFSETS.length);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> kingMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(Chess.KING_MOVE_OFFSETS.length);

        for (final int[] kingMoveOffset : Chess.KING_MOVE_OFFSETS) {
            final int toX = fromX + kingMoveOffset[0];
            final int toY = fromY + kingMoveOffset[1];

            if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = Chess.getPieceScore(attackedPiece);

                final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                if (score == Chess.KING_SCORE) {
                    outScoreMoves.clear();
                    outScoreMoves.add(newScoreMove);
                    return outScoreMoves;
                }

                outScoreMoves.add(newScoreMove);
            }
        }

        assert (outScoreMoves.size() <= Chess.KING_MOVE_OFFSETS.length);

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> queenMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(this.outScoreMovesMaxSizeInQueenMoveOrNull);

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

                    final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                    newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                    if (score == Chess.KING_SCORE) {
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

        if (this.outScoreMovesMaxSizeInQueenMoveOrNull < outScoreMoves.size()) {
            this.outScoreMovesMaxSizeInQueenMoveOrNull = outScoreMoves.size();
        }

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> rookMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(this.outScoreMovesMaxSizeInRookMoveOrNull);

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
                        throw new IllegalArgumentException("Unknown queen move type");
                }

                final int toX = fromX + rookMoveOffsetX;
                final int toY = fromY + rookMoveOffsetY;

                if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));

                    final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                    newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                    if (score == Chess.KING_SCORE) {
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

        if (this.outScoreMovesMaxSizeInRookMoveOrNull < outScoreMoves.size()) {
            this.outScoreMovesMaxSizeInRookMoveOrNull = outScoreMoves.size();
        }

        return outScoreMoves;
    }


    private ArrayList<ScoreMove> bishopMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(this.outScoreMovesMaxSizeInBishopMoveOrNull);

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
                        throw new IllegalArgumentException("Unknown queen move type");
                }

                final int toX = fromX + bishopMoveOffsetX;
                final int toY = fromY + bishopMoveOffsetY;

                if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                    final char attackedPiece = board[toY][toX];
                    final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));

                    final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                    newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                    if (score == Chess.KING_SCORE) {
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

        if (this.outScoreMovesMaxSizeInBishopMoveOrNull < outScoreMoves.size()) {
            this.outScoreMovesMaxSizeInBishopMoveOrNull = outScoreMoves.size();
        }

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> knightMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(Chess.KNIGHT_MOVE_OFFSETS.length);

        for (final int[] knightMoveOffset : Chess.KNIGHT_MOVE_OFFSETS) {
            final int toX = fromX + knightMoveOffset[0];
            final int toY = fromY + knightMoveOffset[1];

            if (Chess.isMoveValid(board, turn, fromX, fromY, toX, toY)) {
                final char attackedPiece = board[toY][toX];
                final int score = Chess.getPieceScore(Character.toLowerCase(attackedPiece));

                final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
                newScoreMove.init(fromX, fromY, toX, toY, score, board[fromY][fromX]);

                if (score == Chess.KING_SCORE) {
                    outScoreMoves.clear();
                    outScoreMoves.add(newScoreMove);
                    return outScoreMoves;
                }

                outScoreMoves.add(newScoreMove);
            }
        }

        assert (outScoreMoves.size() <= Chess.KNIGHT_MOVE_OFFSETS.length);

        return outScoreMoves;
    }
}
