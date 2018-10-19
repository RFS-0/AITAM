package rfs0.aitam.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import rfs0.aitam.commons.ISimulationSettings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class EnvironmentObserver implements Steppable {
	
	private static final long serialVersionUID = 1L;

	private CSVPrinter m_csvPrinter;
	private Collection<String> m_header;
	
	public EnvironmentObserver(Collection<String> header) {
		m_header = header;
		System.currentTimeMillis();
		DateTimeFormat.forPattern("dd_MM_yyyy-HH_mm_ss").print(DateTime.now());
		String fileName = DateTimeFormat.forPattern("dd_MM_yyyy--HH_mm_ss").print(DateTime.now()) + "_output.csv";
		try {
			File file = new File(System.getProperty("user.dir") + ISimulationSettings.SIMULATION_OUTPUT_FOLDER + fileName);
			m_csvPrinter = new CSVPrinter(new PrintWriter(file, ISimulationSettings.CHAR_SET), CSVFormat.DEFAULT);
			m_csvPrinter.printRecord(m_header);
			m_csvPrinter.flush();
		}
		catch (IOException e) {
			Logger.getLogger(EnvironmentObserver.class.getName()).log(Level.SEVERE, "Failed to create file for simulation output", e);
		}
	}

	@Override
	public void step(SimState state) {
		Environment environment = (Environment) state;
		ArrayList<Object> values = new ArrayList<>();
		fillValues(environment, values);
		recordValues(values);
		resetValues(environment);
	}

	private void fillValues(Environment environment, ArrayList<Object> values) {
		for (String column: m_header) {
			values.add(environment.getOutputHolder().get(column));
		}
	}
	
	private void recordValues(ArrayList<Object> values) {
		try {
			getCsvPrinter().printRecord(values);
			getCsvPrinter().flush();
		}
		catch (IOException e) {
			Logger.getLogger(EnvironmentObserver.class.getName()).log(Level.SEVERE, "Failed to write simulation output", e);
		}
	}

	private void resetValues(Environment environment) {
		for (String colum: m_header) {
			environment.getOutputHolder().put(colum, null);
		}
	}

	public CSVPrinter getCsvPrinter() {
		return m_csvPrinter;
	}
}
