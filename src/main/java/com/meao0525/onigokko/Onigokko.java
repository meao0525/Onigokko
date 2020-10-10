package com.meao0525.onigokko;

import com.meao0525.onigokko.command.CommandTabCompleter;
import com.meao0525.onigokko.command.GameCommand;
import com.meao0525.onigokko.game.Mode;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Timer;

public final class Onigokko extends JavaPlugin {

    //ゲーム中か?
    private boolean Gaming = false;
    //ゲームモード
    private Mode mode = Mode.ONIGOKKO;
    //マップ
    private WorldBorder border;
    private Location defaultCenter;
    private double defaultSize;
    private Location prison;
    private Location nigeStartloc;
    private Location oniStartloc;
    //時間
    private int time = 0;
    //鬼リスト
    private ArrayList<Player> oni = new ArrayList<>();
    //スコアボード
    private ScoreboardManager manager;
    private Scoreboard board;
    //チーム
    private Team nigeTeam;
    private Team oniTeam;
    //タイマー
    GameTimer timer;


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("鬼ごっこで遊べるぞ！「/onigo」で遊んでみよう！");
        getCommand("onigo").setExecutor(new GameCommand(this));
        getCommand("onigo").setTabCompleter(new CommandTabCompleter());

        //スコアボード設定
        manager = Bukkit.getScoreboardManager();
        board = manager.getMainScoreboard();
        //チーム設定
        nigeTeam = board.registerNewTeam("逃げチーム");
        nigeTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        nigeTeam.setAllowFriendlyFire(false);
        nigeTeam.setColor(ChatColor.BLUE);
        oniTeam = board.registerNewTeam("鬼チーム");
        oniTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        oniTeam.setAllowFriendlyFire(false);
        oniTeam.setColor(ChatColor.RED);

        //ボーダー取得
        border = getServer().getWorlds().get(0).getWorldBorder();
        defaultCenter = border.getCenter();
        defaultSize = border.getSize();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Onigokko disabled");
    }

    public void start() {
        //ゲームスタート
        Gaming = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (oni.contains(p)) {
                //鬼チームに所属
                oniTeam.addEntry(p.getName());
                //初期地点に移動
                p.teleport(oniStartloc);
            } else {
                //逃げチームに所属
                nigeTeam.addEntry(p.getName());
                //初期地点に移動
                p.teleport(nigeStartloc);
            }
            //効果音大事
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 3.0F, 3.0F);
        }
        //牢屋座標設定
        if ((mode.equals(Mode.KEIDORO))&&(prison == null)) {
            prison = oniStartloc;
            Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "監獄座標を鬼の初期地点に設定しました");
        }
        //タイマースタート
        timer = new GameTimer(time);
        timer.runTaskTimer(this, 0, 20);
    }

    public void end() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "ゲームが終了しました");
        //タイマー終了
        timer.cancel();
        //ボーダーリセット
        border.reset();
        for (Player p : Bukkit.getOnlinePlayers()) {
            //リスポーンを元に戻す
            p.setBedSpawnLocation(getCenter(), true);
            //全員中央に
            p.teleport(getCenter());
            //チーム解散
            if (oni.contains(p)) {
                oniTeam.removeEntry(p.getName());
            } else {
                nigeTeam.removeEntry(p.getName());
            }
        }
    }

    public boolean isGaming() {
        return Gaming;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Location getCenter() { return this.border.getCenter(); }

    public void setCenter(Location center) { this.border.setCenter(center); }

    public double getSize() { return this.border.getSize(); }

    public void setSize(double size) { this.border.setSize(size); }

    public Location getPrison() {
        return prison;
    }

    public void setPrison(Location prison) {
        this.prison = prison;
    }

    public Location getNigeStartloc() {
        return nigeStartloc;
    }

    public void setNigeStartloc(Location nigeStartloc) {
        this.nigeStartloc = nigeStartloc;
    }

    public Location getOniStartloc() {
        return oniStartloc;
    }

    public void setOniStartloc(Location oniStartloc) {
        this.oniStartloc = oniStartloc;
    }

    public ArrayList<Player> getOni() {
        return oni;
    }

    public Team getNigeTeam() {
        return nigeTeam;
    }

    public Team getOniTeam() {
        return oniTeam;
    }

    private class GameTimer extends BukkitRunnable {

        private int time;
        private double maxTime;
        private BossBar timerBar;

        GameTimer(int time) {
            this.time = time;
            this.maxTime = time;
            //ボスバー作成
            timerBar = Bukkit.createBossBar("残り時間:" + time + "s", BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
            //全プレイヤーにタイマー用ボスバーを表示
            for (Player p : Bukkit.getOnlinePlayers()) {
                timerBar.addPlayer(p);
            }
        }

        @Override
        public void run() {
            if (time > 0) {
                timerBar.setTitle("残り時間:" + time + "s");
                timerBar.setProgress(time/maxTime);
            } else {
                //ボスバー消す
                timerBar.removeAll();
                //ゲーム終了
                end();
            }
            //1秒減らす
            time--;
        }
    }
}
