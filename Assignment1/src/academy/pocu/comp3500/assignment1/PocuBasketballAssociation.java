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

                if (i == gameStats.length - 1) {
                    break;
                }

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
        assert (outPlayers.length == TEAM_SIZE);

        return findDreamTeam(players, TEAM_SIZE, outPlayers, scratch);
    }

    public static long findDreamTeam(final Player[] players, int k, final Player[] outPlayers, final Player[] scratch) {
        final int teamSize = k;
        assert (players.length >= teamSize);
        assert (outPlayers.length >= teamSize);

        Sort.quickSort(players, Comparator.comparing(Player::getPassesPerGame).reversed());
        for (int i = 0; i < teamSize; ++i) {
            outPlayers[i] = players[i];
        }

        Sort.quickSort(players, Comparator.comparing(Player::getPassesPerGame));

        long dreamTeamTeamwork = calculateTeamwork(outPlayers);

        for (int playerIndex = 0; playerIndex < players.length - teamSize; ++playerIndex) {
            long maxTempTeamwork = 0;
            int maxTempTeamworkChangeIndex = -1;

            for (int scratchChangeIndex = 0; scratchChangeIndex < teamSize; ++scratchChangeIndex) {
                final Player changedPlayer = outPlayers[scratchChangeIndex];
                outPlayers[scratchChangeIndex] = players[playerIndex];

                long tempTeamwork = calculateTeamwork(outPlayers);
                if (maxTempTeamwork < tempTeamwork) {
                    maxTempTeamwork = tempTeamwork;
                    maxTempTeamworkChangeIndex = scratchChangeIndex;
                }

                outPlayers[scratchChangeIndex] = changedPlayer;
            }

            if (dreamTeamTeamwork < maxTempTeamwork) {
                dreamTeamTeamwork = maxTempTeamwork;
                outPlayers[maxTempTeamworkChangeIndex] = players[playerIndex];
            }
        }

        return dreamTeamTeamwork;
    }

    public static int findDreamTeamSize(final Player[] players, final Player[] scratch) {
        long maxTeamwork = -1;
        int maxTeamworkTeamSize = -1;

        for (int i = 1; i < players.length; ++i) {
            final long teamwork = findDreamTeam(players, i, scratch, scratch);

            if (maxTeamwork < teamwork) {
                maxTeamwork = teamwork;
                maxTeamworkTeamSize = i;
            }
        }

        return maxTeamworkTeamSize;
    }

    private static long calculateTeamwork(final Player... players) {
        // 팀워크 = [팀에 속한 모든 선수의 경기당 패스수를 합한 결과] * [팀에 속한 각 선수의 경기당 어시스트수 중 최솟값]

        long sumPassesPerGame = 0;
        long minAssistsPerGame = Long.MAX_VALUE;

        for (final Player player : players) {
            if (player == null) {
                continue;
            }
            sumPassesPerGame += player.getPassesPerGame();
            minAssistsPerGame = Math.min(minAssistsPerGame, player.getAssistsPerGame());
        }

        return sumPassesPerGame * minAssistsPerGame;
    }

    public static void quickSortPlayerTeamwork(final Player[] players, final boolean isDescending) {
        quickSortPlayerTeamworkRecursive(players, isDescending, 0, players.length - 1);
    }

    private static void swap(final Player[] players, final int p1, final int p2) {
        final Player temp = players[p1];
        players[p1] = players[p2];
        players[p2] = temp;
    }

    private static void quickSortPlayerTeamworkRecursive(final Player[] players, final boolean isDescending, final int left, final int right) {
        if (left >= right) {
            return;
        }

        final int pivotPos = partitionPlayerTeamwork(players, isDescending, left, right);

        quickSortPlayerTeamworkRecursive(players, isDescending, left, pivotPos - 1);
        quickSortPlayerTeamworkRecursive(players, isDescending, pivotPos + 1, right);
    }

    private static int partitionPlayerTeamwork(final Player[] players, final boolean isDescending, final int left, final int right) {
        assert (left < right);

        int pivot = right;
        final int pivotMaxTeamwork = players[pivot].getPassesPerGame() * players[pivot].getAssistsPerGame();

        int pointer = left - 1;
        for (int i = left; i < right; ++i) {
            final int p1MaxTeamwork = players[i].getPassesPerGame() * players[i].getAssistsPerGame();
            if (isDescending) {
                if (p1MaxTeamwork > pivotMaxTeamwork) {
                    ++pointer;
                    swap(players, pointer, i);
                }
            } else {
                if (p1MaxTeamwork < pivotMaxTeamwork) {
                    ++pointer;
                    swap(players, pointer, i);
                }
            }
        }

        pivot = pointer + 1;
        swap(players, pivot, right);

        return pivot;
    }
}
