/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static final Pattern ALLOWED_ICON_NAME_PATTERN = Pattern.compile("[A-Za-z0-9_\\.]+");

    public static boolean isIconNameValid(String name) {
        return ALLOWED_ICON_NAME_PATTERN.matcher(name).matches();
    }
}
