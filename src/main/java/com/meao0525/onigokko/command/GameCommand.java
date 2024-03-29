package com.meao0525.onigokko.command;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.settings.Mode;
import com.meao0525.onigokko.settings.OnigoItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
                        "           nigestartloc --- 逃げの初期地点\n" +
                        "           onistartloc --- 鬼の初期地点\n" +
                        "           center --- マップ中心地点\n" +
                        "           size <int> --- マップ一辺の長さ\n" +
                        "           time <second> --- ゲーム時間\n" +
                        "           prison --- ケイドロ用牢屋地点\n" +
                        "/onigo mode <mode> --- 鬼ごっこゲームモードの設定\n" +
                        "/onigo oni <name> --- 鬼の選出\n" +
                        "/onigo speed <team> <int> --- 足の速さを設定\n" +
                        "/onigo item <item> --- ゲームアイテムの設定\n" +
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

            case "speed":
                return commandSpeed(sender, args);

            case "item":
                return commandItem(sender, args);

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
                    flagInfo = false;
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
        //表示用文字列
        String infoMsg = "";
        //プレイヤーかコンソールか
        if (sender instanceof Player) {
            //情報をスコアボードに表示
            Player psender = (Player)sender;
            //表示切替
            flagInfo = !flagInfo;
            if (flagInfo) {
                //Info用のボードにする
                psender.setScoreboard(plugin.getInfo());
                //スコアの表示
                reloadInfo();
                //座標の送信
                infoMsg += ChatColor.GOLD + "==========[逃げの初期地点]==========\n";
                if (!plugin.getNigeStartloc().isEmpty()) {
                    //逃げ初期地点リスト
                    for (Location l : plugin.getNigeStartloc()) {
                        infoMsg += ChatColor.AQUA + "" + l.getX() + " " + l.getY() + " " + l.getZ() + "\n";
                    }
                } else {
                    infoMsg += ChatColor.RESET + "未設定\n";
                }

                infoMsg += ChatColor.GOLD + "==========[鬼の初期地点]==========\n";
                if (!plugin.getOniStartloc().isEmpty()) {
                    //鬼初期地点リスト
                    for (Location l : plugin.getOniStartloc()) {
                        infoMsg += ChatColor.AQUA + "" + l.getX() + " " + l.getY() + " " + l.getZ() + "\n";
                    }
                } else {
                    infoMsg += ChatColor.RESET + "未設定\n";
                }
                sender.sendMessage("\n" + infoMsg);

            } else {
                //元のボードに戻す
                psender.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        } else {
            //表示用文字列
            infoMsg = ChatColor.GOLD + "==========[どこでも鬼ごっこ]===========\n"
                    + ChatColor.RESET + "ゲームモード: " + ChatColor.AQUA + plugin.getMode().toString() + "\n"
                    + ChatColor.RESET + "マップ中心地点: " + ChatColor.AQUA + plugin.getCenter().getX() + " " + plugin.getCenter().getZ() + "\n"
                    + ChatColor.RESET + "マップ範囲: " + ChatColor.AQUA + plugin.getSize() + "\n"
                    + ChatColor.RESET + "ゲーム時間: " + ChatColor.AQUA + plugin.getTime() + "\n"
                    + ChatColor.RESET + "足の速さ: " + "鬼 " + ChatColor.AQUA + plugin.getOniSpeed()
                    + ChatColor.RESET + " : 逃 " + ChatColor.AQUA + plugin.getNigeSpeed() + "\n"+ ChatColor.RESET;
            //未設定のときは表示しない
            if (plugin.getPrison() != null) {
                infoMsg += ChatColor.RESET + "監獄座標: " +
                        ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " +plugin.getPrison().getZ() + "\n";
            }

            if (!plugin.getNigeStartloc().isEmpty()) {
                infoMsg += ChatColor.GOLD + "==========[逃げの初期地点]==========\n";
                //逃げ初期地点リスト
                for (Location l : plugin.getNigeStartloc()) {
                    infoMsg += ChatColor.AQUA + "" + l.getX() + " " + l.getY() + " " + l.getZ() + "\n";
                }
            }
            if (!plugin.getOniStartloc().isEmpty()) {
                infoMsg += ChatColor.GOLD + "==========[鬼の初期地点]==========\n";
                //鬼初期地点リスト
                for (Location l : plugin.getOniStartloc()) {
                    infoMsg += ChatColor.AQUA + "" + l.getX() + " " + l.getY() + " " + l.getZ() + "\n";
                }
            }

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
            //すでに鬼じゃないアドべの人
            for (Player p : players) {
                if (!plugin.getOni().contains(p.getName()) && p.getGameMode().equals(GameMode.ADVENTURE)) {
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

    public boolean commandSpeed(CommandSender sender, String[] args) {
        //引数違い
        if (args.length != 3) { return false; }

        try {
            //整数取り出す
            int speed = Integer.parseInt(args[2]);
            //でかすぎor小さすぎ
            if (speed < -1) {
                speed = -1;
            } else if ( speed > 3) {
                speed = 3;
            }
            //速さ設定
            if (args[1].equalsIgnoreCase("oni")) {
                plugin.setOniSpeed(speed);
            } else if (args[1].equalsIgnoreCase("nige")) {
                plugin.setNigeSpeed(speed);
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.GRAY + "第3引数は整数を入力してください");
        }
        //infoの更新
        reloadInfo();
        return true;
    }

    public boolean commandItem(CommandSender sender, String[] args) {
        //引数違い
        if (args.length != 2) { return false; }

        OnigoItem item;
        if (args[1].equalsIgnoreCase("pearl")) {
            item = OnigoItem.ONIGO_PEARL;
        } else if(args[1].equalsIgnoreCase("glowing")) {
            item = OnigoItem.ONIGO_GLOWING;
        } else if(args[1].equalsIgnoreCase("dash")) {
            item = OnigoItem.ONIGO_DASH;
        } else {
            return false;
        }
        //トグルで切り替え
        if (plugin.getItemList().contains(item)) {
            //除去
            plugin.getItemList().remove(item);
        } else {
            //追加
            plugin.getItemList().add(item);
        }
        //infoの更新
        reloadInfo();
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
        score = obj.getScore("足の速さ: " + "鬼 " + ChatColor.AQUA + plugin.getOniSpeed() + ChatColor.RESET + " : 逃 " + ChatColor.AQUA + plugin.getNigeSpeed());
        score.setScore(i--);
        //値がないときは表示せぬもの
        if (plugin.getPrison() != null) {
            score = obj.getScore("監獄座標: " + ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " +plugin.getPrison().getZ());
            score.setScore(i--);
        }
        //アイテム
        if (!plugin.getItemList().isEmpty()) {
            score = obj.getScore(ChatColor.GOLD + "=====[ゲームアイテム]=====");
            score.setScore(i--);
            for (OnigoItem oi : plugin.getItemList()) {
                score = obj.getScore(oi.getName());
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
