package academy.pocu.comp3500.assignment3.app;

import academy.pocu.comp3500.assignment3.Player;
import academy.pocu.comp3500.assignment3.chess.PlayerBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Program {

    public static void main(String[] args) {
        final boolean IS_AUTO_PLAY = true; // true 라면 주기적으로 자동으로 다음 턴이 진행됨; false 라면 Enter/Return 키를 누를 때 진행됨
        final boolean IS_WHITE_KEYBOARD_PLAYER = false; // true 라면 하얀색 플레이어의 수를 콘솔에 입력해야 함
        final boolean IS_BLACK_KEYBOARD_PLAYER = false; // true 라면 검은색 플레이어의 수를 콘솔에 입력해야 함

        final int MAX_MOVE_TIME_MILLISECONDS = 1000; // Player 가 턴마다 수를 결정하는 데에 주어진 시간
        final long AUTO_PLAY_TURN_DURATION_IN_MILLISECONDS = 1000; // Autoplay 중 턴마다 일시중지 되는 기간

        PlayerBase whitePlayer;
        PlayerBase blackPlayer;

        if (IS_WHITE_KEYBOARD_PLAYER) {
            whitePlayer = new KeyboardPlayer(true);
        } else {
            whitePlayer = new Player(true, MAX_MOVE_TIME_MILLISECONDS);
        }
        if (IS_BLACK_KEYBOARD_PLAYER) {
            blackPlayer = new KeyboardPlayer(false);
        } else {
            blackPlayer = new Player(false, MAX_MOVE_TIME_MILLISECONDS);
        }

        Game game = new Game(whitePlayer, blackPlayer, MAX_MOVE_TIME_MILLISECONDS);

        System.out.println("Let the game begin!");
        System.out.println(game.toString());

        for (int i = 0; i < 50; ++i) {
            if (game.isNextTurnWhite() && IS_BLACK_KEYBOARD_PLAYER
                    || !game.isNextTurnWhite() && IS_WHITE_KEYBOARD_PLAYER) {
                if (IS_AUTO_PLAY) {
                    pause(AUTO_PLAY_TURN_DURATION_IN_MILLISECONDS);
                } else {
                    continueOnEnter();
                }
            }

            game.nextTurn();

            clearConsole();
            System.out.println(game.toString());

            if (game.isGameOver()) {
                break;
            }
        }
    }

    public static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void continueOnEnter() {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Press enter to continue:");
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}