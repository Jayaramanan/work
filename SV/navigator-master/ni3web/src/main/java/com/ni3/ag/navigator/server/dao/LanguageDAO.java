package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.LanguageItem;

public interface LanguageDAO{
	List<LanguageItem> getTranslations(int id);
}
