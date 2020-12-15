package util.audio;

/**
 * Contains bytes array together with int representing how many bytes in the array are valid.
 */
@Deprecated
public class BytesReadWithArr {
    public byte[] arr;
    public int bytesRead;

    public BytesReadWithArr(byte[] arr, int bytesRead) {
        this.arr = arr;
        this.bytesRead = bytesRead;
    }
}

