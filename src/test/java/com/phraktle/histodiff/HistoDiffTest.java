package com.phraktle.histodiff;

import static org.junit.Assert.*;
import static com.phraktle.histodiff.HistoDiff.diff;

import org.junit.Test;

public class HistoDiffTest {

    @Test
    public void testDiff() {
        long[] pos = new long[] { 1, 2 };
        long[] zero = new long[] { 0, 0 };
        long[] neg = new long[] { -1, -2 };
        assertArrayEquals(zero, diff(pos, pos));
        assertArrayEquals(pos, diff(zero, pos));
        assertArrayEquals(neg, diff(pos, zero));
        assertArrayEquals(pos, diff(null, pos));
        assertArrayEquals(neg, diff(pos, null));
    }

}
