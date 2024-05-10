package com.aldogg.sorter.test;

import com.aldogg.sorter.generators.GeneratorFunctions;
import com.aldogg.sorter.generators.GeneratorParams;
import com.aldogg.sorter.int_.SorterInt;
import com.aldogg.sorter.int_.st.JavaSorterInt;
import com.aldogg.sorter.int_.st.QuickBitSorterInt;
import com.aldogg.sorter.int_.st.RadixBitSorterInt;
import com.aldogg.sorter.int_.st.RadixByteSorterInt;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

public class SorterSecondTest extends BaseTest {
    @Test
    public void testFunctionsSingleThread() throws IOException {
        SorterInt[] sorters = new SorterInt[]{new JavaSorterInt(), new QuickBitSorterInt(), new RadixBitSorterInt(), new RadixByteSorterInt()};

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
                TestAlgorithms<SorterInt> testAlgorithms = new TestAlgorithms<>(sorters);
                testSpeedInt(ITERATIONS, params, testAlgorithms, null);
                testAlgorithms.printTestSpeed(params, writer);
            }
        }

    }

}
