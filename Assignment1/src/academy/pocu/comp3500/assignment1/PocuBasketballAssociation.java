package academy.pocu.comp3500.assignment1;

import academy.pocu.comp3500.assignment1.pba.GameStat;
import academy.pocu.comp3500.assignment1.pba.Player;

import java.util.Comparator;

public class PocuBasketballAssociation {
    private PocuBasketballAssociation() {
    }

    // ---

    public static void processGameStats(final GameStat[] gameStats, final Player[] outPlayers) {
        Sort.quickSort(gameStats, Comparator.comparing(GameStat::getPlayerName));

        String playerName = gameStats[0].getPlayerName();
        int playerIndex = 0;

        int gameCount = 0;
        int points = 0;
        int goals = 0;
        int goalAttempts = 0;
        int assists = 0;
        int numPasses = 0;

        for (int i = 0; i < gameStats.length; ++i) {
            ++gameCount;

            points += gameStats[i].getPoints();
            goals += gameStats[i].getGoals();
            goalAttempts += gameStats[i].getGoalAttempts();
            assists += gameStats[i].getAssists();
            numPasses += gameStats[i].getNumPasses();

            if (i == gameStats.length - 1 || !gameStats[i + 1].getPlayerName().equals(playerName)) {
                outPlayers[playerIndex].setName(playerName);
                outPlayers[playerIndex].setPointsPerGame(points / gameCount);
                outPlayers[playerIndex].setAssistsPerGame(assists / gameCount);
                outPlayers[playerIndex].setPassesPerGame(numPasses / gameCount);
                outPlayers[playerIndex].setShootingPercentage((int) (100.0f * goals / goalAttempts));

                if (i != gameStats.length - 1) {
                    playerName = gameStats[i + 1].getPlayerName();
                    ++playerIndex;

                    gameCount = 0;

                    points = 0;
                    goals = 0;
                    goalAttempts = 0;
                    assists = 0;
                    numPasses = 0;
                }
            }
        }
    }

    public static Player findPlayerPointsPerGame(final Player[] players, int targetPoints) {
        return Search.binarySearchFindAndNear(targetPoints, players, Player::getPointsPerGame, false);
    }

    public static Player findPlayerShootingPercentage(final Player[] players, int targetShootingPercentage) {
        return Search.binarySearchFindAndNear(targetShootingPercentage, players, Player::getShootingPercentage, false);
    }

    public static long find3ManDreamTeam(final Player[] players, final Player[] outPlayers, final Player[] scratch) {
        final int TEAM_SIZE = 3;
        assert (players.length >= TEAM_SIZE);
        assert (outPlayers.length >= TEAM_SIZE);

        return findDreamTeam(players, TEAM_SIZE, outPlayers, scratch);
    }

    public static long findDreamTeam(final Player[] players, final int k, final Player[] outPlayers, final Player[] scratch) {
        assert (players.length >= k);
        assert (outPlayers.length >= k);

        return findDreamTeamAssistSort(players, k, outPlayers, scratch);
//        return findDreamTeamPassSort(players, k, outPlayers, scratch);
    }

    public static int findDreamTeamSize(final Player[] players, final Player[] scratch) {
        if (players.length == 0) {
            return 0;
        }

        Sort.quickSort(players, Comparator.comparing(Player::getAssistsPerGame).reversed());

        int dreamTeamworkTeamSize = -1;
        long sumPass = 0;
        long dreamTeamwork = Long.MIN_VALUE;
        for (int i = 0; i < players.length; ++i) {
            sumPass += players[i].getPassesPerGame();

            final long tempTeamwork = sumPass * players[i].getAssistsPerGame();
            if (dreamTeamwork < tempTeamwork) {
                dreamTeamwork = tempTeamwork;
                dreamTeamworkTeamSize = i + 1;
            }
        }

        return dreamTeamworkTeamSize;
    }

    // ---

    private static long findDreamTeamAssistSort(final Player[] players, final int teamSize, final Player[] outPlayers, final Player[] scratch) {
        assert (players.length >= teamSize);
        assert (outPlayers.length >= teamSize);

        if (teamSize == 0) {
            return 0;
        }

        Sort.quickSort(players, Comparator.comparing(Player::getAssistsPerGame).reversed());

        long sumPass = 0;
        for (int so = 0; so < teamSize; ++so) {
            scratch[so] = players[so];
            outPlayers[so] = players[so];
            sumPass += players[so].getPassesPerGame();
        }
        long dreamTeamwork = sumPass * players[teamSize - 1].getAssistsPerGame();

        final Comparator<Player> scratchHeapPassComparator = Comparator.comparing(Player::getPassesPerGame);
        HeapOperation.buildHeap(scratch, teamSize, true, scratchHeapPassComparator);

        for (int p = teamSize; p < players.length; ++p) {
            if (scratch[0].getPassesPerGame() < players[p].getPassesPerGame()) {
                sumPass -= scratch[0].getPassesPerGame();
                sumPass += players[p].getPassesPerGame();

                HeapOperation.extractAndInsert(players[p], scratch, teamSize, true, scratchHeapPassComparator);
            }

            final long tempTeamwork = sumPass * players[p].getAssistsPerGame();
            if (dreamTeamwork < tempTeamwork) {
                dreamTeamwork = tempTeamwork;
                for (int o = 0; o < teamSize; ++o) {
                    outPlayers[o] = scratch[o];
                }
            }
        }

        return dreamTeamwork;
    }

    private static long findDreamTeamPassSort(final Player[] players, final int teamSize, final Player[] outPlayers, final Player[] scratch) {
        assert (players.length >= teamSize);
        assert (outPlayers.length >= teamSize);

        if (teamSize == 0) {
            return 0;
        }

        Sort.quickSort(players, Comparator.comparing(Player::getPassesPerGame).reversed());

        long sumPass = 0;
        for (int so = 0; so < teamSize; ++so) {
            scratch[so] = players[so];
            outPlayers[so] = players[so];

            sumPass += players[so].getPassesPerGame();
        }

        final Comparator<Player> scratchHeapAssistComparator = Comparator.comparing(Player::getAssistsPerGame);
        HeapOperation.buildHeap(scratch, teamSize, true, scratchHeapAssistComparator);

        long dreamTeamwork = sumPass * scratch[0].getAssistsPerGame();

        for (int p = teamSize; p < players.length; ++p) {
            if (scratch[0].getAssistsPerGame() < players[p].getAssistsPerGame()) {
                sumPass -= scratch[0].getPassesPerGame();
                sumPass += players[p].getPassesPerGame();

                HeapOperation.extractAndInsert(players[p], scratch, teamSize, true, scratchHeapAssistComparator);
            }

            final long tempTeamwork = sumPass * scratch[0].getAssistsPerGame();
            if (dreamTeamwork < tempTeamwork) {
                dreamTeamwork = tempTeamwork;
                for (int o = 0; o < teamSize; ++o) {
                    outPlayers[o] = scratch[o];
                }
            }
        }

        return dreamTeamwork;
    }
}
