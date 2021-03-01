# DiaSynth


## Sections
1. [How does Diasynth look](#how-does-diasynth-look)
2. [Notes for README](#notes-for-readme)
3. [Introduction](#introduction)
4. [What can be the project used for?](#what-can-be-the-project-used-for)
5. [Features](#features)
6. [For who is Diasynth?](#for-who-is-diasynth)
7. [How to install/launch](#how-to-installlaunch)
8. [How to compile (Useful for the plugin programmers)](#how-to-compile-useful-for-the-plugin-programmers)
9. [How to open project in IDE (Useful for the plugin programmers)](#how-to-open-project-in-ide-useful-for-the-plugin-programmers)
10. [Warnings](#warnings)
11. [How to install plugins](#how-to-install-plugins)
12. [How to use program](#how-to-use-program)
13. [How to write plugins](#how-to-write-plugins)
14. [TODO (What should be done in future)](#todo-what-should-be-done-in-future)
15. [Similar projects](#similar-projects)
16. [License](#license)
17. [Object-oriented design (Just images)](#object-oriented-design)


## How does Diasynth look
![Synthesizer](https://user-images.githubusercontent.com/40957172/109537961-d5d85980-7abf-11eb-804a-a9d8d252763e.PNG)

<p>

![Audio player](https://user-images.githubusercontent.com/40957172/109537740-8b56dd00-7abf-11eb-96f7-65205520b10e.PNG)

<p>

![Analyzer library](https://user-images.githubusercontent.com/40957172/109537797-9f9ada00-7abf-11eb-9ec9-d0b25c0b349e.PNG)

<p>
  
![Analyzer - file chooser](https://user-images.githubusercontent.com/40957172/109537847-acb7c900-7abf-11eb-834c-71fc3071cd74.png)


## Notes for README
In the text I will omit the `str.rad` prefix in packages (same for file paths). Since they exist only for global uniqueness of names.

## Introduction
Diasynth contains audio player in style of Audacity, audio synthesizer (using diagrams) and audio analyzer. All contained in one GUI (graphical user interface).

<p>

The program is written in Java.

<p>

Diagram in synthesizer consists of wave generators, operators and connections between them.

<p>

Each part of program can be extended by plugins. To be more specific, it means that there is possibility for user to write own audio analysis algorithms for analyser, own operations for modification of audio tracks loaded in audio player and in case of synthesizer, it means that the user can implement own operators and generators and use them in diagram for sound synthesis.

## What can be the project used for?
The work can serve as alternative to already existing programs not only to amateurs but also to more advanced users. <p>

It can be used on schools to show the behaviour of digital signals or show audio synthesis techniques, such as frequency modulation. <p>

Thanks to plugins it be also used to try out some audio processing algorithms without having to take care of the conversion of audio data.<p>

Last but not least it can be also used to create sound effects which can be used for example in games.


## Features
* Stable
* Extensible by plugins. The program is using features of the Java programming language. Such as annotations to spare the programmer of plugin from GUI programming.
* Easy to write plugins
* Easy to use (Although some changes needs to be done - for analyzer library and audio modification in audioplayer)
* "All in one"
* Ability to save/load diagrams, save/load audio
* Written in Java - Most alternatives are written in C/C++

## For who is Diasynth?
Diasynth is for everyone who wants to work for audio. <p>
Mainly for beginners who find other audio programs overwhelming (Mainly goes for audio synthesis).<p>
Anyone who wants to try out some audio algorithms or write audio units for synthesis.


## How to install/launch
To start the version of program without plugins launch the Diasynth_Original.jar.

<p>

To start the version with plugins launch Diasynth_Updater.jar or Diasynth_Modified.jar if it was already created. Due to time constraints I had to do the plugin loading this way. When launching Diasynth_Updater.jar creates (or rewrites) Diasynth_Modified.jar and launches that file as new process. This will be changed in future, but now you should be aware that every launch of Diasynth_Updater.jar creates new file (~700kB).<p>
The libs directory (and for plugins the Diasynth-plugins directory) has to be in the same directory as the .jar file.

## How to compile (Useful for the plugin programmers)
The application can be build using [Apache Ant](http://www.javazoom.net/mp3spi/sources.html). 
The steps for building are following.
1. Download project (and unzip the .zip).
2. Open command line and move to the directory containing **build.xml** file using `cd` command.
3. The project is then run using `ant run` command. Within this command are called these 2 commands: `ant compile` and `ant jar`. 
First command compiles the project (creates .class files) and the second one creates the **Diasynth\_Original.jar** from the .class files. The created .jar is found in **build/jar**. 

## How to open project in IDE (Useful for the plugin programmers)
To open and launch the project in IDE the references to libraries needs to be resolved. The libraries are in the "**libs**" directory. The references are added within the IDE, I won't write here how to do it for each IDE. For IntelliJ IDE it can be done as written in this [stackoverflow post](https://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project).   

## Warnings
* I don't take responsibility for any damage caused by this software. This was school project so any caused damage is unintentional.

* The program is still under construction (there is still a lot to be done) so the interfaces may change, which may render the already made plugins obsolete. In that case the user can either use the old version of program or rewrite the plugins I will write the plugins, I will give changelog and examples showing how to rewrite the plugins. I will try to preserve the old interfaces.

* The decibel meter's flickering in audio player may be really **unpleasant** for some, so turn it off in view tab. Same goes for the synthesized wave inside synthesizer (but that shouldn't be really annoying to most).

* I won't probably do any updates on this project until july.

* Both analyzer and audio player load the whole song into memory, they are not using streaming.

* The plugin loading mechanism is currently not ideal. When Diasynth_Updater.jar is launched, new .jar file is created which contains all the plugins. (This will change in future.)

* The program performs **logging**, I will put in option to not log later. (It is useful only on first launch of program for troubleshooting)

* The project has to stay as it was downloaded, otherwise references for images or libraries won't be found.

* Since this audio program be careful with volume.

* Plugins can contain any code even intentionally **harmful** for user, so if there will ever be any plugins you should always look in the source code. This goes only for third party plugins of course, I won't put intentionally harmful code in here.  


## How to install plugins
The plugins have to be inside the **Diasynth-plugins** folder and are distributed via the .class files. <p>
In current version (it will be changed in future, see [TODO](#todo-what-should-be-done-in-future)) the plugins have to follow the Java package structure. <p>
For example if we have plugin **pack.age.plugin** then we have to put **pack/age/plugin.class** into the **Diasynth-plugins** folder. 

## How to use program
To switch between program parts use tabs at top left corner.

### Analyzer 
The controls of analyzer should be pretty straightforward. It has **two windows**. <p>
In the **first** one we choose the files to analyze and what we want to analyze.<p>
The **second** is the library of already analyzed files. The files can be seen at bottom, before we can see their info we have to move them to the mid part.<p>
I will remove the mid part later since it is redundant and also at iterator for the files (so the user can easily list through analyzed files).

<p>

The analyzed info is saved into ANALYZED_AUDIO.xml. For program to find this file it needs to be in the same directory as the .jar file (or as src folder in case of compilation).

### Audio player
Everything should be straightforward except the operations.<p> 
To perform operation which has **one** input wave just mark (left-click on wave and drag) the part of the wave you want to perform the operation on. Then press the wanted operation in tab. The operation will be performed on every wave which has Include in operations check mark checked.<p>
Operation with **two** input waves: It has format *out = in op out* (or *out = out op in* depends on the implementations of *op*) where *out* is marked wave, *in* is wave which is currently in clipboard (it is copied now) and *op* is performed operation. The alignment changes only the end index.<p>
I will make it better later (see [TODO](#todo-what-should-be-done-in-future) section).
 

### Synthesizer  
Synthesizer is diagram based, diagram is created inside GUI. Diagrams can be saved and loaded.<p>
Diagram consists of units. Unit is either operator or generator. Generator has time as input parameter, operator doesn't. <p>
The available units can be seen on right in the hierarchical menu. The controls should be straightforward.<p>
The output format of generated wave/waves can be changed inside the **File** tab.
Synthesized wave can be recorded. It can be either recorded to audio player or to file (or both). To set the parameters of the record go into **Record->SET RECORD INFO** tab. The check mark with the conversion is only used when recording to audio player. 
To apply the changes just click *close* or *X*.
<p>
Synthesizer has 2 record types:

* Instant recording, which makes the record immediately. The length given inside **SET RECORD INFO**.
* Real-time recording, which can be stopped by the same button. Maximum length of this recording is given by the time set in the **SET RECORD INFO**. The advantage of this record type is that it registers changes made by user while recording.

#### Units
Some units have properties, which can be seen after we right click the unit. Currently implemented units with properties are constant generator, wavetable generator, output unit, waveshaper.

#### Types of units
##### Generators
###### Classic generators 
1. Generators with function - Those have some function (for example sine wave) and generate values based on that.

2. Wavetable generators - Simply said, it is array with stored wave and we are moving on the indices of array based on frequency found on input. The higher the frequency the further we jump in array with each generated value.

These  generators are of 2 types with and without phase. Generators contain 3 inputs: 
amplitude (what should be the peak of wave). Frequency how fast should the wave repeat. Phase what should be the starting position from which should the wave start generating.
 
###### Envelopes
Envelope is special type of generator.
<p>
ADSR Envelopes are of two types in current version:
Linear and exponential envelope. The type controls the slope of envelope.

![Exponential envelope](https://user-images.githubusercontent.com/40957172/109538129-028c7100-7ac0-11eb-834a-d8e7aedf6f16.PNG)

<p>

![Linear envelope](https://user-images.githubusercontent.com/40957172/109538131-03250780-7ac0-11eb-80d6-93cf32f2beaf.PNG)

<p>

![LINEN_vs_EXPEN](https://user-images.githubusercontent.com/40957172/109538134-03250780-7ac0-11eb-8145-53391cd4b8ac.PNG)

<p>

ADSR envelopes imitate real life sounds. 
They have **four** main parts - **ADSR**. 
First there is big increase in energy (**attack**), 
then decrease of the energy (**decay**) to stable value, 
then we stay on that value for the time of playing (**sustain**) and when we stop playing, then the energy is slowly going to 0 (**release**).
<p>  
So they are usually used to control the amplitude of signal. 
<p>
Envelope in diagram has six inputs.
 
1. The length of attack phase.
2. The value which will be reached at the end of attack phase.
3. The length of the decay phase.
4. The length of the sustain phase.
5. The amplitude of the sustain phase.
6. The length of the decay phase.

###### Noise generators
They are also of two types, with and without frequency. They have two inputs: amplitude and frequency. Amplitude controls the peak of the generated noise and frequency controls how often should be new value generated (new value is generated every (sample rate / frequency) samples). Currently only white noise is implemented, which is just pseudo-random generator. 
White noise should contain the approximately the same energy in every frequency range. For example 3080-3090Hz range should contain the same energy as in 80-90Hz.

##### Operators
Most of them are pretty straightforward, but some are not. I will describe those non-obvious now. 
<p>

###### Division 
The synthesizer expects, that every unit knows its generated minimum and maximum value based on the minimum and maximum values on inputs (I couldn't think of better way to solve this normalization on output unit problem). 
But this doesn't work in case of division, because the closer the divider is to zero the bigger is the value and since most of the units can come "infinitely" close to zero, we really can't know the maximum generated value of division. 
So we have artificial threshold near zero and every value which is in absolute value smaller than this threshold is converted to it (preserving sign). 
(If the generated values are only above the threshold or only below the -threshold then the threshold is ignored).
<p>

###### Normalize operator 
normalizes the given input so the resulting amplitude is one. Normalizer is also on the output unit (right click and go into properties).
<p>

###### Waveshaper 
transforms the input wave to output wave based on internally set function. The function is currently given by drawing. To draw the wave go into **properties** of unit. The x coordinate says the input value, the y says the output (The input wave is normalized first). In future I might add option to write the function as equation.
<p>

###### Rectifiers:
* Full-wave rectifier is absolute value.
* Half-wave rectifier keeps only positive values. The negative values are set to zero.

#### Basic synthesis techniques
##### Amplitude modulation (AM)
AM is when generator, respectively sequence of units, is connected to binary plus and the second argument of plus is constant. 
Output of plus is connected to input controlling amplitude of some other generator. We call the non-constant wave connected to operator plus **modulating wave** and the generator whose amplitude we are modifying is called **carrier oscillator**.

<p>

![AM_500_10](https://user-images.githubusercontent.com/40957172/109538278-2fd91f00-7ac0-11eb-83da-f2f346d8efa8.PNG)

<p>
  
![AM_500_10_Dia](https://user-images.githubusercontent.com/40957172/109538276-2fd91f00-7ac0-11eb-924e-981d85ca53f8.PNG)

##### Ring modulation (RM)
RM is special case of AM, when the constant is equal to zero. So that means we can connect the wave straight to the input controlling amplitude. We don't need the operator plus. 

<p>

![RM_500_10](https://user-images.githubusercontent.com/40957172/109538396-51d2a180-7ac0-11eb-80ed-87c2468b852d.PNG)

<p>
  
![RM_500_10_Dia](https://user-images.githubusercontent.com/40957172/109538393-513a0b00-7ac0-11eb-9edf-c2dc0cb5de98.PNG)

##### Frequency modulation (FM)
FM is basically like AM but with frequency. We have constant and generator (modulating oscillator) connected to operator plus and the output of plus is connected to frequency input of some generator. The constant is called carrier frequency.

<p>

In case of wavetable the FM is indeed FM, but in case of classic generators the FM is implemented using phase modulation. Can be seen in [this article](https://ccrma.stanford.edu/sites/default/files/user/jc/fm_synthesispaper-2.pdf) by J. M. Chowing - Creator of FM.

<p>

So there are differences between FM on wavetable and classic generator. Wavetable is more general, so I will add in future way to generate way straight into wavetable.
 
<p> 
 
![FM_150_500_5](https://user-images.githubusercontent.com/40957172/109538480-6dd64300-7ac0-11eb-9767-739d1c62572f.PNG)

<p> 

![FM_150_500_5_Dia](https://user-images.githubusercontent.com/40957172/109538479-6d3dac80-7ac0-11eb-8233-721f7eb50834.PNG)

## How to write plugins
The plugins can be written without necessity to write own GUI to insert input values for algorithm. 
This next section ([Anotations](#annotations)) is useful if you are writing plugins for synthesizer or audio player.
#### Annotations:
In the next code we will see the annotation to mark the parameters of algorithm with. The program will then make sure to create GUI based on these and set the variable which has this annotation to the value given in the GUI.
<p> 
The parametrs mean this (parameter name is explained in comment):
<p>
Lower and upper bound are limits for variable.
<p>
Default value is the value to which will be the parameter set if user won't type any value into GUI. (If we want to preserve the value between calls of algorithm, then don't set this value, just initialize the variable in a classic way via =)
<p>
Tooltip is the tooltip which will be shown when the user points mouse at the label in the GUI.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PluginParameterAnnotation {
	public static final String UNDEFINED_VAL = "UNDEFINED";

	/**
	 * Name which will be shown on the GUI. 
	 * If not set, then the name of the field will be used.
	 */
	public String name() default UNDEFINED_VAL;
	public String lowerBound() default UNDEFINED_VAL;
	public String upperBound() default UNDEFINED_VAL;
	public String defaultValue() default UNDEFINED_VAL;
	public String parameterTooltip() default "";
}
```

##### First example:
This is taken from the code for non-recursive filter (Class `player.operations.wave.filters.LowPassFilter`). 
```java
@PluginParameterAnnotation(name = "Cutoff frequency:",
                           lowerBound = "0", defaultValue = "400", 
                           parameterTooltip = "Cut-off frequency")
private double cutoffFreq;
@PluginParameterAnnotation(name = "Coefficient count:",
                           lowerBound = "32", defaultValue = "32", 
                           parameterTooltip = "Represents the number 
                              of the coefficients used for filtering"
private int coefCount;
```

<p>

![FiltrDialog](https://user-images.githubusercontent.com/40957172/109538640-9eb67800-7ac0-11eb-937a-2f50a4d0afc9.PNG)

##### Second example:
This is taken from the properties of output unit inside synthesizer (Class `synthesizer.synth.OutputUnit`).
```java
@PluginParameterAnnotation(name = "Amplitude:", lowerBound = "0", 
                           upperBound = "1",
                           parameterTooltip = "Maximum absolute value 
                                               allowed in output")
private double maxAbsoluteValue = 1;
@PluginParameterAnnotation(name = "Always scale:",
                           parameterTooltip = "<html>Multiline<br>" +
                                              "tooltip</html>")
private boolean shouldAlwaysSetToMaxAbs = false;
```

<p>
  
![OutputUnitDialog](https://user-images.githubusercontent.com/40957172/109538637-9e1de180-7ac0-11eb-96d0-c04510820163.PNG)  

##### Implementing ENUM using annotation (CAN BE SKIPPED, since most people don't really need ENUMs)
The class which implements the interface for plugin and has annotated enum value, has to implement this interface. This interface exists for setting the correct enum. 
<p>

We can also notice that the enum values shown in the GUI don't have to be the enum values, we can have some code names for enum values and show these instead, but then you of course have to correctly convert between these values and the real enum values. 
```java
/**
* Wrapper interface for the enums. 
* FieldName is there to * identify the enum in class.
(There may be more enums in class)
*/
public interface EnumWrapperForAnnotationPanelIFace {
	String[] getEnumsToStrings(String fieldName);
	void setEnumValue(String value, String fieldName);
	void setEnumValueToDefault(String fieldName);
	String getDefaultEnumString(String fieldName);
	String getToolTipForComboBox(String fieldName);
}
```

###### Example - implementation of enum wrapper
Taken from AlignmentOnWavesOperation class.
```java
@PluginParameterAnnotation(name = "Length alignment:",
        parameterTooltip = "The enum which value tells " + 
                           "what alignment should be done. " + 
                           "Only changes the end indices, " +
                           "not the start ones")
private AlignmentEnum lengthAlignment = AlignmentEnum.NO_ALIGNMENT;	
	
	
@Override
public String[] getEnumsToStrings(String fieldName) {
    if("lengthAlignment".equals(fieldName)) {
        return AlignmentEnum.getEnumsToStrings();
    }
    return null;
}

@Override
public void setEnumValue(String value, String fieldName) {
    if("lengthAlignment".equals(fieldName)) {
        lengthAlignment = AlignmentEnum.convertStringToEnumValue(value);
    }
}

@Override
public void setEnumValueToDefault(String fieldName) {
    setEnumValue(getDefaultEnumString(fieldName), fieldName);
}

@Override
public String getDefaultEnumString(String fieldName) {
    if("lengthAlignment".equals(fieldName)) {
        int index = getDefaultIndex(fieldName);
        return AlignmentEnum.getEnumsToStrings()[index];
    }
    return "";
}

private int getDefaultIndex(String fieldName) {
    if("lengthAlignment".equals(fieldName)) {
        return 0;
    }
    return -1;
}

@Override
public String getToolTipForComboBox(String fieldName) {
    if("lengthAlignment".equals(fieldName)) {
        return "<html>" +
        "NO_ALIGNMENT means that if the output is longer then <br>" +
        "the input will be used more times to fill the output wave." +
        "<br>Other options are self-explaining." +
        "</html>";
    }
    return "";
}
```

```java
// AlignmentEnum.java

NO_ALIGNMENT,
ALIGN_TO_SHORTER,
ALIGN_TO_LONGER,
ALIGN_TO_INPUT,
ALIGN_TO_OUTPUT


public static String[] getEnumsToStrings() {
	AlignmentEnum[] values = AlignmentEnum.values();
	String[] strings = new String[values.length];
	for (int i = 0; i < values.length; i++) {
		strings[i] = values[i].toString();
	}
	
	return strings;
}


public static AlignmentEnum convertStringToEnumValue(String s) {
	AlignmentEnum[] values = AlignmentEnum.values();
	for (AlignmentEnum v : values) {
		if(v.toString().equals(s)) {
			return v;
		}
	}
	
	return null;
}
```


### Analyzer 
Plugins have to be inside `analyzer.plugin.plugins` package.

<p>

Most people will want to implement `AnalyzerDoublePluginIFace`.

```java
public interface AnalyzerDoublePluginIFace extends
                                           AnalyzerBasePluginIFace {
	/**
	 * @return Returns pair.
	 * First value is the name which will be showed on left.
	 * Second value is the analyzed value converted to string.
	 */
	Pair<String, String> analyze(DoubleWave wave);
}
```

The class `AnalyzerBasePluginIFace` looks like this:

```java
public interface AnalyzerBasePluginIFace {
	/**
	 * Returns the name of the checkbox, which will be shown 
	 * to user in analyzer panel.
	 */
	String getName();
	
	/**
	 * Returns the tooltip for the checkbox in analyzer panel.
	 */
	String getTooltip();
}
```


There are also two other interfaces, but they require extra work from plugin programmer since they don't work with doubles and the pay-off is zero to null, so there is really no need to implement them. They differ from the first one in the format of wave, so only the signature of the analyze method is different.

```java
// AnalyzerBytePluginIFace.java
analyze(byte[] samples, int numberOfChannels, int sampleSize,
        int sampleRate, boolean isBigEndian, boolean isSigned);
	
// AnalyzerIntPluginIFace.java
analyze(int[] samples, int numberOfChannels, int sampleRate);
```



 
### Audio player
Plugins have to be inside `player.plugin.plugins` package.

<p>

To implement operation on wave the programmer just needs to implement **OperationOnWavePluginIFace**:
```java
public interface OperationOnWavePluginIFace extends AudioPlayerJMenuPluginIFace {
    void performOperation(DoubleWave audio, int startIndex, int endIndex);
}

public interface AudioPlayerJMenuPluginIFace extends PluginBaseIFace {
	/**
	* @return Returns tooltip which will be shown when hovering 
	* over the button which will perform the operation.
	*/
	String getPluginTooltip();
}	
	
public interface PluginBaseIFace {
	boolean shouldWaitForParametersFromUser();
	boolean isUsingPanelCreatedFromAnnotations();
	String getPluginName();
}
```

shouldWaitForParametersFromUser is set to true if the algorithm needs data for user, to false if not.

<p>

To implement the operation on two waves of type *out = in op out* (respectively *out = out op in*) the programmer needs to implement **OperationOnWavesPluginIFace**. But be warned that to controls for this type of operation aren't currently ideal.
<p>

Only thing that changes against the variant with single wave is the method signature of `performOperation`.

```java
void performOperation(DoubleWave input, DoubleWave output,
                      int inputStartIndex, int inputEndIndex,
                      int outputStartIndex, int outputEndIndex);
``` 


### Synthesizer  
Plugins have to be inside `synthesizer.synth` package.

<p>

All units in synthesizer have to be derived from class `Unit`. But usually we want to derive from one of the more specialized classes.

If something isn't clear take a look at some of the specialized classes (`Generator`, `GeneratorNoPhase`, `NoiseGenerator`, `NoiseGeneratorNoFreq`, `Operator`, `UnaryOperator`, `BinaryOperator`) or at the `Unit` class or at some of the non-abstract classes such as `SineGenerator`, etc.. They are all inside the `synthesizer.synth` package.

<p>

Generators derive from `Generator` or `GeneratorNoPhase`.

<p>

Noise generators derive from `NoiseGenerator` or `NoiseGeneratorNoFreq`

<p>

Operators derive from `Operator` respectively based on number of inputs from `UnaryOperator` or `BinaryOperator`, etc. To implement n-ary operator just take a look at how the Unary and Binary variant are implemented.

<p>

Envelopes derive from `Envelope`.

#### Note
To implement the no phase variant of generator (respectively no frequency of noise generator) it is the best to do it using the more general unit which contains the missing input. In case of phase it is almost needed, since generators need phase for frequency modulation. 

<p>

(In future I will code in possibility to generate wave into wavetable, then the phase variant isn't needed, because wavetable doesn't need phase or any special code to handle frequency modulation).
###### Example:
```java
public class SineGenerator extends Generator {
    public SineGenerator(Unit u) { super(u); }
    public SineGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }
	
    @Override
    public String getDefaultPanelName() {
        return "Sine";
    }
	
    @Override
    public void resetToDefaultState() {
        // EMPTY
    }
	
	
    @Override
    public double generateSampleConst(double timeInSecs, 
                                      int diagramFrequency,
                                      double amp, double freq,
                                      double phase) {
        double genVal;
        genVal = amp * Math.sin(freqToRad(freq) * timeInSecs + phase);
        return genVal;
    }
	
    @Override
    public String getTooltip() {
        return "Generates sine wave";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
```

```java
public class SineGeneratorNoPhase extends SineGenerator {
    public SineGenerator(Unit u) { super(u); }
    public SineGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }
	
    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, 
                                           double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits,
                                          neutralValues);
    }
}
```

#### Usual case
Now we will take a look at the methods which have to be sometimes implemented, when deriving from specialized class. 

<p>

I won't talk here about all methods from `Unit` class. So if you want to implement some new type of unit, take a look at source codes.

###### Deriving from specialized classes (In some cases this is the only section you need to know)

<p>

For noise generator we need to implement these methods.

```java
/**
* Generates noise between 0 and 1.
* @return
*/
public abstract double generateNoise();
public abstract String getDefaultPanelName();
public abstract String getTooltip();
```

For other types it is similar. We always implement `getDefaultPanelName` and `getTooltip` and then we need to implement the method which returns the sample generated by the unit.

<p>

For `Generator` it is `generateSampleConst`. 

<p>

For `UnaryOperator` it is `unaryOperation`.

<p>

For `BinaryOperator` it is `binaryOperation`.

<p>

For `Envelope` it is `generateEnvelopeSample`.

<p>

For `NoiseGenerator` it is `generateNoise` as said above.

<p>

Sometimes it is needed to set shape of panel by implementing `createShapedPanel` (More on this in **Shape** section below). And of course it is needed to implement the 2 constructors (More on this in **Constructors** section below). 

##### General methods
###### Constructors
Every class has to implement both constructors **and call corresponding parent constructors**. In most cases the call of `super` constructor is the only code inside.  

```java
/**
* Copy constructor. 
* Has to be implemented in all deriving classes.
*/
public Unit(Unit u) {
	// CODE
}

/**
* Constructor used when not copying.  
* Has to be implemented in all deriving classes.
*/
public Unit(DiagramPanel panelWithUnits) {
	// CODE
}
```

Sometimes it makes sense to perform some action in copy constructor, but the same can be done by overriding `copyPanel`, which I personally think is better.


###### Name
`public abstract String getDefaultPanelName();` exists to create unique name for iterator (the button under the play button). Sometimes can be shown on unit, depends on implementation of unit.

###### Input ports

Sometimes it is needed to implement method which creates input ports. Types of implemented ports can be seen at `synthesizer.gui.diagram.panels.port.ports`.

```java
/**
* Should return InputPort[] which will 
* be set as unit's input ports. 
* It is called inside the constructors.
* If there are no input ports then set it to new InputPort[0].
*/
protected abstract InputPort[] createInputPorts(DiagramPanel panelWithUnits, 
                                                double[] neutralValues);
``` 

Then there is this method for neutral values of input ports. I think that I will **remove** it in future, it just isn't useful enough.

```java
/**
* It is used as parameter to the createInputPorts method.
* It is up to the person who implements the plugin, if
* he takes these values into consideration. He may ignore them and
* just set the input ports to hard-coded default values in 
* createInputPorts method.
*
* @return Returns neutral values for ports.
*         If null or if the array is shorter than number of ports 
*         then these values should be ignored.
*/
public abstract double[] getNeutralValuesForPorts();
```


###### Shape
Next 2 methods are implemented inside specialized classes, but sometimes it is useful to override them. They return the shape (and internals) of the Unit - it determines the looks of unit. The shapes can be seen at `synthesizer.gui.diagram.panels.shape`

```java
/**
* Creates new shaped panel
*/
protected abstract ShapedPanel createShapedPanel(DiagramPanel panelWithUnits);

/**
* Creates new shaped panel called with 
* corresponding constructor of same signature as this method 
* (+ the internals of course)
*/
protected abstract ShapedPanel createShapedPanel(int relativeX,
                                                 int relativeY, 
                                                 int w, int h,
                                                 DiagramPanel panelWithUnits); 
```

Implementation example of these two methods from class `Generator`.

```java
@Override
protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
	ShapedPanel sp = new ArcShapedPanel(panelWithUnits, 
	                 new ArcConstantTextInternals(getPanelName()), 
	                 this);
	return sp;
}
	
@Override
protected ShapedPanel createShapedPanel(int relativeX, int relativeY, 
                                        int w, int h,
                                        DiagramPanel panelWithUnits) {
        ShapedPanel sp = new ArcShapedPanel(relativeX, relativeY, 
                         w, h, panelWithUnits,
                         new ArcConstantTextInternals(getPanelName()), 
                         this);
	return sp;
}
```

###### Reset 

This method resets the state of unit to the situation before first played sample. If the unit is stateless, then the body can be empty.

```java
/**
* Resets to the default state
* (as if no sample was ever before played)
*/
public abstract void resetToDefaultState();
```

###### Tooltip
To show info about unit the plugin programmer needs to implement the `String getTooltip();` method.

###### MIN/MAX values

Sometimes it is needed to implement methods which tell the maximum/minimum value found on output of unit. 
These are needed so normalization can be performed on output unit (or normalize operator).

<p>

The `getMaxAbsVal` doesn't have to be implemented again 
(unless you want to get some performance boost if you have some extra information about the unit's outputs - but really this optimization shouldn't be needed).

```java
public double getMaxAbsValue() {
	double min = getMinValue();
	double max = getMaxValue();
	return Math.max(Math.abs(min), Math.abs(max));
}

public abstract double getMinValue();
public abstract double getMaxValue();
```

###### Properties
To add properties into Unit we do it similar way as we implement plugins to audio player. We just need to implement `PluginBaseIFace` interface and in method `setPropertiesPanel` set instance of such class into `propertiesPanel` variable. (this is bad design, I should have used getter instead of setter, I might change it in future, I do this on other places as well).

<p>

For example like this:

```java
// Unit.java
protected abstract void setPropertiesPanel();
	
// OutputUnit.java
@Override
protected void setPropertiesPanel() {
	propertiesPanel = this;
}
```
If we don't want to implement properties, then just set the variable to null.

<p>

Note: I might change the way properties are created in future or add possibility to add more of them, because I don't think that the way it is now is ideal.

###### Future method
The current version of program doesn't (and can't) support recursive filters, because they break the outputs due to inconsistencies in calculations. 
Simply said, when the user changes connection in diagram, then there can be inconsistency (we are calculating part of the diagram as in the old diagram before the change and part in the new one - That isn't problem for most units, but for recursive filters it is). For that I made method `copyInternalState(Unit u)`, which will be used in future to solve this problem.

<p>

The instance on which is the method called should copy its internal state into given unit. Internal state is everything which has something to do with the computation of samples (except the samples themselves).


## TODO (What should be done in future)
Sorted by importance and the probable order in which it will be done. But as I said in warnings, it won't be soon (maybe the first 2 will be since they are quite simple).

- [ ] Add option to disable logging.
- [ ] Remove the middle window in analyzer (the window is unnecessary)
- [ ] Rework the way of adding input waves for audio operation inside audio player, right now it is really non-intuitive.
- [ ] Add the general variant of audio modification with 2 input waves (that means *out = in1 op in2* instead of *out = in op out*).
- [ ] Make the plugin loading mechanism to not create new .jar file.
- [ ] Remove the need for plugins to follow the directory structure - That means the .class file could be put into any subdirectory of Diasynth-plugins directory.
- [ ] Install the libraries into documents, or somewhere. The program will then need uninstaller and installer though.
- [ ] Add iterator for analyzed files.
- [ ] Improve menu with available units for synthesizer.
- [ ] Add option to load generated wave into wavetable generator. Plugin programmer won't have to implement phase variant of generator thanks to that and the FM will be more general.
- [ ] Take a look at [LADSPA](https://www.ladspa.org/) plugins, if and how it can be used for our program. 
- [ ] Add filter and delay unit to synthesizer.
- [ ] Add feedback components to synthesizer
- [ ] Add score support for synthesizer. That means it will be like Csound programming language.
- [ ] Add editor synthesizer's score. Which means that it will have programming support, the user will write score and when doing that, the diagram on the GUI will update.
- [ ] Try to add MIDI to synthesizer
- [ ] Add option for analyzer to process more than single wave at once (use multithreading). But it isn't ideal since, the songs are all loaded into memory.
- [ ] Use streaming instead of loading the whole audio tracks into memory (I am not sure if I will ever do this).


## Object-oriented design
Here are some dependencies of classes shown in diagrams. 
The arrows show flow of information. 
For example A &#8594; B means that information flows from class A to class B. 
Some of the 2-way information flows were inevitable. If there is no information exchange or it isn't well descriptive, then there is only line instead of arrow.

<p>

The word extends marks inheritance. Parent classes are always higher in the diagram.

<p>

I won't describe the diagrams in detail, the diagrams should be self-explanatory.

##### General

![DiasynthTabbedPanelDiagram](https://user-images.githubusercontent.com/40957172/109539538-b5110380-7ac1-11eb-978e-ae991c073adf.png)

##### Analyzer

![AnalyzerDiagram](https://user-images.githubusercontent.com/40957172/109539426-9874cb80-7ac1-11eb-8c9e-6f2ab66c8719.png)

<p>
  
![AnalyzerObserversCommunication](https://user-images.githubusercontent.com/40957172/109539428-9874cb80-7ac1-11eb-984a-3cac223076fb.png)


##### Audio player

![AudioPlayerDiagram](https://user-images.githubusercontent.com/40957172/109539579-c3f7b600-7ac1-11eb-920a-49815b5d03f1.png)


<p>
  
![WaveMainPanelDiagram](https://user-images.githubusercontent.com/40957172/109539719-ec7fb000-7ac1-11eb-8073-3d3ab80798d4.png)

<p>
  
![WavePanelDrawValuesDiagram](https://user-images.githubusercontent.com/40957172/109539722-ed184680-7ac1-11eb-8074-115f533c66bc.png)

##### Synthesizer

![SynthesizerMainPanelDiagram](https://user-images.githubusercontent.com/40957172/109539649-d96ce000-7ac1-11eb-8012-063fe965ddfb.png)

<p>

![SynthPartDiagram](https://user-images.githubusercontent.com/40957172/109539651-d96ce000-7ac1-11eb-934c-f69f9a2a5275.png)

<p>
  
![MovableJPanelDaigram](https://user-images.githubusercontent.com/40957172/109539652-da057680-7ac1-11eb-8e09-47c24e1c5567.png)


## Similar projects
Audio player: 
* [Audacity](https://www.audacityteam.org/) is free open-source audio software.
* [Adobe audition](https://www.adobe.com/cz/products/audition.htm) is paid audio software. 
 
Synthesizers:
* [CSound](https://csound.com/) is programming language, which can be used to create diagrams similar to those in this program. It doesn't contain GUI and is much more complex, but that also means that more things can be done in it.
* [FLStudio](https://www.image-line.com/flstudio/) is closer to the real synthesizers.
* [MAX/MSP](https://cycling74.com/) is paid software. It is advanced variant of synthesizer found in Diasynth, but the controls are much more complex, but also it contains many more features.
* [PureData](https://puredata.info/) is free alternative to MAX/MSP. 

## License
The project is using 2 libraries with following licenses.<p>
The [MP3SPI package](http://www.javazoom.net/mp3spi/sources.html) is licensed under LGPL.
The files from this library can be found in libs directory under the names:
* mp3spi1.9.5.jar
* tritonus_share.jar
* jl1.0.1.jar
<p>

[JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms) is distributed under the terms of the BSD-2-Clause license.
Copyright is found in the LICENSE file taken from https://github.com/wendykierp/JTransforms

<br/>

The files can be found in in libs directory under the names starting with JTransforms.

<p>

As far as I understand it including the LICENSE file with copyright for JTransforms should be enough to cover BSD-2-Clause.

<p>

The libraries (MP3SPI and tritonus) are discontinued for long time. The MP3SPI for around 10 years, the JLayer for 12 years and tritonus even for 17. So this next part really isn't necessary.<p>
To cover the LGPL. It is enough to say which files from library are under that license and tell user where to download the newest version and how to replace them. Replacing is simple just replace the 3 .jar files from MP3SPI package. And to download the newest version go to the javazoom link (http://www.javazoom.net/mp3spi/sources.html) where you will find the JLayer and MP3SPI jar files and check http://www.tritonus.org/ for new version of tritonus lib.

<p>
As for license for whole program, currently undecided, but I guess I will just go with MIT, or any of the above, since the program still isn't done I will decide when I will be doing the last few fixes.
