/*--------------------------------------------------------------*
  Copyright (C) 2006-2008 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.inifile.editor.model;

/**
 * One entry in the ConfigRegistry. Describes a configuration option.
 *
 * @author Andras
 */
public class ConfigOption {
    /**
     * Configuration option data types
     */
    public enum DataType {
      CFG_BOOL,
      CFG_INT,
      CFG_DOUBLE,
      CFG_STRING,
      CFG_FILENAME,
      CFG_FILENAMES,
      CFG_PATH,
      CFG_CUSTOM
    };

    private String name;         // e.g. "sim-time-limit"
    private boolean isPerObject; // if true, entries must be in <object-full-path>.config-name format
    private boolean isGlobal;    // if true, it cannot occur in [Config X] sections
    private DataType dataType;   // option's data type
    private String unit;         // if numeric, its unit ("s") or empty string
    private String defaultValue; // the default value
    private String description;  // help text

    /**
     * Constructor.
     */
    ConfigOption(String name, boolean isPerObject, boolean isGlobal,
                 DataType dataType, String unit, String defaultValue, String description) {
        this.name = name;
        this.isPerObject = isPerObject;
        this.isGlobal = isGlobal;
        this.dataType = dataType;
        this.unit = unit;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public String getName() {
    	return name;
    }

    public boolean isPerObject() {
		return isPerObject;
	}

    public boolean isGlobal() {
    	return isGlobal;
    }

    public DataType getDataType() {
    	return dataType;
    }

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public boolean containsWildcard() {
		return name.contains("%") || name.contains("*");
	}
}
