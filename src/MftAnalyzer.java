import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

public class MftAnalyzer extends Analyzer {

  private MFT mft;

  @Override
  public void analyze() throws IOException {

    mft.setMftSize((int)(mft.mftFile.length()/1024));
    mft.mftFile.seek(0);

    for (int i=0; i<mft.getMftSize(); i++)
      analyzeRecord();

    analyzeNameInRecord();

  }

  public void analyzeRecord() throws IOException {

    Record record = new Record();

    byte[] data = new byte[1024];
    int size = mft.mftFile.read(data, 0, 1024);
    int pointer = 0;

    record = decodeCommonHeader(record, data);
    if(!record.magic.equals("FILE"))
      return;

    pointer = pointer + record.firstAttrOffset;

    while (pointer < 1024) {

      record = decodeCommonAttrHeader(
          record, Arrays.copyOfRange(data, pointer, data.length));

      if(record.type == 0x10){
        decodeStandardAttr(
            record, Arrays.copyOfRange(
                data, pointer + record.contentOffset, data.length));
      }else if (record.type == 0x20){

      }else if (record.type == 0x30){
        decodeNameAttr(
            record, Arrays.copyOfRange(
                data, pointer + record.contentOffset, data.length));
      }else{
        break;
      }

      pointer += record.attrLength;

    }

    if(record.filename.equals(""))
      return;
    mft.records.put(record.recordNum, record);
  }

  public void analyzeNameInRecord(){

    HashMap<Integer, String> fullPaths = new HashMap<>();

    for (int i : mft.records.keySet()) {
      if (!mft.records.get(i).filename.equals("")) {
        fullPaths.put(i, searchName(i));
      }
    }

    for(int i : fullPaths.keySet()){
      mft.records.get(i).filename = fullPaths.get(i);
    }

//    for (int i : mft.records.keySet()){
//      if(!mft.records.get(i).filename.equals(""))
//        debugPrint(mft.records.get(i).filename + ":" + mft.records.get(i).recordNum + "->" + mft.records.get(i).parentRecordNum);
//    }
  }

  public String searchName(int recordNum){
    String fullPath = "";

    while (true) {

      if(mft.records.containsKey(recordNum)) {

        if (mft.records.get(recordNum).recordNum == 5)
          break;

        if(fullPath.equals(""))
          fullPath = mft.records.get(recordNum).filename;
        else
          fullPath = mft.records.get(recordNum).filename + "/" + fullPath;

        if(!mft.records.containsKey(
            mft.records.get(recordNum).parentRecordNum)){
          fullPath = "Broken" + "/" + fullPath;
          break;
        }
        recordNum = mft.records.get(recordNum).parentRecordNum;

      }else {
        break;
      }
    }
    return "/" + fullPath;
  }

  public Record decodeCommonHeader(Record record, byte[] data){

    record.magic = new String(Arrays.copyOfRange(data, 0, 4));
    record.firstAttrOffset = data[20];
    record.alive = Byte.toUnsignedInt(data[22]);
    record.recordNum = littleEndian2int(Arrays.copyOfRange(data, 44, 48));

    return record;
  }

  public Record decodeCommonAttrHeader(Record record, byte[] data){

    record.type = littleEndian2int(Arrays.copyOfRange(data, 0, 4));
    record.attrLength = littleEndian2int(Arrays.copyOfRange(data, 4, 8));
    if(record.type != -1)
      record.contentOffset = Byte.toUnsignedInt(data[20]);

    return record;
  }

  public Record decodeStandardAttr(Record record, byte[] data){

    record.createdTime = byte2dataString(Arrays.copyOfRange(data, 0, 8));
    record.modifiedTime = byte2dataString(Arrays.copyOfRange(data, 8, 16));
    record.entryModifiedTime = byte2dataString(Arrays.copyOfRange(data, 16, 24));
    record.accessedTime = byte2dataString(Arrays.copyOfRange(data, 24, 32));

    return record;
  }

  public Record decodeNameAttr(Record record, byte[] data)
      throws UnsupportedEncodingException {

    record.parentRecordNum = littleEndian2int(Arrays.copyOfRange(data, 0, 8));
    record.nameSize = Byte.toUnsignedInt(data[64]);
    record.filename = new String(
        Arrays.copyOfRange(data, 66, 66+2*record.nameSize),"UTF-8")
        .replace("\u0000", "");

    return record;
  }

  public void setMft(MFT mft) {
    this.mft = mft;
  }

  public int littleEndian2int(byte[] data){
    return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
  }

  public void debugPrint(Object string) {
    System.out.println(string);
  }

  public String byte2dataString(byte[] data){
    byte[] tmp = new byte[8];
    for (int i=0;i<data.length;i++){
      tmp[i] = data[7-i];
    }
//    Long dateTime = Long.valueOf(HexBin.encode(tmp),16)/10000000;
//    debugPrint(dateTime);
//    Calendar calendar = Calendar.getInstance();
//    calendar.setTimeInMillis(145080004);
//    Date date = calendar.getTime();
//    debugPrint(calendar.getTimeInMillis());

    return HexBin.encode(tmp);
  }

}
