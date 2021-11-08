package academy.pocu.comp3500.assignment3;

import academy.pocu.comp3500.assignment3.chess.Move;
import academy.pocu.comp3500.assignment3.chess.PlayerBase;

import java.util.ArrayList;

public final class Player extends PlayerBase {
    private static final int BOARD_SIZE = 8;

    private static final int[][] KING_MOVE_OFFSETS = {
            {-1, 1},
            {-1, 0},
            {-1, -1},
            {0, 1},
            {0, -1},
            {1, 1},
            {1, 0},
            {1, -1}
    };

    private static final int[][] KNIGHT_MOVE_OFFSETS = {
            {-2, -1},
            {-2, 1},
            {-1, -2},
            {-1, 2},
            {1, -2},
            {1, 2},
            {2, -1},
            {2, 1}
    };

    private static final int[][] PAWN_MOVE_OFFSETS = {
            {0, -1},
            {0, -2},
            {0, 1},
            {0, 2},
            {-1, -1},
            {1, -1},
            {-1, 1},
            {1, 1}
    };

    private static final int KING_SCORE = 100000000;
    private static final int QUEEN_SCORE = 1000;
    private static final int ROOK_SCORE = 700;
    private static final int BISHOP_SCORE = 500;
    private static final int KNIGHT_SCORE = 300;
    private static final int PAWN_SCORE = 100;

    private static final int DEPTH = 4;

//    public int scoreMoveNum;
//    public int movesArrayListNum;
//    public int moveNum;
//    public static int boardNum;

    private final EColor color;
    private ScoreMove scratchMove;
    private ScoreMove bestMove;
    private Move resultMove;

    public Player(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds);

//        scoreMoveNum = 0;
//        movesArrayListNum = 0;
//        moveNum = 0;
//        boardNum = 0;

        this.color = isWhite ? EColor.WHITE : EColor.BLACK;
        this.scratchMove = new ScoreMove();
        this.bestMove = new ScoreMove();
        this.resultMove = new Move();
    }

    public Move getNextMove(final char[][] board) {
        return getNextMove(board, null);
    }

    public Move getNextMove(final char[][] board, final Move opponentMove) {
        assert (board.length == BOARD_SIZE);
        assert (board[0].length == BOARD_SIZE);

        final EColor opponent = this.color == EColor.WHITE ? EColor.BLACK : EColor.WHITE;

        ScoreMove move = getBestMoveRecursive(board,
                this.color,
                opponent,
                this.color,
                1,
                Player.DEPTH);

        resultMove.fromX = move.fromX;
        resultMove.fromY = move.fromY;
        resultMove.toX = move.toX;
        resultMove.toY = move.toY;

        return resultMove;
    }

    private ScoreMove getBestMoveRecursive(final char[][] board, final EColor player, final EColor opponent, final EColor turn, final int turnCount, final int maxTurnCount) {
        assert (board.length == BOARD_SIZE);
        assert (board[0].length == BOARD_SIZE);

        if (turnCount >= maxTurnCount) {
//            scoreMoveNum++;
            return new ScoreMove(-1, -1, -1, -1, calculateBoardPoint(board, player));
        }

        if (hasWon(board, opponent)) {
//            scoreMoveNum++;
            return new ScoreMove(-1, -1, -1, -1, -KING_SCORE);
        }

        if (hasWon(board, player)) {
//            scoreMoveNum++;
            return new ScoreMove(-1, -1, -1, -1, KING_SCORE);
        }

        final ArrayList<Move> canMoveList = getCanMoveList(board, player);
        if (canMoveList.isEmpty()) {
//            scoreMoveNum++;
            return new ScoreMove(-1, -1, -1, -1, 0);
        }

//        movesArrayListNum++;
        final ArrayList<ScoreMove> moves = new ArrayList<>();

        for (final Move canMove : canMoveList) {
            final char[][] newBoard = createCopy(board);
            newBoard[canMove.toY][canMove.toX] = newBoard[canMove.fromY][canMove.fromX];
            newBoard[canMove.fromY][canMove.fromX] = 0;

            final EColor nextPlayer = turn == player
                    ? opponent : player;

            int score = getBestMoveRecursive(newBoard,
                    player,
                    opponent,
                    nextPlayer,
                    turnCount + 1,
                    maxTurnCount).score;

//            scoreMoveNum++;
            ScoreMove move = new ScoreMove(canMove.fromX, canMove.fromY, canMove.toX, canMove.toY, score);
            moves.add(move);
        }

        if (turn == player) {
            return getMaxScoreMove(moves);
        }

        return getMinScoreMove(moves);
    }

    private static int getPieceScore(final char attackedPiece) {
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

    private ArrayList<Move> getCanMoveList(final char[][] board, final EColor player) {
//        movesArrayListNum++;
        ArrayList<Move> moves = new ArrayList<>();

        ScoreMove move;
        for (int y = 0; y < BOARD_SIZE; ++y) {
            for (int x = 0; x < BOARD_SIZE; ++x) {
                if (board[y][x] == 0) {
                    continue;
                }

                final char symbol = board[y][x];
                switch (Character.toLowerCase(symbol)) {
                    case 'p':
                        move = pawnMoveOrNull(board, x, y, player);
                        break;
                    case 'k':
                        move = kingMoveOrNull(board, x, y, player);
                        break;
                    case 'q':
                        move = queenMoveOrNull(board, x, y, player);
                        break;
                    case 'r':
                        move = rookMoveOrNull(board, x, y, player);
                        break;
                    case 'b':
                        move = bishopMoveOrNull(board, x, y, player);
                        break;
                    case 'n':
                        move = knightMoveOrNull(board, x, y, player);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown piece symbol");
                }

                if (move == null) {
                    continue;
                }

                assert (isMoveValid(board, player, move.fromX, move.fromY, move.toX, move.toY));

                if (move.score == KING_SCORE) {
                    moves.clear();
                    moves.add(new Move(move.fromX, move.fromY, move.toX, move.toY));
                    return moves;
                }

//                moveNum++;
                moves.add(new Move(move.fromX, move.fromY, move.toX, move.toY));
            }
        }

        return moves;
    }

    private ScoreMove pawnMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor player) {
        int bestScore = -1;
        force_break:
        for (final int[] pawnAttackOffset : PAWN_MOVE_OFFSETS) {
            final int toX = fromX + pawnAttackOffset[0];
            final int toY = fromY + pawnAttackOffset[1];

            if (isMoveValid(board, player, fromX, fromY, toX, toY)) {
                this.scratchMove.fromX = fromX;
                this.scratchMove.fromY = fromY;
                this.scratchMove.toX = toX;
                this.scratchMove.toY = toY;

                final char attackedPiece = board[toY][toX];
                final int score = Player.getPieceScore(Character.toLowerCase(attackedPiece));
                if (score == KING_SCORE) {
                    bestScore = score;
                    final ScoreMove temp = this.bestMove;
                    this.bestMove = this.scratchMove;
                    this.scratchMove = temp;
                    break force_break;
                } else {
                    if (bestScore < score) {
                        bestScore = score;
                        final ScoreMove temp = this.bestMove;
                        this.bestMove = this.scratchMove;
                        this.scratchMove = temp;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }

        this.bestMove.score = bestScore;
        return this.bestMove;
    }

    private ScoreMove kingMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor player) {
        int bestScore = -1;
        force_break:
        for (final int[] kingMoveOffset : KING_MOVE_OFFSETS) {
            final int toX = fromX + kingMoveOffset[0];
            final int toY = fromY + kingMoveOffset[1];

            if (isMoveValid(board, player, fromX, fromY, toX, toY)) {
                this.scratchMove.fromX = fromX;
                this.scratchMove.fromY = fromY;
                this.scratchMove.toX = toX;
                this.scratchMove.toY = toY;

                final char attackedPiece = board[toY][toX];
                final int score = Player.getPieceScore(Character.toLowerCase(attackedPiece));
                if (score == KING_SCORE) {
                    bestScore = score;
                    final ScoreMove temp = this.bestMove;
                    this.bestMove = this.scratchMove;
                    this.scratchMove = temp;
                    break force_break;
                } else {
                    if (bestScore < score) {
                        bestScore = score;
                        final ScoreMove temp = this.bestMove;
                        this.bestMove = this.scratchMove;
                        this.scratchMove = temp;
                    }
                }
            }
        }

        if (bestScore == -1) {
            return null;
        }
        this.bestMove.score = bestScore;
        return this.bestMove;
    }

    private ScoreMove queenMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor player) {
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
                if (isMoveValid(board, player, fromX, fromY, toX, toY)) {
                    this.scratchMove.fromX = fromX;
                    this.scratchMove.fromY = fromY;
                    this.scratchMove.toX = toX;
                    this.scratchMove.toY = toY;

                    final char attackedPiece = board[toY][toX];
                    final int score = Player.getPieceScore(Character.toLowerCase(attackedPiece));
                    if (score == KING_SCORE) {
                        bestScore = score;
                        final ScoreMove temp = this.bestMove;
                        this.bestMove = this.scratchMove;
                        this.scratchMove = temp;
                        break force_break;
                    } else {
                        if (bestScore < score) {
                            bestScore = score;
                            final ScoreMove temp = this.bestMove;
                            this.bestMove = this.scratchMove;
                            this.scratchMove = temp;
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

        this.bestMove.score = bestScore;
        return this.bestMove;
    }

    private ScoreMove rookMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor player) {
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

                if (isMoveValid(board, player, fromX, fromY, toX, toY)) {
                    this.scratchMove.fromX = fromX;
                    this.scratchMove.fromY = fromY;
                    this.scratchMove.toX = toX;
                    this.scratchMove.toY = toY;

                    final char attackedPiece = board[toY][toX];
                    final int score = Player.getPieceScore(Character.toLowerCase(attackedPiece));
                    if (score == KING_SCORE) {
                        bestScore = score;
                        final ScoreMove temp = this.bestMove;
                        this.bestMove = this.scratchMove;
                        this.scratchMove = temp;
                        break force_break;
                    } else {
                        if (bestScore < score) {
                            bestScore = score;
                            final ScoreMove temp = this.bestMove;
                            this.bestMove = this.scratchMove;
                            this.scratchMove = temp;
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

        this.bestMove.score = bestScore;
        return this.bestMove;
    }

    private ScoreMove bishopMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor player) {
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

                if (isMoveValid(board, player, fromX, fromY, toX, toY)) {
                    this.scratchMove.fromX = fromX;
                    this.scratchMove.fromY = fromY;
                    this.scratchMove.toX = toX;
                    this.scratchMove.toY = toY;

                    final char attackedPiece = board[toY][toX];
                    final int score = Player.getPieceScore(Character.toLowerCase(attackedPiece));
                    if (score == KING_SCORE) {
                        bestScore = score;
                        final ScoreMove temp = this.bestMove;
                        this.bestMove = this.scratchMove;
                        this.scratchMove = temp;
                        break force_break;
                    } else {
                        if (bestScore < score) {
                            bestScore = score;
                            final ScoreMove temp = this.bestMove;
                            this.bestMove = this.scratchMove;
                            this.scratchMove = temp;
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

        this.bestMove.score = bestScore;
        return this.bestMove;
    }

    private ScoreMove knightMoveOrNull(final char[][] board, final int fromX, final int fromY, final EColor player) {
        int bestScore = -1;
        force_break:
        for (final int[] knightMoveOffset : KNIGHT_MOVE_OFFSETS) {
            final int toX = fromX + knightMoveOffset[0];
            final int toY = fromY + knightMoveOffset[1];

            if (isMoveValid(board, player, fromX, fromY, toX, toY)) {
                this.scratchMove.fromX = fromX;
                this.scratchMove.fromY = fromY;
                this.scratchMove.toX = toX;
                this.scratchMove.toY = toY;

                final char attackedPiece = board[toY][toX];
                final int score = Player.getPieceScore(Character.toLowerCase(attackedPiece));
                if (score == KING_SCORE) {
                    bestScore = score;
                    final ScoreMove temp = this.bestMove;
                    this.bestMove = this.scratchMove;
                    this.scratchMove = temp;
                    break force_break;
                } else {
                    if (bestScore < score) {
                        bestScore = score;
                        final ScoreMove temp = this.bestMove;
                        this.bestMove = this.scratchMove;
                        this.scratchMove = temp;
                    }
                }
            }

        }

        if (bestScore == -1) {
            return null;
        }

        this.bestMove.score = bestScore;
        return this.bestMove;
    }

    private static boolean hasWon(final char[][] board, final EColor player) {
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

    private static ScoreMove getMaxScoreMove(final ArrayList<ScoreMove> moves) {
        assert (!moves.isEmpty());

        ScoreMove bestMove = moves.get(0);
        for (int i = 1; i < moves.size(); ++i) {
            if (moves.get(i).score > bestMove.score) {
                bestMove = moves.get(i);
            }
        }

        return bestMove;
    }

    private static ScoreMove getMinScoreMove(final ArrayList<ScoreMove> moves) {
        assert (!moves.isEmpty());

        ScoreMove bestMove = moves.get(0);
        for (final ScoreMove move : moves) {
            if (move.score < bestMove.score) {
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int calculateBoardPoint(final char[][] board, final EColor player) {
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

    private static boolean isMoveValid(char[][] board, EColor player, final int fromX, final int fromY, final int toX, final int toY) {
        if (fromX >= BOARD_SIZE || fromX < 0
                || fromY >= BOARD_SIZE || fromY < 0) {
            return false;
        }

        final char symbol = board[fromY][fromX];

        if (symbol == 0) {
            return false;
        }

        if ((player == EColor.WHITE && !Character.isLowerCase(symbol))
                || player != EColor.WHITE && Character.isLowerCase(symbol)) {
            return false;
        }

        if (toX >= BOARD_SIZE || toX < 0
                || toY >= BOARD_SIZE || toY < 0) {
            return false;
        }

        if (fromX == toX && fromY == toY) {
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

    private static char[][] createCopy(final char[][] board) {
        assert (board.length == BOARD_SIZE);
        assert (board[0].length == BOARD_SIZE);

//        boardNum++;
        final char[][] copy = new char[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                copy[i][j] = board[i][j];
            }
        }

        return copy;
    }

    private static boolean isBishopMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
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

    private static boolean isRookMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
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

    private static boolean isKnightMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
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

    private static boolean isQueenMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
        return isBishopMoveValid(board, fromX, fromY, toX, toY) || isRookMoveValid(board, fromX, fromY, toX, toY);
    }

    private static boolean isKingMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
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

    private static boolean isPawnMoveValid(char[][] board, final int fromX, final int fromY, final int toX, final int toY) {
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
