package io.github.kaivian.klibrary.util;

import org.bukkit.NamespacedKey;

public class NamespaceKey {

    public static NamespacedKey from(String value) {
        String pre;
        String post;
        if (!value.contains(":")) {
            pre = NamespacedKey.MINECRAFT;
            post = value;
        } else {
            pre = value.split(":")[0];
            post = value.split(":")[1];
        }
        return new NamespacedKey(pre, post);
    }
}
