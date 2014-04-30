package com.phraktle.histodiff;

import static org.junit.Assert.*;
import static com.phraktle.histodiff.HistoDiff.diff;

import org.junit.Test;

public class HistoDiffTest {

    @Test
    public void testDiff() {
        int[] pos = new int[] { 1, 2 };
        int[] zero = new int[] { 0, 0 };
        int[] neg = new int[] { -1, -2 };
        assertArrayEquals(zero, diff(pos, pos));
        assertArrayEquals(pos, diff(zero, pos));
        assertArrayEquals(neg, diff(pos, zero));
        assertArrayEquals(pos, diff(null, pos));
        assertArrayEquals(neg, diff(pos, null));
    }

}
