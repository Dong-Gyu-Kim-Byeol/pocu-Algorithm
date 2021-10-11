package academy.pocu.comp3500.lab6;

import academy.pocu.comp3500.lab6.leagueofpocu.Player;

public class League {
    private Player[] players;
    private boolean isSorted;

    public League() {
    }

    public League(final Player[] players, final boolean isSorted) {
        this.players = players;
        this.isSorted = isSorted;
    }

    public Player findMatchOrNull(final Player player) {
        return null;
    }

    public Player[] getTop(final int count) {
        return null;
    }

    public Player[] getBottom(final int count) {
        return null;
    }

    public boolean join(final Player player) {
        return false;
    }

    public boolean leave(final Player player) {
        return false;
    }
}