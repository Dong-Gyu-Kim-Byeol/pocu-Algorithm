package academy.pocu.comp3500.assignment1.app;

import academy.pocu.comp3500.assignment1.PocuBasketballAssociation;
import academy.pocu.comp3500.assignment1.Sort;
import academy.pocu.comp3500.assignment1.pba.GameStat;
import academy.pocu.comp3500.assignment1.pba.Player;

import java.util.Comparator;
import java.util.Random;

public class Program {

    private static void testIncludeComment() {
        // write your code here

        {
            GameStat[] gameStats = new GameStat[]{
                    new GameStat("Player 1", 1, 13, 5, 6, 10, 1),
                    new GameStat("Player 2", 2, 5, 2, 5, 0, 10),
                    new GameStat("Player 1", 3, 12, 6, 9, 8, 5),
                    new GameStat("Player 3", 1, 31, 15, 40, 5, 3),
                    new GameStat("Player 2", 1, 3, 1, 3, 12, 2),
                    new GameStat("Player 1", 2, 11, 6, 11, 9, 3),
                    new GameStat("Player 2", 3, 9, 3, 3, 1, 11),
                    new GameStat("Player 3", 4, 32, 15, 51, 4, 2),
                    new GameStat("Player 4", 3, 44, 24, 50, 1, 1),
                    new GameStat("Player 1", 4, 11, 5, 14, 8, 3),
                    new GameStat("Player 2", 4, 5, 1, 3, 1, 9),
            };

            Player[] players = new Player[]{
                    new Player(),
                    new Player(),
                    new Player(),
                    new Player()
            };

            PocuBasketballAssociation.processGameStats(gameStats, players);
/*
players: [
    { "Player 2", pointsPerGame: 5, assistsPerGame: 3, passesPerGame: 8, shootingPercentage: 50 },
    { "Player 1", pointsPerGame: 11, assistsPerGame: 8, passesPerGame: 3, shootingPercentage: 55 },
    { "Player 4", pointsPerGame: 44, assistsPerGame: 1, passesPerGame: 1, shootingPercentage: 48 },
    { "Player 3", pointsPerGame: 31, assistsPerGame: 4, passesPerGame: 2, shootingPercentage: 32 }
]
*/
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 1", 1, 5, 1, 60),
                    new Player("Player 2", 5, 2, 11, 31),
                    new Player("Player 3", 7, 4, 7, 44),
                    new Player("Player 4", 10, 10, 15, 25),
                    new Player("Player 5", 11, 12, 6, 77),
                    new Player("Player 6", 15, 0, 12, 61),
                    new Player("Player 7", 16, 8, 2, 70)
            };

            Player player = PocuBasketballAssociation.findPlayerPointsPerGame(players, 12); // player: Player 5

            player = PocuBasketballAssociation.findPlayerPointsPerGame(players, 5); // player: Player 2
            player = PocuBasketballAssociation.findPlayerPointsPerGame(players, 13); // player: Player 6
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 4", 10, 10, 15, 25),
                    new Player("Player 2", 5, 2, 11, 31),
                    new Player("Player 3", 7, 4, 7, 44),
                    new Player("Player 1", 1, 5, 1, 60),
                    new Player("Player 6", 15, 0, 12, 61),
                    new Player("Player 7", 16, 8, 2, 70),
                    new Player("Player 5", 11, 12, 6, 77)
            };

            Player player = PocuBasketballAssociation.findPlayerShootingPercentage(players, 28); // player: Player 2

            player = PocuBasketballAssociation.findPlayerShootingPercentage(players, 58); // player: Player 1
            player = PocuBasketballAssociation.findPlayerShootingPercentage(players, 72); // player: Player 7
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 2", 5, 12, 14, 50), // 168
                    new Player("Player 6", 15, 2, 5, 40), // 10
                    new Player("Player 5", 11, 1, 11, 54), // 11
                    new Player("Player 4", 10, 3, 51, 88), // 153
                    new Player("Player 7", 16, 8, 5, 77), // 40
                    new Player("Player 1", 1, 15, 2, 22), // 30
                    new Player("Player 3", 7, 5, 8, 66) // 40
            };

            Player[] outPlayers = new Player[3];
            Player[] scratch = new Player[3];

            long maxTeamwork = PocuBasketballAssociation.find3ManDreamTeam(players, outPlayers, scratch); // maxTeamwork: 219, outPlayers: [ Player 4, Player 2, Player 3 ]
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 2", 5, 5, 17, 50),
                    new Player("Player 6", 15, 4, 10, 40),
                    new Player("Player 5", 11, 3, 25, 54),
                    new Player("Player 4", 10, 9, 1, 88),
                    new Player("Player 7", 16, 7, 5, 77),
                    new Player("Player 1", 1, 2, 8, 22),
                    new Player("Player 9", 42, 15, 4, 56),
                    new Player("Player 8", 33, 11, 3, 72),
            };

            int k = 4;
            Player[] outPlayers = new Player[4];
            Player[] scratch = new Player[k];

            long maxTeamwork = PocuBasketballAssociation.findDreamTeam(players, k, outPlayers, scratch); // maxTeamwork: 171, outPlayers: [ Player 6, Player 5, Player 2, Player 7 ]
        }

        Player[] players = new Player[]{
                new Player("Player 1", 2, 5, 10, 78), // 50
                new Player("Player 2", 10, 4, 5, 66), // 20
                new Player("Player 3", 3, 3, 2, 22), // 6
                new Player("Player 4", 1, 9, 8, 12), // 72
                new Player("Player 5", 11, 1, 12, 26), // 12
                new Player("Player 6", 7, 2, 10, 15), // 20
                new Player("Player 7", 8, 15, 3, 11), // 45
                new Player("Player 8", 5, 7, 13, 5), // 91
                new Player("Player 9", 8, 2, 7, 67), // 14
                new Player("Player 10", 1, 11, 1, 29), // 11
                new Player("Player 11", 2, 6, 9, 88) // 54
        };

        Player[] scratch = new Player[players.length];

        int k = PocuBasketballAssociation.findDreamTeamSize(players, scratch); // k: 6
    }

    public static void main(String[] args) {
        testIncludeComment();

        {
            GameStat[] gameStats = new GameStat[]{
                    new GameStat("Player 1", 1, 13, 5, 6, 10, 1),
                    new GameStat("Player 2", 2, 5, 2, 5, 0, 10),
                    new GameStat("Player 1", 3, 12, 6, 9, 8, 5),
                    new GameStat("Player 3", 1, 31, 15, 40, 5, 3),
                    new GameStat("Player 2", 1, 3, 1, 3, 12, 2),
                    new GameStat("Player 1", 2, 11, 6, 11, 9, 3),
                    new GameStat("Player 2", 3, 9, 3, 3, 1, 11),
                    new GameStat("Player 3", 4, 32, 15, 51, 4, 2),
                    new GameStat("Player 4", 3, 44, 24, 50, 1, 1),
                    new GameStat("Player 1", 4, 11, 5, 14, 8, 3),
                    new GameStat("Player 2", 4, 5, 1, 3, 1, 9),
            };

            Player[] players = new Player[]{
                    new Player(),
                    new Player(),
                    new Player(),
                    new Player()
            };

            PocuBasketballAssociation.processGameStats(gameStats, players);

            Player player = getPlayerOrNull(players, "Player 1");
            assert (player != null);
            assert (player.getPointsPerGame() == 11);
            assert (player.getAssistsPerGame() == 8);
            assert (player.getPassesPerGame() == 3);
            assert (player.getShootingPercentage() == 55);

            player = getPlayerOrNull(players, "Player 2");
            assert (player != null);
            assert (player.getPointsPerGame() == 5);
            assert (player.getAssistsPerGame() == 3);
            assert (player.getPassesPerGame() == 8);
            assert (player.getShootingPercentage() == 50);

            player = getPlayerOrNull(players, "Player 3");
            assert (player != null);
            assert (player.getPointsPerGame() == 31);
            assert (player.getAssistsPerGame() == 4);
            assert (player.getPassesPerGame() == 2);
            assert (player.getShootingPercentage() == 32);

            player = getPlayerOrNull(players, "Player 4");
            assert (player != null);
            assert (player.getPointsPerGame() == 44);
            assert (player.getAssistsPerGame() == 1);
            assert (player.getPassesPerGame() == 1);
            assert (player.getShootingPercentage() == 48);
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 1", 1, 5, 1, 60),
                    new Player("Player 2", 5, 2, 11, 31),
                    new Player("Player 3", 7, 4, 7, 44),
                    new Player("Player 4", 10, 10, 15, 25),
                    new Player("Player 5", 11, 12, 6, 77),
                    new Player("Player 6", 15, 0, 12, 61),
                    new Player("Player 7", 16, 8, 2, 70)
            };

            Player player = PocuBasketballAssociation.findPlayerPointsPerGame(players, 12);
            assert (player.getName().equals("Player 5"));

            player = PocuBasketballAssociation.findPlayerPointsPerGame(players, 5);
            assert (player.getName().equals("Player 2"));

            player = PocuBasketballAssociation.findPlayerPointsPerGame(players, 13);
            assert (player.getName().equals("Player 6"));
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 4", 10, 10, 15, 25),
                    new Player("Player 2", 5, 2, 11, 31),
                    new Player("Player 3", 7, 4, 7, 44),
                    new Player("Player 1", 1, 5, 1, 60),
                    new Player("Player 6", 15, 0, 12, 61),
                    new Player("Player 7", 16, 8, 2, 70),
                    new Player("Player 5", 11, 12, 6, 77)
            };

            Player player = PocuBasketballAssociation.findPlayerShootingPercentage(players, 28);
            assert (player.getName().equals("Player 2"));

            player = PocuBasketballAssociation.findPlayerShootingPercentage(players, 58);
            assert (player.getName().equals("Player 1"));

            player = PocuBasketballAssociation.findPlayerShootingPercentage(players, 72);
            assert (player.getName().equals("Player 7"));
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 2", 5, 12, 14, 50),
                    new Player("Player 6", 15, 2, 5, 40),
                    new Player("Player 5", 11, 1, 11, 54),
                    new Player("Player 4", 10, 3, 51, 88),
                    new Player("Player 7", 16, 8, 5, 77),
                    new Player("Player 1", 1, 15, 2, 22),
                    new Player("Player 3", 7, 5, 8, 66)
            };

//            players = new Player[]{
//                    new Player("Player 4", 10, 3, 51, 88),
//                    new Player("Player 2", 5, 12, 14, 50),
//
//                    //new Player("Player 5", 11, 1, 11, 54),
//
//                    new Player("Player 3", 7, 5, 8, 66),
//
//                    //new Player("Player 6", 15, 2, 5, 40),
//                    new Player("Player 7", 16, 8, 5, 77),
//
//                    new Player("Player 1", 1, 15, 2, 22),
//
//            };

            Player[] outPlayers = new Player[3];
            Player[] scratch = new Player[3];

            long maxTeamwork = PocuBasketballAssociation.find3ManDreamTeam(players, outPlayers, scratch);

            assert (maxTeamwork == 219);

            Player player = getPlayerOrNull(outPlayers, "Player 4");
            assert (player != null);

            player = getPlayerOrNull(outPlayers, "Player 2");
            assert (player != null);

            player = getPlayerOrNull(outPlayers, "Player 3");
            assert (player != null);
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 2", 5, 5, 17, 50),
                    new Player("Player 6", 15, 4, 10, 40),
                    new Player("Player 5", 11, 3, 25, 54),
                    new Player("Player 4", 10, 9, 1, 88),
                    new Player("Player 7", 16, 7, 5, 77),
                    new Player("Player 1", 1, 2, 8, 22),
                    new Player("Player 9", 42, 15, 4, 56),
                    new Player("Player 8", 33, 11, 3, 72),
            };

            final int TEAM_SIZE = 4;

            Player[] outPlayers = new Player[TEAM_SIZE];
            Player[] scratch = new Player[TEAM_SIZE];

            long maxTeamwork = PocuBasketballAssociation.findDreamTeam(players, TEAM_SIZE, outPlayers, scratch);

            assert (maxTeamwork == 171);

            Player player = getPlayerOrNull(outPlayers, "Player 5");
            assert (player != null);

            player = getPlayerOrNull(outPlayers, "Player 6");
            assert (player != null);

            player = getPlayerOrNull(outPlayers, "Player 2");
            assert (player != null);

            player = getPlayerOrNull(outPlayers, "Player 7");
            assert (player != null);
        }

        {
            // p 또는 a*p를 작은 순서대로 정렬 시
            Player[] players = new Player[]{
                    new Player("Player 1", 2, 10, 40, 78),
                    new Player("Player 2", 10, 11, 45, 66),
                    new Player("Player 3", 3, 12, 50, 22),
                    new Player("Player 4", 1, 1, 601, 12),
                    new Player("Player 5", 11, 1, 602, 26),
                    new Player("Player 6", 7, 1, 1000, 15)
            };

            Player[] outPlayers = new Player[3];
            Player[] scratch = new Player[3];

            long maxTeamwork = PocuBasketballAssociation.find3ManDreamTeam(players, outPlayers, scratch);

            assert (maxTeamwork == 2203);
        }

        {
            // a*p를 큰 순서대로 정렬 시
            Player[] players = new Player[]{
                    new Player("Player 1", 2, 1, 1000, 78),
                    new Player("Player 2", 10, 999, 1, 66),
                    new Player("Player 3", 3, 998, 1, 22),
                    new Player("Player 4", 1, 12, 50, 12),
                    new Player("Player 5", 11, 11, 45, 26),
                    new Player("Player 6", 7, 10, 40, 15)
            };

            Player[] outPlayers = new Player[3];
            Player[] scratch = new Player[3];

            long maxTeamwork = PocuBasketballAssociation.find3ManDreamTeam(players, outPlayers, scratch);

            assert (maxTeamwork == 1350);
        }

        {
            Player[] players = new Player[]{
                    new Player("Player 1", 2, 5, 10, 78),
                    new Player("Player 2", 10, 4, 5, 66),
                    new Player("Player 3", 3, 3, 2, 22),
                    new Player("Player 4", 1, 9, 8, 12),
                    new Player("Player 5", 11, 1, 12, 26),
                    new Player("Player 6", 7, 2, 10, 15),
                    new Player("Player 7", 8, 15, 3, 11),
                    new Player("Player 8", 5, 7, 13, 5),
                    new Player("Player 9", 8, 2, 7, 67),
                    new Player("Player 10", 1, 11, 1, 29),
                    new Player("Player 11", 2, 6, 9, 88)
            };

            Player[] tempPlayers = new Player[players.length];

            int k = PocuBasketballAssociation.findDreamTeamSize(players, tempPlayers);

            assert (k == 6);
        }
    }

    private static <T> void shuffle(T[] array) {
        int randomIndex;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            randomIndex = random.nextInt(i + 1);

            T temp = array[randomIndex];
            array[randomIndex] = array[i];
            array[i] = temp;
        }
    }

    private static Player getPlayerOrNull(final Player[] players, final String id) {
        for (Player player : players) {
            if (player.getName().equals(id)) {
                return player;
            }
        }

        return null;
    }
}
