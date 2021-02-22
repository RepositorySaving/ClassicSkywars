package marcos.devsoftware.skywars.scoreboard;

import lombok.Getter;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
public class PlayerTeam {

    private final String teamName;
    private final Team team;
    private String prefix;

    public PlayerTeam(Scoreboard scoreboard, String teamName) {
       this.teamName = MessageUtility.format(teamName);
       String teamNameOriginal = ChatColor.stripColor(this.teamName);

       if (scoreboard.getTeam(teamNameOriginal) == null) {
           this.team = scoreboard.registerNewTeam(teamNameOriginal);
       } else {
           this.team = scoreboard.getTeam(teamNameOriginal);
       }
    }

    public PlayerTeam setProperties(boolean seeFriend, boolean fireFriend,  String prefix) {
        team.setCanSeeFriendlyInvisibles(seeFriend);
        team.setAllowFriendlyFire(fireFriend);

        prefix = MessageUtility.format(prefix);

        if (prefix.length() <= 16) {
            team.setPrefix(prefix);
            return this;
        } else {
            prefix = prefix.substring(0, 16);
        }
        
        team.setPrefix(prefix);

        return this;
    }

    @Deprecated
    public PlayerTeam addPlayer(Player player) {
        team.addPlayer(player);
        return this;
    }
}
