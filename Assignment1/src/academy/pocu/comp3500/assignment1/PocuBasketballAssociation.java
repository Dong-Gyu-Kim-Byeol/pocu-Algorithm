package academy.pocu.comp3500.assignment1;

import academy.pocu.comp3500.assignment1.pba.GameStat;
import academy.pocu.comp3500.assignment1.pba.Player;

import java.util.Comparator;

public class PocuBasketballAssociation {
    private PocuBasketballAssociation() {
    }

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

            if (i == gameStats.length - 1 || gameStats[i + 1].getPlayerName().equals(playerName) == false) {
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
        int letf = 0;
        int right = players.length - 1;

        int targetPlayerIndex = -1;
        int minDifference = Integer.MAX_VALUE;

        while (letf <= right) {
            final int mid = (letf + right) / 2;

            final int difference = Math.abs(players[mid].getPointsPerGame() - targetPoints);

            if (difference == 0) {
                minDifference = difference;
                targetPlayerIndex = mid;

                return players[targetPlayerIndex];
            }

            if (minDifference == difference) {
                if (players[targetPlayerIndex].getPointsPerGame() < players[mid].getPointsPerGame()) {
                    targetPlayerIndex = mid;
                }
            }

            if (minDifference > difference) {
                minDifference = difference;
                targetPlayerIndex = mid;
            }

            if (targetPoints < players[mid].getPointsPerGame()) {
                right = mid - 1;
            } else { // players[mid].getPointsPerGame() < targetPoints
                letf = mid + 1;
            }
        }

        return players[targetPlayerIndex];
    }

    public static Player findPlayerShootingPercentage(final Player[] players, int targetShootingPercentage) {
        int letf = 0;
        int right = players.length - 1;

        int targetPlayerIndex = -1;
        int minDifference = Integer.MAX_VALUE;

        while (letf <= right) {
            final int mid = (letf + right) / 2;

            final int difference = Math.abs(players[mid].getShootingPercentage() - targetShootingPercentage);

            if (difference == 0) {
                minDifference = difference;
                targetPlayerIndex = mid;

                return players[targetPlayerIndex];
            }

            if (minDifference == difference) {
                if (players[targetPlayerIndex].getShootingPercentage() < players[mid].getShootingPercentage()) {
                    targetPlayerIndex = mid;
                }
            }

            if (minDifference > difference) {
                minDifference = difference;
                targetPlayerIndex = mid;
            }

            if (targetShootingPercentage < players[mid].getShootingPercentage()) {
                right = mid - 1;
            } else { // players[mid].getShootingPercentage() < targetShootingPercentage
                letf = mid + 1;
            }
        }

        return players[targetPlayerIndex];
    }

    public static long find3ManDreamTeam(final Player[] players, final Player[] outPlayers, final Player[] scratch) {
        final int TEAM_SIZE = 3;
        assert (players.length >= TEAM_SIZE);
        assert (outPlayers.length >= TEAM_SIZE);

        return findDreamTeam(players, TEAM_SIZE, outPlayers, scratch);
    }

    public static long findDreamTeam(final Player[] players, int k, final Player[] outPlayers, final Player[] scratch) {
        final int teamSize = k;
        assert (players.length >= teamSize);
        assert (outPlayers.length >= teamSize);

//        return findDreamTeamAssist(players, teamSize, outPlayers, scratch);
        return findDreamTeamPass(players, teamSize, outPlayers, scratch);

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

    private static long findDreamTeamAssist(final Player[] players, int k, final Player[] outPlayers, final Player[] scratch) {
        final int teamSize = k;
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

        int scratchMinPassIndex = 0;
        for (int p = teamSize; p < players.length; ++p) {
            for (int s = 0; s < teamSize; ++s) {
                if (scratch[scratchMinPassIndex].getPassesPerGame() > scratch[s].getPassesPerGame()) {
                    scratchMinPassIndex = s;
                }
            }
            if (scratch[scratchMinPassIndex].getPassesPerGame() < players[p].getPassesPerGame()) {
                sumPass -= scratch[scratchMinPassIndex].getPassesPerGame();
                sumPass += players[p].getPassesPerGame();

                scratch[scratchMinPassIndex] = players[p];
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


    private static long findDreamTeamPass(final Player[] players, int k, final Player[] outPlayers, final Player[] scratch) {
        final int teamSize = k;
        assert (players.length >= teamSize);
        assert (outPlayers.length >= teamSize);

        if (teamSize == 0) {
            return 0;
        }

        Sort.quickSort(players, Comparator.comparing(Player::getPassesPerGame).reversed());

        long sumPass = 0;
        int scratchMinAssistIndex = 0;
        for (int so = 0; so < teamSize; ++so) {
            scratch[so] = players[so];
            outPlayers[so] = players[so];

            sumPass += players[so].getPassesPerGame();

            if (scratch[scratchMinAssistIndex].getAssistsPerGame() > scratch[so].getAssistsPerGame()) {
                scratchMinAssistIndex = so;
            }
        }
        long dreamTeamwork = sumPass * scratch[scratchMinAssistIndex].getAssistsPerGame();

        for (int p = teamSize; p < players.length; ++p) {
            if (scratch[scratchMinAssistIndex].getAssistsPerGame() < players[p].getAssistsPerGame()) {
                sumPass -= scratch[scratchMinAssistIndex].getPassesPerGame();
                sumPass += players[p].getPassesPerGame();

                scratch[scratchMinAssistIndex] = players[p];
            }
            for (int s = 0; s < teamSize; ++s) {
                if (scratch[scratchMinAssistIndex].getAssistsPerGame() > scratch[s].getAssistsPerGame()) {
                    scratchMinAssistIndex = s;
                }
            }

            final long tempTeamwork = sumPass * scratch[scratchMinAssistIndex].getAssistsPerGame();
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
