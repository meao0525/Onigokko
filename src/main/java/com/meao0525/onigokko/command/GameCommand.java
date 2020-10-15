package com.meao0525.onigokko.command;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.Mode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {

    private Onigokko plugin;

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
                        "/onigo start --- ゲームスタート");
                break;

            case "info":
                //表示用文字列
                String infoMsg = ChatColor.GOLD + "==========[どこでも鬼ごっこ]===========\n"
                        + ChatColor.RESET + "鬼ごっこゲームモード: " + ChatColor.AQUA + plugin.getMode().toString() + "\n"
                        + ChatColor.RESET + "マップ中心地点: " + ChatColor.AQUA + plugin.getCenter().getX() + " " + plugin.getCenter().getZ() + "\n"
                        + ChatColor.RESET + "マップ範囲: " + ChatColor.AQUA + plugin.getSize() + "\n"
                        + ChatColor.RESET + "ゲーム時間: " + ChatColor.AQUA + plugin.getTime() + "\n" + ChatColor.RESET;
                //未設定のときは表示しない
                if (plugin.getPrison() != null) { infoMsg += ChatColor.RESET + "監獄座標: " + ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " +plugin.getPrison().getZ() + "\n"; }
                if (plugin.getNigeStartloc() != null) { infoMsg += ChatColor.RESET + "逃げの初期地点: " + ChatColor.AQUA + plugin.getNigeStartloc().getX() + " " + plugin.getNigeStartloc().getY() + " " + plugin.getNigeStartloc().getZ() + "\n"; }
                if (plugin.getOniStartloc() != null) { infoMsg += ChatColor.RESET + "鬼の初期地点: " + ChatColor.AQUA + plugin.getOniStartloc().getX() + " " + plugin.getOniStartloc().getY() + " " + plugin.getOniStartloc().getZ() + "\n"; }
                infoMsg += ChatColor.GOLD + "==========[鬼プレイヤー]==========\n";
                //info送信
                sender.sendMessage(infoMsg);
                //鬼のプレイヤー名表示
                for (Player p : plugin.getOni()) {
                    sender.sendMessage(p.getDisplayName() + " ");
                }
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
                break;

            case "start":
                //設定漏れはないか
                if (plugin.getNigeStartloc() == null) {
                    sender.sendMessage(ChatColor.GRAY + "逃げの初期地点が設定されていません");
                } else if (plugin.getOniStartloc() == null) {
                    sender.sendMessage(ChatColor.GRAY + "鬼の初期地点が設定されていません");
                } else if (plugin.getOni().size() == 0) {
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

    public boolean commandSet(Player sender, String[] args) {
        //引数違い
        if (!(args.length > 1)) { return false; }

        switch (args[1]) {
            case "nigestartloc":
                if (args.length == 2) {
                    //コマンド使用者の現在地の座標を取得(intで取得するため)
                    plugin.setNigeStartloc(sender.getEyeLocation().getBlock().getLocation());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "逃げの初期地点を現在座標\n"
                            + ChatColor.AQUA + plugin.getNigeStartloc().getX() + " " + plugin.getNigeStartloc().getY() + " " + plugin.getNigeStartloc().getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
                    return true;
                }
                break;

            case "onistartloc":
                if (args.length == 2) {
                    //コマンド使用者の現在地の座標を取得(intで取得するため)
                    plugin.setOniStartloc(sender.getEyeLocation().getBlock().getLocation());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "鬼の初期地点を現在座標\n"
                            + ChatColor.AQUA + plugin.getOniStartloc().getX() + " " + plugin.getOniStartloc().getY() + " " + plugin.getOniStartloc().getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
                    return true;
                }
                break;

            case "center":
                if (args.length == 2) {
                    //センター設定(コマンド使用者の現在地の座標を取得)
                    plugin.setCenter(sender.getEyeLocation().getBlock().getLocation());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "中心地点を現在座標\n"
                            + ChatColor.AQUA + plugin.getCenter().getX() + " " + plugin.getCenter().getY() + " " + plugin.getCenter().getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
                    return true;
                }
                break;

            case "prison":
                if (args.length == 2) {
                    //コマンド使用者の現在地の座標を取得(intで取得するため)
                    plugin.setPrison(sender.getEyeLocation().getBlock().getLocation());
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET + "牢屋地点を現在座標\n"
                            + ChatColor.AQUA + plugin.getPrison().getX() + " " + plugin.getPrison().getY() + " " + plugin.getPrison().getZ() + "\n"
                            + ChatColor.RESET + "に設定しました");
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
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                //名前の一致するプレイヤーを鬼リストにいれる
                if (args[1].equalsIgnoreCase(p.getName())) {
                    plugin.getOni().add(p);
                    sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.AQUA
                            + args[1] + ChatColor.RESET + " を鬼に追加しました");
                    return true;
                }
            }
            sender.sendMessage(ChatColor.GOLD + "[どこでも鬼ごっこ]" + ChatColor.RESET
                    + args[1] + ChatColor.GRAY + " というプレイヤーが見つかりません");
        }
        return true;
    }
}
