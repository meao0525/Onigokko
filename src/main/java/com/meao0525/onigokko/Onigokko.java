package com.meao0525.onigokko;

import com.meao0525.onigokko.command.CommandTabCompleter;
import com.meao0525.onigokko.command.GameCommand;
import com.meao0525.onigokko.event.*;
import com.meao0525.onigokko.game.Mode;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collections;

public final class Onigokko extends JavaPlugin {

    //ゲーム中か?
    private boolean Gaming = false;

    /*---ゲームの設定---*/
    //ゲームモード
    private Mode mode = Mode.ONIGOKKO;
    //マップ
    private WorldBorder border;
    private Location center;
    private Location prison;
    private ArrayList<Location> nigeStartloc = new ArrayList<>();
    private ArrayList<Location> oniStartloc = new ArrayList<>();
    //時間
    private int time = 300;
    //鬼リスト
    private ArrayList<String> oni = new ArrayList<>();
    //足の速さ
    private int nigeSpeed = 0;
    private int oniSpeed = 0;
    //アイテム
    private ArrayList<OnigoItem> itemList = new ArrayList<>();
    /*------------------*/

    //スコアボード
    private ScoreboardManager manager;
    private Scoreboard board; //ゲーム用
    private Scoreboard info; //設定用
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
        board = manager.getNewScoreboard();
        info = manager.getNewScoreboard();
        //登録
        registerTeam(board);


        //ボーダー取得
        border = getServer().getWorlds().get(0).getWorldBorder();
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
        //初期地点リセット
        prison = null;
        nigeStartloc.clear();
        oniStartloc .clear();

        Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "座標設定をリセットしました");
    }

    public void start() {
        //ゲームスタート
        Gaming = true;
        Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]"
                + ChatColor.AQUA + mode.toString() + ChatColor.RESET + " スタート！");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (oni.contains(p.getName())) {
                //鬼になーれ
                makeOni(p);
                //ランダムな初期地点に移動
                p.teleport(getRandomLoc(oniStartloc));
                //足の速さを設定
                setGameWalkSpeed(p, oniSpeed);
                //タイトル
                p.sendTitle("", ChatColor.RED + "- あなたは鬼チームです -", 10, 70, 20);
            } else {
                //残りのアドベンチャーの人を逃げチームにする
                if (p.getGameMode() != GameMode.ADVENTURE) {
                    continue;
                }
                makeNige(p);
                //ランダムな初期地点に移動
                p.teleport(getRandomLoc(nigeStartloc));
                //足の速さを設定
                setGameWalkSpeed(p, nigeSpeed);
                //タイトル
                p.sendTitle("", ChatColor.AQUA + "- あなたは逃げチームです -", 10, 70, 20);
            }
            //タイマー用ボスバー表示
            timerBar.addPlayer(p);
            //合図は大事
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 3.0F, 3.0F);
            //ゲーム用スコアボードにする
            p.setScoreboard(board);
        }
        //牢屋座標設定
        if ((mode.equals(Mode.KEIDORO))&&(prison == null)) {
            prison = getRandomLoc(oniStartloc);
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
            //無所属は無視
            if (!oniTeam.hasEntry(p.getName()) && !nigeTeam.hasEntry(p.getName())) {
                continue;
            }

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
                //リスポーンアイテム消す
                p.getInventory().remove(OnigoItem.ONI_RESPAWN.toItemStack());
            } else {
                nigeTeam.removeEntry(p.getName());
                //捕まってなかった人は発表
                if (p.getWalkSpeed() != 0.0 && !p.isGlowing()) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + p.getName());
                }
                //発行消す
                p.setGlowing(false);
            }
            //オニゴアイテム消す
            removeOnigoItems(p);
            //足の速さ戻す
            p.setWalkSpeed(0.2F);
            //プレイヤーリストの色戻す
            p.setPlayerListName(ChatColor.RESET + p.getName());
            //エフェクト
            p.sendTitle("", ChatColor.GOLD + "--- 終了---", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.3F, 0.5F);
            //元のスコアボードに戻す
            p.setScoreboard(manager.getMainScoreboard());
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
        //リスポーンアイテム
        player.getInventory().addItem(OnigoItem.ONI_RESPAWN.toItemStack());
        //オニゴアイテム渡す
        giveOnigoItems(player);
        //プレイヤーリストの色
        player.setPlayerListName(oniTeam.getColor() + player.getName());
        //発光消す
        player.removePotionEffect(PotionEffectType.GLOWING);
    }

    public void makeNige(Player player) {
        //逃げチームに所属
        nigeTeam.addEntry(player.getName());
        //装備消す
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        //リスポーンアイテム消す
        player.getInventory().remove(OnigoItem.ONI_RESPAWN.toItemStack());
        //オニゴアイテム渡す
        giveOnigoItems(player);
        //プレイヤーリストの色
        player.setPlayerListName(nigeTeam.getColor() + player.getName());
    }

    public void registerEvent() {
        HandlerList.unregisterAll(this);
        getServer().getPluginManager().registerEvents(new DefaultGameEvent(this), this);
        getServer().getPluginManager().registerEvents(new OniTouchEvent(this), this);
        getServer().getPluginManager().registerEvents(new NigeTouchEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnigoPearlThrowEvent(this), this);
        getServer().getPluginManager().registerEvents(new OniRespawnEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnigoGlowingEvent(this), this);
    }

    public void registerTeam(Scoreboard board) {
        //チーム設定
        nigeTeam = board.getTeam("nigeteam");
        oniTeam = board.getTeam("oniteam");
        if (nigeTeam == null) {
            nigeTeam = board.registerNewTeam("nigeteam");
            nigeTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            nigeTeam.setAllowFriendlyFire(true);
            nigeTeam.setColor(ChatColor.BLUE);
        }
        if (oniTeam == null) {
            oniTeam = board.registerNewTeam("oniteam");
            oniTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            oniTeam.setAllowFriendlyFire(false);
            oniTeam.setColor(ChatColor.RED);
        }
    }

    public Location getRandomLoc(ArrayList<Location> list) {
        //シャッフル
        Collections.shuffle(list);
        //始めの値を返す
        return list.get(0);
    }

    public void setGameWalkSpeed(Player player, int speed) {
        //速さは5段階
        float[] speedList = {0.1F, 0.2F, 0.3F, 0.4F, 0.5F};
        //speedには-1～3の値が入っている
        player.setWalkSpeed(speedList[speed + 1]);
    }

    public void giveOnigoItems(Player player) {
        //itemListのアイテムたちを放り込む
        for (OnigoItem oi : itemList) {
            //それぞれのチームが持つべきアイテムか？
            if ((oi.isNige() && nigeTeam.hasEntry(player.getName()))
                || (oi.isOni() && oniTeam.hasEntry(player.getName()))) {
                //一回消してから渡す
                player.getInventory().remove(oi.toItemStack());
                player.getInventory().addItem(oi.toItemStack());
            } else {
                //そうじゃないときは消す
                player.getInventory().remove(oi.toItemStack());
            }
        }
    }

    public void removeOnigoItems(Player player) {
        //itemListのアイテムたちを消す(valuesじゃなくてもいいよね)
        for (OnigoItem oi : itemList) {
            player.getInventory().remove(oi.toItemStack());
        }
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

    public ArrayList<Location> getNigeStartloc() {
        return nigeStartloc;
    }

    public ArrayList<Location> getOniStartloc() {
        return oniStartloc;
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

    public Scoreboard getInfo() {
        return info;
    }

    public int getNigeSpeed() {
        return nigeSpeed;
    }

    public void setNigeSpeed(int nigeSpeed) {
        this.nigeSpeed = nigeSpeed;
    }

    public int getOniSpeed() {
        return oniSpeed;
    }

    public void setOniSpeed(int oniSpeed) {
        this.oniSpeed = oniSpeed;
    }

    public ArrayList<OnigoItem> getItemList() {
        return itemList;
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
