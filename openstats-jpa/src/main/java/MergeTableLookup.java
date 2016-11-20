import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class MergeTableLookup {
	
	public static void main(String... args) throws IOException {
		new MergeTableLookup().run();
	}
	
	public void run() throws IOException {
        // create a new file input stream with the input file specified
        // at the command line
        FileInputStream fin = new FileInputStream("/home/knicholas/Downloads/ACS2012_5-Year_TableShells.xls");
        // create a new org.apache.poi.poifs.filesystem.Filesystem
        POIFSFileSystem poifs = new POIFSFileSystem(fin);
        HSSFWorkbook workbook = new HSSFWorkbook(poifs);
        HSSFSheet sheet = workbook.getSheetAt(0);
        for ( Row row: sheet ) {
        	Cell aCell = row.getCell(0);
        	
        	if ( aCell != null && aCell.toString().equals("B04001")) {
	        	for (Cell cell: row) {
	        		System.out.print(cell.getCellStyle().getIndention()+":"+cell+",");
	        	}
	        	System.out.println();
        	}
        }

        // once all the events are processed close our file input stream
        fin.close();
        System.out.println("done.");
	}

}
