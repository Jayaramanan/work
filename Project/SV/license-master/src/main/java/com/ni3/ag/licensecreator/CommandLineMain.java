package com.ni3.ag.licensecreator;

import com.ni3.ag.licensecreator.actions.CreateActionListener;

public class CommandLineMain{
	public static void main(String[] args){
		if (args.length != 1){
            System.out.println("Usage (UI) : java -jar Ni3LCG-0.0.16.jar");
			System.out.println("Usage (cmd): java -cp Ni3LCG-0.0.16.jar com.ni3.ag.licensecreator.CommandLineMain <user_id>");
			System.exit(0);
		}
		new CommandLineMain(Integer.valueOf(args[0]));
	}
	
	CommandLineMain(final Integer userId){
		CreateActionListener createActionListener = new CreateActionListener();
		String moduleSql = createActionListener.generateUserModuleSql(userId);
		System.out.println(moduleSql);
	}
}
