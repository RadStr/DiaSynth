package Rocnikovy_Projekt;

/**
 * Enumeration representing the possible aggregation of n values.
 */
public enum Aggregations {
    MIN {
        public int defaultValueForMod() {
            return Integer.MAX_VALUE;
        }
    },
    MAX {
        public int defaultValueForMod() {
            return Integer.MIN_VALUE;
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
