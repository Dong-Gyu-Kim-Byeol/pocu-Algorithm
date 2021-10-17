package academy.pocu.comp3500.lab6;

import academy.pocu.comp3500.lab6.leagueofpocu.Player;

import java.util.Comparator;

public class League {
    private final BinaryTree<Player> tree;

    public League() {
        this(new Player[0], true);
    }

    public League(final Player[] players, final boolean isSorted) {
        this.tree = new BinaryTree<Player>(Comparator.comparing(Player::getId), Player::getRating);
        if (isSorted) {
            this.tree.initByArray(players);
        } else {
            this.tree.insertArray(players);
        }
    }

    public Player findMatchOrNull(final Player player) {
        return this.tree.findAndNearWithoutTargetOrNull(player);
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

}
