// MIT License
//
// Copyright (c) 2022 kiinse
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package kiinse.me.plugins.minecore;

import kiinse.me.plugins.minecore.api.MineCorePlugin;
import kiinse.me.plugins.minecore.api.MineCoreMain;
import kiinse.me.plugins.minecore.api.commands.CommandFailureHandler;
import kiinse.me.plugins.minecore.api.exceptions.PluginException;
import kiinse.me.plugins.minecore.api.exceptions.VersioningException;
import kiinse.me.plugins.minecore.api.files.enums.Config;
import kiinse.me.plugins.minecore.api.files.filemanager.YamlFile;
import kiinse.me.plugins.minecore.api.files.locale.LocaleStorage;
import kiinse.me.plugins.minecore.api.files.locale.PlayerLocales;
import kiinse.me.plugins.minecore.api.files.messages.Messages;
import kiinse.me.plugins.minecore.api.files.messages.MessagesUtils;
import kiinse.me.plugins.minecore.api.files.statistic.StatisticManager;
import kiinse.me.plugins.minecore.api.indicators.IndicatorManager;
import kiinse.me.plugins.minecore.api.loader.PluginManager;
import kiinse.me.plugins.minecore.api.schedulers.SchedulersManager;
import kiinse.me.plugins.minecore.api.utilities.TaskType;
import kiinse.me.plugins.minecore.plugin.gui.LocaleFlags;
import kiinse.me.plugins.minecore.plugin.gui.LocaleGUI;
import kiinse.me.plugins.minecore.plugin.initialize.LoadAPI;
import kiinse.me.plugins.minecore.plugin.initialize.RegisterCommands;
import kiinse.me.plugins.minecore.plugin.initialize.RegisterEvents;
import kiinse.me.plugins.minecore.lib.commands.FailureHandler;
import kiinse.me.plugins.minecore.lib.files.locale.MineLocaleStorage;
import kiinse.me.plugins.minecore.lib.files.locale.MinePlayerLocales;
import kiinse.me.plugins.minecore.lib.files.messages.MineMessages;
import kiinse.me.plugins.minecore.lib.files.messages.MineMessagesUtils;
import kiinse.me.plugins.minecore.lib.files.statistic.MineStatisticManager;
import kiinse.me.plugins.minecore.lib.indicators.MineIndicatorManager;
import kiinse.me.plugins.minecore.lib.loader.MinePluginManager;
import kiinse.me.plugins.minecore.lib.schedulers.MineSchedulersManager;
import kiinse.me.plugins.minecore.lib.schedulers.minecore.IndicatorSchedule;
import kiinse.me.plugins.minecore.lib.schedulers.minecore.JumpSchedule;
import kiinse.me.plugins.minecore.lib.schedulers.minecore.MoveSchedule;
import kiinse.me.plugins.minecore.lib.utilities.MineUtils;
import kiinse.me.plugins.minecore.lib.utilities.MineVersionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

@SuppressWarnings({"unused"})
public final class MineCore extends MineCorePlugin implements MineCoreMain {

    private static MineCore instance;
    private boolean isDebug;
    private PluginManager pluginManager;
    private LocaleStorage localeStorage;
    private PlayerLocales locales;
    private StatisticManager mineStatistic;
    private IndicatorManager indicatorManager;
    private SchedulersManager schedulersManager;

    public static @NotNull MineCoreMain getInstance() {
        return instance;
    }

    @Override
    protected void start() throws PluginException {
        try {
            isDebug = false;
            getLogger().setLevel(Level.CONFIG);
            sendLog("Loading " + getName() + "...");
            onStart();
            getPluginManager().registerPlugin(this);
            sendInfo();
            checkForUpdates();
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    @Override
    public void onStart() throws Exception {
        MineCore.instance = this;
        setMineCore(this);
        super.setConfiguration(new YamlFile(this, getConfigurationFileName()));
        isDebug = Objects.requireNonNull(getConfiguration()).getBoolean(Config.DEBUG);
        new LoadAPI(this);
        localeStorage = new MineLocaleStorage(this).load();
        locales = new MinePlayerLocales(this, localeStorage);
        super.setMessages(getMessages(this));
        mineStatistic = new MineStatisticManager(this);
        indicatorManager = new MineIndicatorManager(this);
        schedulersManager = new MineSchedulersManager(this);
        pluginManager = new MinePluginManager(this);
        schedulersManager
                .register(new JumpSchedule(this))
                .register(new MoveSchedule(this))
                .register(new IndicatorSchedule(this));
        LocaleGUI.Companion.setLocaleFlags(new LocaleFlags(this));
        new RegisterCommands(this);
        new RegisterEvents(this);
    }

    @Override
    public void onStop() throws Exception {
        localeStorage.save();
        mineStatistic.save();
        schedulersManager.stopSchedulers();
    }

    @Override
    public void restart() {
        try {
            sendLog("Reloading " + getName() + "!");
            localeStorage.save();
            mineStatistic.save();
            Objects.requireNonNull(getMessages()).reload();
            Objects.requireNonNull(getConfiguration()).reload();
            isDebug = getConfiguration().getBoolean(Config.DEBUG);
            localeStorage.load();
            locales = new MinePlayerLocales(this, localeStorage);
            mineStatistic = new MineStatisticManager(this);
            sendLog(getName() + " reloaded!");
        } catch (Exception e) {
            sendLog(Level.SEVERE, "Error on reloading " + getName() + "! Message:", e);
        }
    }

    private void checkForUpdates() {
        MineUtils.INSTANCE.runTask(TaskType.ASYNC, this, () -> {
            if (!getConfiguration().getBoolean(Config.DISABLE_VERSION_CHECK)) {
                try {
                    var latest = MineVersionUtils.INSTANCE.getLatestGithubVersion("https://github.com/kiinse/MineCore");
                    if (!latest.isGreaterThan(MineVersionUtils.INSTANCE.getPluginVersion(this))) {
                        sendLog("Latest version of MineCore installed, no new versions found <3");
                        return;
                    }
                    var reader = new BufferedReader(new InputStreamReader(
                            Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("version-message.txt"))));
                    var builder = new StringBuilder("\n");
                    while (reader.ready()) {
                        builder.append(reader.readLine()).append("\n");
                    }
                    sendConsole(MineUtils.INSTANCE.replaceWord(builder.toString(), new String[]{
                            "{NEW_VERSION}:" + latest.getOriginalValue(),
                            "{CURRENT_VERSION}:" + getDescription().getVersion()
                    }));
                } catch (IOException | VersioningException e) {
                    sendLog("Error while checking MineCore version! Message:", e);
                }
            }
        });
    }

    @Override
    public @NotNull LocaleStorage getLocaleStorage() {
        return localeStorage;
    }

    @Override
    public @NotNull PlayerLocales getPlayerLocales() {
        return locales;
    }

    @Override
    public @NotNull Messages getMessages(@NotNull MineCorePlugin plugin) {
        return new MineMessages(plugin);
    }

    @Override
    public @NotNull MessagesUtils getMessagesUtils(@NotNull MineCorePlugin plugin) {
        return new MineMessagesUtils(plugin);
    }

    @Override
    public @NotNull StatisticManager getMineStatistic() {
        return mineStatistic;
    }

    @Override
    public @NotNull PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public @NotNull IndicatorManager getIndicatorManager() {
        return indicatorManager;
    }

    @Override
    public @NotNull SchedulersManager getSchedulersManager() {
        return schedulersManager;
    }

    @Override
    public @NotNull CommandFailureHandler getCommandFailureHandler() {
        return new FailureHandler(this);
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }
}
