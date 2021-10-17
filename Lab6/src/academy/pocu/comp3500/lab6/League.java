package academy.pocu.comp3500.lab6;

import academy.pocu.comp3500.lab6.leagueofpocu.Player;

import java.util.Comparator;
import java.util.function.Function;

public class League {
    private final BinaryTree<Player> tree;

    public League() {
        this(new Player[0], true);
    }

    public League(final Player[] players, final boolean isSorted) {
        this.tree = new BinaryTree<Player>(Comparator.comparing(Player::getId), Comparator.comparing(Player::getRating));
        if (isSorted) {
            this.tree.initByArray(players);
        } else {
            this.tree.insertArray(players);
        }
    }

    public Player findMatchOrNull(final Player player) {
        return findAndNearOrNullRecursive(this.tree.getRootOrNull(), player, Player::getRating);
    }

    public Player[] getTop(final int count) {
        final Player[] outData = new Player[Math.min(this.tree.size(), count)];
        this.tree.descendingTraversal(outData);
        return outData;
    }

    public Player[] getBottom(final int count) {
        final Player[] outData = new Player[Math.min(this.tree.size(), count)];
        this.tree.inOrderTraversal(outData);
        return outData;
    }

    public boolean join(final Player player) {
        return this.tree.insert(player);
    }

    public boolean leave(final Player player) {
        return this.tree.delete(player);
    }

    // private
    private static Player findAndNearOrNullRecursive(final BinaryTreeNode<Player> rootOrNull, final Player player, final Function<Player, Integer> function) {
        if (rootOrNull == null) {
            return null;
        }

        final Comparator<Player> comparator = Comparator.comparing(function);

        BinaryTreeNode<Player> node = rootOrNull;
        Player data = null;
        int loopMinDifference = Integer.MAX_VALUE;

        while (node != null) {
            if (player.getId() == node.getData().getId()) {
                int rightDifference = Integer.MAX_VALUE;
                final Player right = findAndNearOrNullRecursive(node.getRight(), player, function);
                if (right != null) {
                    rightDifference = Math.abs(function.apply(player) - function.apply(right));
                }

                int leftDifference = Integer.MAX_VALUE;
                final Player left = findAndNearOrNullRecursive(node.getLeft(), player, function);
                if (left != null) {
                    leftDifference = Math.abs(function.apply(player) - function.apply(left));
                }

                final int minDifference = Math.min(loopMinDifference, Math.min(rightDifference, leftDifference));

                if (minDifference == rightDifference) {
                    return right;
                } else if (minDifference == loopMinDifference) {
                    return data;
                } else {
                    assert (minDifference == leftDifference);
                    return left;
                }
            } else { // player.getId() != node.getData().getId()
                final int compare = comparator.compare(player, node.getData());
                if (compare == 0) {
                    return node.getData();
                }

                final int tempDifference = Math.abs(function.apply(player) - function.apply(node.getData()));

                if (loopMinDifference == tempDifference) {
                    assert (data != null);
                    if (comparator.compare(data, node.getData()) < 0) {
                        data = node.getData();
                    }
                }

                if (loopMinDifference > tempDifference) {
                    loopMinDifference = tempDifference;
                    data = node.getData();
                }

                if (compare < 0) {
                    node = node.getLeft();
                } else {
                    node = node.getRight();
                }
            }
        }

        return data;
    }

}
