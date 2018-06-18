package org.jubaroo.wurmunlimited.mods;

import com.sun.istack.internal.Nullable;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.jubaroo.wurmunlimited.mods.actions.LabyrinthAction;
import org.jubaroo.wurmunlimited.mods.actions.LabyrinthRemoveAction;
import org.jubaroo.wurmunlimited.mods.maze.Maze;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateAMaze implements WurmServerMod, Configurable, ServerStartedListener {
    private static Logger logger = Logger.getLogger(CreateAMaze.class.getName());
    public static boolean executionCostLogging;
    public static boolean logExecutionCost;
    public static long tmpExecutionStartTime;
    public static DecimalFormat executionLogDf;
    public static double totalExecutionCost;
    public static long totalExecutionCostStartTime;
    public static boolean addLabyrinth;

    static {
        CreateAMaze.logger = Logger.getLogger(CreateAMaze.class.getName());
        CreateAMaze.executionCostLogging = false;
        CreateAMaze.logExecutionCost = true;
        CreateAMaze.tmpExecutionStartTime = 0L;
        CreateAMaze.executionLogDf = new DecimalFormat("#.#########");
        CreateAMaze.totalExecutionCost = 0.0;
        CreateAMaze.totalExecutionCostStartTime = -1L;
    }

    public CreateAMaze() {
    }

    public void configure(Properties properties) {
        CreateAMaze.logger.log(Level.INFO, "configure called");
        CreateAMaze.logExecutionCost = CreateAMaze.executionCostLogging;
        CreateAMaze.executionCostLogging = Boolean.valueOf(properties.getProperty("executionCostLogging","true") );
        if (executionCostLogging) { logger.log(Level.INFO, "executionCostLogging enabled"); }
        CreateAMaze.addLabyrinth = Boolean.valueOf(properties.getProperty("addLabyrinth","true") );
        CreateAMaze.logger.log(Level.INFO, "all configure completed");
    }

    @Override
    public void onServerStarted() {
        try {
            CreateAMaze.logger.log(Level.INFO, "onServerStarted called");
            if (addLabyrinth) { ModActions.registerAction(new LabyrinthAction()); ModActions.registerAction(new LabyrinthRemoveAction()); }
            CreateAMaze.logger.log(Level.INFO, "all onServerStarted completed");
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error in onServerStarted()", e);
        }
    }

    public static String fixActionString(final Creature c, String s) {
        s = s.replace("%HIS", c.isNotFemale() ? "his" : "her");
        s = s.replace("%NAME", c.getName());
        s = s.replace("%NAME'S", String.valueOf(c.getName()) + "'s");
        s = s.replace("%HIMSELF", c.isNotFemale() ? "himself" : "herself");
        s = s.replace("%HIM", c.isNotFemale() ? "him" : "her");
        return s;
    }

    public static void actionNotify(final Creature c, @Nullable String myMsg, @Nullable String othersMsg, @Nullable String stealthOthersMsg, @Nullable final Creature[] excludeFromBroadCast) {
        if (excludeFromBroadCast != null) {
            final int length = excludeFromBroadCast.length;
        }
        if (myMsg != null) {
            myMsg = fixActionString(c, myMsg);
            c.getCommunicator().sendNormalServerMessage(myMsg);
        }
        if (stealthOthersMsg != null && c.isStealth()) {
            stealthOthersMsg = fixActionString(c, stealthOthersMsg);
            Server.getInstance().broadCastAction(stealthOthersMsg, c, 8);
        }
        else if (othersMsg != null) {
            othersMsg = fixActionString(c, othersMsg);
            Server.getInstance().broadCastAction(othersMsg, c, 8);
        }
    }

    public static void actionNotify(final Creature c, @Nullable final String myMsg, @Nullable final String othersMsg, @Nullable final String stealthOthersMsg) {
        actionNotify(c, myMsg, othersMsg, stealthOthersMsg, null);
    }

    public String getVersion() {
        return "v1.0";
    }

}
