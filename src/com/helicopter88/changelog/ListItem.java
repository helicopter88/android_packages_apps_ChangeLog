package com.helicopter88.changelog;

import java.util.ArrayList;

public class ListItem {
	public String Commit;

	public ListItem(String line) {
		Commit = formatChangelog(line);
	}

	public ListItem(int error) {
		switch (error) {
		case 1:
			Commit = "Ill-formed changelog.txt";
			break;
		case 2:
			Commit = "Unable to parse changelog \n Please tap here to \n parse it again";
			break;
		default:
			Commit = "Unhandled exception";
			break;
		}
	}

	private static final String formatChangelog(String line) {
		StringBuilder sb = new StringBuilder();
		String[] splitted = line.split("\\|");

		for (String str : splitted) {

			if (str == splitted[splitted.length - 1])
				sb.append(str.trim());
			 else
				sb.append(str.trim() + "\n");
		}

		/** Looks ugly,but || doesn't want to work **/
		if (!sb.toString().contains("project") && !sb.toString().contains("--"))
			sb.delete(0, 17);
		return sb.toString();
	}


}
