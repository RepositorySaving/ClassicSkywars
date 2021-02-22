package marcos.devsoftware.markboard.object;

import lombok.Getter;
import lombok.SneakyThrows;
import marcos.devsoftware.markboard.api.PlayerBoard;
import marcos.devsoftware.markboard.utility.NMS;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MPlayerBoard implements PlayerBoard<String, Integer, String> {

    @Getter
    private final Player player;
    @Getter
    private final Scoreboard scoreboard;
    private String name;

    private Objective objective;
    private Objective buffer;
    private Map<Integer, String> lines = new HashMap<>();

    private boolean deleted = false;

    public MPlayerBoard(Player player, String name) {
        this.player = player;
        this.name = name;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        String subName = (player.getName().length() <= 14) ? player.getName() : player.getName().substring(0, 14);

        this.objective = this.scoreboard.getObjective("sb" + subName);
        this.buffer = this.scoreboard.getObjective("bf" + subName);

        if (this.objective == null) {
            this.objective = this.scoreboard.registerNewObjective("sb" + subName, "dummy");
        }

        if (this.buffer == null) {
            this.buffer = this.scoreboard.registerNewObjective("bf" + subName, "dummy");
        }

        this.objective.setDisplayName(name);
        sendObjective(this.objective, ObjectiveMode.CREATE);
        sendObjectiveDisplay(this.objective);

        this.buffer.setDisplayName(name);
        sendObjective(this.buffer, ObjectiveMode.CREATE);

        this.player.setScoreboard(this.scoreboard);
    }


    public String get(Integer score) {
        if (this.deleted) return null;

        return this.lines.get(score);
    }


    public void set(String name, Integer score) {
        if (this.deleted) return;
        String oldName = this.lines.get(score);

        if (name.equals(oldName)) return;
        this.lines.entrySet().removeIf(entry -> entry.getValue().equals(name));

        if (oldName != null) {
            if (NMS.getVersion().getMajor().equals("1.7")) {
                sendScore(this.objective, oldName, score, true);
                sendScore(this.objective, name, score, false);
            } else {

                sendScore(this.buffer, oldName, score, true);
                sendScore(this.buffer, name, score, false);

                swapBuffers();

                sendScore(this.buffer, oldName, score, true);
                sendScore(this.buffer, name, score, false);
            }
        } else {

            sendScore(this.objective, name, score, false);
            sendScore(this.buffer, name, score, false);
        }

        this.lines.put(score, name);
    }


    public void setAll(String... lines) {
        if (this.deleted) return;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            set(MessageUtility.format(line), lines.length - i);
        }

        Set<Integer> scores = this.lines.keySet();
        for (Integer integer : scores) {
            int score = integer;
            if (score <= 0 || score > lines.length) {
                remove(score);
            }
        }
    }


    public void clear() {
        this.lines.keySet().forEach(this::remove);
        this.lines.clear();
    }

    private void swapBuffers() {
        sendObjectiveDisplay(this.buffer);

        Objective temp = this.buffer;

        this.buffer = this.objective;
        this.objective = temp;
    }

    @SneakyThrows
    private void sendObjective(Objective obj, ObjectiveMode mode) {
        Object objHandle = NMS.getHandle(obj);
        Object packetObj = NMS.PACKET_OBJ.newInstance(objHandle, mode.ordinal());

        NMS.sendPacket(packetObj, this.player);
    }

    @SneakyThrows
    private void sendObjectiveDisplay(Objective obj) {
        Object objHandle = NMS.getHandle(obj);
        Object packet = NMS.PACKET_DISPLAY.newInstance(1, objHandle);

        NMS.sendPacket(packet, this.player);
    }

    @SneakyThrows
    private void sendScore(Objective obj, String name, int score, boolean remove) {
        Object sbHandle = NMS.getHandle(this.scoreboard);
        Object objHandle = NMS.getHandle(obj);

        Object sbScore = NMS.SB_SCORE.newInstance(sbHandle, objHandle, name);

        NMS.SB_SCORE_SET.invoke(sbScore, score);

        Map scores = (Map) NMS.PLAYER_SCORES.get(sbHandle);

        if (remove) {
            if (scores.containsKey(name)) {
                ((Map) scores.get(name)).remove(objHandle);
            }
        } else {
            if (!scores.containsKey(name)) {
                scores.put(name, new HashMap<>());
            }
            ((Map<Object, Object>) scores.get(name)).put(objHandle, sbScore);
        }

        switch (NMS.getVersion().getMajor()) {
            case "1.7": {
                Object packet;
                packet = NMS.PACKET_SCORE.newInstance(sbScore, remove ? 1 : 0);
                NMS.sendPacket(packet, this.player);
                return;
            }
            case "1.8":
            case "1.9":
            case "1.10":
            case "1.11":
            case "1.12":
                Object packet;
                if (remove) {
                    packet = NMS.PACKET_SCORE_REMOVE.newInstance(name, objHandle);
                } else {
                    packet = NMS.PACKET_SCORE.newInstance(sbScore);
                }

                NMS.sendPacket(packet, this.player);
                return;
        }

        Object packet = NMS.PACKET_SCORE.newInstance(remove ? NMS.ENUM_SCORE_ACTION_REMOVE : NMS.ENUM_SCORE_ACTION_CHANGE, obj.getName(), name, score);
        NMS.sendPacket(packet, this.player);
    }


    public void remove(Integer score) {
        if (this.deleted) return;

        String name = this.lines.get(score);
        if (name == null) return;

        this.scoreboard.resetScores(name);
        this.lines.remove(score);
    }


    public void delete() {
        if (this.deleted) return;
        MarkBoard.instance().removeBoard(this.player);

        sendObjective(this.objective, ObjectiveMode.REMOVE);
        sendObjective(this.buffer, ObjectiveMode.REMOVE);

        this.objective.unregister();
        this.objective = null;
        this.buffer.unregister();
        this.buffer = null;
        this.lines = null;
        this.deleted = true;
    }


    public Map<Integer, String> getLines() {
        if (this.deleted) return null;

        return this.lines;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (this.deleted) return;

        this.name = name;
        this.objective.setDisplayName(name);
        this.buffer.setDisplayName(name);

        sendObjective(this.objective, ObjectiveMode.UPDATE);
        sendObjective(this.buffer, ObjectiveMode.UPDATE);
    }

    private enum ObjectiveMode {CREATE, REMOVE, UPDATE}
}