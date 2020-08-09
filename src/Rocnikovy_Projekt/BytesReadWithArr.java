package Rocnikovy_Projekt;

/**
 * Contains bytes array together with int representing how many bytes in the array are valid.
 */
public class BytesReadWithArr {
    byte[] arr;
    int bytesRead;

    public BytesReadWithArr(byte[] arr, int bytesRead) {
        this.arr = arr;
        this.bytesRead = bytesRead;
    }
}

