package plugin.util;

import player.plugin.ifaces.AudioPlayerJMenuPluginIFace;
import plugin.EnumWrapperForAnnotationPanelIFace;
import plugin.PluginParameterAnnotation;
import util.Pair;
import util.logging.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

public class AnnotationPanel extends JScrollPane implements FieldSetterIFace {
    /**
     *
     * @param objectWithAnnotations is the object on which should be the fields changed.
     * @param classWithAnnotations is .getClass() of the objectWithAnnotations (It is final, so I can't take it from the object)
     */
    public AnnotationPanel(Object objectWithAnnotations, Class<?> classWithAnnotations) {
        this.classWithAnnotations = objectWithAnnotations;

        viewPanel = new JPanel(new GridLayout(0, 2));

        Field[] fields;
        fields = classWithAnnotations.getDeclaredFields();
        addFieldsToPanel(objectWithAnnotations, classWithAnnotations, fields);

        // https://stackoverflow.com/questions/16295949/get-all-fields-even-private-and-inherited-from-class
        // Now go through all parent classes
        Class<?> superClass = classWithAnnotations;
        while((superClass = superClass.getSuperclass()) != null) {
            fields = superClass.getDeclaredFields();
            addFieldsToPanel(objectWithAnnotations, superClass, fields);
        }

        this.setViewportView(viewPanel);
    }


    private JPanel viewPanel;
    private Object classWithAnnotations;


    private void addFieldsToPanel(Object objectWithAnnotations, Class<?> classWithAnnotations, Field[] fields) {
        for (Field f : fields) {
            // Sets the accessibility only for this object, when I create another Field object for the same variable it doesn't affect that one
            f.setAccessible(true);
            Class<?> fieldType = f.getType();
            PluginParameterAnnotation annotation = f.getAnnotation(PluginParameterAnnotation.class);
            // Is not parameter - skip
            if (annotation == null) {
                continue;
            }



            String lowerBound = annotation.lowerBound();
            String upperBound = annotation.upperBound();
            /////////////////////////////////////// Now a lot of ifs - try to parse the lower bound if it fails set it to minimum value of given type
            String tooltipParameterType = "Parameter type: " + fieldType.getName();
            String tooltipParameterValue = "Parameter bounds: [";
            boolean isFloatOrDouble = false;
            double lowerBoundDouble;

            if (fieldType == Byte.TYPE) {
                try {
                    byte lb = Byte.parseByte(lowerBound);
                    lowerBoundDouble = lb;
                } catch (Exception e) {
                    lowerBoundDouble = Byte.MIN_VALUE;
                }
            }
            else if (fieldType == Short.TYPE) {
                try {
                    short lb = Short.parseShort(lowerBound);
                    lowerBoundDouble = lb;
                } catch (Exception e) {
                    lowerBoundDouble = Short.MIN_VALUE;
                }
            }
            else if (fieldType == Integer.TYPE) {
                try {
                    int lb = Integer.parseInt(lowerBound);
                    lowerBoundDouble = lb;
                } catch (Exception e) {
                    lowerBoundDouble = Integer.MIN_VALUE;
                }
            }
            else if (fieldType == Long.TYPE) {
                try {
                    long lb = Long.parseLong(lowerBound);
                    lowerBoundDouble = lb;
                } catch (Exception e) {
                    lowerBoundDouble = Long.MIN_VALUE;
                }
            }
            else if (fieldType == Float.TYPE) {
                isFloatOrDouble = true;

                try {
                    float lb = Float.parseFloat(lowerBound);
                    lowerBoundDouble = lb;
                } catch (Exception e) {
                    lowerBoundDouble = Float.NEGATIVE_INFINITY;
                }
            }
            else if (fieldType == Double.TYPE) {
                isFloatOrDouble = true;

                try {
                    double lb = Double.parseDouble(lowerBound);
                    lowerBoundDouble = lb;
                } catch (Exception e) {
                    lowerBoundDouble = Double.NEGATIVE_INFINITY;
                }
            }
            else if(fieldType.isEnum() && AudioPlayerJMenuPluginIFace.
                    isImplementingIFace(EnumWrapperForAnnotationPanelIFace.class, classWithAnnotations)) {
                JLabel parameterName = createLabelBasedOnAnnotation(f, annotation);
                String fieldName = f.getName();
                EnumWrapperForAnnotationPanelIFace wrapper = (EnumWrapperForAnnotationPanelIFace) objectWithAnnotations;

                String[] enumValuesStrings = wrapper.getEnumsToStrings(fieldName);
                JComboBox comboBox = new JComboBox(enumValuesStrings);
                comboBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JComboBox cb = (JComboBox) e.getSource();
                        String selectedItem = (String) cb.getSelectedItem();
                        wrapper.setEnumValue(selectedItem, fieldName);
                    }
                });

                comboBox.setToolTipText(wrapper.getToolTipForComboBox(fieldName));
                comboBox.setSelectedItem(wrapper.getDefaultEnumString(fieldName));
                wrapper.setEnumValueToDefault(fieldName);

                viewPanel.add(parameterName);
                viewPanel.add(comboBox);
                continue;
            }
            else if(fieldType == Boolean.TYPE) {
                JLabel parameterName = createLabelBasedOnAnnotation(f, annotation);
                parameterName.setToolTipText(annotation.parameterTooltip());

                String[] values = new String[] { "FALSE", "TRUE" };
                JComboBox comboBox = new JComboBox(values);
                String tooltip = tooltipParameterType + " " + "{FALSE, TRUE}";
                comboBox.setToolTipText(tooltip);
                comboBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JComboBox cb = (JComboBox) e.getSource();
                        String selectedItem = (String) cb.getSelectedItem();
                        setField(f, selectedItem);
                    }
                });

                setDefaultValueField(f, annotation.defaultValue());
                try {
                    if (f.getBoolean(objectWithAnnotations)) {
                        comboBox.setSelectedItem(comboBox.getItemAt(1));
                    } else {
                        comboBox.setSelectedItem(comboBox.getItemAt(0));
                    }
                }
                catch(Exception e) {
                    continue;
                }

                viewPanel.add(parameterName);
                viewPanel.add(comboBox);
                continue;
            }
            // TODO: DEFAULT VALUES FOR INPUT PORTS
// Later if I will have time I can use this class to set values of the input ports instead of having default values
//            else if(fieldType.isArray()) {
//                int len = Array.getLength(objectWithAnnotations);
//                if(len > 0) {
//                    Object o = Array.get(objectWithAnnotations, 0);
//                    if(o instanceof InputPort) {
//
//                    }
//                }
//                else {
//                    continue;
//                }
//            }
            // TODO: DEFAULT VALUES FOR INPUT PORTS
            else {
                continue;
            }

            tooltipParameterValue = addValueToString(fieldType, lowerBoundDouble, tooltipParameterValue);
            tooltipParameterValue += ", ";


            //////////////////////////// Same as lower bound but with upper bound
            double upperBoundDouble;

            if (fieldType == Byte.TYPE) {
                try {
                    byte ub = Byte.parseByte(upperBound);
                    upperBoundDouble = ub;
                } catch (Exception e) {
                    upperBoundDouble = Byte.MAX_VALUE;
                }
            }
            else if (fieldType == Short.TYPE) {
                try {
                    short ub = Short.parseShort(upperBound);
                    upperBoundDouble = ub;
                } catch (Exception e) {
                    upperBoundDouble = Short.MAX_VALUE;
                }
            }
            else if (fieldType == Integer.TYPE) {
                try {
                    int ub = Integer.parseInt(upperBound);
                    upperBoundDouble = ub;
                } catch (Exception e) {
                    upperBoundDouble = Integer.MAX_VALUE;
                }
            }
            else if (fieldType == Long.TYPE) {
                try {
                    long ub = Long.parseLong(upperBound);
                    upperBoundDouble = ub;
                } catch (Exception e) {
                    upperBoundDouble = Long.MAX_VALUE;
                }
            }
            else if (fieldType == Float.TYPE) {
                try {
                    float ub = Float.parseFloat(upperBound);
                    upperBoundDouble = ub;
                } catch (Exception e) {
                    upperBoundDouble = Float.MAX_VALUE;
                }
            }
            else if (fieldType == Double.TYPE) {
                try {
                    double ub = Double.parseDouble(upperBound);
                    upperBoundDouble = ub;
                } catch (Exception e) {
                    upperBoundDouble = Double.MAX_VALUE;
                }
            }
            else {
                continue;
            }

            tooltipParameterValue = addValueToString(fieldType, upperBoundDouble, tooltipParameterValue);
            tooltipParameterValue += "]";

            setDefaultValueField(f, annotation.defaultValue());

            JLabel parameterName = createLabelBasedOnAnnotation(f, annotation);
            parameterName.setToolTipText(annotation.parameterTooltip());
            String tooltip = tooltipParameterType + " " + tooltipParameterValue;
            JTextField parameterValue = null;
            try {
                parameterValue = new JTextFieldWithBounds(isFloatOrDouble, lowerBoundDouble, upperBoundDouble,
                    tooltip, f, objectWithAnnotations, this::setField);
            }
            catch(Exception e) {
                MyLogger.logException(e);
                System.exit(1489);      // Shouldn't happen since at the start I call f.setAccessible(true);
            }
            Pair<JLabel, JTextField> p = new Pair<>(parameterName, parameterValue);
            viewPanel.add(p.getKey());
            viewPanel.add(p.getValue());
        }
    }


    private static JLabel createLabelBasedOnAnnotation(Field f, PluginParameterAnnotation annotation) {
        JLabel parameterName;
        if(annotation.name().equals(PluginParameterAnnotation.UNDEFINED_VAL)) {
            parameterName = new JLabel(f.getName());
        }
        else {
            parameterName = new JLabel(annotation.name());
        }

        return parameterName;
    }



    public static String addValueToString(Class<?> fieldType, double val, String s) {
        if (fieldType == Byte.TYPE) {
            s += Byte.toString((byte)val);
        }
        else if (fieldType == Short.TYPE) {
            s += Short.toString((short)val);
        }
        else if (fieldType == Integer.TYPE) {
            s += Integer.toString((int)val);
        }
        else if (fieldType == Long.TYPE) {
            s += Long.toString((long)val);
        }
        else if (fieldType == Float.TYPE) {
            s += Float.toString((float)val);
        }
        else if (fieldType == Double.TYPE) {
            s += Double.toString(val);
        }

        return s;
    }

    @Override
    public void setField(Field field, String value) {
        if("-".equals(value) || "".equals(value)) {
            return;
        }
        Class<?> fieldType = field.getType();

        try {
            if (fieldType == Byte.TYPE) {
                byte val = Byte.parseByte(value);
                field.setByte(classWithAnnotations, val);
            }
            else if (fieldType == Short.TYPE) {
                short val = Short.parseShort(value);
                field.setShort(classWithAnnotations, val);
            }
            else if (fieldType == Integer.TYPE) {
                int val = Integer.parseInt(value);
                field.setInt(classWithAnnotations, val);
            }
            else if (fieldType == Long.TYPE) {
                long val = Long.parseLong(value);
                field.setLong(classWithAnnotations, val);
            }
            else if (fieldType == Float.TYPE) {
                float val = Float.parseFloat(value);
                field.setFloat(classWithAnnotations, val);
            }
            else if (fieldType == Double.TYPE) {
                double val = Double.parseDouble(value);
                field.setDouble(classWithAnnotations, val);
            }
            else if(fieldType == Boolean.TYPE) {
                boolean val = Boolean.parseBoolean(value);
                field.setBoolean(classWithAnnotations, val);
            }
        }
        catch (Exception e) {
            // TODO: JUST for now
            MyLogger.logException(e);
            System.exit(4595457);
        }
    }

    private void setDefaultValueField(Field field, String value) {
        if(!PluginParameterAnnotation.UNDEFINED_VAL.equals(value)) {
            setField(field, value);
        }
    }
}
