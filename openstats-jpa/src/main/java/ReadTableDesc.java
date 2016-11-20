

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import openstats.client.census.CensusTable;
import openstats.client.census.CensusTable.StringPair;
import openstats.client.openstates.OpenState;
import openstats.client.openstates.OpenStateClasses;
import openstats.dbmodel.Result;
import openstats.dbmodel.DBAssemblyHandler;
import openstats.model.Assembly;
import openstats.model.District;
import openstats.model.District.CHAMBER;
import openstats.model.Group;
import openstats.model.InfoItem;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jackson.map.ObjectMapper;


public class ReadTableDesc {
	
	private EntityManagerFactory emf;
	private EntityManager em;

	private void initJpa() throws Exception {
		emf = Persistence.createEntityManagerFactory("openstats");
		em = emf.createEntityManager();
	}
	

	public static void main(String... args) throws Exception {
		new ReadTableDesc().run();
	}
	
	private class ProcessStat {
		String seqNumber;
		int cellStartPos; 
		int cellCount;
		CensusTable censusTable;
		public ProcessStat(String seqNumber, int cellStartPos, int saveCellCount, CensusTable censusTable) {
			this.seqNumber = seqNumber;
			this.cellStartPos = cellStartPos;
			this.cellCount = saveCellCount;
			this.censusTable = censusTable;
		}		
		@Override
		public String toString() {
			return "("+censusTable.tableId+":"+seqNumber+":"+cellStartPos+":"+cellCount+")";
		}
		
	}
	
	private class CensusRecordNo implements Comparable<CensusRecordNo>{
		int recordNo;
		String geoid;
		List<String> values = new ArrayList<String>();
		public CensusRecordNo(int recordNo, String geoid) {
			this.recordNo = recordNo;
			this.geoid = geoid;
		}
		@Override
		public int compareTo(CensusRecordNo o) {			
			return recordNo-o.recordNo;
		}
		
	}
	
	public void run() throws Exception {
		try {
			initJpa();
	
			List<ProcessStat> processStatList = createProcessStatList();
			//
			addCensusCellLabels(processStatList);

//			List<Assembly> assemblies = new ArrayList<Assembly>();
			OpenState openState = new OpenStateClasses.GAOpenState();
			
			// process censusTable here ..
//			List<Assembly> assemblies = processCensusTable(processStatList);
//			for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
//			Assembly assembly = DBAssemblyHandler.getAssembly(openState.getState(), openState.getSession(), em);
//			Assembly assembly = new Assembly(openState.getState(), openState.getSession());;
//			censusAssembly.censusAssembly(openState, censusTable, assembly);
//			System.out.println("State:"+openState.getState()+":"+processStatList);
//			assemblyFacade.writeAssembly(assembly);
//			System.out.println(assembly.getState()+":"+assembly.getDistricts().getDistrictList().size());

			List<Assembly> assemblies = processCensusTable(openState, processStatList);
			
			 ObjectMapper mapper = new ObjectMapper();
			 mapper.writerWithDefaultPrettyPrinter().writeValue(Files.newBufferedWriter(Paths.get("/home/knicholas/workspace/openstats-jpa/assemblies.json"), Charset.forName("utf-8")), assemblies);
			
		} finally {
			emf.close();
		}
		
	}
	
	private List<ProcessStat> createProcessStatList() throws IOException {
		CSVParser parser = CSVParser.parse(new File("c:/users/karl/censusdata/Sequence_Number_and_Table_Number_Lookup.txt"), Charset.forName("UTF-8"), CSVFormat.DEFAULT);
		CSVRecord priorRecord = null;
		int cellCount = 0;
		ProcessStat processStat = null;
		List<ProcessStat> processStatList = new ArrayList<ProcessStat>();

//		String prefix = null;
		for(CSVRecord record: parser) {

			String descr = record.get(7);
			String recordId = record.get(1);
			char lastOfRID = recordId.charAt(recordId.length()-1);
//			if ( Character.isDigit(lastOfRID) && descr.contains("Universe:") ) System.out.println(recordId+":"+descr);
			if ( 
//				(descr.equals("Universe: Households") 
				descr.equals("Universe:  Total population")
//				|| descr.equals("Universe:  Families")
//				|| descr.equals("Universe:  Nonfamily households")
//				|| descr.equals("Universe:  Housing units")
//				|| descr.equals("Universe:  Total population in the United States")
				
//				)
				&& Character.isDigit(lastOfRID) 
			) {
				if ( processStat != null ) {
					// process censusTable here ..
					processStatList.add(processStat);
				}
				// start a new one
				String tableDescr = priorRecord.get(7);
				cellCount = Integer.parseInt( priorRecord.get(5).trim().split(" ")[0] );
				processStat = new ProcessStat(
						priorRecord.get(2),
						Integer.parseInt( priorRecord.get(4).trim()),
						cellCount, 
						new CensusTable(priorRecord.get(1), tableDescr)
					);
				System.out.println(""+priorRecord.get(1)+":"+priorRecord.get(7));
//				System.out.println(priorRecord);
			}
			priorRecord = record;
			
		}
		processStatList.add(processStat);

		return processStatList;
	}
	
	private void addCensusCellLabels(List<ProcessStat> processStatList) throws IOException {
		Map<String, ProcessStat> tables = new TreeMap<String, ProcessStat>();
		
		for( ProcessStat processStat: processStatList) {
			tables.put(processStat.censusTable.tableId, processStat);
		}
		
        FileInputStream fin = new FileInputStream("c:/users/karl/censusdata/ACS2012_5-Year_TableShells.xls");
        // create a new org.apache.poi.poifs.filesystem.Filesystem
        POIFSFileSystem poifs = new POIFSFileSystem(fin);
        HSSFWorkbook workbook = new HSSFWorkbook(poifs);
        HSSFSheet sheet = workbook.getSheetAt(0);
        String currentTable = null;
        List<String> currentLabels = new ArrayList<String>();

        for ( Row row: sheet ) {
        	Cell aCell = row.getCell(0);
        	
        	if ( aCell != null && tables.containsKey(aCell.toString()) ) {
        		ProcessStat processStat = tables.get(aCell.toString());
        		if ( !processStat.censusTable.tableId.equals(currentTable) ) {
        			System.out.println();
        			currentTable = processStat.censusTable.tableId;
        		}
/*
    			for(Cell rCell: row) {
    				if ( rCell.getColumnIndex() == 3 ) System.out.print("("+rCell.getCellStyle().getIndention()+")");
    				System.out.print(rCell+"*");
    			}
    			System.out.println();
*/    			

        		if ( !row.getCell(1).toString().isEmpty() && !row.getCell(1).toString().equals("0.5")) {
        			Cell cell = row.getCell(3);
        			int indent = cell.getCellStyle().getIndention();
        			if (currentLabels.size() < (indent+1) ) currentLabels.add(cell.toString());
        			if (currentLabels.size() == (indent+1) ) currentLabels.set(indent, cell.toString());
        			if (currentLabels.size() > (indent+1)) {
        				for ( ; indent<currentLabels.size(); ) {
        					currentLabels.remove(currentLabels.size()-1);
        				}
        				currentLabels.add(cell.toString());
        			}
        			processStat.censusTable.cells.add(new StringPair(row.getCell(2).toString(), labelsToString(currentLabels)));
        			System.out.println(processStat.censusTable.tableId+" "+row.getCell(2).toString()+" "+labelsToString(currentLabels));
        		}
        	}
        }

        // once all the events are processed close our file input stream
        fin.close();
        System.out.println("done.");
		
		
	}
	
	private String labelsToString(List<String> currentLabels) {
		StringBuilder sb = new StringBuilder();
		for ( String label: currentLabels) {
			sb.append(label);
		}
		if ( sb.charAt(sb.length()-1) == ':' ) sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	private List<Assembly> processCensusTable(OpenState openState, List<ProcessStat> processStatList) throws Exception {
//		CensusAssembly censusAssembly = new CensusAssembly();
		List<Assembly> assemblies = new ArrayList<Assembly>();
		String cacheDir = "c:/users/karl/censusdata/";
		String fileName = "g20125" + openState.getState().toLowerCase()+".txt";
//			if ( !Files.exists(Paths.get(cacheDir+fileName)) ) {
//				cacheFile(openState, cacheDir, fileName);
//			}
		
		// Alaska/All_Geographies_Not_Tracts_Block_Groups/"
		
//			ZipFile zipFile = new ZipFile(new File(new URI(fileUrl)));
		
		List<CensusRecordNo> censusRecordNos = new ArrayList<CensusRecordNo>();
		BufferedReader reader = new BufferedReader(new FileReader(cacheDir+fileName));
		int recordNoMin = Integer.MAX_VALUE, recordNoMax = 0;
		String line;
		while ( (line = reader.readLine()) != null ) {
			if ( line.contains("61000US") || line.contains("62000US")) {
				String[] split = line.split("\\b");
	//					System.out.println(split[3]+":"+split[5]+":"+split[7]+":"+split[9]);
				CensusRecordNo censusRecordNo = new CensusRecordNo(Integer.parseInt(split[3].substring(8)), split[9]);
				if ( censusRecordNo.recordNo < recordNoMin ) recordNoMin = censusRecordNo.recordNo;
				if ( censusRecordNo.recordNo > recordNoMax ) recordNoMax = censusRecordNo.recordNo;
				censusRecordNos.add(censusRecordNo);
			}
		}
		reader.close();
	
		Collections.sort(censusRecordNos);
	
		System.out.println("Min:" + recordNoMin +":Max:" + recordNoMax);
		Assembly templateAssembly = DBAssemblyHandler.getAssembly(openState.getState(), openState.getSession(), em);
		for ( ProcessStat processStat: processStatList ) {
	//			ProcessStat processStat  = processStatList.get(1);
			Assembly assembly = new Assembly(templateAssembly);
			assemblies.add(assembly);
	
			Group group = new Group(processStat.censusTable.tableId, processStat.censusTable.tableDescr);
			assembly.setGroup(group);
			List<InfoItem> infoItems = new ArrayList<InfoItem>();
			System.out.println(processStat.censusTable.tableId+":"+processStat.censusTable.tableDescr);
			System.out.print("RecordNo,");
			for ( int i=0, j=processStat.cellCount; i<j; ++i ) {
				System.out.print(processStat.censusTable.cells.get(i).label+",");
				infoItems.add(new InfoItem(processStat.censusTable.cells.get(i).label, processStat.censusTable.cells.get(i).descr));
			}
			System.out.println();
	
			fileName = "20125" + openState.getState().toLowerCase()+processStat.seqNumber+"000.zip";
//				if ( !Files.exists(Paths.get(cacheDir+fileName)) ) {
//					cacheFile(openState, cacheDir, fileName);
//				}
			
			ZipInputStream zipStream = new ZipInputStream(new FileInputStream(cacheDir+fileName));
			zipStream.getNextEntry();
			reader = new BufferedReader(new InputStreamReader(zipStream));
			while ( (line = reader.readLine()) != null ) {
				String[] split = line.split(",");
				int recordNo = Integer.parseInt( split[5] );
				if ( recordNo >= recordNoMin && recordNo <= recordNoMax ) {
					CensusRecordNo tempRecordNo = new CensusRecordNo(recordNo, "");
					int idx = Collections.binarySearch(censusRecordNos, tempRecordNo);
					CensusRecordNo currRecordNo = censusRecordNos.get(idx);
					String districtLabel = currRecordNo.geoid.substring(9);
					CHAMBER chamber;
					if ( currRecordNo.geoid.startsWith("61000")) chamber = CHAMBER.UPPER;
					else chamber = CHAMBER.LOWER;
					District district = assembly.findDistrict(chamber, districtLabel);
					List<Result> results = new ArrayList<Result>();
//					System.out.print(""+recordNo+",");
					for ( int i=0, j=processStat.cellCount; i<j; ++i ) {
						String value = split[processStat.cellStartPos+i-1];
//							System.out.print(value +",");
						currRecordNo.values.add(new String(value));
						results.add(new Result(BigDecimal.valueOf(Long.parseLong(value)), BigDecimal.valueOf(0)) );
					}
					district.setResults(results);
//					System.out.println();
				}
			}
//			reader.
	
/*			
			zipStream.getNextEntry();
			reader = new BufferedReader(new InputStreamReader(zipStream));
			while ( (line = reader.readLine()) != null ) {
				String[] split = line.split(",");
				int recordNo = Integer.parseInt( split[5] );
				if ( recordNo >= recordNoMin && recordNo <= recordNoMax ) {
					CensusRecordNo tempRecordNo = new CensusRecordNo(recordNo, "");
					int idx = Collections.binarySearch(censusRecordNos, tempRecordNo);
					CensusRecordNo currRecordNo = censusRecordNos.get(idx); 

					for ( int i=0, j=processStat.cellCount; i<j; ++i ) {
						String value = split[processStat.cellStartPos+i-1];
						System.out.print(value +",");
						currRecordNo.values.add(new Long(value));
					}
					System.out.println();
				}
			}
*/			
			reader.close();
		}
			
		return assemblies;
		
	}
/*	
	private void cacheFile(OpenState openState, String cacheDir, String fileName) throws Exception {
		String baseUrl = "http://www2.census.gov/acs2012_5yr/summaryfile/2008-2012_ACSSF_By_State_By_Sequence_Table_Subset/";
		String finalUrl = baseUrl + openState.getStateName().replace(" ", "") + "/All_Geographies_Not_Tracts_Block_Groups/";
		String fileUrl = finalUrl + fileName;
		URI uri = new URI(fileUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
		BufferedInputStream reader = new BufferedInputStream(conn.getInputStream());
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(cacheDir+fileName));
		byte[] b = new byte[8192];
		int read;
		while ( (read = reader.read(b)) > 0) writer.write(b, 0, read);
		reader.close();
		writer.close();
		conn.disconnect();
	}
*/
}
