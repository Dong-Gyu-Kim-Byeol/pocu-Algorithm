package academy.pocu.comp3500.assignment3;

import academy.pocu.comp3500.assignment3.chess.Move;
import academy.pocu.comp3500.assignment3.chess.PlayerBase;

import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class Player extends PlayerBase {
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

    private static final int[][] WHITE_PAWN_MOVE_OFFSETS = {
            {0, -1},
            {0, -2}
    };
    private static final int[][] WHITE_PAWN_ATTACK_OFFSETS = {
            {-1, -1},
            {1, -1}
    };

    private static final int[][] BLACK_PAWN_MOVE_OFFSETS = {
            {0, 1},
            {0, 2}
    };
    private static final int[][] BLACK_PAWN_ATTACK_OFFSETS = {
            {-1, 1},
            {1, 1}
    };

    private final static int BOARD_SIZE = 8;
    private final static char[] PIECES = {'p', 'r', 'b', 'q', 'n', 'k'};

    private final Random random = new Random();

    // char position : (char) ((y << 4) ^ x) // char position == (char) -1 : die
    // char piece : (char) (symbol(piece) << 4 ^ number)
    private final char[][] board;
    private final HashMap<Character, Character> myPositionsFromPieces;
    private final HashMap<Character, Character> opponentPositionsFromPieces;

    private Move scratchMove;
    private Move myMove;
    private final Stack<Move> moveRecoveryStack;
    private final Stack<Move> moveBestStack;

    private final int[][] MY_PAWN_MOVE_OFFSETS;
    private final int[][] MY_PAWN_ATTACK_OFFSETS;


    public Player(final boolean isWhite, final int maxMoveTimeMilliseconds) {
        super(isWhite, maxMoveTimeMilliseconds);

        this.myPositionsFromPieces = new HashMap<Character, Character>();
//        this.myPiecesFromPositions = new HashMap<Character, Character>();

        this.opponentPositionsFromPieces = new HashMap<Character, Character>();
//        this.opponentPiecesFromPositions = new HashMap<Character, Character>();

        this.myMove = new Move();
        this.scratchMove = new Move();
        this.moveRecoveryStack = new Stack<Move>();
        this.moveBestStack = new Stack<Move>();

        this.MY_PAWN_MOVE_OFFSETS = this.isWhite() ? WHITE_PAWN_MOVE_OFFSETS : BLACK_PAWN_MOVE_OFFSETS;
        this.MY_PAWN_ATTACK_OFFSETS = this.isWhite() ? WHITE_PAWN_ATTACK_OFFSETS : BLACK_PAWN_ATTACK_OFFSETS;

        this.board = this.createNewBoard();
//        this.printMap();
    }

    private void printMap() {
        if (this.isWhite()) {
            System.out.println(" W ");
        } else {
            System.out.println(" B ");
        }

        for (final char pos : myPositionsFromPieces.values()) {
            final char piece = this.board[pos >> 4][pos & 0x0f];
            System.out.print((char) (piece >> 4));
            System.out.print(" ");
            System.out.print(piece & 0x0f);

            System.out.print("  ");
            System.out.print((int) (pos >> 4));
            System.out.print(" ");
            System.out.println(pos & 0x0f);
        }
    }

    public Move getNextMove(final char[][] board) {
        return getNextMove(board, null);
    }

    private int calculateScore(final char attackedPiece) {
        switch (attackedPiece) {
            case 'k': {
                return Integer.MAX_VALUE;
            }

            case 'q': {
                return 500;
            }

            case 'r': {
                return 300;
            }

            case 'b': {
                return 200;
            }

            case 'n': {
                return 100;
            }

            case 'p': {
                return 50;
            }

            default:
                return 0;
        }
    }

    private boolean isOpponentPiece(final char piece) {
        if (piece == 0) {
            return false;
        }

        if (this.isWhite()) {
            return Character.isUpperCase(piece);
        } else {
            return Character.isLowerCase(piece);
        }
    }

    private boolean isMyPiece(final char piece) {
        if (piece == 0) {
            return false;
        }

        if (this.isWhite()) {
            return Character.isLowerCase(piece);
        } else {
            return Character.isUpperCase(piece);
        }
    }

    private void movePiece(final boolean isOpponentMove, final Move move) {
        final HashMap<Character, Character> moveMap = isOpponentMove ? this.opponentPositionsFromPieces : this.myPositionsFromPieces;
        final HashMap<Character, Character> deleteMap = !isOpponentMove ? this.opponentPositionsFromPieces : this.myPositionsFromPieces;

        final char movePiece = this.board[move.fromY][move.fromX];
        this.board[move.fromY][move.fromX] = 0;

        final char deletePiece = this.board[move.toY][move.toX];
        if (this.isMyPiece((char) (deletePiece >> 4))) {
            deleteMap.put(deletePiece, (char) -1);
        }

        this.board[move.toY][move.toX] = movePiece;
        moveMap.put(movePiece, (char) (move.toY << 4 ^ move.toX));
    }

    public Move getNextMove(final char[][] board, final Move opponentMove) {
        if (opponentMove != null) {
            movePiece(true, opponentMove);
        }


        int bestScore = Integer.MIN_VALUE;
        force_break:
        for (char switchPiece : Player.PIECES) {
            switch (switchPiece) {
                case 'p': {
                    switchPiece = this.isWhite() ? switchPiece : Character.toUpperCase(switchPiece);
                    for (int i = 1; i <= 8; i++) {
                        final char piece = (char) (switchPiece << 4 ^ i);
                        final char pos = myPositionsFromPieces.get(piece);
                        if (pos == (char) -1) {
                            continue;
                        }

                        final int fromX = pos & 0x0f;
                        final int fromY = pos >> 4;

                        // pawn attack
                        for (final int[] pawnAttackOffset : MY_PAWN_ATTACK_OFFSETS) {
                            final int toX = fromX + pawnAttackOffset[0];
                            final int toY = fromY + pawnAttackOffset[1];

                            if (this.isMoveValid(fromX, fromY, toX, toY)) {
                                scratchMove.fromX = fromX;
                                scratchMove.fromY = fromY;
                                scratchMove.toX = toX;
                                scratchMove.toY = toY;

                                final char attackedPiece = (char) (this.board[toY][toX] >> 4);
                                assert (this.isOpponentPiece(attackedPiece));
                                final int score = this.calculateScore(Character.toLowerCase(attackedPiece));
                                if (score == Integer.MAX_VALUE) {
                                    bestScore = score;
                                    final Move temp = this.myMove;
                                    this.myMove = this.scratchMove;
                                    this.scratchMove = temp;
                                    break force_break;
                                } else {
                                    if (bestScore < score) {
                                        bestScore = score;
                                        final Move temp = this.myMove;
                                        this.myMove = this.scratchMove;
                                        this.scratchMove = temp;
                                    }
                                }
                            }
                        }

                        // pawn move
                        for (final int[] pawnMoveOffset : MY_PAWN_MOVE_OFFSETS) {
                            final int toX = fromX + pawnMoveOffset[0];
                            final int toY = fromY + pawnMoveOffset[1];

                            if (this.isMoveValid(fromX, fromY, toX, toY)) {
                                this.scratchMove.fromX = fromX;
                                this.scratchMove.fromY = fromY;
                                this.scratchMove.toX = toX;
                                this.scratchMove.toY = toY;

                                final char emptyPiece = (char) (this.board[toY][toX] >> 4);
                                assert (emptyPiece == 0);
                                final int score = this.calculateScore(Character.toLowerCase(emptyPiece));
                                assert (score == 0);
                                if (bestScore < score) {
                                    bestScore = score;
                                    final Move temp = this.myMove;
                                    this.myMove = this.scratchMove;
                                    this.scratchMove = temp;
                                }
                            }
                        }
                    }

                    break;
                }

                case 'k': {
                    switchPiece = this.isWhite() ? switchPiece : Character.toUpperCase(switchPiece);
                    final char piece = (char) (switchPiece << 4 ^ 1);
                    final char pos = myPositionsFromPieces.get(piece);
                    assert (pos != 0);
                    if (pos == (char) -1) {
                        continue;
                    }

                    final int fromX = pos & 0x0f;
                    final int fromY = pos >> 4;
                    // king
                    for (final int[] kingMoveOffset : KING_MOVE_OFFSETS) {
                        final int toX = fromX + kingMoveOffset[0];
                        final int toY = fromY + kingMoveOffset[1];

                        if (this.isMoveValid(fromX, fromY, toX, toY)) {
                            this.scratchMove.fromX = fromX;
                            this.scratchMove.fromY = fromY;
                            this.scratchMove.toX = toX;
                            this.scratchMove.toY = toY;

                            final char attackedPiece = (char) (this.board[toY][toX] >> 4);
                            assert (this.isOpponentPiece(attackedPiece) || attackedPiece == 0);
                            final int score = this.calculateScore(Character.toLowerCase(attackedPiece));
                            if (score == Integer.MAX_VALUE) {
                                bestScore = score;
                                final Move temp = this.myMove;
                                this.myMove = this.scratchMove;
                                this.scratchMove = temp;
                                break force_break;
                            } else {
                                if (bestScore < score) {
                                    bestScore = score;
                                    final Move temp = this.myMove;
                                    this.myMove = this.scratchMove;
                                    this.scratchMove = temp;
                                }
                            }
                        }
                    }

                    break;
                }

                case 'q': {
                    switchPiece = this.isWhite() ? switchPiece : Character.toUpperCase(switchPiece);
                    final char piece = (char) (switchPiece << 4 ^ 1);
                    final char pos = myPositionsFromPieces.get(piece);
                    assert (pos != 0);
                    if (pos == (char) -1) {
                        continue;
                    }

                    final int fromX = pos & 0x0f;
                    final int fromY = pos >> 4;
                    // queen
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
                            if (this.isMoveValid(fromX, fromY, toX, toY)) {
                                this.scratchMove.fromX = fromX;
                                this.scratchMove.fromY = fromY;
                                this.scratchMove.toX = toX;
                                this.scratchMove.toY = toY;

                                final char attackedPiece = (char) (this.board[toY][toX] >> 4);
                                assert (this.isOpponentPiece(attackedPiece) || attackedPiece == 0);
                                final int score = this.calculateScore(Character.toLowerCase(attackedPiece));
                                if (score == Integer.MAX_VALUE) {
                                    bestScore = score;
                                    final Move temp = this.myMove;
                                    this.myMove = this.scratchMove;
                                    this.scratchMove = temp;
                                    break force_break;
                                } else {
                                    if (bestScore < score) {
                                        bestScore = score;
                                        final Move temp = this.myMove;
                                        this.myMove = this.scratchMove;
                                        this.scratchMove = temp;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }

                    break;
                }

                case 'r': {
                    switchPiece = this.isWhite() ? switchPiece : Character.toUpperCase(switchPiece);
                    for (int i = 1; i <= 2; i++) {
                        final char piece = (char) (switchPiece << 4 ^ i);
                        final char pos = myPositionsFromPieces.get(piece);
                        if (pos == (char) -1) {
                            continue;
                        }

                        final int fromX = pos & 0x0f;
                        final int fromY = pos >> 4;

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

                                if (this.isMoveValid(fromX, fromY, toX, toY)) {
                                    this.scratchMove.fromX = fromX;
                                    this.scratchMove.fromY = fromY;
                                    this.scratchMove.toX = toX;
                                    this.scratchMove.toY = toY;

                                    final char attackedPiece = (char) (this.board[toY][toX] >> 4);
                                    assert (this.isOpponentPiece(attackedPiece) || attackedPiece == 0);
                                    final int score = this.calculateScore(Character.toLowerCase(attackedPiece));
                                    if (score == Integer.MAX_VALUE) {
                                        bestScore = score;
                                        final Move temp = this.myMove;
                                        this.myMove = this.scratchMove;
                                        this.scratchMove = temp;
                                        break force_break;
                                    } else {
                                        if (bestScore < score) {
                                            bestScore = score;
                                            final Move temp = this.myMove;
                                            this.myMove = this.scratchMove;
                                            this.scratchMove = temp;
                                        }
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }

                    break;
                }

                case 'b': {
                    switchPiece = this.isWhite() ? switchPiece : Character.toUpperCase(switchPiece);
                    for (int i = 1; i <= 2; i++) {
                        final char piece = (char) (switchPiece << 4 ^ i);
                        final char pos = myPositionsFromPieces.get(piece);
                        if (pos == (char) -1) {
                            continue;
                        }

                        final int fromX = pos & 0x0f;
                        final int fromY = pos >> 4;

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

                                if (this.isMoveValid(fromX, fromY, toX, toY)) {
                                    this.scratchMove.fromX = fromX;
                                    this.scratchMove.fromY = fromY;
                                    this.scratchMove.toX = toX;
                                    this.scratchMove.toY = toY;

                                    final char attackedPiece = (char) (this.board[toY][toX] >> 4);
                                    assert (this.isOpponentPiece(attackedPiece) || attackedPiece == 0);
                                    final int score = this.calculateScore(Character.toLowerCase(attackedPiece));
                                    if (score == Integer.MAX_VALUE) {
                                        bestScore = score;
                                        final Move temp = this.myMove;
                                        this.myMove = this.scratchMove;
                                        this.scratchMove = temp;
                                        break force_break;
                                    } else {
                                        if (bestScore < score) {
                                            bestScore = score;
                                            final Move temp = this.myMove;
                                            this.myMove = this.scratchMove;
                                            this.scratchMove = temp;
                                        }
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }

                    break;
                }

                case 'n': {
                    switchPiece = this.isWhite() ? switchPiece : Character.toUpperCase(switchPiece);
                    for (int i = 1; i <= 2; i++) {
                        final char piece = (char) (switchPiece << 4 ^ i);
                        final char pos = myPositionsFromPieces.get(piece);
                        if (pos == (char) -1) {
                            continue;
                        }

                        final int fromX = pos & 0x0f;
                        final int fromY = pos >> 4;

                        for (final int[] knightMoveOffset : KNIGHT_MOVE_OFFSETS) {
                            final int toX = fromX + knightMoveOffset[0];
                            final int toY = fromY + knightMoveOffset[1];

                            if (this.isMoveValid(fromX, fromY, toX, toY)) {
                                this.scratchMove.fromX = fromX;
                                this.scratchMove.fromY = fromY;
                                this.scratchMove.toX = toX;
                                this.scratchMove.toY = toY;

                                final char attackedPiece = (char) (this.board[toY][toX] >> 4);
                                assert (this.isOpponentPiece(attackedPiece) || attackedPiece == 0);
                                final int score = this.calculateScore(Character.toLowerCase(attackedPiece));
                                if (score == Integer.MAX_VALUE) {
                                    bestScore = score;
                                    final Move temp = this.myMove;
                                    this.myMove = this.scratchMove;
                                    this.scratchMove = temp;
                                    break force_break;
                                } else {
                                    if (bestScore < score) {
                                        bestScore = score;
                                        final Move temp = this.myMove;
                                        this.myMove = this.scratchMove;
                                        this.scratchMove = temp;
                                    }
                                }
                            }
                        }
                    }

                    break;
                }

                default:
                    throw new IllegalArgumentException("Unknown queen move type");
            }
        }


        assert (isMoveValid(this.myMove.fromX, this.myMove.fromY, this.myMove.toX, this.myMove.toY));
        movePiece(false, this.myMove);

        return this.myMove;

    }

    private boolean isMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        if (fromX >= BOARD_SIZE || fromX < 0
                || fromY >= BOARD_SIZE || fromY < 0) {
            return false;
        }

        final char symbol = (char) (board[fromY][fromX] >> 4);
        assert (this.isOpponentPiece(symbol) || this.isMyPiece(symbol));

        if (symbol == 0) {
            return false;
        }

        if ((this.isWhite() && Character.isUpperCase(symbol))
                || this.isWhite() == false && Character.isLowerCase(symbol)) {
            return false;
        }

        if (toX >= BOARD_SIZE || toX < 0
                || toY >= BOARD_SIZE || toY < 0) {
            return false;
        }

        if (fromX == toX && fromY == toY) {
            return false;
        }

        final char toSymbol = (char) (board[toY][toX] >> 4);
        if (Character.isLowerCase(symbol) && Character.isLowerCase(toSymbol)
                || Character.isUpperCase(symbol) && Character.isUpperCase(toSymbol)) {
            return false;
        }

        char symbolInvariant = Character.toLowerCase(symbol);

        switch (symbolInvariant) {
            case 'p':
                return isPawnMoveValid(fromX, fromY, toX, toY);

            case 'n':
                return isKnightMoveValid(fromX, fromY, toX, toY);

            case 'b':
                return isBishopMoveValid(fromX, fromY, toX, toY);

            case 'r':
                return isRookMoveValid(fromX, fromY, toX, toY);

            case 'q':
                return isQueenMoveValid(fromX, fromY, toX, toY);

            case 'k':
                return isKingMoveValid(fromX, fromY, toX, toY);

            default:
                throw new IllegalArgumentException("Unknown piece symbol");
        }
    }

    private void setPiecesPositionsHashMaps(final boolean isWhite, final char piece, final char position) {
        if (this.isWhite() == isWhite) {
//            this.myPiecesFromPositions.put(position, piece);
            this.myPositionsFromPieces.put(piece, position);
        } else {
//            this.opponentPiecesFromPositions.put(position, piece);
            this.opponentPositionsFromPieces.put(piece, position);
        }
    }

    private char[][] createNewBoard() {
        final char[][] board = new char[BOARD_SIZE][BOARD_SIZE];

        // White pieces
        char y = BOARD_SIZE - 1;
        board[y][0] = 'r' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(true, board[y][0], (char) (y << 4)); // y << 4 ^ 0
        board[y][1] = 'n' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(true, board[y][1], (char) (y << 4 ^ 1));
        board[y][2] = 'b' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(true, board[y][2], (char) (y << 4 ^ 2));

        board[y][3] = 'k' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(true, board[y][3], (char) (y << 4 ^ 3));
        board[y][4] = 'q' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(true, board[y][4], (char) (y << 4 ^ 4));

        board[y][5] = 'b' << 4 ^ 2;
        this.setPiecesPositionsHashMaps(true, board[y][5], (char) (y << 4 ^ 5));
        board[y][6] = 'n' << 4 ^ 2;
        this.setPiecesPositionsHashMaps(true, board[y][6], (char) (y << 4 ^ 6));
        board[y][7] = 'r' << 4 ^ 2;
        this.setPiecesPositionsHashMaps(true, board[y][7], (char) (y << 4 ^ 7));

        // White pawns
        y -= 1;
        for (char x = 0; x < BOARD_SIZE; ++x) {
            board[y][x] = (char) ('p' << 4 ^ x + 1);
            this.setPiecesPositionsHashMaps(true, board[y][x], (char) (y << 4 ^ x));
        }

        // Black pawns
        y = 1;
        for (int x = 0; x < BOARD_SIZE; ++x) {
            board[y][x] = (char) ('P' << 4 ^ x + 1);
            this.setPiecesPositionsHashMaps(false, board[y][x], (char) (y << 4 ^ x));
        }

        // Black pieces
        y = 0;
        board[y][0] = 'R' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(false, board[y][0], (char) (0)); // y << 4 ^ 0
        board[y][1] = 'N' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(false, board[y][1], (char) (1)); // y << 4 ^ 1
        board[y][2] = 'B' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(false, board[y][2], (char) (2)); // y << 4 ^ 2

        board[y][3] = 'K' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(false, board[y][3], (char) (3)); // y << 4 ^ 3
        board[y][4] = 'Q' << 4 ^ 1;
        this.setPiecesPositionsHashMaps(false, board[y][4], (char) (4)); // y << 4 ^ 4

        board[y][5] = 'B' << 4 ^ 2;
        this.setPiecesPositionsHashMaps(false, board[y][5], (char) (5)); // y << 4 ^ 5
        board[y][6] = 'N' << 4 ^ 2;
        this.setPiecesPositionsHashMaps(false, board[y][6], (char) (6)); // y << 4 ^ 6
        board[y][7] = 'R' << 4 ^ 2;
        this.setPiecesPositionsHashMaps(false, board[y][7], (char) (7)); // y << 4 ^ 7

        return board;
    }

    private boolean isBishopMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = (char) (board[fromY][fromX] >> 4);
        char toPiece = (char) (board[toY][toX] >> 4);

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
            if (board[y][x] != 0) {
                return false;
            }

            x += xIncrement;
            y += yIncrement;
        }

        return true;
    }

    private boolean isRookMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = (char) (board[fromY][fromX] >> 4);
        char toPiece = (char) (board[toY][toX] >> 4);

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

    private boolean isKnightMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = (char) (board[fromY][fromX] >> 4);
        char toPiece = (char) (board[toY][toX] >> 4);

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

    private boolean isQueenMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        return isBishopMoveValid(fromX, fromY, toX, toY) || isRookMoveValid(fromX, fromY, toX, toY);
    }

    private boolean isKingMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = (char) (board[fromY][fromX] >> 4);
        char toPiece = (char) (board[toY][toX] >> 4);

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

    private boolean isPawnMoveValid(final int fromX, final int fromY, final int toX, final int toY) {
        char fromPiece = (char) (board[fromY][fromX] >> 4);
        char toPiece = (char) (board[toY][toX] >> 4);

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
