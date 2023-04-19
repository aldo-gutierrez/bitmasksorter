package com.aldogg.sorter.test;

import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.int_.IntSorter;
import com.aldogg.sorter.int_.mt.JavaSorterMTInt;
import com.aldogg.sorter.int_.mt.MixedBitSorterMTInt;
import com.aldogg.sorter.int_.mt.QuickBitSorterMTInt;
import com.aldogg.sorter.int_.mt.RadixBitSorterMTInt;
import com.aldogg.sorter.int_.st.JavaSorterInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;
import com.aldogg.sorter.test.performance.IntSorterPTest;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SorterSecondTest extends BaseTest {
    @Test
    public void testFunctionsSingleThread() throws IOException {
        IntSorter[] sorters = new IntSorter[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

        GeneratorParams params = new GeneratorParams();
        params.random = new Random();
        params.size = 4096;
        params.limitLow = 0;
        params.limitHigh = params.size;

        BufferedWriter writer = getWriter("speed2.csv");
        writer.write("\"Size\"" + "," + "\"Range\"" + "," + "\"Sorter\"" + "," + "\"Time\"" + "\n");

        GeneratorFunctions[] values = GeneratorFunctions.values();
        for (GeneratorFunctions a : values) {
            if (!a.equals(GeneratorFunctions.RANDOM_REAL_RANGE)) {
                params.function = a;
                TestAlgorithms<IntSorter> testAlgorithms = new TestAlgorithms<>(sorters);
                testSpeedInt(ITERATIONS, params, testAlgorithms);
                testAlgorithms.printTestSpeed(params, writer);
            }
        }

    }

}
