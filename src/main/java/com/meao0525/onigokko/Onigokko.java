package com.meao0525.onigokko;

import com.meao0525.onigokko.command.CommandTabCompleter;
import com.meao0525.onigokko.command.GameCommand;
import com.meao0525.onigokko.event.DefaultGameEvent;
import com.meao0525.onigokko.event.NigeTouchEvent;
import com.meao0525.onigokko.event.OniTouchEvent;
import com.meao0525.onigokko.game.Mode;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
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
    private Location center;
    private Location defaultCenter;
    private double defaultSize;
    private Location prison;
    private Location nigeStartloc;
    private Location oniStartloc;
    //時間
    private int time = 300;
    //鬼リスト
    private ArrayList<String> oni = new ArrayList<>();
    //スコアボード
    private ScoreboardManager manager;
    private Scoreboard board;
    //チーム
    private Team nigeTeam;
    private Team oniTeam;
    //タイマー
    GameTimer timer;
    private BossBar timerBar;


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
        nigeTeam = board.getTeam("nigeteam");
        oniTeam = board.getTeam("oniteam");
        if (nigeTeam == null) {
            nigeTeam = board.registerNewTeam("nigeteam");
            nigeTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            nigeTeam.setAllowFriendlyFire(true);
            nigeTeam.setColor(ChatColor.BLUE);
        }
        if (oniTeam == null) {
            oniTeam = board.registerNewTeam("oniteam");
            oniTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            oniTeam.setAllowFriendlyFire(false);
            oniTeam.setColor(ChatColor.RED);
        }

        //ボーダー取得
        border = getServer().getWorlds().get(0).getWorldBorder();
        defaultCenter = border.getCenter();
        defaultSize = border.getSize();
        //タイマーバー作成
        timerBar = Bukkit.createBossBar("残り時間:" + time + "s", BarColor.YELLOW, BarStyle.SOLID, BarFlag.CREATE_FOG);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Onigokko disabled");
    }

    public void reset() {
        //ボーダーをリセット
        border.reset();
        Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "ボーダーをリセットしました");
    }

    public void start() {
        //ゲームスタート
        Gaming = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (oni.contains(p.getName())) {
                //鬼になーれ
                makeOni(p);
                //初期地点に移動
                p.teleport(oniStartloc);
            } else {
                makeNige(p);
                //初期地点に移動
                p.teleport(nigeStartloc);
            }
            //タイマー用ボスバー表示
            timerBar.addPlayer(p);
            //効果音大事
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 3.0F, 3.0F);
        }
        //牢屋座標設定
        if ((mode.equals(Mode.KEIDORO))&&(prison == null)) {
            prison = oniStartloc;
            Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "監獄座標を鬼の初期地点に設定しました");
        }

        //イベント登録
        registerEvent();
        //タイマースタート
        timer = new GameTimer(time);
        timer.runTaskTimer(this, 0, 20);
    }

    public void end() {
        //ゲーム終了
        Gaming = false;
        Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "ゲームが終了しました");
        //タイマー終了
        timer.cancel();
        //結果発表おおおおおおお
        Bukkit.broadcastMessage(ChatColor.GOLD + "----------[どこでも鬼ごっこ]----------\n"
                + ChatColor.RESET + "↓↓ 最後まで逃げのびた人 ↓↓");
        for (Player p : Bukkit.getOnlinePlayers()) {
            //リスポーンを元に戻す
            p.setBedSpawnLocation(center, true);
            //全員中央に
            if (center != null) { p.teleport(center); }
            //ボスバー消す
            timerBar.removePlayer(p);
            //チーム解散
            if (oni.contains(p.getName())) {
                oniTeam.removeEntry(p.getName());
                //装備消す
                p.getInventory().setHelmet(new ItemStack(Material.AIR));
                p.getInventory().setChestplate(new ItemStack(Material.AIR));
                p.getInventory().setLeggings(new ItemStack(Material.AIR));
                p.getInventory().setBoots(new ItemStack(Material.AIR));
            } else {
                nigeTeam.removeEntry(p.getName());
                //捕まってなかった人は発表
                if (p.getWalkSpeed() != 0.0 && !p.isGlowing()) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + p.getName());
                }
                //足の速さ戻す
                p.setWalkSpeed(0.2F);
                //発行消す
                p.setGlowing(false);
            }
            //エフェクト
            p.sendTitle("", ChatColor.GOLD + "--- 終了---", 0, 60, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.3F, 0.5F);
        }
    }

    public void makeOni(Player player) {
        //鬼チームに所属
        oniTeam.addEntry(player.getName());
        //装備渡す
        player.getInventory().setHelmet(OnigoItem.ONI_HELMET.toItemStack());
        player.getInventory().setChestplate(OnigoItem.ONI_CHESTPLATE.toItemStack());
        player.getInventory().setLeggings(OnigoItem.ONI_LEGGINGS.toItemStack());
        player.getInventory().setBoots(OnigoItem.ONI_BOOTS.toItemStack());
        //鬼用スコアボードに変える
    }

    public void makeNige(Player player) {
        //逃げチームに所属
        nigeTeam.addEntry(player.getName());
        //装備消す
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        //逃げ用スコアボードに変える
    }

    public void registerEvent() {
        getServer().getPluginManager().registerEvents(new DefaultGameEvent(this), this);
        getServer().getPluginManager().registerEvents(new OniTouchEvent(this), this);
        getServer().getPluginManager().registerEvents(new NigeTouchEvent(this), this);
    }


    /* ↓↓↓ゲッターセッターヤッター↓↓↓ */
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

    public void setCenter(Location center) {
        this.center = center;
        this.border.setCenter(center);
    }

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

    public ArrayList<String> getOni() {
        return oni;
    }

    public Team getNigeTeam() {
        return nigeTeam;
    }

    public Team getOniTeam() {
        return oniTeam;
    }

    public BossBar getTimerBar() {
        return timerBar;
    }

    //タイマー用内部クラス
    private class GameTimer extends BukkitRunnable {

        private int time;
        private double maxTime;


        GameTimer(int time) {
            this.time = time;
            this.maxTime = time;
        }

        @Override
        public void run() {
            if (time > 0) {
                timerBar.setTitle("残り時間:" + time + "s");
                timerBar.setProgress(time/maxTime);
                if (time < 6) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.3F, 0.5F);
                    }
                }
            } else {
                //ゲーム終了
                end();
            }
            //1秒減らす
            time--;
        }
    }
}
