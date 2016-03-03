import com.sun.org.apache.regexp.internal.RE;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yuto on 2016/03/01.
 */
public class MFT {

  public RandomAccessFile mftFile;
  private int mftSize;

  public TreeMap<Integer, Record> records = new TreeMap<>();

  public MFT(RandomAccessFile mftFile) {
    this.mftFile = mftFile;
    this.mftSize = 0;
  }

  public void setMftSize(int mftSize) {
    this.mftSize = mftSize;
  }

  public int getMftSize() {
    return mftSize;
  }
}

