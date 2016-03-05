public class Record {

  // common header
  public String magic;
  public int firstAttrOffset;
  public int alive;
  public int recordNum;

  //  common attribute header
  public int type;
  public int attrLength;
  public int resident;
  public int contentOffset;

//  standard attribute ($10)
  public String createdTime;
  public String modifiedTime;
  public String entryModifiedTime;
  public String accessedTime;

//  name attribute ($30)
  public int parentRecordNum;
  public int nameSize;
  public String filename = "";

}
