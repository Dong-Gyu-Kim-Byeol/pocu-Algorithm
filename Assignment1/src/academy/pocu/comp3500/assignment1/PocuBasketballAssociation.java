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
        assert (players.length >= 3);
        assert (outPlayers.length == 3);

        Sort.quickSort(players, Comparator.comparing(Player::getAssistsPerGame).reversed());
        final int maxAssistsPerGame = players[2].getAssistsPerGame();

        quickSortPlayerTeamwork(players, maxAssistsPerGame, true);

        for (int i = 0; i < 3; ++i) {
            outPlayers[i] = players[i];
        }

        return calculateTeamwork(outPlayers);
    }

    public static long findDreamTeam(final Player[] players, int k, final Player[] outPlayers, final Player[] scratch) {
        assert (players.length >= k);
        assert (outPlayers.length >= k);

        Sort.quickSort(players, Comparator.comparing(Player::getAssistsPerGame).reversed());
        final int maxAssistsPerGame = players[k - 1].getAssistsPerGame();

        quickSortPlayerTeamwork(players, maxAssistsPerGame, true);

        for (int i = 0; i < k; ++i) {
            outPlayers[i] = players[i];
        }

        final long teamwork = calculateTeamwork(outPlayers);
        return teamwork;
    }

    public static int findDreamTeamSize(final Player[] players, final Player[] scratch) {
        long maxTeamwork = -1;
        int maxTeamworkTeamSize = -1;

        for (int i = 1; i < players.length; ++i) {
            final long teamwork = findDreamTeam(players, i, scratch, players);
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

    public static void quickSortPlayerTeamwork(final Player[] players, final int maxAssistsPerGame, final boolean isDescending) {
        quickSortPlayerTeamworkRecursive(players, maxAssistsPerGame, isDescending, 0, players.length - 1);
    }

    private static void swap(final Player[] players, final int p1, final int p2) {
        final Player temp = players[p1];
        players[p1] = players[p2];
        players[p2] = temp;
    }

    private static void quickSortPlayerTeamworkRecursive(final Player[] players, final int maxAssistsPerGame, final boolean isDescending, final int left, final int right) {
        if (left >= right) {
            return;
        }

        final int pivotPos = partitionPlayerTeamwork(players, maxAssistsPerGame, isDescending, left, right);

        quickSortPlayerTeamworkRecursive(players, maxAssistsPerGame, isDescending, left, pivotPos - 1);
        quickSortPlayerTeamworkRecursive(players, maxAssistsPerGame, isDescending, pivotPos + 1, right);
    }

    private static int partitionPlayerTeamwork(final Player[] players, final int maxAssistsPerGame, final boolean isDescending, final int left, final int right) {
        assert (left < right);

        int pivot = right;
        final int pivotMaxTeamwork = players[pivot].getPassesPerGame() * Math.min(players[pivot].getAssistsPerGame(), maxAssistsPerGame);

        int pointer = left - 1;
        for (int i = left; i < right; ++i) {
            final int p1MaxTeamwork = players[i].getPassesPerGame() * Math.min(players[i].getAssistsPerGame(), maxAssistsPerGame);
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
