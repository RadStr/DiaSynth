package RocnikovyProjektIFace.AudioPlayerPlugins.ifaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PluginParametersAnnotation {
    public static final String UNDEFINED_VAL = "UNDEFINED";

    /**
     * Name which will be shown on the GUI, if not set then the name of the field will be used.
     * Note for programmers: If the name isn't set then it is equal to PluginParametersAnnotation.UNDEFINED_VALUE
     * @return
     */
    public String name() default UNDEFINED_VAL;
    public String lowerBound() default UNDEFINED_VAL;
    public String upperBound() default UNDEFINED_VAL;
    public String defaultValue() default UNDEFINED_VAL;
    public String parameterTooltip() default "";
}
