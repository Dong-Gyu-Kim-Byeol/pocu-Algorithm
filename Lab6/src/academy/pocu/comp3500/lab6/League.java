package academy.pocu.comp3500.lab6;

import academy.pocu.comp3500.lab6.leagueofpocu.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;

public class League {
    private final BinaryTree<Player> tree;
    private final HashMap<Integer, Player> map;

    public League() {
        this(new Player[0], true);
    }

    public League(final Player[] players, final boolean isSorted) {
        final Function<Player, Integer> function = Player::getRating;
        final Comparator<Player> comparator = Comparator.comparing(function);

        this.tree = new BinaryTree<Player>(comparator, Comparator.comparing(Player::getId));
        this.map = new HashMap<Integer, Player>();

        for (final Player player : players) {
            if (this.map.containsKey(player.getId())) {
                continue;
            }
            this.map.put(player.getId(), player);
        }
        this.tree.insertArray(players);
    }

    public Player findMatchOrNull(final Player player) {
        return findAndNearOrNullRecursive(this.tree.getRoot(), player, Player::getRating);
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
        if (this.map.containsKey(player.getId())) {
            return false;
        }

        this.map.put(player.getId(), player);
        return this.tree.insert(player);
    }

    public boolean leave(final Player player) {
        if (this.map.containsKey(player.getId()) == false) {
            return false;
        }

        this.map.remove(player.getId());
        return this.tree.delete(player);
    }

    // private
    private static Player findAndNearOrNullRecursive(final BinaryTreeNode<Player> root, final Player player, final Function<Player, Integer> function) {
        if (root == null) {
            return null;
        }

        final Comparator<Player> comparator = Comparator.comparing(function);

        BinaryTreeNode<Player> node = root;
        Player data = node.getData();
        int loopMinDifference = Math.abs(function.apply(player) - function.apply(node.getData()));

        while (node != null) {
            if (player.getId() == node.getData().getId()) {
                int leftDifference = Integer.MAX_VALUE;
                final Player left = findAndNearOrNullRecursive(node.getLeft(), player, function);
                if (left != null) {
                    leftDifference = Math.abs(function.apply(player) - function.apply(left));
                }

                int rightDifference = Integer.MAX_VALUE;
                final Player right = findAndNearOrNullRecursive(node.getRight(), player, function);
                if (right != null) {
                    rightDifference = Math.abs(function.apply(player) - function.apply(right));
                }

                final int minDifference = Math.min(loopMinDifference, Math.min(rightDifference, leftDifference));

                if (minDifference == rightDifference) {
                    return right;
                } else if (minDifference == leftDifference) {
                    return left;
                } else {
                    assert (minDifference == loopMinDifference);
                    return data;
                }
            }

            final int tempDifference = Math.abs(function.apply(player) - function.apply(node.getData()));

            if (tempDifference == 0) {
                return node.getData();
            }

            if (loopMinDifference == tempDifference) {
                if (comparator.compare(data, node.getData()) < 0) {
                    data = node.getData();
                }
            }

            if (loopMinDifference > tempDifference) {
                loopMinDifference = tempDifference;
                data = node.getData();
            }

            if (comparator.compare(player, node.getData()) < 0) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
        }

        return data;
    }

}