package util;

// TODO: ABS_MIN and ABS_MAX aren't in all performAggregation methods
//  (for obvious reasons - unsigned numbers, etc. - I have to fix that later, currently it is only in the double variant)

/**
 * Enumeration representing the possible aggregation of n values.
 */
public enum Aggregation {
    MIN {
        public int defaultValueForMod() {
            return Integer.MAX_VALUE;
        }
    },
    ABS_MIN {
        public int defaultValueForMod() {
            return Integer.MAX_VALUE;
        }
    },
    MAX {
        public int defaultValueForMod() {
            return Integer.MIN_VALUE;
        }
    },
    ABS_MAX {
        public int defaultValueForMod() {
            return 0;
        }
    },
    AVG {
        public int defaultValueForMod() {
            return 0;
        }
    },
    RMS {
        public int defaultValueForMod() {
            return 0;
        }
    },
    SUM {
        public int defaultValueForMod() {
            return 0;
        }
    };


    /**
     * Returns default value for given aggregation.
     * @return Returns default value for given aggregation.
     */
    public abstract int defaultValueForMod();
}
