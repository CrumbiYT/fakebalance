package com.fakebalance;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public final class BalanceRewriter {
    private BalanceRewriter() {}

    /** Returns a rewritten copy of the line, or null if the line has no balance to change. */
    public static MutableComponent rewrite(Component line) {
        List<String> texts = new ArrayList<>();
        List<Style> styles = new ArrayList<>();
        line.<Boolean>visit((style, text) -> {
            texts.add(text);
            styles.add(style);
            return Optional.<Boolean>empty();
        }, Style.EMPTY);

        StringBuilder joined = new StringBuilder();
        for (String t : texts) joined.append(t);

        Matcher m = FakeBalanceConfig.INSTANCE.pattern().matcher(joined);
        if (!m.find()) return null;

        int start = m.start(3);
        int end = m.end(3);
        String replacement = FakeBalanceConfig.INSTANCE.displayedBalance;

        MutableComponent out = Component.empty();
        boolean inserted = false;
        int offset = 0;
        for (int i = 0; i < texts.size(); i++) {
            String t = texts.get(i);
            Style s = styles.get(i);
            int segStart = offset;
            int segEnd = offset + t.length();
            offset = segEnd;

            // part before the number
            if (segStart < start) {
                String before = t.substring(0, Math.min(t.length(), start - segStart));
                if (!before.isEmpty()) out.append(Component.literal(before).setStyle(s));
            }
            // the number itself (emit the replacement once, in the style where the number starts)
            if (!inserted && start >= segStart && start < segEnd) {
                out.append(Component.literal(replacement).setStyle(s));
                inserted = true;
            }
            // part after the number
            if (segEnd > end) {
                String after = t.substring(Math.max(0, end - segStart));
                if (!after.isEmpty()) out.append(Component.literal(after).setStyle(s));
            }
        }
        return inserted ? out : null;
    }
}
