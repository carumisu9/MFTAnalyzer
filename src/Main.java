import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.RandomAccess;

/**
 * Created by yuto on 2016/03/01.
 */
public class Main {
  public static void main(String[] args) throws IOException {

    MFT mft = null;
    try{
      mft = new MFT(new RandomAccessFile("res/vss001-$MFT", "r"));
    } catch (Exception e){
      e.printStackTrace();
    }

//    analyze mft
    MftAnalyzer analyzer = new MftAnalyzer();
    analyzer.setMft(mft);
    analyzer.analyze();

//    print mft.csv
    CSVPrinter printer = new CSVPrinter();
    printer.print(mft);

    try{
      if(mft.mftFile != null)
        mft.mftFile.close();
    } catch (Exception e){
      e.printStackTrace();
    }


  }
}
