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

        Comparator<Player> comparator = Comparator.comparing((Player p) -> p.getAssistsPerGame() * p.getPassesPerGame());
        Sort.quickSort(players, comparator);

        int playerIndex = players.length - 1;
        for (int i = 0; i < TEAM_SIZE; ++i) {
            outPlayers[i] = players[playerIndex];
            --playerIndex;
        }

        long dreamTeamTeamwork = calculateTeamwork(TEAM_SIZE, outPlayers);
        for (playerIndex = 0; playerIndex < players.length - TEAM_SIZE; ++playerIndex) {
            long maxTempTeamwork = 0;
            int maxTempTeamworkChangeIndex = -1;

            for (int changeIndex = 0; changeIndex < TEAM_SIZE; ++changeIndex) {
                final Player changedPlayer = outPlayers[changeIndex];
                outPlayers[changeIndex] = players[playerIndex];

                final long tempTeamwork = calculateTeamwork(TEAM_SIZE, outPlayers);
                if (maxTempTeamwork < tempTeamwork) {
                    maxTempTeamwork = tempTeamwork;
                    maxTempTeamworkChangeIndex = changeIndex;
                }

                outPlayers[changeIndex] = changedPlayer;
            }

            if (dreamTeamTeamwork < maxTempTeamwork) {
                dreamTeamTeamwork = maxTempTeamwork;
                outPlayers[maxTempTeamworkChangeIndex] = players[playerIndex];
            }
        }

        return dreamTeamTeamwork;

//        return findDreamTeam(players, TEAM_SIZE, outPlayers, scratch);
    }

    public static long findDreamTeam(final Player[] players, int k, final Player[] outPlayers, final Player[] scratch) {
        final int teamSize = k;
        assert (players.length >= teamSize);
        assert (outPlayers.length >= teamSize);

        if (teamSize == 0) {
            return 0;
        }
//        이세진  12시간 전
//        어시스트로 정렬했기 때문에 최소 어시스트가 바뀔 때마다 기존의 팀원 중에 최소 패스만 찾아내면 되는 것(O(k))이 아니라
//        0 ~ 최소 어시스트 인덱스까지 모두 훑으며 최대 팀워크를 찾아야 하기 때문에 시간복잡도에 문제가 생기는 게 아닐까 싶네요
//
//        이세진  12시간 전
//        기존 팀원 중 한명씩만 바꿔나가려면 어떤 부분을 고쳐야할지 곰곰해 생각해보시면 답이 나올 것 같습니다.

//        1. findDreamTeam에서 assist순으로 정렬을 해보셨습니다. 그렇다면 최소의 어시스트를 구할 때 매번 for문을 순회할 필요가 있을까요?
//        2.  특정한  assist 를 기준으로 k명을 어떻게 뽑으면 좋을지 생각해보시고 그리고  업데이트를 할 때 그 k명을 어떻게 업데이트할 것인지 생각해 보세요.

        Sort.quickSort(players, Comparator.comparing(Player::getAssistsPerGame).reversed());

        long dreamTeamwork = 0;
        for (int i = teamSize - 1; i < players.length; ++i) {
            final int minAssistsPerGame = players[i].getAssistsPerGame();
            quickSelectPlayerTeamworkRecursive(teamSize - 1, players, minAssistsPerGame, true, 0, i);

            final long tempTeamwork = calculateTeamwork(teamSize, players);
            if (dreamTeamwork < tempTeamwork) {
                dreamTeamwork = tempTeamwork;
                for (int t = 0; t < teamSize; ++t) {
                    outPlayers[t] = players[t];
                }
            }
        }

        return dreamTeamwork;
    }

    public static int findDreamTeamSize(final Player[] players, final Player[] scratch) {
        if (players.length == 0) {
            return 0;
        }

        Sort.quickSort(players, Comparator.comparing(Player::getAssistsPerGame).reversed());

        long sumPass = 0;
        int maxTeamworkTeamSize = -1;
        long maxTeamwork = Integer.MIN_VALUE;
        for (int i = 0; i < players.length; ++i) {
            sumPass += players[i].getPassesPerGame();
            final long tempTeamwork = sumPass * players[i].getAssistsPerGame();
            if (maxTeamwork < tempTeamwork) {
                maxTeamwork = tempTeamwork;
                maxTeamworkTeamSize = i + 1;
            }
        }

        return maxTeamworkTeamSize;
    }

    private static long calculateTeamwork(final int size, final Player... players) {
        // 팀워크 = [팀에 속한 모든 선수의 경기당 패스수를 합한 결과] * [팀에 속한 각 선수의 경기당 어시스트수 중 최솟값]

        long sumPassesPerGame = 0;
        long minAssistsPerGame = Long.MAX_VALUE;

        for (int i = 0; i < size; ++i) {
            sumPassesPerGame += players[i].getPassesPerGame();
            minAssistsPerGame = Math.min(minAssistsPerGame, players[i].getAssistsPerGame());
        }

        return sumPassesPerGame * minAssistsPerGame;
    }

    private static void quickSortPlayerTeamworkRecursive(final Player[] players, final int minAssistsPerGame, final boolean isDescending, final int left, final int right) {
        assert (left <= right);

        if (left == right) {
            return;
        }

        final int mid = (left + right) / 2;
        final long leftTeamwork = players[left].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[left]);
        final long midTeamwork = players[mid].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[mid]);
        final long rightTeamwork = players[right].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[right]);

        if ((midTeamwork < leftTeamwork && leftTeamwork < rightTeamwork) || (rightTeamwork < leftTeamwork && leftTeamwork < midTeamwork)) {
            Sort.swap(players, left, right);
        } else if ((leftTeamwork < midTeamwork && midTeamwork < rightTeamwork) || (rightTeamwork < midTeamwork && midTeamwork < leftTeamwork)) {
            Sort.swap(players, mid, right);
        }
//        else {
//            Sort.swap(players, right, right);
//        }

        final int pivotPos = partitionPlayerTeamwork(players, minAssistsPerGame, isDescending, left, right);

        quickSortPlayerTeamworkRecursive(players, minAssistsPerGame, isDescending, left, pivotPos - 1);
        quickSortPlayerTeamworkRecursive(players, minAssistsPerGame, isDescending, pivotPos + 1, right);
    }

    private static Player quickSelectPlayerTeamworkRecursive(final int targetIndex, final Player[] players, final int minAssistsPerGame, final boolean isDescending, final int left, final int right) {
        assert (left <= right);

        if (left == right) {
            return players[left];
        }


        final int mid = (left + right) / 2;
        final long leftTeamwork = players[left].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[left]);
        final long midTeamwork = players[mid].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[mid]);
        final long rightTeamwork = players[right].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[right]);

        if ((midTeamwork < leftTeamwork && leftTeamwork < rightTeamwork) || (rightTeamwork < leftTeamwork && leftTeamwork < midTeamwork)) {
            Sort.swap(players, left, right);
        } else if ((leftTeamwork < midTeamwork && midTeamwork < rightTeamwork) || (rightTeamwork < midTeamwork && midTeamwork < leftTeamwork)) {
            Sort.swap(players, mid, right);
        }
//        else {
//            Sort.swap(players, right, right);
//        }

        final int pivotPos = partitionPlayerTeamwork(players, minAssistsPerGame, isDescending, left, right);

        if (targetIndex == pivotPos) {
            return players[targetIndex];
        } else if (targetIndex < pivotPos) {
            return quickSelectPlayerTeamworkRecursive(targetIndex, players, minAssistsPerGame, isDescending, left, pivotPos - 1);
        } else {
            return quickSelectPlayerTeamworkRecursive(targetIndex, players, minAssistsPerGame, isDescending, pivotPos + 1, right);
        }
    }


    private static int partitionPlayerTeamwork(final Player[] players, final int minAssistsPerGame, final boolean isDescending, final int left, final int right) {
        assert (left < right);

        int pivot = right;
        final long pivotMaxTeamwork = players[pivot].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[pivot]);

        int pointer = left - 1;
        for (int i = left; i < right; ++i) {
            final long p1MaxTeamwork = players[i].getPassesPerGame() * getMinAssistsPerGame(minAssistsPerGame, players[i]);
            if (isDescending) {
                if (p1MaxTeamwork > pivotMaxTeamwork) {
                    ++pointer;
                    Sort.swap(players, pointer, i);
                }
            } else {
                if (p1MaxTeamwork < pivotMaxTeamwork) {
                    ++pointer;
                    Sort.swap(players, pointer, i);
                }
            }
        }

        pivot = pointer + 1;
        Sort.swap(players, pivot, right);

        return pivot;
    }

    private static long getMinAssistsPerGame(final int minAssistsPerGame, final Player player) {
        if (player.getAssistsPerGame() < minAssistsPerGame) {
            assert (false);
            return 0;
        } else {
            return minAssistsPerGame;
        }
    }
}
