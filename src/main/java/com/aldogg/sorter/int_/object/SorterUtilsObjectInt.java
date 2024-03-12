package com.aldogg.sorter.int_.object;

import com.aldogg.sorter.RuntimeOptionsInt;
import com.aldogg.sorter.shared.int_mask.MaskInfoInt;
import com.aldogg.sorter.shared.Section;
import com.aldogg.sorter.int_.SorterUtilsInt;

import static com.aldogg.sorter.generic.SorterUtilsGeneric.swap;

public class SorterUtilsObjectInt {

    public static int partitionNotStable(final Object[] oArray, final int oStart, final int[] array, int aStart, final int mask, int n) {
        int left = aStart;
        int right = aStart + n - 1;
        int shift = -aStart + oStart;

        while (left <= right) {
            int element = array[left];
            if ((element & mask) == 0) {
                left++;
            } else {
                while (left <= right) {
                    element = array[right];
                    if ((element & mask) == 0) {
                        SorterUtilsInt.swap(array, left, right);
                        swap(oArray, left + shift, right + shift);
                        left++;
                        right--;
                        break;
                    } else {
                        right--;
                    }
                }
            }
        }
        return left;
    }

    public static int partitionReverseNotStable(final Object[] oArray, final int oStart, final int[] array, int aStart, final int mask, int n) {
        int left = aStart;
        int right = aStart + n - 1;
        int shift = -aStart + oStart;

        while (left <= right) {
            int element = array[left];
            if ((element & mask) == 0) {
                while (left <= right) {
                    element = array[right];
                    if (((element & mask) == 0)) {
                        right--;
                    } else {
                        SorterUtilsInt.swap(array, left, right);
                        swap(oArray, left + shift, right + shift);
                        left++;
                        right--;
                        break;
                    }
                }
            } else {
                left++;
            }
        }
        return left;
    }

    public static int partitionStable(RuntimeOptionsInt runtime, final int oStart, final int mask, int n) {
        if (runtime.aux == null) {
            runtime.oAux = new Object[n];
            runtime.aux = new int[n];
            return partitionStable(runtime, oStart, mask, 0, n);
        } else {
            System.err.println("not supported code path");
            throw new IllegalStateException("not supported code path");
        }
    }

    public static int partitionStable(RuntimeOptionsInt runtime, int oStart, final int mask,
                                      int startAux, int n) {
        final Object[] oArray = runtime.oArray;
        final int[] array = runtime.array;
        final Object[] oAux = runtime.oAux;
        final int[] aux = runtime.aux;
        int left = startAux;
        int oLeft = oStart;
        int right = startAux;
        int endP1 = startAux + n;
        for (int i = startAux, oi = oStart; i < endP1; i++, oi++) {
            int element = array[i];
            Object oElement = oArray[oi];
            if ((element & mask) == 0) {
                array[left] = element;
                oArray[oLeft] = oElement;
                left++;
                oLeft++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        int lengthRight = right - startAux;
        System.arraycopy(aux, startAux, array, left, lengthRight);
        System.arraycopy(oAux, startAux, oArray, oLeft, lengthRight);
        return oLeft;
    }

    public static int partitionReverseStable(RuntimeOptionsInt runtime, final int oStart, final int mask, int n) {
        if (runtime.aux == null) {
            runtime.oAux = new Object[n];
            runtime.aux = new int[n];
            return partitionReverseStable(runtime, oStart, mask, 0, n);
        } else {
            System.err.println("not supported code path");
            throw new IllegalStateException("not supported code path");
        }
    }


    public static int partitionReverseStable(final RuntimeOptionsInt runtime, final int oStart, final int mask,
                                             int startAux, int n) {
        final Object[] oArray = runtime.oArray;
        final int[] array = runtime.array;
        final Object[] oAux = runtime.oAux;
        final int[] aux = runtime.aux;

        int left = startAux;
        int oLeft = oStart;
        int right = startAux;
        int endP1 = startAux + n;
        for (int i = startAux, oi = oStart; i < endP1; i++, oi++) {
            int element = array[i];
            Object oElement = oArray[oi];
            if (!((element & mask) == 0)) {
                array[left] = element;
                oArray[oLeft] = oElement;
                left++;
                oLeft++;
            } else {
                aux[right] = element;
                oAux[right] = oElement;
                right++;
            }
        }
        int lengthRight = right - startAux;
        System.arraycopy(aux, startAux, array, left, lengthRight);
        System.arraycopy(oAux, startAux, oArray, oLeft, lengthRight);
        return oLeft;
    }

    public static int[] partitionStableLastBits(RuntimeOptionsInt runtime, final int oStart, int aStart, final Section section,
                                                final int startAux, final int n) {
        final Object[] oArray = runtime.oArray;
        final int[] array = runtime.array;
        final Object[] oAux = runtime.oAux;
        final int[] aux = runtime.aux;
        int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        int endP1 = aStart + n;
        int[] count = new int[1 << section.bits];
        for (int i = aStart; i < endP1; i++) {
            count[array[i] & mask]++;
        }
        final int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        final int shift = -aStart + oStart;
        if (startAux == 0) {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                int auxIndex = count[elementShiftMasked];
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i + shift];
                count[elementShiftMasked]++;
            }
        } else {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = element & mask;
                int auxIndex = count[elementShiftMasked] + startAux;
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i + shift];
                count[elementShiftMasked]++;
            }
        }
        System.arraycopy(aux, startAux, array, aStart, n);
        System.arraycopy(oAux, startAux, oArray, oStart, n);
        return count;
    }

    public static int[] partitionStableOneGroupBits(RuntimeOptionsInt runtime, final int oStart, int aStart, final Section section,
                                                    final int startAux, final int n) {
        final Object[] oArray = runtime.oArray;
        final int[] array = runtime.array;
        final Object[] oAux = runtime.oAux;
        final int[] aux = runtime.aux;
        final int mask = MaskInfoInt.getMaskRangeBits(section.start, section.shift);
        final int shiftRight = section.shift;
        final int endP1 = aStart + n;
        final int[] count = new int[1 << section.bits];
        for (int i = aStart; i < endP1; i++) {
            count[(array[i] & mask) >>> shiftRight]++;
        }
        final int cLength = count.length;
        for (int i = 0, sum = 0; i < cLength; i++) {
            int countI = count[i];
            count[i] = sum;
            sum += countI;
        }
        final int shift = -aStart + oStart;
        if (startAux == 0) {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                int auxIndex = count[elementShiftMasked];
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i + shift];
                count[elementShiftMasked]++;
            }
        } else {
            for (int i = aStart; i < endP1; i++) {
                int element = array[i];
                int elementShiftMasked = (element & mask) >>> shiftRight;
                int auxIndex = count[elementShiftMasked] + startAux;
                aux[auxIndex] = element;
                oAux[auxIndex] = oArray[i + shift];
                count[elementShiftMasked]++;
            }
        }
        System.arraycopy(aux, startAux, array, aStart, n);
        System.arraycopy(oAux, startAux, oArray, oStart, n);
        return count;
    }

    public static int[] partitionStableNGroupBits(RuntimeOptionsInt runtime, final int oStart, int aStart, final Section[] sections,
                                                    final int startAux, final int n) {
        //TODO Is ok to use this code if bits length <= MAX BITS, otherwise think of code
        Section section = Section.createWithStarAndShift(sections[0].start, sections[sections.length - 1].shift);
        int[] count;
        if (!(section.shift == 0)) {
            count = partitionStableOneGroupBits(runtime, oStart, aStart, section, startAux, n);
        } else {
            count = partitionStableLastBits(runtime, oStart, aStart, section, startAux, n);
        }
        return count;
    }
}