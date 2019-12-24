package com.github.atlantidanetwork.api;

import com.github.atlantidanetwork.api.lib.plugin.AtlantidaPlugin;

public class Atlantida extends AtlantidaPlugin {

    public Atlantida() {
        super("api");
    }

    @Override
    public void onEnable() {
        msg("§6[AtlantidaAPI] §3Api habilitada.");
    }

    @Override
    public void onDisable() {
        msg("§6[AtlantidaAPI] §4Api desabilitada.");
    }
}
