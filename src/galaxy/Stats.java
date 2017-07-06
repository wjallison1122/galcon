package galaxy;

import java.util.LinkedList;

public abstract class Stats {

    protected final Player P;

    public Stats(Player p) {
        P = p;
    }

    protected abstract void updateStats(LinkedList<Player> active, Player winner);

    protected abstract void reportStats();
}
