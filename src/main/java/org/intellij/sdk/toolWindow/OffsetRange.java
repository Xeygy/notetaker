package org.intellij.sdk.toolWindow;

import java.util.List;

public class OffsetRange {
    private final int start;
    private final int end;

    /** class for managing a range of numbers.
     * inclusive on both ends
     * start <= end
     * @param start the start of the range
     * @param end the end of the range
     */
    public OffsetRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    /** tells if n is in range (start, end) inclusive */
    public boolean contains(int n) {
        if (start > end) {
            return false;
        }
        return n >= start && n <= end;
    }

    /** number of ints in the range, inclusive */
    public int size() {
        return end - start + 1;
    }

    /** returns the first range n is in from the list
     * null otherwise */
    public static OffsetRange getRangeWith(List<OffsetRange> ranges, int n) {
        for (OffsetRange range : ranges) {
            if (range.contains(n)) {
                return range;
            }
        }
        return null;
    }


}
