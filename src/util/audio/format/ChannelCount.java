package util.audio.format;

public enum ChannelCount {
    MONO(1) {
        @Override
        public String toString() {
            return "MONO";
        }
    },
    STEREO(2) {
        @Override
        public String toString() {
            return "STEREO";
        }
    }/*,            // TODO: Currently not supported
    QUADRO(4) {
        @Override
        public String toString() {
            return "QUADROPHONIC";
        }
    },
    FIVE_POINT_ONE(6) {
        @Override
        public String toString() {
            return "5.1";
        }
    },
    SEVEN_POINT_ONE(8) {
        @Override
        public String toString() {
            return "7.1";
        }
    }*/;


    public final int CHANNEL_COUNT;
    private ChannelCount(int channelCount) {
        CHANNEL_COUNT = channelCount;
    }

    public static ChannelCount convertNumberToEnum(int channelCount) {
        switch(channelCount) {
            case 1:
                return MONO;
            case 2:
                return STEREO;
// TODO: Currently not supported
//            case 4:
//                return QUADRO;
//            case 6:
//                return FIVE_POINT_ONE;
//            case 8:
//                return SEVEN_POINT_ONE;
            default:
                return STEREO;
        }
    }



    public static ChannelCount parseChannel(String s) {
        ChannelCount[] values = ChannelCount.values();
        for (ChannelCount v : values) {
            if(v.toString().equals(s)) {
                return v;
            }
        }

        return null;
    }

    public static String[] getEnumsToStrings() {
        ChannelCount[] values = ChannelCount.values();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].toString();
        }

        return strings;
    }
}
