package RocnikovyProjektIFace.AudioPlayerPlugins.IFaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PluginParametersAnnotation {
    public static final String UNDEFINED_VAL = "UNDEFINED";
    public String lowerBound() default UNDEFINED_VAL;
    public String upperBound() default UNDEFINED_VAL;
    public String defaultValue() default UNDEFINED_VAL;
    public String parameterTooltip() default "";
}
