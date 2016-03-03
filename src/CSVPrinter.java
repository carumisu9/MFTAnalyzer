import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by yuto on 2016/03/01.
 */
public class CSVPrinter extends Printer {

  private String fileName;

  @Override
  public void print() {

  }

  public void print(MFT mft) throws IOException {

    PrintWriter output =
        new PrintWriter(
            new BufferedWriter(
                new FileWriter("res/output.csv", false
                )
            )
        );

//    for(int i : mft.records.keySet()){

      Record record = mft.records.get(0);

      List<String> list = new ArrayList<>();
      list.add(String.valueOf(record.recordNum));
      list.add(String.valueOf(record.alive));
      list.add(String.valueOf(record.filename));
      list.add(record.createdTime);
      list.add(record.modifiedTime);
      list.add(record.entryModifiedTime);
      list.add(record.accessedTime);
      list.add("\n");

      output.print(String.join(",", list));

//    }

    output.close();

  }
}
