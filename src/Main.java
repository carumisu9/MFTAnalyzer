import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.RandomAccess;

public class Main {
  public static void main(String[] args) throws IOException {

    MFT mft = null;
    try{
      mft = new MFT(new RandomAccessFile(args[0], "r"));
    } catch (Exception e){
      e.printStackTrace();
    }

//    analyze mft
    MftAnalyzer analyzer = new MftAnalyzer();
    analyzer.setMft(mft);
    analyzer.analyze();

//    print mft.csv
    CSVPrinter printer = new CSVPrinter();
    printer.print(mft, args[1]);

    try{
      if(mft.mftFile != null)
        mft.mftFile.close();
    } catch (Exception e){
      e.printStackTrace();
    }

  }
}
