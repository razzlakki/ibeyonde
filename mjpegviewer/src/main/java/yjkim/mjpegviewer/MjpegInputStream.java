package yjkim.mjpegviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MjpegInputStream extends DataInputStream{
    // 0. Variables
    private final static int FRAME_MAX_LENGTH = 200000;
    private final byte[] SOI_MARKER = {(byte)0xFF, (byte)0xD8};
    private final byte[] EOF_MARKER = {(byte)0xFF, (byte)0xD9};
    private int mContentLength = -1;

    byte[] header = null;
    byte[] frameData = null;

    int headerLen = -1;
    int headerLenPrev = -1;

    int skip = 1;
    int count = 0;

    // 1. Constructor
    public MjpegInputStream(InputStream in){
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    // 2. Methods
    /*********************************************************************************************/
    /* Return end index of certain byte sequence                                                 */
    /*********************************************************************************************/
    private int getEndOfSequence(byte[] sequence) throws IOException{
        int seqIndex = 0;
        byte c;
        for(int i = 0; i < FRAME_MAX_LENGTH; i++){
            c = (byte)this.readUnsignedByte();
            if(c == sequence[seqIndex]){
                seqIndex ++;
                if(seqIndex == sequence.length){
                    return i + 1;
                }
            }else seqIndex = 0;
        }
        return -1;
    }

    /*********************************************************************************************/
    /* Return start index of certain byte sequence                                               */
    /*********************************************************************************************/
    private int getStartOfSequence(byte[] sequence) throws IOException{
        int end = getEndOfSequence(sequence);
        return (end < 0 ) ? -1 : (end - sequence.length);
    }

    /*********************************************************************************************/
    /* Parsing HTTP header & return "Content-Length" property                                    */
    /*********************************************************************************************/
    private int parseContentLength(byte[] headerBytes)
            throws IOException, NumberFormatException{

        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        if(props.getProperty("Content-Length")!=null)
             return Integer.parseInt(props.getProperty("Content-Length"));
        return Integer.parseInt(props.getProperty("Content-length"));
    }

    /*********************************************************************************************/
    /* Read one frame and return Bitmap image(Using java decoding function ..may slow)           */
    /*********************************************************************************************/
    public Bitmap readMjpegFrame() throws IOException{
        mark(FRAME_MAX_LENGTH); // reading start

        // 1. Get Header Length
        try{
            headerLen = getStartOfSequence(SOI_MARKER);
        }catch (IOException e){
            Log.e("xxxxx1--1",e.getLocalizedMessage());
            reset();
            return null;
        }

        // 2. Get Header
        reset();
        if(header == null || headerLen != headerLenPrev){ // Header is renewed
            header = new byte[headerLen];
        }
        headerLenPrev = headerLen;
        readFully(header);

        // 3. Get Content Length
        mContentLength = -1;
        try{
            mContentLength = parseContentLength(header);
        }catch (NumberFormatException nfe){ // if illegal format
            Log.e("xxxxx2",nfe.getLocalizedMessage());
            reset();
            return null;
        }catch (IOException e){
            reset();
            return null;
        }

        // 4. Get Frame data
        reset();
        if(frameData == null){
            frameData = new byte[FRAME_MAX_LENGTH];
        }
        skipBytes(headerLen);
        readFully(frameData, 0, mContentLength);

        if(count ++ % skip == 0){
            count = 0;
            //return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData, 0, mContentLength));
            return BitmapFactory.decodeByteArray(frameData, 0, mContentLength);
        }else{
            return null;
        }
    }

    /*********************************************************************************************/
    /* Read one frame and return Bitmap image(Using libjpeg-turbo)                               */
    /*********************************************************************************************/
    //TODO
}
