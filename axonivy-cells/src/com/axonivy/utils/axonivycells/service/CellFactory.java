package com.axonivy.utils.axonivycells.service;

import java.io.InputStream;
import java.util.function.Supplier;

import com.aspose.cells.License;

import ch.ivyteam.ivy.ThirdPartyLicenses;
import ch.ivyteam.ivy.environment.Ivy;

/**
 * Factory class for managing Aspose Cells operations and licensing within Axon
 * Ivy.
 * <p>
 * This class serves as the main entry point for spreadsheet operations,
 * providing:
 * <ul>
 * <li>Automatic license management for Aspose Cells</li>
 * <li>Factory methods for creating spreadsheet converters</li>
 * <li>Utility methods for executing license-dependent operations</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The class follows a static factory pattern and ensures that the Aspose Cells
 * license is properly initialized before any operations are performed. If
 * licensing fails, the application continues in evaluation mode with limited
 * functionality.
 * </p>
 * 
 * <p>
 * <strong>Usage Examples:</strong>
 * </p>
 * 
 * <pre>
 * // Convert spreadsheet to PDF
 * byte[] pdfBytes = CellFactory.convert().from(excelFile).toPdf().asBytes();
 * 
 * // Execute custom operations with guaranteed licensing
 * CellFactory.run(() -> {
 *   // Your Aspose Cells operations here
 * });
 * </pre>
 */
public class CellFactory {
  /**
   * The Aspose Cells license instance, initialized once per application
   * lifecycle. This field is null if license loading fails, indicating evaluation
   * mode.
   */
  private static License license;

  /**
   * Private constructor to prevent instantiation of this utility class. All
   * methods are static and should be accessed directly via the class name.
   */
  private CellFactory() {
  }

  // Static initializer block - executes when the class is first loaded
  static {
    loadLicense();
  }

  /**
   * Initializes the Aspose Cells license for the application.
   * <p>
   * This method implements a singleton pattern to ensure the license is loaded
   * only once per application lifecycle. It retrieves the license from the Axon
   * Ivy {@link ThirdPartyLicenses} service and applies it to the Aspose
   * {@link License} instance.
   * </p>
   * 
   * <p>
   * <strong>Behavior:</strong>
   * <ul>
   * <li>If license is already loaded, the method returns immediately</li>
   * <li>If license stream is available, creates and configures License
   * instance</li>
   * <li>If license stream is null, leaves license as null (evaluation mode)</li>
   * <li>If any exception occurs, logs the error and resets license to null</li>
   * </ul>
   * </p>
   *
   * <p>
   * <strong>Note:</strong> When running in evaluation mode (license == null),
   * Aspose Cells will have functional limitations such as watermarks and document
   * size restrictions.
   * </p>
   * 
   * @see ThirdPartyLicenses#getDocumentFactoryLicense()
   */
  public static void loadLicense() {
    // Check if license is already initialized to avoid redundant loading
    if (license != null) {
      return;
    }
    try {
      // Attempt to retrieve license from Axon Ivy's third-party license service
      InputStream in = ThirdPartyLicenses.getDocumentFactoryLicense();
      if (in != null) {
        // Create and configure the Aspose license
        license = new License();
        license.setLicense(in);
      }
      // If license stream is null, license remains null (evaluation mode)
    } catch (Exception e) {
      // Log any licensing errors and reset license to null
      Ivy.log().error(e);
      license = null;
    }
  }

  /**
   * Creates a new spreadsheet converter for fluent API usage.
   * <p>
   * This factory method provides the main entry point for spreadsheet conversion
   * operations. The returned {@link SpreadsheetConverter} supports a fluent API
   * pattern, allowing for intuitive chaining of conversion operations.
   * </p>
   * 
   * <p>
   * <strong>Usage Examples:</strong>
   * </p>
   * 
   * <pre>
   * // Convert Excel file to PDF as byte array
   * byte[] pdfBytes = CellFactory.convert().from(excelFile).toPdf().asBytes();
   * 
   * // Convert and save to specific file location
   * File outputFile = CellFactory.convert().from(inputFile).to(Format.PDF).asFile("/path/to/output.pdf");
   * 
   * // Convert with custom options
   * InputStream result = CellFactory.convert().from(workbook).withOptions(customOptions).toExcel().asStream();
   * </pre>
   * 
   * <p>
   * <strong>Note:</strong> The license is automatically managed by this factory,
   * so callers don't need to worry about license initialization.
   * </p>
   * 
   * @return a new SpreadsheetConverter instance ready for configuration
   * @see SpreadsheetConverter
   */
  public static SpreadsheetConverter convert() {
    return new SpreadsheetConverter();
  }

  /**
   * Executes a supplier function with guaranteed Aspose Cells license
   * initialization.
   * <p>
   * This utility method ensures that the Aspose Cells license is properly loaded
   * before executing the provided {@link Supplier} function. It provides a safe
   * wrapper for operations that depend on licensed functionality without
   * requiring explicit license checks from the caller.
   * </p>
   *
   * <p>
   * <strong>Usage Examples:</strong>
   * </p>
   * 
   * <pre>
   * // Execute a function that returns a result
   * String result = CellFactory.get(() -> {
   *   Workbook workbook = new Workbook();
   *   // ... perform operations
   *   return workbook.toString();
   * });
   * 
   * // Process data with guaranteed licensing
   * List&lt;String&gt; data = CellFactory.get(() -> processSpreadsheetData(inputFile));
   * </pre>
   *
   * <p>
   * <strong>Note:</strong> This method does not explicitly call
   * {@link #loadLicense()} as the license is already initialized in the static
   * block. However, it provides a semantic guarantee that licensing is handled
   * properly.
   * </p>
   *
   * @param <T>      the return type of the supplier function
   * @param supplier the function to execute, must not be null
   * @return the result produced by the supplier function
   * @throws NullPointerException if supplier is null
   */
  public static <T> T get(Supplier<T> supplier) {
    return supplier.get();
  }

  /**
   * Executes a runnable task with guaranteed Aspose Cells license initialization.
   * <p>
   * This utility method ensures that the Aspose Cells license is properly loaded
   * before executing the provided {@link Runnable} task. It provides a safe
   * wrapper for void operations that depend on licensed functionality without
   * requiring explicit license management from the caller.
   * </p>
   *
   * <p>
   * <strong>Usage Examples:</strong>
   * </p>
   * 
   * <pre>
   * // Execute spreadsheet operations safely
   * CellFactory.run(() -> {
   *   Workbook workbook = new Workbook();
   *   Worksheet sheet = workbook.getWorksheets().get(0);
   *   sheet.getCells().get("A1").putValue("Hello World");
   *   workbook.save("output.xlsx");
   * });
   * 
   * // Batch process multiple files
   * CellFactory.run(() -> {
   *   files.forEach(file -> processSpreadsheet(file));
   * });
   * </pre>
   *
   * <p>
   * <strong>Note:</strong> This method does not explicitly call
   * {@link #loadLicense()} as the license is already initialized in the static
   * block. However, it provides a semantic guarantee that licensing is handled
   * properly.
   * </p>
   *
   * @param run the task to execute, must not be null
   * @throws NullPointerException if run is null
   */
  public static void run(Runnable run) {
    run.run();
  }
}
