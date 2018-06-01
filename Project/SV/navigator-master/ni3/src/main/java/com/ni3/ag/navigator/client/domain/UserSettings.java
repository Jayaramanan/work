/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.text.Collator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.awt.*;

import com.ni3.ag.navigator.client.gateway.LanguageGateway;
import com.ni3.ag.navigator.client.gateway.SettingsGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpLanguageGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpSettingsGatewayImpl;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.LanguageItem;
import com.ni3.ag.navigator.shared.domain.UserSetting;
import org.apache.log4j.Logger;

public class UserSettings{
	private static final Logger log = Logger.getLogger(UserSettings.class);
	private static Map<String, String> settings = new HashMap<String, String>();

	private static Collator collator;
	private static boolean loadDone;

	static public void LoadSettings(){
		SettingsGateway settingsGateway = new HttpSettingsGatewayImpl();
		List<UserSetting> loadedSettings = settingsGateway.getAllSettings();

		for (UserSetting us : loadedSettings)
			settings.put(us.getSection() + "/" + us.getProperty(), us.getValue());
		loadDone = true;

		int LanguageID = getIntAppletProperty("Language", 1);
		LanguageGateway languageGateway = new HttpLanguageGatewayImpl();
		List<LanguageItem> languageItems = languageGateway.getTranslations(LanguageID);

		for (LanguageItem item : languageItems)
			settings.put("words/" + item.getProperty(), item.getValue());

		//TODO languages ids are hardcoded here, but not hardcoded in AC so any language can have any id!!!
		//TODO should be fixed
		Locale language;
		switch (LanguageID){
			case 1:
				language = Locale.ENGLISH;
				break;

			case 2:
				language = Locale.GERMAN;
				break;

			case 3:
				language = Locale.FRENCH;
				break;

			case 4:
				language = Locale.ITALIAN;
				break;

			case 5:
				language = Locale.CHINESE;
				break;

			case 6:
				language = Locale.JAPANESE;
				break;

			case 7:
				language = Locale.KOREAN;
				break;

			case 8:
				language = Locale.SIMPLIFIED_CHINESE;
				break;

			default:
				language = Locale.ENGLISH;
		}

		Locale.setDefault(language);
		collator = Collator.getInstance(language);
		collator.setStrength(Collator.TERTIARY);
	}

	static public String getProperty(String Section, String name, String defaultValue){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return defaultValue;
		}
		String ret = settings.get(Section + "/" + name);
		if (ret == null)
			return defaultValue;
		return ret;
	}

	static public boolean getBooleanAppletProperty(String name, boolean defaultValue){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return defaultValue;
		}
		String value = settings.get("Applet/" + name);
		if (value != null){
			return Utility.processBooleanString(value);
		} else{
			return defaultValue;
		}
	}

	static public boolean getBooleanGraphProperty(String name){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return false;
		}
		String value = settings.get("graph/" + name);
		if (value != null){
			return Utility.processBooleanString(value);
		} else{
			log.warn("Undefined graph property >" + name + "<");
			return false;
		}
	}

	static public int getIntegerProperty(String Section, String name, int defaultValue){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return defaultValue;
		}
		String value = settings.get(Section + "/" + name);
		if (value != null){
			return Integer.valueOf(value);
		} else{
			return defaultValue;
		}
	}

	static public Color getColor(String section, String name, Color defaultColor){
		try{
			String colorString = settings.get(section + "/" + name);
			if (colorString == null)
				return defaultColor;

			return Utility.createColor(colorString);
		} catch (Exception e){
			System.err.println("Undefined color >" + name + "<");
			return defaultColor;
		}
	}

	static public Color getColor(String name, Color defaultColor){
		return getColor("FontColor", name, defaultColor);
	}

	static public Font getFont(String name){
		try{
			String fontString = settings.get("FontColor/" + name);
			return Utility.createFont(fontString);
		} catch (Exception e){
			System.err.println("Undefined font >" + name + "<");
			return Utility.createFont(Utility.DEFAULT_FONT);
		}
	}

	static public String getWord(String englishWord){
		if (settings == null)
			return englishWord;
		String ret = settings.get("words/" + englishWord);
		if (ret == null)
			return englishWord;
		return ret;
	}

	static public String getStringAppletProperty(String name, String Default){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return Default;
		}
		String ret = settings.get("Applet/" + name);
		if (ret == null)
			return Default;
		return ret;
	}

	static public int getIntAppletProperty(String name, int Default){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return Default;
		}
		String ret = settings.get("Applet/" + name);
		if (ret != null)
			return Integer.decode(ret);
		else
			return Default;
	}

	static public long getLongAppletProperty(String name, long Default){
		if (!loadDone){
			log.warn("Properties accessed before loading");
			return Default;
		}
		String ret = settings.get("Applet/" + name);
		if (ret != null)
			return Long.decode(ret);
		else
			return Default;
	}

	public static String getWord(String msgId, Object[] params){
		String text = getWord(msgId);
		return getParsedMessage(text, params);
	}

	public static String getWord(String msgId, List<String> params){
		String text = getWord(msgId);
		return getParsedMessage(text, params);
	}

	private static String getParsedMessage(String text, Object[] params){
		if (text != null && params != null && params.length > 0){
			for (int i = 0; i < params.length; i++){
				text = text.replace("{" + (i + 1) + "}", (params[i] == null ? "" : params[i].toString()));
			}
		}
		return text;
	}

	private static String getParsedMessage(String text, List<String> params){
		if (text != null && params != null && !params.isEmpty()){
			for (int i = 0; i < params.size(); i++){
				String prm = params.get(i);
				text = text.replace("{" + (i + 1) + "}", (prm == null ? "" : prm));
			}
		}
		return text;
	}

	public static Collator getCollator(){
		return collator;
	}

	public static void resetImageRefreshSetting(){
		URLEx url = new URLEx(ServletName.SettingsProvider);

		url.addParam(RequestParam.Action, "createOrUpdate");
		url.addParam(RequestParam.P1, "false");
		url.addParam(RequestParam.P2, SystemGlobals.getUserId());
		url.addParam(RequestParam.P3, "ImageCacheRefresh");
		url.addParam(RequestParam.P4, "Applet");

		url.closeOutput("reset ImageCacheRefresh");
		url.readLine();
		url.close();
	}

	/**
	 * for test purposes
	 */
	public static void initEmptySettings(){
		settings = new HashMap<String, String>();
	}

	public static void saveSettingLocally(String property, String value){
		settings.put("Applet/" + property, value);
	}
}
