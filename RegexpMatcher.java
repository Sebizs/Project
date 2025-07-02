import com.google.re2j.Pattern;
import com.google.re2j.Matcher;
import java.util.List;

public class RegexpMatcher {
    private final List<RegexpPattern> patterns;

    public RegexpMatcher(List<RegexpPattern> patterns) {
        this.patterns = patterns;
    }

    public MatchedResult matchBest(String input) {
        MatchedResult best = null;
        for (RegexpPattern p : patterns) {
            try {
                Pattern pattern = Pattern.compile(p.pattern);
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    String matchedText = matcher.group();
                    if (best == null || matchedText.length() > best.matchedText.length()) {
                        best = new MatchedResult(p.id, matchedText);
                    }
                }
            } catch (Exception e) {
                System.err.println("Regexp hiba mintánál " + p.name + ": " + e.getMessage());
            }
        }
        return best;
    }

    public static class MatchedResult {
        public final int patternId;
        public final String matchedText;

        public MatchedResult(int patternId, String matchedText) {
            this.patternId = patternId;
            this.matchedText = matchedText;
        }
    }
}
