package com.meao0525.onigokko;

import com.meao0525.onigokko.command.CommandTabCompleter;
import com.meao0525.onigokko.command.GameCommand;
import com.meao0525.onigokko.game.Mode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
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
    private Location prison;
    private Location nigeStartloc;
    private Location oniStartloc;
    //時間
    private int time = 0;
    //鬼リスト
    private ArrayList<Player> oni = new ArrayList<>();
    //チーム
    private Team nigeTeam;
    private Team oniTeam;
    //タイマー
    Timer gameTimer;


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("鬼ごっこで遊べるぞ！「/onigo」で遊んでみよう！");
        getCommand("onigo").setExecutor(new GameCommand(this));
        getCommand("onigo").setTabCompleter(new CommandTabCompleter());

        //チーム設定
        nigeTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        nigeTeam.setAllowFriendlyFire(false);
        nigeTeam.setColor(ChatColor.BLUE);
        oniTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        oniTeam.setAllowFriendlyFire(false);
        oniTeam.setColor(ChatColor.RED);

        //ボーダー取得
        border = getServer().getWorlds().get(0).getWorldBorder();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Onigokko disabled");
    }

//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        String sub;
//        //引数がない場合はhelpを実行させる
//        if (args.length == 0) {
//            sub = "help";
//        } else {
//            sub = args[0];
//        }
//        switch (sub) {
//            case "help":
//                sender.sendMessage(ChatColor.GOLD + "==========[どこでも鬼ごっこ]===========\n" + ChatColor.RESET +
//                        "/onigo help --- コマンドの使用方法\n" +
//                        "/onigo info --- ゲーム情報の確認\n" +
//                        "/onigo set --- ゲームの設定変更\n" +
//                        "           nigestartloc <xyz> --- 逃げの初期地点\n" +
//                        "           onistartloc <xyz> --- 鬼の初期地点\n" +
//                        "           center <xyz> --- マップ中心地点\n" +
//                        "           size <int> --- マップ一辺の長さ\n" +
//                        "           time <second> --- ゲーム時間\n" +
//                        "           prison <xyz> --- ケイドロ用牢屋地点\n" +
//                        "/onigo mode <mode> --- 鬼ごっこゲームモードの設定\n" +
//                        "/onigo oni <name> --- 鬼の選出\n" +
//                        "/onigo start --- ゲームスタート");
//                break;
//
//            case "info":
//                //表示用文字列
//                String infoMsg = ChatColor.GOLD + "==========[どこでも鬼ごっこ]===========\n"
//                        + ChatColor.RESET + "鬼ごっこゲームモード: " + ChatColor.AQUA + mode.toString() + "\n"
//                        + ChatColor.RESET + "マップ中心地点: " + ChatColor.AQUA + center.getX() + " " + center.getY() + " " + center.getZ() + "\n"
//                        + ChatColor.RESET + "マップ範囲: " + ChatColor.AQUA + size + "\n"
//                        + ChatColor.RESET + "ゲーム時間: " + ChatColor.AQUA + time + "\n" + ChatColor.RESET;
//                //未設定のときは表示しない
//                if (prison != null) { infoMsg += ChatColor.RESET + "監獄座標: " + ChatColor.AQUA + prison.getX() + " " + prison.getY() + " " + prison.getZ() + "\n"; }
//                if (nigeStartloc != null) { infoMsg += ChatColor.RESET + "逃げの初期地点: " + ChatColor.AQUA + nigeStartloc.getX() + " " + nigeStartloc.getY() + " " + nigeStartloc.getZ() + "\n"; }
//                if (oniStartloc != null) { infoMsg += ChatColor.RESET + "鬼の初期地点: " + ChatColor.AQUA + oniStartloc.getX() + " " + oniStartloc.getY() + " " + oniStartloc.getZ() + "\n"; }
//                infoMsg += ChatColor.GOLD + "==========[鬼プレイヤー]==========\n";
//                //info送信
//                sender.sendMessage(infoMsg);
//                //鬼のプレイヤー名表示
//                for (Player p : oni) {
//                    sender.sendMessage(p.getDisplayName() + " ");
//                }
//                break;
//
//            case "set":
//                if (!(sender instanceof Player)) {
//                    return commandSet((Player)sender, args);
//                } else {
//                    sender.sendMessage(ChatColor.GRAY + "このコマンドはプレイヤーのみ使用可能です");
//                }
//                break;
//
//            case "mode":
//                return commandMode(sender, args);
//
//            case "oni":
//                return commandOni(sender, args);
//
//            case "start":
//                //TODO: スタート処理
//                break;
//        }
//        return true;
//    }
//
//    public boolean commandSet(Player sender, String[] args) {
//        //引数違い
//        if (!(args.length > 1)) { return false; }
//
//        switch (args[1]) {
//            case "nigestartloc":
//                if (args.length == 2) {
//                    //コマンド使用者の現在地の座標を取得(intで取得するため)
//                    nigeStartloc = sender.getLocation().getBlock().getLocation();
//                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "逃げの初期地点を現在座標\n"
//                            + ChatColor.AQUA + nigeStartloc.getX() + " " + nigeStartloc.getY() + " " + nigeStartloc.getZ() + "\n"
//                            + ChatColor.RESET + "に設定しました");
//                    return true;
//                }
//                break;
//
//            case "onistartloc":
//                if (args.length == 2) {
//                    //コマンド使用者の現在地の座標を取得(intで取得するため)
//                    oniStartloc = sender.getLocation().getBlock().getLocation();
//                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼の初期地点を現在座標\n"
//                            + ChatColor.AQUA + oniStartloc.getX() + " " + oniStartloc.getY() + " " + oniStartloc.getZ() + "\n"
//                            + ChatColor.RESET + "に設定しました");
//                    return true;
//                }
//                break;
//
//            case "center":
//                if (args.length == 2) {
//                    //コマンド使用者の現在地の座標を取得(intで取得するため)
//                    center = sender.getLocation().getBlock().getLocation();
//                    //センター設定
//                    center.getWorld().getWorldBorder().setCenter(center);
//                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "中心地点を現在座標\n"
//                            + ChatColor.AQUA + center.getX() + " " + center.getY() + " " + center.getZ() + "\n"
//                            + ChatColor.RESET + "に設定しました");
//                    return true;
//                }
//                break;
//
//            case "prison":
//                if (args.length == 2) {
//                    //コマンド使用者の現在地の座標を取得(intで取得するため)
//                    prison = sender.getLocation().getBlock().getLocation();
//                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "牢屋地点を現在座標\n"
//                            + ChatColor.AQUA + prison.getX() + " " + prison.getY() + " " + prison.getZ() + "\n"
//                            + ChatColor.RESET + "に設定しました");
//                    return true;
//                }
//                break;
//
//            case "size":
//                //第3引数の数値取得
//                if (args.length == 3) {
//                    try {
//                        size = Integer.parseInt(args[2]);
//                        //ボーダーサイズ設定
//                        center.getWorld().getWorldBorder().setSize(size);
//                        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "マップの広さを "
//                                + ChatColor.AQUA + args[2] + ChatColor.RESET + " に設定しました");
//                    } catch (NumberFormatException e) {
//                        sender.sendMessage(ChatColor.GRAY + "第3引数には整数を入力してください");
//                    }
//                    return true;
//                }
//                break;
//
//            case "time":
//                if (args.length == 3) {
//                    try {
//                        time = Integer.parseInt(args[2]);
//                        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "ゲーム時間を "
//                                + ChatColor.AQUA + args[2] + ChatColor.RESET + " 秒に設定しました");
//                    } catch (NumberFormatException e) {
//                        sender.sendMessage(ChatColor.GRAY + "第3引数には整数を入力してください");
//                    }
//                    return true;
//                }
//                break;
//        }
//        return false;
//    }
//
//    public boolean commandMode(CommandSender sender, String[] args) {
//        //引数違い
//        if (!(args.length == 2)) { return false; }
//        switch (args[1]) {
//            case "gokko":
//                mode = Mode.ONIGOKKO;
//                break;
//            case "keidoro":
//                mode = Mode.KEIDORO;
//                break;
//            case "fueoni":
//                mode = Mode.FUEONI;
//                break;
//            case "koorioni":
//                mode = Mode.KOORIONI;
//                break;
//            default:
//                return false;
//        }
//        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼ごっこゲームモードを "
//                + ChatColor.AQUA + mode.toString() + ChatColor.RESET + " に設定しました");
//        return true;
//    }
//
//    public boolean commandOni(CommandSender sender, String[] args) {
//        //引数違い
//        if (!(args.length == 2)) { return false; }
//
//        if (args[1].equalsIgnoreCase("clear")) {
//            //リストの初期化
//            oni.clear();
//            sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼のリストを初期化しました");
//        } else {
//            for (Player p : Bukkit.getOnlinePlayers()) {
//                //名前の一致するプレイヤーを鬼リストにいれる
//                if (args[1].equalsIgnoreCase(p.getName())) {
//                    oni.add(p);
//                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.AQUA
//                            + args[1] + ChatColor.RESET + " を鬼に追加しました");
//                    return true;
//                }
//            }
//            sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET
//                    + args[1] + ChatColor.GRAY + " というプレイヤーが見つかりません");
//        }
//        return true;
//    }

    public void start() {
        //ゲームスタート
        Gaming = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (oni.contains(p)) {
                //鬼チームに所属
                p.getScoreboard().registerNewTeam(oniTeam.getName());
                //初期地点に移動
                p.teleport(oniStartloc);
            } else {
                //逃げチームに所属
                p.getScoreboard().registerNewTeam(nigeTeam.getName());
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
    }

    public void end() {
        //全員のスポーンを中央に戻す
        //全員中央に
        //チーム解散
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
}
