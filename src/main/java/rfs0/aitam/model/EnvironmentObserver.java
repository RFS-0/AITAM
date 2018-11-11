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

/**
 * <p>This class is to store all relevant attributes of the simulation.
 * Those attributes have to be provided upon instantiation of the observer.
 * However the handling of what is being is stored and how the values are updated after each simulation step is entirely handled by the {@link Environment#start()} (see comment in start).
 * The observer writes the values of all attributes to a CSV-File which is stored in the file as defined by {@link ISimulationSettings#SIMULATION_OUTPUT_FOLDER}.
 * The following attributes are used to do this:</p>
 * 
 * <p>{@link EnvironmentObserver#m_header}: The header of the CSV-File for the simulations output.</p>
 * <p>{@link EnvironmentObserver#m_csvPrinter}: A writer that writes all values currently hold in the output holder of the provided {@link Environment} to disk as one line in the CSV-File.</p>
 */
public class EnvironmentObserver implements Steppable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * <p>The header of the CSV-File for the simulations output.</p>
	 */
	private Collection<String> m_header;
	/**
	 * <p>{@link EnvironmentObserver#m_csvPrinter}: A writer that writes all values currently hold in the output holder of the provided {@link Environment} to disk as one line in the CSV-File.</p>
	 */
	private CSVPrinter m_csvPrinter;
	
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

	/**
	 * <p>This method writes a new line to the simulation's CSV-File based on the simulations current state resp. the state captured in {@link Environment#m_outputHolder}.</p>
	 */
	@Override
	public void step(SimState state) {
		Environment environment = (Environment) state;
		LinkedList<Object> values = new LinkedList<Object>();
		fillValues(environment, values);
		recordValues(values);
		resetValues(environment);
	}

	/**
	 * <p>This method fills all attributes as defined by the header ({@link EnvironmentObserver#m_header}) into the provided list.</p>
	 * 
	 * @param environment - the environment resp. its current state.
	 * @param values - a list with objects which will be filled with all the values defined in the header.
	 */
	private void fillValues(Environment environment, LinkedList<Object> values) {
		for (String column: m_header) {
			values.add(environment.getOutputHolder().get(column));
		}
	}
	
	/**
	 * <p>This method writes the a new line to the CSV-File of the current simulation run containing all the provided values.</p>
	 * 
	 * @param values - the values which should be written in the line for the current simulation step. <b>Important:</b> The ordering of those values must be alinged with the header.
	 */
	private void recordValues(LinkedList<Object> values) {
		try {
			getCsvPrinter().printRecord(values);
			getCsvPrinter().flush();
		}
		catch (IOException e) {
			Logger.getLogger(EnvironmentObserver.class.getName()).log(Level.SEVERE, "Failed to write simulation output", e);
		}
	}

	/**
	 * <p>This method resets all the values of the environment's output holder ({@link Environment#m_outputholder}).</p>
	 * 
	 * @param environment - the environment
	 */
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

	/**
	 * @category Getters and setters
	 */
	public CSVPrinter getCsvPrinter() {
		return m_csvPrinter;
	}
}
