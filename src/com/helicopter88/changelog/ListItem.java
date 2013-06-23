package com.helicopter88.changelog;

import java.util.ArrayList;

public class ListItem {
	private String Commit, Url;
	private static ArrayList<String> project = new ArrayList<String>();
	
	public ListItem(String line)
	{
		Commit = formatChangelog(line);
		Url = formatUrl(line);
	}
	
	public ListItem(int error)
	{
		switch(error)
		{
		case 1:
			Commit = "Ill-formed Changelog.txt";
			break;
		case 2:
			Commit = "Unable to parse changelog \n Please tap here to \n parse it again";
			break;
		default:
			Commit = "Unhandled exception";
			break;
		}
	}
	
	public String getCommit()
	{
		return Commit;
	}
	
	public String getUrl()
	{
		return Url;
	}
	
	private static final String formatChangelog(String line) {
		StringBuilder sb = new StringBuilder();
		String[] splitted = line.split("\\|");

		for (String str : splitted) {

			if (str == splitted[splitted.length - 1]) {

				sb.append(str.trim());

			} else {
				sb.append(str.trim() + "\n");
			}
		}

		/** Looks ugly,but || doesn't want to work **/
		if (!sb.toString().contains("project") && !sb.toString().contains("--"))
			sb.delete(0, 49);

		return sb.toString();

	}
	
	private static final String formatUrl(String line) {
		StringBuilder remoteUrl = new StringBuilder();
		StringBuilder finalUrl = new StringBuilder();
		String[] remotes = line.split("\\| Remote: ");
		if (line.isEmpty())
			return ""; // No need to create an URL for something empty
		
		if (line.contains("project")) {

			String remote = line.substring(8, (line.length() - 1)).replace("/",
					"_");
			if (remote.contains("android")) {
				remoteUrl.append("android");
			} else {
				remoteUrl.append("android_");
				remoteUrl.append(remote);
			}
			project.add(remoteUrl.toString());
			return ""; // No need to create urls for lines containing "project"
		}

		for (String srt : remotes) {
			if (!line.isEmpty() && line.length() > 50) {
				String commit_hash = line.substring(9, 50);

				if (srt.contains("cr")) {
					finalUrl.append("https://github.com/CarbonDev/");
				} else if (srt.contains("cm") && !srt.contains("cr")) {
					finalUrl.append("https://github.com/CyanogenMod/");
				} else if (srt.contains("tm") && !srt.contains("cr")) {
					finalUrl.append("https://github.com/TheMuppets/");
					// Looks ugly as hell
					String tm = project.get(project.size() - 1)
							.replace("android", "proprietary").trim();
					project.add(tm);
				} else if (srt.contains("cmdev") && !srt.contains("cr")) {
					finalUrl.append("https://github.com/loosethisskin/");

				} else {
					finalUrl.append("https://github.com/CarbonDev/");
				}

				finalUrl.append(project.get(project.size() - 1).trim());
				finalUrl.append("/commit/".trim());
				finalUrl.append(commit_hash.trim());
				// More remotes should be done,but how to handle gh?
			}
			return finalUrl.toString();
		}
		return "";

	}
}
