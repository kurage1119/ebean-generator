package net.kurage.generator;

import java.util.*;
import java.util.regex.*;

public final class EnglishSingularizer {

    private EnglishSingularizer() {}

    // 単複同形 / 不可算名詞
    private static final Set<String> UNINFLECTED = new HashSet<>(Arrays.asList(
        "bison","deer","fish","moose","salmon","series","species","sheep","swine",
        "aircraft","spacecraft","hovercraft","means","news","offspring","equipment",
        "information","rice","money","advice","luggage","furniture","homework"
    ));

    // 不規則名詞
    private static final Map<String, String> IRREGULAR = new HashMap<>();
    static {
        // 一般的な不規則名詞
        IRREGULAR.put("children","child");
        IRREGULAR.put("people","person");
        IRREGULAR.put("men","man");
        IRREGULAR.put("women","woman");
        IRREGULAR.put("mice","mouse");
        IRREGULAR.put("geese","goose");
        IRREGULAR.put("teeth","tooth");
        IRREGULAR.put("feet","foot");
        IRREGULAR.put("lice","louse");
        IRREGULAR.put("oxen","ox");
        IRREGULAR.put("dice","die");

        // ラテン語・ギリシャ語由来
        IRREGULAR.put("indices","index");
        IRREGULAR.put("appendices","appendix");
        IRREGULAR.put("matrices","matrix");
        IRREGULAR.put("vertices","vertex");
        IRREGULAR.put("analyses","analysis");
        IRREGULAR.put("bases","basis");
        IRREGULAR.put("crises","crisis");
        IRREGULAR.put("diagnoses","diagnosis");
        IRREGULAR.put("theses","thesis");
        IRREGULAR.put("phenomena","phenomenon");
        IRREGULAR.put("criteria","criterion");
        IRREGULAR.put("media","medium");
        IRREGULAR.put("data","datum");
        IRREGULAR.put("bacteria","bacterium");
        IRREGULAR.put("alumni","alumnus");
        IRREGULAR.put("alumnae","alumna");

        // ves → f/fe パターン
        IRREGULAR.put("wolves","wolf");
        IRREGULAR.put("knives","knife");
        IRREGULAR.put("lives","life");
        IRREGULAR.put("wives","wife");
        IRREGULAR.put("leaves","leaf");
        IRREGULAR.put("shelves","shelf");
        IRREGULAR.put("calves","calf");
        IRREGULAR.put("halves","half");
        IRREGULAR.put("loaves","loaf");
        IRREGULAR.put("selves","self");

        // その他よくある特殊複数形
        IRREGULAR.put("buses","bus");
        IRREGULAR.put("heroes","hero");
        IRREGULAR.put("classes","class");
        IRREGULAR.put("addresses","address");
        IRREGULAR.put("expenses","expense");
        IRREGULAR.put("tomatoes","tomato");
        IRREGULAR.put("potatoes","potato");
        IRREGULAR.put("echoes","echo");
        IRREGULAR.put("oxes","ox"); // 念のため
    }

    // 規則ベース
    private static final List<Rule> RULES = Arrays.asList(
        new Rule("sses$", word -> word.substring(0, word.length()-2)),   // classes → class
        new Rule("ies$",  word -> word.substring(0, word.length()-3) + "y"), // cities → city
        new Rule("ves$",  word -> word.substring(0, word.length()-3) + "f"), // wives → wife は辞書優先
        new Rule("xes$",  word -> word.substring(0, word.length()-2)),   // boxes → box
        new Rule("ches$", word -> word.substring(0, word.length()-2)),   // matches → match
        new Rule("shes$", word -> word.substring(0, word.length()-2)),   // wishes → wish
        new Rule("oes$",  word -> word.substring(0, word.length()-2)),   // heroes → hero
        new Rule("zzes$", word -> word.substring(0, word.length()-2)),   // buzzes → buzz
        new Rule("s$",    word -> word.substring(0, word.length()-1))    // cats → cat
    );

    public static String singularize(String input) {
        if (input == null || input.isBlank()) return input;

        String word = input.trim();
        String lower = word.toLowerCase(Locale.ROOT);

        if (UNINFLECTED.contains(lower)) return word;
        if (IRREGULAR.containsKey(lower)) return IRREGULAR.get(lower);

        for (Rule r : RULES) {
            if (r.matches(word)) {
                return r.apply(word);
            }
        }
        return word;
    }

    // ルールクラス
    private static final class Rule {
        private final Pattern pattern;
        private final Replacer replacer;

        Rule(String regex, Replacer replacer) {
            this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            this.replacer = replacer;
        }

        boolean matches(String word) {
            return pattern.matcher(word).find();
        }

        String apply(String word) {
            return replacer.replace(word);
        }
    }

    @FunctionalInterface
    private interface Replacer {
        String replace(String word);
    }

    // デモ
    public static void main(String[] args) {
        String[] samples = {
            "cities","wolves","knives","heroes","boxes","buses","cats","children",
            "people","men","women","mice","geese","teeth","feet","lice","oxen",
            "indices","matrices","vertices","analyses","crises","theses","criteria",
            "phenomena","media","data","bacteria","series","species","fish","news",
            "expenses","classes","addresses","wives","leaves","shelves","calves"
        };
        for (String s : samples) {
            System.out.printf("%-12s -> %s%n", s, singularize(s));
        }
    }
}
