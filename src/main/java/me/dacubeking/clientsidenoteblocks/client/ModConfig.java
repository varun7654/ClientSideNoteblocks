package me.dacubeking.clientsidenoteblocks.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "clientsidenoteblocks")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    boolean debug = false;

    @ConfigEntry.Gui.Tooltip
    boolean enabled = true;
}
