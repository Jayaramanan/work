package com.ni3.ag.navigator.client.gateway;

import com.ni3.ag.navigator.shared.domain.LanguageItem;
import java.util.List;

public interface LanguageGateway{
	List<LanguageItem> getTranslations(int languageID);
}
