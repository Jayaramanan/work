/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.util;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Setting;

public class SettingsUtilTest extends TestCase{

	public void testIsTrueValueTrue(){
		assertTrue(SettingsUtil.isTrueValue("1"));
		assertTrue(SettingsUtil.isTrueValue("true"));
		assertTrue(SettingsUtil.isTrueValue("True"));
		assertTrue(SettingsUtil.isTrueValue("TRUE"));
		assertTrue(SettingsUtil.isTrueValue("y"));
		assertTrue(SettingsUtil.isTrueValue("Y"));
		assertTrue(SettingsUtil.isTrueValue("yes"));
		assertTrue(SettingsUtil.isTrueValue("Yes"));
		assertTrue(SettingsUtil.isTrueValue("YES"));
		assertTrue(SettingsUtil.isTrueValue("t"));
		assertTrue(SettingsUtil.isTrueValue("T"));

	}

	public void testIsTrueValueFalse(){
		assertFalse(SettingsUtil.isTrueValue(null));
		assertFalse(SettingsUtil.isTrueValue(""));
		assertFalse(SettingsUtil.isTrueValue("false"));
		assertFalse(SettingsUtil.isTrueValue("0"));
		assertFalse(SettingsUtil.isTrueValue("n"));
		assertFalse(SettingsUtil.isTrueValue("no"));
		assertFalse(SettingsUtil.isTrueValue("F"));
		assertFalse(SettingsUtil.isTrueValue("abc"));
	}

	public void testIsBooleanSettingTrue(){
		Setting setting = new ApplicationSetting("Applet", "Abc_InUse", "TRUE");
		assertTrue(SettingsUtil.isBooleanSetting(setting));
		setting.setProp("Abc_Visible");
		assertTrue(SettingsUtil.isBooleanSetting(setting));
		setting.setProp("ShowAbc");
		assertTrue(SettingsUtil.isBooleanSetting(setting));
		for (String s : Setting.BOOLEAN_SETTINGS){
			setting.setProp(s);
			assertTrue(SettingsUtil.isBooleanSetting(setting));
		}
	}

	public void testIsBooleanSettingFalse(){
		Setting setting = new ApplicationSetting("Applet", "abc", "TRUE");
		assertFalse(SettingsUtil.isBooleanSetting(setting));
		setting.setProp("AbcInUse");
		assertFalse(SettingsUtil.isBooleanSetting(setting));
		setting.setProp("AbcVisible");
		assertFalse(SettingsUtil.isBooleanSetting(setting));
		setting.setProp("abcShowAbc");
		assertFalse(SettingsUtil.isBooleanSetting(setting));
	}

	public void testIsColorSettingTrue(){
		Setting setting = new ApplicationSetting("", Setting.NODE_SELECTED_COLOR_PROPERTY, "");
		assertTrue(SettingsUtil.isColorSetting(setting));
		setting.setProp(Setting.PREFILTER_BACKGROUND_PROPERTY);
		assertTrue(SettingsUtil.isColorSetting(setting));
		setting.setProp(Setting.GRADIENT_START_COLOR_PROPERTY);
		assertTrue(SettingsUtil.isColorSetting(setting));
		setting.setProp(Setting.GRADIENT_END_COLOR_PROPERTY);
		assertTrue(SettingsUtil.isColorSetting(setting));
	}

	public void testIsColorSettingFalse(){
		Setting setting = new ApplicationSetting("", "", "");
		assertFalse(SettingsUtil.isColorSetting(setting));
		setting.setProp(null);
		assertFalse(SettingsUtil.isColorSetting(setting));
		setting.setProp("abc");
		assertFalse(SettingsUtil.isColorSetting(setting));
	}
}
