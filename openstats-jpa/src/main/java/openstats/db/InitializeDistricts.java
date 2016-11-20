package openstats.db;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import openstats.client.openstates.OpenState;
import openstats.client.openstates.OpenStateClasses;
import openstats.dbmodel.DBAssemblyHandler;
import openstats.model.*;
import openstats.model.District.CHAMBER;

public class InitializeDistricts {
	
	private EntityManagerFactory emf;
	private EntityManager em;
	
	public static void main(String... args) throws Exception {
		new InitializeDistricts().run();
	}
	
	private void initJpa() throws Exception {
		emf = Persistence.createEntityManagerFactory("openstats");
		em = emf.createEntityManager();
	}
	
	private void run() throws Exception {
		initJpa();
		try {
			EntityTransaction et = em.getTransaction();
			et.begin();
			
			for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
//				OpenState openState = new OpenStateClasses.CAOpenState();
				
				FileInputStream file = new FileInputStream(new File("c:/users/karl/censusdata/"+openState.getState().toLowerCase()+".xls"));
		        		     
			    //Get the workbook instance for XLS file
			    HSSFWorkbook workbook = new HSSFWorkbook(file);
			 
			    //Get first sheet from the workbook
			    HSSFSheet sheet = workbook.getSheetAt(0);
			     
				Assembly assembly = new Assembly(openState.getState(), openState.getSession());
				List<District> districtList = assembly.getDistrictList();
	
	    		//Iterate through each rows from first sheet
			    Iterator<Row> rowIterator = sheet.iterator();
			    while(rowIterator.hasNext()) {
			        Row row = rowIterator.next();
			         
			        //For each row, iterate through each columns
			        String geoid = row.getCell(2).getStringCellValue();
			        String name = null;
			        CHAMBER chamber = null;
			        if ( geoid.startsWith("61000US") ) {
			        	name = geoid.substring(9);
			        	chamber = CHAMBER.UPPER;
			        } else if (geoid.startsWith("62000US")) {
			        	name = geoid.substring(9);
			        	chamber = CHAMBER.LOWER;
			        }
			        if ( name != null ) {
						District district = new District(name, chamber );
						district.setDescription(row.getCell(3).getStringCellValue());
						districtList.add(district);
					}
		
			    }
			    file.close();
			    setLegislators(openState, assembly);
				DBAssemblyHandler.createAssembly(assembly, em);
			}
			et.commit();
		} finally {
			emf.close();
		}
		     
	}
	
	public void setLegislators(OpenState openState, Assembly assembly) throws Exception { 
		openState.loadBulkData();

		for ( org.openstates.data.Legislator legislator: org.openstates.model.Legislators.values()) {
			CHAMBER chamber;
			if ( legislator.chamber.equals("upper") ) chamber = CHAMBER.UPPER;
			else if ( legislator.chamber.equals("lower") ) chamber = CHAMBER.LOWER;
			else throw new RuntimeException("Chamber not found:" + legislator.chamber);
			String dNum = legislator.district;
			if ( dNum.length() != 3 ) {
				try {
					dNum = String.format("%03d", Integer.parseInt(dNum));
				} catch (Exception ignored ) {
					dNum = String.format("%3s", dNum);
				}
			}
			District district = assembly.findDistrict(chamber, dNum);
			if (district == null ) {
				continue;
			}
			Legislator aLegislator = new Legislator();
			aLegislator.setName(legislator.full_name);
			aLegislator.setParty(legislator.party);
			district.getLegislators().add(aLegislator);
		}

	}	
}
