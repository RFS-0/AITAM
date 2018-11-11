package rfs0.aitam.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import rfs0.aitam.settings.ISimulationSettings;
import sim.engine.SimState;
import sim.engine.Steppable;

public class EnvironmentObserver implements Steppable {
	
	private static final long serialVersionUID = 1L;

	private CSVPrinter m_csvPrinter;
	private Collection<String> m_header;
	
	public EnvironmentObserver(Collection<String> header) {
		m_header = header;
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
		LinkedList<Object> values = new LinkedList<Object>();
		fillValues(environment, values);
		recordValues(values);
		resetValues(environment);
	}

	private void fillValues(Environment environment, LinkedList<Object> values) {
		for (String column: m_header) {
			values.add(environment.getOutputHolder().get(column));
		}
	}
	
	private void recordValues(LinkedList<Object> values) {
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
			Object value = environment.getOutputHolder().get(colum);
			if (value instanceof Integer) {
				environment.getOutputHolder().put(colum, Integer.valueOf(0));
			} else {
				environment.getOutputHolder().put(colum, null);
			}
		}
	}

	public CSVPrinter getCsvPrinter() {
		return m_csvPrinter;
	}
}
