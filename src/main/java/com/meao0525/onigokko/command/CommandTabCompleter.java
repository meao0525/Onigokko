package com.meao0525.onigokko.command;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("onigo")) {
            if (args.length == 1) { //第1引数
                if (args[0].length() == 0) {
                    return Arrays.asList("help", "info", "set", "mode", "oni", "speed", "reset", "start", "stop");
                }
            } else if (args.length == 2) { //第2引数
                if (args[1].length() == 0) {
                    //第2引数は？
                    switch (args[0]) {
                        case "set":
                            return Arrays.asList("nigestartloc", "onistartloc", "center", "size", "prison", "time");
                        case "mode":
                            return Arrays.asList("gokko", "keidoro", "fueoni", "koorioni");
                        case "oni":
                            ArrayList<String> names = new ArrayList<>();
                            names.add("clear"); //リスト初期火曜
                            names.add("random"); //ランダムに選ぶ用
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                names.add(p.getName());
                            }
                            return names;
                        case "speed":
                            return Arrays.asList("oni", "nige");
                    }
                }
            }
        }

        //デフォのコンプリーター
        return null;
    }
}
