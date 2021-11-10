package academy.pocu.comp3500.assignment3;

import academy.pocu.comp3500.assignment3.chess.Move;
import academy.pocu.comp3500.assignment3.chess.PlayerBase;

import java.util.ArrayList;

public final class Player extends PlayerBase {
    private static final int MINI_MAX_DEPTH = 4;

    // DEPTH = 4
    private static final int SCORE_MOVE_MEMORY_POOL_DEFAULT_SIZE = 107217;
    private static final int BOARD_MEMORY_POOL_DEFAULT_SIZE = 52528;

    private static int outMovesMaxSizeInGetCanMoves = 52;
    private static int outScoreMovesMaxSizeInGetQueenCanMoves = 24;
    private static int outScoreMovesMaxSizeInGetRookCanMoves = 14;
    private static int outScoreMovesMaxSizeInGetBishopCanMoves = 13;

    private final EColor color;

    private final MemoryPool<ScoreMove> scoreMoveMemoryPool;
    private final ManualMemoryPool<char[][]> boardMemoryPool;

    public Player(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds);

        this.color = isWhite ? EColor.WHITE : EColor.BLACK;

        try {
            this.scoreMoveMemoryPool = new MemoryPool<ScoreMove>(ScoreMove.class.getDeclaredConstructor(), SCORE_MOVE_MEMORY_POOL_DEFAULT_SIZE);

            this.boardMemoryPool = new ManualMemoryPool<char[][]>();
            this.initBoardMemoryPool(BOARD_MEMORY_POOL_DEFAULT_SIZE);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("can not getDeclaredConstructor");
        }
    }

    public void printMemoryPoolSize() {
        System.out.println("Player");
        System.out.println("scoreMoveMemoryPool.poolSize() : " + scoreMoveMemoryPool.poolSize());
        System.out.println("boardMemoryPool.poolSize() : " + boardMemoryPool.poolSize());

        System.out.println("outMovesMaxSizeInGetCanMoves : " + outMovesMaxSizeInGetCanMoves);
        System.out.println("outScoreMovesMaxSizeInGetQueenCanMoves : " + outScoreMovesMaxSizeInGetQueenCanMoves);
        System.out.println("outScoreMovesMaxSizeInGetRookCanMoves : " + outScoreMovesMaxSizeInGetRookCanMoves);
        System.out.println("outScoreMovesMaxSizeInGetBishopCanMoves : " + outScoreMovesMaxSizeInGetBishopCanMoves);

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

    private ScoreMove getBestMoveRecursive(final char[][] board, final EColor player, final EColor opponent, final EColor turn, final int turnCount, final int maxTurnCount) {
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
            newScoreMove.init(-1, -1, -1, -1, -Chess.KING_SCORE * 2);
            return newScoreMove;
        }

        if (Chess.hasWon(board, player)) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, Chess.KING_SCORE * 2);
            return newScoreMove;
        }

        final ArrayList<ScoreMove> canMoveList = getCanMoves(board, turn);
        if (canMoveList.isEmpty()) {
            final ScoreMove newScoreMove = this.scoreMoveMemoryPool.getNext();
            newScoreMove.init(-1, -1, -1, -1, 0);
            return newScoreMove;
        }

        for (final ScoreMove canMove : canMoveList) {
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

    private ArrayList<ScoreMove> getCanMoves(final char[][] board, final EColor turn) {
        final ArrayList<ScoreMove> outMoves = new ArrayList<ScoreMove>(Player.outMovesMaxSizeInGetCanMoves);

        ArrayList<ScoreMove> pieceCanMoves;
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
                        pieceCanMoves = getPawnCanMoves(board, x, y, turn);
                        break;
                    case 'k':
                        pieceCanMoves = getKingCanMoves(board, x, y, turn);
                        break;
                    case 'q':
                        pieceCanMoves = getQueenCanMoves(board, x, y, turn);
                        break;
                    case 'r':
                        pieceCanMoves = getRookCanMoves(board, x, y, turn);
                        break;
                    case 'b':
                        pieceCanMoves = getBishopCanMoves(board, x, y, turn);
                        break;
                    case 'n':
                        pieceCanMoves = getKnightCanMoves(board, x, y, turn);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown piece symbol");
                }

                if (pieceCanMoves.size() == 0) {
                    continue;
                }

                for (final ScoreMove pieceCanMove : pieceCanMoves) {
                    assert (Chess.isMoveValid(board, turn, pieceCanMove.fromX(), pieceCanMove.fromY(), pieceCanMove.toX(), pieceCanMove.toY()));

                    if (pieceCanMove.score() == Chess.KING_SCORE) {
                        outMoves.clear();
                        outMoves.add(pieceCanMove);
                        return outMoves;
                    }

                    outMoves.add(pieceCanMove);
                }
            }
        }

        assert (outMoves.isEmpty() == false);

        if (Player.outMovesMaxSizeInGetCanMoves < outMoves.size()) {
            Player.outMovesMaxSizeInGetCanMoves = outMoves.size();
        }

        return outMoves;
    }

    private ArrayList<ScoreMove> getPawnCanMoves(final char[][] board, final int fromX, final int fromY, final EColor turn) {
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

    private ArrayList<ScoreMove> getKingCanMoves(final char[][] board, final int fromX, final int fromY, final EColor turn) {
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

    private ArrayList<ScoreMove> getQueenCanMoves(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(Player.outScoreMovesMaxSizeInGetQueenCanMoves);

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

        if (Player.outScoreMovesMaxSizeInGetQueenCanMoves < outScoreMoves.size()) {
            Player.outScoreMovesMaxSizeInGetQueenCanMoves = outScoreMoves.size();
        }

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> getRookCanMoves(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(Player.outScoreMovesMaxSizeInGetRookCanMoves);

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

        if (Player.outScoreMovesMaxSizeInGetRookCanMoves < outScoreMoves.size()) {
            Player.outScoreMovesMaxSizeInGetRookCanMoves = outScoreMoves.size();
        }

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> getBishopCanMoves(final char[][] board, final int fromX, final int fromY, final EColor turn) {
        ArrayList<ScoreMove> outScoreMoves = new ArrayList<ScoreMove>(Player.outScoreMovesMaxSizeInGetBishopCanMoves);

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

        if (Player.outScoreMovesMaxSizeInGetBishopCanMoves < outScoreMoves.size()) {
            Player.outScoreMovesMaxSizeInGetBishopCanMoves = outScoreMoves.size();
        }

        return outScoreMoves;
    }

    private ArrayList<ScoreMove> getKnightCanMoves(final char[][] board, final int fromX, final int fromY, final EColor turn) {
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

