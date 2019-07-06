package fr.superlog.Commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import fr.superlog.Log.Log;
import org.bukkit.command.CommandSender;

public class Cmdfiltrer implements Cmd {

	public void run(final Log log, final CommandSender sender, final String[] args) {
		final String prefix = log.getConfig().getMessage("prefix");
		if(!sender.hasPermission("superlog.commands.filtrer")) {
			sender.sendMessage(log.getUtils().color(prefix + log.getConfig().getMessage("noperm")));
			return;
		}
		
		if(args.length < 3) {
			sender.sendMessage(log.getUtils().color(prefix + "&7Filtrer command argument:\n" +
							prefix + "&3/log filtrer &b<playerName> <eventName>&7: create a 'filtered.log' about playerName & the eventName)\n" +
							prefix + "  &7\u2937 &b<playerName>: player name to filtrer\n" +
							prefix + "  &7\u2937 &b<eventName>: name of the event to filtrer"));
			return;
		}
		
		final String playerName = args[1];
		final String eventName = args[2];
		
		log.getPlugin().getServer().getScheduler().runTaskAsynchronously(log.getPlugin(), () -> {
			try {
				// Checking player log
				File folder = new File(log.getPlugin().getDataFolder() + File.separator +
											"logs" + File.separator +
											"players" + File.separator +
											playerName + File.separator);
				if(!folder.isDirectory()) throw new Exception("&cThe player &e" + playerName + "&c doesn't have logs.");

				// Processing
				List<String> lines = new ArrayList<>();
				File[] files = folder.listFiles();
				if(files == null) throw new Exception("&cThe player &e" + playerName + "&c doesn't have logs.");
				for(File file : files) {
					// Checking file
					int lastIndex = file.getName().lastIndexOf(".");
					if(lastIndex < 0) continue;
					String logFormatExtension = log.getConfig().getLogsFormat().substring(log.getConfig().getLogsFormat().lastIndexOf("."));
					if(!file.getName().substring(lastIndex).equals(logFormatExtension)) continue;

					// Reading & filtering
					try(BufferedReader br = new BufferedReader(new FileReader(file))){
						for(String line; (line = br.readLine()) != null; ) {
							if(!line.contains(eventName)) continue;
							lines.add(line);
						}
					}catch(Exception e) {
						sender.sendMessage(log.getUtils().color(prefix + "&cUnable to read " + file.getName()));
						if(Log.DEBUG) e.printStackTrace();
					}
				}

				// Writing in file
				if(lines.isEmpty()) throw new Exception("&cNo logs found for player &e" + playerName + "&c and event &e" + eventName + "&c.");
				File filtered = new File(log.getPlugin().getDataFolder() + File.separator + "logs_filtered" + File.separator, "filtered_" + playerName + "_" + eventName + ".log");
				File directory = filtered.getParentFile();
				if(!directory.exists()) directory.mkdirs();
				if(filtered.isFile()) filtered.delete();
				filtered.createNewFile();

				Writer writer = new OutputStreamWriter(new FileOutputStream(filtered, true), log.getUtils().UTF_8);
				for(String newLine : lines) {
					writer.append(newLine);
					writer.append(System.lineSeparator());
				}
				writer.close();
				throw new Exception("&2"+ filtered.getName() + " created, with &a" + lines.size() + " logs&2.");
			}catch(Exception e) {
				log.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(log.getPlugin(), () -> {
					sender.sendMessage(log.getUtils().color(prefix + e.getMessage()));
				});
			}
		});
	}

	public List<String> getTabCompletition(final String[] args) {
		return new ArrayList<>();
	}

}
