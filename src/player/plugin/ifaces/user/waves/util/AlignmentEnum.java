package player.plugin.ifaces.user.waves.util;

public enum AlignmentEnum {
    NO_ALIGNMENT {
        public void updateEndIndicesBasedOnEnumValue(EndIndicesIntPair classToBeUpdated,
                                               int inputStartIndex, int inputEndIndex,
                                               int outputStartIndex, int outputEndIndex,
                                               int inputSongLength, int outputSongLength) {
            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_SHORTER {
        public void updateEndIndicesBasedOnEnumValue(EndIndicesIntPair classToBeUpdated,
                                                     int inputStartIndex, int inputEndIndex,
                                                     int outputStartIndex, int outputEndIndex,
                                                     int inputSongLength, int outputSongLength) {
            int inputLen = inputEndIndex - inputStartIndex;
            int outputLen = outputEndIndex - outputStartIndex;

            if (outputLen > inputLen) {
                int dif = outputLen - inputLen;
                outputEndIndex -= dif;
            }
            else if (outputLen < inputLen) {
                int dif = inputLen - outputLen;
                inputEndIndex -= dif;
            }

            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_LONGER {
        public void updateEndIndicesBasedOnEnumValue(EndIndicesIntPair classToBeUpdated,
                                                     int inputStartIndex, int inputEndIndex,
                                                     int outputStartIndex, int outputEndIndex,
                                                     int inputSongLength, int outputSongLength) {
            int inputLen = inputEndIndex - inputStartIndex;
            int outputLen = outputEndIndex - outputStartIndex;
            if (outputLen > inputLen) {
                int dif = outputLen - inputLen;
                inputEndIndex = Math.min(inputEndIndex + dif, inputSongLength);
            }
            else if (outputLen < inputLen) {
                int dif = inputLen - outputLen;
                outputEndIndex = Math.min(outputEndIndex + dif, outputSongLength);
            }

            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_INPUT {
        public void updateEndIndicesBasedOnEnumValue(EndIndicesIntPair classToBeUpdated,
                                                     int inputStartIndex, int inputEndIndex,
                                                     int outputStartIndex, int outputEndIndex,
                                                     int inputSongLength, int outputSongLength) {
            int inputLen = inputEndIndex - inputStartIndex;
            int outputLen = outputEndIndex - outputStartIndex;
            int dif = outputLen - inputLen;
            outputEndIndex = Math.min(outputEndIndex - dif, outputSongLength);

            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_OUTPUT {
        public void updateEndIndicesBasedOnEnumValue(EndIndicesIntPair classToBeUpdated,
                                                     int inputStartIndex, int inputEndIndex,
                                                     int outputStartIndex, int outputEndIndex,
                                                     int inputSongLength, int outputSongLength) {
            int inputLen = inputEndIndex - inputStartIndex;
            int outputLen = outputEndIndex - outputStartIndex;
            int dif = inputLen - outputLen;
            inputEndIndex = Math.min(inputEndIndex - dif, inputSongLength);

            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    };


    /**
     * Takes the input parameters and based on the parameters and the enum value changes the end indices and puts them to
     * to the classToBeUpdated parameter. The values inside classToBeUpdated aren't considered, it is just used as
     * output parameter.
     * @param classToBeUpdated is the container with end indices to be updated. Output parameter.
     * @param inputStartIndex
     * @param inputEndIndex
     * @param outputStartIndex
     * @param outputEndIndex
     * @param inputSongLength
     * @param outputSongLength
     */
    abstract public void updateEndIndicesBasedOnEnumValue(EndIndicesIntPair classToBeUpdated,
                                                          int inputStartIndex, int inputEndIndex,
                                                          int outputStartIndex, int outputEndIndex,
                                                          int inputSongLength, int outputSongLength);

    private static void setEndIndices(EndIndicesIntPair classToBeUpdated, int inputEndIndex, int outputEndIndex) {
        classToBeUpdated.inputWaveEndIndex = inputEndIndex;
        classToBeUpdated.outputWaveEndIndex = outputEndIndex;
    }


    public static String[] getEnumsToStrings() {
        AlignmentEnum[] values = AlignmentEnum.values();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].toString();
        }

        return strings;
    }


    public static AlignmentEnum convertStringToEnumValue(String s) {
        AlignmentEnum[] values = AlignmentEnum.values();
        for (AlignmentEnum v : values) {
            if (v.toString().equals(s)) {
                return v;
            }
        }

        return null;
    }
}
