package xyz.yusufyaser.ShellAccess;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public final class Main extends JavaPlugin {

    File configFile = new File(getDataFolder(), "config.yml");
    YamlConfiguration config = new YamlConfiguration();

    void Log(String text) {
        String result = C("&e[Shell] ");
        result += C("&r" + text);
        getServer().getConsoleSender().sendMessage(result);
    }

    public static String C(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                saveResource("config.yml", false);
            }

            try {
                config.load(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (config.getString("enable_gotty").equals("yes")) {
            Log(C("Loading gotty"));
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("shell")) {
            if (config.getString("enable_command").equals("yes")) {
                if (sender.hasPermission(config.getString("permission"))) {
                    try {
                        if (args.length == 0) {
                            sender.sendMessage(C("&c/shell <command> [args]"));
                            return true;
                        }

                        String argsS = "";
                        boolean first = true;
                        for (int i = 1; i < args.length; i++) {
                            if (!first) {
                                argsS += " ";
                            }
                            first = false;
                            argsS += args[i];
                        }
                        Runtime rt = Runtime.getRuntime();
                        String[] commands;
                        if (argsS.equals("")) {
                            commands = new String[1];
                            commands[0] = args[0];
                        } else {
                            commands = new String[2];
                            commands[0] = args[0];
                            commands[1] = argsS;
                        }
                        Process proc = rt.exec(commands);

                        BufferedReader stdInput = new BufferedReader(new
                                InputStreamReader(proc.getInputStream()));

                        BufferedReader stdError = new BufferedReader(new
                                InputStreamReader(proc.getErrorStream()));

                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                            sender.sendMessage(s);
                        }

                        while ((s = stdError.readLine()) != null) {
                            sender.sendMessage(s);
                        }
                    } catch (Exception e) {
                        sender.sendMessage(C(e.getMessage()));
                    }
                } else {
                    sender.sendMessage(C(config.getString("no_permission")));
                }
            } else {
                sender.sendMessage("Unknown command. Type \"/help\" for help.");
            }
        }
        return true;
    }
}
