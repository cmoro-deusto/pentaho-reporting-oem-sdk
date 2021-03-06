package org.pentaho.reporting.sdk.sequence;

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.AbstractSequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDescription;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

public class SampleDataSequence extends AbstractSequence
{
  public static final String BACKEND_QUERY = "backend-query";
  public static final String ALL_PARAMETER_TEXT = "all-parameter-text";
  public static final String ALL_PARAMETER_VALUE = "all-parameter-value";

  public SampleDataSequence()
  {
  }

  public String getAllParameterText()
  {
    return (String) getParameter(ALL_PARAMETER_TEXT);
  }

  public void setAllParameterText(final String allParameterText)
  {
    setParameter(ALL_PARAMETER_TEXT, allParameterText);
  }

  public String getAllParameterValue()
  {
    return (String) getParameter(ALL_PARAMETER_VALUE);
  }

  public void setAllParameterValue(final String allParameterValue)
  {
    setParameter(ALL_PARAMETER_VALUE, allParameterValue);
  }

  public String getBackendQuery()
  {
    return (String) getParameter(BACKEND_QUERY);
  }

  public void setBackendQuery(final String backendQuery)
  {
    setParameter(BACKEND_QUERY, backendQuery);
  }

  public SequenceDescription getSequenceDescription()
  {
    return new SampleDataSequenceDescription();
  }

  public TableModel produce(final DataRow dataRow,
                            final DataFactoryContext dataFactoryContext) throws ReportDataFactoryException
  {
    String backendQuery = (String) getParameter(BACKEND_QUERY);
    String displayName = (String) getParameter(ALL_PARAMETER_TEXT);
    String displayValue = (String) getParameter(ALL_PARAMETER_VALUE);

    TableModel tableModel = dataFactoryContext.getContextDataFactory().queryData(backendQuery, dataRow);
    if (tableModel.getColumnCount() < 1)
    {
      throw new ReportDataFactoryException("The base table-model has not enough columns");
    }

    try
    {
      TypedTableModel retval = new TypedTableModel();
      retval.addColumn(tableModel.getColumnName(0), tableModel.getColumnClass(0));
      retval.setValueAt(convertStringToTypedObject(displayValue, tableModel.getColumnClass(0)), 0, 0);
      for (int row = 0; row < tableModel.getRowCount(); row += 1)
      {
        retval.addRow(tableModel.getValueAt(row, 0));
      }

      if (tableModel.getColumnCount() > 1)
      {
        retval.addColumn(tableModel.getColumnName(1), tableModel.getColumnClass(1));
        retval.setValueAt(convertStringToTypedObject(displayName, tableModel.getColumnClass(1)), 0, 1);
        for (int row = 0; row < tableModel.getRowCount(); row += 1)
        {
          retval.setValueAt(tableModel.getValueAt(row, 1), row + 1, 1);
        }
      }
      return retval;
    }
    catch (BeanException be)
    {
      throw new ReportDataFactoryException("Failed to convert value to result-set type", be);
    }
  }

  private Object convertStringToTypedObject (String value, Class type) throws BeanException
  {
    return ConverterRegistry.toPropertyValue(value, type);
  }
}
