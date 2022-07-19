package org.intellij.sdk.toolWindow;

import javax.swing.text.Document;
import javax.swing.text.Style;
import java.util.ArrayList;
import java.util.List;

public class DocumentParser {
    private final Document doc;

    /** class for managing and looking up text in a doc */
    public DocumentParser(Document doc) {
        this.doc = doc;
    }

    /** returns a list of the starting indices of a given
     * String in the note tool window Document
     * @param s a string to look for
     * @return all starting indices
     */
    public List<Integer> getStartOfStrings(String s) {
        ArrayList<Integer> starts = new ArrayList<>();
        for (int i = 0; i <= doc.getLength() - s.length(); i++) {
            try {
                if (doc.getText(i, s.length()).equals(s)) {
                    starts.add(i);
                }
            } catch (Exception e) {
                System.out.println("getStartOfStringsError");
            }
        }
        return starts;
    }

    /** gets content inside braces (i.e. \{content})
     * @param start starting index of the `\`
     * @param minContentLen the minimum valid length
     *                      for content (must be positive)
     * @param maxContentLen the maximum valid length for
     *                      content
     * @return null if can't find content, else returns content
     */
    public String getContentInCurlyBraces(int start, int minContentLen, int maxContentLen) {
        OffsetRange range = getBracedContentRange(start, minContentLen, maxContentLen);
        try {
            if (range != null) {
                return doc.getText(range.getStart(), range.size());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getContentInRange(OffsetRange range) {
        try {
            return doc.getText(range.getStart(), range.size());
        } catch (Exception e) {
            return null;
        }
    }

    public OffsetRange getBracedContentRange(int start, int minContentLen, int maxContentLen) {
        int wordStart = start + 2;
        for (int currLen = minContentLen; currLen <= maxContentLen; currLen++) {
            try {
                int closingBraceLoc = wordStart + currLen;
                String test = doc.getText(closingBraceLoc, 1);
                if (test.equals("}")) {
                    return new OffsetRange(wordStart, closingBraceLoc - 1);
                }
            } catch (Exception e) {
                break;
            }
        }
        return null;
    }

    //TODO: nested curly braces?
    public List<OffsetRange> getBracedContentRanges(int minContentLen, int maxContentLen) {
        List<Integer> starts = getStartOfStrings("\\{");
        ArrayList<OffsetRange> contentRanges = new ArrayList<>();
        for (int start : starts) {
            OffsetRange range = getBracedContentRange(start, minContentLen, maxContentLen);
            if (range != null) {
                contentRanges.add(range);
            }
        }
        return contentRanges;
    }

}
