package com.ch.hologramdrops;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** 플러그인 메시지 번역 (ko / en / zh / es). config.yml 의 language 로 선택 */
public final class Lang {

    private static final Map<String, Map<String, String>> TEXT = new HashMap<>();
    private static String current = "ko";

    static {
        put("enabled",
                "활성화 - 떨어진 아이템 위에 홀로그램이 표시됩니다.",
                "Enabled - holograms will appear above dropped items.",
                "已启用 - 掉落物上方将显示全息文字。",
                "Activado: se mostrarán hologramas sobre los objetos soltados.");
        put("disabled",
                "비활성화 - 모든 홀로그램을 제거했습니다.",
                "Disabled - all holograms removed.",
                "已禁用 - 已移除所有全息文字。",
                "Desactivado: se eliminaron todos los hologramas.");
        put("reloaded",
                "설정을 다시 불러왔습니다.",
                "Configuration reloaded.",
                "配置已重新加载。",
                "Configuración recargada.");
        put("bad-material",
                "blacklisted-materials 에 없는 재질입니다: {0}",
                "Unknown material in blacklisted-materials: {0}",
                "blacklisted-materials 中存在未知材质：{0}",
                "Material desconocido en blacklisted-materials: {0}");
        put("bad-override",
                "item-name-overrides 에 없는 재질입니다: {0}",
                "Unknown material in item-name-overrides: {0}",
                "item-name-overrides 中存在未知材质：{0}",
                "Material desconocido en item-name-overrides: {0}");
        put("bad-language",
                "알 수 없는 language 값입니다 ({0}). en 으로 표시합니다. (ko / en / zh / es)",
                "Unknown language ({0}). Using en. (ko / en / zh / es)",
                "未知的 language 值（{0}），将使用 en。（ko / en / zh / es）",
                "Idioma desconocido ({0}). Se usará en. (ko / en / zh / es)");
    }

    private Lang() {
    }

    private static void put(String key, String ko, String en, String zh, String es) {
        TEXT.computeIfAbsent("ko", k -> new HashMap<>()).put(key, ko);
        TEXT.computeIfAbsent("en", k -> new HashMap<>()).put(key, en);
        TEXT.computeIfAbsent("zh", k -> new HashMap<>()).put(key, zh);
        TEXT.computeIfAbsent("es", k -> new HashMap<>()).put(key, es);
    }

    /** 언어 설정. 지원하지 않는 값이면 false (en 사용) */
    public static boolean set(String lang) {
        String l = lang == null ? "" : lang.toLowerCase(Locale.ROOT).trim();
        if (TEXT.containsKey(l)) {
            current = l;
            return true;
        }
        current = "en";
        return false;
    }

    public static String get(String key, Object... args) {
        String s = TEXT.get(current).getOrDefault(key, key);
        for (int i = 0; i < args.length; i++) s = s.replace("{" + i + "}", String.valueOf(args[i]));
        return s;
    }
}
