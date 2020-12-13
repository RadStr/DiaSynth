package RocnikovyProjektIFace.AudioPlayerPlugins.ifaces.PluginIFacesForUsers.WithInputWavePackage;

public enum AlignmentEnum {
    NO_ALIGNMENT {
        public void updateDataBasedOnEnumValue(AbstractPluginClass classToBeUpdated,
                                               int inputStartIndex, int inputEndIndex,
                                               int outputStartIndex, int outputEndIndex,
                                               int inputSongLength, int outputSongLength) {
            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_SHORTER {
        public void updateDataBasedOnEnumValue(AbstractPluginClass classToBeUpdated,
                                               int inputStartIndex, int inputEndIndex,
                                               int outputStartIndex, int outputEndIndex,
                                               int inputSongLength, int outputSongLength) {
            int inputLen = inputEndIndex - inputStartIndex;
            int outputLen = outputEndIndex - outputStartIndex;

            if(outputLen > inputLen) {
                int dif = outputLen - inputLen;
                outputEndIndex -= dif;
            }
            else if(outputLen < inputLen) {
                int dif = inputLen - outputLen;
                inputEndIndex -= dif;
            }

            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_LONGER {
        public void updateDataBasedOnEnumValue(AbstractPluginClass classToBeUpdated,
                                               int inputStartIndex, int inputEndIndex,
                                               int outputStartIndex, int outputEndIndex,
                                               int inputSongLength, int outputSongLength) {
            int inputLen = inputEndIndex - inputStartIndex;
            int outputLen = outputEndIndex - outputStartIndex;
            if(outputLen > inputLen) {
                int dif = outputLen - inputLen;
                inputEndIndex = Math.min(inputEndIndex + dif, inputSongLength);
            }
            else if(outputLen < inputLen) {
                int dif = inputLen - outputLen;
                outputEndIndex = Math.min(outputEndIndex + dif, outputSongLength);
            }

            setEndIndices(classToBeUpdated, inputEndIndex, outputEndIndex);
        }
    },
    ALIGN_TO_INPUT {
        public void updateDataBasedOnEnumValue(AbstractPluginClass classToBeUpdated,
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
        public void updateDataBasedOnEnumValue(AbstractPluginClass classToBeUpdated,
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

    abstract public void updateDataBasedOnEnumValue(AbstractPluginClass classToBeUpdated,
                                                    int inputStartIndex, int inputEndIndex,
                                                    int outputStartIndex, int outputEndIndex,
                                                    int inputSongLength, int outputSongLength);

    private static void setEndIndices(AbstractPluginClass classToBeUpdated, int inputEndIndex, int outputEndIndex) {
        classToBeUpdated.setInputEndIndex(inputEndIndex);
        classToBeUpdated.setOutputEndIndex(outputEndIndex);
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
            if(v.toString().equals(s)) {
                return v;
            }
        }

        return null;
    }
}
