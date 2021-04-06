package com.meao0525.onigokko.command;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.Mode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameCommand implements CommandExecutor {

    private Onigokko plugin;
    private boolean flagInfo = false;

    public GameCommand(Onigokko plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String sub;
        //引数がない場合はhelpを実行させる
        if (args.length == 0) {
            sub = "help";
        } else {
            sub = args[0];
        }
        if (plugin.isGaming() && !(sub.equalsIgnoreCase("stop"))) {
            //ゲーム中
            sender.sendMessage(ChatColor.GRAY + "このコマンドはゲーム中に使用できません");
            return true;
        }
        switch (sub) {
            case "help":
                sender.sendMessage(ChatColor.GOLD + "==========[どこでも鬼ごっこ]===========\n" + ChatColor.RESET +
                        "/onigo help --- コマンドの使用方法\n" +
                        "/onigo info --- ゲーム情報の確認\n" +
                        "/onigo set --- ゲームの設定変更\n" +
                        "           nigestartloc <xyz> --- 逃げの初期地点\n" +
                        "           onistartloc <xyz> --- 鬼の初期地点\n" +
                        "           center <xyz> --- マップ中心地点\n" +
                        "           size <int> --- マップ一辺の長さ\n" +
                        "           time <second> --- ゲーム時間\n" +
                        "           prison <xyz> --- ケイドロ用牢屋地点\n" +
                        "/onigo mode <mode> --- 鬼ごっこゲームモードの設定\n" +
                        "/onigo oni <name> --- 鬼の選出\n" +
                        "/onigo reset --- ゲーム設定のリセット\n" +
                        "/onigo start --- ゲームスタート\n" +
                        "/onigo stop --- ゲームを強制終了");
                break;

            case "info":
                commandInfo(sender);
                break;

            case "set":
                if (sender instanceof Player) {
                    return commandSet((Player)sender, args);
                } else {
                    sender.sendMessage(ChatColor.GRAY + "このコマンドはプレイヤーのみ使用可能です");
                }
                break;

            case "mode":
                return commandMode(sender, args);

            case "oni":
                return commandOni(sender, args);

            case "reset":
                plugin.reset();
                reloadInfo();
                break;

            case "start":
                //設定漏れはないか
                if (plugin.getNigeStartloc().isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "逃げの初期地点が設定されていません");
                } else if (plugin.getOniStartloc().isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "鬼の初期地点が設定されていません");
                } else if (plugin.getOni().isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "おいおい、鬼がいねーぞ");
                } else {
                    //スタート処理
                    plugin.start();
                }
                break;

            case "stop":
                if (plugin.isGaming()) {
                    //強制終了処理
                    plugin.getServer().broadcastMessage(ChatColor.RED + "ゲームを強制終了しました");
                    plugin.end();
                } else {
                    sender.sendMessage(ChatColor.GRAY + "終了するゲームがありません");
                }
                break;
        }
        return true;
    }

    public void commandInfo(CommandSender sender) {
        //情報をスコアボードに表示
        if (sender instanceof Player) {
            Player psender = (Player)sender;
            //表示切替
            flagInfo = !flagInfo;
            if (flagInfo) {
                //Info用のボードにする
                psender.setScoreboard(plugin.getInfo());
                //スコアの表示
                reloadInfo();
            } else {
                //元のボードに戻す
                psender.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        } else {
            //表示用文字列
            String infoMsg = ChatColor.GOLD + "==========[どこでも鬼ごっこ]===========\n"
                    + ChatColor.RESET + "ゲームモード: " + ChatColor.AQUA + plugin.getMode().toString() + "\n"
                    + ChatColor.RESET + "マップ中心地点: " + ChatColor.AQUA + plugin.getCenter().getX() + " " + plugin.getCenter().getZ() + "\n"
                    + ChatColor.RESET + "マップ範囲: " + ChatColor.AQUA + plugin.getSize() + "\n"
                    + ChatColor.RESET + "ゲーム時間: " + ChatColor.AQUA + plugin.getTime() + "\n" + ChatColor.RESET;
            //未設定のときは表示しない
            if (plugin.getPrison() != null) {
                infoMsg += ChatColor.RESET + "監獄座標: " +
                        ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " +plugin.getPrison().getZ() + "\n";
            }
//            if (plugin.getNigeStartloc() != null) { infoMsg += ChatColor.RESET + "逃げの初期地点: " + ChatColor.AQUA + plugin.getNigeStartloc().getX() + " " + plugin.getNigeStartloc().getY() + " " + plugin.getNigeStartloc().getZ() + "\n"; }
//            if (plugin.getOniStartloc() != null) { infoMsg += ChatColor.RESET + "鬼の初期地点: " + ChatColor.AQUA + plugin.getOniStartloc().getX() + " " + plugin.getOniStartloc().getY() + " " + plugin.getOniStartloc().getZ() + "\n"; }

            //鬼のプレイヤー名表示
            infoMsg += ChatColor.GOLD + "==========[鬼プレイヤー]==========\n" + ChatColor.RESET;
            for (String n : plugin.getOni()) {
                infoMsg += n + "\n";
            }
            //info送信
            sender.sendMessage("\n" + infoMsg);
        }
    }

    public boolean commandSet(Player sender, String[] args) {
        //引数違い
        if (!(args.length > 1)) { return false; }

        switch (args[1]) {
            case "nigestartloc":
                if (args.length == 2) {
                    //コマンド使用者の現在地の座標を取得(intで取得するため)
                    Location loc = sender.getLocation().getBlock().getLocation();
                    plugin.getNigeStartloc().add(loc);
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "逃げの初期地点に現在座標\n"
                            + ChatColor.AQUA + loc.getX() + " " + loc.getY() + " " + loc.getZ() + "\n"
                            + ChatColor.RESET + "を追加しました");
                    //infoの更新
                    reloadInfo();
                    return true;
                }
                break;

            case "onistartloc":
                if (args.length == 2) {
                    //コマンド使用者の現在地の座標を取得(intで取得するため)
                    Location loc = sender.getLocation().getBlock().getLocation();
                    plugin.getOniStartloc().add(loc);
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼の初期地点を現在座標\n"
                            + ChatColor.AQUA + loc.getX() + " " + loc.getY() + " " + loc.getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
                    //infoの更新
                    reloadInfo();
                    return true;
                }
                break;

            case "center":
                if (args.length == 2) {
                    //センター設定(コマンド使用者の現在地の座標を取得)
                    plugin.setCenter(sender.getLocation().getBlock().getLocation());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "中心地点を現在座標\n"
                            + ChatColor.AQUA + plugin.getCenter().getX() + " " + plugin.getCenter().getY() + " " + plugin.getCenter().getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
                    //infoの更新
                    reloadInfo();
                    return true;
                }
                break;

            case "prison":
                if (args.length == 2) {
                    //コマンド使用者の現在地の座標を取得(intで取得するため)
                    plugin.setPrison(sender.getLocation().getBlock().getLocation());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "牢屋地点を現在座標\n"
                            + ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " + plugin.getPrison().getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
                    //infoの更新
                    reloadInfo();
                    return true;
                }
                break;

            case "size":
                //第3引数の数値取得
                if (args.length == 3) {
                    try {
                        //ボーダーサイズ設定
                        plugin.setSize(Double.parseDouble(args[2]));
                        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "マップの広さを "
                                + ChatColor.AQUA + args[2] + ChatColor.RESET + " に設定しました");
                        //infoの更新
                        reloadInfo();
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.GRAY + "第3引数には整数を入力してください");
                    }
                    return true;
                }
                break;

            case "time":
                if (args.length == 3) {
                    try {
                        plugin.setTime(Integer.parseInt(args[2]));
                        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "ゲーム時間を "
                                + ChatColor.AQUA + args[2] + ChatColor.RESET + " 秒に設定しました");
                        //infoの更新
                        reloadInfo();
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.GRAY + "第3引数には整数を入力してください");
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean commandMode(CommandSender sender, String[] args) {
        //引数違い
        if (!(args.length == 2)) { return false; }
        switch (args[1]) {
            case "gokko":
                plugin.setMode(Mode.ONIGOKKO);
                break;
            case "keidoro":
                plugin.setMode(Mode.KEIDORO);
                break;
            case "fueoni":
                plugin.setMode(Mode.FUEONI);
                break;
            case "koorioni":
                plugin.setMode(Mode.KOORIONI);
                break;
            default:
                return false;
        }

        //infoの更新
        reloadInfo();

        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼ごっこゲームモードを "
                + ChatColor.AQUA + plugin.getMode().toString() + ChatColor.RESET + " に設定しました");
        return true;
    }

    public boolean commandOni(CommandSender sender, String[] args) {
        //引数違い
        if (!(args.length == 2)) { return false; }

        if (args[1].equalsIgnoreCase("clear")) {
            //リストの初期化
            plugin.getOni().clear();
            sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼のリストを初期化しました");

            //infoの更新
            reloadInfo();

        } else if (args[1].equalsIgnoreCase("random")) {
            //プレイヤーのリスト作成
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            //シャッフル
            Collections.shuffle(players);
            //すでに鬼じゃなきゃやらせる
            for (Player p : players) {
                if (!plugin.getOni().contains(p.getName())) {
                    plugin.getOni().add(p.getName());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.AQUA
                            + p.getName() + ChatColor.RESET + " を鬼に追加しました");

                    //infoの更新
                    reloadInfo();
                    return true;
                }
            }
            sender.sendMessage(ChatColor.GRAY + " 全員鬼とかある！？");

        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                //名前の一致するプレイヤーを鬼リストにいれる
                if (args[1].equalsIgnoreCase(p.getName())) {
                    if (!plugin.getOni().contains(p.getName())) {
                        plugin.getOni().add(p.getName());
                        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.AQUA
                                + args[1] + ChatColor.RESET + " を鬼に追加しました");

                        //infoの更新
                        reloadInfo();
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET
                                + args[1] + ChatColor.GRAY + " はすでに鬼です");
                    }

                }
            }
            sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET
                    + args[1] + ChatColor.GRAY + " というプレイヤーが見つかりません");
        }
        return true;
    }

    public void  reloadInfo() {
        //基本情報
        Objective obj = plugin.getInfo().getObjective("info");
        //一度消す
        if (obj != null) {
            obj.unregister();
        }
        //再登録
        obj = plugin.getInfo().registerNewObjective("info", "dummy", ChatColor.GOLD + "=====[どこでも鬼ごっこ]=====");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int i = 0;
        Score score = obj.getScore("ゲームモード: " + ChatColor.AQUA + plugin.getMode().toString());
        score.setScore(i--);
        score = obj.getScore("マップ中心地点: " + ChatColor.AQUA + plugin.getCenter().getX() + " " + plugin.getCenter().getZ());
        score.setScore(i--);
        score = obj.getScore("マップ範囲: " + ChatColor.AQUA + plugin.getSize());
        score.setScore(i--);
        score = obj.getScore("ゲーム時間: " + ChatColor.AQUA + plugin.getTime());
        score.setScore(i--);
        //値がないときは表示せぬものたち
        if (plugin.getPrison() != null) {
            score = obj.getScore("監獄座標: " + ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " +plugin.getPrison().getZ());
            score.setScore(i--);
        }
        if (!plugin.getNigeStartloc().isEmpty()) {
            score = obj.getScore("逃げの初期地点: ");
            score.setScore(i--);
            //逃げ初期地点リスト
            for (Location l : plugin.getNigeStartloc()) {
                score = obj.getScore(ChatColor.AQUA + "" + l.getX() + " " + l.getY() + " " + l.getZ());
                score.setScore(i--);
            }
        }
        if (!plugin.getOniStartloc().isEmpty()) {
            score = obj.getScore("鬼の初期地点: ");
            score.setScore(i--);
            //鬼初期地点リスト
            for (Location l : plugin.getOniStartloc()) {
                score = obj.getScore(ChatColor.AQUA + "" + l.getX() + " " + l.getY() + " " + l.getZ());
                score.setScore(i--);
            }
        }

        //鬼プレイヤー
        score = obj.getScore(ChatColor.GOLD + "=====[鬼プレイヤー]=====");
        score.setScore(i--);
        //鬼プレイヤーを登録していくぅ
        for (String n : plugin.getOni()) {
            score = obj.getScore(n);
            score.setScore(i--);
        }
    }
}
